package mpi.comm;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class CommTest {

  public static void main(String args[]) throws Exception {
    try {
      CommTest a = new CommTest(args);
    }
    catch (Exception e) {
    }
  }

  public CommTest() {
  }

  public CommTest(String[] args) throws Exception {
    int DATA_SIZE = 100;
    MPI.Init(args);
    int size = MPI.COMM_WORLD.Size();
    int rank = MPI.COMM_WORLD.Rank();

    if (size < 8) {
      if (rank == 0)
	System.out.println("comm->CommTest runs with eight processes");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    /* Creating the new communicator */
    byte byteArray[] = new byte[DATA_SIZE];

    for (int i = 0; i < DATA_SIZE; i++) {
      byteArray[i] = 's';
    }

    int[] newIds = { 2, 3, 4 };
    Intracomm newComm = MPI.COMM_WORLD.Create((MPI.COMM_WORLD.Group())
	.Incl(newIds));
    int[] newIds1 = { 5, 6, 7 };
    Intracomm newComm1 = MPI.COMM_WORLD.Create((MPI.COMM_WORLD.Group())
	.Incl(newIds1));

    int[] newIds2 = { 0, 1 };
    Intracomm newComm2 = MPI.COMM_WORLD.Create((MPI.COMM_WORLD.Group())
	.Incl(newIds2));

    int[] newIds3 = { 0, 2, 4, 6 };
    Intracomm newComm3 = MPI.COMM_WORLD.Create((MPI.COMM_WORLD.Group())
	.Incl(newIds3));

    if (MPI.COMM_WORLD.Rank() == 0 && newComm3.Rank() == 0) {
      newComm3.Send(byteArray, 0, DATA_SIZE, MPI.BYTE, 1, 90);
    } else if (MPI.COMM_WORLD.Rank() == 2 && newComm3.Rank() == 1) {

      byte byteReadArray[] = new byte[DATA_SIZE];

      for (int i = 0; i < DATA_SIZE; i++) {
	byteReadArray[i] = 'x';
      }

      // System.out.println("Receving bytes");
      newComm3.Recv(byteReadArray, 0, DATA_SIZE, MPI.BYTE, 0, 90);

      if (Arrays.equals(byteArray, byteReadArray)) {
	/*
	 * System.out.println("\n#################" + "\n <<<<PASSED>>>> " +
	 * "\n################");
	 */
      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
	// System.exit(0);
      }

    } // end while(true)

    MPI.COMM_WORLD.Barrier();

    if (rank == 0)
      System.out.println("CommTest TEST COMPLETE");

    MPI.Finalize();

  }
}
