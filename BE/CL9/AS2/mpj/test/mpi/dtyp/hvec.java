package mpi.dtyp;

// This test is from MPICH test suite.

/* The original version of this was sent by  
 ung-Hoon Ko
 I've modified it to fit the automated tests requirements
 */
/****************************************************************************

 MPI-Java version :
 Sung-Hoon Ko(shko@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 03/22/98

 ****************************************************************************/

import mpi.*;

public class hvec {
  static public void main(String[] args) throws MPIException {
    try {
      hvec c = new hvec(args);
    }
    catch (Exception e) {
    }
  }

  public hvec() {
  }

  public hvec(String[] args) throws Exception {
    int my_rank;

    MPI.Init(args);
    my_rank = MPI.COMM_WORLD.Rank();

    Datatype messtyp, messtyp2;
    int root = 0;
    int i, istat, big_offset;
    final int DL = 32;

    int dar[] = new int[DL];

    for (i = 0; i < DL; i++)
      dar[i] = my_rank;

    int count = 2;
    int bllen = 3;
    int gap = 1;
    int str = bllen + gap;

    messtyp = Datatype.Vector(count, bllen, str, MPI.INT);
    messtyp.Commit();
    MPI.COMM_WORLD.Bcast(dar, 0, 1, messtyp, root);
    if (my_rank == 1) {
      System.out.println("  0 = Sent, 1 = Not Sent");
      System.out.println("  Vector Type with Gap : ");
    }
    if (my_rank == 1) {
      for (i = 0; i < DL; i++)
	System.out.print(dar[i] + " ");
      System.out.println();
      System.out.println();
    }

    for (big_offset = -1; big_offset <= 2; big_offset++) {
      if (my_rank == 1)
	System.out
	    .println(" Three of above vector types combined, with offset = "
		+ big_offset + " ints");

      for (i = 0; i < DL; i++)
	dar[i] = my_rank;

      count = 3;
      int ext = messtyp.Extent();
      messtyp2 = Datatype.Hvector(count, 1, ext + big_offset, messtyp);
      messtyp2.Commit();
      MPI.COMM_WORLD.Bcast(dar, 0, 1, messtyp2, root);

      MPI.COMM_WORLD.Barrier();

      if (my_rank == 1) {
	for (i = 0; i < DL; i++)
	  System.out.print(dar[i] + " ");
	System.out.println();
	System.out.println();
      }
    }

    if (my_rank == 1) {
      System.out.println("hvec TEST Completed");
    }

    MPI.Finalize();
  }
}
