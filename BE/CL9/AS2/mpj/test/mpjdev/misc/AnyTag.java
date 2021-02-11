import mpjdev.*;
import mpjbuf.*;
import java.util.Arrays;

public class AnyTag {

  public static void main(String args[]) throws Exception {
    int DATA_SIZE = 100;
    Comm.init(args);

    for (int h = 0; h < 1000; h++) {
      System.out.println("\n\n\n************************<TEST==" + h +
                         ">**************************\n\n\n");
      Comm.out.println("\n\n\n************************<TEST==" + h +
                       ">**************************\n\n\n");
      int intArray[] = new int[DATA_SIZE];

      for (int i = 0; i < DATA_SIZE; i++) {
        intArray[i] = i + 1;
      }

      if (Comm.WORLD.id() == 0) {
        System.out.println("Writing intBuffer");
        Buffer intBuffer = new Buffer( (DATA_SIZE * 4) + 8);
        intBuffer.putSectionHeader(Buffer.INT);
        intBuffer.write(intArray, 0, DATA_SIZE);
        intBuffer.commit();

        for (int k = 1; k < Comm.WORLD.size(); k++) {
          Comm.WORLD.send(intBuffer, k, h);
        }

        System.out.println("Send Completed \n\n");

      }
      else {
        int intReadArray[] = new int[DATA_SIZE];

        for (int i = 0; i < intReadArray.length; i++) {
          intReadArray[i] = -1;
        }

        Buffer intBuffer = new Buffer( (DATA_SIZE * 4) + 8);
        System.out.println("Receving ints ");
        Comm.WORLD.recv(intBuffer, Comm.WORLD.ANY_SOURCE, Comm.WORLD.ANY_TAG);
        intBuffer.commit();
        intBuffer.getSectionHeader(Buffer.INT);
        System.out.println("Read Int");
        intBuffer.read(intReadArray, 0, DATA_SIZE);

        if (Arrays.equals(intArray, intReadArray)) {
          System.out.println("Passed");
        }
        else {
          System.out.println("Failed");
        }

      }

      //This should be the last call, in order to finish the communication
    } //end big for loop.

    //Comm.WORLD.barrier();
    try {
      Thread.currentThread().sleep(100000);
    }
    catch (Exception e) {}
    Comm.finish();
  } //end constr
} //end class
