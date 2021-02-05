package tictactoe;
import java.rmi.*;

public class Server{
    public static void main(String[] args) {
        try{
            TTTServerImpl ttt = new TTTServerImpl();
            
            //bind remote object to server
            Naming.rebind("TicTacToe", ttt);
            
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
    }
}