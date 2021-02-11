package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;
import mpi.*;
import java.util.Arrays;

/*
 * This test sends and receives all of the eight basic datatypes.
 * sends each datatype as a separate section.
 */

public class BufferTest1 {
  public BufferTest1() {
  }

  static public void main(String[] args) throws Exception {
    try {
      BufferTest1 a = new BufferTest1(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTest1(String args[]) throws Exception {
    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPI.Init(args);

    int id = -1;
    id = MPI.COMM_WORLD.Rank();
    int size = MPI.COMM_WORLD.Size();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTest1, must run with 2 processes");
      MPI.COMM_WORLD.Barrier();
      MPI.Finalize();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

    int DATA_SIZE = 100;
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

    int h = 1;

    if (id == 0) {
      mpjbuf.Buffer byteBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE + 8 + SEND_OVERHEAD), SEND_OVERHEAD,
	  (DATA_SIZE + 8) + SEND_OVERHEAD);
      byteBuffer.putSectionHeader(mpjbuf.Type.BYTE);
      byteBuffer.write(byteArray, 0, DATA_SIZE);
      byteBuffer.commit();

      mpjbuf.Buffer charBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 2 + 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, (DATA_SIZE * 2 + 8) + SEND_OVERHEAD);
      charBuffer.putSectionHeader(mpjbuf.Type.CHAR);
      charBuffer.write(charArray, 0, DATA_SIZE);
      charBuffer.commit();

      mpjbuf.Buffer intBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 4 + 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, (DATA_SIZE * 4 + 8) + SEND_OVERHEAD);

      intBuffer.putSectionHeader(mpjbuf.Type.INT);
      intBuffer.write(intArray, 0, DATA_SIZE);
      intBuffer.commit();

      mpjbuf.Buffer shortBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 2 + 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, (DATA_SIZE * 2 + 8) + SEND_OVERHEAD);
      shortBuffer.putSectionHeader(mpjbuf.Type.SHORT);
      shortBuffer.write(shortArray, 0, DATA_SIZE);
      shortBuffer.commit();

