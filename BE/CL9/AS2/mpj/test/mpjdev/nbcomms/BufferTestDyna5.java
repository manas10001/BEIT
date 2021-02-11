package buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * Gather methods of Buffer Class, and Scatter methods of Buffer Class.
 */
public class BufferTestDyna5 {
  public static void main(String args[]) throws Exception {

    //the first call will always be init()
    MPJDev.init(args);

    if (MPJDev.WORLD.id() == 0) {
      /********* THINGIES TO BE SENT ******************/
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
          2, 4, 6, 8};

      Buffer floatBuffer = new Buffer(8);
      floatBuffer.putSectionHeader(Buffer.FLOAT_DYNAMIC);
      floatBuffer.strGather(floatArray, 0, 2, 0, 2, indexes);

      Buffer byteBuffer = new Buffer(8);
      byteBuffer.putSectionHeader(Buffer.BYTE_DYNAMIC);
      byteBuffer.strGather(byteArray, 0, 2, 0, 2, indexes);

      Buffer charBuffer = new Buffer(8);
      charBuffer.putSectionHeader(Buffer.CHAR_DYNAMIC);
      charBuffer.strGather(charArray, 0, 2, 0, 2, indexes);

      Buffer intBuffer = new Buffer(8); // 32 + 16 ...
      intBuffer.putSectionHeader(Buffer.INT_DYNAMIC);
      intBuffer.strGather(intArray, 0, 2, 0, 2, indexes);

      Buffer shortBuffer = new Buffer(8);
      shortBuffer.putSectionHeader(Buffer.SHORT_DYNAMIC);
      shortBuffer.strGather(shortArray, 0, 2, 0, 2, indexes);

      Buffer booleanBuffer = new Buffer(8);
      booleanBuffer.putSectionHeader(Buffer.BOOLEAN_DYNAMIC);
      booleanBuffer.strGather(booleanArray, 0, 2, 0, 2, indexes);

      Buffer longBuffer = new Buffer(8);
      longBuffer.putSectionHeader(Buffer.LONG_DYNAMIC);
      longBuffer.strGather(longArray, 0, 2, 0, 2, indexes);

      Buffer doubleBuffer = new Buffer(8);
      doubleBuffer.putSectionHeader(Buffer.DOUBLE_DYNAMIC);
      doubleBuffer.strGather(doubleArray, 0, 2, 0, 2, indexes);

      /********* PACKING OF THE BUFFER *****************/
      //uses blocking send to
      floatBuffer.commit();
      intBuffer.commit();
      byteBuffer.commit();
      charBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      booleanBuffer.commit();
      shortBuffer.commit();

      MPJDev.WORLD.send(floatBuffer, 1, 992);
      MPJDev.WORLD.send(intBuffer, 1, 999);
      MPJDev.WORLD.send(byteBuffer, 1, 998);
      MPJDev.WORLD.send(charBuffer, 1, 997);
      MPJDev.WORLD.send(doubleBuffer, 1, 996);
      MPJDev.WORLD.send(longBuffer, 1, 995);
      MPJDev.WORLD.send(booleanBuffer, 1, 994);
      MPJDev.WORLD.send(shortBuffer, 1, 993);
      /********* PACKING OF THE BUFFER *****************/
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

      int indexes[] = {
          2, 4, 6, 8};

      Buffer intBuffer = new Buffer(8);
      Buffer floatBuffer = new Buffer(8);
      Buffer doubleBuffer = new Buffer(8);
      Buffer shortBuffer = new Buffer(8);
      Buffer booleanBuffer = new Buffer(8);
      Buffer charBuffer = new Buffer(8);
      Buffer longBuffer = new Buffer(8);
      Buffer byteBuffer = new Buffer(8);

      MPJDev.WORLD.recv(floatBuffer, 0, 992);
      MPJDev.WORLD.recv(intBuffer, 0, 999);
      MPJDev.WORLD.recv(byteBuffer, 0, 998);
      MPJDev.WORLD.recv(charBuffer, 0, 997);
      MPJDev.WORLD.recv(doubleBuffer, 0, 996);
      MPJDev.WORLD.recv(longBuffer, 0, 995);
      MPJDev.WORLD.recv(booleanBuffer, 0, 994);
      MPJDev.WORLD.recv(shortBuffer, 0, 993);

      floatBuffer.commit();
      intBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      shortBuffer.commit();
      charBuffer.commit();
      byteBuffer.commit();
      booleanBuffer.commit();

      try {
        floatBuffer.getSectionHeader(Buffer.FLOAT_DYNAMIC);
        floatBuffer.strScatter(floatReadArray, 0, 2, 0, 2, indexes);

        intBuffer.getSectionHeader(Buffer.INT_DYNAMIC);
        intBuffer.strScatter(intReadArray, 0, 2, 0, 2, indexes);

        doubleBuffer.getSectionHeader(Buffer.DOUBLE_DYNAMIC);
        doubleBuffer.strScatter(doubleReadArray, 0, 2, 0, 2, indexes);

        longBuffer.getSectionHeader(Buffer.LONG_DYNAMIC);
        longBuffer.strScatter(longReadArray, 0, 2, 0, 2, indexes);

        shortBuffer.getSectionHeader(Buffer.SHORT_DYNAMIC);
        shortBuffer.strScatter(shortReadArray, 0, 2, 0, 2, indexes);

        charBuffer.getSectionHeader(Buffer.CHAR_DYNAMIC);
        charBuffer.strScatter(charReadArray, 0, 2, 0, 2, indexes);

        byteBuffer.getSectionHeader(Buffer.BYTE_DYNAMIC);
        byteBuffer.strScatter(byteReadArray, 0, 2, 0, 2, indexes);

        booleanBuffer.getSectionHeader(Buffer.BOOLEAN_DYNAMIC);
        booleanBuffer.strScatter(booleanReadArray, 0, 2, 0, 2, indexes);
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

    //This should be the last call, in order to finish the communication
    MPJDev.finish();
  }
}
