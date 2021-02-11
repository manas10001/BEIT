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
import jgf_mpj_benchmarks.*; 
//import jgfutil.*; 
import mpi.*;

public class JGFAlltoallBench implements JGFSection1{

  public static  int nprocess;
  public static  int rank;

  private static final int INITSIZE = 1;
  private static final int MAXSIZE =  1000000;
  private static final double TARGETTIME = 10.0;
  private static final int MLOOPSIZE = 20;
  private static final int SMAX = 1000000;
  private static final int SMIN = 4;

  public JGFAlltoallBench(int rank, int nprocess) {
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
    
/* Alltoall an array of doubles */

/* Create the timers */
      if(rank==0){
        JGFInstrumentor.addTimer("Section1:Alltoall:Double", "bytes");
        JGFInstrumentor.addTimer("Section1:Alltoall:Barrier", "barriers");
      }


/* loop over no of different message sizes */
    for(l=0;l<MLOOPSIZE;l++){
	    
      //System.out.println(l+":"+MLOOPSIZE);

/* Initialize the sending data */
      m_size = (int)(Math.exp(Math.log((double)SMIN)+(double) ((double) l/(double) MLOOPSIZE*logsize)));

      double [] recv_arr = new double[m_size*nprocess];
      double [] send_arr = new double[m_size*nprocess];
      time[0] = 0.0;
      size=INITSIZE;
      
      MPI.COMM_WORLD.Barrier();

/* Start the timer */

      while (time[0] < TARGETTIME && size < MAXSIZE){
        if(rank==0){
          JGFInstrumentor.resetTimer("Section1:Alltoall:Double");
          JGFInstrumentor.startTimer("Section1:Alltoall:Double");
        }


/* Carryout the broadcast operation */
        for (int k=0; k<size; k++){
          MPI.COMM_WORLD.Alltoall(send_arr,0,m_size,MPI.DOUBLE,
			  recv_arr,0,m_size,MPI.DOUBLE);
          MPI.COMM_WORLD.Barrier();

        }

/* Stop the timer. Note that this reports no of bytes sent per process  */
        if(rank==0){
          JGFInstrumentor.stopTimer("Section1:Alltoall:Double"); 
          time[0] = JGFInstrumentor.readTimer("Section1:Alltoall:Double"); 
          JGFInstrumentor.addOpsToTimer("Section1:Alltoall:Double",(double) size*m_size*8); 
        }

/* Broadcast time to the other processes */
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Bcast(time,0,1,MPI.DOUBLE,0);
        size *=2;
      }
 
        size /=2;

/* determine the cost of the Barrier, subtract the cost and write out
   the performance time */
      MPI.COMM_WORLD.Barrier();
      if(rank==0) {
        JGFInstrumentor.resetTimer("Section1:Alltoall:Barrier");
        JGFInstrumentor.startTimer("Section1:Alltoall:Barrier");
      }

      for (int k=0; k<size; k++){
        MPI.COMM_WORLD.Barrier();
      }

      if(rank==0) {
        JGFInstrumentor.stopTimer("Section1:Alltoall:Barrier");
        b_time = JGFInstrumentor.readTimer("Section1:Alltoall:Barrier");
        JGFInstrumentor.addTimeToTimer("Section1:Alltoall:Double", -b_time);
        JGFInstrumentor.printperfTimer("Section1:Alltoall:Double",m_size); 
      }

    }

/* Alltoall an array of objects containing a double */

/* Create the timer */
    if(rank==0){
      JGFInstrumentor.addTimer("Section1:Alltoall:Object", "objects");
    }


/* loop over no of different message sizes */
    for(l=0;l<MLOOPSIZE;l++){

/* Initialize the sending data */
      m_size = (int)(Math.exp(Math.log((double)SMIN)+(double) ((double) l/(double) MLOOPSIZE*logsize)));
      obj_double [] recv_arr_obj = new obj_double[m_size*nprocess];
      obj_double [] send_arr_obj = new obj_double[m_size*nprocess];
      for(int k=0;k<m_size*nprocess;k++){
       send_arr_obj[k] = new obj_double(0.0);
      }
      time[0] = 0.0;
      size=INITSIZE;

      MPI.COMM_WORLD.Barrier();

/* Start the timer */
      while (time[0] < TARGETTIME && size < MAXSIZE){
        if(rank==0){
          JGFInstrumentor.resetTimer("Section1:Alltoall:Object");
          JGFInstrumentor.startTimer("Section1:Alltoall:Object");
        }

/* Carryout the broadcast operation */
        for (int k=0; k<size; k++){
          MPI.COMM_WORLD.Alltoall(send_arr_obj,0,m_size,MPI.OBJECT,recv_arr_obj,0,m_size,MPI.OBJECT);
          MPI.COMM_WORLD.Barrier();

        }

/* Stop the timer */
        if(rank==0){
          JGFInstrumentor.stopTimer("Section1:Alltoall:Object");
          time[0] = JGFInstrumentor.readTimer("Section1:Alltoall:Object");
          JGFInstrumentor.addOpsToTimer("Section1:Alltoall:Object",(double) size*m_size);
          System.out.println("time " + time[0] + " size " + size);
        }

/* Broadcast time to the other processes */
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Bcast(time,0,1,MPI.DOUBLE,0);
        size *=2;
      }

        size /=2;

/* determine the cost of the Barrier, subtract the cost and write out the performance time */
      MPI.COMM_WORLD.Barrier();
      if(rank==0) {
        JGFInstrumentor.resetTimer("Section1:Alltoall:Barrier");
        JGFInstrumentor.startTimer("Section1:Alltoall:Barrier");
      }

      for (int k=0; k<size; k++){
        MPI.COMM_WORLD.Barrier();
      }

      if(rank==0) {
        JGFInstrumentor.stopTimer("Section1:Alltoall:Barrier");
        b_time = JGFInstrumentor.readTimer("Section1:Alltoall:Barrier");
        JGFInstrumentor.addTimeToTimer("Section1:Alltoall:Object", -b_time);
        JGFInstrumentor.printperfTimer("Section1:Alltoall:Object",m_size);
      }

    }

  }


  public static void main(String[] argv) throws MPIException{
  }

  public JGFAlltoallBench(String[] argv) throws Exception {

/* Initialise MPI */
     MPI.Init(argv);
     rank = MPI.COMM_WORLD.Rank();

     nprocess = MPI.COMM_WORLD.Size();

     if(rank==0){
      JGFInstrumentor.printHeader(1,0,nprocess);
     }

     JGFAlltoallBench ata = new JGFAlltoallBench(rank, nprocess);
     ata.JGFrun();

/* Finalise MPI */
     MPI.Finalize();

  }

}

