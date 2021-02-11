package mpi.pt2pt;

import mpi.*;
import java.nio.ByteBuffer;

//no detach ..what's the problem with these test-cases! !!
public class isend2 {

  static int tasks, bytes, i;
  static int rank;
  static int DATA_SIZE = 1000;
  static int data[] = new int[DATA_SIZE];
  static Request req = null;
  static Status stats = null;

  public isend2() {
  }

  public isend2(String[] args) throws Exception {

    MPI.Init(args);
    rank = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();

    if (rank == 0) {
      System.out.println("> Testing Isend/Irecv...");
      for (i = 0; i < DATA_SIZE; i++)
	data[i] = -1;
      req = MPI.COMM_WORLD.Isend(data, 0, DATA_SIZE, MPI.INT, 1, 1);
      stats = req.Wait();
    } else if (rank == 1) {
      req = MPI.COMM_WORLD.Irecv(data, 0, DATA_SIZE, MPI.INT, 0, 1);
      stats = req.Wait();
      int num = stats.Get_count(MPI.INT);
      int els = stats.Get_elements(MPI.INT);

      for (int j = 0; j < DATA_SIZE; j++) {
	if (data[j] != -1) {
	  System.out.println("ERROR(0): incorrect data");
	}
      }
      if (num != DATA_SIZE) {
	System.out.println("ERROR(1): received ints are not equal "
	    + "to num sent");
      }
      if (stats.source != 0) {
	System.out.println("ERROR(2): source not equal to 0");
      }
      if (stats.tag != 1) {
	System.out.println("ERROR(3): tag not equal to 1");
      }
      if (els != DATA_SIZE * 4) {
	System.out.println("ERROR(4): received bytes are not equal "
	    + "to bytes sent");
      }

      for (i = 0; i < DATA_SIZE; i++)
	data[i] = 0;

    }

    if (rank == 0) {
      System.out.println("> Testing Ibsend/Irecv...");
      // buf = new mpi.Buffer(
      // MPI.COMM_WORLD.Pack_size (DATA_SIZE, MPI.INT) );
      ByteBuffer buf = ByteBuffer.allocateDirect(MPI.COMM_WORLD.Pack_size(
	  DATA_SIZE, MPI.INT));
      MPI.Buffer_attach(buf);
      for (i = 0; i < DATA_SIZE; i++)
	data[i] = -1;
      req = MPI.COMM_WORLD.Ibsend(data, 0, DATA_SIZE, MPI.INT, 1, 1);
      stats = req.Wait();
    } else if (rank == 1) {
      req = MPI.COMM_WORLD.Irecv(data, 0, DATA_SIZE, MPI.INT, 0, 1);
      stats = req.Wait();
      int num = stats.Get_count(MPI.INT);
      int els = stats.Get_elements(MPI.INT);

      for (int j = 0; j < DATA_SIZE; j++) {
	if (data[j] != -1) {
	  System.out.println("ERROR(5): incorrect data");
	}
      }
      if (num != DATA_SIZE) {
	System.out.println("ERROR(6): received ints are not equal "
	    + "to num sent");
      }
      if (stats.source != 0) {
	System.out.println("ERROR(7): source not equal to 0");
      }
      if (stats.tag != 1) {
	System.out.println("ERROR(8): tag not equal to 1");
      }
      if (els != DATA_SIZE * 4) {
	System.out.println("ERROR(9): received bytes are not equal "
	    + "to bytes sent");
      }

      for (i = 0; i < DATA_SIZE; i++)
	data[i] = 0;
    }

    if (rank == 0) {
      System.out.println("> Testing Irsend/Irecv...");
      for (i = 0; i < DATA_SIZE; i++)
	data[i] = -1;
      req = MPI.COMM_WORLD.Irsend(data, 0, DATA_SIZE, MPI.INT, 1, 1);
      stats = req.Wait();
    } else if (rank == 1) {
      req = MPI.COMM_WORLD.Irecv(data, 0, DATA_SIZE, MPI.INT, 0, 1);
      stats = req.Wait();
      int num = stats.Get_count(MPI.INT);
      int els = stats.Get_elements(MPI.INT);

      for (int j = 0; j < DATA_SIZE; j++) {
	if (data[j] != -1) {
	  System.out.println("ERROR(10): incorrect data");
	}
      }
      if (num != DATA_SIZE) {
	System.out.println("ERROR(11): received ints are not equal "
	    + "to num sent");
      }
      if (stats.source != 0) {
	System.out.println("ERROR(12): source not equal to 0");
      }
      if (stats.tag != 1) {
	System.out.println("ERROR(13): tag not equal to 1");
      }
      if (els != DATA_SIZE * 4) {
	System.out.println("ERROR(14): received bytes are not equal "
	    + "to bytes sent");
      }

      for (i = 0; i < DATA_SIZE; i++)
	data[i] = 0;
    }

    if (rank == 0) {
      System.out.println("> Testing Issend/Irecv...");
      for (i = 0; i < DATA_SIZE; i++)
	data[i] = -1;
      req = MPI.COMM_WORLD.Issend(data, 0, DATA_SIZE, MPI.INT, 1, 1);
      stats = req.Wait();
    } else if (rank == 1) {
      req = MPI.COMM_WORLD.Irecv(data, 0, DATA_SIZE, MPI.INT, 0, 1);
      stats = req.Wait();
      int num = stats.Get_count(MPI.INT);
      int els = stats.Get_elements(MPI.INT);

      for (int j = 0; j < DATA_SIZE; j++) {
	if (data[j] != -1) {
	  System.out.println("ERROR(15): incorrect data");
	}
      }
      if (num != DATA_SIZE) {
	System.out.println("ERROR(16): received ints are not equal "
	    + "to num sent");
      }
      if (stats.source != 0) {
	System.out.println("ERROR(17): source not equal to 0");
      }
      if (stats.tag != 1) {
	System.out.println("ERROR(18): tag not equal to 1");
      }
      if (els != DATA_SIZE * 4) {
	System.out.println("ERROR(19): received bytes are not equal "
	    + "to bytes sent");
      }

    }

    MPI.COMM_WORLD.Barrier();
    if (rank == 1)
      System.out.println("Isend2 TEST COMPLETE\n");
    MPI.Finalize();

  }
}
