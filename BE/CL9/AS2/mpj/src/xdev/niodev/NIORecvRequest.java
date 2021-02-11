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
 * File         : NIORecvRequest.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 BST 2004
 * Revision     : $Revision: 1.10 $
 * Updated      : $Date: 2005/12/21 13:18:18 $
 *    
 */

package xdev.niodev;

import java.util.UUID;
import java.nio.channels.*;
import java.nio.*;
import mpjdev.*;
import mpjbuf.NIOBuffer ;
import xdev.*;

public class NIORecvRequest
    extends NIORequest {
  long sequenceNum ;

  // 4 hash table keys for queues in which send request may be placed,
  // or in which matching receive requests may be found.
  NIODevice.Key [] arrKeys ;
      
  // Link fields for 4 queues in which request may be placed.
  // Next entry in queue.
  NIORecvRequest arrNext [] ;
  
  // Previous entry in queue, (or final entry in queue, if this request
  // is at front of queue).
  NIORecvRequest arrPrev [] ;
  
  // Hash table key for queue in which receive request may be placed,
  // or in which matching send request may be found.
  NIODevice.Key recvKey ;

  // Link fields for queue in which request may be placed.
  // Next entry in queue.
  NIORecvRequest recvNext ;

  // Previous entry in queue, (or final entry in queue, if this request
  // is at front of queue).
  NIORecvRequest recvPrev ;
	  
  int code = -1;
  boolean readPending = false;
  int bytesRead = 0;
  NIOSendRequest sendRequest ;

  /* if the events are to be posted by user thread */
  NIORecvRequest(ProcessID srcID, String src, String dst, ProcessID dstID,
                 int tag, boolean completed,
                 mpjbuf.Buffer buf, int context, 
		 mpjdev.Status status, int recvCounter , 
		 long nextSequenceNum ) {

    this.srcUUID = srcID.uuid() ;
    this.dstUUID = dstID.uuid() ;

    this.tag = tag;
    this.completed = completed;
    this.staticBuffer = ((NIOBuffer)buf.getStaticBuffer()).getBuffer() ;
    this.buffer = buf;
    this.dynamicBuffer = buf.getDynamicBuffer();
    this.context = context;
    //this.rank_source = srcID.rank() ;
    this.status = status;
    this.recvCounter = recvCounter ;

    synchronized(NIORecvRequest.class) {
      sequenceNum =  nextSequenceNum ; 
    }
  }

  /* if the events are to be posted by selector thread */
  NIORecvRequest(UUID dstUUID, int tag,  boolean completed, 
                 int context, int sBufSize, int dBufSize, int commMode,
                 SocketChannel channel, int numEls, mpjbuf.Type type, 
		 int sendCounter, int recvCounter , UUID srcUUID ) {
	  
    this.arrNext = new NIORecvRequest [4] ;
    this.arrPrev = new NIORecvRequest [4] ;
	      
    this.dstUUID = dstUUID;
    this.tag = tag;
    this.completed = completed;
    this.context = context;
    this.sBufSize = sBufSize;
    this.dBufSize = dBufSize;
    this.commMode = commMode;
    this.channel = channel;
    this.numEls = numEls;
    this.type = type;
    this.sendCounter = sendCounter ;
    this.recvCounter = recvCounter ;
    this.srcUUID = srcUUID ;
  }

  SocketChannel channel = null;
  int commMode = 0;

  int getCommMode() {
    return commMode;
  }

  void setCommMode(int commMode) {
    this.commMode = commMode;
  }

  public Status iwait() {
    if(alreadyCompleted) {
		if (NIODevice.isHybrid) {
			status.srcID = this.srcHybUUID;
		}
    return status ; 	    
    }

    this.waitMe();

    /* this should be probably be done somewhere at the start of irecv
     * method */
    status.tag = this.tag;
    //status.source = this.rank_source; [ we dont know rank at this level]
    status.numEls = this.numEls;
    status.type = this.type;
    
    if (NIODevice.isHybrid) {
	 status.srcID = this.srcHybUUID;
	}else{
    status.srcID = this.srcUUID;
	}
    
    complete(status);
    this.alreadyCompleted = true ; 
    return status;
  }

}
