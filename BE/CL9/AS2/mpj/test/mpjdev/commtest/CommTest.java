package commtest;

import mpjdev.*;
import mpjbuf.*;
import java.util.Arrays;

public class CommTest {

  public static void main(String args[]) throws Exception {
    int DATA_SIZE = 100;
    MPJDev.init(args);
    /* Creating the new communicator */
    byte byteArray[] = new byte[DATA_SIZE];

    for (int i = 0; i < DATA_SIZE; i++) {
      byteArray[i] = 's';
    }
    
    int[] newIds = {2, 3, 4};
    Comm newComm = MPJDev.WORLD.create(newIds);
    
    int[] newIds1 = {5, 6, 7};
    Comm newComm1 = MPJDev.WORLD.create(newIds1);

    int[] newIds2 = {0, 1};
    Comm newComm2 = MPJDev.WORLD.create(newIds2);    
   
    int[] newIds3 = {0, 2, 4, 6};
    Comm newComm3 = MPJDev.WORLD.create(newIds3);
    System.out.println("<"+MPJDev.WORLD.id()+">==<"+newComm3.id());

    if (MPJDev.WORLD.id() == 0 && newComm3.id() == 0) {
      Buffer byteBuffer = new Buffer(DATA_SIZE + 8); 
      byteBuffer.putSectionHeader(Buffer.BYTE);
      byteBuffer.write(byteArray, 0, DATA_SIZE); 
      byteBuffer.commit(); 
      System.out.println("Sending bytes");
      newComm3.send(byteBuffer, 1, 90);
    }
    else if ( ( (MPJDev.WORLD.id() == 2) || (MPJDev.WORLD.id() == 4) ||
		(MPJDev.WORLD.id() == 6) ) && newComm3.id() == 1) {
      
      byte byteReadArray[] = new byte[DATA_SIZE];
      
      for (int i = 0; i < DATA_SIZE; i++) {
        byteReadArray[i] = 'x';
      }

      Buffer byteBuffer = new Buffer( (DATA_SIZE) + 8);
      System.out.println("Receving bytes");
      newComm3.recv(byteBuffer, 0, 90);
      byteBuffer.commit();

      try {
        byteBuffer.getSectionHeader(Buffer.BYTE);
        byteBuffer.read(byteReadArray, 0, DATA_SIZE);
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      if (Arrays.equals(byteArray, byteReadArray)) {
        System.out.println("\n#################" +
                           "\n <<<<PASSED>>>> " +
                           "\n################");
      }
      else {
        System.out.println("\n#################" +
                           "\n <<<<FAILED>>>> " +
                           "\n################");
        System.exit(0);
      }

    } //end while(true)

    //System.out.println("Calling the barrier");
    //MPJDev.WORLD.nbarrier();
    //System.out.println("Barrier ends");
    try {
      Thread.currentThread().sleep(10000);
    }
    catch (Exception e) {}
    MPJDev.finish();

  }
}
