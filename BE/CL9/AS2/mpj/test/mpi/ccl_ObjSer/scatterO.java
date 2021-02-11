package mpi.ccl_ObjSer;

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
 11/16/98
 ****************************************************************************/

import mpi.*;

public class scatterO {
  static public void main(String[] args) throws Exception {
    try {
      scatterO c = new scatterO(args);
    }
    catch (Exception e) {
    }
  }

  public scatterO() {
  }

  public scatterO(String[] args) throws Exception {

    final int MAXLEN = 1000;

    int root, i = 0, j, k;
    test out[] = new test[MAXLEN * 64];
    test in[] = new test[MAXLEN];
    int myself, tasks;

    for (int l = 0; l < MAXLEN; l++)
      in[l] = new test();
    for (int l = 0; l < MAXLEN * 64; l++)
      out[l] = new test();

    MPI.Init(args);
    myself = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    for (j = 1, root = 0; j <= MAXLEN; j *= 10, root = (root + 1) % tasks) {
      if (myself == root)
	for (i = 0; i < j * tasks; i++)
	  out[i].a = i;

      MPI.COMM_WORLD.Scatter(out, 0, j, MPI.OBJECT, in, 0, j, MPI.OBJECT, root);

      for (k = 0; k < j; k++) {
	if (in[k].a != k + myself * j) {
	  System.out.println("task " + myself + ":" + "bad answer ("
	      + (in[k].a) + ") at index " + k + " of " + j + "(should be "
	      + (k + myself * j) + ")");
	  break;
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("ScatterO TEST COMPLETE");
    MPI.Finalize();
  }
}
