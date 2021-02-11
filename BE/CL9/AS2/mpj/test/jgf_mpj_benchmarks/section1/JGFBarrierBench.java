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


import jgf_mpj_benchmarks.*; 
//import jgfutil.*; 
import mpi.*;

public class JGFBarrierBench implements JGFSection1{

  public  static  int nprocess;
  public  static  int rank;

  private static final int INITSIZE = 1;
  private static final int MAXSIZE = 1000000;
  private static final double TARGETTIME = 10.0;

  public JGFBarrierBench(int rank, int nprocess) {
	this.rank = rank;    
        this.nprocess=nprocess;
  }

  public void JGFrun() throws MPIException {

    int size;
    double [] time = new double[1];


/* Create the timer */ 
      if(rank==0){
        JGFInstrumentor.addTimer("Section1:Barrier", "barriers");
      }

      time[0] = 0.0;
      size=INITSIZE;

      MPI.COMM_WORLD.Barrier();

/* Start the timer */
      while (time[0] < TARGETTIME && size < MAXSIZE){
        if(rank==0){
          JGFInstrumentor.resetTimer("Section1:Barrier");
          JGFInstrumentor.startTimer("Section1:Barrier");
        }

/* Carryout the barrier operation */
        for (int k=0; k<size; k++){
          MPI.COMM_WORLD.Barrier();
        }

/* Stop the timer */
        if(rank==0){
          JGFInstrumentor.stopTimer("Section1:Barrier"); 
          time[0] = JGFInstrumentor.readTimer("Section1:Barrier"); 
          JGFInstrumentor.addOpsToTimer("Section1:Barrier",(double) size); 
        }

/* Broadcast time to the other processes */
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Bcast(time,0,1,MPI.DOUBLE,0);
        size *=2;
      }
 
/* Print the timing information */
        if(rank==0){
          JGFInstrumentor.printperfTimer("Section1:Barrier");
        }

  }

  public static void main(String[] argv) throws MPIException{
  }

  public JGFBarrierBench(String[] argv) throws Exception {

/* Initialise MPI */
     MPI.Init(argv);
     rank = MPI.COMM_WORLD.Rank();
     nprocess = MPI.COMM_WORLD.Size();

     if(rank==0){
     JGFInstrumentor.printHeader(1,0,nprocess);
     }
     JGFBarrierBench ba = new JGFBarrierBench(rank, nprocess);
     ba.JGFrun();

/* Finalise MPI */
     MPI.Finalize();

  }

}

