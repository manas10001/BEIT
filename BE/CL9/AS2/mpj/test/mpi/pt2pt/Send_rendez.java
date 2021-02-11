package mpi.pt2pt;

import mpi.*;
import java.util.Arrays;

public class Send_rendez {
  int DATA_SIZE = 131073;

  public Send_rendez() {
  }

  public Send_rendez(String args[]) throws Exception {

    MPI.Init(args);
    int me = MPI.COMM_WORLD.Rank();

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
      MPI.COMM_WORLD.Send(intArray, 0, DATA_SIZE, MPI.INT, 1, 999);
      MPI.COMM_WORLD.Send(byteArray, 0, DATA_SIZE, MPI.BYTE, 1, 998);
      MPI.COMM_WORLD.Send(charArray, 0, DATA_SIZE, MPI.CHAR, 1, 997);
      MPI.COMM_WORLD.Send(doubleArray, 0, DATA_SIZE, MPI.DOUBLE, 1, 996);
      MPI.COMM_WORLD.Send(longArray, 0, DATA_SIZE, MPI.LONG, 1, 995);
      MPI.COMM_WORLD.Send(booleanArray, 0, DATA_SIZE, MPI.BOOLEAN, 1, 994);
      MPI.COMM_WORLD.Send(shortArray, 0, DATA_SIZE, MPI.SHORT, 1, 993);
      MPI.COMM_WORLD.Send(floatArray, 0, DATA_SIZE, MPI.FLOAT, 1, 992);
      // System.out.println("Send Completed \n\n");
    } else if (MPI.COMM_WORLD.Rank() == 1) {
      // try { Thread.currentThread().sleep(DATA_SIZE); }catch(Exception e){}
      // System.out.println(" ** Recv calling ** ");
      MPI.COMM_WORLD.Recv(intReadArray, 0, DATA_SIZE, MPI.INT, 0, 999);
      MPI.COMM_WORLD.Recv(byteReadArray, 0, DATA_SIZE, MPI.BYTE, 0, 998);
      MPI.COMM_WORLD.Recv(charReadArray, 0, DATA_SIZE, MPI.CHAR, 0, 997);
      MPI.COMM_WORLD.Recv(doubleReadArray, 0, DATA_SIZE, MPI.DOUBLE, 0, 996);
      MPI.COMM_WORLD.Recv(longReadArray, 0, DATA_SIZE, MPI.LONG, 0, 995);
      MPI.COMM_WORLD.Recv(booleanReadArray, 0, DATA_SIZE, MPI.BOOLEAN, 0, 994);
      MPI.COMM_WORLD.Recv(shortReadArray, 0, DATA_SIZE, MPI.SHORT, 0, 993);
      MPI.COMM_WORLD.Recv(floatReadArray, 0, DATA_SIZE, MPI.FLOAT, 0, 992);
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

    if (MPI.COMM_WORLD.Rank() == 0) {
      System.out.println("Send_rendez TEST Completed");
    }

    MPI.COMM_WORLD.Barrier();
    MPI.Finalize();
  }

  public static void main(String args[]) throws Exception {
    Send_rendez test = new Send_rendez(args);
  }
}
