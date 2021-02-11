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
 07/23/98
 ****************************************************************************
 */

import mpi.*;
import java.nio.ByteBuffer;

//no detach ...
public class isendO {

  static int tasks, bytes, i;
  // static byte buf[] = new byte[10000];
  static ByteBuffer buf = null;
  static Request req[];
  static Status stats[];

  static test a[] = new test[1];
  static test b[] = new test[10];

  static void wstart() throws MPIException {
    stats = Request.Waitall(req);

    for (i = 0; i < tasks; i++)
      if (b[i] != null && b[i].a != i)
	System.out.println("ERROR : data is" + b[i].a + ", should be" + i);
  }

  static public void main(String[] args) throws Exception {
    try {
      isendO c = new isendO(args);
    }
    catch (Exception e) {
    }
  }

  public isendO() {
  }

  public isendO(String[] args) throws Exception {
    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (tasks > 8) {
      if (me == 0)
	System.out.println("isendO runs with less than 8 processes!");
      MPI.Finalize();
      return;
    }

    a[0] = new test();
    for (i = 0; i < 10; i++)
      b[i] = new test();

    a[0].a = MPI.COMM_WORLD.Rank();

    req = new Request[2 * tasks];
    stats = new Status[2 * tasks];
    // buf = new mpi.Buffer(10000);
    buf = ByteBuffer.allocateDirect(10000);
    MPI.Buffer_attach(buf);

    if (a[0].a == 0)
      System.out.println("> Testing Isend/Irecv...");
    for (i = 0; i < tasks; i++)
      b[i].a = -1;
    for (i = 0; i < tasks; i++) {

      req[2 * i] = MPI.COMM_WORLD.Isend(a, 0, 1, MPI.OBJECT, i, 1);
      req[2 * i + 1] = MPI.COMM_WORLD.Irecv(b, i, 1, MPI.OBJECT, i, 1);

    }

    wstart();
    /*
     * if(a[0].a == 0) System.out.println("> Testing Issend/Irecv...");
     * for(i=0;i<tasks;i++) b[i].a = -1;
     * 
     * for(i=0;i<tasks;i++) {
     * req[2*i]=MPI.COMM_WORLD.Issend(a,0,1,MPI.OBJECT,i,1);
     * req[2*i+1]=MPI.COMM_WORLD.Irecv(b,i,1,MPI.OBJECT,i,1); }
     * 
     * wstart();
     * 
     * if(a[0].a == 0) System.out.println("> Testing Irecv/Irsend...");
     * for(i=0;i<tasks;i++) b[i].a = -1; for(i=0;i<tasks;i++) {
     * req[2*i+1]=MPI.COMM_WORLD.Irecv(b,i,1,MPI.OBJECT,i,1); }
     * 
     * 
     * MPI.COMM_WORLD.Barrier();
     * 
     * for(i=0;i<tasks;i++) {
     * req[2*i]=MPI.COMM_WORLD.Irsend(a,0,1,MPI.OBJECT,i,1); }
     * 
     * 
     * wstart();
     * 
     * if(a[0].a == 0) System.out.println("> Testing Ibsend/Irecv...");
     * for(i=0;i<tasks;i++) b[i].a = -1; for(i=0;i<tasks;i++) {
     * 
     * req[2*i]=MPI.COMM_WORLD.Ibsend(a,0,1,MPI.OBJECT,i,1);
     * req[2*i+1]=MPI.COMM_WORLD.Irecv(b,i,1,MPI.OBJECT,i,1);
     * 
     * } wstart();
     */
    MPI.COMM_WORLD.Barrier();
    if (a[0].a == 1)
      System.out.println("IsendO TEST COMPLETE");
    MPI.Finalize();
  }
}
