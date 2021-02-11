/*
 The MIT License

 Copyright (c) 2005 - 20013
   1. SEECS National University of Sciences and Technology
   2. Aamir Shafi (2005 - 2013)
   3. Ansar Javed (2013 - 2013)
   4. Mohsan Jameel (2013 - 2013)
   5. Bibrak Qamar  (2013 - 2013)

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
 * File         : HYBDevice.java
 * Author       : Aamir Shafi, Mohsan Jameel, Ansar Javed, Bibrak Qamar
 * Created      : Mon Aug  5 12:22:15 PKT 2013
 * Revision     : $Revision: 1.28 $
 * Updated      : $Date: 2013/12/17 17:24:47 $
 *    
 */

package xdev.hybdev;

import java.util.UUID;
import java.nio.*;
import mpjdev.*;
import xdev.*;

import mpjbuf.RawBuffer ;

public abstract class HYBRequest
    extends Request {

  HYBRequest nextCompleted, prevCompleted ; 
  boolean inCompletedList ; 
  
  HYBDevice device ; 

  /* buffers for storing static/dynamic sections of the buffer */
  ByteBuffer dBuffer = null;
  RawBuffer eagerBuffer = null;
  byte[] dynamicBuffer = null;
  ByteBuffer staticBuffer;
  byte[] bytes = null; 
  boolean alreadyCompleted = false ; 

  
  boolean dSection = true, sSection = true;
  int context = -1, tag, commMode, rank_source = 0, sBufSize = 0, dBufSize = 0 ; 
  int sendCounter, recvCounter, numEls = -1 ; 
  
  UUID dstUUID, srcUUID;
  mpjdev.Status status = null;
  mpjbuf.Buffer buffer = null;
  mpjbuf.Type type = null;

  volatile boolean completed = false; 

  public boolean cancel() {
    /* First start considering the sender side
     * a) Send. --> For eager-send, its not possible.
     *          --> For rendezvous, yeah, can be cancelled.
     *    Isend --> Eager-send, not possible.
     *          --> For rendezvous, not possible.
     * b) Same for Ready/Send
     * c) Bsend --> Not possible at all.
     * d) Ssend --> Possible.
     * e) Recv  --> Eager-send, not possible.
     *          --> Rendezvous-protocol.*/

    return false;
  }

  synchronized void setCompleted(boolean completed) {
    this.completed = completed;
  }

  synchronized boolean isCompleted() {
    return completed;
  }

  public static Status iwaitany(HYBRequest [] requests) { 
    System.out.println(" HYBRequest.iwaitany( .. )") ;
    return null;
  }

  /**
   */
  public abstract Status iwait();

  /**
   * Method used by iwait() to actually wait for the communication to finish.
   */
  synchronized void waitMe() {
    while (!completed) {
      try {
        this.wait();
      }
      catch (Exception e) {
        e.printStackTrace() ; 
      }
    }

    //device.completedList.remove(this); 
    // .. remove from completedList ..
  }

  /**
   * Method used to notify that the communication operation has finished.
   */
  synchronized void notifyMe() {
    this.completed = true;

    try {
      this.notify();
    }
    catch (Exception e) {
      e.printStackTrace() ;
    }
  }

  /** this method returns Status object if the communication is completed,
   *  and if its not completed, it returns zero.
   */
  public Status itest() {
    if(alreadyCompleted) {
      return null; 
    }
    synchronized (this) {
      if (this.isCompleted()) {
        return new mpjdev.Status(rank_source, tag, -1); //what is index ?
      }
      else {
        return null; //change this to something understand at higher levels ...
      }
    }
  }

  public void free() {
  }

  public boolean isnull() {
    return false;
  }
}
