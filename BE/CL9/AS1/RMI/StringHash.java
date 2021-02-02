import java.rmi.*;

//Define interface with method that we will access remotely
public interface StringHash extends Remote {
    int getHash(String str) throws RemoteException;
}
