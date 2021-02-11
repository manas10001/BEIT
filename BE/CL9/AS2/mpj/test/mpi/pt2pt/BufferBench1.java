package mpi.pt2pt;

import mpi.*;
import mpjbuf.*; 
import java.util.Arrays;

public class BufferBench1 {
  static public void main(String[] args) throws Exception {
	  try{
		BufferBench1 c = new BufferBench1(args);
	  }catch(Exception e){
	  }
  }
  public BufferBench1() {
  }
  
  public BufferBench1(String args[]) throws Exception{

    int REPEAT = 1000 , k=0; 
    long time[] = new long[REPEAT] ; 
    long cum_time; 
    long minimum = 0L; 
    long start = 0L;
    long end = 0L; 
    long maxMemory=0L, freeMemory=0L, totalMemory=0L;
    mpjbuf.RawBuffer buffer ; 
    int i=0,j=0; 
    Runtime rt = Runtime.getRuntime() ; 
    System.out.println("# size allocation_time used_mem "+
		    "total_memory max_memory");
    
    for(int u=0 ; u<100000 ; u++) {    
      for(j=128 ; j<=16*1024*1024 ; j*=2) {
        buffer = BufferFactory.create(j) ;
        BufferFactory.destroy( buffer ); 
      }
    }


    //try { Thread.currentThread().sleep(500); } catch(Exception e) {}
    
	    
    for(j=128 ; j<=16*1024*1024 ; j*=2) {

for(int q=0 ; q<10 ; q++) {
	
      cum_time = 0L;   	  
      
  for(k=0 ; k<REPEAT ; k++) { 
      start = System.nanoTime(); 	  
      buffer = BufferFactory.create(j) ;
      end = System.nanoTime(); 	  
      time[k] = end-start ; 
      //maxMemory = rt.maxMemory(); 
      //totalMemory = rt.totalMemory(); 
      //freeMemory = rt.freeMemory(); 
      //memory[k] = rt.totalMemory() - rt.freeMemory() ; 
      BufferFactory.destroy( buffer ); 
  }
  
  for(k=0 ; k<REPEAT ; k++) { 
     cum_time += time[k] ; 	  
     //cum_mem += memory[k] ; 
     //print time here ...
  }

  if(q == 0) {
    minimum = cum_time;     
    //min_mem = cum_mem ;
  } else {
    if(cum_time < minimum) {
      minimum = cum_time;  	    
      //min_mem = cum_mem ;
    }
  }

}  

  System.out.println(j+" "+
		  (minimum)/(1000.0*REPEAT)+" "); 
		  //(min_mem)/(1024*1024)+" "); 
		  //totalMemory/(1024*1024)+" "+
		  //maxMemory/(1024*1024)); 
     }

  }


  
}
