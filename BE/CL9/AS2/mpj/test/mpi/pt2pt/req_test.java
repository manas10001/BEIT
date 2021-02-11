import mpi.*;
import java.util.Arrays;

public class req_test {

  public static void main(String args[]) throws Exception {

    MPI.Init(args);
    int intArray[] = new int[100];
    int intReadArray[] = new int[100];
    mpi.Request req = null;
    mpi.Status status = null;

    for (int i = 0; i < intArray.length; i++) {
      intArray[i] = i + 1;
      intReadArray[i] = 3;
    }

    int rank = MPI.COMM_WORLD.Rank();

    /*
     * The next twenty seven lines of code actually are testing Wait() and then
     * checkout if Status object really contains something ok, ciao
     */
    if (rank == 0) {
      req = MPI.COMM_WORLD.Isend(intArray, 0, 100, MPI.INT, 1, 999);
      status = req.Wait();
      System.out.println("Send Completed \n\n");
      System.out.println("(s)status.index " + status.index);
      System.out.println("(s)status.source " + status.source);
      System.out.println("(s)status.index " + status.tag);
    } else if (rank == 1) {
      try {
	Thread.currentThread().sleep(1000);
      }
      catch (Exception e) {
      }
      System.out.println(" ** Recv calling ** ");
      req = MPI.COMM_WORLD.Irecv(intReadArray, 0, 100, MPI.INT, 0, 999);
      status = req.Wait();
      System.out.println(" ** Recv completed ** ");

      if (Arrays.equals(intArray, intReadArray)) {
	System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	    + "\n################");
      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }
      System.out.println("(r)status.index " + status.index);
      System.out.println("(r)status.source " + status.source);
      System.out.println("(r)status.index " + status.tag);
    }

    /*
     * The next twenty seven lines of code actually are testing Wait() and then
     * checkout if Status object really contains something ok, ciao
     */

    if (rank == 0) {
      req = MPI.COMM_WORLD.Isend(intArray, 0, 100, MPI.INT, 1, 999);
      status = req.Wait();
      System.out.println("Send Completed \n\n");
      System.out.println("(s)status.index " + status.index);
      System.out.println("(s)status.source " + status.source);
      System.out.println("(s)status.index " + status.tag);
    } else if (rank == 1) {
      try {
	Thread.currentThread().sleep(1000);
      }
      catch (Exception e) {
      }
      System.out.println(" ** Recv calling ** ");
      req = MPI.COMM_WORLD.Irecv(intReadArray, 0, 100, MPI.INT, 0, 999);
      status = req.Wait();
      System.out.println(" ** Recv completed ** ");

      if (Arrays.equals(intArray, intReadArray)) {
	System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	    + "\n################");
      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }
      System.out.println("(r)status.index " + status.index);
      System.out.println("(r)status.source " + status.source);
      System.out.println("(r)status.index " + status.tag);
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
