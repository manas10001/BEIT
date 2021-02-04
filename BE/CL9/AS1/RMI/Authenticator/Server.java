import java.rmi.*;

public class Server{
    public static void main(String[] args) {
        try{
            AuthenticatorImpl authenticator = new AuthenticatorImpl();
            
            //bind remote object to server
            Naming.rebind("Authenticator", authenticator);
            
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
    }
}