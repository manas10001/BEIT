package microbenchmarkmpiJava.pingpong.Send; 

//
// mpiJava version : Taboada
//                   July 2002
// DES
//

import java.io.*;
import java.text.NumberFormat;
import java.lang.Math;
import mpi.*;

public class PingPong {


  public static void main(String[] args) throws MPIException {
  }

  public PingPong() {
  }

  public PingPong(String[] args) throws Exception {
	  

    double      startwtime, endwtime;
    int         i, iterations, size, ns, my_pe, npes, pot2size;
    Status      status;
	long 		time;
	
    MPI.Init(args);

    my_pe = MPI.COMM_WORLD.Rank();
    npes  = MPI.COMM_WORLD.Size();

    MPI.COMM_WORLD.Barrier();

	  iterations = Integer.parseInt(System.getProperty("ITERATIONS"));
	
	  size = Integer.parseInt(System.getProperty("SIZE"));
	  
      byte A[] = new byte [size];
      
      for (i = 0; i < size; i++) {
	    A[i] = (byte)'0'; //(double) 1. / (i + 1);
      }

		pot2size = (int)(Math.log((double) size)/1.386294361);
		if (pot2size < 0) pot2size = 0;          


		MPI.COMM_WORLD.Barrier();			  

	if (my_pe == 0) {
		for (ns = 0; ns < iterations; ns++) {	
		 MPI.COMM_WORLD.Barrier();	
			startwtime = MPI.Wtime();	
			MPI.COMM_WORLD.Send(A, 0, size, MPI.BYTE, 1, 10);
			status = MPI.COMM_WORLD.Recv(A, 0, size, MPI.BYTE, 1, 20);			  	
			endwtime = MPI.Wtime();

			//Format the Number to Display
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(6);
			nf.setMinimumFractionDigits(6);			  
			
			time = (long) (500000.*(endwtime - startwtime));	
			System.out.println(nf.format((double) time/1000000)+" \t "+time+" \t "+nf.format((double) size/time)+" \t "+size+" \t "+pot2size);
		}
	}
	if (my_pe == 1) {
		for (ns = 0; ns < iterations; ns++) {			
                       MPI.COMM_WORLD.Barrier();
			status = MPI.COMM_WORLD.Recv(A, 0 , size, MPI.BYTE, 0, 10);
			MPI.COMM_WORLD.Send(A, 0, size, MPI.BYTE, 0, 20);
		}	  		
	}
    MPI.Finalize();    
    if(my_pe == 0) {
       System.out.println(" Send PingPong TEST COMPLETE");	    
    }
  }
}
