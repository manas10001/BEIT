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
 * File         : SMPSendRequest.java
 * Author       : Bryan Carpenter, Aamir Shafi
 * Created      : Thu Nov 14 12:06:35 EST 2002
 * Revision     : $Revision: 1.2 $
 * Updated      : $Date: 2005/03/24 10:49:51 $
 */
package xdev.smpdev;
import mpjbuf.RawBuffer ;
import xdev.XDevException;
import mpjbuf.NIOBuffer ;
import xdev.ProcessID;
import mpjbuf.RawBuffer ;
import java.util.UUID;

public class SMPSendRequest extends SMPRequest {

    // 4 hash table keys for queues in which send request may be placed,
    // or in which matching receive requests may be found.
    SMPDeviceImpl.Key[] keys;

    // Link fields for 4 queues in which request may be placed.

    // Next entry in queue.
    SMPSendRequest next[];

    // Previous entry in queue, (or final entry in queue, if this request
    // is at front of queue).
    SMPSendRequest prev[];
   // int tag, context;
   // ProcessID sourceID;
int bufoffset = 0;

// constructor of the SMPSendRequest
    SMPSendRequest(mpjbuf.Buffer buf, int context, ProcessID destID,
            ProcessID sourceID, int tag) {

        completed = false;

        try {
            buf.commit();
            this.type = buf.getSectionHeader();
            this.numEls = buf.getSectionSize();
        } catch (Exception e) {
            throw new XDevException(e);
        } //jd

        /*	SMPDeviceImpl.out.println(" initializing the keys of SMPSendRequest");
        SMPDeviceImpl.out.println(" context  "+context );
        SMPDeviceImpl.out.println(" destID   "+destID );
        SMPDeviceImpl.out.println(" sourceID "+sourceID );
        SMPDeviceImpl.out.println(" tag "+tag );
         */
        /*  keys = new SMPDeviceImpl.Key [] {
        new SMPDeviceImpl.Key(context, destID, sourceID, tag),
        new SMPDeviceImpl.Key(context, destID, sourceID, SMPDeviceImpl.ANY_TAG),
        new SMPDeviceImpl.Key(context, destID, SMPDeviceImpl.ANY_SRC, tag),
        new SMPDeviceImpl.Key(context, destID, SMPDeviceImpl.ANY_SRC, SMPDeviceImpl.ANY_TAG)
        } ;
*/ 

        keys = new SMPDeviceImpl.Key[]{
                    new SMPDeviceImpl.Key(context,destID, sourceID, tag),
                    new SMPDeviceImpl.Key(context, destID,sourceID, SMPDevice.ANY_TAG),
                    new SMPDeviceImpl.Key(context, destID,xdev.Device.ANY_SRC, tag),
                    new SMPDeviceImpl.Key(context, destID,xdev.Device.ANY_SRC, SMPDevice.ANY_TAG)
                };

/*
         keys = new SMPDeviceImpl.Key[]{
                    new SMPDeviceImpl.Key(context, sourceID, tag),
                    new SMPDeviceImpl.Key(context, sourceID, SMPDevice.ANY_TAG),
                    new SMPDeviceImpl.Key(context, xdev.Device.ANY_SRC, tag),
                    new SMPDeviceImpl.Key(context, xdev.Device.ANY_SRC, SMPDevice.ANY_TAG)
                };
*/
        next = new SMPSendRequest[4];
        prev = new SMPSendRequest[4];

        this.buffer = buf;
       // this.sourceID = sourceID;
        this.tag = tag;
        this.context = context;
        this.srcID = sourceID;
        this.sBufSize = buf.getSize();
        this.staticBuffer = ((NIOBuffer) buf.getStaticBuffer()).getBuffer();

      //  this.commMode = commMode;
      //  this.sendCounter = sendCounter;
        this.bufoffset = buf.offset();
        UUID srcUUID = sourceID.uuid();
        this.status = new mpjdev.Status(srcUUID, tag, -1); //jd
    }
}

