package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

public class BufferTestDyna2 {

  static public void main(String[] args) throws Exception {
    try {
      BufferTestDyna2 a = new BufferTestDyna2(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTestDyna2() {
  }

  public BufferTestDyna2(String[] args) throws Exception {
    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);

    int id = MPJDev.WORLD.id();
    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1) {
	System.out.println("BufferTestDyna2: Must run with 2 tasks!");
      }
      // MPJDev.WORLD.barrier();
      MPJDev.finish();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

    if (MPJDev.WORLD.id() == 0) {
      /********* THINGS TO BE SENT ******************/
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

      /********* PACKING OF THE BUFFER *****************/
      Buffer buffer = new Buffer(BufferFactory.create(8 * 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, SEND_OVERHEAD + (8 * 8));
      buffer.putSectionHeader(Type.BYTE_DYNAMIC);
      buffer.write(byteArray, 0, 100); // writes the first section
      buffer.putSectionHeader(Type.CHAR_DYNAMIC);
      buffer.write(charArray, 0, 100); // writes teh second section
      buffer.putSectionHeader(Type.INT_DYNAMIC);
      buffer.write(intArray, 0, 100); // and so on ....
      buffer.putSectionHeader(Type.SHORT_DYNAMIC);
      buffer.write(shortArray, 0, 100);
      buffer.putSectionHeader(Type.BOOLEAN_DYNAMIC);
      buffer.write(booleanArray, 0, 100);
      buffer.putSectionHeader(Type.LONG_DYNAMIC);
      buffer.write(longArray, 0, 100);
      buffer.putSectionHeader(Type.DOUBLE_DYNAMIC);
      buffer.write(doubleArray, 0, 100);
      buffer.putSectionHeader(Type.FLOAT_DYNAMIC);
      buffer.write(floatArray, 0, 100);
      /********* PACKING OF THE BUFFER *****************/
      buffer.commit();
      MPJDev.WORLD.send(buffer, 1, 999, true);
      // Buffer, destinationRank, tag (make sure you
      // have matchin recv also called)
      // System.out.println("Send Completed \n\n");
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
      Buffer buffer = new Buffer(BufferFactory.create(8 * 8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, RECV_OVERHEAD + (8 * 8));
      MPJDev.WORLD.recv(buffer, 0, 999, true);
      buffer.commit();

      try {
	// System.out.println("Read byte array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(byteReadArray, 0, 100); // reads the first sectiona

	// System.out.println("Read char array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(charReadArray, 0, 100); // reads the second section

	// System.out.println("Read int array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(intReadArray, 0, 100); // and so on ....

	// System.out.println("Read short array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(shortReadArray, 0, 100);

	// System.out.println("Read boolean array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(booleanReadArray, 0, 100);

	// System.out.println("Read long array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(longReadArray, 0, 100);

	// System.out.println("Read double array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(doubleReadArray, 0, 100);

	// System.out.println("Read float array");
	buffer.getSectionHeader();
	buffer.getSectionSize();
	buffer.read(floatReadArray, 0, 100);
      }
      catch (Exception e) {
	e.printStackTrace();
	MPJDev.finish();
      }

      BufferFactory.destroy(buffer.getStaticBuffer());

    }

    if (id == 1) {
      System.out.println("BufferTestDyna2 TEST Completed (?) " + id);
    }
    // MPJDev.WORLD.barrier();
    // This should be the last call, in order to finish the communication
    MPJDev.finish();
  }
}
