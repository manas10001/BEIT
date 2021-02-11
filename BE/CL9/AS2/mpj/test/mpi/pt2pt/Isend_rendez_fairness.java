package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class Isend_rendez_fairness {
  int DATA_SIZE = 32 * 1024 * 1024;

  public Isend_rendez_fairness() {
  }

  public Isend_rendez_fairness(String args[]) throws Exception {

    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();

    int intArray1[] = new int[DATA_SIZE];
    int intArray2[] = new int[DATA_SIZE];

    for (int i = 0; i < DATA_SIZE; i++) {
      if (me != 0) {
	intArray1[i] = -24;
	intArray2[i] = -80;
      }
    }

    if (me == 0) {

      Request req2 = MPI.COMM_WORLD.Irecv(intArray2, 0, DATA_SIZE, MPI.INT, 2,
	  992);
      Request req1 = MPI.COMM_WORLD.Irecv(intArray1, 0, DATA_SIZE, MPI.INT, 1,
	  991);
      req1.Wait();
      req2.Wait();

      for (int j = 0; j < DATA_SIZE; j++) {
	if (intArray1[j] != 3 || intArray2[j] != 4) {
	  System.out.println(" FAILED");
	  break;
	}
      }

      System.out.println(" Process O Exiting");

    } else if (me == 1) {
      MPI.COMM_WORLD.Send(intArray1, 0, DATA_SIZE, MPI.INT, 0, 991);
    } else if (me == 2) {
      MPI.COMM_WORLD.Send(intArray2, 0, DATA_SIZE, MPI.INT, 0, 992);
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }

  public static void main(String args[]) throws Exception {
    Isend_rendez_fairness test = new Isend_rendez_fairness(args);
  }
}
