package mpi.pt2pt;

import mpi.*;

public class TestSendInit {

  static int me, tasks, i;
  static int mebuf[] = new int[1];
  static int data[];

  static Prequest req[];

  static void wstart() throws MPIException {
    for (i = 0; i < tasks; i++)
      data[i] = -1;
    MPI.COMM_WORLD.Barrier();
    for (i = 0; i < 2 * tasks; i++) {
      req[i].Start();
    }

    Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != i)
	System.out.println("ERROR in Startall: data is " + data[i]
	    + ", should be " + i);

  }

  static public void main(String[] args) throws Exception {

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    data = new int[tasks];
    req = new Prequest[2 * tasks];

    mebuf[0] = me;
    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Send_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);
    }

    for (int j = 0; j <= 2; j++) {
      System.out.println("Iteration " + j);
      wstart();
    }

    MPI.Finalize();
  }
}
