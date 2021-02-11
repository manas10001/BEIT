package mpi.comm;

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

 ****************************************************************************/

import mpi.*;

public class compare {
  static public void main(String[] args) throws Exception {
    try {
      compare a = new compare(args);
    }
    catch (Exception e) {
    }
  }

  public compare() {
  }

  public compare(String[] args) throws Exception {

    Intracomm comm1, comm2;
    int me, result, color, key;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();

    comm1 = (Intracomm) MPI.COMM_WORLD.clone();

    result = Comm.Compare(comm1, comm1);
    if (result != MPI.IDENT)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.IDENT) + "(MPI_IDENT)");

    result = Comm.Compare(MPI.COMM_WORLD, comm1);
    if (result != MPI.CONGRUENT)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.CONGRUENT) + "(MPI_CONGRUENT)");

    color = 1;
    key = -me;
    comm2 = comm1.Split(color, key);
    result = Comm.Compare(comm1, comm2);
    if (result != MPI.SIMILAR)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.SIMILAR) + "(MPI_SIMILAR)");

    color = me;
    comm2 = comm1.Split(color, key);
    result = Comm.Compare(comm1, comm2);
    if (result != MPI.UNEQUAL)
      System.out.println("ERROR in MPI_Comm_compare, result = " + result
	  + ", should be " + (MPI.UNEQUAL) + "(MPI_UNEQUAL)");

    MPI.COMM_WORLD.Barrier();

    if (me == 0)
      System.out.println("Compare TEST COMPLETE");

    MPI.Finalize();
  }
}
