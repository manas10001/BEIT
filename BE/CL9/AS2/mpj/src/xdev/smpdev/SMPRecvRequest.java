/*
The MIT License

 Copyright (c) 2005 - 2010
    1. Distributed Systems Group, University of Portsmouth (2005)
    2. Community Grids Laboratory, Indiana University (2004)
    3. Aamir Shafi (2005 - 2010)
    4. Bryan Carpenter (2005 - 2010)
    5. Jawad Manzoor (2009)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

/*
 * File         : SMPRecvRequest.java
 * Author       : Bryan Carpenter, Aamir Shafi
 * Created      : Fri Nov 15 21:19:37 EST 2002
 * Revision     : $Revision: 1.2 $
 * Updated      : $Date: 2005/03/24 10:49:51 $
 */

package xdev.smpdev; 

import xdev.ProcessID;

public class SMPRecvRequest extends SMPRequest {

    // Hash table key for queue in which receive request may be placed,
    // or in which matching send request may be found.
    SMPDeviceImpl.Key key ;
    
    // Link fields for queue in which request may be placed.
    // Next entry in queue.
    SMPRecvRequest next ;

    // Previous entry in queue, (or final entry in queue, if this request
    // is at front of queue).
    SMPRecvRequest prev ;

    long sequenceNum ;

    private static long nextSequenceNum = 0 ;

    SMPRecvRequest(mpjbuf.Buffer buf, int context, ProcessID destID, 
		    ProcessID sourceID, int tag, mpjdev.Status status) {
//System.out.println("Overloaded constructor called in SMPRecvRequest "+this.hashCode()); 	    
        completed = false;
        //this.status = status;
/*	SMPDeviceImpl.out.println(" initializing the keys of SMPRecvRequest");
	SMPDeviceImpl.out.println(" context  "+context );
	SMPDeviceImpl.out.println(" destID   "+destID );
	SMPDeviceImpl.out.println(" sourceID "+sourceID );
	SMPDeviceImpl.out.println(" tag "+tag );
*/	
        key = new SMPDeviceImpl.Key(context, destID, sourceID, tag) ;
       // key = new SMPDeviceImpl.Key(context, sourceID, tag) ;
        //this.rank_source = sourceID.rank();
        this.buffer = buf ;
        this.dynamicBuffer = buf.getDynamicBuffer();
        this.status = status;
        this.srcID = sourceID;
        synchronized(SMPRecvRequest.class) {
            sequenceNum = nextSequenceNum++ ;
        }
    }
}

