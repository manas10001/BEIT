package mpi.dtyp;

import mpi.*;
import java.util.Arrays;

/**
 * This example application, is to check and demonstrate the functonality of
 * contiguous datatypes. I'm writing an MPJ program by coverting a C program by
 * Forest Hoffman at following URL
 * http://www.linux-mag.com/2003-04/extreme_01.html
 * 
 */
public class Vector {
  public static int NUM_PARAMS = 1024;

  public static void main(String args[]) throws Exception {
    try {
      Vector c = new Vector(args);
    }
    catch (Exception e) {
    }
  }

  public Vector() {
  }

  public Vector(String[] args) throws Exception {

    MPI.Init(args);
    int i, rank, size;

    double[] data = new double[NUM_PARAMS];
    double[] rdata = new double[NUM_PARAMS];

    rank = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();
    Datatype parameterType;

    /* only the first process reads the parameters */
    if (rank == 0) {
      for (i = 0; i < NUM_PARAMS; i++) {
	data[i] = (double) (i * i);
      }
    }

    if (rank == 1) {

      MPI.COMM_WORLD.Recv(rdata, 0, 64, MPI.DOUBLE, 0, 10);
      System.out.println("recv completed");

      for (int j = 0; j < 10; j++) {
	System.out.print("rdata[" + j + "]=" + rdata[j] + "\t");
      }

      /*
       * work out what would be test ? if (Arrays.equals(source,dest)) {
       * System.out.println("PASS"); }else { System.out.println("\n****");
       * System.out.println("FAIL"); System.out.println("****"); }
       */
    } else if (rank == 0) {
      parameterType = Datatype.Vector(64, 1, 16, MPI.DOUBLE);
      parameterType.Commit();
      MPI.COMM_WORLD.Send(data, 0, 1, parameterType, 1, 10);
      System.out.println("Send Completed");
    }
    try {
      Thread.currentThread().sleep(1000);
    }
    catch (Exception e) {
    }
    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
