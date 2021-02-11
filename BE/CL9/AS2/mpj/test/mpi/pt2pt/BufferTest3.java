package mpi.pt2pt;

import mpi.*;
import mpjbuf.*;
import java.util.Arrays;

public class BufferTest3 {
  public BufferTest3() {
  }

  public BufferTest3(String args[]) throws Exception {
    int offset = 0, size = 100;
    int DATA_SIZE = 2;
    int[] data = new int[DATA_SIZE];
    int[] rdata = new int[DATA_SIZE];

    for (int i = 0; i < data.length; i++) {
      data[i] = i;
    }

    RawBuffer rawBuffer = BufferFactory.create(size);

    // 1. create mpjbuf.Buffer object ..
    mpjbuf.Buffer mpjbuf = new mpjbuf.Buffer(rawBuffer, offset, size);

    // 2. write message ...
    mpjbuf.putSectionHeader(Type.INT);
    mpjbuf.write(data, 0, 2);

    mpjbuf.commit();

    // 3. read message ...
    Type sectionHeader = mpjbuf.getSectionHeader();
    int sectionSize = mpjbuf.getSectionSize();
    mpjbuf.read(rdata, 0, 2);
    mpjbuf.clear();

    // 4. compare results ...
    for (int i = 0; i < rdata.length; i++) {
      if (rdata[i] != i) {
	System.out.println(" Error: arrays don't match");
	break;
      }
    }

    BufferFactory.destroy(mpjbuf.getStaticBuffer());

    /*
     * long start = System.nanoTime(); long end = System.nanoTime();
     * mpjbuf.Buffer buffer[] = new mpjbuf.Buffer[100]; int i=0;
     * 
     * for(int j=128 ; j<=256*1024 ; j*=2) { start = System.nanoTime();
     * buffer[i] = Buddy1BufferFactory.createBuffer(j) ; end =
     * System.nanoTime(); i++; }
     * 
     * int p=0; int[] writeArray = null ; int[] readArray = null ; int intCount
     * = -1;
     * 
     * for(int j=128 ; j<=256*1024 ; j*=2) { intCount = (j-8)/4; writeArray =
     * new int[ intCount ]; readArray = new int[ intCount ]; for(int u=0 ;
     * u<intCount ; u++) { writeArray[u] = u; readArray[u] = 0; }
     * 
     * for(int q=0 ; q<1000 ; q++) {
     * buffer[p].putSectionHeader(mpjbuf.Type.INT); buffer[p].write(writeArray,
     * 0, intCount); buffer[p].commit(); buffer[p].getSectionHeader();
     * buffer[p].getSectionSize (); buffer[p].read(readArray, 0, intCount);
     * buffer[p].clear();
     * 
     * if(java.util.Arrays.equals(writeArray, readArray)) {
     * System.out.println("PASSED <"+j+">"); } else {
     * System.out.println("FAILED"); } } p++; }
     * 
     * p=0;
     * 
     * for(int j=128 ; j<=256*1024 ; j*=2) { intCount = (j-8)/4; writeArray =
     * new int[ intCount ]; readArray = new int[ intCount ]; for(int u=0 ;
     * u<intCount ; u++) { writeArray[u] = u*j; readArray[u] = 0; }
     * buffer[p].putSectionHeader(mpjbuf.Type.INT); buffer[p].write(writeArray,
     * 0, intCount); p++; }
     * 
     * p=0;
     * 
     * for(int j=128 ; j<=256*1024 ; j*=2) { intCount = (j-8)/4; writeArray =
     * new int[ intCount ]; readArray = new int[ intCount ]; for(int u=0 ;
     * u<intCount ; u++) { writeArray[u] = u*j; readArray[u] = 0; }
     * 
     * buffer[p].commit(); buffer[p].getSectionHeader(); int numEls =
     * buffer[p].getSectionSize ();
     * 
     * buffer[p].read(readArray, 0, intCount); buffer[p].clear();
     * 
     * for(int t=0 ; t< 10 ; t++) { System.out.print("w["+t+"]="+writeArray[t]);
     * }
     * 
     * System.out.println("");
     * 
     * for(int t=0 ; t< 10 ; t++) { System.out.print("r["+t+"]="+readArray[t]);
     * }
     * 
     * System.out.println("");
     * 
     * if(java.util.Arrays.equals(writeArray, readArray)) {
     * System.out.println("PASSED <"+j+">"); } else {
     * System.out.println("FAILED <"+j+">"); }
     * 
     * p++; }
     * 
     * for(int k=i-1 ; k>=0 ; k--) {
     * //System.out.println("*** calling destroy ***"+k);
     * Buddy1BufferFactory.destroyBuffer( buffer[k]);
     * //System.out.println("*** destroyed successfully called ***"+k); }
     */

  }

  public static void main(String args[]) throws Exception {
    BufferTest3 test = new BufferTest3(args);
  }

}
