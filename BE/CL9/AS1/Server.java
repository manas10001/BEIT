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
			
			//read the inputstream from client socket
			dsin = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
			
			//stream to write msg to client
			cmdin = new DataInputStream(System.in);

			//send mg to client
			dsout = new DataOutputStream(sock.getOutputStream());
			
			mymsg = "";
			msg = "";
			
			//keep communicating until adios is mentioned by either sides
			while(!mymsg.equals("adios")){
				//read clients msg
				msg = dsin.readUTF();
				System.out.println("Client: "+msg);
				
				//break connection if client says adios
				if(msg.equals("adios"))
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
