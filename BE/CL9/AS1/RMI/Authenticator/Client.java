import java.rmi.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner sc = null;
        String username = null, password = null;
        //Server url from where we want to call the method remotely
        String url = "rmi://127.0.0.1/Authenticator";

        try {
            //lookup method finds the reference of remote object
            Authenticator authenticator = (Authenticator)Naming.lookup(url);
            
            sc = new Scanner(System.in);
            System.out.print("Enter Username: ");
            username = sc.nextLine();
            System.out.printf("Enter Password: ");
            password = sc.nextLine();

            //call the remote method
            if(authenticator.auth(username, password))
                System.out.println("User account Authenticated!");
            else
                System.out.println("Authentication Failed!");
        } 
        catch (Exception ex) {
            System.out.println(ex.toString());   
        }
        finally{
            sc.close();
        }
    }
}