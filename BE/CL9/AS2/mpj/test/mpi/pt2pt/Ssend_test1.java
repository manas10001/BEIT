package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class Ssend_test1 {
  public Ssend_test1() {
  }

  public Ssend_test1(String args[]) throws Exception {

    MPI.Init(args);

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
      MPI.COMM_WORLD.Ssend(intArray, 0, 100, MPI.INT, 1, 999);
      MPI.COMM_WORLD.Ssend(byteArray, 0, 100, MPI.BYTE, 1, 998);
      MPI.COMM_WORLD.Ssend(charArray, 0, 100, MPI.CHAR, 1, 997);
      MPI.COMM_WORLD.Ssend(doubleArray, 0, 100, MPI.DOUBLE, 1, 996);
      MPI.COMM_WORLD.Ssend(longArray, 0, 100, MPI.LONG, 1, 995);
      MPI.COMM_WORLD.Ssend(booleanArray, 0, 100, MPI.BOOLEAN, 1, 994);
      MPI.COMM_WORLD.Ssend(shortArray, 0, 100, MPI.SHORT, 1, 993);
      MPI.COMM_WORLD.Ssend(floatArray, 0, 100, MPI.FLOAT, 1, 992);
    } else if (MPI.COMM_WORLD.Rank() == 1) {
      // try { Thread.currentThread().sleep(1000); }catch(Exception e){}
      MPI.COMM_WORLD.Recv(intReadArray, 0, 100, MPI.INT, 0, 999);
      MPI.COMM_WORLD.Recv(byteReadArray, 0, 100, MPI.BYTE, 0, 998);
      MPI.COMM_WORLD.Recv(charReadArray, 0, 100, MPI.CHAR, 0, 997);
      MPI.COMM_WORLD.Recv(doubleReadArray, 0, 100, MPI.DOUBLE, 0, 996);
      MPI.COMM_WORLD.Recv(longReadArray, 0, 100, MPI.LONG, 0, 995);
      MPI.COMM_WORLD.Recv(booleanReadArray, 0, 100, MPI.BOOLEAN, 0, 994);
      MPI.COMM_WORLD.Recv(shortReadArray, 0, 100, MPI.SHORT, 0, 993);
      MPI.COMM_WORLD.Recv(floatReadArray, 0, 100, MPI.FLOAT, 0, 992);

      if (Arrays.equals(intArray, intReadArray)
	  && Arrays.equals(floatArray, floatReadArray)
	  && Arrays.equals(doubleArray, doubleReadArray)
	  && Arrays.equals(longArray, longReadArray)
	  && Arrays.equals(shortArray, shortReadArray)
	  && Arrays.equals(charArray, charReadArray)
	  && Arrays.equals(byteArray, byteReadArray)
	  && Arrays.equals(booleanArray, booleanReadArray)) {

	System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	    + "\n################");

      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }
    }

    MPI.COMM_WORLD.Barrier();

    if (MPI.COMM_WORLD.Rank() == 0) {
      System.out.println("Ssend_test1 TEST Completed");
    }

    MPI.Finalize();

  }

  public static void main(String args[]) throws Exception {
    Ssend_test1 test = new Ssend_test1(args);
  }
}
