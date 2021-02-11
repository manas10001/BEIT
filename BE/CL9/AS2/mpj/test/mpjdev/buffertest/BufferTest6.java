package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * This checks the sending/receiving of the java objects.
 */
public class BufferTest6 {
  static public void main(String[] args) throws Exception {
    try {
      BufferTest6 a = new BufferTest6(args);
    }
    catch (Exception e) {
    }
  }

  public BufferTest6(String[] args) throws Exception {
    String[] nargs = new String[args.length + 1];
    System.arraycopy(args, 0, nargs, 0, args.length);
    nargs[args.length] = this.toString();
    MPJDev.init(args);

    int id = MPJDev.WORLD.id();

    int size = MPJDev.WORLD.size();

    if (size > 2) {
      if (id == 1)
	System.out.println("BufferTest6: Must run with 2 tasks!");
      MPJDev.finish();
      return;
    }
    int SEND_OVERHEAD = MPJDev.getSendOverhead();
    int RECV_OVERHEAD = MPJDev.getRecvOverhead();

    java.util.Vector vector1 = null;
    java.util.Vector vector = new java.util.Vector();

    for (int i = 0; i < 10; i++) {
      vector.add(i + "");
    }

    if (MPJDev.WORLD.id() == 0) {
      Object[] source = new Object[5];
      source[0] = vector;
      source[1] = vector;
      source[2] = null;
      source[3] = null;
      source[4] = null;

      Buffer writeBuffer = new Buffer(BufferFactory.create(8 + SEND_OVERHEAD),
	  SEND_OVERHEAD, 8 + SEND_OVERHEAD);

      writeBuffer.putSectionHeader(Type.OBJECT);
      writeBuffer.write(source, 0, 1);

      writeBuffer.commit();
      MPJDev.WORLD.send(writeBuffer, 1, 992, true);
      BufferFactory.destroy(writeBuffer.getStaticBuffer());

    } else if (MPJDev.WORLD.id() == 1) {
      Buffer readBuffer = new Buffer(BufferFactory.create(8 + RECV_OVERHEAD),
	  RECV_OVERHEAD, 8 + RECV_OVERHEAD);
      Object[] source = new Object[5];
      source[0] = null;
      source[1] = null;
      source[2] = null;
      source[3] = null;
      source[4] = null;

      MPJDev.WORLD.recv(readBuffer, 0, 992, true);
      readBuffer.commit();
      readBuffer.getSectionHeader();
      readBuffer.getSectionSize();
      try {
	readBuffer.read(source, 0, 1);
      }
      catch (Exception e) {
	e.printStackTrace();
      }

      BufferFactory.destroy(readBuffer.getStaticBuffer());

      vector1 = (java.util.Vector) source[0];

      if (vector1.equals(vector)) {
	System.out.println("BufferTest6 TEST Completed" + id);
      } else {
	System.out.println("BufferTest6 TEST (Unsuccessful) Completed");
      }

    }

    MPJDev.finish();
  }
}
