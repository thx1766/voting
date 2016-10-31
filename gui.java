import javax.net.ssl.*;
import java.io.*;

class gui{

	static void request_validation_number(String name){
		//string is not in voter list
		//print id number
		//return 1
		//string is in voter list
		try{
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket("localhost",1234);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			String command = "valnum";
			bw.write(command+'\n');
			bw.flush();
			
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String response = br.readLine();
			if(response.equals("name?")){
				bw.write(name+'\n');
				bw.flush();
			}
			String response2 = br.readLine();
			if(response2.contains("invalidname")){
				System.out.println("name already in use");
			}else{
				int valnum = Integer.parseInt(response2);
				System.out.println("Validation number is:"+valnum);
			}

		}catch(Exception e){e.printStackTrace();}
	}

	static void transfer_validation_numbers(){
		try{
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 1234);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write("transfer"+'\n');
			bw.flush();
		}catch(Exception e){e.printStackTrace();}
	}

	static void cast_vote(String randomId, String validationNum, String vote){
		try{
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket("localhost",1235);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			bw.write("cast vote"+'\n');
			bw.flush();
			String line = br.readLine();
			if(line.equals("idnum?")){
//System.out.println("requesting id");
				bw.write(randomId+'\n');
				bw.flush();
			}
			line = br.readLine();
			if(line.equals("valnum?")){
//System.out.println("requesting valnum");
				bw.write(validationNum+'\n');
				bw.flush();
			}else{
//System.out.println("invalid id");
				System.out.print(line);
			}
			line = br.readLine();
			if(line.equals("vote?")){
//System.out.println("requesting vote");
				bw.write(vote+'\n');
				bw.flush();
			}else{
//System.out.println("invalid validation number");
				System.out.println(line);
			}
			line = br.readLine();
System.out.println(line);
		}catch(Exception e){e.printStackTrace();}
	}

	static void view_votes(){
		try{
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("localhost",1235);
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write("view votes"+'\n');
		bw.flush();

		InputStream is = socket.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
String results = "";
//while((
results = br.readLine();//)!=null)
System.out.println(results);
			System.out.println(br.readLine());
			System.out.println(br.readLine());
			System.out.println(br.readLine());
		}catch(Exception e){e.printStackTrace();}
	}

	static void usage(){
		System.out.println("Choice should be one of:");
		System.out.println("request: request a voter validation number from CLA (after sypplying name)");
		System.out.println("transfer: request transfer of validation numbers from CLA to CTF");
		System.out.println("cast <idNumber> <valNumber> <vote>: cast a vote");
		System.out.println("view: view results from CTF");
		System.out.println("quit: exit");
	}

	public static void main(String args[]){
		System.out.println("GUI");

		InputStream is = System.in;
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String choice="";
		System.out.println("request|transfer|cast|view|quit");
		while(true){
			System.out.print("> ");
			try{choice = br.readLine();}catch(Exception e){e.printStackTrace();}
			if(choice.equals("quit"))
				break;
			else if(choice.contains("request")){
				System.out.print("Name: ");
				String name = "";
				try{name = br.readLine();}catch(Exception e){e.printStackTrace();}
				request_validation_number(name);
			}else if(choice.contains("transfer"))
				transfer_validation_numbers();
			else if(choice.contains("cast")){
				System.out.print("id number: ");
				String idNumber = "";
				try{idNumber = br.readLine();}catch(Exception e){e.printStackTrace();}
				System.out.print("validation number: ");
				String valNumber = "";
				try{valNumber = br.readLine();}catch(Exception e){e.printStackTrace();}
				System.out.print("vote choice: ");
				String vote = "";
				try{vote = br.readLine();}catch(Exception e){e.printStackTrace();}
				cast_vote(idNumber, valNumber, vote);
			}else if(choice.contains("view"))
				view_votes();
			else
				usage();
		}
	}
}
