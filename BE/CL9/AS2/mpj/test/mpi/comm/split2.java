package mpi.comm;

import mpi.*;

public class split2 {
  static public void main(String[] args) throws Exception {
    try {
      split2 a = new split2(args);
    }
    catch (Exception e) {
    }
  }

  public split2() {
  }

  public split2(String[] args) throws Exception {

    MPI.Init(args);
    int size = MPI.COMM_WORLD.Size();
    int rank = MPI.COMM_WORLD.Rank();

    if (size != 8) {
      if (rank == 0)
	System.out.println("comm->split2: MUST RUN WITH 8 processes");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    int color = 2 * rank / size;
    int key = size - rank - 1;
    Intracomm newcomm = MPI.COMM_WORLD.Split(color, key);
    int nrank = newcomm.Rank();

    MPI.COMM_WORLD.Barrier();
    if (rank == 0)
      System.out.println("split2 TEST COMPLETE");
    MPI.Finalize();
  }
}
