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
 07/18/98
 ****************************************************************************
 */

import mpi.*;

public class testsomeO {
  static public void main(String[] args) throws Exception {
    try {
      testsomeO c = new testsomeO(args);
    }
    catch (Exception e) {
    }
  }

  public testsomeO() {
  }

  public testsomeO(String[] args) throws Exception {

    int me, tasks, i, index, done, outcount;
    test mebuf[] = new test[1];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    test data[] = new test[tasks];
    Request req[] = new Request[tasks];
    Status status[];

    mebuf[0] = new test();
    for (i = 0; i < tasks; i++) {
      data[i] = new test();
      data[i].a = -1;
    }

    mebuf[0].a = me;
    if (me != 0)
      MPI.COMM_WORLD.Send(mebuf, 0, 1, MPI.OBJECT, 0, 1);
    else {
      req[0] = MPI.REQUEST_NULL;
      for (i = 1; i < tasks; i++)
	req[i] = MPI.COMM_WORLD.Irecv(data, i, 1, MPI.OBJECT, i, 1);

      done = 0;
      while (done < tasks - 1) {
	status = Request.Testsome(req);

	outcount = status.length;
	for (i = 0; i < outcount; i++) {
	  done++;

	  if (!req[status[i].index].Is_null())
	    System.out
		.println("ERROR(2) in MPI_Testsome: reqest not set to NULL");
	  if (data[status[i].index].a != status[i].index)
	    System.out.println("ERROR(3) in MPI_Testsome: wrong data");
	}
      }

      status = Request.Testsome(req);
      if (status.length != 0)
	// if(status != null) //Aamir changed it from
	// null to checking length zero
	System.out.println("ERROR in MPI_Testsome: status is NOT null");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("TestsomeO TEST COMPLETE");
    MPI.Finalize();
  }
}
