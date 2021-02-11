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
 * File         : Buddy1FreeList.java
 * Author       : Bryan Carpenter, Aamir Shafi
 * Created      : Sat July 9 12:22:15 BST 2005
 * Revision     : $Revision: 1.2 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 *    
 */
package mpjbuf ;

/**
  * `FreeList' is implemented as a concatenated set of sublists, each
  * of type `RegionFreeList'.
  */

public class Buddy1FreeList {
    Buddy1RegionFreeList front ;

    Buddy1RegionFreeList getFront() {
        return front ;
    }

    /**
     * Add new sublist to front of free list.
     */
    void add(Buddy1RegionFreeList sublist) {
        if(front != null)
	    front.prevList = sublist ;

        sublist.prevList = null ;
	sublist.nextList = front ;
	front = sublist ;
    }

    /**
     * Remove sublist from any position in free list.
     */
    void remove(Buddy1RegionFreeList sublist) {
        Buddy1RegionFreeList prev = sublist.prevList ;
	Buddy1RegionFreeList next = sublist.nextList ;
	
	if(prev == null)
            front = next ;
	else
            prev.nextList = next ;
	
	if(next != null)
            next.prevList = prev ;
    }
}


   
