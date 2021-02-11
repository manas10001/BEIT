package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * This checks the gathering/scattering of the Java objects. Doesn't work at the
 * moment
 */
public class BufferTest8 {
  static public void main(String[] args) throws Exception {
    try {
      BufferTest8 a = new BufferTest8(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTest8() {
  }

  public BufferTest8(String[] args) throws Exception {

    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);

    int indexes[] = { 2, 4, 6, 8 };
    // the first call will always be init()

    int id = MPJDev.WORLD.id();
    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTest8: Must run with 2 tasks!");
      // MPJDev.WORLD.barrier();
      MPJDev.finish();
      return;
    }

    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

    java.util.Vector vector1 = null;
    java.util.Vector vector = new java.util.Vector();
    vector.add("1");
    vector.add("2");

    if (MPJDev.WORLD.id() == 0) {
      Object[] source = new Object[100];
      for (int j = 0; j < source.length; j++) {
	source[j] = vector;
      }
      Buffer writeBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      writeBuffer.putSectionHeader(Type.OBJECT);
      writeBuffer.strGather(source, 0, 2, 0, 2, indexes);
      writeBuffer.commit();
      // try { Thread.currentThread().sleep(100); }catch(Exception e){}
      MPJDev.WORLD.send(writeBuffer, 1, 992, true);
      BufferFactory.destroy(writeBuffer.getStaticBuffer());
      // System.out.println("Send Completed \n");
    } else if (MPJDev.WORLD.id() == 1) {
      Buffer readBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);

      Object[] source = new Object[100];

      for (int j = 0; j < source.length; j++) {
	source[j] = null;
      }

      MPJDev.WORLD.recv(readBuffer, 0, 992, true);
      readBuffer.commit();
      readBuffer.getSectionHeader();
      int r = readBuffer.getSectionSize();
      try {
	readBuffer.strScatter(source, 0, 2, 0, 2, indexes);
      }
      catch (Exception e) {
	e.printStackTrace();
      }

      BufferFactory.destroy(readBuffer.getStaticBuffer());
      /*
       * for (int j = 0; j < source.length; j++) { System.out.print("\t source["
       * + j + "] :: " + source[j]); }
       */
      System.out.println("BufferTest8 TEST Completed (?)");
    }

    // MPJDev.WORLD.barrier();
    MPJDev.finish();
  }
}
