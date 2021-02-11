package mpi.ccl_ObjSer;

/****************************************************************************

 Object version :
 Sang Lim(slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 11/30/98
 ****************************************************************************/

import mpi.*;

public class allgatherO {
  static public void main(String[] args) throws Exception {
    try {
      allgatherO c = new allgatherO(args);
    }
    catch (Exception e) {
    }
  }

  public allgatherO() {
  }

  public allgatherO(String[] args) throws Exception {
    final int MAXLEN = 6;

    int root, i, j, k, l, m;
    int myself, tasks;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int out[][] = new int[MAXLEN][3];
    int in[][] = new int[MAXLEN * tasks][];

    for (j = 0; j < tasks; j++) {
      if (j == myself)
	for (l = 0; l < MAXLEN; l++)
	  for (i = 0; i < 3; i++)
	    out[l][i] = l + j * MAXLEN;
    }

    MPI.COMM_WORLD.Allgather(out, 0, MAXLEN, MPI.OBJECT, in, 0, MAXLEN,
	MPI.OBJECT);

    for (j = 0; j < tasks; j++) {
      if (j == myself) {
	for (l = 0; l < MAXLEN * tasks; l++)
	  for (i = 0; i < 3; i++) {
	    if (in[l][i] != l)
	      System.out.println("Recived data : " + in[l][i] + " at proc. "
		  + j + "  in recive buffer[" + l + "][" + i + "] should be : "
		  + l);
	  }
      }
      MPI.COMM_WORLD.Barrier();
    }
    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("AllgatherO TEST COMPLETE");
    MPI.Finalize();
  }
}
