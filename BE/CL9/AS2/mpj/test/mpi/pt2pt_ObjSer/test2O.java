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
 07/30/98
 ****************************************************************************
 */

import mpi.*;

public class test2O {
  static public void main(String[] args) throws Exception {
    try {
      test2O c = new test2O(args);
    }
    catch (Exception e) {
    }
  }

  public test2O() {
  }

  public test2O(String[] args) throws Exception {

    int numtask, taskid, rc;
    test outmsg[] = new test[1];
    test inmsg[] = new test[1];
    int i, dest = 0, type = 1;
    int source, rtype = type, rbytes = -1, flag, dontcare = -1;
    int msgid;
    Status status;
    Request req;

    MPI.Init(args);
    taskid = MPI.COMM_WORLD.Rank();
    numtask = MPI.COMM_WORLD.Size();

    if (numtask > 2) {
      if (taskid == 0) {
	System.out.println("test2O must run with less than 8 tasks");
      }
      MPI.Finalize();
      return;
    }

    if (taskid == 1) {
      MPI.COMM_WORLD.Barrier();
      outmsg[0] = new test();
      outmsg[0].a = 5;
      type = 1;
      MPI.COMM_WORLD.Send(outmsg, 0, 1, MPI.OBJECT, dest, type);
    }

    if (taskid == 0) {
      source = MPI.ANY_SOURCE;
      rtype = MPI.ANY_TAG;
      req = MPI.COMM_WORLD.Irecv(inmsg, 0, 1, MPI.OBJECT, source, rtype);

      status = req.Test();
      if (status != null)
	System.out.println("ERROR(1)");
      MPI.COMM_WORLD.Barrier();

      status = req.Wait();
      if (inmsg[0].a != 5 || status.source != 1 || status.tag != 1) {
	System.out.println("ERROR(2)");
	System.out.println(" inmsg[0].a " + inmsg[0].a);
	System.out.println(" status.source " + status.source);
	System.out.println(" status.tag " + status.source);
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (taskid == 1)
      System.out.println("Test2O TEST COMPLETE <" + taskid + ">");
    MPI.Finalize();
  }
}
