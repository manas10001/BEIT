package buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * This checks the gathering/scattering of the Java objects.
 */
public class BufferTest7 {
  public static void main(String args[]) throws Exception {

    int indexes[] = {
        2, 4, 6, 8, 12, 14, 16, 18, 22, 24, 26, 28, 32, 34, 36, 38, 42, 44, 46,
        48, 52, 54, 56, 58,
        62, 64, 66, 68, 72, 74, 76, 78, 82, 84, 86, 88, 92, 94, 96, 98};
    //the first call will always be init()
    MPJDev.init(args);

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

      Buffer writeBuffer = new Buffer(8);
      writeBuffer.putSectionHeader(Buffer.OBJECT);
      writeBuffer.gather(source, 40, 0, indexes);
      writeBuffer.commit();
      MPJDev.WORLD.send(writeBuffer, 1, 992);
      System.out.println("Send Completed");
      writeBuffer.clear();

    }
    else if (MPJDev.WORLD.id() == 1) {

      Buffer readBuffer = new Buffer(8);
      Object[] source = new Object[100];

      for (int k = 0; k < source.length; k++) {
        source[k] = null;
      }

      MPJDev.WORLD.recv(readBuffer, 0, 992);
      readBuffer.commit();
      int els = readBuffer.getSectionHeader(Buffer.OBJECT);
      System.out.println(" els " + els);

      try {
        readBuffer.scatter(source, 40, 0, indexes);
        System.out.println("Receive Completed ");
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      for (int j = 0; j < source.length; j++) {
        System.out.println("source[" + j + "] :: " + source[j]);
      }

    }

    //This should be the last call, in order to finish the communication
    //MPJDev.WORLD.nbarrier();
    MPJDev.finish();
  }
}
