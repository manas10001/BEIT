/*
 The MIT License

 Copyright (c) 2005 - 2007
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2007)
   4. Bryan Carpenter (2005 - 2007)
   5. Mark Baker (2005 - 2007)

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * File         : Contiguous.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.14 $
 * Updated      : $Date: 2005/08/12 19:12:08 $
 */

package mpi;

import mpjbuf.*;
import java.nio.ByteBuffer ; 

/**
 * Subclass of `Datatype' representing a derived datatype created using
 * `Contiguous()'.
 */
public class Contiguous
    extends Datatype {

  private int count, oldSize;
  private Datatype oldType;

  Contiguous(int count, Datatype oldType) throws MPIException {
    bufferType = oldType.bufferType; 

    if (count < 0) {
      throw new MPIException(
          "Count argument of Contiguous must be non-negative");
    }

    this.oldType = oldType;
    this.count = count;
    baseType = oldType.baseType;
    this.oldSize = oldType.Size();
    size = count * this.oldSize;
    byteSize = count * oldType.byteSize;
    computeBounds();

  }

  private void computeBounds() {

    // Compute `ub' and `lb', which define the `extent' of the new type.
    // Also defines `ubSet', `lbSet' for this type.
    int oldExtent; //aamir

    ubSet = count > 0 && oldType.ubSet;
    lbSet = count > 0 && oldType.lbSet;

    lb = Integer.MAX_VALUE;
    ub = Integer.MIN_VALUE;
    extent = 0; // Follow MPICH in defining extent to be zero if
    // bounds are undefined (i.e. empty type).
    //if the oldtype is MPI.LB & MPI.UB, extent is zero.
    //so, is this right ?

    if (oldSize != 0 || oldType.lbSet || oldType.ubSet) {

      // `oldType.ub', `oldType.lb', `oldType.extent' all well-defined.

      if (count > 0) {

        // Assume all extents are non-negative.
        oldExtent = oldType.extent;
        extent = count * oldExtent;
        lb = oldType.lb;
        ub = lb + extent;

        /* Following "correct" according to standard, but
           apparently disagrees with MPICH.

                         if(oldExtent > 0) {
            lb = oldType.lb ;
            ub = (count - 1) * oldExtent + oldType.ub ;
                         }
                         else {
            lb = (count - 1) * oldExtent + oldType.lb ;
            ub = oldType.ub ;
                         }

                         extent = ub - lb ;
         */
      }
    }
  }

  mpjbuf.Buffer createWriteBuffer(ByteBuffer slicedBuffer, int messageSize) { 
    return new mpjbuf.Buffer(new NIOBuffer( messageSize, slicedBuffer ), 
		    MPI.BSEND_OVERHEAD , messageSize );
  }

  mpjbuf.Buffer createWriteBuffer(int count) throws MPIException {
    int capacity = packedSize(count) + MPI.SEND_OVERHEAD ;
    int offset = MPI.SEND_OVERHEAD ;
    mpjbuf.RawBuffer rawBuffer = BufferFactory.create(capacity);
    mpjbuf.Buffer wBuffer = new mpjbuf.Buffer(rawBuffer, offset, capacity) ;
    
    try {
      wBuffer.putSectionHeader(this.bufferType); // need conversion
    }
    catch (Exception e) {
      throw new MPIException(e);  	    
    }
    
    return wBuffer;
  }

  mpjbuf.Buffer createReadBuffer(int count) {
    int capacity = packedSize(count) + MPI.RECV_OVERHEAD ;
    int offset = MPI.RECV_OVERHEAD ;
    mpjbuf.RawBuffer rawBuffer = BufferFactory.create(capacity);
    mpjbuf.Buffer mpjbuf = new mpjbuf.Buffer(rawBuffer, offset, capacity) ;
    return mpjbuf;
  }

  int packedSize(int count) {
    int dataSize = count * byteSize;
    int totalSize = 0;
    int sectionHeader = 8; //what about multiple sections.
    totalSize = sectionHeader + dataSize;
    int padding = 0;

    if (totalSize % 8 != 0)
      padding = totalSize % 8;

    return totalSize + padding;
  }

  void setPacker() {

    Packer oldPacker = oldType.getPacker(); //giving it els to avoid errors.
    
    if (oldPacker instanceof SimplePacker) {
      int oldCount = ( (SimplePacker) oldPacker).numEls; //(aamir)
      int newCount = count * oldCount;
      packer = SimplePackerFactory.create(newCount, baseType);
    }
    else if (oldPacker instanceof MultistridedPacker) {
      MultistridedPacker multiOldPacker = (MultistridedPacker) oldPacker;
      int oldRank = multiOldPacker.rank; //(rank)
      int[] oldIndexes = multiOldPacker.indexes; //indexes(aamir)
      int oldExtent = multiOldPacker.rank; //extents is an array (aamir)

      int rank = oldRank + 1;
      int[] indexes = new int[2 * rank];
      for (int i = 0; i < oldRank; i++)
        indexes[i] = oldIndexes[i];
      indexes[oldRank] = count;
      for (int i = 0; i < oldRank; i++)
        indexes[rank + i] = oldIndexes[oldRank + i];
      indexes[rank + oldRank] = oldExtent;

      packer = MultistridedPackerFactory.create(rank, indexes,
                                                extent, size,
                                                baseType);
    }
    else {
      packer = new ContiguousPacker(count, oldType);
    }
  }

  private class ContiguousPacker
      extends GenericPacker {

    Packer itemPacker;
    int itemLen, itemSize;

    ContiguousPacker(int count, Datatype oldType) {
      super(Contiguous.this.extent, Contiguous.this.size);
      this.itemPacker = oldType.getPacker(); 
      this.itemLen = oldType.extent;
      this.itemSize = oldType.size;
    }

    public void pack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      for (int i = 0; i < count; i++) {
        itemPacker.pack(mpjbuf, buf, offset);
        offset += itemLen;
      }
    }

    public void unpack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      for (int i = 0; i < count; i++) {
        itemPacker.unpack(mpjbuf, buf, offset);
        offset += itemLen;
      }
    }

    public void unpackPartial(mpjbuf.Buffer mpjbuf, int length,
                              Object buf, int offset) throws MPIException {

      int numFull = length / itemSize;
      for (int i = 0; i < numFull; i++) {
        itemPacker.unpack(mpjbuf, buf, offset);
        offset += itemLen;
      }
      int residue = length - numFull * itemSize;
      if (residue > 0)
        itemPacker.unpackPartial(mpjbuf, residue, buf, offset);
    }
  }

}

// Things to do:
//

