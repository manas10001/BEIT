import mpjdev.*;
import xdev.*;
import mpjbuf.*;
import java.util.Arrays;

public class init {
	public static void main(String args[]) throws Exception{		
		//Device dev = Device.newInstance("niodev");
		Device dev = MPJDev.init(new String[]{"","","niodev"});
		long t1 = System.nanoTime();
		xdev.ProcessID [] ids = dev.init(args);		
		xdev.ProcessID myID = dev.id();

		if(Integer.parseInt(args[0]) == 0) {			
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
	}
}
