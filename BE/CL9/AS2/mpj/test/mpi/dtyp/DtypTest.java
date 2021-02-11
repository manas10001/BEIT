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
public class DtypTest {

  public static void main(String args[]) throws Exception {
    try {
      DtypTest c = new DtypTest(args);
    }
    catch (Exception e) {
    }
  }

  public DtypTest() {
  }

  public DtypTest(String[] args) throws Exception {
    MPI.Init(args);
    int i, j, k, rank, size, dest, src, tag;
    rank = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();
    src = 0;
    dest = 1;
    tag = 10;

    if (rank == 1) {

    } else if (rank == 0) {
      System.out.println(" Checking out, extent, lb, ub of basic datatypes ");
      System.out.println(" Byte***** ");
      System.out.println(" ext " + MPI.BYTE.Extent());
      System.out.println(" lb  " + MPI.BYTE.Lb());
      System.out.println(" ub  " + MPI.BYTE.Ub());
      System.out.println(" Boolean***** ");
      System.out.println(" ext " + MPI.BOOLEAN.Extent());
      System.out.println(" lb  " + MPI.BOOLEAN.Lb());
      System.out.println(" ub  " + MPI.BOOLEAN.Ub());
      System.out.println(" char***** ");
      System.out.println(" ext " + MPI.CHAR.Extent());
      System.out.println(" lb  " + MPI.CHAR.Lb());
      System.out.println(" ub  " + MPI.CHAR.Ub());
      System.out.println(" short***** ");
      System.out.println(" ext " + MPI.SHORT.Extent());
      System.out.println(" lb  " + MPI.SHORT.Lb());
      System.out.println(" ub  " + MPI.SHORT.Ub());
      System.out.println(" int ***** ");
      System.out.println(" ext " + MPI.INT.Extent());
      System.out.println(" lb  " + MPI.INT.Lb());
      System.out.println(" ub  " + MPI.INT.Ub());
      System.out.println(" float***** ");
      System.out.println(" ext " + MPI.FLOAT.Extent());
      System.out.println(" lb  " + MPI.FLOAT.Lb());
      System.out.println(" ub  " + MPI.FLOAT.Ub());
      System.out.println(" long***** ");
      System.out.println(" ext " + MPI.LONG.Extent());
      System.out.println(" lb  " + MPI.LONG.Lb());
      System.out.println(" ub  " + MPI.LONG.Ub());
      System.out.println(" double***** ");
      System.out.println(" ext " + MPI.DOUBLE.Extent());
      System.out.println(" lb  " + MPI.DOUBLE.Lb());
      System.out.println(" ub  " + MPI.DOUBLE.Ub());
      System.out.println(" Upper Bound (UB)***** ");
      System.out.println(" ext " + MPI.UB.Extent());
      System.out.println(" lb  " + MPI.UB.Lb());
      System.out.println(" ub  " + MPI.UB.Ub());
      System.out.println(" Lower Bound (LB)***** ");
      System.out.println(" ext " + MPI.LB.Extent());
      System.out.println(" lb  " + MPI.LB.Lb());
      System.out.println(" ub  " + MPI.LB.Ub());
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
