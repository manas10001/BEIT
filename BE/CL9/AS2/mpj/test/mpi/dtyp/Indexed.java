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
public class Indexed {

  public static void main(String args[]) throws Exception {
    try {
      Indexed c = new Indexed(args);
    }
    catch (Exception e) {
    }
  }

  public Indexed() {
  }

  public Indexed(String[] args) throws Exception {
    int DIM = 8;
    MPI.Init(args);
    int i, j, k, rank, size, dest, src, tag;

    int[] blklen = new int[DIM];
    int[] displ = new int[DIM];
    double[] data = new double[(DIM * DIM)];
    rank = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();
    Datatype parameterType;

    /* only the first process reads the parameters */
    for (i = 0; i < DIM; i++) {
      blklen[i] = DIM - i;
      displ[i] = (i * DIM) + i;
    }
    src = 0;
    dest = 1;
    tag = 10;
    Datatype indexedDT = Datatype.Indexed(blklen, displ, MPI.DOUBLE);
    indexedDT.Commit();

    if (rank == 1) {

      MPI.COMM_WORLD.Recv(data, 0, 1, indexedDT, src, tag);
      System.out.println("recv completed");

      for (j = 0; j < 64; j++) {
	System.out.print("data[" + j + "]=" + data[j] + " ");
      }

      /*
       * work out what would be test ? if (Arrays.equals(source,dest)) {
       * System.out.println("PASS"); }else { System.out.println("\n****");
       * System.out.println("FAIL"); System.out.println("****"); }
       */
    } else if (rank == 0) {
      for (i = 0; i < (DIM * DIM); i++)
	data[i] = (double) (i + 1);

      MPI.COMM_WORLD.Send(data, 0, 1, indexedDT, dest, tag);
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
