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
package jgf_mpj_benchmarks.section2.sor;


//package sor;
import jgf_mpj_benchmarks.jgfutil.*; 
import java.util.Random;
import mpi.*;

public class JGFSORBench extends SOR implements JGFSection2{ 

  public static int nprocess;
  public static int rank;
  private int size; 
  private int datasizes[]={1000,1500,2000};
  private static final int JACOBI_NUM_ITER = 100;
  private static final long RANDOM_SEED = 10101010;

  public static int p_row;
  public static int ref_p_row;
  public static int rem_p_row;

  double [][] p_G = null;
  int m_size,n_size,m_length;

  Random R = new Random(RANDOM_SEED);

  public JGFSORBench(int nprocess, int rank) {
        this.nprocess=nprocess;
        this.rank=rank;
  }

  public void JGFsetsize(int size){
    this.size = size;
  }

  public void JGFinitialise(){

  }
 
  public void JGFkernel() throws MPIException{

  int iup = 0;

/* create the array G on process 0 */

  if(rank==0) {
    m_size = datasizes[size];
    n_size = datasizes[size];
  } else {
    m_size = 0;
    n_size = 0;
  }

  double G[][] = RandomMatrix(m_size, n_size,R);

/* create the sub arrays of G */

  p_row = (((datasizes[size] / 2) + nprocess -1) / nprocess)*2;
  ref_p_row = p_row;
  rem_p_row = p_row - ((p_row*nprocess) - datasizes[size]);
  if(rank==(nprocess-1)){
    if((p_row*(rank+1)) > datasizes[size]) {
       p_row = rem_p_row;
    }
  }

  p_G = new double [p_row+2][datasizes[size]]; 

/* copy or send the values of G to the sub arrays p_G */
   if(rank==0) {

      if(nprocess==1) {
        iup = p_row+1;
      } else {
        iup = p_row+2;
      }

      for(int i=1;i<iup;i++){
        for(int j=0;j<p_G[0].length;j++){
          p_G[i][j] = G[i-1][j]; 
        }
      }

      for(int j=0;j<G[0].length;j++){
        p_G[0][j] = 0.0; 
      }

      for(int k=1;k<nprocess;k++){
        if(k==nprocess-1) {
          m_length = rem_p_row + 1;
        } else {
          m_length = p_row + 2; 
        }
        MPI.COMM_WORLD.Send(G,(k*p_row)-1,m_length,MPI.OBJECT,k,k);
      }

   } else {
      MPI.COMM_WORLD.Recv(p_G,0,p_row+2,MPI.OBJECT,0,rank);
      //MPI.COMM_WORLD.Recv(p_G,0,p_row+1,MPI.OBJECT,0,rank);
   }

   if(rank==(nprocess-1)){
      for(int j=0;j<datasizes[size];j++){
        p_G[p_G.length-1][j] = 0.0; 
      }

   }
 
   MPI.COMM_WORLD.Barrier();

    System.gc();
    SORrun(1.25, p_G, JACOBI_NUM_ITER,G);


  }

  public void JGFvalidate(){

//    double refval[] = {0.4984199298207158,1.123010681492097,1.9967774998523777};
    double refval[] = {0.498574406322512,1.1234778980135105,1.9954895063582696};

    if(rank==0) {
      double dev = Math.abs(Gtotal - refval[size]);
      if (dev > 1.0e-12 ){
        System.out.println("Validation failed");
        System.out.println("Gtotal = " + Gtotal + "  " + dev + "  " + size);
      }
    }
  }

  public void JGFtidyup(){
   System.gc();
  }  



  public void JGFrun(int size) throws MPIException{

    if(rank==0) {
      JGFInstrumentor.addTimer("Section2:SOR:Kernel", "Iterations",size);
    }

    JGFsetsize(size); 
    JGFinitialise(); 
    JGFkernel(); 
    JGFvalidate(); 
    JGFtidyup(); 

    if(rank==0){    
      JGFInstrumentor.addOpsToTimer("Section2:SOR:Kernel", (double) (JACOBI_NUM_ITER));
      JGFInstrumentor.printTimer("Section2:SOR:Kernel"); 
    }
  }

 private static double[][] RandomMatrix(int M, int N, java.util.Random R)
  {
                double A[][] = new double[M][N];

        for (int i=0; i<N; i++)
                        for (int j=0; j<N; j++)
                        {
                A[i][j] = R.nextDouble() * 1e-6;
                        }      
                return A;
        }


}
