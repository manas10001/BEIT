package mpi.ccl;

/*
 MPI-Java version :
 Sang Lim (slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 12/2/98
 */

import mpi.*;

public class alltoallv {
  static public void main(String[] args) throws Exception {
    try {
      alltoallv c = new alltoallv(args);
    }
    catch (Exception e) {
    }
  }

  public alltoallv() {
  }

  public alltoallv(String[] args) throws Exception {

    final int MAXLEN = 10;

    int myself, tasks;
    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int root, i = 0, j, k, stride = 15;
    int out[] = new int[tasks * stride];
    int in[] = new int[tasks * stride];
    int sdis[] = new int[tasks];
    int scount[] = new int[tasks];
    int rdis[] = new int[tasks];
    int rcount[] = new int[tasks];
    int ans[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 15, 16, 17, 18,
	19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
	37, 38, 39, 40, 41, 42, 43, 44 };

    for (i = 0; i < tasks; i++) {
      sdis[i] = i * stride;
      scount[i] = 15;
      rdis[i] = i * 15;
      rcount[i] = 15;
    }

    if (myself == 0)
      for (i = 0; i < tasks; i++)
	scount[i] = 10;

    rcount[0] = 10;
    for (j = 0; j < tasks; j++)
      for (i = 0; i < stride; i++) {
	out[i + j * stride] = i + myself * stride;
	in[i + j * stride] = 0;
      }

    MPI.COMM_WORLD.Alltoallv(out, 0, scount, sdis, MPI.INT, in, 0, rcount,
	rdis, MPI.INT);

    /*
     * for(i=0; i<tasks*stride; i++) if (ans[i]!=in[i])
     * System.out.println("recived data : "+in[i]+"at ["+i+
     * "]  should be : "+ans[i]+" on proc. : "+ myself);
     */
    MPI.COMM_WORLD.Barrier();

    if (myself == 0) {
      System.out.println("Alltoallv TEST COMPLETE");
    }

    MPI.Finalize();
  }
}
