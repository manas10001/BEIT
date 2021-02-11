/* This file generated automatically from template NIOBuffer.java.in. */

/*
The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package mpjbuf;
import java.nio.*;

/**
 * Native methods implementation of `RawBuffer'.
 *
 * Apart from array bounds checking in `gather' and `scatter', none of these
 * methods check validity of arguments.  Passing invalid arguments will
 * generally crash the JVM.  So direct use of this class is <i>very unsafe</i>.
 * This class is designed specifically for use from `mpjbuf.Buffer', which
 * provides all the necessary guarantees.
 */
public class NIOBuffer implements RawBuffer {
    private ByteBuffer buffer = null;
    private int capacity ;

    NIOBuffer() {
    }

    NIOBuffer(int capacity) {
        this.capacity = capacity ;
				//for native device we introduce allocateDirect
        buffer = ByteBuffer.allocateDirect(capacity);
    }

    public NIOBuffer(int capacity, ByteBuffer slicedBuffer) {
        this.capacity = capacity ;
	this.buffer = slicedBuffer ;
    }

    public void copy(ByteBuffer srcBuffer, int srcOffset, int srcLength, 
		    int dstOffset) {
      this.buffer.position( dstOffset );
      srcBuffer.limit( srcLength + srcOffset );
      srcBuffer.position( srcOffset );
      this.buffer.put(srcBuffer) ;
      this.buffer.flip() ;
    }
		    

    public int getCapacity() {
        return capacity ;
    }

    /* introduced for NIODevice */
    public void setBuffer(ByteBuffer buf) {
        this.buffer.put(buf);
    }

    /* introduced for NIODevice */
    public ByteBuffer getBuffer() {
        return buffer;
    }

    /* introduced for NIODevice ?*/
    public void setEncoding(ByteOrder order) {
            buffer.order(order);
    }

    public void free() {
      buffer.clear();
    }
    
    public void clear() {
      buffer.clear();
    }

    public void putByte(int value, int bufOff) {
            buffer.position(bufOff);
            buffer.put((byte)value);
    }

    public int getByte(int bufOff) {
	    buffer.clear() ;
            buffer.position(bufOff);
            byte b = buffer.get();
            return b;
    }

    public void putInt(int value, int bufOff) {
            buffer.position(bufOff);
            buffer.putInt(value);
    }

    public int getInt(int bufOff, boolean revBytes) {
            buffer.position(bufOff);
            int i = buffer.getInt();
            return i;
    }

  /******************************* BYTE **********************************/

  public void write(byte[] source, int srcOff, int numEls,
                    int bufOff) {
    buffer.position(bufOff);
    buffer.put(source, srcOff, numEls); //(byte only)
  }

  public int gather(byte[] source,
                    int numEls, int offs, int[] indexes,
                    int bufOff) {
    buffer.position(bufOff);
    int index = 0;

    for (int i = 0; i < numEls; i++) {
      index = indexes[offs + i];
      buffer.put(source[index]); //byte only
    }

    return numEls;
  }

  public void strGather(byte[] source, int srcOff,
                        int rank, int exts, int strs, int[] shape,
                        int bufOff) {
    buffer.position(bufOff);
    writeStrGather(source, srcOff, rank, exts, strs, shape, Type.BYTE);
  }

  public void read(byte[] dest, int dstOff, int numEls,
                   int bufOff, boolean revBytes) {
    buffer.position(bufOff);
    buffer.get(dest, dstOff, numEls); //byte only
  }

  public int scatter(byte[] dest,
                     int numEls, int offs, int[] indexes,
                     int bufOff, boolean revBytes) {
    buffer.position(bufOff);
    int indx = 0;

    for (int i = 0; i < numEls; i++) {
      indx = indexes[offs + i];
      dest[indx] = buffer.get(); //byte only
    }

    return numEls;
  }


