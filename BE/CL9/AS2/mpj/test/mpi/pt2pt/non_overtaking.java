package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class non_overtaking {
  public non_overtaking() {
  }

  public non_overtaking(String args[]) throws Exception {

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
      MPI.COMM_WORLD.Ssend(intArray, 0, 100, MPI.INT, 1, 999);
      MPI.COMM_WORLD.Ssend(floatArray, 0, 100, MPI.FLOAT, 1, 992);
    } else if (MPI.COMM_WORLD.Rank() == 1) {
      // try { Thread.currentThread().sleep(1000); }catch(Exception e){}
      MPI.COMM_WORLD.Recv(intReadArray, 0, 100, MPI.INT, 0, MPI.ANY_TAG);
      MPI.COMM_WORLD.Recv(floatReadArray, 0, 100, MPI.FLOAT, 0, 992);

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
      System.out.println("non_overtaking TEST Completed");
    }

    MPI.Finalize();

  }

  public static void main(String args[]) throws Exception {
    non_overtaking test = new non_overtaking(args);
  }
}
