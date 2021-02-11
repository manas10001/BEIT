package mpi.topo;

/****************************************************************************

 MESSAGE PASSING INTERFACE TEST CASE SUITE

 Copyright IBM Corp. 1995

 IBM Corp. hereby grants a non-exclusive license to use, copy, modify, and
 distribute this software for any purpose and without fee provided that the
 above copyright notice and the following paragraphs appear in all copies.

 IBM Corp. makes no representation that the test cases comprising this
 suite are correct or are an accurate representation of any standard.

 In no event shall IBM be liable to any party for direct, indirect, special
 incidental, or consequential damage arising out of the use of this software
 even if IBM Corp. has been advised of the possibility of such damage.

 IBM CORP. SPECIFICALLY DISCLAIMS ANY WARRANTIES INCLUDING, BUT NOT LIMITED
 TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS AND IBM
 CORP. HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

 ****************************************************************************

 These test cases reflect an interpretation of the MPI Standard.  They are
 are, in most cases, unit tests of specific MPI behaviors.  If a user of any
 test case from this set believes that the MPI Standard requires behavior
 different than that implied by the test case we would appreciate feedback.

 Comments may be sent to:
 Richard Treumann
 treumann@kgn.ibm.com

 ****************************************************************************

 MPI-Java version :
 Sung-Hoon Ko(shko@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 03/22/98

 ****************************************************************************
 */

import mpi.*;

public class cart {
  static public void main(String[] args) throws Exception {
    try {
      cart c = new cart(args);
    }
    catch (Exception e) {
    }
  }

  public cart() {
  }

