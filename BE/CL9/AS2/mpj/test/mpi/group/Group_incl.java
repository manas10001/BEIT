package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_incl {

  public static void main(String args[]) throws Exception {
    try {
      Group_incl a = new Group_incl(args);
    }
    catch (Exception e) {
    }
  }

  public Group_incl() {
  }

  public Group_incl(String[] args) throws Exception {
    MPI.Init(args);
    mpi.Group grp = MPI.COMM_WORLD.Group();
    int me = grp.Rank();
    int tasks = grp.Size();

    if (tasks < 8) {
      if (me == 0)
	System.out.println("group->Group_incl: MUST RUN with 8 tasks");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    int[] incl1 = { 4, 0, 5 };
    mpi.Group ngrp = grp.Incl(incl1);
    int new_me = ngrp.Rank();

    if (me == 0) {
      if (new_me != 0 && new_me != 1 && new_me != 2) {
	System.out.println("Error(1): ");
      }
    } else if (me == 1) {
      if (new_me != -1) {
	System.out
	    .println("Error(2): This process <" + me + ">is not in the new "
		+ "group and its rank should be -1, instead it is <" + new_me
		+ ">");
      }
    } else if (me == 2) {
      if (new_me != -1) {
	System.out
	    .println("Error(3): This process <" + me + ">is not in the new "
		+ "group and its rank should be -1, instead it is <" + new_me
		+ ">");
      }
    } else if (me == 3) {
      if (new_me != -1) {
	System.out
	    .println("Error(4): This process <" + me + ">is not in the new "
		+ "group and its rank should be -1, instead it is <" + new_me
		+ ">");
      }
    } else if (me == 4) {
      if (new_me != 0 && new_me != 1 && new_me != 2) {
	System.out.println("Error(5): ");
      }
    } else if (me == 5) {
      if (new_me != 0 && new_me != 1 && new_me != 2) {
	System.out.println("Error(6): ");
      }
    } else if (me == 6) {
      if (new_me != -1) {
	System.out
	    .println("Error(7): This process <" + me + ">is not in the new "
		+ "group and its rank should be -1, instead it is <" + new_me
		+ ">");
      }
    } else if (me == 7) {
      if (new_me != -1) {
	System.out
	    .println("Error(8): This process <" + me + ">is not in the new "
		+ "group and its rank should be -1, instead it is <" + new_me
		+ ">");
      }
    }
    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Group_incl TEST COMPLETED");

    MPI.Finalize();
  }

}
