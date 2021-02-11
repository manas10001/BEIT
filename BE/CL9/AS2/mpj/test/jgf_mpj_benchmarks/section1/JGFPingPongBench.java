/**************************************************************************
*                                                                         *
*         Java Grande Forum Benchmark Suite - MPJ Version 1.0             *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         * 
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package jgf_mpj_benchmarks.section1;


import java.io.*;
//import jgfutil.*; 
import jgf_mpj_benchmarks.*; 
import mpi.*;

public class JGFPingPongBench implements JGFSection1{

  public  static  int nprocess;
  public  static  int rank;

  private static final int INITSIZE = 1;
  private static final int MAXSIZE =  1000000;
  private static final double TARGETTIME =  10.0;
  private static final int MLOOPSIZE = 25;
  private static final int SMAX = 5000000;
  private static final int SMIN = 4;
  private int count = 1;

  public JGFPingPongBench(int rank, int nprocess) {
	this.rank = rank;  
        this.nprocess=nprocess;
  }

  public void JGFrun() throws MPIException {

    int size,i,l,m_size;
    double logsize;
    double b_time; 
    b_time = 0.0;
    double [] time = new double[1];

    m_size = 0;
    logsize = Math.log((double) SMAX) - Math.log((double) SMIN);

// PingPong an array of doubles 

// Create the timer 
      if(rank==0){
        JGFInstrumentor.addTimer("Section1:PingPong:Double", "bytes");
      }


// loop over no of different message sizes 
    for(l=0;l<MLOOPSIZE;l++){

// Initialize the sending data 
      m_size = (int)(Math.exp(Math.log((double)SMIN)+(double) ((double) l/(double) MLOOPSIZE*logsize)));
      double [] arr = new double[m_size];
      time[0] = 0.0;
      size=INITSIZE;

      MPI.COMM_WORLD.Barrier();

// Start the timer 
      while (time[0] < TARGETTIME && size < MAXSIZE){
        if(rank==0){
          JGFInstrumentor.resetTimer("Section1:PingPong:Double");
          JGFInstrumentor.startTimer("Section1:PingPong:Double");

// Carryout the pingpong on process 0 
          for (int k=0; k<size; k++){
            MPI.COMM_WORLD.Ssend(arr,0,arr.length,MPI.DOUBLE,1,1);
            MPI.COMM_WORLD.Recv(arr,0,arr.length,MPI.DOUBLE,1,2);
          }

// Stop the timer 
          JGFInstrumentor.stopTimer("Section1:PingPong:Double"); 
          time[0] = JGFInstrumentor.readTimer("Section1:PingPong:Double"); 
          JGFInstrumentor.addOpsToTimer("Section1:PingPong:Double",(double) size*arr.length*8); 

        } else {

// Carryout the pingpong on process 1 
          for (int k=0; k<size; k++){
            MPI.COMM_WORLD.Recv(arr,0,arr.length,MPI.DOUBLE,0,1);
            MPI.COMM_WORLD.Ssend(arr,0,arr.length,MPI.DOUBLE,0,2);
          }
 
        }

// Broadcast time to the other processes 
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Bcast(time,0,1,MPI.DOUBLE,0);
        size *=2;
      }
 
// write out the performance time 
      MPI.COMM_WORLD.Barrier();
      if(rank==0){
        JGFInstrumentor.printperfTimer("Section1:PingPong:Double",arr.length); 
      }

    }

/* PingPong an array of objects containing a double */

/* Create the timer */
    if(rank==0){
      JGFInstrumentor.addTimer("Section1:PingPong:Object", "objects");
    }


/* loop over no of different message sizes */
    for(l=0;l<MLOOPSIZE;l++){

/* Initialize the sending data */
      m_size = (int)(Math.exp(Math.log((double)SMIN)+(double) ((double) l/(double) MLOOPSIZE*logsize)));
      obj_double [] arr_obj = new obj_double[m_size];
      for(int k=0;k<m_size;k++){
       arr_obj[k] = new obj_double(0.0);
      }
      time[0] = 0.0;
      size=INITSIZE;

      MPI.COMM_WORLD.Barrier();

/* Start the timer */
      while (time[0] < TARGETTIME && size < MAXSIZE){
count++;
        if(rank==0){
          JGFInstrumentor.resetTimer("Section1:PingPong:Object");
          JGFInstrumentor.startTimer("Section1:PingPong:Object");
/* Carryout the pingpong operation on process 0 */
          for (int k=0; k<size; k++){ 
 //           System.out.println(" Process <"+rank+"> ssending a message "+
//			    "to process <1> with tag <"+(1+count)+">");
            MPI.COMM_WORLD.Ssend(arr_obj,0,arr_obj.length,MPI.OBJECT,1,(1+count) 
		    );
  //          System.out.println(" Process <"+rank+"> receiving a message "+
//			    "from process <1> with tag <"+(2+count)+">");
            MPI.COMM_WORLD.Recv(arr_obj,0,arr_obj.length,MPI.OBJECT,1,(2+count));
          }

/* Stop the timer */
          JGFInstrumentor.stopTimer("Section1:PingPong:Object");
          time[0] = JGFInstrumentor.readTimer("Section1:PingPong:Object");
          JGFInstrumentor.addOpsToTimer("Section1:PingPong:Object",(double) size*arr_obj.length);

        } else {

/* Carryout the pingpong operation on process 1 */
          for (int k=0; k<size; k++){
  //          System.out.println(" Process <"+rank+"> receiving a message "+
//			    "from process <0> with tag <"+(1+count)+">");
            MPI.COMM_WORLD.Recv(arr_obj,0,arr_obj.length,MPI.OBJECT,0,
			    (1+count));
  //          System.out.println(" Process <"+rank+"> ssending a message "+
//			    "to process <0> with tag <"+(2+count)+">");
            MPI.COMM_WORLD.Ssend(arr_obj,0,arr_obj.length,MPI.OBJECT,0,
			    (2+count));
          }

        }

/* Broadcast time to the other processes */
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Bcast(time,0,1,MPI.DOUBLE,0);
        size *=2;
      }


/* write out the performance time */
      MPI.COMM_WORLD.Barrier();
      if(rank==0) {
        JGFInstrumentor.printperfTimer("Section1:PingPong:Object",arr_obj.length);
      }

    }

  }


  public static void main(String[] argv) throws MPIException{
  }

  public JGFPingPongBench(String[] argv) throws Exception {

/* Initialise MPI */
     MPI.Init(argv);
     rank = MPI.COMM_WORLD.Rank();
     nprocess = MPI.COMM_WORLD.Size();

     if(nprocess!=2) {
       if(rank==0) {
         System.out.println("The JGFPingPong benchmark may only be executed on 2 processes");
       }
       MPI.COMM_WORLD.Barrier();
       System.exit(0);
     }

     if(rank==0){
     JGFInstrumentor.printHeader(1,0,nprocess);
     }
     JGFPingPongBench pp = new JGFPingPongBench(rank, nprocess);
     pp.JGFrun();

/* Finalise MPI */
     MPI.Finalize();

  }

}

