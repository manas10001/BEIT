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
 * File         : Indexed.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.18 $
 * Updated      : $Date: 2005/08/12 19:12:08 $
 */

package mpi;

import mpjbuf.*;
import java.nio.ByteBuffer ;

/**
 * Subclass of `Datatype' representing a derived datatype created using
 * `Indexed()' or `Hindexed()'.
 */
public class Indexed
    extends Datatype {

  private int[] blockLengths, displacements;
  private boolean unitsOfOldExtent;
  // true => Indexed, false => Hindexed.
  private boolean unitBlocks;
  // Case where all blocks have unit length is detected and
  // treated specially.

  private Datatype oldType;
  int numBlocks; //(aamir)
  int repetitions; //(aamir)

  public Indexed(int[] array_of_blocklengths, int[] array_of_displacements,
                 Datatype oldType, 
		 boolean unitsOfOldExtent) throws MPIException {

    this.oldType = oldType;
    this.bufferType = oldType.bufferType; //aamir
    this.unitsOfOldExtent = unitsOfOldExtent;
    int oldExtent = oldType.Extent();
    numBlocks = array_of_blocklengths.length;
    repetitions = 0;
    unitBlocks = true;

    for (int i = 0; i < numBlocks; i++) {
      int blockLength = array_of_blocklengths[i];
      if (blockLength < 0) {
        throw new MPIException(
            "All block-lengths for Indexed/Hindexed must be non-negative");
      }
      repetitions += blockLength;
      if (blockLength != 1) unitBlocks = false;
    }

    if (!unitBlocks) {
      blockLengths = new int[numBlocks];
      for (int i = 0; i < numBlocks; i++)
        blockLengths[i] = array_of_blocklengths[i];
    }

    displacements = new int[numBlocks];
    // We store the "real" displacements.

    if (unitsOfOldExtent) {
      for (int i = 0; i < numBlocks; i++)
        displacements[i] = oldExtent * array_of_displacements[i];
    }
    else {
      for (int i = 0; i < numBlocks; i++)
        displacements[i] = array_of_displacements[i];
    }

    baseType = oldType.baseType;
    int oldSize = oldType.Size();
    size = repetitions * oldSize;
    byteSize = repetitions * oldType.byteSize;
    
    computeBounds(oldSize);

  }

  private void computeBounds(int oldSize) {

    // Compute `ub' and `lb', which define the `extent' of the new type.
    // Also defines `ubSet', `lbSet' for this type.

    ubSet = repetitions > 0 && oldType.ubSet;
    lbSet = repetitions > 0 && oldType.lbSet;

    // Compute ub and lb, which define the extent of the new type.

    lb = Integer.MAX_VALUE;
    ub = Integer.MIN_VALUE;
    extent = 0; // Follow MPICH in defining extent to be zero if
    // bounds are undefined (i.e. empty type).

    if (oldSize != 0 || oldType.lbSet || oldType.ubSet) {

      // `oldType.ub', `oldType.lb', `oldType.extent' all well-defined.

      int oldExtent = oldType.extent; //aamir
      //int numBlocks = displacements.length; //aamir

      // Assume all extents are non-negative.

      for (int i = 0; i < numBlocks; i++) {
        int blockLen = unitBlocks ? 1 : blockLengths[i];

        if (blockLen > 0) {

          int startBlock = displacements[i];

          int max_ub = startBlock + (blockLen - 1) * oldExtent +
              oldType.ub;
          if (max_ub > ub)
            ub = max_ub;

          int min_lb = startBlock + oldType.lb;
          if (min_lb < lb)
            lb = min_lb;

          /* Following "correct" according to standard, but likely to
           cause compatibility problems with MPICH?
                               if(oldExtent > 0) {
              int max_ub = startBlock + (blockLen - 1) * oldExtent +
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

              int min_lb = startBlock + (blockLen - 1) * oldExtent +
                           oldType.lb ;
              if (min_lb < lb)
                  lb = min_lb ;
                               }
           */
        }
      }

      extent = ub - lb;
    }
  }

  mpjbuf.Buffer createWriteBuffer(ByteBuffer slicedBuffer, int messageSize) {
    return new mpjbuf.Buffer( new NIOBuffer(messageSize, slicedBuffer), 
		    MPI.BSEND_OVERHEAD , messageSize ) ; 
  }

  mpjbuf.Buffer createWriteBuffer(int count) throws MPIException {
    int capacity = packedSize(count) + MPI.SEND_OVERHEAD ;
    int offset = MPI.SEND_OVERHEAD ;
    mpjbuf.RawBuffer rawBuffer = BufferFactory.create(capacity);
    mpjbuf.Buffer wBuffer = new mpjbuf.Buffer(rawBuffer, offset, capacity);

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

    if (oldType instanceof BasicType && unitBlocks) {
      packer = GatherPackerFactory.create(numBlocks, displacements, baseType,
                                          extent);
    }
    else {
      packer = new IndexedPacker();
    }
  }

  private class IndexedPacker
      extends GenericPacker {

    Packer itemPacker;
    int itemSize;
    int numBlocks; //(aamir)

    IndexedPacker() {
      super(Indexed.this.extent, Indexed.this.size);

      itemPacker = oldType.getPacker(); //just to avoid errors ..giving it arg 2
      this.itemSize = oldType.extent;
    }

    public void pack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      int numBlocks = displacements.length;

      for (int i = 0; i < numBlocks; i++) {
        int boffset = offset + displacements[i];
        int blockLen = unitBlocks ? 1 : blockLengths[i];
        itemPacker.pack(mpjbuf, buf, boffset, blockLen);
      }
    }

    public void unpack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      int numBlocks = displacements.length;

      for (int i = 0; i < numBlocks; i++) {
        int boffset = offset + displacements[i];
        int blockLen = unitBlocks ? 1 : blockLengths[i];
        itemPacker.unpack(mpjbuf, buf, boffset, blockLen);
      }
    }

    public void unpackPartial(mpjbuf.Buffer mpjbuf, int length,
                              Object buf, int offset) throws MPIException {

      int remaining = length;
      for (int i = 0; i < displacements.length; i++) {
        int boffset = offset + displacements[i];
        int blockLen = unitBlocks ? 1 : blockLengths[i];
        int blockSize = blockLen * itemSize;
        if (blockSize < remaining) {
          itemPacker.unpack(mpjbuf, buf, boffset, blockLen);
          remaining -= blockSize;
        }
        else {
          int numFull = remaining / itemSize;
          int fullItemsSize = numFull * itemSize;
          itemPacker.unpack(mpjbuf, buf, boffset, numFull);
          itemPacker.unpackPartial(mpjbuf, remaining - fullItemsSize,
                                   buf, boffset + fullItemsSize);
          return;
        }
      }
    }
  }

}

// Things to do:
//

