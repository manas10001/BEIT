import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_excl {

	public static void main(String args[]) throws Exception{		

		MPI.Init(args);		
		Group grp = MPI.COMM_WORLD.Group();
		System.out.print("<"+grp.Rank()+">");		
		if(grp.Rank() == 0)
			System.out.println("size<"+grp.Size()+">");
		int[] excl = {5,6,0,2,3};
		Group ngrp = grp.Excl(excl);
		try { Thread.currentThread().sleep(1000); }catch(Exception e){}		
		if(ngrp != null) {
			if(ngrp.Rank() == 0) {				
				//System.out.print("[ngrpsize<"+ngrp.Size()+">]");
			}

			//System.out.print("gr<"+grp.Rank()+">:ngr<"+ngrp.Rank()+">\n");				
			System.out.print("gr<"+grp.ids[grp.Rank()].rank()+">:ngr<"+ngrp.ids[ngrp.Rank()].rank()+">\n");	
			System.out.print("gr<"+grp.ids[grp.Rank()].uuid()+">:ngr<"+ngrp.ids[ngrp.Rank()].uuid()+">\n");	
			for(int i=0 ; i<ngrp.ids.length ; i++) {
				System.out.print("ngr{"+ngrp.Rank()+"}<"+ngrp.ids[i].uuid()+">\n");	
			}
				
		}else {
			//System.out.print("<"+grp.Rank()+">");		
		}			
		
		try { Thread.currentThread().sleep(1000); }catch(Exception e){}		
		MPI.Finalize();
	}
}
