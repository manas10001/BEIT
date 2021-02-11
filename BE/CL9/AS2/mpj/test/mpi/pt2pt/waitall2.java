package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class waitall2 {
  public waitall2() {
  }

  public waitall2(String args[]) throws Exception {
    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();

    mpi.Request r[] = new Request[10];
    mpi.Status s[] = new Status[10];
    r[0] = MPI.REQUEST_NULL;
    r[9] = MPI.REQUEST_NULL;

    int intArray[] = new int[100];
    float floatArray[] = new float[100];
    double doubleArray[] = new double[100];
    long longArray[] = new long[100];
    boolean booleanArray[] = new boolean[100];
    short shortArray[] = new short[100];
    char charArray[] = new char[100];
    byte byteArray[] = new byte[100];

    int intReadArray[] = new int[100];
    float floatReadArray[] = new float[100];
    double doubleReadArray[] = new double[100];
    long longReadArray[] = new long[100];
    boolean booleanReadArray[] = new boolean[100];
    short shortReadArray[] = new short[100];
    char charReadArray[] = new char[100];
    byte byteReadArray[] = new byte[100];

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

      r[1] = MPI.COMM_WORLD.Isend(intArray, 0, 100, MPI.INT, 1, 1);
      r[2] = MPI.COMM_WORLD.Isend(byteArray, 0, 100, MPI.BYTE, 1, 2);
      /*
       * r[3]=MPI.COMM_WORLD.Isend(charArray,0,100,MPI.CHAR,1,3);
       * r[4]=MPI.COMM_WORLD.Isend(doubleArray,0,100,MPI.DOUBLE,1,4);
       * r[5]=MPI.COMM_WORLD.Isend(longArray,0,100,MPI.LONG,1,5);
       * r[6]=MPI.COMM_WORLD.Isend(booleanArray,0,100,MPI.BOOLEAN,1,6);
       * r[7]=MPI.COMM_WORLD.Isend(shortArray,0,100,MPI.SHORT,1,7);
       * r[8]=MPI.COMM_WORLD.Isend(floatArray,0,100,MPI.FLOAT,1,8);
       */
      r[1].Wait();
      r[2].Wait();
      /*
       * s = Request.Waitall(r);
       * 
       * for(int i=0 ; i<s.length ; i++) {
       * 
       * if(s[i].equals(MPI.EMPTY_STATUS) ) { continue; }
       * 
       * if( s[i].source != 1 && s[i].tag != i) {
       * System.out.println("Error in status objects (sender)"); }
       * 
       * }
       */

    } else if (MPI.COMM_WORLD.Rank() == 1) {
      r[1] = MPI.COMM_WORLD.Irecv(intReadArray, 0, 100, MPI.INT, 0, 1);
      r[2] = MPI.COMM_WORLD.Irecv(byteReadArray, 0, 100, MPI.BYTE, 0, 2);
      r[1].Wait();
      r[2].Wait();
      /*
       * r[3]=MPI.COMM_WORLD.Irecv(charReadArray,0,100,MPI.CHAR,0,3);
       * r[4]=MPI.COMM_WORLD.Irecv(doubleReadArray,0,100,MPI.DOUBLE,0,4);
       * r[5]=MPI.COMM_WORLD.Irecv(longReadArray,0,100,MPI.LONG,0,5);
       * r[6]=MPI.COMM_WORLD.Irecv(booleanReadArray,0,100,MPI.BOOLEAN,0,6);
       * r[7]=MPI.COMM_WORLD.Irecv(shortReadArray,0,100,MPI.SHORT,0,7);
       * r[8]=MPI.COMM_WORLD.Irecv(floatReadArray,0,100,MPI.FLOAT,0,8);
       * 
       * s = Request.Waitall(r);
       * 
       * for(int i=0 ; i<s.length ; i++) {
       * 
       * if(s[i].equals(MPI.EMPTY_STATUS) ) { continue; }
       * 
       * if( s[i].source != 0 && s[i].tag != i) {
       * System.out.println("Error in status objects (Receiver)"); }
       * 
       * }
       */
      if (Arrays.equals(intArray, intReadArray)) { /*
						    * &&
						    * Arrays.equals(floatArray
						    * ,floatReadArray) &&
						    * Arrays.equals(doubleArray,
						    * doubleReadArray) &&
						    * Arrays.
						    * equals(longArray,longReadArray
						    * ) &&
						    * Arrays.equals(shortArray
						    * ,shortReadArray) &&
						    * Arrays.
						    * equals(charArray,charReadArray
						    * ) &&
						    * Arrays.equals(byteArray
						    * ,byteReadArray) &&
						    * Arrays.equals
						    * (booleanArray,
						    * booleanReadArray)) {
						    */

	System.out.println("waitall2 TEST Completed");

	System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	    + "\n################");

      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();

  }

  public static void main(String args[]) throws Exception {
    waitall2 test = new waitall2(args);
  }
}
