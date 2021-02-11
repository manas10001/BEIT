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

package jgf_mpj_benchmarks.section2.crypt;

//package crypt;
import jgf_mpj_benchmarks.jgfutil.*; 
import mpi.*;

public class JGFCryptBench extends IDEATest implements JGFSection2{ 

  public static int nprocess;
  public static int rank;
  private int size; 
  private int datasizes[]={3000000,20000000,50000000};

  public JGFCryptBench(int nprocess, int rank) {
        this.nprocess=nprocess;
        this.rank=rank;
  }

  public void JGFsetsize(int size){
    this.size = size;
  }

  public void JGFinitialise(){
    array_rows = datasizes[size];

/* determine the array dimension size on each process
   p_array_rows will be smaller on process (nprocess-1).
   ref_p_array_rows is the size on all processes except process (nprocess-1),
   rem_p_array_rows is the size on process (nprocess-1).
*/

    p_array_rows = (((array_rows / 8) + nprocess -1) / nprocess)*8;
    ref_p_array_rows = p_array_rows;
    rem_p_array_rows = p_array_rows - ((p_array_rows*nprocess) - array_rows);
    if(rank==(nprocess-1)){
      if((p_array_rows*(rank+1)) > array_rows) {
        p_array_rows = rem_p_array_rows;
      }
    }

    buildTestData();
  }
 
  public void JGFkernel() throws MPIException{
    Do(); 
  }

  public void JGFvalidate(){
    boolean error;

    if(rank==0) {
      error = false; 
      for (int i = 0; i < array_rows; i++){
        error = (plain1 [i] != plain2 [i]); 
        if (error){
  	  System.out.println("Validation failed");
	  System.out.println("Original Byte " + i + " = " + plain1[i]); 
	  System.out.println("Encrypted Byte " + i + " = " + crypt1[i]); 
	  System.out.println("Decrypted Byte " + i + " = " + plain2[i]); 
	  //break;
        }
      }
    }
  }


  public void JGFtidyup(){
    freeTestData(); 
  }  



  public void JGFrun(int size) throws MPIException{

    if(rank==0){
      JGFInstrumentor.addTimer("Section2:Crypt:Kernel", "Kbyte",size);
    }

    JGFsetsize(size); 
    JGFinitialise(); 
    JGFkernel(); 
    JGFvalidate(); 
    JGFtidyup(); 

    if(rank==0){     
      JGFInstrumentor.addOpsToTimer("Section2:Crypt:Kernel", (2*array_rows)/1000.); 
      JGFInstrumentor.printTimer("Section2:Crypt:Kernel"); 
    }
  }
}
