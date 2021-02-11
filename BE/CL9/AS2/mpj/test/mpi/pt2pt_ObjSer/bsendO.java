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
import java.nio.ByteBuffer;

//only one detach ..need two i guess at rank0 and one at rank1
public class bsendO {
  static public void main(String[] args) throws Exception {
    try {
      bsendO c = new bsendO(args);
    }
    catch (Exception e) {
    }
  }

  public bsendO() {
  }

  public bsendO(String[] args) throws Exception {
    /*
     * Note that the buffer sizes must include the BSEND_OVERHEAD; these values
     * are probably sizeof(int) too large
     */

    int len, tasks, me, i, size, rc;
    Status status;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    test datatest[] = new test[10];
    test recdata[] = new test[10];
    test a[] = new test[1000];
    test b[] = new test[1000];

    int intsize = 4;
    // byte buf1[] = new byte[1000*intsize+MPI.BSEND_OVERHEAD];
    // byte buf100[] = new byte[100000*intsize+MPI.BSEND_OVERHEAD];
    // No obvious rationale to this.
    // Probably bsend is always unsafe for objects. dbc.
    // mpi.Buffer buf1 = new mpi.Buffer(
    // MPI.COMM_WORLD.Pack_size(1000,MPI.INT) );
    ByteBuffer buf1 = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(1000,
	MPI.INT));
    // mpi.Buffer buf100 = new mpi.Buffer(
    // MPI.COMM_WORLD.Pack_size(100000,MPI.INT));
    ByteBuffer buf100 = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(
	100000, MPI.INT));

    for (i = 0; i < 10; i++) {
      datatest[i] = new test();
      recdata[i] = new test();
      datatest[i].a = 1;
      recdata[i].a = 0;
    }

    if (me == 0) {

      MPI.Buffer_attach(buf1);
      MPI.COMM_WORLD.Bsend(datatest, 0, 10, MPI.OBJECT, 1, 1);

      MPI.Buffer_detach();

      MPI.Buffer_attach(buf100);

      // MPI.COMM_WORLD.Barrier();

      /* test to see if large array is REALLY being buffered */
      for (i = 0; i < 1000; i++) {
	a[i] = new test();
	a[i].a = 1;
	b[i] = new test();
	b[i].a = 0;
      }

      MPI.COMM_WORLD.Bsend(a, 0, 1000, MPI.OBJECT, 1, 1);

      MPI.COMM_WORLD.Recv(b, 0, 1000, MPI.OBJECT, 1, 2);

      for (i = 0; i < 1000; i++)
	if (b[i].a != 2)
	  System.out.println("ERROR, incorrect data[" + i + "]=" + b[i].a
	      + ", task 0");

    } else if (me == 1) {
      MPI.COMM_WORLD.Recv(recdata, 0, 10, MPI.OBJECT, 0, 1);
      // MPI.COMM_WORLD.Barrier();

      MPI.Buffer_attach(buf100);

      /* test to see if large array is REALLY being buffered */
      for (i = 0; i < 1000; i++) {
	a[i] = new test();
	a[i].a = 2;
	b[i] = new test();
	b[i].a = 0;
      }
      MPI.COMM_WORLD.Bsend(a, 0, 1000, MPI.OBJECT, 0, 2);

      MPI.COMM_WORLD.Recv(b, 0, 1000, MPI.OBJECT, 0, 1);

      for (i = 0; i < 1000; i++)
	if (b[i].a != 1)
	  System.out.println("ERROR , incorrect data[" + i + "]=" + b[i].a
	      + ", task 1");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("BsendO TEST COMPLETE");
    MPI.Finalize();
  }
}
