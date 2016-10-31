import java.util.ArrayList;
import java.io.*;
import java.lang.Math;
import javax.net.ssl.*;

class cla{
	ArrayList<String> voterNames;
	ArrayList<Integer> validationNums;
	ArrayList<Integer> transferredNums;

	cla(){
//		System.out.println("Initializing voter list and validation number list");
		voterNames = new ArrayList<String>();
		validationNums = new ArrayList<Integer>();
		transferredNums = new ArrayList<Integer>();
	}

	int process_registration(String name){
		if(voterNames.contains(name))
			return -1;
		else{
			voterNames.add(name);
			int valNum = (int) ((Math.random())*10000);
System.out.println("generated random:"+valNum);
			while(validationNums.contains(new Integer(valNum))){
System.out.println("collision! new random:"+valNum);
				valNum = (int) ((Math.random())*10000);
			}
			validationNums.add(new Integer(valNum));
			return valNum;
		}
	}

	void transfer(){
System.out.println("transfer requested");
		try{
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("localhost",1235);
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("begin transfer"+'\n');
		bw.flush();
		int numElements = validationNums.size();
		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		while(numElements != 0){
			if(br.readLine().equals("ready")){
				String data = validationNums.get(numElements-1).toString();
				bw.write(data+'\n');
				bw.flush();
				if(br.readLine().equals("inserted")){
					transferredNums.add(validationNums.get(numElements-1));
					numElements--;
				}
			}
		}
		bw.write("transfer complete"+'\n');
		bw.flush();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void main(String args[]){
		cla server = new cla();

		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		try{
			SSLServerSocket serversocket = (SSLServerSocket) factory.createServerSocket(1234);
while(true){
			SSLSocket socket = (SSLSocket) serversocket.accept();
Runnable ch = new claconnectionHandler(socket, server);
new Thread(ch).start();
}

		}catch(Exception e){e.printStackTrace();}
	}
}

class claconnectionHandler implements Runnable{
    SSLSocket sock;
    cla server;
    claconnectionHandler(SSLSocket mysocket, cla mycla){
        sock = mysocket;
        server = mycla;
    }
    public void run(){
//        System.out.println("run method of connectionHandler invoked");
        try{
            InputStream is = sock.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = sock.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            String data = "";
            while((data = br.readLine())!=null){
                if(data.equals("valnum")){
                    bw.write("name?\n");
                    bw.flush();
                    String name = br.readLine();
                    int valnum = server.process_registration(name);
                    if(valnum == -1){
                        bw.write("invalidname"+'\n');
                        bw.flush();
                    }else{
                        String valnumS = Integer.toString(valnum);
                        bw.write(valnumS+'\n');
                        bw.flush();
                    }
                }else if(data.equals("transfer")){
			server.transfer();
		}
            }
		os.close();
		sock.close();
        }catch(Exception e){e.printStackTrace();}
        
    }
}
