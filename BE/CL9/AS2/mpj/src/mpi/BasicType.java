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
 * File         : BasicType.java
 * Author       : Sang Lim, Sung-Hoon Ko, Xinying Li, Bryan Carpenter, 
 *                Aamir Shafi
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.13 $
 * Updated      : $Date: 2005/08/12 19:12:08 $
 */

package mpi;

import mpjbuf.*;
import java.nio.ByteBuffer ;

public class BasicType
    extends Datatype {

  int type;

  BasicType(int type) {

    // It is an error if type < 0 or type > Datatype.OBJECT.
    // But since this constructor should not be called from user
    // code we do not check for this condition.

    this.type = type;

    if (type == LB || type == UB) {
      baseType = UNDEFINED;
      size = 0;
      byteSize = 0;
    }
    else { 

      baseType = type;

      switch (type) {

        case UNDEFINED: 
          size = 0;
          byteSize = 0;
          break;

        case NULL:
          size = 0; 
          byteSize = 0;
          break;

        case BYTE:
          bufferType = mpjbuf.Type.BYTE;
          byteSize = 1;
          break;

        case CHAR:
          bufferType = mpjbuf.Type.CHAR;
          byteSize = 2;
          break;

        case SHORT:
          bufferType = mpjbuf.Type.SHORT;
          byteSize = 2;
          break;

        case BOOLEAN:
          bufferType = mpjbuf.Type.BOOLEAN;
          byteSize = 1;
          break;

        case INT:
          bufferType = mpjbuf.Type.INT;
          byteSize = 4;
          break;

        case LONG:
          bufferType = mpjbuf.Type.LONG;
          byteSize = 8;
          break;

        case FLOAT:
          bufferType = mpjbuf.Type.FLOAT;
          byteSize = 4;
          break;

        case DOUBLE:
          bufferType = mpjbuf.Type.DOUBLE;
          byteSize = 8;
          break;

        case PACKED: //is this right ?

          size = 0;
          byteSize = 0;
          break;

        case OBJECT: //remember, object type is not copied onto static buffer.
          bufferType = mpjbuf.Type.OBJECT;
          size = 0;
          byteSize = 0;
          break;

        default:
          break;
      }

      size = 1;
    }

    computeBounds();

  }

  private void computeBounds() {

    // Compute `ub' and `lb', which define the `extent' of the new type.
    // Also defines `ubSet', `lbSet' for this type.

    switch (type) {
      case LB:
        lb = 0;
        ub = 0;
        lbSet = true;
        ubSet = false;

        break;

      case UB:
        lb = 0;
        ub = 0;
        lbSet = false;
        ubSet = true;

        break;

      default: // **** what about PACKED? ****

        lb = 0;
        ub = 1;
        lbSet = false;
        ubSet = false;

        break;
    }

    extent = ub - lb;
  }

  mpjbuf.Buffer createWriteBuffer(ByteBuffer slicedBuffer, int messageSize) {
    return new mpjbuf.Buffer(new NIOBuffer(messageSize, slicedBuffer), 
		    MPI.BSEND_OVERHEAD, messageSize );
  }

  mpjbuf.Buffer createWriteBuffer(int count) throws MPIException {
    int capacity = packedSize(count) + MPI.SEND_OVERHEAD ;
    int offset = MPI.SEND_OVERHEAD ;
    mpjbuf.RawBuffer rawBuffer = BufferFactory.create(capacity);
    mpjbuf.Buffer wBuffer = new mpjbuf.Buffer (rawBuffer, offset, capacity) ;
    
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
    packer = SimplePackerFactory.create(1, baseType);
  }

}

// Things to do:
//

