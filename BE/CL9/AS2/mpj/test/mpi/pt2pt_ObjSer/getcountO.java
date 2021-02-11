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
 08/3/98
 ****************************************************************************
 */

import mpi.*;
import java.io.*;

public class getcountO {
  static public void main(String[] args) throws Exception {
    try {
      getcountO c = new getcountO(args);
    }
    catch (Exception e) {
    }
  }

  public getcountO() {
  }

  public getcountO(String[] args) throws Exception {
    int me, count, i, j;

    int datatest[][] = new int[7][4];
    int recdata[][] = new int[7][4];
    Status status;

    for (i = 0; i < 7; i++)
      for (j = 0; j < 4; j++) {
	datatest[i][j] = j + i * 4;
	recdata[i][j] = 0;
      }
    MPI.Init(args);

    me = MPI.COMM_WORLD.Rank();

    if (me == 0)
      MPI.COMM_WORLD.Send(datatest, 0, 7, MPI.OBJECT, 1, 1);

    else if (me == 1) {
      status = MPI.COMM_WORLD.Recv(recdata, 0, 7, MPI.OBJECT, 0, 1);

      for (i = 0; i < 7; i++)
	for (j = 0; j < 4; j++) {
	  if (recdata[i][j] != datatest[i][j])
	    System.out.println("Recived data  " + recdata[i][j] + " at index ["
		+ i + "][" + j + "] should be : " + datatest[i][j]);
	}
      count = status.Get_count(MPI.OBJECT);
      if (count != 7)
	System.out.println("ERROR(4) in MPI_Get_count, count = " + count
	    + ", should be 7");
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 1)
      System.out.println("Get_countO TEST COMPLETE.");
    MPI.Finalize();
  }
}
