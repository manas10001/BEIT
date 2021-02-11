package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * This checks the gathering/scattering of the Java objects.
 */
public class BufferTest7 {
  static public void main(String[] args) throws Exception {
    try {
      BufferTest7 a = new BufferTest7(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTest7() {
  }

  public BufferTest7(String[] args) throws Exception {

    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);

    int indexes[] = { 2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36,
	38, 42, 44, 46, 48, 52, 54, 56, 58, 62, 64, 66, 68, 72, 74, 76, 78, 82,
	84, 86, 88, 92, 94, 96, 98 };

    int id = MPJDev.WORLD.id();
    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTest7: Must run with 2 tasks!");
      MPJDev.finish();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

    java.util.Vector vector1 = null;
    java.util.Vector vector = new java.util.Vector();

    for (int k = 0; k < 10; k++) {
      vector.add("" + k);
    }
    if (MPJDev.WORLD.id() == 0) {

      Object[] source = new Object[100];

      for (int k = 0; k < source.length; k++) {
	source[k] = vector;
      }

      Buffer writeBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      writeBuffer.putSectionHeader(Type.OBJECT);
      writeBuffer.gather(source, 40, 0, indexes);
      writeBuffer.commit();
      MPJDev.WORLD.send(writeBuffer, 1, 992, true);
      // System.out.println("Send Completed");
      writeBuffer.clear();

      BufferFactory.destroy(writeBuffer.getStaticBuffer());

    } else if (MPJDev.WORLD.id() == 1) {

      Buffer readBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);

      Object[] source = new Object[100];

      for (int k = 0; k < source.length; k++) {
	source[k] = null;
      }

      MPJDev.WORLD.recv(readBuffer, 0, 992, true);
      readBuffer.commit();
      readBuffer.getSectionHeader();
      int els = readBuffer.getSectionSize();

      // System.out.println(" els " + els);

      try {
	readBuffer.scatter(source, 40, 0, indexes);
	// System.out.println("Receive Completed ");
      }
      catch (Exception e) {
	e.printStackTrace();
      }

      BufferFactory.destroy(readBuffer.getStaticBuffer());

      System.out.println("BufferTest7 TEST Completed (?)");
      /*
       * for (int j = 0; j < source.length; j++) { System.out.println("source["
       * + j + "] :: " + source[j]); }
       */

    }

    // This should be the last call, in order to finish the communication
    // try { MPJDev.WORLD.barrier(); }catch(Exception e){}
    MPJDev.finish();
  }
}
