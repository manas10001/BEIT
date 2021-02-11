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
 * File         : SMPDevice.java
 * Author       : Aamir Shafi
 * Created      :
 * Revision     : 
 * Updated      :
 */
package xdev.smpdev;

import xdev.*;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;


/**
 * An abstract device
 */
public class SMPDevice implements Device {

    public static Logger logger = Logger.getLogger("mpj");
		public static int SEND_OVERHEAD=0;
		public static int RECV_OVERHEAD=0;
		
		
    /**
     * init
     * The question that is coming to me mind again and again is ...
     * so each thread has an object of xdev.smpdev.SMPDevice
     * so when they call like SMPDevice.init( ..) method ...in order to sync
     * the access, do we need to make this method static or its possible
     * to sync different instances of this object ....
     * Welcome to concurrent-programming ..but not as hard as parallel
     * programming itself ...
     *
     * Write a test program .... yeah i did ..but we need static methods ...
     * args[0] is +=+> nprocs ...
     * args[1] is =+=> rank   ... is it like ProcessID ?
     * args[2] conf-file may be passed if required ...
     */
    public ProcessID[] init(String[] args) throws XDevException {

        if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug("Inside SMPDevice Init");      
        }
        ProcessID[] ids = null;

        try {

            ids = xdev.smpdev.SMPDeviceImpl.init(args[1],
                    Integer.parseInt(args[0]));
        } catch (Exception e) {
            throw new XDevException(e);
        }

        return ids;
    }

    /**
     * returns my id
     */
    public ProcessID id() {
        return xdev.smpdev.SMPDeviceImpl.WORLD.id();
    }

    /**
     * Shuts the device
     */
    public void finish() throws XDevException {
        xdev.smpdev.SMPDeviceImpl.finish();
    }

    /**
     * Non-blocking send using standard-mode.
     */
    public mpjdev.Request isend(mpjbuf.Buffer buf, ProcessID destID,
            int tag, int context)
            throws XDevException {

        mpjdev.Request req = null;

        try {
            req = xdev.smpdev.SMPDeviceImpl.WORLD.isend(buf, destID,
                    tag, context);
        } catch (Exception e) {
            throw new XDevException(e);
        }

        return req;

    }

    /**
     * Blocking send using standard-mode.
     */
    public void send(mpjbuf.Buffer buf, ProcessID destID,
            int tag, int context) throws XDevException {

        try {
            xdev.smpdev.SMPDeviceImpl.WORLD.send(buf, destID, tag, context);
        } catch (Exception e) {
            throw new XDevException(e);
        }

    }

    /**
     * Non-blocking send using synchronous-mode
     */
    public mpjdev.Request issend(mpjbuf.Buffer buf, ProcessID destID,
            int tag, int context) throws XDevException {
        mpjdev.Request req = null;

        try {
            req = xdev.smpdev.SMPDeviceImpl.WORLD.isend(buf, destID, tag, context);
        } catch (Exception e) {
            throw new XDevException(e);
        }

        return req;

    }

    /**
     * Blocking send using synchronous-mode
     */
    public void ssend(mpjbuf.Buffer buf, ProcessID destID,
            int tag, int context) throws XDevException {
        try {
            xdev.smpdev.SMPDeviceImpl.WORLD.send(buf, destID, tag, context);
        } catch (Exception e) {
            throw new XDevException(e);
        }
    }

    public mpjdev.Status recv(mpjbuf.Buffer buf, ProcessID srcID,
            int tag, int context)
            throws XDevException {
        mpjdev.Status s = null;

        try {
            s = xdev.smpdev.SMPDeviceImpl.WORLD.recv(buf, srcID, tag, context);
        } catch (Exception e) {
            throw new XDevException(e);
        }

        return s;

    }

    public mpjdev.Request irecv(mpjbuf.Buffer buf, ProcessID srcID,
            int tag, int context, mpjdev.Status status)
            throws XDevException {
        mpjdev.Request req = null;

        try {
            req = xdev.smpdev.SMPDeviceImpl.WORLD.irecv(buf, srcID,
                    tag, context, status);
        } catch (Exception e) {
            throw new XDevException(e);
        }

        return req;

    }
   /**
   * Blocking probe method
   * @param srcID The sourceID of the sender
   * @param tag The tag of the message
   * @param context The integer specifying the context
   * @return mpjdev.Status The status object
   */
    public mpjdev.Status probe(ProcessID srcID, int tag,
            int context) throws XDevException {

    mpjdev.Status status = null;
    boolean comp = false;

    while (!comp) {
      status = this.iprobe(srcID, tag, context);
      if (status != null) {
        comp = true;
      }
    }

    return status;
    }

      /**
   * Non-Blocking probe method.
   * @param srcID
   * @param tag
   * @param context
   * @return mpjdev.Status
   */

    public mpjdev.Status iprobe(ProcessID srcID, int tag,
            int context) throws XDevException {
      
    // ProcessID dstUUID = id().uuid();
   ///  ProcessID srcUUID = srcID.uuid();
        mpjdev.Status status = null;

        try {
            status = xdev.smpdev.SMPDeviceImpl.WORLD.iprobe(srcID,
                    tag, context);
        } catch (Exception e) {
            throw new XDevException(e);
        }

      
              return status;
        
   // return null;	  
}
  /**
   * Non-Blocking overloaded probe method.
   * 
   * @param srcID
   * @param dstID
   * @param tag
   * @param context
   * @return mpjdev.Status
   */  
  public mpjdev.Status iprobeAndFetch(ProcessID srcID, ProcessID dstID, int tag,
          int context, mpjbuf.Buffer buf) throws XDevException {
    
    return xdev.smpdev.SMPDeviceImpl.WORLD.iprobeAndFetch( srcID, dstID,  tag,
          context,  buf);
     
  }
  /**
   * This method does not have any definitation, 
   * It is added in xdev.Device.java
   * Network device should have its implementation only
   * */
  public mpjdev.Status iprobe(ProcessID srcID, ProcessID dstID, int tag,
            int context) throws XDevException { 
    return null;
  }
  