      mpjbuf.Buffer booleanBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 2 + 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, (DATA_SIZE * 2 + 8) + SEND_OVERHEAD);
      booleanBuffer.putSectionHeader(mpjbuf.Type.BOOLEAN);
      booleanBuffer.write(booleanArray, 0, DATA_SIZE);
      booleanBuffer.commit();

      mpjbuf.Buffer longBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 8 + 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, (DATA_SIZE * 8 + 8) + SEND_OVERHEAD);
      longBuffer.putSectionHeader(mpjbuf.Type.LONG);
      longBuffer.write(longArray, 0, DATA_SIZE);
      longBuffer.commit();

      mpjbuf.Buffer doubleBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 8 + 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, (DATA_SIZE * 8 + 8) + SEND_OVERHEAD);
      doubleBuffer.putSectionHeader(mpjbuf.Type.DOUBLE);
      doubleBuffer.write(doubleArray, 0, DATA_SIZE);
      doubleBuffer.commit();

      mpjbuf.Buffer floatBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 4 + 8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, (DATA_SIZE * 4 + 8) + SEND_OVERHEAD);
      floatBuffer.putSectionHeader(mpjbuf.Type.FLOAT);
      floatBuffer.write(floatArray, 0, DATA_SIZE);
      floatBuffer.commit();

      // System.out.println("Sending integers"+id);
      MPI.COMM_WORLD.mpjdevComm.send(intBuffer, 1, (1 + (h * 10)), true);

      // System.out.println("Sent integers");
      // System.out.println("Sending bytes");
      MPI.COMM_WORLD.mpjdevComm.send(byteBuffer, 1, (2 + (h * 10)), true);

      // System.out.println("Sending chars");
      MPI.COMM_WORLD.mpjdevComm.send(charBuffer, 1, (3 + (h * 10)), true);
      // System.out.println("Sending doubles");

      MPI.COMM_WORLD.mpjdevComm.send(doubleBuffer, 1, (4 + (h * 10)), true);
      // System.out.println("Sending longs");

      MPI.COMM_WORLD.mpjdevComm.send(longBuffer, 1, (5 + (h * 10)), true);
      // System.out.println("Sending bols");

      MPI.COMM_WORLD.mpjdevComm.send(booleanBuffer, 1, (6 + (h * 10)), true);
      // System.out.println("Sending shorts");

      MPI.COMM_WORLD.mpjdevComm.send(shortBuffer, 1, (7 + (h * 10)), true);
      // System.out.println("Sending floats");

      MPI.COMM_WORLD.mpjdevComm.send(floatBuffer, 1, (8 + (h * 10)), true);
      // System.out.println("Send Completed \n\n");
      //
      BufferFactory.destroy(intBuffer.getStaticBuffer());
      BufferFactory.destroy(charBuffer.getStaticBuffer());
      BufferFactory.destroy(shortBuffer.getStaticBuffer());
      BufferFactory.destroy(longBuffer.getStaticBuffer());
      BufferFactory.destroy(booleanBuffer.getStaticBuffer());
      BufferFactory.destroy(floatBuffer.getStaticBuffer());
      BufferFactory.destroy(byteBuffer.getStaticBuffer());
      BufferFactory.destroy(doubleBuffer.getStaticBuffer());
    }

    else if (id == 1) {

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

      mpjbuf.Buffer intBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 4 + 8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, (DATA_SIZE * 4 + 8) + RECV_OVERHEAD);

      mpjbuf.Buffer floatBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 4 + 8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, (DATA_SIZE * 4 + 8) + RECV_OVERHEAD);

      mpjbuf.Buffer doubleBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 8 + 8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, (DATA_SIZE * 8 + 8) + RECV_OVERHEAD);

      mpjbuf.Buffer shortBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 2 + 8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, (DATA_SIZE * 2 + 8) + RECV_OVERHEAD);

      mpjbuf.Buffer booleanBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  (DATA_SIZE + 8) + RECV_OVERHEAD);

      mpjbuf.Buffer charBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 2 + 8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, (DATA_SIZE * 2 + 8) + RECV_OVERHEAD);

      mpjbuf.Buffer longBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE * 8 + 8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, (DATA_SIZE * 8 + 8) + RECV_OVERHEAD);

      mpjbuf.Buffer byteBuffer = new mpjbuf.Buffer(
	  BufferFactory.create(DATA_SIZE + 8 + RECV_OVERHEAD), RECV_OVERHEAD,
	  (DATA_SIZE + 8) + RECV_OVERHEAD);

      // System.out.println("Receving ints ");

      MPI.COMM_WORLD.mpjdevComm.recv(intBuffer, 0, (1 + (h * 10)), true);
      // System.out.println("Receving bytes");

      MPI.COMM_WORLD.mpjdevComm.recv(byteBuffer, 0, (2 + (10 * h)), true);
      // System.out.println("Receving chars");

      MPI.COMM_WORLD.mpjdevComm.recv(charBuffer, 0, (3 + (10 * h)), true);
      // System.out.println("Receving doubles");

      MPI.COMM_WORLD.mpjdevComm.recv(doubleBuffer, 0, (4 + (10 * h)), true);
      // System.out.println("Receving longs");

      MPI.COMM_WORLD.mpjdevComm.recv(longBuffer, 0, (5 + (10 * h)), true);
      // System.out.println("Receving bools");

      MPI.COMM_WORLD.mpjdevComm.recv(booleanBuffer, 0, (6 + (10 * h)), true);
      // System.out.println("Receving shorts");

      MPI.COMM_WORLD.mpjdevComm.recv(shortBuffer, 0, (7 + (10 * h)), true);
      // System.out.println("Receving floats");

      MPI.COMM_WORLD.mpjdevComm.recv(floatBuffer, 0, (8 + (10 * h)), true);

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
	intBuffer.read(intReadArray, 0, DATA_SIZE);
	floatBuffer.read(floatReadArray, 0, DATA_SIZE);
	doubleBuffer.read(doubleReadArray, 0, DATA_SIZE);
	longBuffer.read(longReadArray, 0, DATA_SIZE);
	shortBuffer.read(shortReadArray, 0, DATA_SIZE);
	charBuffer.read(charReadArray, 0, DATA_SIZE);
	byteBuffer.read(byteReadArray, 0, DATA_SIZE);
	booleanBuffer.read(booleanReadArray, 0, DATA_SIZE);
      }

      catch (Exception e) {
	e.printStackTrace();
      }

      if (Arrays.equals(intArray, intReadArray)
	  && Arrays.equals(floatArray, floatReadArray)
	  && Arrays.equals(doubleArray, doubleReadArray)
	  && Arrays.equals(longArray, longReadArray)
	  && Arrays.equals(shortArray, shortReadArray)
	  && Arrays.equals(charArray, charReadArray)
	  && Arrays.equals(byteArray, byteReadArray)
	  && Arrays.equals(booleanArray, booleanReadArray)) {
	/*
	 * System.out.println("\n####mpjbuf.BufferTest1####" +
	 * "\n####<<PASSED>>####" + "\n####iteration<"+h+">####");
	 */
      } else {
	System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	    + "\n################");
	System.exit(0);
      }
      BufferFactory.destroy(intBuffer.getStaticBuffer());
      BufferFactory.destroy(charBuffer.getStaticBuffer());
      BufferFactory.destroy(shortBuffer.getStaticBuffer());
      BufferFactory.destroy(longBuffer.getStaticBuffer());
      BufferFactory.destroy(booleanBuffer.getStaticBuffer());
      BufferFactory.destroy(floatBuffer.getStaticBuffer());
      BufferFactory.destroy(byteBuffer.getStaticBuffer());
      BufferFactory.destroy(doubleBuffer.getStaticBuffer());
    }

    MPI.Finalize();

    if (id == 1) {
      System.out.println("BufferTest1 TEST COMPLETE <" + id + ">");
    }

  }

}
