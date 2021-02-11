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
 03/22/98

 ****************************************************************************
 */

import mpi.*;

public class lbub2 {
  public static void main(String args[]) throws Exception {
    try {
      lbub2 c = new lbub2(args);
    }
    catch (Exception e) {
    }
  }

  public lbub2() {
  }

  public lbub2(String[] args) throws Exception {

    int error = 0;
    int numtasks, me;
    int extent, lb, ub;
    int aob[] = new int[3], aod[] = new int[3];
    int[] bob = new int[2], bod = new int[2];

    MPI.Init(args);

    Datatype newtype, newtype2, newtype3, newtype4, newtype5;
    Datatype newtype6, newtype7, newtype8, newtype9;
    Datatype aot[] = new Datatype[3];
    Datatype bot[] = new Datatype[2];

    me = MPI.COMM_WORLD.Rank();
    numtasks = MPI.COMM_WORLD.Size();

    if ((numtasks != 1) && (me != 0)) {
      // System.out.println
      // ("Testcase uses one task, extraneous task #"+me+" exited.");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      // System.exit(0);
    } else {

      newtype = Datatype.Contiguous(4, MPI.INT);
      newtype.Commit();

      aot[0] = newtype;
      aod[0] = 3;
      aob[0] = 1;
      aot[1] = MPI.UB;
      aod[1] = 100;
      aob[1] = 1;
      aot[2] = MPI.LB;
      aod[2] = 0;
      aob[2] = 1;
      newtype2 = Datatype.Struct(aob, aod, aot);
      newtype2.Commit();

      extent = newtype2.Extent();
      lb = newtype2.Lb();
      ub = newtype2.Ub();
      if ((extent != 100) | (lb != 0) | (ub != 100)) {
	error++;
	System.out.println("Should be: Extent = 100, lb = 0, ub = 100.");
	System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	    + ", ub = " + ub);
      }

      bot[0] = newtype;
      bod[0] = 65;
      bob[0] = 1;
      bot[1] = MPI.INT;
      bod[1] = 97;
      bob[1] = 1;
      newtype3 = Datatype.Struct(bob, bod, bot);
      newtype3.Commit();

      extent = newtype3.Extent();
      lb = newtype3.Lb();
      ub = newtype3.Ub();
      if ((extent != 33) | (lb != 65) | (ub != 98)) {
	error++;
	System.out.println("Should be: Extent = 33, lb = 65, ub = 98.");
	System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	    + ", ub = " + ub);
      }

      aot[0] = newtype;
      aod[0] = 32;
      aob[0] = 1;
      aot[1] = MPI.LB;
      aod[1] = 3;
      aob[1] = 1;
      aot[2] = MPI.UB;
      aod[2] = 94;
      aob[2] = 1;
      newtype4 = Datatype.Struct(aob, aod, aot);
      newtype4.Commit();

      extent = newtype4.Extent();
      lb = newtype4.Lb();
      ub = newtype4.Ub();
      if ((extent != 91) | (lb != 3) | (ub != 94)) {
	error++;
	System.out.println("Should be: Extent = 91, lb = 3, ub = 94.");
	System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	    + ", ub = " + ub);
      }

      aot[0] = newtype;
      aod[0] = 13;
      aob[0] = 2;
      aot[1] = MPI.LB;
      aod[1] = -3;
      aob[1] = 1;
      aot[2] = MPI.UB;
      aod[2] = 96;
      aob[2] = 1;
      newtype5 = Datatype.Struct(aob, aod, aot);
      newtype5.Commit();

      extent = newtype5.Extent();
      lb = newtype5.Lb();
      ub = newtype5.Ub();
      if ((extent != 99) | (lb != -3) | (ub != 96)) {
	error++;
	System.out.println("Should be: Extent = 99, lb = -3, ub = 96.");
	System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	    + ", ub = " + ub);
      }

      aot[0] = newtype;
      aod[0] = 5;
      aob[0] = 2;
      aot[1] = MPI.LB;
      aod[1] = -3;
      aob[1] = 1;
      aot[2] = MPI.UB;
      aod[2] = 86;
      aob[2] = 1;
      newtype6 = Datatype.Struct(aob, aod, aot);
      newtype6.Commit();

      extent = newtype6.Extent();
      lb = newtype6.Lb();
      ub = newtype6.Ub();
      if ((extent != 89) | (lb != -3) | (ub != 86)) {
	error++;
	System.out.println("Should be: Extent = 89, lb = -3, ub = 86.");
	System.out.println("Is:        Extent = " + extent + ", lb = " + lb
	    + ", ub = " + ub);
      }

      if (error == 0)
	System.out.println("Upper bound/lower bound/extent test passed.\n");
      else
	System.out.println("ERRORS in bounds/extent test.\n");

      System.out.println("lbub2 TEST COMPLETED");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
    }// end else

  }
}
