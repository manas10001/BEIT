import CorbaBasicModule.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.util.Scanner;

public class CorbaBasicClient{
    public static void main(String args[])
    {
        CorbaBasic CorbaBasicImpl=null;
        
        try
        {
            // initialize the ORB object request broker
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(args,null);

	   
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            
            String name = "CorbaBasic";

           // narrow converts generic object into string type 
           CorbaBasicImpl = CorbaBasicHelper.narrow(ncRef.resolve_str(name));

            System.out.print("Enter String = ");
            Scanner sc = new Scanner(System.in);
            String str = sc.nextLine();

            //remote function call
            String tempStr = CorbaBasicImpl.process_string(str);
            
            System.out.println(tempStr);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}