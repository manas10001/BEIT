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

public class zero5 {
  public static void main(String args[]) throws Exception {
    try {
      zero5 c = new zero5(args);
    }
    catch (Exception e) {
    }
  }

  public zero5() {
  }

  public zero5(String[] args) throws Exception {

    final int MSZ = 10;
    int myself, tasks;

    int i;
    int ii[] = new int[MSZ];
    int check[] = new int[MSZ];
    int numtasks, me;
    int len;
    int error = 0;
    int count1, count2, count3;
    int aob[] = new int[3];
    int aod[] = new int[3];

    MPI.Init(args);

    Datatype newtype;
    Datatype aot[] = new Datatype[3];
    Status status;
    myself = MPI.COMM_WORLD.Rank();
    numtasks = MPI.COMM_WORLD.Size();
    me = MPI.COMM_WORLD.Rank();

    if ((numtasks < 2)) {
      System.out.println("this testcase requires 2 tasks.");
      MPI.COMM_WORLD.Abort(me);
    }

    if ((numtasks > 2) && (me > 1)) {
      System.out.println("Testcase uses two tasks, extraneous task#" + me
	  + " exited.");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      // System.exit(0);
    } else {

      for (i = 0; i < MSZ; i++) {
	check[i] = i;
      }
      check[1] = -1;
      check[5] = -1;
      check[8] = -1;
      check[9] = -1;
      aot[0] = MPI.INT;
      aob[0] = 0;
      aod[0] = 2;
      aot[1] = MPI.INT;
      aob[1] = 1;
      aod[1] = 0;
      aot[2] = MPI.INT;
      aob[2] = 2;
      aod[2] = 2;

      newtype = Datatype.Struct(aob, aod, aot);
      newtype.Commit();

      if (myself == 0) {
	for (i = 0; i < MSZ; i++) {
	  ii[i] = i;
	}
	MPI.COMM_WORLD.Send(ii, 0, 2, newtype, 1, 0);
      } else if (myself == 1) {
	for (i = 0; i < MSZ; i++) {
	  ii[i] = -1;
	}
	status = MPI.COMM_WORLD.Recv(ii, 0, 2, newtype, 0, 0);
	for (i = 0; i < MSZ; i++) {
	  if (ii[i] != check[i])
	    error++;
	}
	if (error > 0) {
	  System.out.println("FAILURE: Results below.");
	  for (i = 0; i < MSZ; i++) {
	    System.out.println("check[" + i + "]=" + check[i]);
	    System.out.println("ii[" + i + "]=" + ii[i]);
	  }
	} else {
	  System.out.println("SUCCESS with sent message.");
	}

	count1 = status.Get_count(newtype);
	count2 = status.Get_elements(newtype);
	if ((count1 == 2) && (count2 == 6))
	  System.out.println("Success with Get_count & Get_elements.");
	else
	  System.out.println("Should be 2, 6 but is " + count1 + ", " + count2);

      }

      if (myself == 1) {
	System.out.println(" zero5 TEST COMPLETED");
      }
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
    } // end else

  }
}
