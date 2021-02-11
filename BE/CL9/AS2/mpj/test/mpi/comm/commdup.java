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

public class commdup {
  static public void main(String[] args) throws Exception {
    try {
      commdup a = new commdup(args);
    }
    catch (Exception e) {
    }
  }

  public commdup() {
  }

  public commdup(String[] args) throws Exception {
    final int ITER = 20;
    int i, myself;

    MPI.Init(args);

    Comm comm, newcomm;

    myself = MPI.COMM_WORLD.Rank();

    for (i = 0; i < ITER; i++)
      comm = (Comm) MPI.COMM_WORLD.clone();

    comm = MPI.COMM_WORLD;
    for (i = 0; i < ITER; i++) {
      newcomm = (Comm) comm.clone();
      comm = newcomm;
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("CommDup TEST COMPLETE");
    MPI.Finalize();
  }
}