  public void strScatter(byte[] dest, int dstOff,
                         int rank, int exts, int strs, int[] shape,
                         int bufOff, boolean revBytes) {
    buffer.position(bufOff);
    readStrScatter(dest, dstOff, rank, exts, strs, shape, Type.BYTE);
  }


    /******************************* SHORT **********************************/

    public void write(short [] source, int srcOff, int numEls,
                             int bufOff) {
            buffer.position(bufOff);
	    ShortBuffer ShortBuffer = buffer.asShortBuffer() ; 
	    ShortBuffer.put(source, srcOff, numEls);

            //for(int i=0 ; i<numEls ; i++) {
            //      buffer.putShort(source[i + srcOff]);
            //}
    }

    public int gather(short [] source,
                             int numEls, int offs, int [] indexes,
                             int bufOff) {
            buffer.position(bufOff);
            int index = 0;

            for(int i = 0; i < numEls; i++) {
                    index = indexes[offs + i];
                    buffer.putShort(source[index]);
            }

            return numEls;
    }


    public void strGather(short [] source, int srcOff,
                                 int rank, int exts, int strs, int [] shape,
                                 int bufOff) {
             buffer.position(bufOff);
             writeStrGather(source, srcOff, rank, exts, strs, shape, 
			     Type.SHORT);
    }

    public void read(short [] dest, int dstOff, int numEls,
                            int bufOff, boolean revBytes) {
            buffer.position(bufOff);
	    ShortBuffer ShortBuffer = buffer.asShortBuffer() ; 
            ShortBuffer.get(dest, dstOff, numEls);
            //for (int i = 0; i < numEls; i++) {
            //        dest[i + dstOff] = buffer.getShort();
            //}

    }

    public int scatter(short [] dest,
                              int numEls, int offs, int [] indexes,
                              int bufOff, boolean revBytes) {
             buffer.position(bufOff);
             int indx = 0;

             for (int i = 0; i < numEls; i++){
                     indx = indexes[offs + i];
                     dest[indx] = buffer.getShort();
             }

             return numEls;
    }

    public void strScatter(short [] dest, int dstOff,
                                  int rank, int exts, int strs, int [] shape,
                                  int bufOff, boolean revBytes) {
            buffer.position(bufOff);
            readStrScatter(dest, dstOff, rank, exts, strs, shape, 
			    Type.SHORT);
    }


    /******************************* INT **********************************/

    public void write(int [] source, int srcOff, int numEls,
                             int bufOff) {
            buffer.position(bufOff);
	    IntBuffer IntBuffer = buffer.asIntBuffer() ; 
	    IntBuffer.put(source, srcOff, numEls);

            //for(int i=0 ; i<numEls ; i++) {
            //      buffer.putInt(source[i + srcOff]);
            //}
    }

    public int gather(int [] source,
                             int numEls, int offs, int [] indexes,
                             int bufOff) {
            buffer.position(bufOff);
            int index = 0;

            for(int i = 0; i < numEls; i++) {
                    index = indexes[offs + i];
                    buffer.putInt(source[index]);
            }

            return numEls;
    }


    public void strGather(int [] source, int srcOff,
                                 int rank, int exts, int strs, int [] shape,
                                 int bufOff) {
             buffer.position(bufOff);
             writeStrGather(source, srcOff, rank, exts, strs, shape, 
			     Type.INT);
    }

    public void read(int [] dest, int dstOff, int numEls,
                            int bufOff, boolean revBytes) {
            buffer.position(bufOff);
	    IntBuffer IntBuffer = buffer.asIntBuffer() ; 
            IntBuffer.get(dest, dstOff, numEls);
            //for (int i = 0; i < numEls; i++) {
            //        dest[i + dstOff] = buffer.getInt();
            //}

    }

    public int scatter(int [] dest,
                              int numEls, int offs, int [] indexes,
                              int bufOff, boolean revBytes) {
             buffer.position(bufOff);
             int indx = 0;

             for (int i = 0; i < numEls; i++){
                     indx = indexes[offs + i];
                     dest[indx] = buffer.getInt();
             }

             return numEls;
    }

