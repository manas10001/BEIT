package tictactoe;
import java.rmi.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class Client {

    //clears the current terminal
    public static void clearTerm(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //print board
    public static void printBoard(ArrayList<Integer> board){
        // clearTerm();
        System.out.println("Current Board: \n");
        for(int i = 0; i < 9; i++){
            char toPrint = (char) (i+1+'0');

            if(board.get(i) == 1)
                toPrint = 'C';
            if(board.get(i) == 2)
                toPrint = 'S';

            if((i+1) % 3 == 0)
                System.out.println(toPrint);
            else
                System.out.print(toPrint + "|");
        }
    }

    //handles win lose or tie
    public static boolean handleWinner(ArrayList<Integer> board, TTTServer ttt) throws RemoteException{
        if(ttt.howFull(board) >= 5){
            System.out.println("-----------------------------Checking for winner");
            int result = ttt.determineWinner(board);
            if(result == -1){
                System.out.println("Its a tie!");
                return true;
            }else if(result == 1){
                System.out.println("You Win!");
                return true;
            }else if(result == 2){
                System.out.println("You Lost!");
                return true;
            }
        }
        return false;
    }

    public static void letsPlay(ArrayList<Integer> board, TTTServer ttt, Scanner sc) throws RemoteException{
        boolean keepPlaying = true;
        int move;

        while(keepPlaying){
            printBoard(board);

            if(handleWinner(board, ttt)){
                keepPlaying = false;
                continue;
            }

            System.out.print("Enter your move: ");
            move = sc.nextInt();
            
            //clients turn will be skipped if move chosen is invalid
            if(ttt.isValidMove(move, board)){
                //clients move; server will return its move and we will add it to board
                board.set(move-1, 1);
                System.out.println("-----------------------------Board after your move");
                printBoard(board);
        
                if(handleWinner(board, ttt)){
                    keepPlaying = false;
                    continue;
                }
            }else{
                System.out.println("You made a mistake and lost your turn!");
            }

            System.out.println("-----------------------------Server will play");
            int sMove = ttt.serverMove(board);
            // System.out.println("-----------------------------Server takes"+ sMove);
            board.set(sMove, 2);

        }
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        //Server url from where we want to call the method remotely
        String url = "rmi://127.0.0.1/TicTacToe";

        /*create board with 9 zeros
            Board Values :  0 -> Empty slot
                            1 -> Slot Acquired by client
                            2 -> Slot Acquired by server
        */
        ArrayList<Integer> board = new ArrayList<>(Collections.nCopies(9, 0));

        try {
            //lookup method finds the reference of remote object
            TTTServer ttt = (TTTServer)Naming.lookup(url);
            
            Client.letsPlay(board, ttt, sc);
        } 
        catch (Exception ex) {
            System.out.println(ex.toString());   
        }
        finally{
            sc.close();
        }
    }
}