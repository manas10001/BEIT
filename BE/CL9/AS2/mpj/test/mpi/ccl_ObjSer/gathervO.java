package mpi.ccl_ObjSer;

/*
 MPI-Java version :
 Sang Lim (slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 12/1/98
 */

import mpi.*;

public class gathervO {
  static public void main(String[] args) throws Exception {
    try {
      gathervO c = new gathervO(args);
    }
    catch (Exception e) {
    }
  }

  public gathervO() {
  }

  public gathervO(String[] args) throws Exception {

    final int MAXLEN = 10;

    int root, i, j, k;
    int myself, tasks, stride = 15;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (myself == 0) {
	System.out.println("gathervO must run with fewer than 8 tasks!");
      }
      MPI.Finalize();
      return;
    }

    int out[][] = new int[MAXLEN][MAXLEN];
    int in[][] = new int[MAXLEN * stride * tasks][MAXLEN];
    int ans[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0 };

    int dis[] = new int[MAXLEN];
    int rcount[] = new int[MAXLEN];

    for (i = 0; i < MAXLEN * stride * tasks; i++)
      for (j = 0; j < MAXLEN; j++)
	in[i][j] = 0;

    for (i = 0; i < MAXLEN; i++)
      for (j = 0; j < MAXLEN; j++) {
	dis[i] = i * stride;
	rcount[i] = 5;
	out[j][i] = j + 1;
      }
    rcount[0] = 10;

    if (myself == 0)
      MPI.COMM_WORLD.Gatherv(out, 0, 10, MPI.OBJECT, in, 0, rcount, dis,
	  MPI.OBJECT, 0);
    else
      MPI.COMM_WORLD.Gatherv(out, 0, 5, MPI.OBJECT, in, 0, rcount, dis,
	  MPI.OBJECT, 0);
    /*
     * if(myself==0){ for(j=0; j<MAXLEN;j++){ for(i=0; i<tasks*stride; i++) if
     * (ans[i]!=in[i][j])
     * System.out.println("recived data : "+in[i][j]+"at ["+i+
     * "]["+j+"] should be : "+ans[i]); } }
     */
    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("GathervO TEST COMPLETE");
    MPI.Finalize();
  }
}
