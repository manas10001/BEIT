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

import mpi.*;

public class ssend {
  static public void main(String[] args) throws Exception {
    try {
      ssend c = new ssend(args);
    }
    catch (Exception e) {
    }
  }

  public ssend() {
  }

  public ssend(String[] args) throws Exception {

    char buf[] = new char[10];
    int len, tasks, me, i;
    Status status;
    double time, timeoffset;
    double timeBuf[] = new double[1];
    double timeoffsetBuf[] = new double[1];

    /*
     * This test makes assumptions about the global nature of MPI_WTIME that are
     * not required by MPI, and may falsely signal an error
     */

    len = buf.length;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    if (me == 0) {
      /* First, roughly synchronize the clocks */
      MPI.COMM_WORLD.Recv(timeoffsetBuf, 0, 1, MPI.DOUBLE, 1, 1);
      timeoffset = timeoffsetBuf[0];
      timeoffset = MPI.Wtime() - timeoffset;

      MPI.COMM_WORLD.Ssend(buf, 0, len, MPI.CHAR, 1, 1);

      time = MPI.Wtime() - timeoffset;
      timeBuf[0] = time;
      MPI.COMM_WORLD.Ssend(timeBuf, 0, 1, MPI.DOUBLE, 1, 2);
    } else if (me == 1) {
      time = MPI.Wtime();
      timeBuf[0] = time;

      MPI.COMM_WORLD.Ssend(timeBuf, 0, 1, MPI.DOUBLE, 0, 1);

      for (i = 0; i < 3000000; i++)
	;

      MPI.COMM_WORLD.Recv(buf, 0, len, MPI.CHAR, 0, 1);
      MPI.COMM_WORLD.Recv(timeBuf, 0, 1, MPI.DOUBLE, 0, 2);
      time = timeBuf[0];
      time = time - MPI.Wtime();
      if (time < 0)
	time = -time;
      if (time > .1)
	System.out.println("ERROR (Not important): MPI_Ssend did"
	    + "not synchronize");

      // Don't understand exactly what this is *meant* to do, but on
      // general principles it seems dubious one could make an effective
      // test of the MPI spec this way... DBC.
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 1)
      System.out.println("Ssend TEST COMPLETE");
    MPI.Finalize();
  }
}
