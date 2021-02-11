package mpjdev.buffertest;

import mpjdev.*;

import mpjbuf.*;

/*
 * Gather/Scatter Operation on a single
 */
public class BufferTest4 {

  public BufferTest4(String args[]) throws Exception {

    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);

    int size = MPJDev.WORLD.size();
    int id = MPJDev.WORLD.id();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTest4: Must run with 2 tasks!");
      MPJDev.finish();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();
    int weirdSize = 40 + 80 + 160 + 80 + 40 + 320 + 320 + 160 + (8 * 8) + 100;

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

      int indexes[] = { 2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36,
	  38, 42, 44, 46, 48, 52, 54, 56, 58, 62, 64, 66, 68, 72, 74, 76, 78,
	  82, 84, 86, 88, 92, 94, 96, 98 };

      Buffer buffer = new Buffer(
	  BufferFactory.create(weirdSize + SEND_OVERHEAD), SEND_OVERHEAD,
	  SEND_OVERHEAD + weirdSize);

      buffer.putSectionHeader(Type.BYTE);
      buffer.gather(byteArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.CHAR);
      buffer.gather(charArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.INT);
      buffer.gather(intArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.SHORT);
      buffer.gather(shortArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.BOOLEAN);
      buffer.gather(booleanArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.LONG);
      buffer.gather(longArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.DOUBLE);
      buffer.gather(doubleArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.FLOAT);
      buffer.gather(floatArray, 40, 0, indexes);
      buffer.commit();
      MPJDev.WORLD.send(buffer, 1, 999, true);

      BufferFactory.destroy(buffer.getStaticBuffer());

      // Buffer, destinationRank, tag (make sure you have matchin recv also
      // called)
      // System.out.println("Send Completed \n\n");
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
      Buffer buffer = new Buffer(
	  BufferFactory.create(weirdSize + RECV_OVERHEAD), RECV_OVERHEAD,
	  RECV_OVERHEAD + weirdSize);

      MPJDev.WORLD.recv(buffer, 0, 999, true);
      int indexes[] = { 2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36,
	  38, 42, 44, 46, 48, 52, 54, 56, 58, 62, 64, 66, 68, 72, 74, 76, 78,
	  82, 84, 86, 88, 92, 94, 96, 98 };
      buffer.commit();

      try {
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(byteReadArray, 40, 0, indexes);
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(charReadArray, 40, 0, indexes);
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(intReadArray, 40, 0, indexes);
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(shortReadArray, 40, 0, indexes);
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(booleanReadArray, 40, 0, indexes);
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(longReadArray, 40, 0, indexes);
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(doubleReadArray, 40, 0, indexes);
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.scatter(floatReadArray, 40, 0, indexes);
      }
      catch (Exception e) {
	e.printStackTrace();
      }

      BufferFactory.destroy(buffer.getStaticBuffer());

      /*
       * 
       * System.out.print(
       * "\t********* DISPLAYING THE INT ARRAY READ *********\n\n"); for (int j
       * = 0; j < intReadArray.length; j++) { System.out.print("int[" + j + "]="
       * + intReadArray[j] + "\t"); }
       * 
       * System.out.print(
       * "\t******** DISPLAYING THE FLOAT ARRAY READ ********\n\n"); for (int j
       * = 0; j < floatReadArray.length; j++) { System.out.print("float[" + j +
       * "]=" + floatReadArray[j] + "\t"); }
       * 
       * System.out.print(
       * "\t******* DISPLAYING THE DOUBLE ARRAY READ ********\n\n"); for (int j
       * = 0; j < doubleReadArray.length; j++) { System.out.print("double[" + j
       * + "]=" + doubleReadArray[j] + "\t"); }
       * 
       * System.out.print("\t******** DISPLAYING THE LONG ARRAY READ ********\n\n"
       * ); for (int j = 0; j < longReadArray.length; j++) {
       * System.out.print("long[" + j + "]=" + longReadArray[j] + "\t"); }
       * 
       * System.out.print(
       * "\t******** DISPLAYING THE SHORT ARRAY READ ********\n\n"); for (int j
       * = 0; j < shortReadArray.length; j++) { System.out.print("short[" + j +
       * "]=" + shortReadArray[j] + "\t"); }
       * 
       * System.out.print(
       * "\t******** DISPLAYING THE FLOAT ARRAY READ ********\n\n"); for (int j
       * = 0; j < charReadArray.length; j++) { System.out.print("char[" + j +
       * "]=" + charReadArray[j] + "\t"); }
       * 
       * System.out.print("\t******** DISPLAYING THE BYTE ARRAY READ ********\n\n"
       * ); for (int j = 0; j < byteReadArray.length; j++) {
       * System.out.print("byte[" + j + "]=" + byteReadArray[j] + "\t"); }
       * 
       * System.out.print(
       * "\t******** DISPLAYING THE BOOLEAN ARRAY READ ********\n\n"); for (int
       * j = 0; j < booleanReadArray.length; j++) { System.out.print("boolean["
       * + j + "]=" + booleanReadArray[j] + "\t"); }
       * System.out.println("Receive Completed \n\n");
       */

    }

    if (id == 1)
      System.out.println("BufferTest4 TEST Completed (?)" + id);
    // MPJDev.WORLD.barrier();
    MPJDev.finish();
  }

  public static void main(String args[]) throws Exception {
    BufferTest4 test = new BufferTest4(args);
  }
}
