package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class Isend_eager {
  int DATA_SIZE = 100;

  public Isend_eager() {
  }

  public Isend_eager(String args[]) throws Exception {

    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();

    Request req[] = new Request[8];

    int intArray[] = new int[DATA_SIZE];
    float floatArray[] = new float[DATA_SIZE];
    double doubleArray[] = new double[DATA_SIZE];
    long longArray[] = new long[DATA_SIZE];
    boolean booleanArray[] = new boolean[DATA_SIZE];
    short shortArray[] = new short[DATA_SIZE];
    char charArray[] = new char[DATA_SIZE];
    byte byteArray[] = new byte[DATA_SIZE];

    int intReadArray[] = new int[DATA_SIZE];
    float floatReadArray[] = new float[DATA_SIZE];
    double doubleReadArray[] = new double[DATA_SIZE];
    long longReadArray[] = new long[DATA_SIZE];
    boolean booleanReadArray[] = new boolean[DATA_SIZE];
    short shortReadArray[] = new short[DATA_SIZE];
    char charReadArray[] = new char[DATA_SIZE];
    byte byteReadArray[] = new byte[DATA_SIZE];

    for (int i = 0; i < intArray.length; i++) {
      intArray[i] = i + 1;
      floatArray[i] = i + 11;
      doubleArray[i] = i + 11.11;
      longArray[i] = i + 11;
      booleanArray[i] = true;
      shortArray[i] = 1;
      charArray[i] = 's';
      byteArray[i] = 's';

      intReadArray[i] = 3;
      floatReadArray[i] = i + 19;
      doubleReadArray[i] = i + 99.11;
      longReadArray[i] = i + 9;
      shortReadArray[i] = 2;
      booleanReadArray[i] = false;
      charReadArray[i] = 'x';
      byteReadArray[i] = 'x';
    }

    if (MPI.COMM_WORLD.Rank() == 0) {
      for (int i = 0; i < 100; i = i + 8) {

	req[0] = MPI.COMM_WORLD.Isend(intArray, 0, DATA_SIZE, MPI.INT, 1, i);
	req[1] = MPI.COMM_WORLD.Isend(byteArray, 0, DATA_SIZE, MPI.BYTE, 1,
	    i + 1);
	req[2] = MPI.COMM_WORLD.Isend(charArray, 0, DATA_SIZE, MPI.CHAR, 1,
	    i + 2);
	req[3] = MPI.COMM_WORLD.Isend(doubleArray, 0, DATA_SIZE, MPI.DOUBLE, 1,
	    i + 3);
	req[4] = MPI.COMM_WORLD.Isend(longArray, 0, DATA_SIZE, MPI.LONG, 1,
	    i + 4);
	req[5] = MPI.COMM_WORLD.Isend(booleanArray, 0, DATA_SIZE, MPI.BOOLEAN,
	    1, i + 5);
	req[6] = MPI.COMM_WORLD.Isend(shortArray, 0, DATA_SIZE, MPI.SHORT, 1,
	    i + 6);
	req[7] = MPI.COMM_WORLD.Isend(floatArray, 0, DATA_SIZE, MPI.FLOAT, 1,
	    i + 7);

	for (int j = 0; j < 8; j++) {
	  Status st = req[j].Wait();
	  // System.out.println("status.tag(s) "+st.tag);
	  // System.out.println("status.source(s) "+st.source);
	}
	// System.out.println("Send Completed \n\n");
      }

    } else if (MPI.COMM_WORLD.Rank() == 1) {
      // System.out.println("sleeping ");
      // try { Thread.currentThread().sleep(10000); }catch(Exception e){}
      // System.out.println("waking-up");

      for (int i = 0; i < 1000; i = i + 8) {
	// System.out.println(" ** Recv calling ** ");
	req[0] = MPI.COMM_WORLD
	    .Irecv(intReadArray, 0, DATA_SIZE, MPI.INT, 0, i);
	req[1] = MPI.COMM_WORLD.Irecv(byteReadArray, 0, DATA_SIZE, MPI.BYTE, 0,
	    i + 1);
	req[2] = MPI.COMM_WORLD.Irecv(charReadArray, 0, DATA_SIZE, MPI.CHAR, 0,
	    i + 2);
	req[3] = MPI.COMM_WORLD.Irecv(doubleReadArray, 0, DATA_SIZE,
	    MPI.DOUBLE, 0, i + 3);
	req[4] = MPI.COMM_WORLD.Irecv(longReadArray, 0, DATA_SIZE, MPI.LONG, 0,
	    i + 4);
	req[5] = MPI.COMM_WORLD.Irecv(booleanReadArray, 0, DATA_SIZE,
	    MPI.BOOLEAN, 0, i + 5);
	req[6] = MPI.COMM_WORLD.Irecv(shortReadArray, 0, DATA_SIZE, MPI.SHORT,
	    0, i + 6);
	req[7] = MPI.COMM_WORLD.Irecv(floatReadArray, 0, DATA_SIZE, MPI.FLOAT,
	    0, i + 7);

	for (int j = 0; j < 8; j++) {
	  Status status = req[j].Wait();
	  // System.out.println("status.tag(r) "+status.tag);
	  // System.out.println("status.source(r) "+status.source);
	}
	// System.out.println(" ** Recv completed ** ");

	if (Arrays.equals(intArray, intReadArray)
	    && Arrays.equals(floatArray, floatReadArray)
	    && Arrays.equals(doubleArray, doubleReadArray)
	    && Arrays.equals(longArray, longReadArray)
	    && Arrays.equals(shortArray, shortReadArray)
	    && Arrays.equals(charArray, charReadArray)
	    && Arrays.equals(byteArray, byteReadArray)
	    && Arrays.equals(booleanArray, booleanReadArray)) {
	  /*
	   * System.out.println("\n#################"+ "\n <<<<PASSED>>>> "+
	   * "\n################");
	   */
	} else {
	  System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	      + "\n################");
	}
      }
    }

    MPI.COMM_WORLD.Barrier();
    if (MPI.COMM_WORLD.Rank() == 0) {
      System.out.println("Isend_eager TEST Completed");
    }
    try {
      MPI.Finalize();
    }
    catch (Exception e) {
    }

  }

  public static void main(String args[]) throws Exception {
    Isend_eager test = new Isend_eager(args);
  }
}
