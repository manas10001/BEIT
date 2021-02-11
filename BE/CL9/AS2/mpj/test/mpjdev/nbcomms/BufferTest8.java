package buffertest;

import mpjdev.*;
import mpjbuf.*;

/**
 * This checks the gathering/scattering of the Java objects.
 * Doesn't work at the moment
 */
public class BufferTest8 {
  public static void main(String args[]) throws Exception {
    int indexes[] = {
        2, 4, 6, 8};
    //the first call will always be init()   
    MPJDev.init(args);
    
    java.util.Vector vector1 = null;
    java.util.Vector vector = new java.util.Vector();
    vector.add("1");
    vector.add("2");

    if (MPJDev.WORLD.id() == 0) {
      Object[] source = new Object[100];
      for (int j = 0; j < source.length; j++) {
        source[j] = vector;
      }
      Buffer writeBuffer = new Buffer(8);
      writeBuffer.putSectionHeader(Buffer.OBJECT);
      writeBuffer.strGather(source, 0, 2, 0, 2, indexes);
      //try { Thread.currentThread().sleep(100); }catch(Exception e){}
      MPJDev.WORLD.send(writeBuffer, 1, 992);
      System.out.println("Send Completed \n");
    }
    else if (MPJDev.WORLD.id() == 1) {
      Buffer readBuffer = new Buffer(8);
      Object[] source = new Object[100];

      for (int j = 0; j < source.length; j++) {
        source[j] = null;
      }

      MPJDev.WORLD.recv(readBuffer, 0, 992);
      readBuffer.commit();
      System.out.println("numEls " + readBuffer.getSectionHeader(Buffer.OBJECT));
      try {
        readBuffer.strScatter(source, 0, 2, 0, 2, indexes);
        System.out.println("Receive Completed \n");
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      for (int j = 0; j < source.length; j++) {
        System.out.print("\t source[" + j + "] :: " + source[j]);
      }
    }

    //This should be the last call, in order to finish the communication
    //MPJDev.WORLD.nbarrier();
    MPJDev.finish();
  }
}