    public void strScatter(int [] dest, int dstOff,
                                  int rank, int exts, int strs, int [] shape,
                                  int bufOff, boolean revBytes) {
            buffer.position(bufOff);
            readStrScatter(dest, dstOff, rank, exts, strs, shape, 
			    Type.INT);
    }


    /******************************* LONG **********************************/

    public void write(long [] source, int srcOff, int numEls,
                             int bufOff) {
            buffer.position(bufOff);
	    LongBuffer LongBuffer = buffer.asLongBuffer() ; 
	    LongBuffer.put(source, srcOff, numEls);

            //for(int i=0 ; i<numEls ; i++) {
            //      buffer.putLong(source[i + srcOff]);
            //}
    }

    public int gather(long [] source,
                             int numEls, int offs, int [] indexes,
                             int bufOff) {
            buffer.position(bufOff);
            int index = 0;

            for(int i = 0; i < numEls; i++) {
                    index = indexes[offs + i];
                    buffer.putLong(source[index]);
            }

            return numEls;
    }


    public void strGather(long [] source, int srcOff,
                                 int rank, int exts, int strs, int [] shape,
                                 int bufOff) {
             buffer.position(bufOff);
             writeStrGather(source, srcOff, rank, exts, strs, shape, 
			     Type.LONG);
    }

    public void read(long [] dest, int dstOff, int numEls,
                            int bufOff, boolean revBytes) {
            buffer.position(bufOff);
	    LongBuffer LongBuffer = buffer.asLongBuffer() ; 
            LongBuffer.get(dest, dstOff, numEls);
            //for (int i = 0; i < numEls; i++) {
            //        dest[i + dstOff] = buffer.getLong();
            //}

    }

    public int scatter(long [] dest,
                              int numEls, int offs, int [] indexes,
                              int bufOff, boolean revBytes) {
             buffer.position(bufOff);
             int indx = 0;

             for (int i = 0; i < numEls; i++){
                     indx = indexes[offs + i];
                     dest[indx] = buffer.getLong();
             }

             return numEls;
    }

    public void strScatter(long [] dest, int dstOff,
                                  int rank, int exts, int strs, int [] shape,
                                  int bufOff, boolean revBytes) {
            buffer.position(bufOff);
            readStrScatter(dest, dstOff, rank, exts, strs, shape, 
			    Type.LONG);
    }


    /******************************* CHAR **********************************/

    public void write(char [] source, int srcOff, int numEls,
                             int bufOff) {
            buffer.position(bufOff);
	    CharBuffer CharBuffer = buffer.asCharBuffer() ; 
	    CharBuffer.put(source, srcOff, numEls);

            //for(int i=0 ; i<numEls ; i++) {
            //      buffer.putChar(source[i + srcOff]);
            //}
    }

    public int gather(char [] source,
                             int numEls, int offs, int [] indexes,
                             int bufOff) {
            buffer.position(bufOff);
            int index = 0;

            for(int i = 0; i < numEls; i++) {
                    index = indexes[offs + i];
                    buffer.putChar(source[index]);
            }

            return numEls;
    }


    public void strGather(char [] source, int srcOff,
                                 int rank, int exts, int strs, int [] shape,
                                 int bufOff) {
             buffer.position(bufOff);
             writeStrGather(source, srcOff, rank, exts, strs, shape, 
			     Type.CHAR);
    }

    public void read(char [] dest, int dstOff, int numEls,
                            int bufOff, boolean revBytes) {
            buffer.position(bufOff);
	    CharBuffer CharBuffer = buffer.asCharBuffer() ; 
            CharBuffer.get(dest, dstOff, numEls);
            //for (int i = 0; i < numEls; i++) {
            //        dest[i + dstOff] = buffer.getChar();
            //}

    }

    public int scatter(char [] dest,
                              int numEls, int offs, int [] indexes,
                              int bufOff, boolean revBytes) {
             buffer.position(bufOff);
             int indx = 0;

             for (int i = 0; i < numEls; i++){
                     indx = indexes[offs + i];
                     dest[indx] = buffer.getChar();
             }

             return numEls;
    }

