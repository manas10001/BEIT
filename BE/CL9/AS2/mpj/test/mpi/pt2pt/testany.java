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

public class testany {
  static public void main(String[] args) throws Exception {
    try {
      testany c = new testany(args);
    }
    catch (Exception e) {
    }
  }

  public testany() {
  }

  public testany(String[] args) throws Exception {

    int me, tasks, i, index, done;
    ;
    int mebuf[] = new int[1];
    boolean flag;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    int data[] = new int[tasks];
    Request req[] = new Request[tasks];
    Status status;

    mebuf[0] = me;
    if (me > 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.INT, 0, 1);
    else if (me == 0) {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.INT, i, 1);

      done = 0;
      while (done < tasks - 1) {
	status = Request.Testany(req);
	if (status != null) {
	  done++;
	  if (!req[status.index].Is_null())
	    System.out.println("ERROR in MPI_Testany: reqest not set to null");
	  if (data[status.index] != status.index)
	    System.out.println("ERROR in MPI.Testany: wrong data");
	}
      }
    }

    // MPI.COMM_WORLD.Barrier();
    // if(me == 1)
    System.out.println("Testany TEST COMPLETE <" + me + ">");
    MPI.Finalize();
  }
}
