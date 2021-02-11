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
/*
 * File         : Buddy2Region.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sat July 9 12:22:15 BST 2005
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2005/11/27 19:40:12 $
 *    
 */
package mpjbuf ;

import java.util.ArrayList;
import java.nio.ByteBuffer ;
    
public class Buddy2Region {

  int regionSize = -1; 
  ArrayList<Buddy2FreeList> freeLists = 
	  new ArrayList<Buddy2FreeList>();
  ByteBuffer buffer = null; 

  Buddy2Region(int size) {

    this.buffer = ByteBuffer.allocateDirect (size);	  
    this.regionSize = size ; 
    int numChunks = (this.regionSize + BufferConstants.CHUNK_SIZE -1)
	    /BufferConstants.CHUNK_SIZE ; 
    int level = CustomMath.widthInBits( numChunks-1);
    //System.out.println("initial buffer is at level "+level);
    Buddy2FreeList fList ;
    
    for(int i=0 ; i<level ; i++) {
      fList = new Buddy2FreeList(); 
      freeLists.add(fList);
      //System.out.println("Filling level "+i+" with empty List "+fList);
    }

    int bufoffset = 0;
    int realOffset = 0; 
    int capacity = this.regionSize ;
 // FreeListNode node = new FreeListNode( this.buffer, bufoffset, capacity, 
//		    level, this, realOffset ); 
    mpjbuf.Buddy2Buffer bufferNode = new mpjbuf.Buddy2Buffer( this.buffer, 
		    bufoffset, capacity , level, this, realOffset );
    fList = new Buddy2FreeList(); 
    fList.add( bufferNode );
    //System.out.println("Filling level "+level+" with List "+fList);
    freeLists.add(fList);

  }

}
