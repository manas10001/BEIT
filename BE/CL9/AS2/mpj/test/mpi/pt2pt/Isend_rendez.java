package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class Isend_rendez {

  // int DATA_SIZE=2600000;
  int DATA_SIZE = 26000;

  public Isend_rendez() {
  }

  public Isend_rendez(String args[]) throws Exception {
    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();
    Request rreq[] = new Request[8];
    Request sreq[] = new Request[8];

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
    }

    if (me == 0) {
      for (int i = 1; i < 10; i = i + 4) {

	for (int j = 0; j < intArray.length; j++) {
	  intReadArray[j] = 3;
	  floatReadArray[j] = j + 19;
	  doubleReadArray[j] = j + 99.11;
	  longReadArray[j] = j + 9;
	  shortReadArray[j] = 2;
	  booleanReadArray[j] = false;
	  charReadArray[j] = 'x';
	  byteReadArray[j] = 'x';
	}
	sreq[0] = MPI.COMM_WORLD.Isend(intArray, 0, DATA_SIZE, MPI.INT, 1, i);
	sreq[1] = MPI.COMM_WORLD.Isend(byteArray, 0, DATA_SIZE, MPI.BYTE, 1,
	    i + 1);
	// sreq[2]=MPI.COMM_WORLD.Isend(charArray,0,DATA_SIZE,MPI.CHAR,1,i+2);
	// sreq[3]=MPI.COMM_WORLD.Isend(doubleArray,0,DATA_SIZE,MPI.DOUBLE,1,i+3);

	/*
	 * sreq[4]=MPI.COMM_WORLD.Isend(longArray,0,DATA_SIZE,MPI.LONG,1,i+4);
	 * sreq[5]=MPI.COMM_WORLD.Isend(
	 * booleanArray,0,DATA_SIZE,MPI.BOOLEAN,1,i+5);
	 * sreq[6]=MPI.COMM_WORLD.Isend(shortArray,0,DATA_SIZE,MPI.SHORT,1,i+6
	 * );
	 * sreq[7]=MPI.COMM_WORLD.Isend(floatArray,0,DATA_SIZE,MPI.FLOAT,1,i+7
	 * );
	 */
	rreq[0] = MPI.COMM_WORLD.Irecv(intReadArray, 0, DATA_SIZE, MPI.INT, 1,
	    i);
	rreq[1] = MPI.COMM_WORLD.Irecv(byteReadArray, 0, DATA_SIZE, MPI.BYTE,
	    1, i + 1);
	// rreq[2]= MPI.COMM_WORLD.Irecv(
	// charReadArray,0,DATA_SIZE,MPI.CHAR,1,i+2);
	// rreq[3]= MPI.COMM_WORLD.Irecv(doubleReadArray,0,DATA_SIZE,
	// MPI.DOUBLE,1,i+3);
	/*
	 * //rreq[4]= MPI.COMM_WORLD.Irecv(longReadArray,0,
	 * DATA_SIZE,MPI.LONG,1,i+4); rreq[5]=
	 * MPI.COMM_WORLD.Irecv(booleanReadArray,0,DATA_SIZE,
	 * MPI.BOOLEAN,1,i+5); rreq[6]= MPI.COMM_WORLD.Irecv(
	 * shortReadArray,0,DATA_SIZE,MPI.SHORT,1,i+6); rreq[7]=
	 * MPI.COMM_WORLD.Irecv( floatReadArray,0,DATA_SIZE,MPI.FLOAT,1,i+7);
	 */
	for (int j = 0; j < 2; j++) {
	  Status st = sreq[j].Wait();
	}

	for (int j = 0; j < 2; j++) {
	  Status st = rreq[j].Wait();
	}

	if (Arrays.equals(intArray, intReadArray) && /*
						      * Arrays.equals(charArray,
						      * charReadArray) &&
						      * Arrays.
						      * equals(doubleArray
						      * ,doubleReadArray) &&
						      * Arrays
						      * .equals(floatArray,
						      * floatReadArray) &&
						      * Arrays.equals(longArray,
						      * longReadArray) &&
						      * Arrays.
						      * equals(shortArray,
						      * shortReadArray) &&
						      * Arrays
						      * .equals(booleanArray
						      * ,booleanReadArray) &&
						      */
	Arrays.equals(byteArray, byteReadArray)) {
	  /*
	   * System.out.println("\n#################"+
	   * "\n <<<<sender PASSED>>>> "+ "\n################" );
	   */
	} else {
	  System.out.println("\n#################"
	      + "\n <<<<sender FAILED>>>> " + "\n################");
	}

      }// end for

    } else if (me == 1) {

      for (int i = 1; i < 10; i = i + 4) {
	for (int j = 0; j < intArray.length; j++) {
	  intReadArray[j] = 3;
	  floatReadArray[j] = j + 19;
	  doubleReadArray[j] = j + 99.11;
	  longReadArray[j] = j + 9;
	  shortReadArray[j] = 2;
	  booleanReadArray[j] = false;
	  charReadArray[j] = 'x';
	  byteReadArray[j] = 'x';
	}

	sreq[0] = MPI.COMM_WORLD.Isend(intArray, 0, DATA_SIZE, MPI.INT, 0, i);
	sreq[1] = MPI.COMM_WORLD.Isend(byteArray, 0, DATA_SIZE, MPI.BYTE, 0,
	    i + 1);
	// sreq[2]=MPI.COMM_WORLD.Isend(charArray,0,DATA_SIZE,MPI.CHAR,0,i+2);
	// sreq[3]=MPI.COMM_WORLD.Isend(doubleArray,0,DATA_SIZE,MPI.DOUBLE,0,i+3);

	/*
	 * sreq[4]=MPI.COMM_WORLD.Isend(longArray,0,DATA_SIZE,MPI.LONG,0,i+4);
	 * sreq[5]=MPI.COMM_WORLD.Isend(
	 * booleanArray,0,DATA_SIZE,MPI.BOOLEAN,0,i+5);
	 * sreq[6]=MPI.COMM_WORLD.Isend(shortArray,0,DATA_SIZE,MPI.SHORT,0,i+6
	 * );
	 * sreq[7]=MPI.COMM_WORLD.Isend(floatArray,0,DATA_SIZE,MPI.FLOAT,0,i+7
	 * );
	 */
	rreq[0] = MPI.COMM_WORLD.Irecv(intReadArray, 0, DATA_SIZE, MPI.INT, 0,
	    i);
	rreq[1] = MPI.COMM_WORLD.Irecv(byteReadArray, 0, DATA_SIZE, MPI.BYTE,
	    0, i + 1);
	// rreq[2]= MPI.COMM_WORLD.Irecv(
	// charReadArray,0,DATA_SIZE,MPI.CHAR,0,i+2);
	// rreq[3]= MPI.COMM_WORLD.Irecv(doubleReadArray,0,DATA_SIZE,
	// MPI.DOUBLE,0,i+3);
	/*
	 * rreq[4]=
	 * MPI.COMM_WORLD.Irecv(longReadArray,0,DATA_SIZE,MPI.LONG,0,i+4);
	 * rreq[5]= MPI.COMM_WORLD.Irecv(booleanReadArray,0,DATA_SIZE,
	 * MPI.BOOLEAN,0,i+5); rreq[6]= MPI.COMM_WORLD.Irecv(
	 * shortReadArray,0,DATA_SIZE,MPI.SHORT,0,i+6); rreq[7]=
	 * MPI.COMM_WORLD.Irecv( floatReadArray,0,DATA_SIZE,MPI.FLOAT,0,i+7);
	 */
	for (int j = 0; j < 2; j++) {
	  Status st = sreq[j].Wait();
	}

	for (int j = 0; j < 2; j++) {
	  Status status = rreq[j].Wait();
	}

	if (Arrays.equals(intArray, intReadArray) && /*
						      * Arrays.equals(charArray,
						      * charReadArray) &&
						      * Arrays.
						      * equals(doubleArray
						      * ,doubleReadArray) &&
						      * Arrays
						      * .equals(floatArray,
						      * floatReadArray) &&
						      * Arrays.equals(longArray,
						      * longReadArray) &&
						      * Arrays.
						      * equals(shortArray,
						      * shortReadArray) &&
						      * Arrays
						      * .equals(booleanArray
						      * ,booleanReadArray) &&
						      */
	Arrays.equals(byteArray, byteReadArray)) {

	  System.out.println("\n#################"
	      + "\n <<<<receiver PASSED>>>> " + "\n################");

	} else {
	  System.out.println("\n#################"
	      + "\n <<<<receiver FAILED>>>> " + "\n################");
	}

      } // end for
    }// end else

    MPI.COMM_WORLD.Barrier();

    if (MPI.COMM_WORLD.Rank() == 1) {
      System.out.println("Isend_rendez TEST Completed <" + me + ">");
    }

    MPI.Finalize();
  }

  public static void main(String args[]) throws Exception {
    Isend_rendez test = new Isend_rendez(args);
  }

}
