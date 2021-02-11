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
 * File         : Buddy1RegionFreeList.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sat July 9 12:22:15 BST 2005
 * Revision     : $Revision: 1.5 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 *    
 */
package mpjbuf ; 

import java.nio.ByteBuffer ;

/**
 * Local free list associated with a particular region.
 */
public class Buddy1RegionFreeList {


    // Whole of memory associated with region.
    ByteBuffer memory ;

    // The global free list this local list is associated with.
    Buddy1FreeList globalFreeList ;

    // Forward and backward pointers in global free list.
    Buddy1RegionFreeList prevList, nextList ;
    //up and down below are same  ??? aamir  :-)

    int front ;
    // Extra fields used by the buddy algorithm:
    int level, blockSize ;

    Buddy1RegionFreeList up, down ;

    /**
     * Empty local free list for a particular region.
     *
     * This free list (when non-empty) will become a local sublist of
     * `globalFreeList'.
     */
    Buddy1RegionFreeList(int level, int blockSize, ByteBuffer memory,
		    Buddy1FreeList globalFreeList) {
        this.level     = level ;
        this.blockSize = blockSize ;

        this.memory    = memory ;
        this.globalFreeList = globalFreeList ;

        front = BufferConstants.NULL ;
    }

    void add(int position) {
	if (front == BufferConstants.NULL) {  // Local free list empty
            memory.position( position + BufferConstants.NEXT ) ;
	    memory.putInt(BufferConstants.NULL); 
            memory.position( position + BufferConstants.PREV ) ;
	    memory.putInt(BufferConstants.NULL); 
            front = position ;
	    globalFreeList.add(this) ;  // Add to global free list
	}
	
	else {
            memory.position( position + BufferConstants.NEXT ); 
	    memory.putInt( front ) ;
            memory.position( position + BufferConstants.PREV ); 
	    memory.putInt( BufferConstants.NULL ) ;
            memory.position( front + BufferConstants.PREV ); 
	    memory.putInt( position ) ;
	    front = position ; 
	}
    }

    void remove(int position) {
        memory.position ( position + BufferConstants.PREV ); 
	int prev = memory.getInt() ;
        memory.position ( position + BufferConstants.NEXT ); 
	int next = memory.getInt() ;
	
	if (prev == BufferConstants.NULL)
	    front = next ;
	else {
            memory.position( prev + BufferConstants.NEXT ); 		
	    memory.putInt( next ); 
	}
	
	if (next != BufferConstants.NULL) {
	    memory.position( next + BufferConstants.PREV ); 
	    memory.putInt( prev );
	}

	if (front == BufferConstants.NULL) {  // Local free list now empty
	    globalFreeList.remove(this) ;  // Remove from global free list
	}
    }

    void removeFront() {
	memory.clear() ; 
        memory.position(front + BufferConstants.NEXT );
	front = memory.getInt() ; 
	
	if (front == BufferConstants.NULL) {  // Local free list now empty 
	    globalFreeList.remove(this) ;  // Remove from global free list
	}
	else {
	    memory.position( front + BufferConstants.PREV ); 
	    memory.putInt( BufferConstants.NULL );
	}
    }

}
