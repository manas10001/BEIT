package mpi.signals; 

import java.io.*;
import mpi.*;

public interface Task /*implements Serializable*/ {
    /**
     * Specify how the input task is to be executed. The result of the 
     * evaluation should always return an object of type {@Result}.
     */
    public Result runTask();
    /**
     * ????
     */
    public void setResult(Result output);
}
