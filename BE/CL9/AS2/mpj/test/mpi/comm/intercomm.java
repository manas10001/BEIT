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

public class intercomm {

  static int newsize, me, size, color, key, local_lead, remote_lead, newme,
      othersum;
  static int mebuf[] = new int[1];
  static int sum[] = new int[1];
  static int newsum[] = new int[1];
  static boolean flag;
  static Intracomm comm, mergecomm;
  static Intercomm intercomm;
  static Status status;
  static Group newgid;

  static void inter_tests() throws MPIException {
    flag = intercomm.Test_inter();
    if (flag != true)
      System.out.println("ERROR in MPI_Comm_test_inter: flag = " + flag
	  + ", should be 1");

    newsize = intercomm.Remote_size();
    if (newsize != size / 2)
      System.out.println("ERROR in MPI_Comm_remote_size: size = " + newsize
	  + ", should be " + (size / 2));

    newgid = intercomm.Remote_group();
    newsize = newgid.Size();
    if (newsize != size / 2)
      System.out.println("ERROR in MPI_Comm_remote_group: size = " + newsize
	  + ", should be " + (size / 2));

    newsum[0] = sum[0];
    status = intercomm.Sendrecv_replace(newsum, 0, 1, MPI.INT, newme, 7, newme,
	7);
    othersum = size / 2 * (size / 2 - 1);
    if (me % 2 == 0)
      othersum += size / 2;
    if (othersum != newsum[0])
      System.out.println("ERROR in Intercomm_create, sum = " + othersum
	  + ", should be " + newsum);

    boolean high = (color == 1) ? true : false;
    Intracomm mergecomm = intercomm.Merge(high);
    mebuf[0] = me;
    mergecomm.Allreduce(mebuf, 0, newsum, 0, 1, MPI.INT, MPI.SUM);
    if (newsum[0] != size * (size - 1) / 2)
      System.out.println("ERROR in MPI_Intercomm_merge: sum = " + newsum[0]
	  + ", should be " + size * (size - 1) / 2);

  }

  // //////////////////////////////////////////////////////////

  public static void main(String args[]) throws Exception {
    try {
      intercomm a = new intercomm(args);
    }
    catch (Exception e) {
    }
  }

  public intercomm() {
  }

  public intercomm(String[] args) throws Exception {

    Intracomm comm1, comm2;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();

    if (size % 2 == 1) {
      if (me == 0)
	System.out.println("comm->intercomm: MUST RUN WITH EVEN NUMBER"
	    + "OF TASKS");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    key = me;
    color = me % 2;
    comm = MPI.COMM_WORLD.Split(color, key);
    comm1 = comm;
    flag = comm.Test_inter();
    if (flag != false)
      System.out.println("ERROR in MPI_Comm_test_inter: flag = " + flag
	  + ", should be false");
    newme = comm.Rank();
    mebuf[0] = me;
    comm.Allreduce(mebuf, 0, sum, 0, 1, MPI.INT, MPI.SUM);
    local_lead = 0;
    // local_lead = (color==0) ? 0 : 1;
    remote_lead = (color == 1) ? 0 : 1;

    intercomm = MPI.COMM_WORLD.Create_intercomm(comm, local_lead, remote_lead,
	5);

    inter_tests();
    /*
     * Intercomm incomm = (Intercomm) intercomm.clone(); intercomm = incomm;
     * inter_tests();
     * 
     * MPI.COMM_WORLD.Barrier();
     */
    if (me == 0)
      System.out.println("Intercomm TEST COMPLETE");
    MPI.Finalize();
  }
}
