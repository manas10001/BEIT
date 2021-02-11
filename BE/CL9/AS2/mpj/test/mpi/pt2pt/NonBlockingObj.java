import mpi.*;
import java.util.Arrays;

public class NonBlockingObj {
  public NonBlockingObj(String args[]) throws Exception {

    Request req;
    MPI.Init(args);

    java.util.Vector vector1 = null;
    java.util.Vector vector = new java.util.Vector();

    for (int i = 0; i < 10; i++) {
      vector.add(i + "");
    }

    Object[] source = new Object[5];
    source[0] = vector;
    source[1] = vector;
    source[2] = vector;
    source[3] = vector;
    source[4] = vector;

    Object[] dest = new Object[5];
    dest[0] = null;
    dest[1] = null;
    dest[2] = null;
    dest[3] = null;
    dest[4] = null;

    if (MPI.COMM_WORLD.Rank() == 0) {
      req = MPI.COMM_WORLD.Isend(source, 0, 5, MPI.OBJECT, 1, 999);
      System.out.println("req " + req);
      req.Wait();
      System.out.println("Isend Completed \n\n");
    }

    else if (MPI.COMM_WORLD.Rank() == 1) {

      req = MPI.COMM_WORLD.Irecv(dest, 0, 5, MPI.OBJECT, 0, 999);
      req.Wait();

      if (Arrays.equals(source, dest)) {
	System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	    + "\n################");
      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }

  public static void main(String args[]) throws Exception {
    NonBlockingObj test = new NonBlockingObj(args);
  }
}
