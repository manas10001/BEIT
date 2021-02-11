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
 * File         : Struct.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.16 $
 * Updated      : $Date: 2005/08/25 10:22:07 $
 */

package mpi;

import mpjbuf.*;
import java.nio.ByteBuffer;

/**
 * Subclass of `Datatype' representing a derived datatype created using
 * `Struct()'.
 * createWriteBuffer doesn't write the sectionHeader because Struct datatype
 * may consist of various different datatypes, thus the best place to write
 * these section headers (and read them as well) are pack() and unpack()
 * methods in StructPacker class. This behaviour is only exhibited,
 * (writing various different kind of sections) by Struct datatype.
 * Thus, it has be dealt here only. This
 * may not be the best way of doing this, but at the mom. it looks *the best*.
 */
public class Struct
    extends Datatype {

  private int[] blockLengths, displacements;
  private Datatype[] oldTypes;

  private boolean unitsOfOldExtent;
  // true => Indexed, false => Hindexed.

  private boolean unitBlocks;
  // Case where all blocks have unit length is detected and
  // treated specially.

  private Datatype oldType;
  private int numBlocks;
  //int extent = 0;

  /** you want to pack multiple type of non-contigous or contigous elements.
   */
  public Struct(int[] array_of_blocklengths, int[] array_of_displacements,
                Datatype[] array_of_types) throws MPIException {

    numBlocks = array_of_blocklengths.length;
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("\n\n--struct--");
      MPI.logger.debug("numBlocks " + numBlocks);
    }

    blockLengths = new int[numBlocks];
    displacements = new int[numBlocks];
    oldTypes = new Datatype[numBlocks];
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("iterating over numBlocks ");
    }

    for (int i = 0; i < numBlocks; i++) {
      blockLengths[i] = array_of_blocklengths[i];
      displacements[i] = array_of_displacements[i];
      oldTypes[i] = array_of_types[i];
      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
        MPI.logger.debug("numBlocks[" + i + "] bkln<" + blockLengths[i] +
			">,dis<" +displacements[i] + 
			">,otype<" + oldTypes[i] + ">"); 
      }
    }

    // Compute base type, type size
    baseType = UNDEFINED;
    for (int i = 0; i < numBlocks; i++) {
      Datatype oldType = oldTypes[i];
      int oldBaseType = oldType.baseType;
      if (oldBaseType != UNDEFINED) {
        if (baseType == UNDEFINED) {
          baseType = oldBaseType;
          bufferType = oldType.bufferType;
        }
        else if (oldBaseType != baseType) {
          /*MPIException.processMpiJavaException(MPI.ERR_TYPE,
                 "Base types of all component types in a Struct " +
                 "must agree.");*/
          throw new MPIException(
              "Base types of all component types in a Struct " +
              "must agree");
        }
        if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
          MPI.logger.debug("numBlocks[" + i + "] bkln<" + blockLengths[i] +
                        ">,dis<" + displacements[i] + ">,otype<" + oldTypes[i] +
                        ">");
	}
        size += blockLengths[i] * oldType.Size();
      }

      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
        MPI.logger.debug("numBlocks[" + i + "] size<" + size + ">,otype<" +
                      oldTypes[i] + ">");
        MPI.logger.debug("baseType " + baseType);
      }
    }

    computeBounds();
    
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("bufferType " + bufferType);
      MPI.logger.debug("after calling computeBounds");
      MPI.logger.debug("extent " + extent);
      MPI.logger.debug("lb " + lb);
      MPI.logger.debug("ub " + ub);
      MPI.logger.debug("lbSet " + lbSet);
      MPI.logger.debug("ubSet " + ubSet);
      MPI.logger.debug(
		      "bufferType for struct hasn't been set, "+
		      " and it won't work like this");
    }
  }

  private void computeBounds() throws MPIException {
    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) 
      MPI.logger.debug("--computeBounds--");
    // Compute `ub' and `lb', which define the `extent' of the new type.
    // Also defines `ubSet', `lbSet' for this type.

    ubSet = false;
    lbSet = false;
    lb = Integer.MAX_VALUE;
    ub = Integer.MIN_VALUE;

    // Because currently we don't save the true lb (ub) of old types,
    // following will be an upper bound (lower bound), obtained
    // from precursor types for which lbSet (ubSet) is false.

    int trueLb = Integer.MAX_VALUE;
    int trueUb = Integer.MIN_VALUE;

    extent = 0; // Follow MPICH in defining extent to be zero if
    // bounds are undefined (i.e. empty type).

    if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("initial values on computeBounds");
      MPI.logger.debug("extent " + extent);
      MPI.logger.debug("lb " + lb);
      MPI.logger.debug("ub " + ub);
      MPI.logger.debug("lbSet " + lbSet);
      MPI.logger.debug("ubSet " + ubSet);
      MPI.logger.debug("trueLb " + trueLb);
      MPI.logger.debug("trueUb " + trueUb);

      MPI.logger.debug("--iterating over the numBlocks<" + numBlocks);
    }
    for (int i = 0; i < numBlocks; i++) {
      int blockLen = blockLengths[i];

      if(MPI.DEBUG && MPI.logger.isDebugEnabled()) {
        MPI.logger.debug(" iteration<" + i + ">");
        MPI.logger.debug(" blklen <" + blockLen + ">");
      }

      if (blockLen < 0) {
        /*MPIException.processMpiJavaException(MPI.ERR_COUNT,
               "All block-lengths for Struct " +
               "must be non-negative.") ;*/
        throw new MPIException(
            "All block-lengths for Struct must be non-negative");
      }

      if (blockLen > 0) {
        if(MPI.DEBUG && MPI.logger.isDebugEnabled()) 
          MPI.logger.debug("if blklen > 0");
        Datatype oldtype = oldTypes[i];
        int oldBaseType = oldtype.baseType;
        if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
          MPI.logger.debug("getting old type" + oldtype);
          MPI.logger.debug("old base type" + oldBaseType);
	}
        int oldSize = oldtype.Size();
        if(MPI.DEBUG && MPI.logger.isDebugEnabled()) 
          MPI.logger.debug("old size " + oldSize);
        boolean oldUbSet = oldtype.ubSet;
        boolean oldLbSet = oldtype.lbSet;
        if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
          MPI.logger.debug("oldubset " + oldUbSet);
          MPI.logger.debug("oldlbset" + oldLbSet);
	}
        if (oldSize != 0 || oldLbSet || oldUbSet) {
          if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
            MPI.logger.debug("within oldSize != 0 or oldLbSet or oldUbSet ");
	  }
          // `oldtype.ub', `oldtype.lb', `oldtype.extent'
          // all well-defined.

          int oldExtent = oldtype.Extent();
	  
          if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
            MPI.logger.debug("oldextent" + oldExtent);
	  }

          int max_ub = displacements[i] + (blockLen - 1) * oldExtent +
              oldtype.ub;
          if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
            MPI.logger.debug("max_ub" + max_ub);
	  }
          if (oldUbSet) {
            ubSet = true;
            if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
              MPI.logger.debug("(MPI.uB) max_ub" + max_ub);
              MPI.logger.debug("(MPI.uB) true" + trueUb);
              MPI.logger.debug("(MPI.uB) ub" + ub);
              MPI.logger.debug("(MPI.uB) oldtype.ub" + oldtype.ub);
	    }
            //aamir -- removed this if condition
            //if (max_ub > ub)
            ub = max_ub;

            if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
              MPI.logger.debug("(after((MPI.uB) ub" + ub);
	    }
          }
          else {
            if (max_ub > trueUb) {
              trueUb = max_ub;

              /* aamir (start) */
              if (max_ub > displacements[i])
                ub = max_ub; // + oldSize;
              else
                ub = displacements[i]; //+oldSize;

              //ub = (max_ub*displacements[i])+oldSize; //aamir + (what is epsolon here ??)
              //MPI.logger.debug("ub aamir ");
              /* aamir (end) */
            }
            /*
                else {
             ub = displacements[i] + oldSize;//aamir
                }*/
          }
          if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
            MPI.logger.debug("trueUb" + trueUb);
	  }
          int min_lb = displacements[i] + oldtype.lb;
          if (oldLbSet) {
            lbSet = true;
            if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
              MPI.logger.debug("(MPI.lB) min_lb" + min_lb);
              MPI.logger.debug("(MPI.lB) true" + trueLb);
              MPI.logger.debug("(MPI.lB) lb" + lb);
              MPI.logger.debug("(MPI.LB) oldtype.lb" + oldtype.lb);
	    }

            //aamir -- removed this if condition
            //if (min_lb < lb)
            lb = min_lb;

            if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
              MPI.logger.debug(")after((MPI.UB) lb" + lb);
	    }
          }
          else {
            if (min_lb < trueLb) {
              trueLb = min_lb;

              /* aamir (start) */
              if (min_lb < displacements[i])
                lb = min_lb;
              else
                lb = displacements[i];

              //lb = min_lb*displacements[i];
              //lb = min_lb;
              /* aamir (end) */
            }
            /*
                else
             lb = displacements[i];//aamir
             */
          }

          if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
            MPI.logger.debug("trueLb" + trueLb);
	  }
          /* Following "correct" according to standard, but likely to
             cause compatibility problems with MPICH?

                               int maxStartElement = startBlock +
                  oldExtent > 0 ? (blockLen - 1) * oldExtent : 0

                               // `ubSet' acts like a most significant positive bit in
                               // the maximization operation.

                               if (oldUbSet == ubSet) {
              int max_ub = maxStartElement + oldtype.ub ;
              if (max_ub > ub)
                  ub = max_ub ;
                               }
                               else if(oldUbSet) {
              ub    = maxStartElement + oldtype.ub ;
              ubSet = true ;
                               }

                               int minStartElement = startBlock +
                  oldExtent > 0 ? 0 : (blockLen - 1) * oldExtent ;

                               // `lbSet' acts like a most significant negative bit in
                               // the minimization operation.

                               if (oldLbSet == lbSet) {
              int min_lb = minStartElement + oldtype.lb ;
              if (min_lb < lb)
                  lb = min_lb ;
                               }
                               else if(oldLbSet) {
              lb    = minStartElement + oldtype.lb ;
              lbSet = true ;
                               }
           */
        }
      } //end if blkLen > 0

      if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
        MPI.logger.debug("lb<" + lb + ">");
        MPI.logger.debug("ub<" + ub + ">");
        MPI.logger.debug("trueLb<" + trueLb + ">");
        MPI.logger.debug("trueUb<" + trueUb + ">");
      }

    }

    if(MPI.DEBUG && MPI.logger.isDebugEnabled())  {
      MPI.logger.debug("(e)lb<" + lb + ">");
      MPI.logger.debug("(e)ub<" + ub + ">");
      MPI.logger.debug("(e)trueLb<" + trueLb + ">");
      MPI.logger.debug("(e)trueUb<" + trueUb + ">");
    }
    /*
           if(lb > trueLb) {

              // MPIException.processMpiJavaException(MPI.ERR_TYPE,
              //    "Compatibility restriction: in Struct, lb defined by MPI_LB " +
              //    "may not be higher than \"true\" lb.") ;

     throw new MPIException("Compatibility restriction");
           }

           if(ub < trueUb) {
              // MPIException.processMpiJavaException(MPI.ERR_TYPE,
              // "Compatibility restriction: in Struct, ub defined by MPI_UB " +
              // "may not be lower than \"true\" ub.") ;
       // throw new MPIException("Compatibility restriction");
           }
     */

    if (lb != Integer.MAX_VALUE || ub != Integer.MIN_VALUE)
      extent = ub - lb;
  }

  mpjbuf.Buffer createWriteBuffer(ByteBuffer slicedBuffer, int messageSize) {
    return new mpjbuf.Buffer( new NIOBuffer(messageSize, slicedBuffer), 
		    MPI.BSEND_OVERHEAD , messageSize ) ; 
  }
  
  mpjbuf.Buffer createWriteBuffer(int count) throws MPIException {
    int capacity = packedSize(count) +MPI.SEND_OVERHEAD ;
    int offset = MPI.SEND_OVERHEAD ; 
    mpjbuf.RawBuffer rawBuffer = BufferFactory.create(capacity); 
    mpjbuf.Buffer wBuffer = new mpjbuf.Buffer(rawBuffer, offset, capacity);
    
    try {
      wBuffer.putSectionHeader(this.bufferType);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    return wBuffer;
  }

  mpjbuf.Buffer createReadBuffer(int count) {
    int capacity = packedSize(count)+ MPI.RECV_OVERHEAD ;
    int offset = MPI.RECV_OVERHEAD ;
    mpjbuf.RawBuffer rawBuffer = BufferFactory.create (capacity);
    mpjbuf.Buffer mpjbuf = new mpjbuf.Buffer( rawBuffer , offset, capacity) ;
    return mpjbuf;
  }

  int packedSize(int count) {
    /* calculate the size of each block, add them */
    int sectionHeader = 8; //what about multiple sections.

    for (int i = 0; i < blockLengths.length; i++) {
      int blockLen = blockLengths[i];
      Datatype oldtype = oldTypes[i];
      int sectionSize = 0;
      sectionSize = (blockLen * oldtype.byteSize);
   // int padding = 0;
   // if (sectionSize % 8 != 0)
   // padding = sectionSize % 8;
   // sectionSize += padding;
      byteSize += sectionSize;
    }

    /* calculate the size of each block, add them */
    return ((byteSize * count)+sectionHeader) ;
  }

  void setPacker() {
    packer = new StructPacker();
  }

  private class StructPacker
      extends GenericPacker {

    StructPacker() {
      super(Struct.this.extent, Struct.this.size); //what is size ?
    }

    public void pack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      int numBlocks = displacements.length;

      for (int i = 0; i < numBlocks; i++) {
        int boffset = offset + displacements[i];
        int blockLen = blockLengths[i];
        Datatype oldtype = oldTypes[i];
        Packer itemPacker = oldtype.getPacker(); 
        int itemExtent = oldtype.extent;
        for (int j = 0; j < blockLen; j++) {
          itemPacker.pack(mpjbuf, buf, boffset); 
          boffset += itemExtent;
        }
      }
    }

    public void unpack(mpjbuf.Buffer mpjbuf, Object buf, 
		    int offset) throws MPIException {

      int numBlocks = displacements.length;

      for (int i = 0; i < numBlocks; i++) {
        int boffset = offset + displacements[i];
        int blockLen = blockLengths[i];
        Datatype oldtype = oldTypes[i];

        Packer itemPacker = oldtype.getPacker(); 
        int itemExtent = oldtype.extent;

        for (int j = 0; j < blockLen; j++) {
          itemPacker.unpack(mpjbuf, buf, boffset, itemExtent);
          boffset += itemExtent;
        }

        if (oldtype.bufferType == null)
          System.out.println("bufferType is null in createWriteBuffer");
      }
    }

    public void unpackPartial(mpjbuf.Buffer mpjbuf, int length,
                              Object buf, int offset) throws MPIException {

      int numBlocks = displacements.length;
      int residue = length;
      int boffset = 0;
      Datatype oldtype = null;
      //Packer itemPacker;


      for (int i = 0; i < numBlocks; i++) {
        boffset = offset + displacements[i];
        int blockLen = blockLengths[i];
        oldtype = oldTypes[i];

        int blockSize = blockLen * oldtype.size;

        if (residue < blockSize)break;

        Packer itemPacker = oldtype.getPacker(); //just to avoid errors
        int itemLen = oldtype.extent;

        for (int j = 0; j < blockLen; j++) {
          itemPacker.unpack(mpjbuf, buf, boffset);
          boffset += itemLen;
        }

        residue -= blockSize;
      }

      // Precondition is `length < size'.
      // So `size > 0'.  So `numBlocks > 0'.
      // So `boffset', `oldtype' always defined on exit from above loop.

      int remFull = residue / oldtype.size;
      for (int j = 0; j < remFull; j++) {
        //is this itemPacker good enough here ?

        Packer itemPacker = oldtype.getPacker(); //just to avoid errors
        int itemLen = oldtype.extent;
        itemPacker.unpack(mpjbuf, buf, boffset);
        boffset += itemLen;
      }

      residue -= remFull * oldtype.size;
      Packer itemPacker = oldtype.getPacker(); //jsut to avoid errors
      if (residue > 0)
        itemPacker.unpackPartial(mpjbuf, residue, buf, boffset);
    }
  }

}

// Things to do:
//

