package mpi.comm;

import mpi.*;
import java.util.Arrays;

/*
 * things i have done today, 
 * 1. Intracomm class, createGroup method. Intracomm constructor. 
 * created this comm_test class
 * 2. was trying to make this stupid collective 
 * operations work. as their 
 * and the backup should be 20th Jan and get aamirmpj, 
 * the whole code now .... 
 */

public class comm_test {
  /* this test runs with eight processors at the moment. */
  public static void main(String args[]) throws Exception {
    try {
      comm_test a = new comm_test(args);
    }
    catch (Exception e) {
    }
  }

  public comm_test() {
  }

  public comm_test(String[] args) throws Exception {

    MPI.Init(args);
    Group grp1 = MPI.COMM_WORLD.Group();
    Comm myComm;
    int[] incl1 = { 0, 2, 4, 6 };
    int rank = MPI.COMM_WORLD.Rank();
    int size = MPI.COMM_WORLD.Size();

    if (size < 8) {
      if (rank == 0)
	System.out.println("comm->comm_test: RUNS WITH 8 processes atleast");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    Group grp2 = grp1.Incl(incl1);
    // System.out.println("world<"+rank+">,newComm<"+grp2.Rank()+">");

    /*
     * i've to write this if statement because group methods return null at the
     * moment, which results in nullpointer exception. this has to be fixed by
     * returning EMPTY and other relevant flags
     */

    // if( rank== 0 || rank==2|| rank==4 || rank==6 ) {
    // System.out.println("rank "+rank+" is within");
    myComm = MPI.COMM_WORLD.Create(grp2);
    // }
    // if(myComm == null){
    // System.out.println("rank "+rank+" myComm rank = "+myComm.Rank());
    // }
    MPI.COMM_WORLD.Barrier();
    if (rank == 0)
      System.out.println("comm_test TEST COMPLETE");
    MPI.Finalize();
  }
}
