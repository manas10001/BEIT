import TTTModule.TTT;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

public class TTTServer {
    public static void main(String[] args)
    {
        try
        {
            // Creates and initializes an ORB instance
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args,null);

            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();


            TTTImpl cbimpl = new TTTImpl();
            
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(cbimpl);

            TTT helper_ref = TTTModule.TTTHelper.narrow(ref);

            // get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            

            String name = "TTT";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path,helper_ref);

            System.out.println("-------------Server started------------");
            
            // wait for invocations from clients
            orb.run();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
