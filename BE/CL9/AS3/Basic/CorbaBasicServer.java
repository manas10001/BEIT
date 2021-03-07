import CorbaBasicModule.CorbaBasic;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

public class CorbaBasicServer {
    public static void main(String[] args)
    {
        try
        {
            // initialize the ORB
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args,null);

            // initialize the POA
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // creating the  object of implementation
            CorbaBasicImpl cbimpl = new CorbaBasicImpl();
            
            // get the object reference from the servant class
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(cbimpl);
            
            CorbaBasic helper_ref = CorbaBasicModule.CorbaBasicHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            

            String name = "CorbaBasic";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path,helper_ref);

            System.out.println("Server started");
            orb.run();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
