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
 * File         : MXRequest.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sun Oct 23 18:56:11 GMT 2005
 * Revision     : 
 * Updated      : 
 *    
 */

package xdev.mxdev;

import java.util.UUID;
import java.nio.*;
import mpjdev.*;
import xdev.*;

import mpjbuf.RawBuffer ;

public abstract class MXRequest
    extends Request {

  // in theory do not need this for SendRequest ...it is required for recv
  // requests ...when wiat completes after irecv ..it fills in all the info
  // in this and this guy is returned ..i am also using it in nativepeek ...
  // when the first wait is called .. it has some info that has to be 
  // filled in the status ..so i am this status object to fill that info in ..
  // later when wait is called after calling peek ..we make sure that we dont
  // call wait for primary message as it has already been called ..thus 
  // we need info like what was the size of primary message and tag ..and 
  // that info is in this status object ...
  mpjdev.Status status ;
  long handle, localEndpointHandle, requestStruct ;
  protected mpjbuf.Buffer bufferHandle;
  //long nativeRequestHandle ; 
  int testCalled = 0;
  int isPeeked = 0 ;

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

  /**
   */
  public abstract Status iwait();

  /** this method returns Status object if the communication is completed,
   *  and if its not completed, it returns zero.
   */
  public abstract Status itest();

  public void free() {
  }

  public boolean isnull() {
    return false;
  }

}
