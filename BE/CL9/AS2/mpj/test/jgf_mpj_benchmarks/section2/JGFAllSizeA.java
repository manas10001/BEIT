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
package jgf_mpj_benchmarks.section2;

//import crypt.*;
//import series.*;
//import lufact.*;
//import sor.*;
//import sparsematmult.*;

import jgf_mpj_benchmarks.section2.crypt.*; 
import jgf_mpj_benchmarks.section2.series.*; 
import jgf_mpj_benchmarks.section2.lufact.*; 
import jgf_mpj_benchmarks.section2.sor.*; 
import jgf_mpj_benchmarks.section2.sparsematmult.*; 
import jgf_mpj_benchmarks.jgfutil.*; 
//import jgfutil.*; 
import mpi.*;

public class JGFAllSizeA{ 

  public static int nprocess;
  public static int rank;

  public static void main(String argv[]) throws Exception{
    JGFAllSizeA jgfAllSizeA = new JGFAllSizeA(argv); 	  
  }

  public JGFAllSizeA() {
  }

  public JGFAllSizeA(String[] argv) throws Exception {

/* Initialise MPI */
     MPI.Init(argv);
     rank = MPI.COMM_WORLD.Rank();
     nprocess = MPI.COMM_WORLD.Size();

    int size = 0;

    if(rank==0) {
      JGFInstrumentor.printHeader(2,0,nprocess);
    }

    JGFSeriesBench se = new JGFSeriesBench(nprocess,rank);
    se.JGFrun(size);

    JGFLUFactBench lub = new JGFLUFactBench(nprocess,rank);
    lub.JGFrun(size);

    JGFCryptBench cb = new JGFCryptBench(nprocess,rank); 
    cb.JGFrun(size);

    JGFSORBench jb = new JGFSORBench(nprocess,rank);
    jb.JGFrun(size);

    JGFSparseMatmultBench smm = new JGFSparseMatmultBench(nprocess,rank);
    smm.JGFrun(size);

/* Finalise MPI */
    MPI.Finalize();
     
    if(rank==0) {
      System.out.println("JGFAllSizeA section2 TEST COMPLETE");
    }
 
  }
}


