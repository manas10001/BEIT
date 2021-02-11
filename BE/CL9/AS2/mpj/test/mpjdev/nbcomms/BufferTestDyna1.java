package buffertest;

import mpjdev.*;
import mpjbuf.*;

public class BufferTestDyna1 {

  public static void main(String args[]) throws Exception {

    int DATA_SIZE = 100;
    MPJDev.init(args);
    int h = 10;

//for(int h=1 ; h<1000 ; h++) {
//		System.out.println("\n\n\n************************<TEST=="+h+">**************************\n\n\n");
//		MPJDev.WORLD.out.println("\n\n\n************************<TEST=="+h+">**************************\n\n\n");


    if (MPJDev.WORLD.id() == 0) {

      /********* THINGS TO BE SENT ******************/

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

      System.out.println("Writing byteBuffer");
      Buffer byteBuffer = new Buffer(8);
      byteBuffer.putSectionHeader(Buffer.BYTE_DYNAMIC);
      byteBuffer.write(byteArray, 0, DATA_SIZE); //write the array of DATA_SIZE bytes
      byteBuffer.commit();

      System.out.println("Writing charBuffer");
      Buffer charBuffer = new Buffer(8);
      charBuffer.putSectionHeader(Buffer.CHAR_DYNAMIC);
      charBuffer.write(charArray, 0, DATA_SIZE); //write the array of DATA_SIZE char
      charBuffer.commit();

      System.out.println("Writing intBuffer");
      Buffer intBuffer = new Buffer(8);
      intBuffer.putSectionHeader(Buffer.INT_DYNAMIC);
      intBuffer.write(intArray, 0, DATA_SIZE); //write the array of DATA_SIZE char
      intBuffer.commit();

      System.out.println("Writing shortBuffer");
      Buffer shortBuffer = new Buffer(8);
      shortBuffer.putSectionHeader(Buffer.SHORT_DYNAMIC);
      shortBuffer.write(shortArray, 0, DATA_SIZE);
      shortBuffer.commit();

      System.out.println("Writing booleanBuffer");
      Buffer booleanBuffer = new Buffer(8);
      booleanBuffer.putSectionHeader(Buffer.BOOLEAN_DYNAMIC);
      booleanBuffer.write(booleanArray, 0, DATA_SIZE);
      booleanBuffer.commit();

      System.out.println("Writing long Buffer");
      Buffer longBuffer = new Buffer(8);
      longBuffer.putSectionHeader(Buffer.LONG_DYNAMIC);
      longBuffer.write(longArray, 0, DATA_SIZE);
      longBuffer.commit();

      System.out.println("Writing double buffer");
      Buffer doubleBuffer = new Buffer(8);
      doubleBuffer.putSectionHeader(Buffer.DOUBLE_DYNAMIC);
      doubleBuffer.write(doubleArray, 0, DATA_SIZE);
      doubleBuffer.commit();

      System.out.println("Writing float Buffer");
      Buffer floatBuffer = new Buffer(8);
      floatBuffer.putSectionHeader(Buffer.FLOAT_DYNAMIC);
      floatBuffer.write(floatArray, 0, DATA_SIZE);
      floatBuffer.commit();
      //uses blocking send to
      MPJDev.WORLD.send(intBuffer, 1, (1 + (h * 10)));
      MPJDev.WORLD.send(byteBuffer, 1, (2 + (h * 10)));
      MPJDev.WORLD.send(charBuffer, 1, (3 + (h * 10)));
      MPJDev.WORLD.send(doubleBuffer, 1, (4 + (h * 10)));
      MPJDev.WORLD.send(longBuffer, 1, (5 + (h * 10)));
      MPJDev.WORLD.send(booleanBuffer, 1, (6 + (h * 10)));
      MPJDev.WORLD.send(shortBuffer, 1, (7 + (h * 10)));
      MPJDev.WORLD.send(floatBuffer, 1, (8 + (h * 10)));
      System.out.println("Send Completed \n\n");

    }
    else if (MPJDev.WORLD.id() == 1) {

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

      Buffer intBuffer = new Buffer(8);
      Buffer floatBuffer = new Buffer(8);
      Buffer doubleBuffer = new Buffer(8);
      Buffer shortBuffer = new Buffer(8);
      Buffer booleanBuffer = new Buffer(8);
      Buffer charBuffer = new Buffer(8);
      Buffer longBuffer = new Buffer(8);
      Buffer byteBuffer = new Buffer(8);

      System.out.println("Receving ints ");
      MPJDev.WORLD.recv(intBuffer, 0, (1 + (h * 10)));
      System.out.println("Receving bytes");
      MPJDev.WORLD.recv(byteBuffer, 0, (2 + (10 * h)));
      System.out.println("Receving chars");
      MPJDev.WORLD.recv(charBuffer, 0, (3 + (10 * h)));
      System.out.println("Receving doubles");
      MPJDev.WORLD.recv(doubleBuffer, 0, (4 + (10 * h)));
      System.out.println("Receving longs");
      MPJDev.WORLD.recv(longBuffer, 0, (5 + (10 * h)));
      System.out.println("Receving bools");
      MPJDev.WORLD.recv(booleanBuffer, 0, (6 + (10 * h)));
      System.out.println("Receving shorts");
      MPJDev.WORLD.recv(shortBuffer, 0, (7 + (10 * h)));
      System.out.println("Receving floats");
      MPJDev.WORLD.recv(floatBuffer, 0, (8 + (10 * h)));

      intBuffer.commit();
      floatBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      shortBuffer.commit();
      byteBuffer.commit();
      booleanBuffer.commit();
      charBuffer.commit();

      try {
        intBuffer.getSectionHeader(Buffer.INT_DYNAMIC);
        floatBuffer.getSectionHeader(Buffer.FLOAT_DYNAMIC);
        doubleBuffer.getSectionHeader(Buffer.DOUBLE_DYNAMIC);
        longBuffer.getSectionHeader(Buffer.LONG_DYNAMIC);
        shortBuffer.getSectionHeader(Buffer.SHORT_DYNAMIC);
        byteBuffer.getSectionHeader(Buffer.BYTE_DYNAMIC);
        booleanBuffer.getSectionHeader(Buffer.BOOLEAN_DYNAMIC);
        charBuffer.getSectionHeader(Buffer.CHAR_DYNAMIC);

        System.out.println("Read Int");
        intBuffer.read(intReadArray, 0, DATA_SIZE);
        System.out.println("Reading Float");
        floatBuffer.read(floatReadArray, 0, DATA_SIZE);
        System.out.println("Reading Double");
        doubleBuffer.read(doubleReadArray, 0, DATA_SIZE);
        System.out.println("Reading Long");
        longBuffer.read(longReadArray, 0, DATA_SIZE);
        System.out.println("Reading Short");
        shortBuffer.read(shortReadArray, 0, DATA_SIZE);
        System.out.println("Reading Char");
        charBuffer.read(charReadArray, 0, DATA_SIZE);
        System.out.println("Reading Byte");
        byteBuffer.read(byteReadArray, 0, DATA_SIZE);
        System.out.println("Reading Boolean");
        booleanBuffer.read(booleanReadArray, 0, DATA_SIZE);
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      System.out.print(
          "\t********* DISPLAYING THE INT ARRAY READ *********\n\n");
      for (int j = 0; j < intReadArray.length; j++) {
        System.out.print("int[" + j + "]=" + intReadArray[j] + "\t");
      }

      System.out.print(
          "\t******** DISPLAYING THE FLOAT ARRAY READ ********\n\n");
      for (int j = 0; j < floatReadArray.length; j++) {
        System.out.print("float[" + j + "]=" + floatReadArray[j] + "\t");
      }

      System.out.print(
          "\t******* DISPLAYING THE DOUBLE ARRAY READ ********\n\n");
      for (int j = 0; j < doubleReadArray.length; j++) {
        System.out.print("double[" + j + "]=" + doubleReadArray[j] + "\t");
      }

      System.out.print("\t******** DISPLAYING THE LONG ARRAY READ ********\n\n");
      for (int j = 0; j < longReadArray.length; j++) {
        System.out.print("long[" + j + "]=" + longReadArray[j] + "\t");
      }

      System.out.print(
          "\t******** DISPLAYING THE SHORT ARRAY READ ********\n\n");
      for (int j = 0; j < shortReadArray.length; j++) {
        System.out.print("short[" + j + "]=" + shortReadArray[j] + "\t");
      }

      System.out.print(
          "\t******** DISPLAYING THE FLOAT ARRAY READ ********\n\n");
      for (int j = 0; j < charReadArray.length; j++) {
        System.out.print("char[" + j + "]=" + charReadArray[j] + "\t");
      }

      System.out.print("\t******** DISPLAYING THE BYTE ARRAY READ ********\n\n");
      for (int j = 0; j < byteReadArray.length; j++) {
        System.out.print("byte[" + j + "]=" + byteReadArray[j] + "\t");
      }

      System.out.print(
          "\t******** DISPLAYING THE BOOLEAN ARRAY READ ********\n\n");
      for (int j = 0; j < booleanReadArray.length; j++) {
        System.out.print("boolean[" + j + "]=" + booleanReadArray[j] + "\t");
      }

      System.out.println("Receive Completed \n\n");
    }
    //MPJDev.WORLD.barrier();
//}
    System.out.println("Calling the barrier");
    //MPJDev.WORLD.barrier();
    System.out.println("Barrier ends");
    try {
      Thread.currentThread().sleep(500);
    }
    catch (Exception e) {}

    MPJDev.finish();

  }
}
