package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * Gather methods of Buffer Class, and Scatter methods of Buffer Class.
 */
public class BufferTestDyna3 {

  static public void main(String[] args) throws Exception {
    try {
      BufferTestDyna3 a = new BufferTestDyna3(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTestDyna3() {
  }

  public BufferTestDyna3(String[] args) throws Exception {

    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);

    int id = MPJDev.WORLD.id();
    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTestDyna3: Must run with 2 tasks!");
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

      Buffer byteBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      byteBuffer.putSectionHeader(Type.BYTE_DYNAMIC);
      byteBuffer.gather(byteArray, 40, 0, indexes);
      byteBuffer.commit();

      Buffer charBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);
      charBuffer.putSectionHeader(Type.CHAR_DYNAMIC);
      charBuffer.gather(charArray, 40, 0, indexes);
      charBuffer.commit();

      Buffer intBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);
      intBuffer.putSectionHeader(Type.INT_DYNAMIC);
      intBuffer.gather(intArray, 40, 0, indexes);
      intBuffer.commit();

      // (100*2)+section-overhead(8bytes)+NOPADDING
      Buffer shortBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);
      shortBuffer.putSectionHeader(Type.SHORT_DYNAMIC);
      shortBuffer.gather(shortArray, 40, 0, indexes);
      shortBuffer.commit();

      Buffer booleanBuffer = new Buffer(
	  BufferFactory.create(8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  8 + SEND_OVERHEAD);
      booleanBuffer.putSectionHeader(Type.BOOLEAN_DYNAMIC);
      booleanBuffer.gather(booleanArray, 40, 0, indexes);
      booleanBuffer.commit();

      Buffer longBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);
      longBuffer.putSectionHeader(Type.LONG_DYNAMIC);
      longBuffer.gather(longArray, 40, 0, indexes);
      longBuffer.commit();

      Buffer doubleBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);
      doubleBuffer.putSectionHeader(Type.DOUBLE_DYNAMIC);
      doubleBuffer.gather(doubleArray, 40, 0, indexes);
      doubleBuffer.commit();

      Buffer floatBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);
      floatBuffer.putSectionHeader(Type.FLOAT_DYNAMIC);
      floatBuffer.gather(floatArray, 40, 0, indexes);
      floatBuffer.commit();
      /********* PACKING OF THE BUFFER *****************/
      // uses blocking send to
      MPJDev.WORLD.send(intBuffer, 1, 999, true); // Buffer, destinationRank,
						  // tag (make sure you have
						  // matchin recv also called)
      MPJDev.WORLD.send(byteBuffer, 1, 998, true);
      MPJDev.WORLD.send(charBuffer, 1, 997, true);
      MPJDev.WORLD.send(doubleBuffer, 1, 996, true);
      MPJDev.WORLD.send(longBuffer, 1, 995, true);
      MPJDev.WORLD.send(booleanBuffer, 1, 994, true);
      MPJDev.WORLD.send(shortBuffer, 1, 993, true);
      MPJDev.WORLD.send(floatBuffer, 1, 992, true);
      /********* PACKING OF THE BUFFER *****************/
      // System.out.println("Send Completed \n\n");
      BufferFactory.destroy(intBuffer.getStaticBuffer());
      BufferFactory.destroy(charBuffer.getStaticBuffer());
      BufferFactory.destroy(shortBuffer.getStaticBuffer());
      BufferFactory.destroy(longBuffer.getStaticBuffer());
      BufferFactory.destroy(booleanBuffer.getStaticBuffer());
      BufferFactory.destroy(floatBuffer.getStaticBuffer());
      BufferFactory.destroy(byteBuffer.getStaticBuffer());
      BufferFactory.destroy(doubleBuffer.getStaticBuffer());

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

      int indexes[] = { 2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36,
	  38, 42, 44, 46, 48, 52, 54, 56, 58, 62, 64, 66, 68, 72, 74, 76, 78,
	  82, 84, 86, 88, 92, 94, 96, 98 };

      Buffer intBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);
      Buffer floatBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);
      Buffer doubleBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);
      Buffer shortBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);
      Buffer booleanBuffer = new Buffer(
	  BufferFactory.create(8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  8 + RECV_OVERHEAD);
      Buffer charBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);
      Buffer longBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);
      Buffer byteBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);

      MPJDev.WORLD.recv(intBuffer, 0, 999, true);
      MPJDev.WORLD.recv(byteBuffer, 0, 998, true);
      MPJDev.WORLD.recv(charBuffer, 0, 997, true);
      MPJDev.WORLD.recv(doubleBuffer, 0, 996, true);
      MPJDev.WORLD.recv(longBuffer, 0, 995, true);
      MPJDev.WORLD.recv(booleanBuffer, 0, 994, true);
      MPJDev.WORLD.recv(shortBuffer, 0, 993, true);
      MPJDev.WORLD.recv(floatBuffer, 0, 992, true);

      intBuffer.commit();
      floatBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      shortBuffer.commit();
      charBuffer.commit();
      byteBuffer.commit();
      booleanBuffer.commit();

      try {
	intBuffer.getSectionHeader();
	intBuffer.getSectionSize();
	intBuffer.scatter(intReadArray, 40, 0, indexes);

	floatBuffer.getSectionHeader();
	floatBuffer.getSectionSize();
	floatBuffer.scatter(floatReadArray, 40, 0, indexes);

	doubleBuffer.getSectionHeader();
	doubleBuffer.getSectionSize();
	doubleBuffer.scatter(doubleReadArray, 40, 0, indexes);

	longBuffer.getSectionHeader();
	longBuffer.getSectionSize();
	longBuffer.scatter(longReadArray, 40, 0, indexes);

	shortBuffer.getSectionHeader();
	shortBuffer.getSectionSize();
	shortBuffer.scatter(shortReadArray, 40, 0, indexes);

	charBuffer.getSectionHeader();
	charBuffer.getSectionSize();
	charBuffer.scatter(charReadArray, 40, 0, indexes);

	byteBuffer.getSectionHeader();
	byteBuffer.getSectionSize();
	byteBuffer.scatter(byteReadArray, 40, 0, indexes);

	booleanBuffer.getSectionHeader();
	booleanBuffer.getSectionSize();
	booleanBuffer.scatter(booleanReadArray, 40, 0, indexes);
      }
      catch (Exception e) {
	e.printStackTrace();
      }

      BufferFactory.destroy(intBuffer.getStaticBuffer());
      BufferFactory.destroy(charBuffer.getStaticBuffer());
      BufferFactory.destroy(shortBuffer.getStaticBuffer());
      BufferFactory.destroy(longBuffer.getStaticBuffer());
      BufferFactory.destroy(booleanBuffer.getStaticBuffer());
      BufferFactory.destroy(floatBuffer.getStaticBuffer());
      BufferFactory.destroy(byteBuffer.getStaticBuffer());
      BufferFactory.destroy(doubleBuffer.getStaticBuffer());

      System.out.println("BufferTestDyna3 TEST Completed (?) ");
    }

    // MPJDev.WORLD.barrier();
    // This should be the last call, in order to finish the communication
    MPJDev.finish();
  }
}
