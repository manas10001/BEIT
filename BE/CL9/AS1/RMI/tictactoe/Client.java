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
        clearTerm();
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

    public static void letsPlay(ArrayList<Integer> board, TTTServer ttt, Scanner sc) throws RemoteException{
        boolean keepPlaying = true;
        int move;

        while(keepPlaying){
            printBoard(board);
            System.out.print("Enter your move: ");
            move = sc.nextInt();
            //if(isValidMove(move, board)){}
            //clients move; server will add its move in arraylist
            board.add(move-1, 1);

            if(ttt.howFull(board) > 5){
                int result = ttt.determineWinner(board);
                if(result == 0){
                    keepPlaying = false;
                    System.out.println("Its a tie!");
                }else if(result == 1){
                    keepPlaying = false;
                    System.out.println("You Win!");
                }else if(result == 2){
                    keepPlaying = false;
                    System.out.println("You Lost!");
                }else{
                    ttt.serverMove(board);
                    printBoard(board);
                }
            }
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