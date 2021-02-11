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

public class test3 {
  static public void main(String[] args) throws Exception {
    try {
      test3 c = new test3(args);
    }
    catch (Exception e) {
    }
  }

  public test3() {
  }

  public test3(String[] args) throws Exception {

    int i, done;
    int in[] = new int[1];
    int out[] = new int[1];
    int myself, tasks;
    Request req1, req2;
    Status status;

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    in[0] = -1;
    out[0] = 1;

    if (myself < 2) {
      if (myself == 0) {
	req1 = MPI.COMM_WORLD.Isend(out, 0, 1, MPI.INT, 1, 1);
	req2 = MPI.COMM_WORLD.Irecv(in, 0, 1, MPI.INT, 1, 2);
	for (;;) {
	  status = req1.Test();
	  if (status != null)
	    break;
	}
	for (;;) {
	  status = req2.Test();
	  if (status != null)
	    break;
	}
      } else if (myself == 1) {
	MPI.COMM_WORLD.Send(out, 0, 1, MPI.INT, 0, 2);
	MPI.COMM_WORLD.Recv(in, 0, 1, MPI.INT, 0, 1);
      }
      if (in[0] != 1)
	System.out.println("ERROR IN TASK " + myself + ", in[0]=" + in[0]);
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 1)
      System.out.println("Test3 TEST COMPLETE");
    MPI.Finalize();
  }
}
