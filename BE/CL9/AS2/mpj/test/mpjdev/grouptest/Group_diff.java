import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_diff {

	public static void main(String args[]) throws Exception{		

		MPI.Init(args);		
		Group grp = MPI.COMM_WORLD.Group();		
		int[] incl1 = {4,0,5,6};
		int[] incl2 = {6,7,4};
		Group grp1 = grp.Incl(incl1);		
		Group grp2 = grp.Incl(incl2);
		Group grp3 = null;
		
		try { Thread.currentThread().sleep(1000); }catch(Exception e){}		

		//System.out.println("<"+grp.Rank()+">1<"+grp1+">2<"+grp2+">\n");
		if(grp.Rank() == 0 || grp.Rank() ==5 || grp.Rank() == 4 || grp.Rank() == 6) {			
			//System.out.println("grp2.size "+grp2.Size());			
			grp3 = Group.Difference(grp1,grp2);
			System.out.println("gr<"+grp.Rank()+">:grp3<"+grp3.Rank()+"><grp3.size<"+grp3.Size()+">\n");
			//System.out.print("gr<"+grp.ids[grp.Rank()].rank()+">:ngrp<"+grp3.ids[grp3.Rank()].rank()+">\n");
			//System.out.println("grp3.size "+grp3.Size());
			/*
			System.out.print("gr<"+grp.ids[grp.Rank()].uuid()+">:ngrp<"+grp3.ids[grp3.Rank()].uuid()+">\n");		
			for(int i=0 ; i<grp3.ids.length ; i++) {
				System.out.print("grp3{"+grp3.Rank()+"}<"+grp3.ids[i].uuid()+">\n");	
			}*/
		}else {
			//System.out.print("<"+grp.Rank()+">");		
		}
			
		try { Thread.currentThread().sleep(1000); }catch(Exception e){}		
		MPI.Finalize();	
	}
}
