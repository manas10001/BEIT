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
 * File         : Buddy2FreeList.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sat July 9 12:22:15 BST 2005
 * Revision     : $Revision: 1.2 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 *    
 */
package mpjbuf ;

import java.util.ArrayList;


    /* Buddy2FreeList has no memory associated withit i mean when no args
     * constructor is called ..it still is like an entry in the freelist 
     * but there is no associated memory or anything like that ...how 
     * does that work?
     */
public class Buddy2FreeList { //extends Buddy2FreeListNode

  Buddy2Buffer head, tail;
  int size = 0;

  Buddy2FreeList() {
    //next = this;
    head = null;
    //prev = this;
    tail = null;
    size = 0;
  }
      
  boolean isEmpty() {
    //return (next == this);	      
    return (head == null && tail == null);	      
  }
      
  void add(Buddy2Buffer node) {
    if(head == null && tail == null) {
      this.head = node;
      this.tail = node;
      node.next = node;
      node.prev = node;
    } else {
      head.next.prev = node;
      node.next = head.next;
      head.next = node;
      node.prev = head;
      tail = node;
    }
    size++;
  }
      
  void remove(Buddy2Buffer node) {
    if(head == tail) {
      head = null;
      tail = null;
    } else if(head == node) {
      head.prev.next = head.next;
      head.next.prev = head.prev;
      head = head.prev;
    } else if(tail == node) {
      tail.prev.next = tail.next;
      tail.next.prev = tail.prev;
      tail = tail.next; 
    } else {
      node.prev.next = node.next;
      node.next.prev = node.prev;
    }
    size--; 
  }

  Buddy2Buffer front() {
    return head;	      
  }
  
  Buddy2Buffer rear() {
    return tail;	      
  }

}
