import java.rmi.*;
import java.util.Scanner;

public class Server{
    public static void main(String[] args) {
        Scanner sc = null;
        try{
            sc = new Scanner(System.in);

            StringHashImpl strHash = new StringHashImpl();
            
            //bind remote object to server
            Naming.rebind("HashServer", strHash);
            
        }catch(Exception ex){
            System.out.println(ex.toString());
        }finally{
            sc.close();
        }
    }
}