public mpjdev.Request peek() throws XDevException {
    return null ; 
  }

// putting in the the peek utility here in the file
static   CompletedList completedList = new CompletedList() ;

  static class CompletedList { 
	  
    SMPRequest front, back ; 	 
    int size ; 

    /** 
     * Remove request from any position in the completedList
     */
    synchronized void remove(SMPRequest request) { 
      if(request.inCompletedList) { 
        if(front == back) {
          front = null;
                back = null;
            } else if (front == request) {
                front.prevCompleted.nextCompleted = front.nextCompleted;
                front.nextCompleted.prevCompleted = front.prevCompleted;
                front = front.prevCompleted;
            } else if (back == request) {
                back.prevCompleted.nextCompleted = back.nextCompleted;
                back.nextCompleted.prevCompleted = back.prevCompleted;
                back = back.nextCompleted;
            } else {
                request.prevCompleted.nextCompleted = request.nextCompleted;
                request.nextCompleted.prevCompleted = request.prevCompleted;
            }

            request.inCompletedList = false;
            size--;
            if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
              logger.debug(" size " + size);
	    }
        }
    }

    /** 
     * Remove request from the front of completedList
     * Wait until a request is found
     */
    synchronized SMPRequest remove() {

        while (listEmpty()) {
            try {
                wait();
            } catch (Exception e) {
            }
        }

        SMPRequest oldFront = null;
        oldFront = front;
        if (front == back) {
            front = null;
            back = null;
        } else {
            front.prevCompleted.nextCompleted = front.nextCompleted;
            front.nextCompleted.prevCompleted = front.prevCompleted;
            front = front.prevCompleted;
        }

        oldFront.inCompletedList = false;
        size--;

        if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" size " + size);
	}

        return oldFront;
    }

    /**
     * Add request at the front of completedList 
     */
    synchronized void add(SMPRequest request) {
        if (listEmpty()) {
            front = request;
            back = request;
            request.nextCompleted = request;
            request.prevCompleted = request;
        } else {
            front.nextCompleted.prevCompleted = request;
            request.nextCompleted = front.nextCompleted;
            front.nextCompleted = request;
            request.prevCompleted = front;
            back = request;
        }
        size++;
	if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" size " + size);
	}
        request.inCompletedList = true;
        notify();
    }

    boolean listEmpty() {
        return (front == null && back == null);
    }
}

/// ending here the pasting :)
	public int getRecvOverhead() { 
    return RECV_OVERHEAD ; 
  }

	public int getSendOverhead() { 
    return SEND_OVERHEAD ; 
  }
}
