import java.rmi.*;
import java.rmi.server.*;

public class StringHashImpl extends UnicastRemoteObject implements StringHash{
    public StringHashImpl() throws RemoteException{
        super();
    }

    //returns hashCode of a string
    public int getHash(String s) throws RemoteException{
            return s.hashCode();
    }
}

