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

package jgf_mpj_benchmarks.section2.lufact;

//package lufact; 
import jgf_mpj_benchmarks.jgfutil.*; 
import mpi.*;

public class JGFLUFactBench extends Linpack implements JGFSection2{

  public static int nprocess;
  public static int rank;
  private int size;
  private int datasizes[] = {500,1000,2000};
  public static int rem_p_ldaa;
  public static int real_p_ldaa;

  public JGFLUFactBench(int nprocess, int rank) {
        this.nprocess=nprocess;
        this.rank=rank;
  }

  public void JGFsetsize(int size){
    this.size = size;
  }

  public void JGFinitialise() throws MPIException{

    int r_count,z_count;
    int p_ldaa;

    n = datasizes[size]; 
    ldaa = n; 
    lda = ldaa + 1;

    if(rank==0) {
      a = new double[ldaa][lda];
      b = new double [ldaa];
      x = new double [ldaa];
    }

    ipvt = new int [ldaa];

/* determine the size of the sub arrays and copy the data
   in a cyclic distribution to the sub arrays.  */

    p_ldaa = (ldaa + nprocess - 1) / nprocess;
    rem_p_ldaa = (p_ldaa*nprocess) - ldaa;

    real_p_ldaa = p_ldaa;
    for(int i=1;i<=rem_p_ldaa;i++){
      if(rank==(nprocess-i)){
        real_p_ldaa = p_ldaa-1;
      }
    }
 
    buf_a = new double[p_ldaa][lda];
    list = new int [ldaa];
    buf_list = new int [ldaa];

    if(rank==0) {
      long nl = (long) n;   //avoid integer overflow
      ops = (2.0*(nl*nl*nl))/3.0 + 2.0*(nl*nl);
    }

    if(rank==0) {
      norma = matgen(a,lda,n,b);    
    }

    if(rank==0) {
      r_count = 0;
      z_count = 0;
      for(int i=0;i<a.length;i++){
       if(r_count==0) {
         for(int l=0;l<a[0].length;l++){
           buf_a[z_count][l] = a[i][l]; 
         }
         z_count++;
       } else {
         MPI.COMM_WORLD.Send(a,i,1,MPI.OBJECT,r_count,10);     
       }
     
       buf_list[i] = z_count - 1;
       list[i] = r_count;
       r_count++;
       if(r_count == nprocess) {
        r_count = 0;
       }

      }

    } else {
      for(int i=0;i<real_p_ldaa;i++){
        MPI.COMM_WORLD.Recv(buf_a,i,1,MPI.OBJECT,0,10);
      }
      for(int i=real_p_ldaa;i<buf_a.length;i++){
        for(int ki=0;ki<buf_a[0].length;ki++){
          buf_a[i][ki] = -9.0;
        }
      }

    }

    MPI.COMM_WORLD.Bcast(list,0,list.length,MPI.INT,0);
    MPI.COMM_WORLD.Bcast(buf_list,0,list.length,MPI.INT,0);

  }

  public void JGFkernel() throws MPIException{

    MPI.COMM_WORLD.Barrier();
    if(rank==0) {    
      JGFInstrumentor.startTimer("Section2:LUFact:Kernel");  
    }
 
    info = dgefa(a,lda,n,ipvt);

    if(rank==0) {
    dgesl(a,lda,n,ipvt,b,0);
    }


    MPI.COMM_WORLD.Barrier();
    if(rank==0) {    
    JGFInstrumentor.stopTimer("Section2:LUFact:Kernel"); 
    }  
  }

  public void JGFvalidate(){

    int i;
    double eps,residn;
    final double ref[] = {6.0, 12.0, 20.0}; 

    if(rank==0) {
    for (i = 0; i < n; i++) {
      x[i] = b[i];
    }
    norma = matgen(a,lda,n,b);
    for (i = 0; i < n; i++) {
      b[i] = -b[i];
    }

    dmxpy(n,b,n,lda,x,a);
    resid = 0.0;
    normx = 0.0;
    for (i = 0; i < n; i++) {
      resid = (resid > abs(b[i])) ? resid : abs(b[i]);
      normx = (normx > abs(x[i])) ? normx : abs(x[i]);
    }

    eps =  epslon((double)1.0);
    residn = resid/( n*norma*normx*eps );
    
    if (residn > ref[size]) {
	System.out.println("Validation failed");
	System.out.println("Computed Norm Res = " + residn);
        System.out.println("Reference Norm Res = " + ref[size]); 
    }
    }

  }

    public void JGFtidyup(){
    // Make sure large arrays are gc'd.
    a = null; 
    b = null;
    x = null;
    ipvt = null; 
    System.gc();  
  }


  public void JGFrun(int size) throws MPIException{

    if(rank==0){
      JGFInstrumentor.addTimer("Section2:LUFact:Kernel", "Mflops",size);
    }

    JGFsetsize(size); 
    JGFinitialise(); 
    JGFkernel(); 
    JGFvalidate(); 
    JGFtidyup(); 

    if(rank==0){
      JGFInstrumentor.addOpsToTimer("Section2:LUFact:Kernel", ops/1.0e06);
      JGFInstrumentor.printTimer("Section2:LUFact:Kernel"); 
    }

  }

}
