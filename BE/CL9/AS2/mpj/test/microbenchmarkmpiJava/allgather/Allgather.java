package microbenchmarkmpiJava.allgather; 
//
// mpiJava version : Taboada
//                   July 2002
// DES
//

import java.io.*;
import java.text.NumberFormat;
import java.lang.Math;
import mpi.*;

public class Allgather {

  public static void main(String[] args) throws MPIException {
  }

  public Allgather() {
  }

  public Allgather(String[] args) throws Exception { 

    double      startwtime, endwtime, pot2npesd;
    int         i, iterations, size, ns, my_pe, npes, localsize, pot2npes, pot2size;

    MPI.Init(args);

    my_pe = MPI.COMM_WORLD.Rank();
    npes  = MPI.COMM_WORLD.Size();

    MPI.COMM_WORLD.Barrier();
	
	  iterations = Integer.parseInt(System.getProperty("ITERATIONS"));
	
	  size = Integer.parseInt(System.getProperty("SIZE"));
  
	  localsize = size / npes;
		  
      byte A[] = new byte [localsize];
      byte B[] = new byte [size];	  
	  double timesBcast[] = new double [iterations];	  
	  double timesBcastReduce[] = new double [iterations];
	        
      for (i = 0; i < localsize; i++) {
	    A[i] = (byte)'0'; //(double) 1. / (i + 1);
      }

		pot2npes = (int)(Math.log(npes)/0.69314718);
		pot2npesd = (double)(Math.log(npes)/0.69314718);
		
		pot2size = (int)(Math.log(size)/1.386294361);
		if (pot2size < 0) pot2size = 0;	
		          

	    for (ns = 0; ns < iterations; ns++) {
		
	    	  MPI.COMM_WORLD.Barrier();	
			  	  
			  startwtime = MPI.Wtime();					
			  
      		  MPI.COMM_WORLD.Allgather(A,0,localsize,MPI.BYTE,B,0,localsize,MPI.BYTE);	
			  		  
			  endwtime = MPI.Wtime();
		  	  timesBcast[ns] = (double) (1000000*(endwtime - startwtime));	
		  			  
			}
   		
		MPI.COMM_WORLD.Reduce(timesBcast, 0, timesBcastReduce, 0, iterations, MPI.DOUBLE, MPI.MAX, 0);

		if (my_pe == 0) {
			  //Format the Number to Display
			  NumberFormat nf = NumberFormat.getInstance();
              nf.setMaximumFractionDigits(6);
              nf.setMinimumFractionDigits(6);	
			  
		      for (i = 0; i < iterations; i++) {
			  		System.out.print(nf.format((double) timesBcastReduce[i]/1000000)+" \t "+(long) timesBcastReduce[i]+" \t "+nf.format((double)(size/(timesBcastReduce[i])))); 
 			  		System.out.println(" \t "+npes+" \t "+pot2npes+" \t "+nf.format(pot2npesd)+" \t "+size+" \t "+pot2size);     		  
			  }
		}
		
    MPI.Finalize();    

    if(my_pe == 0)  
      System.out.println("Allgather TEST COMPLETE");
  }
}
