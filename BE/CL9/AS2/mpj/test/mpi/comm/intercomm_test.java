package mpi.comm;

import mpi.*;
import java.util.Arrays;

public class intercomm_test {

  public static void main(String args[]) throws Exception {
    try {
      intercomm_test a = new intercomm_test(args);
    }
    catch (Exception e) {
    }
  }

  public intercomm_test() {
  }

  public intercomm_test(String[] args) throws Exception {

    MPI.Init(args);
    int rank = MPI.COMM_WORLD.Rank();
    Group grp = MPI.COMM_WORLD.Group();
    int size = MPI.COMM_WORLD.Size();

    if (size < 8) {
      if (rank == 0)
	System.out.println("comm->intercomm_test: RUNS with 8 processes");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    /* creating first intra-communicator */
    Intracomm comm1 = null;
    int[] incl1 = { 0, 2, 4, 6 };

    if (rank == 0 || rank == 2 || rank == 4 || rank == 6) {
      Group grp1 = grp.Incl(incl1);
      comm1 = MPI.COMM_WORLD.Create(grp1);
    }

    /* creating first intra-communicator */
    Intracomm comm2 = null;
    int[] incl2 = { 1, 3, 5, 7 };

    if (rank == 1 || rank == 3 || rank == 5 || rank == 7) {
      Group grp2 = grp.Incl(incl2);
      comm2 = MPI.COMM_WORLD.Create(grp2);
    }

    Intercomm icomm = null;

    if (rank == 0 || rank == 2 || rank == 4 || rank == 6) {
      icomm = MPI.COMM_WORLD.Create_intercomm(comm1, 0, 1, 56);
    }

    if (rank == 1 || rank == 3 || rank == 5 || rank == 7) {
      icomm = MPI.COMM_WORLD.Create_intercomm(comm2, 1, 0, 56);
    }

    int[] testArray1 = new int[10];
    int[] testArray2 = new int[10];

    for (int i = 0; i < testArray1.length; i++) {
      testArray1[i] = i;
      testArray2[i] = 0;
    }

    // System.out.println("initialized them ...");
    if (rank == 2) {
      icomm.Send(testArray1, 0, 10, MPI.INT, 2, 78);
      MPI.COMM_WORLD.Send(testArray1, 0, 10, MPI.INT, 5, 78);
    } else if (rank == 5) {
      icomm.Recv(testArray2, 0, 10, MPI.INT, 1, 78);
      if (Arrays.equals(testArray1, testArray2)) {
	/*
	 * System.out.println("****************"+ "***PASSED*******"+
	 * "****************");
	 */
      }

    }

    MPI.COMM_WORLD.Barrier();

    if (rank == 5) {
      System.out.println("intercomm_test TEST COMPLETE");
    }

    MPI.Finalize();
  }

}
