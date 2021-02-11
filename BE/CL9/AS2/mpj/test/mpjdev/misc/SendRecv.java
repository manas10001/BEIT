/*
 * File         : SendRecv.java
 * Author       : Sang Lim
 * Revision     : $Revision: 1.1.1.1 $
 * Updated      : $Date: 2005/03/19 16:06:39 $
 */
import mpjdev.*;
import mpjbuf.*;
import java.io.*;

public class SendRecv {

  public static void main(String[] args) throws Exception {

    Comm.init(args);
    Buffer wb = new Buffer(500);
    int id = Comm.WORLD.id();
    int size = Comm.WORLD.size();

    if (id == 0) {
      float[] fArray0 = new float[50];

      for (int i = 0; i < fArray0.length; i++)
        fArray0[i] = i;

      wb.putSectionHeader(Buffer.FLOAT);
      wb.write(fArray0, 0, fArray0.length);

      int[] indexes = new int[30];
      indexes[0] = 10;
      indexes[1] = 2;
      indexes[5] = 4;
      indexes[6] = 3;
      int srcOff = 7, rank = 2, exts = 5, strs = 0;
      wb.strGather(fArray0, srcOff, rank, exts, strs, indexes);
      indexes[0] = 0;
      indexes[1] = 1;

      for (int i = 2; i < 30; i++)
        indexes[i] = indexes[i - 1] + 2;

      wb.gather(fArray0, 24, 2, indexes);
      indexes[0] = 0;
      indexes[1] = 0;

      for (int i = 2; i < 30; i++)
        indexes[i] = indexes[i - 1] + 2;

      wb.gather(fArray0, 24, 2, indexes);
      wb.commit();

      Comm.WORLD.send(wb, 1, 99);

    }
    else if (id == 1) {
      Buffer rb = new Buffer(500);
      Comm.WORLD.recv(rb, 0, 99);
      rb.commit();
      float[] rArray0 = new float[50];
      int numEls = rb.getSectionHeader(Buffer.FLOAT);
      System.out.println("numEls (as read by getSectionHeader()" + numEls);
      rb.read(rArray0, 0, rArray0.length);
      System.out.println("******* WRITE SECTION OF MESSAGE *******");

      for (int i = 0; i < rArray0.length; i++)
        System.out.println("First section of message[" + i + "] = " + rArray0[i]);

      rb.read(rArray0, 0, 12);
      System.out.println("\n\n******* READ SECTION OF MESSAGE *******");

      for (int i = 0; i < 12; i++)
        System.out.println("First section of message[" + i + "] = " +
                           rArray0[i]);

      rb.read(rArray0, 0, 24);
      System.out.println("\n\n******* ODD GATHER SECTION OF MESSAGE *******");
      for (int i = 0; i < 24; i++)
        System.out.println("First section of message[" + i + "] = " +
                           rArray0[i]);

      System.out.println("\n\n******* EVEN GATHER SECTION OF MESSAGE *******");
      rb.read(rArray0, 0, 24);

      for (int i = 0; i < 24; i++)
        System.out.println("Second section of message[" + i + "] = " +
                           rArray0[i]);

      System.out.println("\n\n******* STRGATHER SECTION OF MESSAGE *******");
      rb.commit(); // Rewind buffer
      rb.getSectionHeader(Buffer.FLOAT);

      rb.read(rArray0, 0, 50);
      for (int i = 0; i < 50; i++)
        rArray0[i] = 0;

      int[] indexes = new int[30];

      for (int i = 0; i < 50; i++)
        rArray0[i] = 0;

      indexes[0] = 10;
      indexes[1] = 2;
      indexes[5] = 4;
      indexes[6] = 3;

      int dstOff = 7, rank = 2, exts = 5, strs = 0;
      rb.strScatter(rArray0, dstOff, rank, exts, strs, indexes);

      System.out.println("\n\n******* STRSCATTERED MESSAGE *******");

      for (int i = 0; i < 50; i++)
        System.out.println("StrScatter of message[" + i + "] = " +
                           rArray0[i]);

      indexes[0] = 0;
      indexes[1] = 1;

      for (int i = 2; i < 30; i++)
        indexes[i] = indexes[i - 1] + 2;

      rb.scatter(rArray0, 24, 2, indexes);
      indexes[0] = 0;
      indexes[1] = 0;

      for (int i = 2; i < 30; i++)
        indexes[i] = indexes[i - 1] + 2;

      rb.scatter(rArray0, 24, 2, indexes);
      System.out.println("\n\n******* SCATTERED MESSAGE *******");

      for (int i = 0; i < 50; i++)
        System.out.println("Scattered message[" + i + "] = " + rArray0[i]);

    }

    //Comm.WORLD.barrier();
    Comm.finish();
  }
}

