package mpi.signals;

import mpi.*;
import java.io.*;

public class TestTask implements Task,Serializable{
  
    String pool_proc ; 
    String ack = null;
    //MyLinkList mll ;

    public TestTask(){ 
	this.pool_proc="un-initialized"; 
	//mll = new MyLinkList(1,this.pool_proc);
    } 

    public TestTask(String s){ 
	this.pool_proc = s; 
	//mll = new MyLinkList(1,this.pool_proc);
    } 

    public Result runTask() { 
	System.out.println("***********************************");
	System.out.println("I come from " + this.pool_proc );
	
	int my_pe = 0;

	try{
	    System.out.println("I am right now on "+MPI.Get_processor_name());
	    my_pe = MPI.COMM_WORLD.Rank();
	}catch(Exception e){
	    System.err.println("Error in TestTask.runTask() " + e);
	}

	System.out.println("***********************************");	
	// returning the name of the processor this task ran on..
	return new TestResult(my_pe);
    } 

    public void setAck(String s){
	this.ack = "recieved ack by "+s ;
    }

    public void setResult(Result output){ 
	
    } 
    
    public String toString(){
	return "Task sent from "+ pool_proc + " ack : " + ack ;
    }

}





