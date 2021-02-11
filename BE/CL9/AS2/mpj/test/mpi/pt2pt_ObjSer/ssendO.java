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
 08/10/98
 ****************************************************************************
 */

import mpi.*;

public class ssendO {
  static public void main(String[] args) throws Exception {
    try {
      ssendO c = new ssendO(args);
    }
    catch (Exception e) {
    }
  }

  public ssendO() {
  }

  public ssendO(String[] args) throws Exception {

    test a[] = new test[10];
    test b[] = new test[10];

    int len, tasks, me, i;
    Status status;
    double time, timeoffset;
    double timeBuf[] = new double[1];
    double timeoffsetBuf[] = new double[1];

    /*
     * This test makes assumptions about the global nature of MPI_WTIME that are
     * not required by MPI, and may falsely signal an error
     */

    len = a.length;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < 10; i++) {
      a[i] = new test();
      b[i] = new test();
      a[i].a = i;
      b[i].a = 0;
    }
    if (me == 0) {
      /* First, roughly synchronize the clocks */
      MPI.COMM_WORLD.Recv(timeoffsetBuf, 0, 1, MPI.DOUBLE, 1, 1);
      timeoffset = timeoffsetBuf[0];
      timeoffset = MPI.Wtime() - timeoffset;

      MPI.COMM_WORLD.Ssend(a, 0, len, MPI.OBJECT, 1, 1);

      time = MPI.Wtime() - timeoffset;
      timeBuf[0] = time;

    } else if (me == 1) {
      time = MPI.Wtime();
      timeBuf[0] = time;

      MPI.COMM_WORLD.Ssend(timeBuf, 0, 1, MPI.DOUBLE, 0, 1);

      for (i = 0; i < 3000000; i++)
	;

      MPI.COMM_WORLD.Recv(b, 0, len, MPI.OBJECT, 0, 1);

      time = timeBuf[0];
      time = time - MPI.Wtime();
      if (time < 0)
	time = -time;
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 1)
      System.out.println("SsendO TEST COMPLETE");
    MPI.Finalize();
  }
}
