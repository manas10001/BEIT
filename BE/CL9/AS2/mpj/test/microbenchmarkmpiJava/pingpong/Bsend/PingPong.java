package microbenchmarkmpiJava.pingpong.Bsend; 
//
// mpiJava version : Taboada
//                   July 2002
// DES
//

import java.io.*;
import java.text.NumberFormat;
import java.lang.Math;
import mpi.*;
import java.nio.ByteBuffer ;
//no detach thing.

public class PingPong {

	static private byte A[];
	//static final private byte B[] = new byte [10000000];
	//static final private mpi.Buffer B = new mpi.Buffer(10000000+100);  
	static final private ByteBuffer B = 
		ByteBuffer.allocate( 10000000+100);  

  public static void main(String[] args) throws MPIException {
  }

  public PingPong() {
  }

  public PingPong(String[] args) throws Exception {
	  

    double      startwtime, endwtime; 
    int         i, iterations, size, ns, my_pe, npes, pot2size;
    Status      status;
	int 		MPI_BSEND_OVERHEAD = 10000000;
	long 		time;
	
    MPI.Init(args);

    my_pe = MPI.COMM_WORLD.Rank();
    npes  = MPI.COMM_WORLD.Size();

	
	  iterations = Integer.parseInt(System.getProperty("ITERATIONS"));
	
	  size = Integer.parseInt(System.getProperty("SIZE"));
	  
     A = new byte [size];
    // byte B[] = new byte [MPI_BSEND_OVERHEAD];
	        

          
		MPI.Buffer_attach(B);

		pot2size = (int)(Math.log((double) size)/1.386294361);
		if (pot2size < 0) pot2size = 0;

		for (i = 0; i < size; i++) {
	    	A[i] = (byte)'0'; //(double) 1. / (i + 1);
      	}
			
		MPI.COMM_WORLD.Barrier();			  

	if (my_pe == 0) {
		for (ns = 0; ns < iterations; ns++) {		
		
			startwtime = MPI.Wtime();	
			MPI.COMM_WORLD.Bsend(A, 0, size, MPI.BYTE, 1, 10);
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
			status = MPI.COMM_WORLD.Recv(A, 0 , size, MPI.BYTE, 0, 10);
			MPI.COMM_WORLD.Bsend(A, 0, size, MPI.BYTE, 0, 20);
		}	  		
	}
    MPI.Finalize();    
    if(my_pe == 0) {
       System.out.println(" Bsend PingPong TEST COMPLETE");	    
    }
  }
}
