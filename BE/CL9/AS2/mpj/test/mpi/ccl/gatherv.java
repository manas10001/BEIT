package mpi.ccl;

/*
 MPI-Java version :
 Sang Lim (slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 12/1/98
 */
import mpi.*;

public class gatherv {
  static public void main(String[] args) throws Exception {
    try {
      gatherv c = new gatherv(args);
    }
    catch (Exception e) {
    }
  }

  public gatherv() {
  }

  public gatherv(String[] args) throws Exception {

    final int MAXLEN = 10;

    int root, i, j, k;
    int myself, tasks, stride = 15;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (myself == 0)
	System.out.println("gatherv must run with less than 8 tasks");

      MPI.Finalize();
      return;
    }

    int out[] = new int[MAXLEN];
    int in[] = new int[MAXLEN * stride * tasks];
    int dis[] = new int[MAXLEN];
    int rcount[] = new int[MAXLEN];
    int ans[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1,
	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	0 };

    for (i = 0; i < MAXLEN; i++) {
      dis[i] = i * stride;
      rcount[i] = 5;
      out[i] = 1;
    }
    rcount[0] = 10;

    for (i = 0; i < MAXLEN * tasks * stride; i++) {
      in[i] = 0;
    }

    if (myself == 0)
      MPI.COMM_WORLD.Gatherv(out, 0, 10, MPI.INT, in, 0, rcount, dis, MPI.INT,
	  0);
    else
      MPI.COMM_WORLD
	  .Gatherv(out, 0, 5, MPI.INT, in, 0, rcount, dis, MPI.INT, 0);

    /*
     * if(myself==0){ for(i=0; i<tasks*stride; i++) if (ans[i]!=in[i])
     * System.out.println("recived data : "+in[i]+"at ["+i+
     * "] should be : "+ans[i]); }
     */

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Gatherv TEST COMPLETE");
    MPI.Finalize();
  }
}
