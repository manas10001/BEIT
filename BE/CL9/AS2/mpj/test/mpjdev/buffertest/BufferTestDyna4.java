package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * Gather/Scatter Operation on a single
 */
public class BufferTestDyna4 {
  static public void main(String[] args) throws Exception {
    try {
      BufferTestDyna4 a = new BufferTestDyna4(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTestDyna4() {
  }

  public BufferTestDyna4(String[] args) throws Exception {

    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);
    int id = MPJDev.WORLD.id();
    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTestDyna4: Must run with 2 tasks!");
      // MPJDev.WORLD.barrier();
      MPJDev.finish();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

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

      Buffer buffer = new Buffer(BufferFactory.create((8 * 8) + SEND_OVERHEAD),
	  SEND_OVERHEAD, (8 * 8) + SEND_OVERHEAD);
      buffer.putSectionHeader(Type.BYTE_DYNAMIC);
      buffer.gather(byteArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.CHAR_DYNAMIC);
      buffer.gather(charArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.INT_DYNAMIC);
      buffer.gather(intArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.SHORT_DYNAMIC);
      buffer.gather(shortArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.BOOLEAN_DYNAMIC);
      buffer.gather(booleanArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.LONG_DYNAMIC);
      buffer.gather(longArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.DOUBLE_DYNAMIC);
      buffer.gather(doubleArray, 40, 0, indexes);
      buffer.putSectionHeader(Type.FLOAT_DYNAMIC);
      buffer.gather(floatArray, 40, 0, indexes);
      buffer.commit();
      MPJDev.WORLD.send(buffer, 1, 999, true); // Buffer, destinationRank, tag
					       // (make sure you have matchin
					       // recv also called)
      // System.out.println("Send Completed \n\n");
      //
      BufferFactory.destroy(buffer.getStaticBuffer());
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
      // Same rules as above apply here as well. For the size of the things.
      Buffer buffer = new Buffer(BufferFactory.create((8 * 8) + RECV_OVERHEAD),
	  RECV_OVERHEAD, (8 * 8) + RECV_OVERHEAD);

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
      System.out.println("BufferTestDyna4 TEST Completed (?)");
    }

    // MPJDev.WORLD.barrier();
    MPJDev.finish();
  }
}
