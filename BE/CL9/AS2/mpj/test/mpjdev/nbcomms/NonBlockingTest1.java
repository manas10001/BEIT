package nbcomms;

import mpjdev.*;
import mpjbuf.*;
import java.util.Arrays;

/**
 * This test isends and receives all of the eight basic datatypes.
 * isends each datatype as a separate section.
 */

public class NonBlockingTest1 {

  public static void main(String args[]) throws Exception {
    MPJDev.init(args);
    int id = -1;
    id = MPJDev.WORLD.id();

    int DATA_SIZE = 100;

    mpjdev.Request [] sreqs = new mpjdev.Request[8];
    mpjdev.Request [] rreqs = new mpjdev.Request[8];
    
    int intArray[] = new int[DATA_SIZE];
    float floatArray[] = new float[DATA_SIZE];
    double doubleArray[] = new double[DATA_SIZE];
    long longArray[] = new long[DATA_SIZE];
    boolean booleanArray[] = new boolean[DATA_SIZE];
    short shortArray[] = new short[DATA_SIZE];
    char charArray[] = new char[DATA_SIZE];
    byte byteArray[] = new byte[DATA_SIZE];

    for (int i = 0; i < DATA_SIZE; i++) {
      intArray[i] = i + 1;
      floatArray[i] = i + 11;
      doubleArray[i] = i + 11.11;
      longArray[i] = i + 11;
      booleanArray[i] = true;
      shortArray[i] = 1;
      charArray[i] = 's';
      byteArray[i] = 's';
    }

    for (int h = 1; h < 5; h++) {

      System.out.println("**********<TEST==" + h + ">***************");

      if (id == 0) {
        Buffer byteBuffer = new Buffer(DATA_SIZE + 8);
        byteBuffer.putSectionHeader(Buffer.BYTE);
        byteBuffer.write(byteArray, 0, DATA_SIZE);
        byteBuffer.commit();

        Buffer charBuffer = new Buffer( (DATA_SIZE * 2) + 8);
        charBuffer.putSectionHeader(Buffer.CHAR);
        charBuffer.write(charArray, 0, DATA_SIZE);
        charBuffer.commit();

        Buffer intBuffer = new Buffer( (DATA_SIZE * 4) + 8);
        intBuffer.putSectionHeader(Buffer.INT);
        intBuffer.write(intArray, 0, DATA_SIZE);
        intBuffer.commit();

        Buffer shortBuffer = new Buffer( (DATA_SIZE * 2) + 8);
        shortBuffer.putSectionHeader(Buffer.SHORT);
        shortBuffer.write(shortArray, 0, DATA_SIZE);
        shortBuffer.commit();

        Buffer booleanBuffer = new Buffer( (DATA_SIZE) + 8);
        booleanBuffer.putSectionHeader(Buffer.BOOLEAN);
        booleanBuffer.write(booleanArray, 0, DATA_SIZE);
        booleanBuffer.commit();

        Buffer longBuffer = new Buffer( (DATA_SIZE * 8) + 8);
        longBuffer.putSectionHeader(Buffer.LONG);
        longBuffer.write(longArray, 0, DATA_SIZE);
        longBuffer.commit();

        Buffer doubleBuffer = new Buffer( (DATA_SIZE * 8) + 8);
        doubleBuffer.putSectionHeader(Buffer.DOUBLE);
        doubleBuffer.write(doubleArray, 0, DATA_SIZE);
        doubleBuffer.commit();

        Buffer floatBuffer = new Buffer( (DATA_SIZE * 4) + 8);
        floatBuffer.putSectionHeader(Buffer.FLOAT);
        floatBuffer.write(floatArray, 0, DATA_SIZE);
        floatBuffer.commit();
        /*uses blocking isend to
                 try {
          Thread.currentThread().sleep(10000);
                 }
                 catch (Exception e) {}
         */
        //System.out.println("Sending integers");
        sreqs[0] = MPJDev.WORLD.isend(intBuffer, 1, (1 + (h * 10)));
        //System.out.println("Sent integers");
        //System.out.println("Sending bytes");
        sreqs[1] = MPJDev.WORLD.isend(byteBuffer, 1, (2 + (h * 10)));
        //System.out.println("Sending chars");
        sreqs[2] = MPJDev.WORLD.isend(charBuffer, 1, (3 + (h * 10)));
        //System.out.println("Sending doubles");
        sreqs[3] = MPJDev.WORLD.isend(doubleBuffer, 1, (4 + (h * 10)));
        //System.out.println("Sending longs");
        sreqs[4] = MPJDev.WORLD.isend(longBuffer, 1, (5 + (h * 10)));
        //System.out.println("Sending bols");
        sreqs[5] = MPJDev.WORLD.isend(booleanBuffer, 1, (6 + (h * 10)));
        //System.out.println("Sending shorts");
	sreqs[6] = MPJDev.WORLD.isend(shortBuffer, 1, (7 + (h * 10)));
        //System.out.println("Sending floats");
        sreqs[7] = MPJDev.WORLD.isend(floatBuffer, 1, (8 + (h * 10)));
        System.out.println("Send Completed \n\n");
	
	for(int r=0 ; r<8 ; r++) {
	    sreqs[r].iwait();		
	}
      }
      else if (id == 1) {

        /********* THINGS TO BE READ ****************/
        int intReadArray[] = new int[DATA_SIZE];
        float floatReadArray[] = new float[DATA_SIZE];
        double doubleReadArray[] = new double[DATA_SIZE];
        long longReadArray[] = new long[DATA_SIZE];
        boolean booleanReadArray[] = new boolean[DATA_SIZE];
        short shortReadArray[] = new short[DATA_SIZE];
        char charReadArray[] = new char[DATA_SIZE];
        byte byteReadArray[] = new byte[DATA_SIZE];

        for (int i = 0; i < intReadArray.length; i++) {
          intReadArray[i] = 3;
          floatReadArray[i] = i + 19;
          doubleReadArray[i] = i + 99.11;
          longReadArray[i] = i + 9;
          booleanReadArray[i] = false;
          shortReadArray[i] = 2;
          charReadArray[i] = 'x';
          byteReadArray[i] = 'x';
        }

        Buffer intBuffer = new Buffer( (DATA_SIZE * 4) + 8);
        Buffer floatBuffer = new Buffer( (DATA_SIZE * 4) + 8);
        Buffer doubleBuffer = new Buffer( (DATA_SIZE * 8) + 8);
        Buffer shortBuffer = new Buffer( (DATA_SIZE * 2) + 8);
        Buffer booleanBuffer = new Buffer( (DATA_SIZE) + 8);
        Buffer charBuffer = new Buffer( (DATA_SIZE * 2) + 8);
        Buffer longBuffer = new Buffer( (DATA_SIZE * 8) + 8);
        Buffer byteBuffer = new Buffer( (DATA_SIZE) + 8);

        System.out.println("Receving ints ");
        rreqs[0] = MPJDev.WORLD.irecv(intBuffer, 0, (1 + (h * 10)));
        System.out.println("Receving bytes");
        rreqs[1] = MPJDev.WORLD.irecv(byteBuffer, 0, (2 + (10 * h)));
        System.out.println("Receving chars");
        rreqs[2] = MPJDev.WORLD.irecv(charBuffer, 0, (3 + (10 * h)));
        System.out.println("Receving doubles");
        rreqs[3] = MPJDev.WORLD.irecv(doubleBuffer, 0, (4 + (10 * h)));
        System.out.println("Receving longs");
        rreqs[4] = MPJDev.WORLD.irecv(longBuffer, 0, (5 + (10 * h)));
        System.out.println("Receving bools");
        rreqs[5] = MPJDev.WORLD.irecv(booleanBuffer, 0, (6 + (10 * h)));
        System.out.println("Receving shorts");
        rreqs[6] = MPJDev.WORLD.irecv(shortBuffer, 0, (7 + (10 * h)));
        System.out.println("Receving floats");
        rreqs[7] = MPJDev.WORLD.irecv(floatBuffer, 0, (8 + (10 * h)));

	for(int r=0 ; r<8 ; r++) {
	    rreqs[r].iwait();		
	}

        intBuffer.commit();
        floatBuffer.commit();
        doubleBuffer.commit();
        longBuffer.commit();
        shortBuffer.commit();
        byteBuffer.commit();
        booleanBuffer.commit();
        charBuffer.commit();

        try {
          intBuffer.getSectionHeader(Buffer.INT);
          floatBuffer.getSectionHeader(Buffer.FLOAT);
          doubleBuffer.getSectionHeader(Buffer.DOUBLE);
          longBuffer.getSectionHeader(Buffer.LONG);
          shortBuffer.getSectionHeader(Buffer.SHORT);
          byteBuffer.getSectionHeader(Buffer.BYTE);
          booleanBuffer.getSectionHeader(Buffer.BOOLEAN);
          charBuffer.getSectionHeader(Buffer.CHAR);
          intBuffer.read(intReadArray, 0, DATA_SIZE);
          floatBuffer.read(floatReadArray, 0, DATA_SIZE);
          doubleBuffer.read(doubleReadArray, 0, DATA_SIZE);
          longBuffer.read(longReadArray, 0, DATA_SIZE);
          shortBuffer.read(shortReadArray, 0, DATA_SIZE);
          charBuffer.read(charReadArray, 0, DATA_SIZE);
          byteBuffer.read(byteReadArray, 0, DATA_SIZE);
          booleanBuffer.read(booleanReadArray, 0, DATA_SIZE);
        }
        catch (Exception e) {
          e.printStackTrace();
        }

        if (Arrays.equals(intArray, intReadArray) &&
            Arrays.equals(floatArray, floatReadArray) &&
            Arrays.equals(doubleArray, doubleReadArray) &&
            Arrays.equals(longArray, longReadArray) &&
            Arrays.equals(shortArray, shortReadArray) &&
            Arrays.equals(charArray, charReadArray) &&
            Arrays.equals(byteArray, byteReadArray) &&
            Arrays.equals(booleanArray, booleanReadArray)) {
          System.out.println("\n#################" +
                             "\n <<<<PASSED>>>> " +
                             "\n################");
        }
        else {
          System.out.println("\n#################" +
                             "\n <<<<FAILED>>>> " +
                             "\n################");
          System.exit(0);
        }
      }

    } //end while(true)

    try {
      Thread.currentThread().sleep(500);
    }
    catch (Exception e) {}
    MPJDev.finish();

  }
}
