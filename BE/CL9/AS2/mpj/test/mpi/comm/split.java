package mpi.comm;

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

 ****************************************************************************/

import mpi.*;

public class split {
  static public void main(String[] args) throws Exception {
    try {
      split a = new split(args);
    }
    catch (Exception e) {
    }
  }

  public split() {
  }

  public split(String[] args) throws Exception {

    int i, me, tasks, color, key;
    int mebuf[] = new int[1];
    int ranks[] = new int[128];

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    if (tasks % 2 == 1) { // || tasks < 8) {
      if (me == 0)
	System.out.println("comm->split: MUST RUN WITH EVEN NUMBER OF TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    color = me % 2;
    key = me;
    Intracomm newcomm = MPI.COMM_WORLD.Split(color, key);

    mebuf[0] = me;
    newcomm.Allgather(mebuf, 0, 1, MPI.INT, ranks, 0, 1, MPI.INT);

    if (me % 2 == 0) {
      for (i = 0; i < tasks / 2; i++)
	if (ranks[i] != 2 * i)
	  System.out.println("ERROR in MPI.Comm_split: wrong tasks");
    }
    if (me % 2 != 0) {
      for (i = 0; i < tasks / 2; i++)
	if (ranks[i] != 2 * i + 1)
	  System.out.println("ERROR in MPI.Comm_split: wrong tasks");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Split TEST COMPLETE");
    MPI.Finalize();
  }
}
