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
 * File         : GenericPacker.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.12 $
 * Updated      : $Date: 2005/08/02 13:07:05 $
 */

package mpi;

public abstract class GenericPacker
    implements Packer {

  int extent, size;

  public GenericPacker(int extent, int size) {
    this.extent = extent;
    this.size = size;
  }

  public void pack(mpjbuf.Buffer mpjbuf, Object buf, 
		  int offset, int count) throws MPIException {
    for (int i = 0; i < count; i++) {
      pack(mpjbuf, buf, offset);
      offset += extent;
    }
  }

  public void unpack(mpjbuf.Buffer mpjbuf, int length,
                     Object buf, int offset, int count) throws MPIException {
    
    if(size == 0 )  {
      return;	    
    }

    if (count * size < length) {
      throw new MPIException("Error in GenericPacker: count*size <"+
		     (count*size) + " is less than length <"+length+">");
    }
    else {
      int numFull = length / size;
      for (int i = 0; i < numFull; i++) {
        unpack(mpjbuf, buf, offset);
        offset += extent ;
      }
      int residue = length - numFull * size;
      if (residue > 0) {
        unpackPartial(mpjbuf, residue, buf, offset);
      }
    }
  }

  /**
   * this method was not there, but putting it exactly as 
   * pack method above offcourse changing pack to unpack.
   */
  public void unpack(mpjbuf.Buffer mpjbuf, Object buf, 
		  int offset, int count) throws MPIException {
    for (int i = 0; i < count; i++) {
      unpack(mpjbuf, buf, offset);
      offset += extent;
    }
  }
}

