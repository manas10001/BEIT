package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_intersect {

  public static void main(String args[]) throws Exception {
    try {
      Group_intersect a = new Group_intersect(args);
    }
    catch (Exception e) {
    }
  }

  public Group_intersect() {
  }

  public Group_intersect(String[] args) throws Exception {

    MPI.Init(args);
    mpi.Group grp = MPI.COMM_WORLD.Group();
    int me = grp.Rank();
    int tasks = grp.Size();

    if (tasks < 8) {
      if (me == 0)
	System.out.println("group->Group_intersect: MUST RUN with 8 tasks");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    int[] incl1 = { 4, 0, 5, 6 };
    int[] incl2 = { 6, 7, 4 };
    mpi.Group grp1 = grp.Incl(incl1);
    mpi.Group grp2 = grp.Incl(incl2);

    if (grp1.Size() != 4) {
      System.out.println("Error(1): ?|?|?|? ");
    } else if (grp2.Size() != 3) {
      System.out.println("Error(2): ?|?|?|? ");
    }

    mpi.Group grp3 = mpi.Group.Intersection(grp1, grp2);

    if (grp3.Size() != 2) {
      System.out.println("Error(3): ");
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 0)
      System.out.println("Group_intersect TEST COMPLETED");

    MPI.Finalize();
  }

}
