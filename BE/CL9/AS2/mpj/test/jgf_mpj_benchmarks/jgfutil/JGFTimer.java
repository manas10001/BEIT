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

public class JGFTimer {

  public String name; 
  public String opname; 
  public double time; 
  public double opcount; 
  public long calls; 
  public int size = -1;
  
  private long start_time;
  private boolean on; 

  public JGFTimer(String name, String opname){
    this.name = name;
    this.opname = opname;
    reset(); 
  }

  public JGFTimer(String name, String opname, int size){
    this.name = name;
    this.opname = opname;
    this.size = size;
    reset();
  }

  public JGFTimer(String name){
    this(name,""); 
  }



  public void start(){
    if (on) System.out.println("Warning timer " + name + " was already turned on");
    on = true; 
    start_time = System.currentTimeMillis();
  }


  public void stop(){
    time += (double) (System.currentTimeMillis()-start_time) / 1000.;
    if (!on) System.out.println("Warning timer " + name + " wasn't turned on");
    calls++;
    on = false;  
  }

  public void addops(double count){
    opcount += count;
  } 

  public void addtime(double added_time){
    time += added_time;
  }

  public void reset(){
    time = 0.0; 
    calls = 0; 
    opcount = 0; 
    on = false;
  }

  public double perf(){
    return opcount / time; 
  }

  public void longprint(){
      System.out.println("Timer            Calls         Time(s)       Performance("+opname+"/s)");   
     System.out.println(name + "           " + calls +    "           "  +  time + "        " + this.perf());
  }

  public void print(){
    if (opname.equals("")) {
      System.out.println(name + "   " + time + " (s)");
    }
    else {

      switch(size) {
      case 0:
      System.out.println(name + ":SizeA" + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      case 1:
      System.out.println(name + ":SizeB" + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      case 2:
      System.out.println(name + ":SizeC" + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      default:
      System.out.println(name + "\t" + time + " (s) \t " + (float)this.perf() + "\t"
                        + " ("+opname+"/s)");
      break;
      }

    }
  }


  public void printperf(){

     String name;
     name = this.name; 

     // pad name to 40 characters
     while ( name.length() < 40 ) name = name + " "; 
     
     System.out.println(name + "\t" + (float)this.perf() + "\t"
			+ " ("+opname+"/s)");  
  }

  public void printperf(int arr_size){

     String name;
     name = this.name;

     // pad name to 40 characters
     while ( name.length() < 40 ) name = name + " ";
    
     System.out.println(name + "\t" + (float)this.perf() + "\t"
                        + " ("+opname+"/s)" + "\t" + " Array Size = " + arr_size);
  }

}
