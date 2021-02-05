import java.rmi.*;
import java.rmi.server.*;

public class AuthenticatorImpl extends UnicastRemoteObject implements Authenticator{
    public AuthenticatorImpl() throws RemoteException{
        super();
    }

    //returns authentication status true or false
    public boolean auth(String username, String password) throws RemoteException{
            return (username.equals("manas") && password.equals("password"));
    }
}