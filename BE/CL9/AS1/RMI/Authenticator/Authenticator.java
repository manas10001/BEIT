import java.rmi.*;

//Define interface with method that we will access remotely
public interface Authenticator extends Remote {
    boolean auth(String username, String password) throws RemoteException;
}
