import java.rmi.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner sc = null;
        String str = null;
        //Server url from where we want to call the method remotely
        String url = "rmi://127.0.0.1/HashServer";

        try {
            //lookup method finds the reference of remote object
            StringHash strHashRemote = (StringHash)Naming.lookup(url);
            
            sc = new Scanner(System.in);
            System.out.println("Enter string to hash: ");
            str = sc.nextLine();

            //call the remote method
            System.out.println("The hash is: "+ strHashRemote.getHash(str));
        } 
        catch (Exception ex) {
            System.out.println(ex.toString());   
        }
        finally{
            sc.close();
        }
    }
}