    public void strScatter(char [] dest, int dstOff,
                                  int rank, int exts, int strs, int [] shape,
                                  int bufOff, boolean revBytes) {
            buffer.position(bufOff);
            readStrScatter(dest, dstOff, rank, exts, strs, shape, 
			    Type.CHAR);
    }


    /******************************* FLOAT **********************************/

    public void write(float [] source, int srcOff, int numEls,
                             int bufOff) {
            buffer.position(bufOff);
	    FloatBuffer FloatBuffer = buffer.asFloatBuffer() ; 
	    FloatBuffer.put(source, srcOff, numEls);

            //for(int i=0 ; i<numEls ; i++) {
            //      buffer.putFloat(source[i + srcOff]);
            //}
    }

    public int gather(float [] source,
                             int numEls, int offs, int [] indexes,
                             int bufOff) {
            buffer.position(bufOff);
            int index = 0;

            for(int i = 0; i < numEls; i++) {
                    index = indexes[offs + i];
                    buffer.putFloat(source[index]);
            }

            return numEls;
    }


    public void strGather(float [] source, int srcOff,
                                 int rank, int exts, int strs, int [] shape,
                                 int bufOff) {
             buffer.position(bufOff);
             writeStrGather(source, srcOff, rank, exts, strs, shape, 
			     Type.FLOAT);
    }

    public void read(float [] dest, int dstOff, int numEls,
                            int bufOff, boolean revBytes) {
            buffer.position(bufOff);
	    FloatBuffer FloatBuffer = buffer.asFloatBuffer() ; 
            FloatBuffer.get(dest, dstOff, numEls);
            //for (int i = 0; i < numEls; i++) {
            //        dest[i + dstOff] = buffer.getFloat();
            //}

    }

    public int scatter(float [] dest,
                              int numEls, int offs, int [] indexes,
                              int bufOff, boolean revBytes) {
             buffer.position(bufOff);
             int indx = 0;

             for (int i = 0; i < numEls; i++){
                     indx = indexes[offs + i];
                     dest[indx] = buffer.getFloat();
             }

             return numEls;
    }

    public void strScatter(float [] dest, int dstOff,
                                  int rank, int exts, int strs, int [] shape,
                                  int bufOff, boolean revBytes) {
            buffer.position(bufOff);
            readStrScatter(dest, dstOff, rank, exts, strs, shape, 
			    Type.FLOAT);
    }


    /******************************* DOUBLE **********************************/

    public void write(double [] source, int srcOff, int numEls,
                             int bufOff) {
            buffer.position(bufOff);
	    DoubleBuffer DoubleBuffer = buffer.asDoubleBuffer() ; 
	    DoubleBuffer.put(source, srcOff, numEls);

            //for(int i=0 ; i<numEls ; i++) {
            //      buffer.putDouble(source[i + srcOff]);
            //}
    }

    public int gather(double [] source,
                             int numEls, int offs, int [] indexes,
                             int bufOff) {
            buffer.position(bufOff);
            int index = 0;

            for(int i = 0; i < numEls; i++) {
                    index = indexes[offs + i];
                    buffer.putDouble(source[index]);
            }

            return numEls;
    }


    public void strGather(double [] source, int srcOff,
                                 int rank, int exts, int strs, int [] shape,
                                 int bufOff) {
             buffer.position(bufOff);
             writeStrGather(source, srcOff, rank, exts, strs, shape, 
			     Type.DOUBLE);
    }

    public void read(double [] dest, int dstOff, int numEls,
                            int bufOff, boolean revBytes) {
            buffer.position(bufOff);
	    DoubleBuffer DoubleBuffer = buffer.asDoubleBuffer() ; 
            DoubleBuffer.get(dest, dstOff, numEls);
            //for (int i = 0; i < numEls; i++) {
            //        dest[i + dstOff] = buffer.getDouble();
            //}

    }

