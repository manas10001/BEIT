package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_com {

  public static void main(String args[]) throws Exception {
    try {
      Group_com a = new Group_com(args);
    }
    catch (Exception e) {
    }
  }

  public Group_com() {
  }

  public Group_com(String[] args) throws Exception {
    MPI.Init(args);
    int res = -1;
    mpi.Group grp = MPI.COMM_WORLD.Group();
    int me = MPI.COMM_WORLD.Rank();
    int tasks = MPI.COMM_WORLD.Size();

    if (tasks < 8) {
      if (me == 0)
	System.out.println("group->Group_com: MUST RUN with 8 tasks");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    int[] incl1 = { 4, 0, 5, 6 };
    int[] incl2 = { 6, 7, 4 };
    int[] incl3 = { 6, 0, 5, 4 };

    mpi.Group grp0 = grp.Incl(incl1);
    mpi.Group grp1 = grp.Incl(incl1);
    mpi.Group grp2 = grp.Incl(incl2);
    mpi.Group grp3 = MPI.GROUP_EMPTY;
    mpi.Group grp4 = grp.Incl(incl3);
    mpi.Group grp5 = null;

    // if(me == 0) {
    res = mpi.Group.Compare(grp0, grp1);
    if (res != MPI.IDENT) {
      System.out.println("ERROR(1): grp1{4 0 5 6} and grp0{4 0 5 6} "
	  + "are IDENTICAL");
    }

    res = mpi.Group.Compare(grp1, grp2);
    if (res != MPI.UNEQUAL) {
      System.out.println("res<" + res + ">");
      System.out.println("unequal<" + MPI.UNEQUAL + ">");
      System.out.println("ERROR(2): grp1{4 0 5 6} and grp2{6 7 4} "
	  + "are UNEQUAL");
    }

    res = mpi.Group.Compare(grp4, grp1);
    if (res != MPI.SIMILAR) {
      System.out.println("ERROR(3): grp1{4 0 5 6} and grp4{6 0 5 4} "
	  + "are SIMILAR");
    }

    res = mpi.Group.Compare(grp3, grp1);
    if (res != MPI.UNEQUAL) {
      System.out.println("ERROR(4): grp1{4 0 5 6} and grp3{  " + "are UNEQUAL");
    }

    // these are empty groups and generate an exception ..i could have easily
    // made them return IDENT ...but i dont think they fit well into the
    // definition of IDENT ...
    // res = mpi.Group.Compare(grp3,grp3);
    // if(res != MPI.IDENT) {
    // System.out.println("ERROR(3): grp3{ } and grp3{ } "+
    // "are IDENTICAL");
    // }

    // this test should generate an exception which it does ...
    // res = mpi.Group.Compare(grp5, grp5);

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Group_com TEST COMPLETED");

    // }

    MPI.Finalize();
  }
}
