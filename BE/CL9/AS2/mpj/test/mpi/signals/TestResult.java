package mpi.signals;

//TestResult.java 
import java.io.*; 
import mpi.*;

public class TestResult implements Result,Serializable{ 

    public int output;
 
    public TestResult(){ 
	this.output = -1; 
    } 
    public TestResult(int d){ 
	this.output = d; 
    } 

    public String toString(){ 
	return "Ack : proc_rank "+output+" has done the job"; 
    } 
} 







