package buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * Gather methods of Buffer Class, and Scatter methods of Buffer Class.
 */
public class BufferTest3 {

  public BufferTest3(String args[]) throws Exception {
    
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

      Buffer byteBuffer = new Buffer(40 + 8);
      byteBuffer.putSectionHeader(Buffer.BYTE);
      byteBuffer.gather(byteArray, 40, 0, indexes);
      byteBuffer.commit();

      Buffer charBuffer = new Buffer(80 + 8);
      charBuffer.putSectionHeader(Buffer.CHAR);
      charBuffer.gather(charArray, 40, 0, indexes);
      charBuffer.commit();

      Buffer intBuffer = new Buffer(160 + 8);
      intBuffer.putSectionHeader(Buffer.INT);
      intBuffer.gather(intArray, 40, 0, indexes);
      intBuffer.commit();

      //(100*2)+section-overhead(8bytes)+NOPADDING
      Buffer shortBuffer = new Buffer(80 + 8);
      shortBuffer.putSectionHeader(Buffer.SHORT);
      shortBuffer.gather(shortArray, 40, 0, indexes);
      shortBuffer.commit();

      Buffer booleanBuffer = new Buffer(40 + 8);
      booleanBuffer.putSectionHeader(Buffer.BOOLEAN);
      booleanBuffer.gather(booleanArray, 40, 0, indexes);
      booleanBuffer.commit();

      Buffer longBuffer = new Buffer(320 + 8);
      longBuffer.putSectionHeader(Buffer.LONG);
      longBuffer.gather(longArray, 40, 0, indexes);
      longBuffer.commit();

      Buffer doubleBuffer = new Buffer(320 + 8);
      doubleBuffer.putSectionHeader(Buffer.DOUBLE);
      doubleBuffer.gather(doubleArray, 40, 0, indexes);
      doubleBuffer.commit();

      Buffer floatBuffer = new Buffer(160 + 8);
      floatBuffer.putSectionHeader(Buffer.FLOAT);
      floatBuffer.gather(floatArray, 40, 0, indexes);
      floatBuffer.commit();
      /********* PACKING OF THE BUFFER *****************/
      //uses blocking send to
      MPJDev.WORLD.send(intBuffer, 1, 999); 
      MPJDev.WORLD.send(byteBuffer, 1, 998);
      MPJDev.WORLD.send(charBuffer, 1, 997);
      MPJDev.WORLD.send(doubleBuffer, 1, 996);
      MPJDev.WORLD.send(longBuffer, 1, 995);
      MPJDev.WORLD.send(booleanBuffer, 1, 994);
      MPJDev.WORLD.send(shortBuffer, 1, 993);
      MPJDev.WORLD.send(floatBuffer, 1, 992);
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
          2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36, 38, 42, 44,
          46, 48, 52, 54, 56, 58,
          62, 64, 66, 68, 72, 74, 76, 78, 82, 84, 86, 88, 92, 94, 96, 98};

      Buffer intBuffer = new Buffer(160 + 8);
      Buffer floatBuffer = new Buffer(160 + 8);
      Buffer doubleBuffer = new Buffer(320 + 8);
      Buffer shortBuffer = new Buffer(80 + 8);
      Buffer booleanBuffer = new Buffer(40 + 8);
      Buffer charBuffer = new Buffer(80 + 8);
      Buffer longBuffer = new Buffer(320 + 8);
      Buffer byteBuffer = new Buffer(40 + 8);

      intBuffer = new Buffer(160 + 8);
      floatBuffer = new Buffer(160 + 8);
      doubleBuffer = new Buffer(320 + 8);
      shortBuffer = new Buffer(80 + 8);
      booleanBuffer = new Buffer(40 + 8);
      charBuffer = new Buffer(80 + 8);
      longBuffer = new Buffer(320 + 8);
      byteBuffer = new Buffer(40 + 8);

      MPJDev.WORLD.recv(intBuffer, 0, 999);
      MPJDev.WORLD.recv(byteBuffer, 0, 998);
      MPJDev.WORLD.recv(charBuffer, 0, 997);
      MPJDev.WORLD.recv(doubleBuffer, 0, 996);
      MPJDev.WORLD.recv(longBuffer, 0, 995);
      MPJDev.WORLD.recv(booleanBuffer, 0, 994);
      MPJDev.WORLD.recv(shortBuffer, 0, 993);
      MPJDev.WORLD.recv(floatBuffer, 0, 992);

      intBuffer.commit();
      floatBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      shortBuffer.commit();
      charBuffer.commit();
      byteBuffer.commit();
      booleanBuffer.commit();

      try {
        intBuffer.getSectionHeader(Buffer.INT);
        intBuffer.scatter(intReadArray, 40, 0, indexes);
        floatBuffer.getSectionHeader(Buffer.FLOAT);
        floatBuffer.scatter(floatReadArray, 40, 0, indexes);
        doubleBuffer.getSectionHeader(Buffer.DOUBLE);
        doubleBuffer.scatter(doubleReadArray, 40, 0, indexes);
        longBuffer.getSectionHeader(Buffer.LONG);
        longBuffer.scatter(longReadArray, 40, 0, indexes);
        shortBuffer.getSectionHeader(Buffer.SHORT);
        shortBuffer.scatter(shortReadArray, 40, 0, indexes);
        charBuffer.getSectionHeader(Buffer.CHAR);
        charBuffer.scatter(charReadArray, 40, 0, indexes);
        byteBuffer.getSectionHeader(Buffer.BYTE);
        byteBuffer.scatter(byteReadArray, 40, 0, indexes);
        booleanBuffer.getSectionHeader(Buffer.BOOLEAN);
        booleanBuffer.scatter(booleanReadArray, 40, 0, indexes);
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
    //This should be the last call, in order to finish the communication
    MPJDev.finish();
  }

  public static void main(String args[]) throws Exception {
    BufferTest3 test = new BufferTest3(args);
  }
}
