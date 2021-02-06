package tictactoe;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class TTTServerImpl extends UnicastRemoteObject implements TTTServer{
    public TTTServerImpl() throws RemoteException{
        super();
    }

    public boolean isValidMove(int move, ArrayList<Integer> board) throws RemoteException{
        if(board.get(move) == 0)
            return true;
        return false;
    }

    //chooses the servers move and returns its position
    public int serverMove(ArrayList<Integer> board) throws RemoteException{
        //choose a field and place your token
        //currently server just puts token in first available slot
        for(int i = 0; i < 9; i++){
            if(board.get(i) == 0){
                return i;
            }
        }
        return -1;
    }

    //returns how full is the board
    public int howFull(ArrayList<Integer> board) throws RemoteException{
        //cout will hold how many blank cells are left
        int count = 0;
        for(int i : board){
            if(i != 0)
                count++;
        }
        return count;
    }

    //determines result of match
    /*determines winner or determines tie
        -1 = tie
        0 = undetermined
        1 = client
        2 = server
    */
    public int determineWinner(ArrayList<Integer> board) throws RemoteException{
        int result = 0;

        if(board.get(0) == board.get(1) && board.get(1) == board.get(2))
            result = board.get(0);
        
        if(board.get(3) == board.get(4) && board.get(4) == board.get(5))
            result = board.get(3);

        if(board.get(6) == board.get(7) && board.get(7) == board.get(8))
            result = board.get(6);

        if(board.get(0) == board.get(3) && board.get(3) == board.get(6))
            result = board.get(0);

        if(board.get(1) == board.get(4) && board.get(4) == board.get(7))
            result = board.get(1);

        if(board.get(2) == board.get(5) && board.get(5) == board.get(8))
            result = board.get(2);

        if(board.get(0) == board.get(4) && board.get(4) == board.get(8))
            result = board.get(0);

        if(board.get(2) == board.get(4) && board.get(4) == board.get(6))
            result = board.get(2);

        //if we cant find a winner and board is full its a tie
        if(result == 0 && howFull(board) == 9)
            result = -1;

        return result;
    }
}
