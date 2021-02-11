package mpi.signals; 

import java.io.*;

/**
 * Simple implementation of the class task
 * lesseee
 */
  
public class Job implements Serializable{

    /**
     * This value holds the input task.
     */
    private Task input;

    /** 
     * This values holds the result of the input task evaluation.
     */
    private Result output;

    /**
     * A Job is instantiated by specifying the input task, expected result 
     * and their fully qualified class names.
     */
    public Job(Task task_input, Result result) {	
	input = task_input;
	output = result;
    }

    /**
     * returns the input task
     */
    public Task getInput() {
	return input;
    }

    /**
     * returns the result of the evaluation. This method blocks if the 
     * evaluation is not complete.
     */
    public  Object getOutput() {
	return output;
    }

    /**
     * sets the result of the evaluation 
     */ 
    public void setOutput(Result job_output) {
	output = job_output;
    }

    /**
     * Returns a string representation of the job
     */
    public String toString() {	
	//StringBuffer strbuf = new StringBuffer();
	//return strbuf.toString();
    
	return "Input is : " + input + "\n Output is : " + output +"\n";
    }
    
    /**
     * For garbage collection
     */
    protected void finalize() throws Throwable {
	input = null;
	output = null;
	//server = null;
	super.finalize();
    }
}










