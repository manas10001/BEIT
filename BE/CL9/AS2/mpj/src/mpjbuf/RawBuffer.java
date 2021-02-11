/* This file generated automatically from template RawBuffer.java.in. */

/*
The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2008)
   3. Bryan Carpenter (2005 - 2008)
   4. Mark Baker (2005 - 2008)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package mpjbuf;

import java.nio.ByteBuffer ;

/**
 * Interface implemented by static component of buffer.
 */
public interface RawBuffer {

    int getCapacity() ;

    void free() ;

    void putByte(int value, int bufOff) ;

    int getByte(int bufOff) ;

    void putInt(int value, int bufOff) ;

    int getInt(int bufOff, boolean revBytes) ;

    void copy(ByteBuffer srcBuffer, int srcOffset, int srcLength,
		    int dstOffset) ; 
		


    void write(byte [] source, int srcOff, int numEls, int bufOff) ;

    int gather(byte [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(byte [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(byte [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(byte [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(byte [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;


    void write(short [] source, int srcOff, int numEls, int bufOff) ;

    int gather(short [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(short [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(short [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(short [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(short [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;


    void write(int [] source, int srcOff, int numEls, int bufOff) ;

    int gather(int [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(int [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(int [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(int [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(int [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;


    void write(long [] source, int srcOff, int numEls, int bufOff) ;

    int gather(long [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(long [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(long [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(long [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(long [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;


    void write(char [] source, int srcOff, int numEls, int bufOff) ;

    int gather(char [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(char [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(char [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(char [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(char [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;


    void write(float [] source, int srcOff, int numEls, int bufOff) ;

    int gather(float [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(float [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(float [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(float [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(float [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;


    void write(double [] source, int srcOff, int numEls, int bufOff) ;

    int gather(double [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(double [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(double [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(double [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(double [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;


    void write(boolean [] source, int srcOff, int numEls, int bufOff) ;

    int gather(boolean [] source, int numEls, int offs, int [] indexes,
               int bufOff) ;

    void strGather(boolean [] source, int srcOff,
                   int rank, int exts, int strs, int [] shape,
                   int bufOff) ;

    void read(boolean [] dest, int dstOff, int numEls,
              int bufOff, boolean revBytes) ;

    int scatter(boolean [] dest, int numEls, int offs, int [] indexes,
                int bufOff, boolean revBytes) ;

    void strScatter(boolean [] dest, int dstOff,
                    int rank, int exts, int strs, int [] shape,
                    int bufOff, boolean revBytes) ;

}

