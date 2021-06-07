import java.net.*;
import java.io.*;

class Client{
	
	Socket sock = null;
	DataInputStream dsin = null, cmdin = null;
	DataOutputStream dsout = null;
	String msg = null, mymsg = null;
	
	Client(int port){
		try{
		
			//register socket for client and sends req to server
			sock = new Socket("127.0.0.1",port);
			
			cmdin = new DataInputStream(System.in);
					
			dsout = new DataOutputStream(sock.getOutputStream());
			
			dsin = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
			
			mymsg = "";
			msg = "";
			
			//keep communicating until bye is mentioned by either sides
			while(!msg.equals("bye")){
				//send msg to server
				System.out.print("Enter Message: ");
				mymsg = cmdin.readLine();
				dsout.writeUTF(mymsg);
				
				//break connection if client says bye
				if(mymsg.equals("bye"))
					break;
				
				//recieve from server
				msg = dsin.readUTF();
				System.out.println("Server: "+msg);
			}
			//close all connections
			dsin.close();
			dsout.close();
			sock.close();
			cmdin.close();
		
		}catch(Exception ex){
			System.out.println("Client exception: "+ex.toString());
		}
	}
	
	public static void main(String[] s){
		Client client = new Client(6789);
	}
}
