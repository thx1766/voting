import java.util.ArrayList;
import java.io.*;
import java.lang.Math;
import javax.net.ssl.*;
class ctf{
	ArrayList<Integer> valNums;
	ArrayList<Integer> idNums;
	ArrayList<Integer>candidateA;
	ArrayList<Integer>candidateB;
	ArrayList<Integer>alreadyVoted;

	ctf(){
		valNums = new ArrayList<Integer>();
		idNums = new ArrayList<Integer>();
		candidateA = new ArrayList<Integer>();
		candidateB = new ArrayList<Integer>();
		alreadyVoted = new ArrayList<Integer>();
	}

	public static void main(String args[]){
		ctf server = new ctf();
		
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		try{
			SSLServerSocket serversocket = (SSLServerSocket) factory.createServerSocket(1235);
			while(true){
				SSLSocket socket = (SSLSocket) serversocket.accept();
				Runnable ch = new ctfconnectionHandler(socket, server);
				new Thread(ch).start();
			}
		}catch(Exception e){e.printStackTrace();}
	}
}

class ctfconnectionHandler implements Runnable{
	SSLSocket sock;
	ctf server;

	ctfconnectionHandler(SSLSocket mysocket, ctf myctf){
		sock = mysocket;
		server = myctf;
	}

	public void run(){
		try{
			InputStream is = sock.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			OutputStream os = sock.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			String data = "";
			while((data = br.readLine())!=null){
				if(data.equals("begin transfer")){
					System.out.println("transfer initiated");
					bw.write("ready"+'\n');
					bw.flush();
					data = br.readLine();
					while(!data.equals("transfer complete")){
						server.valNums.add(new Integer(Integer.parseInt(data)));
						bw.write("inserted"+'\n');
						bw.flush();
						bw.write("ready"+'\n');
						bw.flush();
						System.out.println("inserted value: "+data);
						data = br.readLine();
					}
				}else if(data.equals("cast vote")){
					System.out.println("cast vote");
					bw.write("idnum?"+'\n');
					bw.flush();
					System.out.println("requested idnum");
					String idnum = br.readLine();
					System.out.println("recieved: "+idnum);
					if(server.idNums.contains(new Integer(Integer.parseInt(idnum))) /*&& !server.alreadyVoted.contains(new Integer(Integer.parseInt(idnum)))*/){
						bw.write("ID number in use, please choose another\n"+'\n');
						bw.flush();
						bw.write("vote not stored\n"+'\n');
						bw.flush();
					}else{
						bw.write("valnum?"+'\n');
						bw.flush();
						System.out.println("requested valnum");
						String valnum = br.readLine();
						System.out.println("recieved: "+valnum);
						Integer vN = new Integer(Integer.parseInt(valnum));
						if(!server.valNums.contains(vN)){
							bw.write("Validation number is not in system"+'\n');
							bw.flush();
							bw.write("Register for valid number"+'\n');
							bw.flush();
						}else if(!server.alreadyVoted.contains(vN)){
							bw.write("vote?"+'\n');
							bw.flush();
							String vote = br.readLine();
							String response="";
							Integer idN = new Integer(Integer.parseInt(idnum));
							server.idNums.add(idN);
							System.out.println("added "+idnum+" to idnumbers");
							response+="idnumber added ";
							if(vote.equals("A")){
								server.candidateA.add(idN);
								server.alreadyVoted.add(vN);
							}else if(vote.equals("B")){
								server.candidateB.add(idN);
								server.alreadyVoted.add(vN);
							}else{
								System.out.println("bad user input for candidate");
							}
							response+="vote stored";
							bw.write(response+'\n');
							bw.flush();
						}else{
							bw.write("That validation number has already been used"+'\n');
							bw.flush();
							bw.write("You are not allowed to vote more than once"+'\n');
							bw.flush();
						}
					}
				}else if(data.equals("view votes")){
					System.out.println("view votes");
					int c1 = server.candidateA.size();
					int c2 = server.candidateB.size();
					bw.write("\nvotes for candidate A: ");
					while(c1 > 0){
						bw.write("<"+server.candidateA.get(c1-1).toString()+">");
						c1--;
					}
					bw.write("\nvotes for candidate B: ");
					while(c2 > 0){
						bw.write("<"+server.candidateB.get(c2-1).toString()+">");
						c2--;
					}
					int voters = server.alreadyVoted.size();
					bw.write("\nused valudation numbers: ");
					while(voters > 0){
						bw.write('<'+server.alreadyVoted.get(voters-1).toString()+'>');
					voters--;
					}
					bw.write('\n');
					bw.flush();
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}
}
