package mpi.pt2pt;

import mpi.*;

public class dacian_test {

  public dacian_test(String[] args) {

    MPI.Init(args);
    int rank = MPI.COMM_WORLD.Rank();

    if (rank == 0) {
      String[] msg = new String[10];
      msg[0] = new String("Hello");
      MPI.COMM_WORLD.Send(msg, 0, 1, MPI.OBJECT, 1, 13);
    } else {
      String[] message = new String[10];
      MPI.COMM_WORLD.Recv(message, 0, 1, MPI.OBJECT, 0, 13);
      System.out.println(message[0]);
    }

    MPI.Finalize();
  }

}
