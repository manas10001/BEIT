/*
 The MIT License
 
 Copyright (c) 2005 - 2008
 1. Distributed Systems Group, University of Portsmouth (2005)
 2. Aamir Shafi (2005 - 2008)
 3. Bryan Carpenter (2005 - 2008)
 4. Mark Baker (2005 - 2008)
 
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
 * File         : Request.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sat Oct 2 12:22:15 BST 2004
 * Revision     : $Revision: 1.10 $
 * Updated      : $Date: 2005/12/21 13:18:17 $
 *
 */

package mpjdev;

import java.util.*;
import java.nio.ByteBuffer;

public abstract class Request {

  boolean isNull = false;

  LinkedList list = new LinkedList();

  public abstract mpjdev.Status iwait();

  public abstract Status itest();

  public abstract void free();

  public abstract boolean isnull();

  public abstract boolean cancel();

  Waitany waitany;

  static class Waitany {

    Request completed;

    int index;

    Request[] reqs;

    Waitany next, prev;

    boolean done;

    synchronized void wakeup() {
      done = true;
      notify();
    }

    synchronized void waitfor() {
      while (!done) {
	try {
	  wait();
	}
	catch (Exception e) {
	}
      }
    }

  }

  static WaitanyQue waitanyQue = new WaitanyQue();

  static class WaitanyQue {

    Waitany front, back;

    synchronized void remove(Waitany waitany) {
      if (front == back) {
	front = null;
	back = null;
      } else if (front == waitany) {
	front.prev.next = front.next;
	front.next.prev = front.prev;
	front = front.prev;
      } else if (back == waitany) {
	back.prev.next = back.next;
	back.next.prev = back.prev;
	back = back.next;
      } else {
	waitany.prev.next = waitany.next;
	waitany.next.prev = waitany.prev;
      }
    }

    synchronized void add(Waitany waitany) {
      if (listEmpty()) {
	front = waitany;
	back = waitany;
	waitany.next = waitany;
	waitany.prev = waitany;
      } else {
	front.next.prev = waitany;
	waitany.next = front.next;
	front.next = waitany;
	waitany.prev = front;
	back = waitany;
      }

    }

    synchronized Waitany front() {
      return front;
    }

    boolean listEmpty() {
      return (front == null && back == null);
    }

  }

  public static mpjdev.Status iwaitany(mpjdev.Request[] requests) {
    Waitany w = initializeWaitany(requests);
    Request r = null;
    Waitany wr = null;

    while (w.completed == null) {
      if (w == waitanyQue.front()) {

	do {
	  // TODO: This is a very dirty hack
	  // The request seriously needs to be abstracted in someway
	  // here dev is needed and the mpjdev.request is trying to
	  // access it as
	  // MPJDev.dev which doesn't exist, it exists in as
	  // javampjdev.MPJDev.dev
	  // and what about the nativ case?

	  if (Constants.isNative == false) { // TODO true or false ??
					     // false false
	    // I bet its false !!!
	    r = mpjdev.javampjdev.MPJDev.dev.peek();
	  } else {
	    r = null;
	  }

	  wr = processRequest(r);
	  // processRequest might return null ..
	  if (wr != w) {
	    w.wakeup();
	  }
	} while (wr != w);

	if (!waitanyQue.listEmpty()) {
	  waitanyQue.front.wakeup();
	}

      } else {
	w.waitfor();
      }
    }

    Status completedStatus = w.completed.iwait();
    completedStatus.index = w.index;
    // this flag was used in mpi.Request to check if completion operations
    // have already been called--but now we need a flag at this level
    // this ensures that if we call iwaitany with the same set of requests,
    // we do not endup finishing the same request object ..there is nothing
    // to stop iwait from choosiung the same reuqest ..so we need to have
    // some control mechanism to check that this request is NULL i.e. it has
    // already been completed ...
    if (completedStatus == null) {
      System.out.println("not possible 1");
    }

    if (requests[completedStatus.index] == null) {
      System.out.println("completedStatus.index " + completedStatus.index);
      System.out.println("not possible 2");
    }
    requests[completedStatus.index].isNull = true;
    return completedStatus;
    /*
     * mpjdev.Request peekedRequest = null; mpjdev.Status completedStatus =
     * null; boolean found = false ; boolean inActive = true ;
     * 
     * // check if there is a valid request which could be peeked for(int i=0 ;
     * i< requests.length ; i++) { if(requests[i] != null) { inActive = false; }
     * }
     * 
     * if(inActive) { return null; }
     * 
     * // first test for all message .. for(int i=0 ; i< requests.length ; i++)
     * { if(requests[i] != null) { completedStatus = requests[i].itest() ;
     * if(completedStatus != null) { completedStatus = requests[i].iwait() ;
     * completedStatus.index = i ; return completedStatus ; } } }
     * 
     * do { peekedRequest = MPJDev.dev.peek() ;
     * 
     * // calling iwait is definitely right thing to do for mxdev ... // not
     * sure about niodev ... completedStatus = peekedRequest.iwait() ;
     * 
     * // sort-out the index ... for(int j=0 ;j<requests.length ; j++) {
     * if(requests[j] != null) { if(peekedRequest == requests[j]) {
     * completedStatus.index = j ; found = true; break; } } }
     * 
     * } while(!found) ;
     * 
     * return completedStatus ;
     */
  }

  static synchronized Waitany initializeWaitany(Request[] reqs) {
    Waitany w = new Waitany();
    boolean found = false;

    for (int i = 0; i < reqs.length; i++) {
      if (reqs[i] != null && !reqs[i].isNull) {
	if (reqs[i].itest() != null) {
	  w.completed = reqs[i];
	  w.index = i;
	  found = true;
	  break;
	}
      }
    }

    if (!found) {
      for (int i = 0; i < reqs.length; i++) {
	if (reqs[i] != null) {
	  reqs[i].waitany = w;
	}
      }
      waitanyQue.add(w);
    }

    w.reqs = reqs;
    return w;
  }

  static synchronized Waitany processRequest(Request r) {

    Waitany w = r.waitany;

    if (w == null) {
      return null;
    }

    w.completed = r;

    for (int i = 0; i < w.reqs.length; i++) {
      // System.out.println("w.reqs["+i+"]=<"+w.reqs[i]+">");
      // System.out.println("r=<"+r+">");
      if (w.reqs[i] == r) {
	// System.out.println("setting the index ");
	w.index = i;
      }

      if (w.reqs[i] != null) {
	w.reqs[i].waitany = null;
      }
    }

    waitanyQue.remove(w);

    return w;

  }

  /*
   * invoke all completion handlers in order they were defined
   */
  protected void complete(mpjdev.Status status) {
    Iterator iter = list.iterator();
    CompletionHandler handler = null;

    while (iter.hasNext()) {

      handler = (CompletionHandler) iter.next();

      handler.handleCompletion(status);
    }
  }

  /*
   * add handler (logically) to the end of a list
   */
  public void addCompletionHandler(mpjdev.CompletionHandler handler) {
    list.add(handler);
  }
}
