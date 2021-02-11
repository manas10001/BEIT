package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_excl {

  public static void main(String args[]) throws Exception {
    try {
      Group_excl a = new Group_excl(args);
    }
    catch (Exception e) {
    }
  }

  public Group_excl() {
  }

  public Group_excl(String[] args) throws Exception {

    MPI.Init(args);
    mpi.Group grp = MPI.COMM_WORLD.Group();
    int me = grp.Rank();
    int tasks = grp.Size();

    if (tasks < 8) {
      if (me == 0)
	System.out.println("group->Group_excl: MUST RUN with 8 tasks");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    int[] excl = { 5, 6, 0, 2, 3 };
    mpi.Group ngrp = grp.Excl(excl);

    if (ngrp.Size() != 3) {
      System.out.println(" Error(1): ...");
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 0)
      System.out.println("Group_excl TEST COMPLETED");

    MPI.Finalize();
  }

}
