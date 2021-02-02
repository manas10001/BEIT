import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server{
	
	ServerSocket serversock = null;
	Socket sock = null;
	
	Server(int port){
		try{
			
			//register server socket and await connection from client
			serversock = new ServerSocket(port);
			
			ArrayList<ClientHandler> clients = new ArrayList<>();
			
			while(true){
				//wait for client to connect and accept it when it does
				sock = serversock.accept();
				System.out.println("log: New Client Connected!");
				
				//call the handler class to start handling the object
				ClientHandler clientHandler  = new ClientHandler(sock, clients);
				
				//add the client to client list
				clients.add(clientHandler);
				
				//start thread
				clientHandler.start();
			}
		}catch(Exception ex){
			System.out.println("Server exception: "+ex.toString());
		}	
	}
	
	public static void main(String[] s){
		Server server = new Server(6790);
	}
}

//a class for thread that will handle each clients
class ClientHandler extends Thread{
	
	ArrayList<ClientHandler> clients;
	PrintWriter output;
	Socket sock;
	
	//get data to local context
	public ClientHandler(Socket sock, ArrayList<ClientHandler> clients){
		this.sock = sock;
		this.clients = clients;
	}
	
	public void run(){
		try{
			System.out.println("log: A thread is created! ");
			
			//read content from socket
			BufferedReader inp = new BufferedReader( new InputStreamReader(sock.getInputStream()));
			
			//write content to socket
			output = new PrintWriter(sock.getOutputStream(),true);
			
			while(true){
				//users msg
				String msg = inp.readLine();
				
				if(msg.equals("exit"))
					break;
				
				//fwd the message to all other clients
				System.out.println("log: Sending msg to all clients: "+msg);

				for(ClientHandler ch: clients){
					ch.output.println(msg);
				}

				System.out.println("log: "+msg);
			}
		}catch(Exception ex){
			System.out.println("Exception in a client handler: "+ex.toString());
		}
	}
}
