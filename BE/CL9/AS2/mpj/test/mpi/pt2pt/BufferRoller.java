package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class BufferRoller {
  static public void main(String[] args) throws Exception {
    try {
      BufferRoller c = new BufferRoller(args);
    }
    catch (Exception e) {
    }
  }

  public BufferRoller() {
  }

  public BufferRoller(String args[]) throws Exception {

    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();
    int iterations = 10;
    int dsize = 2000;
    int sendArray[] = new int[dsize];
    int recvArray[] = new int[dsize];

    if (MPI.COMM_WORLD.Rank() == 1)
      Thread.currentThread().sleep(10000);

    for (int j = 0; j < iterations; j++) {

      if (MPI.COMM_WORLD.Rank() == 0) {

	for (int i = 0; i < sendArray.length; i++) {
	  sendArray[i] = (iterations * j) + j;
	}

	MPI.COMM_WORLD.Send(sendArray, 0, dsize, MPI.INT, 1, j);

      } else if (MPI.COMM_WORLD.Rank() == 1) {

	for (int i = 0; i < recvArray.length; i++) {
	  recvArray[i] = 0;
	  sendArray[i] = (iterations * j) + j;
	}

	MPI.COMM_WORLD.Recv(recvArray, 0, dsize, MPI.INT, 0, j);

	if (Arrays.equals(sendArray, recvArray)) {
	  System.out.println(" <<<<PASSED>>>> ");
	} else {
	  System.out.println(" <<<<FAILED>>>> ");
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
