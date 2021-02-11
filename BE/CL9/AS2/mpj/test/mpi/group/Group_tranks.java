package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_tranks {

  public static void main(String args[]) throws Exception {
    try {
      Group_tranks a = new Group_tranks(args);
    }
    catch (Exception e) {
    }
  }

  public Group_tranks() {
  }

  public Group_tranks(String[] args) throws Exception {

    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();
    int tasks = MPI.COMM_WORLD.Size();

    if (tasks < 8) {
      if (me == 0)
	System.out.println("group->Group_tranks: MUST RUN 8 TASKS");

      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }
    mpi.Group grp = MPI.COMM_WORLD.Group();
    int[] incl1 = { 7, 6, 5, 4 };
    int[] ranks1 = { 0, 1, 2, 3 };
    mpi.Group grp1 = grp.Incl(incl1);
    int[] ranks2 = mpi.Group.Translate_ranks(grp1, ranks1, grp);

    if (java.util.Arrays.equals(incl1, ranks2)) {
      if (me == 0)
	System.out.println("Group_tranks TEST COMPLETED");
    } else {
      System.out.println("Error(1): Test failed");
      for (int i = 0; i < ranks1.length; i++) {
	System.out.println(" expected [" + i + "]=" + incl1[i]);
	System.out.println(" actual   [" + i + "]=" + ranks2[i]);
      }
    }

    MPI.COMM_WORLD.Barrier();

    MPI.Finalize();
  }

}
