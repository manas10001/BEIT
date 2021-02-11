package mpi.pt2pt;

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
//no detach ..
import mpi.*;
import java.nio.ByteBuffer;

public class start {

  static int me, tasks, rc, i, bytes;
  static int mebuf[] = new int[1];
  static int data[];
  // static byte buf[];
  static ByteBuffer buf = null;

  static Prequest req[];
  static Status stats[];

  static void wstart() throws MPIException {
    for (i = 0; i < tasks; i++)
      data[i] = -1;
    MPI.COMM_WORLD.Barrier();
    for (i = 0; i < 2 * tasks; i++) {
      req[i].Start();
    }

    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != i)
	System.out.println("ERROR in Startall: data is " + data[i]
	    + ", should be " + i);
    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */
    for (i = 1; i < 2 * tasks; i += 2) {
      bytes = stats[i].Get_count(MPI.INT); // fix by aamir.

      if (bytes != 1)// aamir.
	System.out.println("ERROR in Waitall: bytes = " + bytes
	    + ", should be 1");
    }

  }

  // ////////////////////////////////////////////////////////////////////

  static public void main(String[] args) throws MPIException {
    try {
      start c = new start(args);
    }
    catch (Exception e) {
    }
  }

  public start() {
  }

  public start(String[] args) throws Exception {

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    // data = new int[tasks+MPI.BSEND_OVERHEAD];
    data = new int[tasks];
    int intsize = 4;
    // buf = new byte[tasks * (intsize+MPI.BSEND_OVERHEAD)];
    // buf = new mpi.Buffer( MPI.COMM_WORLD.Pack_size(tasks, MPI.INT) );
    buf = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(tasks, MPI.INT)
	+ tasks * MPI.BSEND_OVERHEAD);
    req = new Prequest[2 * tasks];
    stats = new Status[2 * tasks];

    MPI.Buffer_attach(buf);

    mebuf[0] = me;

    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Send_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);
    }
    if (me == 0)
      System.out.println("Testing send/recv init...");
    wstart();

    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Ssend_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);
    }
    if (me == 0)
      System.out.println("Testing ssend init...");
    wstart();

    for (i = 0; i < tasks; i++) {
      req[2 * i] = MPI.COMM_WORLD.Bsend_init(mebuf, 0, 1, MPI.INT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Recv_init(data, i, 1, MPI.INT, i, 1);
    }

    if (me == 0)
      System.out.println("Testing bsend init...");
    wstart();

    MPI.COMM_WORLD.Barrier();

    if (me == 1)
      System.out.println("Start TEST COMPLETE");
    MPI.Finalize();
  }
}
