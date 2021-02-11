package mpi.group;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

public class Group_self {

  public static void main(String args[]) throws Exception {
    try {
      Group_self a = new Group_self(args);
    }
    catch (Exception e) {
    }
  }

  public Group_self() {
  }

  public Group_self(String[] args) throws Exception {
    MPI.Init(args);
    mpi.Group grp = MPI.COMM_WORLD.Group();
    int me = grp.Rank();
    int tasks = grp.Size();
    int data[] = new int[10];
    int rdata[] = new int[10];
    int src_dest = 0;
    int tag = 10;

    for (int i = 0; i < data.length; i++) {
      data[i] = i;
    }

    mpi.Request req = MPI.COMM_SELF.Isend(data, 0, 10, MPI.INT, src_dest, tag);
    MPI.COMM_SELF.Recv(rdata, 0, 10, MPI.INT, src_dest, tag);
    req.Wait();

    for (int i = 0; i < rdata.length; i++) {
      if (rdata[i] != i) {
	System.out.println("Error at index " + i + ": it is " + data[i]
	    + "while " + "we are expecting " + i);
	break;
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (me == 0)
      System.out.println("Group_self TEST COMPLETED");

    MPI.Finalize();
  }

}
