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

public class sendrecv {

  static public void main(String[] args) throws Exception {
    try {
      sendrecv c = new sendrecv(args);
    }
    catch (Exception e) {
    }
  }

  public sendrecv() {
  }

  public sendrecv(String[] args) throws Exception {
    int src, dest, sendtag, recvtag, tasks, me, i;
    Status status;

    MPI.Init(args);
    tasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    int sendbuf[] = new int[1000];
    int recvbuf[] = new int[1000];
    if (me < 2) {
      src = dest = 1 - me;
      sendtag = me;
      recvtag = src;

      for (i = 0; i < 100; i++) {
	sendbuf[i] = me;
	recvbuf[i] = -1;
      }

      status = MPI.COMM_WORLD.Sendrecv(sendbuf, 0, 100, MPI.INT, dest, sendtag,
	  recvbuf, 0, 100, MPI.INT, src, recvtag);

      for (i = 0; i < 2000000; i++)
	;

      for (i = 0; i < 100; i++)
	if (recvbuf[i] != src) {
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
    for (i = 0; i < 100; i++) {
      sendbuf[i] = me;
      recvbuf[i] = -1;
    }

    status = MPI.COMM_WORLD.Sendrecv(sendbuf, 0, 100, MPI.INT, dest, sendtag,
	recvbuf, 0, 100, MPI.INT, src, recvtag);

    for (i = 0; i < 2000000; i++)
      ;
    for (i = 0; i < 100; i++)
      if (recvbuf[i] != src) {
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
      System.out.println("SendRecv TEST COMPLETE");
    MPI.Finalize();
  }
}
