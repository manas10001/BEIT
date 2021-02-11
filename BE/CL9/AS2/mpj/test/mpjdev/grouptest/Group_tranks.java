import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_tranks {

	public static void main(String args[]) throws Exception{		

		MPI.Init(args);		
		Group grp = MPI.COMM_WORLD.Group();		
		int[] incl1 = {7,6,5,4};			
		int[] ranks1 = {0,1,2,3};		
		Group grp1 = grp.Incl(incl1);		
		int[] ranks2 = Group.Translate_ranks(grp1,ranks1,grp);

		for(int i=0 ; i<ranks2.length ; i++) {
			System.out.println("ranks2["+i+"]=<"+ranks2[i]+">");
		}
		
		try { Thread.currentThread().sleep(1000); }catch(Exception e){}		
		MPI.Finalize();	
	}
}
