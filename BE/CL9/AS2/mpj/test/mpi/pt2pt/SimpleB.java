package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class SimpleB {
  public SimpleB(String args[]) throws Exception {
    Request sreq = null;
    mpi.Status status = null;
    MPI.Init(args);

    int intArray[] = new int[100];
    int intReadArray[] = new int[100];

    for (int i = 0; i < intArray.length; i++) {
      intArray[i] = i + 1;
      intReadArray[i] = 3;
    }

    if (MPI.COMM_WORLD.Rank() == 0) {

      MPI.COMM_WORLD.Send(intArray, 0, 100, MPI.INT, 1, 999);
      // System.out.println("isend icopmpleted<"+status.source+
      // ">,tag ==<"+status.tag+">");

    } else if (MPI.COMM_WORLD.Rank() == 1) {
      MPI.COMM_WORLD.Recv(intReadArray, 0, 100, MPI.INT, 0, 999);
      // System.out.println("irecv completed<"+status.source+
      // ">,tag ==<"+status.tag+">");

      if (Arrays.equals(intArray, intReadArray)) {
	// System.out.println("\n#################"+
	// "\n <<<<PASSED>>>> "+
	// "\n################");
      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }

    }

    MPI.Finalize();
  }

  public static void main(String args[]) throws Exception {
    SimpleB test = new SimpleB(args);
  }
}
