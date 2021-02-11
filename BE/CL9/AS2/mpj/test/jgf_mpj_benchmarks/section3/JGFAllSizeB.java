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
package jgf_mpj_benchmarks.section3;

import jgf_mpj_benchmarks.section3.moldyn.*;
import jgf_mpj_benchmarks.section3.montecarlo.*;
import jgf_mpj_benchmarks.section3.raytracer.*;
import jgf_mpj_benchmarks.jgfutil.*;
/*
import moldyn.*;
import montecarlo.*;
import raytracer.*;
import jgfutil.*;*/
import mpi.*;

public class JGFAllSizeB{ 

  public static int nprocess;
  public static int rank;

  public static void main(String argv[]) throws Exception{
    JGFAllSizeB jgfAllSizeB = new JGFAllSizeB(argv) ; 	  
  }

  public JGFAllSizeB() {
  }

  public JGFAllSizeB(String[] argv) throws Exception {

/* Initialise MPI */
    MPI.Init(argv);
    rank = MPI.COMM_WORLD.Rank();
    nprocess = MPI.COMM_WORLD.Size();

    int size = 1;

    if(rank==0) {
      JGFInstrumentor.printHeader(3,1,nprocess);
    }

    JGFMolDynBench mold = new JGFMolDynBench(nprocess,rank); 
    mold.JGFrun(size);

    //JGFMonteCarloBench mc = new JGFMonteCarloBench(nprocess,rank);
    //mc.JGFrun(size);

    JGFRayTracerBench rtb = new JGFRayTracerBench(nprocess,rank);
    rtb.JGFrun(size); 

/* Finalise MPI */
    MPI.Finalize();

    if(rank==0) {
      System.out.println("JGFAllSizeB (section3) TEST COMPLETE");
    }
 
  }
}


