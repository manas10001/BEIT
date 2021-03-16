import TTTModule.TTTPOA;
import java.lang.String;
import java.util.ArrayList;

//extend the portable obj adapter it manages server side resources
class TTTImpl extends TTTPOA{
    
    TTTImpl(){
        super();
    }    

    /* Helper method to convert string to arraylist */
    public ArrayList<Integer> convertToArlst(String str){
        ArrayList<Integer> ar = new ArrayList<Integer>();

        for(int i = 0; i < str.length(); i++){
            ar.add(Character.getNumericValue(str.charAt(i)));
        }

        return ar;
    }

    /* TTT methods ported to be corba compatible */

    public boolean isValidMove(int move, String boardStr){
        ArrayList<Integer> board = convertToArlst(boardStr);
        if(board.get(move-1) == 0)
            return true;
        return false;
    }

    //chooses the servers move and returns its position
    public int serverMove(String boardStr){
        ArrayList<Integer> board = convertToArlst(boardStr);
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
    public int howFull(String boardStr){
        ArrayList<Integer> board = convertToArlst(boardStr);
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
    public int determineWinner(String boardStr){
        ArrayList<Integer> board = convertToArlst(boardStr);
        int result = 0;

        if(board.get(0) == board.get(1) && board.get(1) == board.get(2))
            result = board.get(0);
        
        if(board.get(3) == board.get(4) && board.get(4) == board.get(5) && result == 0)
            result = board.get(3);

        if(board.get(6) == board.get(7) && board.get(7) == board.get(8) && result == 0)
            result = board.get(6);

        if(board.get(0) == board.get(3) && board.get(3) == board.get(6) && result == 0)
            result = board.get(0);

        if(board.get(1) == board.get(4) && board.get(4) == board.get(7) && result == 0)
            result = board.get(1);

        if(board.get(2) == board.get(5) && board.get(5) == board.get(8) && result == 0)
            result = board.get(2);

        if(board.get(0) == board.get(4) && board.get(4) == board.get(8) && result == 0)
            result = board.get(0);

        if(board.get(2) == board.get(4) && board.get(4) == board.get(6) && result == 0)
            result = board.get(2);


        //if we cant find a winner and board is full its a tie
        if(result == 0 && howFull(boardStr) == 9)
            result = -1;

        return result;
    }
}