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

public class test1 {
  static public void main(String[] args) throws Exception {
    try {
      test1 c = new test1(args);
    }
    catch (Exception e) {
    }
  }

  public test1() {
  }

  public test1(String[] args) throws Exception {
    int outmsg[] = new int[1];
    int inmsg[] = new int[1];
    int i, me, flag = 0;
    Status status = null;
    Request msgid;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    if (me == 1) {
      outmsg[0] = 5;
      MPI.COMM_WORLD.Send(outmsg, 0, 1, MPI.INT, 0, 1);
    }
    if (me == 0) {
      msgid = MPI.COMM_WORLD.Irecv(inmsg, 0, 1, MPI.INT, MPI.ANY_SOURCE,
	  MPI.ANY_TAG);
      while (status == null) {

	status = msgid.Test();
	// System.out.println("test1 -- status "+status);

	if (status != null)
	  break;
      }

      if (inmsg[0] != 5 || status.source != 1 || status.tag != 1)
	System.out.println("ERROR inmsg[0]=" + inmsg[0] + ", src="
	    + status.source + ", tag=" + status.tag + ", should be 5,1,1");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Test1 TEST COMPLETE");
    MPI.Finalize();
  }
}
