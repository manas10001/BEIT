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
 * File         : MXRecvRequest.java
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

public class MXRecvRequest
    extends MXRequest {

  //probaby very ugly to put a reference for the device here.
  //the problem is that status contains this rank(id) that is used 
  //get the srcID of the process. this srcID is a pain in the ass, 
  //i think we should just get rid of it. what we need is a mapping
  //from commRank to devRank ....devRank will be similar to 
  //commRank in WORLD Communicator. 
  MXDevice device ; 
  int [] ctrlMsg = new int[2];

  MXRecvRequest(MXDevice device) { 
    this.device = device ; 	  
  }
  
  protected long matchRecvHandle, matchRecvMaskHandle, sBufLengthHandle,
	         dBufLengthHandle, bufferAddressHandle, 
		 ctrlMsgHandle ;

  public Status iwait() {
  //  System.out.println("this <"+this+"> calling iwait with testCalled <"+testCalled+">");
    nativeIwait(status);	  
    //System.out.println("this <"+this+"> called iwait with testCalled <"+testCalled+">");
    status.srcID = device.pids[status.source].uuid(); 
    //System.out.println(" this <"+this+"> bufferHandle.getSize() "+bufferHandle.getSize()); 
    // not putting this in itest because at MPI level, each Test() calls
    // device's test() followed by wait() .. 

    MXDevice.requestMap.remove(new Long(this.handle)); 
    try { 
      bufferHandle.commit(); 
      status.type = bufferHandle.getSectionHeader();
      status.numEls = bufferHandle.getSectionSize(); 
    }
    catch(Exception e) { 
      e.printStackTrace(); 
      //throw new XDevException(e); 	    
    }  
    complete(status); 
    testCalled = 1; 
    return status;
  }

  public Status itest() { 
    //System.out.println("this <"+this+
//		    "> calling itest with testCalled <"+testCalled+">");
    if(testCalled == 1) {
//    System.out.println(" returning as this has been called before ");	    
      return status;  	    
    }
    int isCompleted = nativeItest(status);	  
    //System.out.println("this <"+this+
//		    "> called itest with testCalled <"+testCalled+">");
    if(isCompleted == 1) { 
      status.srcID = device.pids[status.source].uuid(); 
      testCalled = 1; 
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