    public int scatter(double [] dest,
                              int numEls, int offs, int [] indexes,
                              int bufOff, boolean revBytes) {
             buffer.position(bufOff);
             int indx = 0;

             for (int i = 0; i < numEls; i++){
                     indx = indexes[offs + i];
                     dest[indx] = buffer.getDouble();
             }

             return numEls;
    }

    public void strScatter(double [] dest, int dstOff,
                                  int rank, int exts, int strs, int [] shape,
                                  int bufOff, boolean revBytes) {
            buffer.position(bufOff);
            readStrScatter(dest, dstOff, rank, exts, strs, shape, 
			    Type.DOUBLE);
    }


 /******************************* BOOLEAN **********************************/

 public void write(boolean[] source, int srcOff, int numEls,
                   int bufOff) {
   buffer.position(bufOff);

   for (int i = 0; i < numEls; i++) {
     buffer.put( (byte) (source[i + srcOff] ? 1 : 0)); //boolean
   }
 }

 public int gather(boolean[] source,
                   int numEls, int offs, int[] indexes,
                   int bufOff) {
   buffer.position(bufOff);
   int index = 0;

   for (int i = 0; i < numEls; i++) {
     index = indexes[offs + i];
     buffer.put( (byte) (source[index] ? 1 : 0)); //boolean
   }

   return numEls;
 }

 public void strGather(boolean[] source, int srcOff,
                       int rank, int exts, int strs, int[] shape,
                       int bufOff) {
   buffer.position(bufOff);
   writeStrGather(source, srcOff, rank, exts, strs, shape, 
		   Type.BOOLEAN);
 }

  public void read(boolean[] dest, int dstOff, int numEls,
                   int bufOff, boolean revBytes) {
    buffer.position(bufOff);

    for (int i = 0; i < numEls; i++) {
     dest[i + dstOff] = (buffer.get() == 1) ;
    }

  }

  public int scatter(boolean[] dest,
                     int numEls, int offs, int[] indexes,
                     int bufOff, boolean revBytes) {
    buffer.position(bufOff);
    int indx = 0;

    for (int i = 0; i < numEls; i++) {
      indx = indexes[offs + i];

      dest[indx] = buffer.get() == 1 ;
    }

    return numEls;
  }

  public void strScatter(boolean[] dest, int dstOff,
                         int rank, int exts, int strs, int[] shape,
                         int bufOff, boolean revBytes) {
    buffer.position(bufOff);
    readStrScatter(dest, dstOff, rank, exts, strs, shape, 
		    Type.BOOLEAN);
  }

    private void writeStrGatherBuffer(Object source, int off, Type type){
        switch( type.getCode() ) {
        case 0: // "byte";
            buffer.put(((byte[])source)[off]);
            break;

        case 1: // "char";
            buffer.putChar(((char[])source)[off]);
            break;

        case 2: // "short";
            buffer.putShort(((short[])source)[off]);
            break;

        case 3: // "boolean";
            if((((boolean[])source)[off]) == true) {
                buffer.put(((byte)1));
            }else {
                buffer.put(((byte)0));
            }
            break;

        case 4: // "int";
            buffer.putInt(((int[])source)[off]);
            break;

        case 5: // "long";
            buffer.putLong(((long[])source)[off]);
            break;

        case 6: // "float";
            buffer.putFloat(((float[])source)[off]);
            break;

        case 7: // "double";
            buffer.putDouble(((double[])source)[off]);
            break;

        default:
            throw new RuntimeException("WriteBuffer::writeStrGatherBuffer:"+
                                 "Unknown type "+type+".");
        }
    }