  public cart(String[] args) throws Exception {

    final int MAXDIMS = 10;
    int tasks, me, type, ndims;

    int rank, src, dest, rc;
    int cnt = 0, i;

    MPI.Init(args);
    rank = MPI.COMM_WORLD.Rank();

    Comm comms[] = new Comm[20];

    Group gid = MPI.COMM_WORLD.Group();

    tasks = gid.Size();
    if (tasks != 6) {
      if (rank == 0)
	System.out.println("topo->cart: MUST RUN WITH 6 TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    /* test non-periodic topology */
    int dims2[] = new int[2];

    dims2[0] = 0;
    dims2[1] = 0;
    // Cartcomm.Dims_create(tasks,dims2);
    dims2[0] = 3;
    dims2[1] = 2;
    if (dims2[0] != 3 || dims2[1] != 2)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims2[0] + ","
	  + dims2[1] + ", should be 3, 2");

    boolean periods2[] = new boolean[2];
    periods2[0] = false;
    periods2[1] = false;
    Cartcomm comm = MPI.COMM_WORLD.Create_cart(dims2, periods2, false);
    comms[cnt++] = comm;
    me = comm.Rank();

    type = comm.Topo_test();
    if (type != MPI.CART)
      System.out.println("ERROR in MPI_Topo_test, type = " + type
	  + ", should be " + MPI.CART);

    ndims = comm.Get().dims.length;
    if (ndims != 2)
      System.out.println("ERROR in MPI_Cartdim_get, ndims = " + ndims
	  + ", should be 2");

    int coords2[] = new int[2];
    dims2 = comm.Get().dims;
    periods2 = comm.Get().periods;
    coords2 = comm.Get().coords;

    if (dims2[0] != 3 || dims2[1] != 2)
      System.out.println("ERROR in MPI_Cart_get, dims = " + dims2[0] + ","
	  + dims2[1] + ", should be 3, 2");
    if (periods2[0] != false || periods2[1] != false)
      System.out.println("WRONG PERIODS!");
    if (coords2[0] != me / 2 || coords2[1] != me % 2) {
      System.out.println("ERROR in MPI_Cart_get, coords = " + coords2[0] + ","
	  + coords2[1] + ", should be " + (me / 2) + "," + (me % 2));
      System.exit(0);
    }

    rank = comm.Rank(coords2);
    if (rank != me)
      System.out.println("ERROR in MPI_Cart_rank, rank = " + rank
	  + ", should be " + me);

    coords2 = comm.Coords(rank);
    if (coords2[0] != me / 2 || coords2[1] != me % 2) {
      System.out.println("ERROR in MPI_Cart_coords, coords = " + coords2[0]
	  + "," + coords2[1] + ", should be " + (me / 2) + ", " + (me % 2));
      System.exit(0);
    }

    src = comm.Shift(0, 5).rank_source;
    dest = comm.Shift(0, 5).rank_dest;

    if (src != MPI.PROC_NULL || dest != MPI.PROC_NULL)
      System.out.println("ERROR in MPI_Cart_shift, src/dest = " + src + ","
	  + dest + ", should be " + MPI.PROC_NULL + ", " + MPI.PROC_NULL);

    src = comm.Shift(0, 1).rank_source;
    dest = comm.Shift(0, 1).rank_dest;

    if (me / 2 < 2 && dest != me + 2)
      System.out.println("ERROR in MPI_Cart_shift, dest = " + dest
	  + ", should be " + (me + 2));

    if (me / 2 > 0 && src != me - 2)
      System.out.println("ERROR in MPI_Cart_shift, src = " + src
	  + ", should be " + (me - 2));

    src = comm.Shift(1, -1).rank_source;
    dest = comm.Shift(1, -1).rank_dest;

    if ((me % 2 == 1) && (dest != me - 1))
      System.out.println("ERROR in MPI_Cart_shift, dest = " + dest
	  + ", should be " + (me - 1));
    if (me % 2 == 1 && src != MPI.PROC_NULL)
      System.out.println("ERROR in MPI_Cart_shift, src = " + src
	  + ", should be " + MPI.PROC_NULL);
    if (me % 2 == 0 && src != me + 1)
      System.out.println("ERROR in MPI_Cart_shift, src = " + src
	  + ", should be " + (me + 1));
    if (me % 2 == 0 && dest != MPI.PROC_NULL)
      System.out.println("ERROR in MPI_Cart_shift, dest = " + dest
	  + ", should be " + MPI.PROC_NULL);

    /* test periodic topology */

    dims2[0] = 2;
    dims2[1] = 0;
    Cartcomm.Dims_create(tasks, dims2);
    dims2[0] = 2;
    dims2[1] = 3;

    if (dims2[0] != 2 || dims2[1] != 3)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims2[0] + ","
	  + dims2[1] + ", should be 2, 3");

    periods2[0] = true;
    periods2[1] = true;
    comm = MPI.COMM_WORLD.Create_cart(dims2, periods2, false);
    comms[cnt++] = comm;
    me = comm.Rank();
    coords2[0] = me / 3;
    coords2[1] = me % 3;
    rank = comm.Rank(coords2);
    if (rank != me)
      System.out.println("ERROR in MPI_Cart_rank, rank = " + rank
	  + ", should be " + me);

    coords2 = comm.Coords(rank);
    if (coords2[0] != me / 3 || coords2[1] != me % 3)
      System.out.println("ERROR in MPI_Cart_coords, coords = " + coords2[0]
	  + "," + coords2[1] + ", should be " + (me / 3) + "," + (me % 3));

    src = comm.Shift(0, 5).rank_source;
    dest = comm.Shift(0, 5).rank_dest;
    if (src != (me + 3) % 6 || dest != (me + 3) % 6)
      System.out.println("ERROR in MPI_Cart_shift, src/dest = " + src + ", "
	  + dest + ", should be " + (me + 3) + ", " + (me + 3));

    src = comm.Shift(1, -1).rank_source;
    dest = comm.Shift(1, -1).rank_dest;
    int k = (me % 3 == 0) ? 1 : 0;
    if (dest != (me - 1) + 3 * k)
      System.out.println("ERROR in MPI_Cart_shift, dest = " + dest
	  + ", should be " + ((me - 1 + 3) % 3));

    k = (me % 3 == 2) ? 1 : 0;
    if (src != (me + 1) - 3 * k)
      System.out.println("ERROR in MPI_Cart_shift, src = " + src
	  + ", should be " + ((me + 1 + 3) % 3));

    dims2[0] = 1;
    comm = MPI.COMM_WORLD.Create_cart(dims2, periods2, false);
    comms[cnt++] = comm;

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Cart TEST COMPLETE\n");
    MPI.Finalize();

  }
}
