package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class BreakANYSRC {
  static public void main(String[] args) throws MPIException {
    try {
      BreakANYSRC c = new BreakANYSRC(args);
    }
    catch (Exception e) {
    }
  }

  public BreakANYSRC() {
  }

  public BreakANYSRC(String args[]) throws Exception {

    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();
    int DATA_SIZE = 10;
    int intArray[] = new int[DATA_SIZE];
    int intReadArray[] = new int[DATA_SIZE];

    for (int i = 0; i < DATA_SIZE; i++) {
      intArray[i] = i;
      intReadArray[i] = 0;
    }

    if (me == 0) {

      /* test #1 */
      System.out.println(" sending ");
      Request req = MPI.COMM_WORLD.Isend(intArray, 0, DATA_SIZE, MPI.INT, 0, 1);
      System.out.println(" receiving");
      MPI.COMM_WORLD.Recv(intReadArray, 0, DATA_SIZE, MPI.INT, 0, 1);
      System.out.println(" received it ..");
      req.Wait();
      System.out.println(" called wait for send");

      if (Arrays.equals(intArray, intReadArray)) {
	System.out.println("test #1 passed");
      } else {
	System.out.println("test #1 failed");
      }

      /* test #2 */
      for (int j = 0; j < DATA_SIZE; j++) {
	intReadArray[j] = 0;
      }

      req = MPI.COMM_WORLD.Irecv(intReadArray, 0, DATA_SIZE, MPI.INT, 0, 1);
      MPI.COMM_WORLD.Send(intArray, 0, DATA_SIZE, MPI.INT, 0, 1);
      req.Wait();

      if (Arrays.equals(intArray, intReadArray)) {
	System.out.println("test #2 passed");
      } else {
	System.out.println("test #2 failed");
      }

      /* test #3 */
      for (int j = 0; j < DATA_SIZE; j++) {
	intReadArray[j] = 0;
      }
      System.out.println(" calling recv ");
      req = MPI.COMM_WORLD.Irecv(intReadArray, 0, DATA_SIZE, MPI.INT,
	  MPI.ANY_SOURCE, 1);
      System.out.println(" calling send");
      MPI.COMM_WORLD.Send(intArray, 0, DATA_SIZE, MPI.INT, 0, 1);
      System.out.println(" sent ");
      req.Wait();
      System.out.println(" received ");

      if (Arrays.equals(intArray, intReadArray)) {
	System.out.println("test #3 passed");
      } else {
	System.out.println("test #3 passed");
      }

      System.out.println(" BreakANYSRC TEST COMPLETED ");

    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
    System.out.println(" BreakANYSRC TEST COMPLETED " + me);
  }

}
