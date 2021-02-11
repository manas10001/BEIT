package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

public class BufferTestDyna1 {

  static public void main(String[] args) throws Exception {
    try {
      BufferTestDyna1 a = new BufferTestDyna1(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTestDyna1() {
  }

  public BufferTestDyna1(String[] args) throws Exception {

    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);
    int DATA_SIZE = 100;
    int h = 10;

    int id = MPJDev.WORLD.id();
    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1) {
	System.out.println("BufferTestDyna1: Must run with 2 tasks!");
      }
      // MPJDev.WORLD.barrier();
      MPJDev.finish();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

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

      Buffer byteBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);
      byteBuffer.putSectionHeader(Type.BYTE_DYNAMIC);
      byteBuffer.write(byteArray, 0, DATA_SIZE); // write the array of DATA_SIZE
						 // bytes
      byteBuffer.commit();

      Buffer charBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      charBuffer.putSectionHeader(Type.CHAR_DYNAMIC);
      charBuffer.write(charArray, 0, DATA_SIZE); // write the array of DATA_SIZE
						 // char
      charBuffer.commit();

      Buffer intBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      intBuffer.putSectionHeader(Type.INT_DYNAMIC);
      intBuffer.write(intArray, 0, DATA_SIZE); // write the array of DATA_SIZE
					       // char
      intBuffer.commit();

      // System.out.println("Writing shortBuffer");
      Buffer shortBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      shortBuffer.putSectionHeader(Type.SHORT_DYNAMIC);
      shortBuffer.write(shortArray, 0, DATA_SIZE);
      shortBuffer.commit();

      // System.out.println("Writing booleanBuffer");
      Buffer booleanBuffer = new Buffer(
	  BufferFactory.create(8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  8 + SEND_OVERHEAD);

      booleanBuffer.putSectionHeader(Type.BOOLEAN_DYNAMIC);
      booleanBuffer.write(booleanArray, 0, DATA_SIZE);
      booleanBuffer.commit();

      // System.out.println("Writing long Buffer");
      Buffer longBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      longBuffer.putSectionHeader(Type.LONG_DYNAMIC);
      longBuffer.write(longArray, 0, DATA_SIZE);
      longBuffer.commit();

      // System.out.println("Writing double buffer");
      Buffer doubleBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      doubleBuffer.putSectionHeader(Type.DOUBLE_DYNAMIC);
      doubleBuffer.write(doubleArray, 0, DATA_SIZE);
      doubleBuffer.commit();

      // System.out.println("Writing float Buffer");
      Buffer floatBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      floatBuffer.putSectionHeader(Type.FLOAT_DYNAMIC);
      floatBuffer.write(floatArray, 0, DATA_SIZE);
      floatBuffer.commit();
      // uses blocking send to
      MPJDev.WORLD.send(intBuffer, 1, (1 + (h * 10)), true);
      MPJDev.WORLD.send(byteBuffer, 1, (2 + (h * 10)), true);
      MPJDev.WORLD.send(charBuffer, 1, (3 + (h * 10)), true);
      MPJDev.WORLD.send(doubleBuffer, 1, (4 + (h * 10)), true);
      MPJDev.WORLD.send(longBuffer, 1, (5 + (h * 10)), true);
      MPJDev.WORLD.send(booleanBuffer, 1, (6 + (h * 10)), true);
      MPJDev.WORLD.send(shortBuffer, 1, (7 + (h * 10)), true);
      MPJDev.WORLD.send(floatBuffer, 1, (8 + (h * 10)), true);
      // System.out.println("Send Completed \n\n");

      BufferFactory.destroy(intBuffer.getStaticBuffer());
      BufferFactory.destroy(charBuffer.getStaticBuffer());
      BufferFactory.destroy(shortBuffer.getStaticBuffer());
      BufferFactory.destroy(longBuffer.getStaticBuffer());
      BufferFactory.destroy(booleanBuffer.getStaticBuffer());
      BufferFactory.destroy(floatBuffer.getStaticBuffer());
      BufferFactory.destroy(byteBuffer.getStaticBuffer());
      BufferFactory.destroy(doubleBuffer.getStaticBuffer());

    } else if (MPJDev.WORLD.id() == 1) {

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

      // System.out.println("Receving ints ");
      MPJDev.WORLD.recv(intBuffer, 0, (1 + (h * 10)), true);
      // System.out.println("Receving bytes");
      MPJDev.WORLD.recv(byteBuffer, 0, (2 + (10 * h)), true);
      // System.out.println("Receving chars");
      MPJDev.WORLD.recv(charBuffer, 0, (3 + (10 * h)), true);
      // System.out.println("Receving doubles");
      MPJDev.WORLD.recv(doubleBuffer, 0, (4 + (10 * h)), true);
      // System.out.println("Receving longs");
      MPJDev.WORLD.recv(longBuffer, 0, (5 + (10 * h)), true);
      // System.out.println("Receving bools");
      MPJDev.WORLD.recv(booleanBuffer, 0, (6 + (10 * h)), true);
      // System.out.println("Receving shorts");
      MPJDev.WORLD.recv(shortBuffer, 0, (7 + (10 * h)), true);
      // System.out.println("Receving floats");
      MPJDev.WORLD.recv(floatBuffer, 0, (8 + (10 * h)), true);

      intBuffer.commit();
      floatBuffer.commit();
      doubleBuffer.commit();
      longBuffer.commit();
      shortBuffer.commit();
      byteBuffer.commit();
      booleanBuffer.commit();
      charBuffer.commit();

      try {
	intBuffer.getSectionHeader();
	intBuffer.getSectionSize();
	floatBuffer.getSectionHeader();
	floatBuffer.getSectionSize();
	doubleBuffer.getSectionHeader();
	doubleBuffer.getSectionSize();
	longBuffer.getSectionHeader();
	longBuffer.getSectionSize();
	shortBuffer.getSectionHeader();
	shortBuffer.getSectionSize();
	byteBuffer.getSectionHeader();
	byteBuffer.getSectionSize();
	booleanBuffer.getSectionHeader();
	booleanBuffer.getSectionSize();
	charBuffer.getSectionHeader();
	charBuffer.getSectionSize();

	// System.out.println("Read Int");
	intBuffer.read(intReadArray, 0, DATA_SIZE);
	// System.out.println("Reading Float");
	floatBuffer.read(floatReadArray, 0, DATA_SIZE);
	// System.out.println("Reading Double");
	doubleBuffer.read(doubleReadArray, 0, DATA_SIZE);
	// System.out.println("Reading Long");
	longBuffer.read(longReadArray, 0, DATA_SIZE);
	// System.out.println("Reading Short");
	shortBuffer.read(shortReadArray, 0, DATA_SIZE);
	// System.out.println("Reading Char");
	charBuffer.read(charReadArray, 0, DATA_SIZE);
	// System.out.println("Reading Byte");
	byteBuffer.read(byteReadArray, 0, DATA_SIZE);
	// System.out.println("Reading Boolean");
	booleanBuffer.read(booleanReadArray, 0, DATA_SIZE);
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

      System.out.println("BufferTestDyna1 TEST Completed (?)");
    }

    // MPJDev.WORLD.barrier();
    MPJDev.finish();

  }
}
