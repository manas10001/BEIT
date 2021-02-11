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
//no detach
import mpi.*;
import java.nio.ByteBuffer;

public class start2 {

  static int me, tasks, rc, i, bytes;
  static int mebuf[] = new int[1];
  static int data[];
  static ByteBuffer buf;

  static Prequest req[] = new Prequest[4];
  static Status stats[];

  static void wstart() throws MPIException {
    for (i = 0; i < tasks; i++)
      data[i] = -1;

    MPI.COMM_WORLD.Barrier();

    for (i = 0; i < 2 * tasks; i++)
      req[i].Start();

    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (data[i] != i)
	System.out.println("ERROR in Startall: data is " + data[i]
	    + ", should be " + i);
    /* ONLY THE RECEIVERS HAVE STATUS VALUES ! */
    for (i = 1; i < 2 * tasks; i += 2) {
      bytes = stats[i].Get_count(MPI.BYTE);

      if (bytes != 4)
	System.out.println("ERROR in Waitall: bytes = " + bytes
	    + ", should be 4");
    }
  }

  //
  // ////////////////////////////////////////////////////////////////////

  static public void main(String[] args) throws MPIException {
  }

  public start2() {
  }

  public start2(String[] args) throws Exception {

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();
    int X = 100;
    int[] data1 = new int[X];
    int[] data2 = new int[X];
    int[] data3 = new int[X];
    int[] data4 = new int[X];

    int tag1 = 1;
    int tag2 = 2;
    int tag3 = 3;
    int tag4 = 4;

    // buf = new mpi.Buffer( MPI.COMM_WORLD.Pack_size( X, MPI.INT) );
    buf = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(X, MPI.INT));
    MPI.Buffer_attach(buf);

    if (me == 0) {
      System.out.println("Testing send/recv init...");
      for (int i = 0; i < X; i++)
	data1[i] = -1;
      req[0] = MPI.COMM_WORLD.Send_init(data1, 0, X, MPI.INT, 1, tag1);
    } else if (me == 1) {
      req[0] = MPI.COMM_WORLD.Recv_init(data1, 0, X, MPI.INT, 0, tag1);
    }

    if (me == 0) {
      System.out.println("Testing ssend init...");
      for (int i = 0; i < X; i++)
	data2[i] = -1;
      req[1] = MPI.COMM_WORLD.Ssend_init(data2, 0, X, MPI.INT, 1, tag2);
    } else if (me == 1) {
      req[1] = MPI.COMM_WORLD.Recv_init(data2, 0, X, MPI.INT, 0, tag2);
    }

    if (me == 0) {
      System.out.println("Testing bsend init...");
      for (int i = 0; i < X; i++)
	data3[i] = -1;
      req[2] = MPI.COMM_WORLD.Bsend_init(data3, 0, X, MPI.INT, 1, tag3);
    } else if (me == 1) {
      req[2] = MPI.COMM_WORLD.Recv_init(data3, 0, X, MPI.INT, 0, tag3);
    }

    if (me == 0) {
      System.out.println("Testing rsend init...");
      for (int i = 0; i < X; i++)
	data4[i] = -1;
      req[3] = MPI.COMM_WORLD.Rsend_init(data4, 0, X, MPI.INT, 1, tag4);
    } else if (me == 1) {
      req[3] = MPI.COMM_WORLD.Recv_init(data4, 0, X, MPI.INT, 0, tag4);
    }

    /* Starting the communications */
    for (int k = 0; k < 4; k++) {
      if (req[k] != null)
	req[k].Start();
    }

    if (me == 1) {
      for (int i = 0; i < X; i++) {
	if (data1[i] != -1) {
	  System.out.println("ERROR(1): incorrect data");
	}

	if (data2[i] != -1) {
	  System.out.println("ERROR(2): incorrect data");
	}

	if (data3[i] != -1) {
	  System.out.println("ERROR(3): incorrect data");
	}

	if (data4[i] != -1) {
	  System.out.println("ERROR(4): incorrect data");
	}
      }
    }// checking the results ..

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Start2 TEST COMPLETE\n");
    MPI.Finalize();
  }
}
