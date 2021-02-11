package mpi.dtyp;

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
 09/10/99

 ****************************************************************************
 */

import mpi.*;

public class zero1 {
  public static void main(String args[]) throws Exception {
    try {
      zero1 c = new zero1(args);
    }
    catch (Exception e) {
    }
  }

  public zero1() {
  }

  public zero1(String[] args) throws Exception {

    int myself, tasks;

    int ii[] = new int[1];
    int numtasks, me;
    int count1, count2, count3;
    int len[] = new int[0];
    int disp[] = new int[0];
    Datatype type[] = new Datatype[0];

    MPI.Init(args);
    Datatype newtype;
    Status status;

    myself = MPI.COMM_WORLD.Rank();
    numtasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    if ((numtasks > 2) && (me > 1)) {
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    } else {
      newtype = Datatype.Struct(len, disp, type);
      newtype.Commit();

      if (myself == 0) {
	ii[0] = 2;
	MPI.COMM_WORLD.Send(ii, 0, 100, newtype, 1, 0);
      } else if (myself == 1) {
	ii[0] = 0;
	status = MPI.COMM_WORLD.Recv(ii, 0, 100, newtype, 0, 0);
	if (ii[0] != 0)
	  System.out.println("ERROR!");

	count1 = status.Get_count(newtype);
	count2 = status.Get_elements(newtype);

	if ((count1 == 100) && (count2 == MPI.UNDEFINED))
	  System.out.println("Success\n");
	else
	  System.out.println("Should be 100, MPI.UNDEFINED but is " + count1
	      + ", " + count2);
      }

      if (myself == 1)
	System.out.println("zero1 TEST COMPLETE");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
    }
  }
}
