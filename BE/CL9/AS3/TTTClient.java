import TTTModule.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.util.*;

class TTTClient{


    /* TTT methods start*/
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
    public static boolean handleWinner(ArrayList<Integer> board, TTT TTTimpl){
        if(TTTimpl.howFull(board) >= 5){
            System.out.println("-----------------------------Checking for winner");
            int result = TTTimpl.determineWinner(board);
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

    public static void letsPlay(ArrayList<Integer> board, TTT TTTimpl, Scanner sc){
        boolean keepPlaying = true;
        int move;

        while(keepPlaying){
            printBoard(board);

            if(handleWinner(board, TTTimpl)){
                keepPlaying = false;
                continue;
            }

            System.out.print("Enter your move: ");
            move = sc.nextInt();
            
            //clients turn will be skipped if move chosen is invalid
            if(TTTimpl.isValidMove(move, board)){
                //clients move; server will return its move and we will add it to board
                board.set(move-1, 1);
                System.out.println("-----------------------------Board after your move");
                printBoard(board);
        
                if(handleWinner(board, TTTimpl)){
                    keepPlaying = false;
                    continue;
                }
            }else{
                System.out.println("You made a mistake and lost your turn!");
            }

            System.out.println("-----------------------------Server will play");
            int sMove = TTTimpl.serverMove(board);
            // System.out.println("-----------------------------Server takes"+ sMove);
            board.set(sMove, 2);

        }
    }
    /* TTT methods enc*/

    public static void main(String args[])
    {
        TTT TTTimpl=null;
        
        try
        {
            // initialize the ORB object request broker
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args,null);

	   
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            
            String name = "TTT";

           // narrow converts generic object into string type 
           TTTimpl = TTTHelper.narrow(ncRef.resolve_str(name));

            System.out.print("Enter String = ");
            Scanner sc = new Scanner(System.in);
            String str = sc.nextLine();

            //remote function call
            String tempStr = TTTimpl.process_string(str);
            
            System.out.println(tempStr);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}