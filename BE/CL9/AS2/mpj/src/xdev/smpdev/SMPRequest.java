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
 * File         : SMPRequest.java
 * Author       : Sang Lim, Aamir Shafi
 * Created      : Thu Jan 17 17:17:40 2002
 * Revision     : $Revision: 1.4 $
 * Updated      : $Date: 2005/06/23 12:43:29 $
 */
/**
 * Request object for non-blocking communications.
 */
package xdev.smpdev;

import mpi.*;
import mpjdev.Status;
import mpjdev.Request;
import mpjbuf.Buffer;
import xdev.XDevException;
import xdev.ProcessID;
import mpjbuf.RawBuffer;
import java.nio.*;
import java.util.UUID;

public class SMPRequest extends mpjdev.Request {
        

    int context = -1, tag, numEls = -1;
    mpjbuf.Type type = null;
    ProcessID srcID;
    // added variable for the peek method
    SMPRequest nextCompleted, prevCompleted;
    boolean inCompletedList;
    boolean dSection = true, sSection = true;
    int commMode, rank_source = 0, sBufSize = 0, dBufSize = 0;
    RawBuffer eagerBuffer = null;
    byte[] dynamicBuffer = null;
    ByteBuffer staticBuffer;
    int sendCounter;
    mpjbuf.Buffer buffer;
    mpjdev.Status status=null;// jd

    /**
     * Wait for a single non-blocking communication to complete.
     * If this was a receive, initializes the `source' and `tag' fields
     * of the returned status object.
     * Equivalent to MPI_WAIT.
     */
    public SMPRequest() {
//System.out.println("Default constructor called in SMPRequest "+this.hashCode()); 	    
    }

    public mpjdev.Status iwait() throws XDevException {

        /* why is this returning null?! */
        if (completed) {
//System.out.println("Returning null from -- iwait ");
           // return null;
            return this.status;
        }


        WaitSet waiting = null;

        synchronized (SMPDeviceImpl.class) {
            if (isPending()) {
    //             System.out.println("isPending Status "+ this.hashCode());
                waiting = new WaitSet(new SMPRequest[]{this});
            }
        }

        if (waiting != null) {

            try {
  //            System.out.println("awaiting selection "+ this.hashCode());
                waiting.awaitSelection();
            } catch (InterruptedException e) {
                // Should use `cause', but not sure if everybody is using 1.4
                throw new XDevException("In iwait, unexpected " +
                        "InterruptedException: " + e.getMessage());
            }
        }
        // complete(new mpjdev.Status(status));
//System.out.println("calling complete "+ this.hashCode());
        //  System.out.println("-- 2 Status "+ status.srcID +"  "+status.tag);


        complete(status);
        completed = true;

	status.numEls = this.numEls ; 
	status.type = this.type ; 

//System.out.println("Returning status from -- iwait "+ this.hashCode());
        return this.status;

    }

    /**
     * Wait for one non-blocking communication from a set to complete.
     * The `index' field of the returned status object defines which
     * communication in the `reqs' array was selected.
     * If this was a receive, the `source' and `tag' fields
     * of the returned status object are also initialized.
     * Equivalent to MPI_WAITANY.
     */
    public static mpjdev.Status iwaitany(SMPRequest[] reqs) throws XDevException {
//System.out.println("Inide iwaitany");
        boolean empty = true;
        SMPRequest selected = null;
        WaitSet waiting = null;

        synchronized (SMPDeviceImpl.class) {

            for (int i = 0; i < reqs.length; i++) {
                SMPRequest req = reqs[i];
                if (req == null) {
                    throw new XDevException("In iwaitany, requests array " +
                            "contains a null element");
                } else if (!req.completed) {
                    empty = false;
                    if (!req.isPending()) {
                        req.status.index = i;
                        selected = req;
                        break;
                    }
                }
            }

            if (empty) {
                return null;
            }

            if (selected == null) {
                waiting = new WaitSet(reqs);
            }
        }

        if (waiting != null) {
            try {

                selected = waiting.awaitSelection();
            } catch (InterruptedException e) {
                // Should use `cause', but not sure if everybody is using 1.4
                throw new XDevException("In iwaitany, unexpected " +
                        "InterruptedException: " + e.getMessage());
            }
        }

        selected.completed = true;

        //Should be changed... should remove 'MPI_REQUEST_NULL'

        reqs[selected.status.index] = MPI_REQUEST_NULL;

        return selected.status;
    }
    // The `pending' flag is cleared after a send (respectively receive)
    // matching this receive (respectively send) request has been posted.
    private boolean pending = false;

    void setPending(boolean pending) {

        this.pending = pending;
    }

    boolean isPending() {

        return pending;
    }
    // The `completed' flag is set after successful completion of an
    // `iwait' on this request, or succesful completion of an `iwaitany'
    // selecting this request.
    boolean completed = true;
    private WaitSet waitSet;

    void addToWaitSet(WaitSet waitSet, int index) {

        this.waitSet = waitSet;

        status.index = index;
    }

    public boolean cancel() {
        return false;
    }

    public void free() {
    }

    public boolean isnull() {
        return false;
    }

    public mpjdev.Status itest() {
        if (completed) {
            return null;
        }
        synchronized (this) {
              //     System.out.println("is pending? "+this.isPending());
             if (!this.isPending()) { //jd
                   UUID srcUUID = srcID.uuid();
                   mpjdev.Status st = new mpjdev.Status(srcUUID, tag, -1); //what is index ?
              //     System.out.println("status is null? "+st);
            return st;
        }
       else {
                       //      System.out.println("status is null");
           return null; //change this to something understand at higher levels ...
         }
        }
    }
    /*
    public mpjdev.Status itest() {
    return null;
    }
     */

    void removeFromWaitSet() {
        waitSet = null;
    }

    WaitSet getWaitSet() {
        return waitSet;
    }

    /**
     *  Not sure what this is exactly.  It's like the requests are channels
     *  and the "WaitSet" is an alternation---sort of, but not quite.
     *  There is probably a cleaner abstraction lurking here somewhere.
     */
    static class WaitSet {

        private Semaphore sem;
        SMPRequest reqs[];
        SMPRequest selected;

        /**
         *  Create a "wait set" containing specified requests.
         */
        public WaitSet(SMPRequest[] reqs) {

            sem = new Semaphore(0);

            this.reqs = reqs;

            for (int i = 0; i < reqs.length; i++) {
                reqs[i].addToWaitSet(this, i);
            }
        }

        /**
         *  Select an element from the wait set, and clear the set.
         */
        void select(SMPRequest req) {

            selected = req;

            for (int i = 0; i < reqs.length; i++) {
                reqs[i].removeFromWaitSet();
            }

            sem.signal();
        }

        /**
         *  Wait for an element of the wait set to be selected.
         */
        SMPRequest awaitSelection() throws InterruptedException {

            sem.acquire();
           //  System.out.println("---sem aquired ");
            return selected;
        }
    }
    public static final SMPRequest MPI_REQUEST_NULL = new SMPRequest();
}

class Semaphore {

    private int s;

    public Semaphore(int s) {
        this.s = s;
    }

    public synchronized void acquire() throws InterruptedException {
        if (s == 0) {
//System.out.println("calling wait(0) "+ this.hashCode());
//new Exception().printStackTrace(System.out);
            wait(0);
        }
        s--;
    }

    public synchronized void signal() {
        s++;
        notify();
    }
}

