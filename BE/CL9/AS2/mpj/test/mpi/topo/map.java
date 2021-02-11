package mpi.topo;

/****************************************************************************

 MPI-Java version :
 Sung-Hoon Ko(shko@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 03/22/98

 ****************************************************************************/

import mpi.*;

public class map {
  static public void main(String[] args) throws Exception {
    try {
      map c = new map(args);
    }
    catch (Exception e) {
    }
  }

  public map() {
  }

  public map(String[] args) throws Exception {

    final int NUM_DIMS = 2;
    int rank, size, i;
    int errors = 0;
    int dims[] = new int[NUM_DIMS];
    boolean periods[] = new boolean[NUM_DIMS];
    int new_rank;

    MPI.Init(args);
    rank = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();

    if (size != 8) {
      if (rank == 0) {
	System.out.println("topo->map must run with 8 tasks!");
      }
      MPI.Finalize();
      return;
    }

    /* Clear dims array and get dims for topology */
    for (i = 0; i < NUM_DIMS; i++) {
      dims[i] = 0;
      periods[i] = false;
    }
    dims[0] = 2;
    dims[1] = 4;

    // Cartcomm.Dims_create(size,dims);

    Cartcomm intcomm = MPI.COMM_WORLD.Create_cart(dims, periods, false);

    /* Look at what rankings a cartesian topology MIGHT have */
    new_rank = intcomm.Map(dims, periods);

    /* Check that all new ranks are used exactly once */
    int rbuf[] = new int[size];
    int sbuf[] = new int[size];

    for (i = 0; i < size; i++)
      sbuf[i] = 0;
    sbuf[new_rank] = 1;
    MPI.COMM_WORLD.Reduce(sbuf, 0, rbuf, 0, size, MPI.INT, MPI.SUM, 0);
    if (rank == 0) {
      for (i = 0; i < size; i++) {
	if (rbuf[i] != 1) {
	  errors++;
	  System.out.println("Rank " + i + " used " + rbuf[i] + " times");
	}
      }
      if (errors == 0)
	System.out.println("Map test passed\n");
    }

    MPI.Finalize();
  }
}
