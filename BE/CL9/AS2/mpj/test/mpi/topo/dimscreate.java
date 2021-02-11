package mpi.topo;

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

public class dimscreate {
  static public void main(String[] args) throws Exception {
    try {
      dimscreate c = new dimscreate(args);
    }
    catch (Exception e) {
    }
  }

  public dimscreate() {
  }

  public dimscreate(String[] args) throws Exception {

    final int MAXDIMS = 10;
    int rc, tasks, me, ndims;
    int dims[] = new int[MAXDIMS];
    int dims2[] = new int[2];
    int dims3[] = new int[3];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    tasks = 6;

    ndims = 2;
    dims2[0] = 0;
    dims2[1] = 0;
    Cartcomm.Dims_create(tasks, dims2);
    if (dims2[0] != 3 || dims2[1] != 2)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims2[0] + ","
	  + dims2[1] + ", should be 3, 2");

    ndims = 2;
    dims2[0] = 2;
    dims2[1] = 0;
    Cartcomm.Dims_create(tasks, dims2);
    if (dims2[0] != 2 || dims2[1] != 3)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims2[0] + ","
	  + dims2[1] + ", should be 2, 3");

    dims3[0] = 0;
    dims3[1] = 0;
    dims3[2] = 0;
    ndims = 3;
    Cartcomm.Dims_create(tasks, dims3);
    if (dims3[0] != 3 || dims3[1] != 2 || dims3[2] != 1)
      System.out.println("ERROR in MPI_Dims_create, dims = " + dims3[0] + ","
	  + dims3[1] + "," + dims3[2] + ", should be 3,2,1");

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("DimsCreate TEST COMPLETE\n");
    MPI.Finalize();
  }
}
