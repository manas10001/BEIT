/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be included
 in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * File         : Vector.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.15 $
 * Updated      : $Date: 2005/08/12 19:12:08 $
 */

package mpi;

import mpjbuf.*;
import java.nio.ByteBuffer ;

/**
 * Subclass of `Datatype' representing a derived datatype created using
 * `Vector()' or `Hvector()'.
 */
public class Vector
    extends Datatype {

  private int count, blockLength, stride;
  private boolean unitsOfOldExtent;
  // true => Vector, false => Hvector.
  private Datatype oldType;
  private int realStride;

  /*
   * count  == total number of elements that you wish to transfer
   * blockLength == blockLength ..like one row 
   * may've 10 elemtns you want to send just a row.
   * stride      == jump ...skip these number of elements
   * oldtype
   * boolean, true or false.
   */
  public Vector(int count, int blockLength, int stride, Datatype oldType,
                boolean unitsOfOldExtent) throws MPIException {


    if (count < 0) {
      throw new MPIException(
          "count argument of Vector or Hvector  must be non-negative");
    }

    if (blockLength < 0) {
      throw new MPIException(
          "Block-length argument of Vector or Hvector must be non-negative");
    }

    bufferType = oldType.bufferType;
    this.oldType = oldType;
    this.count = count;
    this.blockLength = blockLength;
    this.stride = stride;
    
    int oldExtent = oldType.extent;
    realStride = stride;
    
    if (unitsOfOldExtent) realStride *= oldExtent;
    
    baseType = oldType.baseType;
    int oldSize = oldType.Size();
    int repetitions = count * blockLength;
    size = repetitions * oldSize;
    byteSize = repetitions * oldType.byteSize;
    
    computeBounds(oldSize, repetitions);
  }

  private void computeBounds(int oldSize, int repetitions) {

    // Compute `ub' and `lb', which define the `extent' of the new type.
    // Also defines `ubSet', `lbSet' for this type.

    ubSet = repetitions > 0 && oldType.ubSet; //was oldtype.
    lbSet = repetitions > 0 && oldType.lbSet; //was oldtype.

    // Compute ub and lb, which define the extent of the new type.

    lb = Integer.MAX_VALUE;
    ub = Integer.MIN_VALUE;
    extent = 0; // Follow MPICH in defining extent to be zero if
    // bounds are undefined (i.e. empty type).

    if (oldSize != 0 || oldType.lbSet || oldType.ubSet) {

      // `oldType.ub', `oldType.lb', `oldType.extent' all well-defined.

      if (count > 0 && blockLength > 0) { //was blockSize => blockLength (aamir)

        // Assume all extents are non-negative.

        int oldExtent = oldType.extent;

        if (realStride > 0) {
          ub = realStride * (count - 1) + (blockLength - 1) * oldExtent +
              oldType.ub;
          lb = oldType.lb;
        }
        else {
          ub = (blockLength - 1) * oldExtent + oldType.ub;
          lb = realStride * (count - 1) + oldType.lb;
        }

        extent = ub - lb;
      }

      /* Following "correct" according to standard, but likely to
         cause compatibility problems with MPICH?
                   for (int i = 0 ; i < count ; i++) {

                int startBlock = realStride * i ;

          if(oldExtent > 0) {
              int max_ub = startBlock + (blocklength - 1) * oldExtent +
                           oldType.ub ;
              if (max_ub > ub)
                ub = max_ub ;

              int min_lb = startBlock + oldType.lb ;
              if (min_lb < lb)
                lb = min_lb ;
          }
          else {
              int max_ub = startBlock + oldType.ub ;
              if (max_ub > ub)
                ub = max_ub ;

              int min_lb = startBlock + (blocklength - 1) * oldExtent +
                           oldType.lb ;
              if (min_lb < lb)
                lb = min_lb ;
          }
                   }

                   extent = ub - lb ;
       */
    }
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

  mpjbuf.Buffer createWriteBuffer(ByteBuffer slicedBuffer, int messageSize) { 
    return new mpjbuf.Buffer(new NIOBuffer(messageSize, slicedBuffer), 
		    MPI.BSEND_OVERHEAD , messageSize );
  }

  mpjbuf.Buffer createReadBuffer(int count) {
    int capacity = packedSize(count) + MPI.RECV_OVERHEAD ;
    int offset = MPI.RECV_OVERHEAD ; 
    mpjbuf.RawBuffer rawBuffer = BufferFactory.create(capacity); 
    mpjbuf.Buffer mpjbuf = new mpjbuf.Buffer(rawBuffer, offset, capacity); 
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

    Packer oldPacker = oldType.getPacker();

    if (oldPacker instanceof SimplePacker) {

      int oldCount = ( (SimplePacker) oldPacker).numEls;
      int newCount = blockLength * oldCount;
      int[] indexes = {
          newCount, count, 1, realStride};
      packer = MultistridedPackerFactory.create(2, indexes, extent, size,
                                                baseType);
    }
    else if (oldPacker instanceof MultistridedPacker) {
      MultistridedPacker multiOldPacker = (MultistridedPacker) oldPacker;

      int oldRank = multiOldPacker.rank;
      int[] oldIndexes = multiOldPacker.indexes;

      int rank;
      int[] indexes;
      if (blockLength == 1) {
        rank = oldRank + 1;
        indexes = new int[2 * rank]; //it was int[] indexes (aamir)
        for (int i = 0; i < oldRank; i++)
          indexes[i] = oldIndexes[i];
        indexes[oldRank] = count;
        for (int i = 0; i < oldRank; i++)
          indexes[rank + i] = oldIndexes[oldRank + i];
        indexes[rank + oldRank] = realStride;
      }
      else {
        rank = oldRank + 2;
        indexes = new int[2 * rank]; //it was int[] indexes
        for (int i = 0; i < oldRank; i++)
          indexes[i] = oldIndexes[i];
        indexes[oldRank] = blockLength;
        indexes[oldRank + 1] = count;
        for (int i = 0; i < oldRank; i++)
          indexes[rank + i] = oldIndexes[oldRank + i];
        indexes[rank + oldRank] = oldType.extent;
        indexes[rank + oldRank + 1] = realStride;
      }

      packer = MultistridedPackerFactory.create(rank, indexes,
                                                extent, size,
                                                baseType);
    }
    else {
      packer = new VectorPacker();
    }
  }

  private class VectorPacker
      extends GenericPacker {

    Packer itemPacker;
    int itemExtent;
    int itemSize;

    VectorPacker() {
      super(Vector.this.extent, Vector.this.size); 
      itemPacker = oldType.getPacker(); 
      this.itemExtent = oldType.extent;
      this.itemSize = oldType.size ;
    }

    public void pack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      for (int i = 0; i < count; i++) {
        int boffset = offset;
        for (int j = 0; j < blockLength; j++) {
          itemPacker.pack(mpjbuf, buf, boffset);
          boffset += itemExtent;
        }
        offset += realStride;
      }
    }

    public void unpack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      for (int i = 0; i < count; i++) {
        int boffset = offset;
        for (int j = 0; j < blockLength; j++) {
          itemPacker.unpack(mpjbuf, buf, boffset);
          boffset += itemExtent;
        }
        offset += realStride;
      }
    }

    public void unpackPartial(mpjbuf.Buffer mpjbuf, int length,
                              Object buf, int offset) throws MPIException {

      int numFull = length / itemSize;
      int numBlocks = numFull / blockLength;
      
      for (int i = 0; i < numBlocks; i++) {
        int boffset = offset;
        for (int j = 0; j < blockLength; j++) {
          itemPacker.unpack(mpjbuf, buf, boffset);
          boffset += itemExtent;
        }
        offset += realStride;
      }

      int remFull = numFull - numBlocks * blockLength;

      for (int j = 0; j < remFull; j++) {
        itemPacker.unpack(mpjbuf, buf, offset);
        offset += itemExtent;
      }

      int residue = length - numFull * itemSize;

      if (residue > 0)
        itemPacker.unpackPartial(mpjbuf, residue, buf, offset);

    }
  }

}

// Things to do:
//

