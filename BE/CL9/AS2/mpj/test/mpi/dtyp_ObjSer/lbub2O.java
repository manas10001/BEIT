package mpi.dtyp_ObjSer;

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

public class lbub2O {
  static public void main(String[] args) throws Exception {
    try {
      lbub2O c = new lbub2O(args);
    }
    catch (Exception e) {
    }
  }

  public lbub2O() {
  }

  public lbub2O(String[] args) throws Exception {

    Datatype newtype, newtype2, newtype3, newtype4, newtype5;
    Datatype newtype6, newtype7, newtype8, newtype9;
    int aob[] = new int[3];
    int error = 0;
    int numtasks, me;
    int extent, lb, ub;
    int aod[] = new int[3];

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    numtasks = MPI.COMM_WORLD.Size();

    Datatype aot[] = new Datatype[3];

    if ((numtasks != 1) && (me != 0)) {
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    newtype = Datatype.Contiguous(4, MPI.OBJECT);
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

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }
}
