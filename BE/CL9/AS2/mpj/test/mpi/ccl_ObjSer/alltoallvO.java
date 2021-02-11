package mpi.ccl_ObjSer;

/*
 MPI-Java version :
 Sang Lim (slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 12/3/98
 */

import mpi.*;

public class alltoallvO {
  static public void main(String[] args) throws Exception {
    try {
      alltoallvO c = new alltoallvO(args);
    }
    catch (Exception e) {
    }
  }

  public alltoallvO() {
  }

  public alltoallvO(String[] args) throws Exception {

    final int MAXLEN = 10;

    int myself, tasks;
    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (myself == 0)
	System.out.println("alltoallvO must run with fewer than 8 tasks!");
      MPI.Finalize();
      return;
    }

    int root, i = 0, j, k, stride = 15;
    int out[][] = new int[tasks * stride][MAXLEN];
    int in[][] = new int[tasks * stride][MAXLEN];
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
      for (i = 0; i < stride; i++)
	for (k = 0; k < MAXLEN; k++) {
	  out[i + j * stride][k] = i + myself * stride;
	  in[i + j * stride][k] = 0;
	}

    MPI.COMM_WORLD.Alltoallv(out, 0, scount, sdis, MPI.OBJECT, in, 0, rcount,
	rdis, MPI.OBJECT);

    /*
     * for(j = 0; j < tasks; j++){ if(myself==j){ for(k=0; k<MAXLEN; k++){
     * for(i=0; i<tasks*stride; i++) if (ans[i]!=in[i][k])
     * System.out.println("recived data : "
     * +in[i][k]+"at ["+i+"]["+k+"] should be : "+ans[i]+" on proc. : "+j); } }
     * MPI.COMM_WORLD.Barrier(); }
     */

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("AlltoallvO TEST COMPLETE");
    MPI.Finalize();
  }
}
