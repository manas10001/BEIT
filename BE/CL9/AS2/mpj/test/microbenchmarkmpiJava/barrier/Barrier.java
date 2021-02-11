package microbenchmarkmpiJava.barrier;

//
// mpiJava version : Taboada
//                   July 2002
// DES
//

import java.io.*;
import java.text.NumberFormat;
import mpi.*;

public class Barrier {

  public static void main(String[] args) throws MPIException {
  }

  public Barrier() {
  }

  public Barrier(String[] args) throws Exception {

    double      startwtime, endwtime, pot2npesd;
    int         i, iterations, ns, my_pe, npes, pot2npes;
    Status      status;
	long 		time;

    MPI.Init(args);

    my_pe = MPI.COMM_WORLD.Rank();
    npes  = MPI.COMM_WORLD.Size();

    MPI.COMM_WORLD.Barrier();

	  iterations = Integer.parseInt(System.getProperty("ITERATIONS"));
	
		pot2npes = (int)(Math.log(npes)/0.69314718);
		pot2npesd = (double)(Math.log(npes)/0.69314718);	

	  //Format the Number to Display
	  NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(6);
      nf.setMinimumFractionDigits(6);

	for (ns = 0; ns < iterations; ns++) {	
   		MPI.COMM_WORLD.Barrier();	
    	startwtime = MPI.Wtime();	
    	MPI.COMM_WORLD.Barrier();			  	
		endwtime = MPI.Wtime();

		if ( my_pe == 0) {
		    time = (long) (1000000*(endwtime - startwtime));	
		    System.out.println(nf.format(((double) time/1000000))+" \t "+time+" \t "+npes+" \t "+pot2npes+" \t "+nf.format(pot2npesd));
		}			
		
	}
		
    MPI.Finalize();    
    if(my_pe == 0)  
      System.out.println("Barrier TEST COMPLETE");
  }
}
