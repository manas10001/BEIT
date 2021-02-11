package mpi.ccl_ObjSer;

/*
 MPI-Java version :
 Sang Lim (slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 12/2/98
 */

import mpi.*;

public class scattervO {
  static public void main(String[] args) throws Exception {
    try {
      scattervO c = new scattervO(args);
    }
    catch (Exception e) {
    }
  }

  public scattervO() {
  }

  public scattervO(String[] args) throws Exception {

    final int MAXLEN = 10;

    int myself, tasks;
    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (myself == 0)
	System.out.println("scattervO must run with fewer than 8 tasks!");
      MPI.Finalize();
      return;
    }

    int root, i = 0, j, k, stride = 15;
    test out[] = new test[tasks * stride];
    test in[] = new test[MAXLEN];
    int dis[] = new int[tasks];
    int scount[] = new int[tasks];

    for (i = 0; i < MAXLEN; i++) {
      in[i] = new test();
      in[i].a = 0;
    }
    for (i = 0; i < tasks; i++) {
      dis[i] = i * stride;
      scount[i] = 5;
    }

    scount[0] = 10;

    for (i = 0; i < tasks * stride; i++) {
      out[i] = new test();
      out[i].a = i;
    }

    MPI.COMM_WORLD.Scatterv(out, 0, scount, dis, MPI.OBJECT, in, 0,
	scount[myself], MPI.OBJECT, 0);

    String[] messbuf = new String[1];

    if (myself == 0) {
      System.out.println("Original array on root...");
      for (i = 0; i < tasks * stride; i++)
	System.out.print(out[i].a + " ");
      System.out.println();
      System.out.println();

      System.out.println("Result on proc 0...");
      System.out.println("Stride = 15 " + "Count = " + scount[0]);
      for (i = 0; i < MAXLEN; i++)
	System.out.print(in[i].a + " ");
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
	mess.append(in[i].a + " ");
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
	mess.append(in[i].a + " ");
      mess.append("\n");

      messbuf[0] = mess.toString();
      MPI.COMM_WORLD.Send(messbuf, 0, 1, MPI.OBJECT, 0, 0);
    }

    if (myself == 0)
      System.out.println("ScattervO TEST COMPLETE");
    MPI.Finalize();
  }
}

// Things to do
//
// Make output deterministic by gathering and printing from root.

