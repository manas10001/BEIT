package mpi.pt2pt;

import mpi.*;
import java.nio.ByteBuffer;

public class SimpleBsend {
  static public void main(String[] args) throws Exception {
    try {
      SimpleBsend c = new SimpleBsend(args);
    }
    catch (Exception e) {
    }
  }

  public SimpleBsend() {
  }

  public SimpleBsend(String[] args) throws Exception {

    int len, tsks, me, i, size, rc;
    final int A1 = 1000;
    Status status;

    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    size = MPI.COMM_WORLD.Size();

    if (size > 2) {
      if (me == 0)
	System.out.println("SimpleBsend: Must run with 2 tasks!");
      MPI.Finalize();
      return;
    }

    // System.out.println(MPI.Get_processor_name());

    int data1[] = new int[A1];
    int intsize = 4;

    // mpi.Buffer buf1 = new mpi.Buffer(MPI.COMM_WORLD.Pack_size( A1, MPI.INT)
    // );
    ByteBuffer buf1 = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(A1,
	MPI.INT) + MPI.BSEND_OVERHEAD);

    if (me == 0) {
      for (int j = 0; j < data1.length; j++) {
	data1[j] = j;
      }
      MPI.Buffer_attach(buf1);
      MPI.COMM_WORLD.Bsend(data1, 0, A1, MPI.INT, 1, 1);
      MPI.Buffer_detach();
    } else if (me == 1) {
      for (int j = 0; j < data1.length; j++) {
	data1[j] = 0;
      }
      MPI.COMM_WORLD.Recv(data1, 0, A1, MPI.INT, 0, 1);
      for (int j = 0; j < data1.length; j++) {
	if (data1[j] != j) {
	  System.out.println("Error at index " + j + " expected value" + j
	      + "actual value" + data1[j]);
	  break;
	}
      }

    }

    if (me == 1)
      System.out.println("SimpleBsend TEST COMPLETE" + me);
    MPI.Finalize();
  }

}
