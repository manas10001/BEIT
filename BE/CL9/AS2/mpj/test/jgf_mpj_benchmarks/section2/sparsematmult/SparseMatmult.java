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
*      adapted from SciMark 2.0, author Roldan Pozo (pozo@cam.nist.gov)   *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package jgf_mpj_benchmarks.section2.sparsematmult;

//package sparsematmult;
import jgf_mpj_benchmarks.jgfutil.*;
import mpi.*;

public class SparseMatmult
{

  public static double ytotal = 0.0;

	/* 10 iterations used to make kernel have roughly
		same granulairty as other Scimark kernels. */

	public static void test( double y[], double val[], int row[],
				int col[], double x[], int NUM_ITERATIONS, int buf_row[], double p_y[]) throws MPIException
	{
        int nz = val.length;


        MPI.COMM_WORLD.Barrier();
        if(JGFSparseMatmultBench.rank==0){
          JGFInstrumentor.startTimer("Section2:SparseMatmult:Kernel"); 
        }

		for (int reps=0; reps<NUM_ITERATIONS; reps++)
		{
			for (int i=0; i<nz; i++)
			{
      			p_y[ row[i] ] += x[ col[i] ] * val[i];
			}
                // create updated copy on each process 
                MPI.COMM_WORLD.Allreduce(p_y,0,y,0,y.length,MPI.DOUBLE,MPI.SUM);
		}

        MPI.COMM_WORLD.Barrier();

        if(JGFSparseMatmultBench.rank==0){
          JGFInstrumentor.stopTimer("Section2:SparseMatmult:Kernel"); 
        } 

        if(JGFSparseMatmultBench.rank==0){
          for (int i=0; i<buf_row.length; i++) {
            ytotal += y[ buf_row[i] ];
          }
        }

	}
}
