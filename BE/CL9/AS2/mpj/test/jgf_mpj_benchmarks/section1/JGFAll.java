/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - MPJ Version 1.0         *
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
import mpi.*;

public class JGFAll{ 

  public static int nprocess;
  public static int rank;

  public static void main(String argv[]) throws Exception{
    JGFAll jgfAll = new JGFAll(argv) ; 	  
  }

  public JGFAll() {
  }

  public JGFAll(String[] argv) throws Exception {

/* Initialise MPI */
    MPI.Init(argv);
    rank = MPI.COMM_WORLD.Rank();    
    nprocess = MPI.COMM_WORLD.Size();

    if(rank==0) {
      JGFInstrumentor.printHeader(1,0,nprocess);
    }

    JGFAlltoallBench ata = new JGFAlltoallBench(rank, nprocess);
    ata.JGFrun();

    JGFBarrierBench ba = new JGFBarrierBench(rank, nprocess);
    ba.JGFrun();
    
    //this throws an exception if i un-comment the above tests.
    //the exception is thrown while 
    JGFBcastBench bc = new JGFBcastBench(rank, nprocess);
    bc.JGFrun();

    //Hangs for objects 
    JGFGatherBench ga = new JGFGatherBench(rank, nprocess);
    ga.JGFrun();

    //JGFPingPongBench pp = new JGFPingPongBench(rank, nprocess);
    //pp.JGFrun(); 
  
    JGFReduceBench rd = new JGFReduceBench(rank, nprocess);
    rd.JGFrun();

    JGFScatterBench sc = new JGFScatterBench(rank, nprocess);
    sc.JGFrun();

/* Finalise MPI */
    MPI.Finalize();
 
    if(rank==0) {
      System.out.println("JGFAll Section1 TEST COMPLETE");
    }
    
  }
}


