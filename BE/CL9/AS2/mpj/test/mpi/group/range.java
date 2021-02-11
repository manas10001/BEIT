package mpi.group;

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

public class range {
  static public void main(String[] args) throws Exception {
    try {
      range a = new range(args);
    }
    catch (Exception e) {
    }
  }

  public range() {
  }

  public range(String[] args) throws Exception {

    int i, size, myself;
    int ranks2_1[] = new int[2];
    int ranks2_2[] = new int[2];
    int ranks3_1[] = new int[3];
    int ranks3_2[] = new int[3];
    int ranks5_1[] = new int[5];
    int ranks5_2[] = new int[5];
    int ranks6_1[] = new int[6];
    int ranks6_2[] = new int[6];

    int ranges1[][] = new int[1][3];
    int ranges2[][] = new int[2][3];

    int cnt = 0;

    MPI.Init(args);

    mpi.Group group, newgroup;
    mpi.Group groups[] = new mpi.Group[20];

    myself = MPI.COMM_WORLD.Rank();

    group = MPI.COMM_WORLD.Group();

    groups[cnt++] = group;
    size = group.Size();

    if (size != 8) {
      if (myself == 0)
	System.out.println("MUST RUN WITH 8 TASKS");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    ranges2[0][0] = 1;
    ranges2[0][1] = 4;
    ranges2[0][2] = 1;
    ranges2[1][0] = 5;
    ranges2[1][1] = 8;
    ranges2[1][2] = 2;

    newgroup = group.Range_incl(ranges2);

    groups[cnt++] = newgroup;
    size = newgroup.Size();
    if (size != 6)
      System.out.println("ERROR: Size = " + size + ", should be 6");

    for (i = 0; i < 6; i++)
      ranks6_1[i] = i;
    ranks6_2 = mpi.Group.Translate_ranks(newgroup, ranks6_1, group);

    if (ranks6_2[0] != 1 || ranks6_2[1] != 2 || ranks6_2[2] != 3
	|| ranks6_2[3] != 4 || ranks6_2[4] != 5 || ranks6_2[5] != 7)
      System.out.println("ERROR: Wrong ranks " + ranks6_2[0] + ","
	  + ranks6_2[1] + "," + ranks6_2[2] + "," + ranks6_2[3] + ","
	  + ranks6_2[4] + "," + ranks6_2[5]);

    newgroup = group.Range_excl(ranges2);
    groups[cnt++] = newgroup;
    size = newgroup.Size();
    if (size != 2)
      System.out.println("ERROR: Size = " + size + ", should be 2");

    for (i = 0; i < 2; i++)
      ranks2_1[i] = i;
    ranks2_2 = mpi.Group.Translate_ranks(newgroup, ranks2_1, group);
    if (ranks2_2[0] != 0 || ranks2_2[1] != 6)
      System.out.println("ERROR: Wrong ranks " + ranks2_2[0] + ","
	  + ranks2_2[1]);

    ranges1[0][0] = 6;
    ranges1[0][1] = 0;
    ranges1[0][2] = -3;
    newgroup = group.Range_incl(ranges1);
    groups[cnt++] = newgroup;
    size = newgroup.Size();
    if (size != 3)
      System.out.println("ERROR: Size = " + size + ", should be 3");

    for (i = 0; i < 3; i++)
      ranks3_1[i] = i;
    ranks3_2 = mpi.Group.Translate_ranks(newgroup, ranks3_1, group);
    if (ranks3_2[0] != 6 || ranks3_2[1] != 3 || ranks3_2[2] != 0)
      System.out.println("ERROR: Wrong ranks " + ranks3_2[0] + ","
	  + ranks3_2[1] + "," + ranks3_2[2]);

    newgroup = group.Range_excl(ranges1);
    groups[cnt++] = newgroup;
    size = newgroup.Size();
    if (size != 5)
      System.out.println("ERROR: Size = " + size + ", should be 5");

    for (i = 0; i < 5; i++)
      ranks5_1[i] = i;
    ranks5_2 = mpi.Group.Translate_ranks(newgroup, ranks5_1, group);
    if (ranks5_2[0] != 1 || ranks5_2[1] != 2 || ranks5_2[2] != 4
	|| ranks5_2[3] != 5 || ranks5_2[4] != 7)
      System.out.println("ERROR: Wrong ranks " + ranks5_2[0] + ","
	  + ranks5_2[1] + "," + ranks5_2[2] + "," + ranks5_2[3] + ","
	  + ranks5_2[4]);

    MPI.COMM_WORLD.Barrier();
    if (myself == 0)
      System.out.println("Group Range TEST COMPLETE");
    MPI.Finalize();
  }
}
