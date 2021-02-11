package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_diff {

  public static void main(String args[]) throws Exception {
    try {
      Group_diff a = new Group_diff(args);
    }
    catch (Exception e) {
    }
  }

  public Group_diff() {
  }

  public Group_diff(String[] args) throws Exception {
    MPI.Init(args);
    mpi.Group grp = MPI.COMM_WORLD.Group();
    int me = grp.Rank();
    int tasks = grp.Size();

    if (tasks < 8) {
      if (me == 0)
	System.out.println("group->Group_diff: MUST RUN with 8 tasks");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    int[] incl1 = { 4, 0, 5, 6 };
    int[] incl2 = { 6, 7, 4 };
    mpi.Group grp1 = grp.Incl(incl1);
    mpi.Group grp2 = grp.Incl(incl2);
    mpi.Group grp3 = null;
    grp3 = mpi.Group.Difference(grp1, grp2);

    if (grp3.Size() != 2) {
      System.out.println(" Error(1): ....");
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 0)
      System.out.println("Group_diff TEST COMPLETED");

    MPI.Finalize();
  }
}
