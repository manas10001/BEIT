package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_union {

  public static void main(String args[]) throws Exception {
    try {
      Group_union a = new Group_union(args);
    }
    catch (Exception e) {
    }
  }

  public Group_union() {
  }

  public Group_union(String[] args) throws Exception {
    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();
    int size = MPI.COMM_WORLD.Size();

    if (size < 8) {
      if (me == 0) {
	System.out.println("group->Group_union: MUST HAVE 8 TASKS");
      }
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    mpi.Group grp = MPI.COMM_WORLD.Group();
    me = grp.Rank();
    int[] incl1 = { 4, 0, 5 };
    int[] incl2 = { 6, 7, 4 };
    mpi.Group grp1 = grp.Incl(incl1);
    mpi.Group grp2 = grp.Incl(incl2);
    mpi.Group grp3 = null;
    grp3 = mpi.Group.Union(grp1, grp2);

    if (grp3.Size() != 5) {
      System.out.println("Error(1): the new group should've five processes "
	  + "in it ... ");
    }

    MPI.COMM_WORLD.Barrier();

    if (me == 0)
      System.out.println("Group_union TEST COMPLETED");

    MPI.Finalize();
  }
}
