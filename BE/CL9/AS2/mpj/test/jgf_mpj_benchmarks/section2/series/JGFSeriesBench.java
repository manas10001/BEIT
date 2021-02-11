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

package jgf_mpj_benchmarks.section2.series;

//package series;
import jgf_mpj_benchmarks.jgfutil.*; 
import mpi.*;

public class JGFSeriesBench extends SeriesTest implements JGFSection2{ 

  public static int nprocess;
  public static int rank;
  private int size; 
  private int datasizes[]={10000,100000,1000000};

  public JGFSeriesBench(int nprocess, int rank) {
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

    p_array_rows = (array_rows + nprocess -1) / nprocess;
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
     double ref[][] = {{2.8729524964837996, 0.0},
                       {1.1161046676147888, -1.8819691893398025},
                       {0.34429060398168704, -1.1645642623320958},
                       {0.15238898702519288, -0.8143461113044298}};
/* 
// for 200 points 
    double ref[][] = {{2.8377707562588803, 0.0},
		       {1.0457844730995536, -1.8791032618587762},
		       {0.27410022422635033, -1.158835123403027},
		       {0.08241482176581083, -0.8057591902785817}};
*/ 
 
   if(rank==0) { 
    for (int i = 0; i < 4; i++){
      for (int j = 0; j < 2; j++){
	double error = Math.abs(TestArray[j][i] - ref[i][j]);
	if (error > 1.0e-12 ){
	  System.out.println("Validation failed for coefficient " + j + "," + i);
	  System.out.println("Computed value = " + TestArray[j][i]);
	  System.out.println("Reference value = " + ref[i][j]);
	}
      }
    }
   }
  }

  public void JGFtidyup(){
    freeTestData(); 
  }  



  public void JGFrun(int size) throws MPIException{

    if(rank==0){
      JGFInstrumentor.addTimer("Section2:Series:Kernel", "coefficients",size);
    }
    JGFsetsize(size); 
    JGFinitialise(); 
    JGFkernel(); 
    JGFvalidate(); 
    JGFtidyup(); 

    if(rank==0){ 
      JGFInstrumentor.addOpsToTimer("Section2:Series:Kernel", (double) (array_rows * 2 - 1));
      JGFInstrumentor.printTimer("Section2:Series:Kernel"); 
    }
  }
}
