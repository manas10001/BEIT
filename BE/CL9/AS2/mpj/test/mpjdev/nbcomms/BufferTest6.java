package buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * This checks the sending/receiving of the java objects.
 * @author Aamir Shafi
 * Distributed Systems Group
 * Portsmouth
 */
public class BufferTest6 {
  public static void main(String args[]) throws Exception {
    MPJDev.init(args);   

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

      Buffer writeBuffer = new Buffer(8);
      writeBuffer.putSectionHeader(Buffer.OBJECT);
      writeBuffer.write(source, 0, 1);
      writeBuffer.commit();
      MPJDev.WORLD.send(writeBuffer, 1, 992);
      System.out.println("Send Completed");

    } else if (MPJDev.WORLD.id() == 1) {
      Buffer readBuffer = new Buffer(8);
      Object[] source = new Object[5];
      source[0] = null;
      source[1] = null;
      source[2] = null;
      source[3] = null;
      source[4] = null;
      MPJDev.WORLD.recv(readBuffer, 0, 992);
      readBuffer.commit();
      readBuffer.getSectionHeader(Buffer.OBJECT);

      try {
        readBuffer.read(source, 0, 1);
        System.out.println("Receive Completed");
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      vector1 = (java.util.Vector) source[0];

      if (vector1.equals(vector)) {
        System.out.println("SUCCESS_FUL");
      }
      else {
        System.out.println("NOT Successful");
      }
    }
    try {
      Thread.currentThread().sleep(1000);
    }
    catch (Exception e) {}
    MPJDev.finish();
  }
}
