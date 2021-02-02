import java.net.*;
import java.io.*;

class Server{
	
	ServerSocket serversock = null;
	Socket sock = null;
	DataInputStream dsin = null, cmdin = null;
	DataOutputStream dsout = null;
	String msg = null, mymsg = null;
	
	Server(int port){
		try{
			
			//register server socket and await connection from client
			serversock = new ServerSocket(port);
			
			//wait for client to connect and accept it when it does
			sock = serversock.accept();
			System.out.println("Client Connected!");
			
			//read the inputstream from socket
			dsin = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
			
			//stream to read msg from cmdline
			cmdin = new DataInputStream(System.in);

			//stream to write to socket
			dsout = new DataOutputStream(sock.getOutputStream());
			
			mymsg = "";
			msg = "";
			
			//keep communicating until bye is mentioned by either sides
			while(!mymsg.equals("bye")){
				//read clients msg
				msg = dsin.readUTF();
				System.out.println("Client: "+msg);
				
				//break connection if client says bye
				if(msg.equals("bye"))
					break;
				
				//write our msg
				mymsg = cmdin.readLine();
				dsout.writeUTF(mymsg);				
				
			}
			//close everything
			sock.close();
			dsin.close();
			cmdin.close();
			dsout.close();
			serversock.close();
			
		}catch(Exception ex){
			System.out.println("Server exception: "+ex.toString());
		}	
	}
	
	
	public static void main(String[] s){
		Server server = new Server(6789);
	}
}
