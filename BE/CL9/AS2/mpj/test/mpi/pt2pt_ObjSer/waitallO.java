package mpi.pt2pt_ObjSer;

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

 Object version :
 Sang Lim(slim@npac.syr.edu)
 Northeast Parallel Architectures Center at Syracuse University
 09/3/98
 ****************************************************************************
 */

import mpi.*;

public class waitallO {
  static public void main(String[] args) throws Exception {
    try {
      waitallO c = new waitallO(args);
    }
    catch (Exception e) {
    }
  }

  public waitallO() {
  }

  public waitallO(String[] args) throws Exception {

    int me, tasks, bytes, i;
    test mebuf[] = new test[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    mebuf[0] = new test();
    mebuf[0].a = me;

    test data[] = new test[tasks];
    for (int j = 0; j < tasks; j++) {
      data[j] = new test();
      data[j].a = -1;
    }
    Request req[] = new Request[2 * tasks];
    Status stats[] = new Status[2 * tasks];

    // mebuf[0] = me;
    for (i = 0; i < tasks; i++) {
      if (i != me) {
	req[2 * i] = MPI.COMM_WORLD.Isend(mebuf, 0, 1, MPI.OBJECT, i, 1);
	// Original IBM code used `Irsend' here. Clearly wrong? dbc.

	req[2 * i + 1] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);
      }
    }
    stats = Request.Waitall(req);

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("WaitallO TEST COMPLETE");
    MPI.Finalize();
  }
}
