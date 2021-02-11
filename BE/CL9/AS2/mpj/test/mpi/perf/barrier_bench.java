package mpi.perf; 
import mpi.*;

class barrier_bench {
  static public void main(String[] args) throws MPIException {

    final int MAXLEN = 1024;
    int root,i,j=MAXLEN,k;
    //byte out[] = new byte[MAXLEN];
    int REPEAT  = 1000;
    int WARM_UP = 10;
    int myself,tasks;
    double time;

/*
    for(i=0;i<j;i++) {
        out[i] = 's' ; //(byte)i+j;
    }
*/
    
    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank(); 
    tasks = MPI.COMM_WORLD.Size();        
    root = 0;
    long startTime=0l, endTime=0l;
    
    for(j=0;j<WARM_UP;j++)  {           
      MPI.COMM_WORLD.Barrier();
    }
    
    //startTime = MPI.Wtime();
    
    for(j=0;j<REPEAT ;j++)  {           
      MPI.COMM_WORLD.Barrier();
    }

    //endTime = MPI.Wtime();
    if(myself == tasks-1) {
 //   System.out.println(tasks+"     "+
//		    ((endTime - startTime)/(1000*REPEAT)) );
    }
    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();   
  }
}
