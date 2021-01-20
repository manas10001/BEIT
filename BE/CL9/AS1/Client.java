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
			
			//read clients input from terminal
			cmdin = new DataInputStream(System.in);
					
			//send output to server
			dsout = new DataOutputStream(sock.getOutputStream());
			
			//read the inputstream from server socket
			dsin = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
			
			mymsg = "";
			msg = "";
			
			//keep communicating until adios is mentioned by either sides
			while(!msg.equals("adios")){
				//send msg to server
				mymsg = cmdin.readLine();
				dsout.writeUTF(mymsg);
				
				//break connection if client says adios
				if(mymsg.equals("adios"))
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
