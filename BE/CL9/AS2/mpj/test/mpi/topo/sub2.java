package mpi.topo;

import mpi.*;

public class sub2 {
  static public void main(String[] args) throws Exception {
    try {
      sub2 c = new sub2(args);
    }
    catch (Exception e) {
    }
  }

  public sub2() {
  }

  public sub2(String[] args) throws Exception {

    int dims[] = new int[2];
    dims[0] = 2;
    dims[1] = 3;
    boolean periods[] = new boolean[2];
    int size, rank;
    MPI.Init(args);
    rank = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();

    if (size != 8) {
      if (rank == 0)
	System.out.println("topo->sub2: MUST RUN WITH 8 TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    Cartcomm comm = MPI.COMM_WORLD.Create_cart(dims, periods, false);

    if (comm != null) {
      int[] dims2 = comm.Get().dims;
      boolean remain[] = new boolean[2];
      remain[0] = false;
      remain[1] = true;
      Cartcomm subcomm = comm.Sub(remain);
      int nsize = subcomm.Size();
      int nrank = subcomm.Rank();

      System.out.println("rank <" + rank + ">,nrank<" + nrank + ">,size<"
	  + size + ">,nsize <" + nsize);
    } else {
      System.out.println("rank <" + rank + ",size<" + size + ">");
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
