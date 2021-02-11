package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

/*
 * Gather methods of Buffer Class, and Scatter methods of Buffer Class.
 */
public class BufferTest5 {

  public BufferTest5(String args[]) throws Exception {

    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);

    int id = MPJDev.WORLD.id();
    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTest5: Must run with 2 tasks!");
      MPJDev.finish();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

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

      int indexes[] = { 2, 4, 6, 8 };

      Buffer floatBuffer = new Buffer(
	  BufferFactory.create(40 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  40 + 8 + SEND_OVERHEAD);
      floatBuffer.putSectionHeader(Type.FLOAT);
      floatBuffer.strGather(floatArray, 0, 2, 0, 2, indexes);

      Buffer byteBuffer = new Buffer(
	  BufferFactory.create(16 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  16 + 8 + SEND_OVERHEAD);
      byteBuffer.putSectionHeader(Type.BYTE);
      byteBuffer.strGather(byteArray, 0, 2, 0, 2, indexes);

      Buffer charBuffer = new Buffer(
	  BufferFactory.create(24 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  24 + 8 + SEND_OVERHEAD);
      charBuffer.putSectionHeader(Type.CHAR);
      charBuffer.strGather(charArray, 0, 2, 0, 2, indexes);

      Buffer intBuffer = new Buffer(
	  BufferFactory.create(40 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  40 + 8 + SEND_OVERHEAD);
      intBuffer.putSectionHeader(Type.INT);
      intBuffer.strGather(intArray, 0, 2, 0, 2, indexes);

      Buffer shortBuffer = new Buffer(
	  BufferFactory.create(24 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  24 + 8 + SEND_OVERHEAD);
      shortBuffer.putSectionHeader(Type.SHORT);
      shortBuffer.strGather(shortArray, 0, 2, 0, 2, indexes);

      Buffer booleanBuffer = new Buffer(
	  BufferFactory.create(16 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  16 + 8 + SEND_OVERHEAD);
      booleanBuffer.putSectionHeader(Type.BOOLEAN);
      booleanBuffer.strGather(booleanArray, 0, 2, 0, 2, indexes);

      Buffer longBuffer = new Buffer(
	  BufferFactory.create(72 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  72 + 8 + SEND_OVERHEAD);
      longBuffer.putSectionHeader(Type.LONG);
      longBuffer.strGather(longArray, 0, 2, 0, 2, indexes);

      Buffer doubleBuffer = new Buffer(
	  BufferFactory.create(72 + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  72 + 8 + SEND_OVERHEAD);
      doubleBuffer.putSectionHeader(Type.DOUBLE);
      doubleBuffer.strGather(doubleArray, 0, 2, 0, 2, indexes);

      /********* PACKING OF THE BUFFER *****************/
      // uses blocking send to
      floatBuffer.commit();
      intBuffer.commit();
      byteBuffer.commit();
      charBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      booleanBuffer.commit();
      shortBuffer.commit();

      MPJDev.WORLD.send(floatBuffer, 1, 992, true);
      MPJDev.WORLD.send(intBuffer, 1, 999, true);
      MPJDev.WORLD.send(byteBuffer, 1, 998, true);
      MPJDev.WORLD.send(charBuffer, 1, 997, true);
      MPJDev.WORLD.send(doubleBuffer, 1, 996, true);
      MPJDev.WORLD.send(longBuffer, 1, 995, true);
      MPJDev.WORLD.send(booleanBuffer, 1, 994, true);
      MPJDev.WORLD.send(shortBuffer, 1, 993, true);
      /********* PACKING OF THE BUFFER *****************/

      BufferFactory.destroy(intBuffer.getStaticBuffer());
      BufferFactory.destroy(charBuffer.getStaticBuffer());
      BufferFactory.destroy(shortBuffer.getStaticBuffer());
      BufferFactory.destroy(longBuffer.getStaticBuffer());
      BufferFactory.destroy(booleanBuffer.getStaticBuffer());
      BufferFactory.destroy(floatBuffer.getStaticBuffer());
      BufferFactory.destroy(byteBuffer.getStaticBuffer());
      BufferFactory.destroy(doubleBuffer.getStaticBuffer());

    } else if (MPJDev.WORLD.id() == 1) {

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

      int indexes[] = { 2, 4, 6, 8 };

      Buffer intBuffer = new Buffer(
	  BufferFactory.create(40 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  40 + 8 + RECV_OVERHEAD);
      Buffer floatBuffer = new Buffer(
	  BufferFactory.create(40 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  40 + 8 + RECV_OVERHEAD);
      Buffer doubleBuffer = new Buffer(
	  BufferFactory.create(72 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  72 + 8 + RECV_OVERHEAD);
      Buffer shortBuffer = new Buffer(
	  BufferFactory.create(24 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  24 + 8 + RECV_OVERHEAD);
      Buffer booleanBuffer = new Buffer(
	  BufferFactory.create(16 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  16 + 8 + RECV_OVERHEAD);
      Buffer charBuffer = new Buffer(
	  BufferFactory.create(24 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  24 + 8 + RECV_OVERHEAD);
      Buffer longBuffer = new Buffer(
	  BufferFactory.create(72 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  72 + 8 + RECV_OVERHEAD);
      Buffer byteBuffer = new Buffer(
	  BufferFactory.create(16 + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  16 + 8 + RECV_OVERHEAD);

      MPJDev.WORLD.recv(floatBuffer, 0, 992, true);
      MPJDev.WORLD.recv(intBuffer, 0, 999, true);
      MPJDev.WORLD.recv(byteBuffer, 0, 998, true);
      MPJDev.WORLD.recv(charBuffer, 0, 997, true);
      MPJDev.WORLD.recv(doubleBuffer, 0, 996, true);
      MPJDev.WORLD.recv(longBuffer, 0, 995, true);
      MPJDev.WORLD.recv(booleanBuffer, 0, 994, true);
      MPJDev.WORLD.recv(shortBuffer, 0, 993, true);

      floatBuffer.commit();
      intBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      shortBuffer.commit();
      charBuffer.commit();
      byteBuffer.commit();
      booleanBuffer.commit();

      try {
	floatBuffer.getSectionHeader();
	floatBuffer.getSectionSize();
	floatBuffer.strScatter(floatReadArray, 0, 2, 0, 2, indexes);

	intBuffer.getSectionHeader();
	intBuffer.getSectionSize();
	intBuffer.strScatter(intReadArray, 0, 2, 0, 2, indexes);

	doubleBuffer.getSectionHeader();
	doubleBuffer.getSectionSize();
	doubleBuffer.strScatter(doubleReadArray, 0, 2, 0, 2, indexes);

	longBuffer.getSectionHeader();
	longBuffer.getSectionSize();
	longBuffer.strScatter(longReadArray, 0, 2, 0, 2, indexes);

	shortBuffer.getSectionHeader();
	shortBuffer.getSectionSize();
	shortBuffer.strScatter(shortReadArray, 0, 2, 0, 2, indexes);

	charBuffer.getSectionHeader();
	charBuffer.getSectionSize();
	charBuffer.strScatter(charReadArray, 0, 2, 0, 2, indexes);

	byteBuffer.getSectionHeader();
	byteBuffer.getSectionSize();
	byteBuffer.strScatter(byteReadArray, 0, 2, 0, 2, indexes);

	booleanBuffer.getSectionHeader();
	booleanBuffer.getSectionSize();
	booleanBuffer.strScatter(booleanReadArray, 0, 2, 0, 2, indexes);
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
      /*
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
       * 
       * System.out.println("Receive Completed \n\n");
       */
      // System.out.println("***BufferTest5***");

    }

    // This should be the last call, in order to finish the communication
    // MPJDev.WORLD.barrier();

    if (id == 1) {
      System.out.println("BufferTest5 TEST Completed (?)" + id);
    }

    MPJDev.finish();
  }

  public static void main(String args[]) throws Exception {
    BufferTest5 test = new BufferTest5(args);
  }
}
