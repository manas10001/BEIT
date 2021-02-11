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

package jgf_mpj_benchmarks.section2.sparsematmult;

//package sparsematmult;
import jgf_mpj_benchmarks.jgfutil.*; 
import java.util.Random;
import mpi.*;

public class JGFSparseMatmultBench extends SparseMatmult implements JGFSection2{ 

  public static int nprocess;
  public static int rank;
  private int size; 
  private static final long RANDOM_SEED = 10101010;

  private static final int datasizes_M[] = {50000,100000,500000};
  private static final int datasizes_N[] = {50000,100000,500000};
  private static final int datasizes_nz[] = {250000,500000,2500000};
  private static final int SPARSE_NUM_ITER = 200;
//  private static final int SPARSE_NUM_ITER = 200;

  Random R = new Random(RANDOM_SEED);

  double [] x; 
  double [] y; 
  double [] p_y; 
  double [] val = null; 
  int [] col = null;
  int [] row = null;
  double [] buf_val = null; 
  int [] buf_col = null;
  int [] buf_row = null;

  int p_datasizes_nz,ref_p_datasizes_nz,rem_p_datasizes_nz;

  public JGFSparseMatmultBench(int nprocess, int rank) {
        this.nprocess=nprocess;
        this.rank=rank;
  }

  public void JGFsetsize(int size){
    this.size = size;

  }

  public void JGFinitialise() throws MPIException{


/* Determine the size of the arrays row,val and col on each
   process. Note that the array size on process (nprocess-1) may
   be smaller than the other array sizes. 
*/

  p_datasizes_nz = (datasizes_nz[size] + nprocess -1) /nprocess;
  ref_p_datasizes_nz = p_datasizes_nz;
  rem_p_datasizes_nz = p_datasizes_nz - ((p_datasizes_nz*nprocess) - datasizes_nz[size]);
  if(rank==(nprocess-1)){
   if((p_datasizes_nz*(rank+1)) > datasizes_nz[size]) {
     p_datasizes_nz = rem_p_datasizes_nz;
   }
  }

/* Initialise the arrays val,col,row. Create full sizes arrays on process 0 */

  x = RandomVector(datasizes_N[size], R);
  y = new double[datasizes_M[size]];
  p_y = new double[datasizes_M[size]];

  val = new double[p_datasizes_nz];
  col = new int[p_datasizes_nz];
  row = new int[p_datasizes_nz];

  if(rank==0) {
    buf_val = new double[datasizes_nz[size]];
    buf_col = new int[datasizes_nz[size]];
    buf_row = new int[datasizes_nz[size]];
  }

/* initialise arrays val,col,row on process 0 and send the data to 
the other processes 
*/

  if(rank==0) {

    for (int i=0; i<p_datasizes_nz; i++) {

        // generate random row index (0, M-1)
        row[i] = Math.abs(R.nextInt()) % datasizes_M[size];
        buf_row[i] = row[i]; 
        // generate random column index (0, N-1)
        col[i] = Math.abs(R.nextInt()) % datasizes_N[size];
        buf_col[i] = col[i];
        val[i] = R.nextDouble();
        buf_val[i] = val[i];
    }

    for(int k=1;k<nprocess;k++) {
      if(k==nprocess-1) {
        p_datasizes_nz = rem_p_datasizes_nz;
      } 
      for (int i=0; i<p_datasizes_nz; i++) {
        buf_row[i+(k*ref_p_datasizes_nz)] = Math.abs(R.nextInt()) % datasizes_M[size];
        buf_col[i+(k*ref_p_datasizes_nz)] = Math.abs(R.nextInt()) % datasizes_N[size];
        buf_val[i+(k*ref_p_datasizes_nz)] = R.nextDouble();
      }
      MPI.COMM_WORLD.Ssend(buf_row,(k*ref_p_datasizes_nz),p_datasizes_nz,MPI.INT,k,1);
      MPI.COMM_WORLD.Ssend(buf_col,(k*ref_p_datasizes_nz),p_datasizes_nz,MPI.INT,k,2);
      MPI.COMM_WORLD.Ssend(buf_val,(k*ref_p_datasizes_nz),p_datasizes_nz,MPI.DOUBLE,k,3);
    }
    
    p_datasizes_nz = ref_p_datasizes_nz; 
  } else {
    MPI.COMM_WORLD.Recv(row,0,p_datasizes_nz,MPI.INT,0,1);
    MPI.COMM_WORLD.Recv(col,0,p_datasizes_nz,MPI.INT,0,2);
    MPI.COMM_WORLD.Recv(val,0,p_datasizes_nz,MPI.DOUBLE,0,3);
  }

  }
 
  public void JGFkernel() throws MPIException{

    SparseMatmult.test(y, val, row, col, x, SPARSE_NUM_ITER, buf_row, p_y);

  }

  public void JGFvalidate(){

    if(rank==0) {
      double refval[] = {75.02484945753453,150.0130719633895,749.5245870753752};
      double dev = Math.abs(ytotal - refval[size]);
      if (dev > 1.0e-12 ){
        System.out.println("Validation failed");
        System.out.println("ytotal = " + ytotal + "  " + dev + "  " + size);
      }
    }

  }

  public void JGFtidyup(){
   System.gc();
  }  



  public void JGFrun(int size) throws MPIException{

    if(rank==0){
      JGFInstrumentor.addTimer("Section2:SparseMatmult:Kernel", "Iterations",size);
    }

    JGFsetsize(size); 
    JGFinitialise(); 
    JGFkernel(); 
    JGFvalidate(); 
    JGFtidyup(); 

    if(rank==0){     
      JGFInstrumentor.addOpsToTimer("Section2:SparseMatmult:Kernel", (double) (SPARSE_NUM_ITER));
      JGFInstrumentor.printTimer("Section2:SparseMatmult:Kernel"); 
    }
  }

        private static double[] RandomVector(int N, java.util.Random R)
        {
                double A[] = new double[N];

                for (int i=0; i<N; i++)
                        A[i] = R.nextDouble() * 1e-6;

                return A;
        }


}
