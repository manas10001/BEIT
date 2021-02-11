package mpi.dtyp;

import mpi.*;
import java.util.Arrays;

/**
 * This example application, is to check and demonstrat the functonality of
 * contiguous datatypes. I'm writing an MPJ program by coverting a C program by
 * Forest Hoffman at following URL
 * http://www.linux-mag.com/2003-04/extreme_01.html
 */

public class Contiguous {
  public static int NUM_PARAMS = 12;

  public static void main(String args[]) throws Exception {
    try {
      Contiguous c = new Contiguous(args);
    }
    catch (Exception e) {
    }
  }

  public Contiguous() {
  }

  public Contiguous(String[] args) throws Exception {

    MPI.Init(args);
    int i, rank, size;

    double[] params = new double[NUM_PARAMS];
    double[] rparams = new double[NUM_PARAMS];

    rank = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();
    Datatype parameterType;

    /* only the first process reads the parameters */
    if (rank == 0) {
      for (i = 0; i < NUM_PARAMS; i++) {
	params[i] = (double) (i * i);
      }
    }

    parameterType = Datatype.Contiguous(NUM_PARAMS, MPI.DOUBLE);
    parameterType.Commit(); // this is doing nothing at the moment.

    if (rank == 1) {
      MPI.COMM_WORLD.Recv(rparams, 0, 1, parameterType, 0, 10);
      System.out.println("recv completed");

      for (int j = 0; j < rparams.length; j++) {
	System.out.print("rparams[" + j + "]=" + rparams[j] + "\t");
      }

      /*
       * work out what would be test ? if (Arrays.equals(source,dest)) {
       * System.out.println("PASS"); }else { System.out.println("\n****");
       * System.out.println("FAIL"); System.out.println("****"); }
       */
    } else if (rank == 0) {
      MPI.COMM_WORLD.Send(params, 0, 1, parameterType, 1, 10);
      System.out.println("Send Completed");
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
