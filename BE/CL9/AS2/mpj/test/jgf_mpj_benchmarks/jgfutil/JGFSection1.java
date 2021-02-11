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


package jgf_mpj_benchmarks.jgfutil; 
import mpi.*;

public interface JGFSection1 {

   public final int INITSIZE = 10000;
   public final int MAXSIZE = 1000000000;
   public final double TARGETTIME = 1.0; 

   public void JGFrun() throws MPIException; 

}
