import mpi.*;
import java.util.Arrays;

public class NonBlocking {
  public NonBlocking(String args[]) throws Exception {

    Request req1, req2, req3, req4, req5, req6, req7, req8;
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
      req1 = MPI.COMM_WORLD.Isend(intArray, 0, 100, MPI.INT, 1, 999);
      req2 = MPI.COMM_WORLD.Isend(byteArray, 0, 100, MPI.BYTE, 1, 998);
      req3 = MPI.COMM_WORLD.Isend(charArray, 0, 100, MPI.CHAR, 1, 997);
      req4 = MPI.COMM_WORLD.Isend(doubleArray, 0, 100, MPI.DOUBLE, 1, 996);
      req5 = MPI.COMM_WORLD.Isend(longArray, 0, 100, MPI.LONG, 1, 995);
      req6 = MPI.COMM_WORLD.Isend(booleanArray, 0, 100, MPI.BOOLEAN, 1, 994);
      req7 = MPI.COMM_WORLD.Isend(shortArray, 0, 100, MPI.SHORT, 1, 993);
      req8 = MPI.COMM_WORLD.Isend(floatArray, 0, 100, MPI.FLOAT, 1, 992);

      req1.Wait();
      req2.Wait();
      req3.Wait();
      req4.Wait();
      req5.Wait();
      req6.Wait();
      req7.Wait();
      req8.Wait();

      System.out.println("Isend Completed \n\n");
    }

    else if (MPI.COMM_WORLD.Rank() == 1) {

      req1 = MPI.COMM_WORLD.Irecv(intReadArray, 0, 100, MPI.INT, 0, 999);
      req2 = MPI.COMM_WORLD.Irecv(byteReadArray, 0, 100, MPI.BYTE, 0, 998);
      req3 = MPI.COMM_WORLD.Irecv(charReadArray, 0, 100, MPI.CHAR, 0, 997);
      req4 = MPI.COMM_WORLD.Irecv(doubleReadArray, 0, 100, MPI.DOUBLE, 0, 996);
      req5 = MPI.COMM_WORLD.Irecv(longReadArray, 0, 100, MPI.LONG, 0, 995);
      req6 = MPI.COMM_WORLD
	  .Irecv(booleanReadArray, 0, 100, MPI.BOOLEAN, 0, 994);
      req7 = MPI.COMM_WORLD.Irecv(shortReadArray, 0, 100, MPI.SHORT, 0, 993);
      req8 = MPI.COMM_WORLD.Irecv(floatReadArray, 0, 100, MPI.FLOAT, 0, 992);

      req1.Wait();
      req2.Wait();
      req3.Wait();
      req4.Wait();
      req5.Wait();
      req6.Wait();
      req7.Wait();
      req8.Wait();

      if (Arrays.equals(intArray, intReadArray)/*
					        * && Arrays.equals(floatArray,
					        * floatReadArray) &&
					        * Arrays.equals
					        * (doubleArray,doubleReadArray)
					        * && Arrays.equals(longArray,
					        * longReadArray) &&
					        * Arrays.equals
					        * (shortArray,shortReadArray) &&
					        * Arrays
					        * .equals(charArray,charReadArray
					        * ) && Arrays.equals(byteArray,
					        * byteReadArray) &&
					        * Arrays.equals
					        * (booleanArray,booleanReadArray
					        * )
					        */) {
	System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	    + "\n################");
      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
      }
    }

    MPI.COMM_WORLD.Barrier();
    try {
      MPI.Finalize();
    }
    catch (Exception e) {
    }
  }

  public static void main(String args[]) throws Exception {
    NonBlocking test = new NonBlocking(args);
  }
}
