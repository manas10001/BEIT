package mpi.ccl_ObjSer;

/***************************************************************************
 Object version :
 Sang Lim(slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 11/1/98
 ****************************************************************************/

import mpi.*;

public class bcastO {
  static public void main(String[] args) throws Exception {
    try {
      bcastO c = new bcastO(args);
    }
    catch (Exception e) {
    }
  }

  public bcastO() {
  }

  public bcastO(String[] args) throws Exception {

    final int MAXLEN = 100;

    int root = 0, i, j, k;
    int out[][] = new int[MAXLEN][MAXLEN];
    int myself, tasks;
    double time;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (myself == root) {
      for (i = 0; i < MAXLEN; i++)
	for (k = 0; k < MAXLEN; k++)
	  out[i][k] = k;
    } else {
      for (i = 0; i < MAXLEN; i++)
	for (k = 0; k < MAXLEN; k++)
	  out[i][k] = k + 1;
    }

    MPI.COMM_WORLD.Bcast(out, 0, MAXLEN, MPI.OBJECT, root);

    for (k = 0; k < MAXLEN; k++) {
      for (i = 0; i < MAXLEN; i++)
	if (out[i][k] != k) {
	  System.out.println("bad answer out[" + i + "][" + k + "] = "
	      + out[i][k] + " should be " + k);
	  break;
	}
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("BcastO TEST COMPLETE");
    MPI.Finalize();
  }
}
