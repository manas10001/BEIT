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

public class sendrecvO {

  public sendrecvO() {
  }

  public static void main(String args[]) throws Exception {
    sendrecvO test = new sendrecvO(args);
  }

  public sendrecvO(String[] args) throws Exception {
    int src, dest, sendtag, recvtag, tasks, me, i;
    Status status;

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    test datatest[] = new test[1000];
    test recdata[] = new test[1000];

    for (i = 0; i < 1000; i++) {
      datatest[i] = new test();
      recdata[i] = new test();
      datatest[i].a = me;
      recdata[i].a = 0;
    }

    if (me < 2) {
      src = dest = 1 - me;
      sendtag = me;
      recvtag = src;

      status = MPI.COMM_WORLD.Sendrecv(datatest, 0, 100, MPI.OBJECT, dest,
	  sendtag, recdata, 0, 100, MPI.OBJECT, src, recvtag);

      for (i = 0; i < 2000000; i++)
	;

      for (i = 0; i < 100; i++)
	if (recdata[i].a != src) {
	  System.out.println("ERROR in MPI.Sendrecv: incorrect data\n");
	  break;
	}

      if (status.source != src)
	System.out.println("ERROR in MPI.Sendrecv: incorrect source\n");
      if (status.tag != recvtag)
	System.out.println("ERROR in MPI.Sendrecv: incorrect tag ("
	    + status.tag + ")");
    }

    src = (me == 0) ? tasks - 1 : me - 1;
    dest = (me == tasks - 1) ? 0 : me + 1;
    sendtag = me;
    recvtag = src;

    status = MPI.COMM_WORLD.Sendrecv(datatest, 0, 100, MPI.OBJECT, dest,
	sendtag, recdata, 0, 100, MPI.OBJECT, src, recvtag);

    for (i = 0; i < 2000000; i++)
      ;

    for (i = 0; i < 100; i++)
      if (recdata[i].a != src) {
	System.out.println("ERROR in MPI.Sendrecv: incorrect data\n");
	break;
      }

    if (status.source != src)
      System.out.println("ERROR in MPI.Sendrecv: incorrect source\n");
    if (status.tag != recvtag)
      System.out.println("ERROR in MPI.Sendrecv: incorrect tag (" + status.tag
	  + ")");

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("SendRecvO TEST COMPLETE");
    MPI.Finalize();
  }
}
