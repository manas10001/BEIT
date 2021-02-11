package mpi.ccl;

/*
 MPI-Java version :
 Sang Lim (slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 12/2/98
 */

import mpi.*;

public class scatterv {
  static public void main(String[] args) throws Exception {
    try {
      scatterv c = new scatterv(args);
    }
    catch (Exception e) {
    }
  }

  public scatterv() {
  }

  public scatterv(String[] args) throws Exception {

    final int MAXLEN = 10;

    int myself, tasks;
    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (myself == 0)
	System.out.println("scatterv must run with less than 8 tasks! ");
      MPI.Finalize();
      return;
    }

    int root, i = 0, j, k, stride = 15;
    int out[] = new int[tasks * stride];
    int in[] = new int[MAXLEN];
    int dis[] = new int[MAXLEN];
    int scount[] = new int[MAXLEN];

    for (i = 0; i < MAXLEN; i++) {
      dis[i] = i * stride;
      scount[i] = 5;
      in[i] = 0;
    }
    scount[0] = 10;

    for (i = 0; i < tasks * stride; i++)
      out[i] = i;

    MPI.COMM_WORLD.Scatterv(out, 0, scount, dis, MPI.INT, in, 0,
	scount[myself], MPI.INT, 0);

    String[] messbuf = new String[1];

    if (myself == 0) {
      System.out.println("Original array on root...");
      for (i = 0; i < tasks * stride; i++)
	System.out.print(out[i] + " ");
      System.out.println();
      System.out.println();

      System.out.println("Result on proc 0...");
      System.out.println("Stride = 15 " + "Count = " + scount[0]);
      for (i = 0; i < MAXLEN; i++)
	System.out.print(in[i] + " ");
      System.out.println();
      System.out.println();

      // Reproduces output of original test case, but deterministically

      int nmess = tasks < 3 ? tasks : 3;
      for (int t = 1; t < nmess; t++) {
	MPI.COMM_WORLD.Recv(messbuf, 0, 1, MPI.OBJECT, t, 0);

	System.out.print(messbuf[0]);
      }
    }

    if (myself == 1) {
      StringBuffer mess = new StringBuffer();

      mess.append("Result on proc 1...\n");
      mess.append("Stride = 15 " + "Count = " + scount[1] + "\n");
      for (i = 0; i < MAXLEN; i++)
	mess.append(in[i] + " ");
      mess.append("\n");
      mess.append("\n");

      messbuf[0] = mess.toString();
      MPI.COMM_WORLD.Send(messbuf, 0, 1, MPI.OBJECT, 0, 0);
    }

    if (myself == 2) {
      StringBuffer mess = new StringBuffer();

      mess.append("Result on proc 2...\n");
      mess.append("Stride = 15 " + "Count = " + scount[2] + "\n");
      for (i = 0; i < MAXLEN; i++)
	mess.append(in[i] + " ");
      mess.append("\n");

      messbuf[0] = mess.toString();
      MPI.COMM_WORLD.Send(messbuf, 0, 1, MPI.OBJECT, 0, 0);
    }

    if (myself == 0)
      System.out.println("Scatterv TEST COMPLETE");
    MPI.Finalize();
  }
}

// Things to do
//
// Make output deterministic by gathering and printing from root.

