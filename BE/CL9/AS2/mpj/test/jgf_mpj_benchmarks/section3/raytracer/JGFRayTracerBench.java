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
package jgf_mpj_benchmarks.section3.raytracer;


//package raytracer; 

import jgf_mpj_benchmarks.jgfutil.*; 
import mpi.*;

public class JGFRayTracerBench extends RayTracer implements JGFSection3 {

  public static int nprocess;
  public static int rank;

  public JGFRayTracerBench(int nprocess, int rank) {
        this.nprocess=nprocess;
        this.rank=rank;
  }


  public void JGFsetsize(int size){
    this.size = size;
  }

  public void JGFinitialise() throws MPIException{

    if(rank==0) {
      JGFInstrumentor.startTimer("Section3:RayTracer:Init"); 
    }

    // set image size 
    width = height = datasizes[size]; 

    // create the objects to be rendered 
    scene = createScene();

    // get lights, objects etc. from scene. 
    setScene(scene); 

    numobjects = scene.getObjects();

    if(rank==0) {
      JGFInstrumentor.stopTimer("Section3:RayTracer:Init"); 
    }

  }

  public void JGFapplication() throws MPIException{ 

    if(rank==0){
      JGFInstrumentor.startTimer("Section3:RayTracer:Run");  
    }

    // Set interval to be rendered to the whole picture 
    // (overkill, but will be useful to retain this for parallel versions)
    Interval interval = new Interval(0,width,height,0,height,1); 

    // Do the business!
    render(interval); 

    if(rank==0) {
      JGFInstrumentor.stopTimer("Section3:RayTracer:Run");  
    }

  } 


  public void JGFvalidate(){
    long refval[] = {2676692,29827635};
    long dev = checksum - refval[size]; 
    if (dev != 0 ){
      System.out.println("Validation failed"); 
      System.out.println("Pixel checksum = " + checksum);
      System.out.println("Reference value = " + refval[size]); 
    }
  }

  public void JGFtidyup(){    
    scene = null;  
    lights = null;  
    prim = null;  
    tRay = null;  
    inter = null;  

    System.gc(); 
  }


  public void JGFrun(int size) throws MPIException{

    if(rank==0) {
      JGFInstrumentor.addTimer("Section3:RayTracer:Total", "Solutions",size);
      JGFInstrumentor.addTimer("Section3:RayTracer:Init", "Objects",size);
      JGFInstrumentor.addTimer("Section3:RayTracer:Run", "Pixels",size);
    }

    JGFsetsize(size); 

    if(rank==0){
      JGFInstrumentor.startTimer("Section3:RayTracer:Total");
    }

    JGFinitialise(); 
    JGFapplication(); 
  
    if(rank==0){
      JGFvalidate(); 
    }

    JGFtidyup(); 

    if(rank==0) {
      JGFInstrumentor.stopTimer("Section3:RayTracer:Total");

      JGFInstrumentor.addOpsToTimer("Section3:RayTracer:Init", (double) numobjects);
      JGFInstrumentor.addOpsToTimer("Section3:RayTracer:Run", (double) (width*height));
      JGFInstrumentor.addOpsToTimer("Section3:RayTracer:Total", 1);

      JGFInstrumentor.printTimer("Section3:RayTracer:Init"); 
      JGFInstrumentor.printTimer("Section3:RayTracer:Run"); 
      JGFInstrumentor.printTimer("Section3:RayTracer:Total"); 
    }
  }


}
 