    private void writeStrGather(Object source, int srcOff, int rank,
                                int exts, int strs, int[] indexes, 
				Type type) {
        switch(rank){
        case 0:
            writeStrGatherBuffer(source, srcOff, type);
            break;
        case 1:{
            int index0 = srcOff;
            for(int i = 0; i < indexes[exts]; i++){

                writeStrGatherBuffer(source, index0, type);
                index0 += indexes[strs];
            }
            break;
        }
        case 2:{
            int index0 = srcOff;
            for(int i = 0; i < indexes[exts]; i++){

                int index1 = index0;
                for(int j = 0; j < indexes[exts + 1]; j++){

                    writeStrGatherBuffer(source, index1, type);
                    index1 += indexes[strs + 1];
                }
                index0 += indexes[strs];
            }
            break;
        }
        case 3:{
            int index0 = srcOff;
            for(int i = 0; i < indexes[exts]; i++){

                int index1 = index0;
                for(int j = 0; j < indexes[exts + 1]; j++){

                    int index2 = index1;
                    for(int k = 0; k < indexes[exts + 2]; k++){

                        writeStrGatherBuffer(source, index2, type);
                        index2 += indexes[strs + 2];
                    }
                    index1 += indexes[strs + 1];
                }
                index0 += indexes[strs];
            }
            break;
        }
        default:
               int str = indexes[strs];
               for (int i = 0; i < indexes[exts]; i++)
                       writeStrGather(source, srcOff + str * i,
                                       rank - 1, exts + 1, strs + 1, indexes, type);
               break;
        }
    }

    private void readStrScatterBuffer(Object source, int off, Type type){

        switch( type.getCode() ) {
        case 0: // "byte";
                try {
                        ((byte[])source)[off] = buffer.get();
                }catch(Exception e) { e.printStackTrace(); System.exit(0); }
                break;

        case 1: // "char";
            ((char[])source)[off] = buffer.getChar();
            break;

        case 2: // "short";
            ((short[])source)[off] = buffer.getShort();
            break;

        case 3: // "boolean";
                        if(buffer.get() == 1) {
                                ((boolean[])source)[off] = true;
                        }else {
                                ((boolean[])source)[off] = false;
                        }
            break;
        case 4: // "int";
            ((int[])source)[off] = buffer.getInt();
            break;
        case 5: // "long";
            ((long[])source)[off] = buffer.getLong();
            break;
        case 6: // "float";
            ((float[])source)[off] = buffer.getFloat();
            break;
        case 7: // "double";
            ((double[])source)[off] = buffer.getDouble();
            break;
        default:
            throw new RuntimeException("WriteBuffer::readStrGatherBuffer:"+
                                 "Unknown type "+type+".");
        }
    }

    private void readStrScatter(Object dest, int dstOff, int rank,
                                int exts, int strs, int[] indexes, Type type) {
            switch(rank){
                    case 0:
                            readStrScatterBuffer(dest, dstOff, type);
                            break;
                    case 1: {
                                    int index0 = dstOff;

                                    for(int i = 0; i < indexes[exts]; i++){
                                            readStrScatterBuffer(dest, index0, type);
                                            index0 += indexes[strs];
                                    }

                                    break;
                    }


        case 2:{
            int index0 = dstOff;
            for(int i = 0; i < indexes[exts]; i++){

                int index1 = index0;
                for(int j = 0; j < indexes[exts + 1]; j++){

                    readStrScatterBuffer(dest, index1, type);
                    index1 += indexes[strs + 1];
                }
                index0 += indexes[strs];
            }
            break;
        }
        case 3:{

            int index0 = dstOff;
            for(int i = 0; i < indexes[exts]; i++){

                int index1 = index0;
                for(int j = 0; j < indexes[exts + 1]; j++){

                    int index2 = index1;
                    for(int k = 0; k < indexes[exts + 2]; k++){

                        readStrScatterBuffer(dest, index2, type);
                        index2 += indexes[strs + 2];
                    }
                    index1 += indexes[strs + 1];
                }
                index0 += indexes[strs];
            }
            break;
        }
        default:
            int str = indexes[strs];
            for (int i = 0; i < indexes[exts]; i++)
                readStrScatter(dest, dstOff + str * i,
                               rank - 1, exts + 1, strs + 1, indexes, type);
            break;
        }
    }

}
