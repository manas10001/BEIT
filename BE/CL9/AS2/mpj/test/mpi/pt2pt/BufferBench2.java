package mpi.pt2pt;

import mpi.*;
import mpjbuf.*;
import java.util.Arrays;

public class BufferBench2 {
  public BufferBench2() {
  }

  public BufferBench2(String args[]) throws Exception {

    long start = 0L;
    long end = 0L;
    long maxMemory = 0L, freeMemory = 0L, totalMemory = 0L;
    mpjbuf.RawBuffer buffer[] = new mpjbuf.RawBuffer[30];
    int i = 0, j = 0;
    Runtime rt = Runtime.getRuntime();
    System.out.println("# size allocation_time used_mem "
	+ "total_memory max_memory");

    for (int u = 0; u < 100; u++) {
      for (j = 128; j <= 4 * 1024 * 1024; j *= 2) {
	buffer[0] = BufferFactory.create(j);
	BufferFactory.destroy(buffer[0]);
      }
    }

    for (j = 128; j <= 16 * 1024 * 1024; j *= 2) {
      start = System.nanoTime();
      buffer[i] = BufferFactory.create(j);
      end = System.nanoTime();
      maxMemory = rt.maxMemory();
      totalMemory = rt.totalMemory();
      freeMemory = rt.freeMemory();
      System.out.println(j + " " + (end - start) / 1000 + " "
	  + (totalMemory - freeMemory) / (1024 * 1024) + " " + totalMemory
	  / (1024 * 1024) + " " + maxMemory / (1024 * 1024));
      i++;
    }

    for (int k = i - 1; k >= 0; k--) {
      // System.out.println("*** calling destroy ***"+k);
      BufferFactory.destroy(buffer[k]);
      // System.out.println("*** destroyed successfully called ***"+k);
    }

  }

  public static void main(String args[]) throws Exception {
    BufferBench2 test = new BufferBench2(args);
  }

}
