package tictactoe;
import java.rmi.*;
import java.util.ArrayList;

public interface TTTServer extends Remote{
    //all functions of the ttt server

    //selects servers move
    void serverMove(ArrayList<Integer> board) throws RemoteException;
    
    //returns how full the board is
    int howFull(ArrayList<Integer> board) throws RemoteException;

    //validate a move
    //boolean isValidMove(int move, ArrayList<Integer> board) throws RemoteException;

    /*determines winner or determines tie
        -1 = undetermined
        0 = tie
        1 = client
        2 = server
    */
    int determineWinner(ArrayList<Integer> board) throws RemoteException;
}
