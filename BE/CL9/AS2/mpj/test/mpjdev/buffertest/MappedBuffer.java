package mpjdev.buffertest;

import mpjdev.*;
import mpjbuf.*;
import java.util.Arrays;
import java.nio.*;
import java.nio.channels.FileChannel.*;
import java.nio.channels.*;
import java.io.*;

public class MappedBuffer {
  public MappedBuffer() {
  }

  static public void main(String[] args) throws Exception {
    try {
      MappedBuffer a = new MappedBuffer(args);
    }
    catch (Exception e) {
    }
  }

  public MappedBuffer(String[] args) throws Exception {

    int DATA_SIZE = 100;
    File f = new File("mappedfile1");
    RandomAccessFile raf = new RandomAccessFile(f, "rw");
    FileChannel fileChannel = raf.getChannel();
    MappedByteBuffer mappedByteBuffer = fileChannel.map(MapMode.READ_WRITE, 0,
	400);// f.length());

    MPJDev.init(args);
    int intArray[] = new int[DATA_SIZE];

    for (int k = 1; k < 2; k++) {

      for (int i = 0; i < DATA_SIZE; i++) {
	intArray[i] = 8;
      }

      if (MPJDev.WORLD.id() == 0) {
	for (int i = 0; i < DATA_SIZE; i++) {
	  mappedByteBuffer.putInt(intArray[i]);
	}
	// mappedByteBuffer.flip();
      } else if (MPJDev.WORLD.id() == 1) {
	/* Just make sure that this reads when the data is in there */
	try {
	  Thread.currentThread().sleep(1000);
	}
	catch (Exception e) {
	}
	int intReadArray[] = new int[DATA_SIZE];
	for (int i = 0; i < DATA_SIZE; i++) {
	  intReadArray[i] = 9;
	}
	for (int i = 0; i < DATA_SIZE; i++) {
	  intReadArray[i] = mappedByteBuffer.getInt();
	}
	if (Arrays.equals(intArray, intReadArray)) {
	  System.out.println("\n#################" + "\n <<<<PASSED>>>> "
	      + "\n################");
	} else {
	  System.out.println("\n#################" + "\n <<<<FAILED>>>> "
	      + "\n################");
	}
      }
    }

    try {
      Thread.currentThread().sleep(10000);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    raf.close();
    // f.close();
    fileChannel.close();
    MPJDev.finish();
  }
}
