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
 * File         : MXSendRequest.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sun Oct 23 18:59:59 GMT 2005
 * Revision     : 
 * Updated      : 
 *    
 */

package xdev.mxdev ;

import java.util.UUID;
import java.nio.channels.*;
import java.nio.*;
import mpjdev.*;
import mpjbuf.NIOBuffer ;
import xdev.*;

public class MXSendRequest
    extends MXRequest {
  
  long dbufHandle ;
  int dbuflen ; 

  public Status iwait() {
    mpjdev.Status status = new mpjdev.Status();	  
    nativeIwait(status);	 
    // not putting this in itest because at MPI level, each Test() calls
    // device's test() followed by wait() .. 
    MXDevice.requestMap.remove(new Long(this.handle)); 
    complete(status); 
    testCalled = 1 ; 	    
    return status;
  }

  public Status itest() {
    mpjdev.Status status = new mpjdev.Status();	  
    int isCompleted = nativeItest(status);	  
    if(isCompleted == 1) { 
      testCalled = 1 ; 	    
    }
    return ((isCompleted == 1)?status:null) ; 
  }

  native void nativeIwait(Status status);
  native int nativeItest(Status status);
  private static native void nativeRequestInit();

  static {
    nativeRequestInit();	  
  }

}
