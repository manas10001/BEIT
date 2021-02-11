package buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * Gather/Scatter Operation on a single
 */
public class BufferTestDyna4 {
  public static void main(String args[]) throws Exception {

    //the first call will always be init()
    MPJDev.init(args);

    if (MPJDev.WORLD.id() == 0) {
      int intArray[] = new int[100];
      for (int j = 0; j < intArray.length; j++) {
        intArray[j] = j + 1;
      }

      float floatArray[] = new float[100];
      for (int i = 0; i < floatArray.length; i++) {
        floatArray[i] = i + 11;
      }

      double doubleArray[] = new double[100];
      for (int i = 0; i < doubleArray.length; i++) {
        doubleArray[i] = i + 11.11;
      }

      long longArray[] = new long[100];
      for (int i = 0; i < longArray.length; i++) {
        longArray[i] = i + 11;
      }

      boolean booleanArray[] = new boolean[100];
      for (int i = 0; i < booleanArray.length; i++) {
        booleanArray[i] = true;
      }

      short shortArray[] = new short[100];
      for (int i = 0; i < shortArray.length; i++) {
        shortArray[i] = 1;
      }

      char charArray[] = new char[100];
      for (int i = 0; i < charArray.length; i++) {
        charArray[i] = 's';
      }

      byte byteArray[] = new byte[100];
      for (int i = 0; i < byteArray.length; i++) {
        byteArray[i] = 's';
      }

      int indexes[] = {
          2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36, 38, 42, 44,
          46, 48, 52, 54, 56, 58,
          62, 64, 66, 68, 72, 74, 76, 78, 82, 84, 86, 88, 92, 94, 96, 98};

      Buffer buffer = new Buffer( (8 * 8));
      buffer.putSectionHeader(Buffer.BYTE_DYNAMIC);
      buffer.gather(byteArray, 40, 0, indexes);
      buffer.putSectionHeader(Buffer.CHAR_DYNAMIC);
      buffer.gather(charArray, 40, 0, indexes);
      buffer.putSectionHeader(Buffer.INT_DYNAMIC);
      buffer.gather(intArray, 40, 0, indexes);
      buffer.putSectionHeader(Buffer.SHORT_DYNAMIC);
      buffer.gather(shortArray, 40, 0, indexes);
      buffer.putSectionHeader(Buffer.BOOLEAN_DYNAMIC);
      buffer.gather(booleanArray, 40, 0, indexes);
      buffer.putSectionHeader(Buffer.LONG_DYNAMIC);
      buffer.gather(longArray, 40, 0, indexes);
      buffer.putSectionHeader(Buffer.DOUBLE_DYNAMIC);
      buffer.gather(doubleArray, 40, 0, indexes);
      buffer.putSectionHeader(Buffer.FLOAT_DYNAMIC);
      buffer.gather(floatArray, 40, 0, indexes);
      buffer.commit();
      MPJDev.WORLD.send(buffer, 1, 999); //Buffer, destinationRank, tag (make sure you have matchin recv also called)
      System.out.println("Send Completed \n\n");
    }

    else if (MPJDev.WORLD.id() == 1) {

      /********* THINGIES TO BE READ ******************/
      int intReadArray[] = new int[100];
      for (int j = 0; j < intReadArray.length; j++) {
        intReadArray[j] = 3;
      }

      float floatReadArray[] = new float[100];
      for (int i = 0; i < floatReadArray.length; i++) {
        floatReadArray[i] = i + 19;
      }

      double doubleReadArray[] = new double[100];
      for (int i = 0; i < doubleReadArray.length; i++) {
        doubleReadArray[i] = i + 99.11;
      }

      long longReadArray[] = new long[100];
      for (int i = 0; i < longReadArray.length; i++) {
        longReadArray[i] = i + 9;
      }

      boolean booleanReadArray[] = new boolean[100];
      for (int i = 0; i < booleanReadArray.length; i++) {
        booleanReadArray[i] = false;
      }

      short shortReadArray[] = new short[100];
      for (int i = 0; i < shortReadArray.length; i++) {
        shortReadArray[i] = 2;
      }

      char charReadArray[] = new char[100];
      for (int i = 0; i < charReadArray.length; i++) {
        charReadArray[i] = 'x';
      }

      byte byteReadArray[] = new byte[100];
      for (int i = 0; i < byteReadArray.length; i++) {
        byteReadArray[i] = 'x';
      }

      /********* THINGIES TO BE READ ******************/
      //Same rules as above apply here as well. For the size of the things.
      Buffer buffer = new Buffer( (8 * 8));

      MPJDev.WORLD.recv(buffer, 0, 999);
      int indexes[] = {
          2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36, 38, 42, 44,
          46, 48, 52, 54, 56, 58,
          62, 64, 66, 68, 72, 74, 76, 78, 82, 84, 86, 88, 92, 94, 96, 98};
      buffer.commit();

      try {
        buffer.getSectionHeader(Buffer.BYTE_DYNAMIC);
        buffer.scatter(byteReadArray, 40, 0, indexes);
        buffer.getSectionHeader(Buffer.CHAR_DYNAMIC);
        buffer.scatter(charReadArray, 40, 0, indexes);
        buffer.getSectionHeader(Buffer.INT_DYNAMIC);
        buffer.scatter(intReadArray, 40, 0, indexes);
        buffer.getSectionHeader(Buffer.SHORT_DYNAMIC);
        buffer.scatter(shortReadArray, 40, 0, indexes);
        buffer.getSectionHeader(Buffer.BOOLEAN_DYNAMIC);
        buffer.scatter(booleanReadArray, 40, 0, indexes);
        buffer.getSectionHeader(Buffer.LONG_DYNAMIC);
        buffer.scatter(longReadArray, 40, 0, indexes);
        buffer.getSectionHeader(Buffer.DOUBLE_DYNAMIC);
        buffer.scatter(doubleReadArray, 40, 0, indexes);
        buffer.getSectionHeader(Buffer.FLOAT_DYNAMIC);
        buffer.scatter(floatReadArray, 40, 0, indexes);
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

    MPJDev.finish();
  }
}
