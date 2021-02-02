import java.net.*;
import java.io.*;
import java.util.Scanner;

class Client{
	
	Socket sock = null;
	
	Client(int port){
		try{
		
			//register socket for client and sends req to server
			sock = new Socket("127.0.0.1", port);
			
			//read data from socket
			BufferedReader inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			//printwriter to writeto socket
			PrintWriter sendmsg = new PrintWriter(sock.getOutputStream(), true);
			
			Scanner sc = new Scanner(System.in);
			String msg = "";
			
			System.out.println("Enter client name");
			String userName = sc.nextLine();
			
			//create a thread for async msg read from server and start it
			ServerResponseHandler srh = new ServerResponseHandler(sock, userName);
			srh.start();
			
			//take client input and send to server
			do{
				msg = sc.nextLine();
				sendmsg.println(userName+": "+msg);
			}while(!msg.equals("exit"));
			
			//kill thread 
			srh.stop();
			
		}catch(Exception ex){
			System.out.println("Client exception: "+ex.toString());
		}
	}
	
	public static void main(String[] s){
		Client client = new Client(6790);
	}
}

class ServerResponseHandler extends Thread{
	Socket sock;
	BufferedReader inp;
	String uname;
	
	ServerResponseHandler(Socket sock, String uname)throws IOException{
		this.sock = sock;
		this.inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.uname = uname;
	}
	
	public void run(){
		try{
			while(true){
				String msg = inp.readLine();
				
				//dont disply self messages 
				if(msg.startsWith(uname))
					continue;
					
				System.out.println(msg);
			}
		}catch(Exception ex){
			System.out.println("Exception in ServerResponseHandler: "+ex.toString());
		}finally{
			try{
                    inp.close();
                }catch(Exception e){
                    System.out.println(e.toString());
                }
		}
	}
}
