package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class intertwined {
  public intertwined() {
  }

  public intertwined(String args[]) throws Exception {

    MPI.Init(args);

    int me = MPI.COMM_WORLD.Rank();

    int intArray[] = new int[100];
    float floatArray[] = new float[100];

    int intReadArray[] = new int[100];
    float floatReadArray[] = new float[100];

    for (int i = 0; i < intArray.length; i++) {
      intArray[i] = i + 1;
      floatArray[i] = i + 11;

      intReadArray[i] = 3;
      floatReadArray[i] = i + 19;
    }

    if (MPI.COMM_WORLD.Rank() == 0) {
      Request req = MPI.COMM_WORLD.Isend(intArray, 0, 100, MPI.INT, 1, 999);
      MPI.COMM_WORLD.Ssend(floatArray, 0, 100, MPI.FLOAT, 1, 992);
      req.Wait();
    } else if (MPI.COMM_WORLD.Rank() == 1) {
      // try { Thread.currentThread().sleep(1000); }catch(Exception e){}
      MPI.COMM_WORLD.Recv(floatReadArray, 0, 100, MPI.FLOAT, 0, 992);
      MPI.COMM_WORLD.Recv(intReadArray, 0, 100, MPI.INT, 0, 999);

      if (Arrays.equals(intArray, intReadArray)
	  && Arrays.equals(floatArray, floatReadArray)) {

	System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	    + "\n################");

      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }
    }

    MPI.COMM_WORLD.Barrier();

    if (MPI.COMM_WORLD.Rank() == 0) {
      System.out.println("Intertwined TEST Completed");
    }

    MPI.Finalize();

  }

  public static void main(String args[]) throws Exception {
    intertwined test = new intertwined(args);
  }
}
