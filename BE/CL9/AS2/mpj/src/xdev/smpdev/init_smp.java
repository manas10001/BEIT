
package xdev.smpdev;

import mpjdev.*;
import xdev.*;
import mpjbuf.*;
import java.util.Arrays;

public class init_smp {
	public static void main(String args[]) throws Exception{		
     	Device dev =  new xdev.smpdev.SMPDevice();
		long t1 = System.nanoTime();
xdev.ProcessID [] ids ;
	
	try{
               ids = dev.init(args);		

		xdev.ProcessID myID = dev.id();

		if(Integer.parseInt(args[1]) == 0) { // i have changed from args[0] to args[1]			
			System.out.print("\n"+args[0]+">su-time<"+(System.nanoTime()-t1)/(1000*1000*1000)+">");		
			System.out.println("myID "+myID);
			System.out.println("rank "+args[0]);
			System.out.println("uuid "+myID.uuid());		
		
			for(int i=0 ; i<ids.length ; i++) {
				System.out.println("\n -----<"+i+">------");
				System.out.print("ids["+i+"]=>"+ids[i]+"\t");	
				System.out.println("uuid "+ids[i].uuid());		
			}			
		}
		dev.finish();

        }catch (Exception ee) {  

	System.out.println("here is the exception in init_smp");
        ee.printStackTrace();
        }


	}
}

