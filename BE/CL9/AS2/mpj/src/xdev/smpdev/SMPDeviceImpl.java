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
 * File         : SMPDeviceImpl.java
 * Author       : Sang Lim, Bryan Carpenter, Aamir Shafi 
 * Created      : Wed Nov 13 17:07:15 EST 2002
 * Revision     : $Revision: 1.5 $
 * Updated      : $Date: 2005/06/23 12:43:29 $
 */
package xdev.smpdev;

//Replace these stars with something more understandable ...
import xdev.*;
//import xdev.niodev.ConfigReader ; 
import mpjdev.Status;
import mpjdev.Request;
import mpjbuf.*;
import java.nio.ByteBuffer;
import mpjbuf.NIOBuffer;
import java.util.HashMap;
import java.util.UUID;
import java.io.PrintStream;
import java.io.FileOutputStream;

/**
 * The communicator class.  Directly analogous to an MPI communicator.
 */
public class SMPDeviceImpl {

    static int numRegisteredThreads = 0;
    private static boolean initialized = false;  // MPJ initialized
    public static PrintStream out = null;
    private int size;
    private Thread[] threads;    //Map from node id to Thread.
    private HashMap ids;          //Map from Thread to node id.
    xdev.ProcessID id = null;
    xdev.ProcessID[] pids = null;
    static SMPDevProcess smpProcess = null;

    private int context;
    private int barrierCount;
    private SMPDeviceImpl newSMPDeviceImpl;
    public static final int MODEL_MULTIPROCESS = 0;
    public static final int MODEL_MULTITHREADED = 1;

    public static int getModel() {
        return MODEL_MULTITHREADED;
    }

    /**
     * Number of processes spanned by this communicator.
     */
    public int size() {
        return size;
    }

    /**
     * Id of current process relative to this communicator.
     * Equivalent to MPI_COMM_RANK.
     */
    public xdev.ProcessID id() throws XDevException {

        Thread thisThread = Thread.currentThread();
        ProcessID value = null ; 

	if(thisThread.getThreadGroup() instanceof SMPDevProcess) {
	  value = ((SMPDevProcess)(thisThread.getThreadGroup())).getID();
	} 
	 
        if (value == null) {
            throw new XDevException("SMPDeviceImpl.id() invoked by thread " +
                    "outside communicator group");
        }
        return value;
    }
    /**
     * Create a new communicator the spanning the same
     * set of processes, but with a distinct communication context.    
    public SMPDeviceImpl dup() throws XDevException {
    int [] ids = new int [size] ;
    for(int i = 0 ; i < size ; i++)
    ids [i] = i ;

    return create(ids) ;
    }
     */
    private static int nextContext;

    /**
     * Create a new communicator the spanning the set of processes
     * selected by the `ids' array (containing ids are relative to this
     * communicator).
     * The new communicator also has a distinct communication context.
     * Processes that are out side of the group will return null.

    public synchronized SMPDeviceImpl create(int [] ids)  throws XDevException {
    int myId = id() ;
    boolean amInGroup = false ;

    if(barrierCount == 0) {
    newSMPDeviceImpl = new SMPDeviceImpl() ;
    synchronized(SMPDeviceImpl.class) {
    newSMPDeviceImpl.context = nextContext++ ;
    }
    newSMPDeviceImpl.size = ids.length ;
    newSMPDeviceImpl.threads = new Thread [newSMPDeviceImpl.size] ;
    newSMPDeviceImpl.ids = new HashMap() ;
    boolean inGroup [] = new boolean [size] ;

    for(int i = 0 ; i < newSMPDeviceImpl.size ; i++) {
    int id = ids [i] ;
    if(id < 0 || id > size) {
    throw new XDevException("In SMPDeviceImpl.create(), value ids [" +
    i + "] is out of range (" + id + ")") ;
    }

    if(inGroup [id]) {
    throw new XDevException("In SMPDeviceImpl.create(), value " + id +
    "duplicated in ids array") ;
    }
    else
    inGroup [id] = true ;

    if(id == myId)
    amInGroup = true ;

    newSMPDeviceImpl.threads [i] = threads [id] ;
    newSMPDeviceImpl.ids.put(threads [id], new Integer(i)) ;
    }//end for ....

    newSMPDeviceImpl.barrierCount = 0 ;
    }//if barrierCount == 0 ends
    else {
    if(ids.length != newSMPDeviceImpl.size) {
    throw new XDevException("In SMPDeviceImpl.create(), threads " +
    "specify different values for ids array") ;
    }

    for(int i = 0 ; i < newSMPDeviceImpl.size ; i++) {
    int id = ids [i] ;
    if(id == myId)
    amInGroup = true ;
    if(id < 0 || id > size ||
    newSMPDeviceImpl.threads [i] != threads [id]) {
    throw new XDevException("In SMPDeviceImpl.create(), threads " +
    "specify different values for ids array") ;
    }
    }
    }//end else if barrierCount == 0

    barrierCount++ ;
    SMPDeviceImpl result = amInGroup ? newSMPDeviceImpl : null ;

    if(barrierCount == size) {// Reset barrier and wake up other threads.
    barrierCount = 0 ;
    notifyAll() ;
    }

    else {
    try {
    wait() ;
    }
    catch(InterruptedException e) {
    throw new XDevException("In SMPDeviceImpl.create(), unexpected " +
    "interuption during wait()??") ;
    }
    }

    return result ;
    }
     */
    /**
     * Destroy this communicator.    
    public void free() {}
     */
    /**
     * Blocking send ...
     * 
     * Equivalent to MPI_SEND
     */
  public void send(mpjbuf.Buffer buf, ProcessID destID, int tag, int context)
      throws Exception {
        SMPRequest req = (SMPRequest) isend(buf, destID, tag, context);

        if (mpi.MPI.DEBUG && SMPDevice.logger.isDebugEnabled()) {
          SMPDevice.logger.debug("After isend in -- calling iwait "
	                                      + req.hashCode());
	}
        req.iwait();
        if (mpi.MPI.DEBUG && SMPDevice.logger.isDebugEnabled()) {
          SMPDevice.logger.debug("After ---- calling iwait "+ req.hashCode());
        }
    }

    /**
     * Blocking receive of message, whose contents are copied to `buf'.
     * The capacity of `buf' must be large enough to accept these
     * contents.  Initializes the `source' and `tag' fields of the
     * returned `Status'.  Equivalent to MPI_RECV.
     */
    public mpjdev.Status recv(mpjbuf.Buffer buf, ProcessID srcID, int tag,
      int context) throws XDevException {
         mpjdev.Status status = new mpjdev.Status(srcID.uuid(), tag, -1);
        SMPRequest req = (SMPRequest) irecv(buf, srcID, tag, context, status);
        return req.iwait();
    }
    private RecvQueue recvQueue = new RecvQueue();
    private SendQueue sendQueue = new SendQueue();

    /**
     * Non-blocking version of `send'.
     * Equivalent to MPI_ISEND
     */
    public mpjdev.Request isend(mpjbuf.Buffer buf, ProcessID destID,
            int tag, int context) throws Exception {

//	out.println(id().uuid()+"-isend-<"+tag+">-<"+context+">-start");	
        // System.out.println("-isend-<"+tag+">-<"+context+">-start");

        /*these checks should be at upper levels ...
        if (buf == null) 
        throw new XDevException("In SMPDeviceImpl.isend(), buffer is null.");

        if(dest < 0) {
        throw new XDevException("In SMPDeviceImpl.isend(), requested negative " +
        "message destination: " + dest) ;
        } else if(dest >= size) {
        throw new XDevException("In SMPDeviceImpl.isend(), requested destination " +
        dest + " does not exist in communicator " +
        "of size " + size) ;
        }*/

        ProcessID myID = id();
//	out.println(" start of isend <"+myID+">=<"+id()+">");
        SMPSendRequest send = new SMPSendRequest(buf, context,
                destID, myID, tag);
        SMPRecvRequest matchingRecv = null;
      //  System.out.println(myID +" sending to "+destID+" context "+context+" tag "+ tag);
        //   System.out.println("In isend -- " + send.hashCode() + " Context " + context + " Tag " + tag + " srcID " + myID.uuid() + " destID " + destID.uuid());
        synchronized (SMPDeviceImpl.class) {

            matchingRecv = recvQueue.rem(send);
//	    out.println("Did we find the matching recv "+matchingRecv); 

            if (matchingRecv != null) {

//		out.println("matching recv found ...");		    

// copy data from `buf' to buffer in `matchingRecv'
// and initialize status field in `matchingRecv'.    
// matchingRecv.buf.copy(buf) ;
////////////////////////////////////////////////////////////////////
/////////////////// *message copying stuff* ///////////////////////
             //   System.out.println(" isend -- matching recv size "+  ((NIOBuffer)(matchingRecv.buffer.getStaticBuffer())).getCapacity() +" receiver "+destID+" context "+context);
             //   System.out.println(" isend -- buf size "+ buf.getSize()+" sender "+myID);
                matchingRecv.buffer.setSize(buf.getSize());
//out.println(id() + " setting the size in the next line to <"+ 
                //                matchingRecv.buffer.getSize() +">") ;
                if (buf.getDynamicBuffer() != null) {
                    matchingRecv.buffer.setDynamicBuffer(buf.getDynamicBuffer());
                }
                
        // ((NIOBuffer)
        // matchingRecv.buffer.getStaticBuffer()).getBuffer().limit(
        // buf.getSize() );
        
        // HYB how much I need to get + myOffset

        ((NIOBuffer) matchingRecv.buffer.getStaticBuffer()).getBuffer().limit(
            matchingRecv.buffer.getSize() + matchingRecv.buffer.offset()); 
        // ((NIOBuffer)
        // matchingRecv.buffer.getStaticBuffer()).getBuffer().position();
        ((NIOBuffer) matchingRecv.buffer.getStaticBuffer()).getBuffer()
            .position(SMPDevice.RECV_OVERHEAD); // HYB
        // ((NIOBuffer) buf.getStaticBuffer()).getBuffer().limit(buf.getSize());
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().limit(
            buf.getSize() + buf.offset());
        // ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(0);
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(
            SMPDevice.SEND_OVERHEAD); // HYB
        // System.out.println ("xdev-MRecv B$PUT: Src= limit:"+((NIOBuffer)
        // buf.getStaticBuffer()).getBuffer().limit()
        // +" Dst= Limit:"+((NIOBuffer)
        // matchingRecv.buffer.getStaticBuffer()).getBuffer().limit()
        // +"^^^^^^\n");
        ((NIOBuffer) matchingRecv.buffer.getStaticBuffer()).getBuffer().put(
                        ((NIOBuffer) buf.getStaticBuffer()).getBuffer());
                ((NIOBuffer) matchingRecv.buffer.getStaticBuffer()).getBuffer().flip();
                ((NIOBuffer) buf.getStaticBuffer()).getBuffer().clear();


                matchingRecv.status.srcID = myID.uuid();
                matchingRecv.status.tag = tag;
                matchingRecv.setPending(false);
                matchingRecv.status.numEls = send.numEls;
                 matchingRecv.status.type = send.type; //temp

                   matchingRecv.numEls = send.numEls; //temp
                 matchingRecv.type = send.type; 

                // Check if anybody is iwait-ing on `matchingRecv'.
                // If so, remove all requests from wait set, and signal 
                // the waiting thread.

                SMPRequest.WaitSet waiting = matchingRecv.getWaitSet();
                if (waiting != null) {
                    waiting.select(matchingRecv);
                }

                send.setPending(false);
            } else {
                //   System.out.println("No matching recv found                ...");
                send.setPending(true);
                sendQueue.add(send);
//	      out.println("added it to sendqueue "+sendQueue);
                
            }
        }
//System.out.println("Returning send from smpdev ");
//	out.println(" end of isend <"+myID+">=<"+id()+">");
//	out.println(id().uuid()+"-isend-<"+tag+">-<"+context+">-end");	
        return send;
    }





  /**
   * Non-Blocking overloaded probe method.
   * 
   * @param srcID
   * @param dstID
   * @param tag
   * @param context
   * @return mpjdev.Status
   **/  
  public mpjdev.Status iprobeAndFetch(ProcessID srcID, ProcessID dstID, int tag,
    int context, mpjbuf.Buffer buf) throws XDevException {
    
    SMPSendRequest request = sendQueue.check(context, dstID, srcID, tag);
    
    if (request != null) {
      
      synchronized (SMPDeviceImpl.class) {
//        System.out.println (" smpdev: complete Request, processing buffer ");
        mpjdev.Status status = new mpjdev.Status(id().uuid(), tag, -1);
        SMPRecvRequest recv = new SMPRecvRequest(buf, context, dstID, srcID, tag,
              status);
      
        SMPSendRequest matchingSend = sendQueue.rem(recv);
        buf.setSize(matchingSend.buffer.getSize());
        if (matchingSend.buffer.getDynamicBuffer() != null) {
          buf.setDynamicBuffer(matchingSend.buffer.getDynamicBuffer());
        }
        // ((NIOBuffer)
        // matchingSend.buffer.getStaticBuffer()).getBuffer().limit(buf.getSize());
        ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer().limit(
            matchingSend.buffer.getSize() + matchingSend.buffer.offset()); // HYB
        // ((NIOBuffer)
        // matchingSend.buffer.getStaticBuffer()).getBuffer().position(0);
        ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer()
            .position(SMPDevice.SEND_OVERHEAD); // HYB
        // ((NIOBuffer) buf.getStaticBuffer()).getBuffer().limit(buf.getSize()
        // );
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().limit(
            buf.getSize() + buf.offset()); // HYB
        // ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(0);
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(
            SMPDevice.RECV_OVERHEAD); // HYB

        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().put(
            ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer());
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().flip();
        
        ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer().clear();

        
        
        recv.status = status;
        // System.out.println("UUID" + matchingSend.sourceID.uuid());
        // System.out.println("Status -- tag" + status.tag);
        recv.status.srcID = matchingSend.srcID.uuid();
        recv.status.tag = matchingSend.tag;
        recv.type = matchingSend.type;
        recv.numEls = matchingSend.numEls;        
        request.srcID = matchingSend.srcID;
        status.srcID = matchingSend.srcID.uuid();

        // recv.status.numEls = matchingSend.numEls; //temp
        // recv.status.type = matchingSend.type; //temp

        // System.out.println(" In irecv -- status " + status.index + "  " +
        // recv.status.srcID + "  " + recv.status.tag );

        matchingSend.setPending(false);

        // Check if anybody is iwait-ing on `matchingSend'.
        // If so, remove all requests from wait set, and signal
        // the waiting thread.

        SMPRequest.WaitSet waiting = matchingSend.getWaitSet();
        if (waiting != null) {
          waiting.select(matchingSend);
        }
        recv.setPending(false);
        
        
        return status;
      }
    }
    //System.out.println (" smpdev: incomplete Request, releasing lock in Fetch ");
    return null;
  }
     /**
     * Non-blocking version of `recv'.* Equivalent to MPI_IRECV
     */
    public mpjdev.Request irecv(mpjbuf.Buffer buf, ProcessID srcID,
            int tag, int context, mpjdev.Status status)
            throws XDevException {

//	out.println(id().uuid()+"-irecv-<"+tag+">-<"+context+">-start");	
//	System.out.println("irecv-<"+tag+">-<"+context+">-start");	
/*
        if (buf == null) 
        throw new XDevException("In SMPDeviceImpl.irecv(), buffer is null.");

        if(src < -2) {    // -1, -2 are ANY_TAG, ANY_SOURCE

        throw new XDevException("In SMPDeviceImpl.irecv(), requested negative " +
        "message source: " + src) ;
        } else if(src >= size) {
        throw new XDevException("In SMPDeviceImpl.irecv(), requested source " +
        src + " does not exist in communicator " +
        "of size " + size) ;
        }
         */
//System.out.println("Inside irecv in smpdev ");
        ProcessID myID = id();
        //out.println(" start of irecv <"+myID+">=<"+id()+">");
        SMPRecvRequest recv = new SMPRecvRequest(buf, context, myID,
                srcID, tag, status);
        //out.println(" recv_request <"+recv+">=<"+id()+">");
// recv.status =status;
  //      System.out.println(myID +" receiving from "+srcID+" context "+context+" tag "+ tag);
        synchronized (SMPDeviceImpl.class) {

            SMPSendRequest matchingSend = sendQueue.rem(recv);
//matchingSend.status = status;
            if (matchingSend != null) {

                //out.println(" got matching send req. <"+matchingSend+">=<"
                //		    +id()+">");
// Copy data from buffer in `matchingSend' to `buf'
// and initialize status field in `recv'.    
//                buf.copy(matchingSend.buf) ;
// copying is going to be a little complicated than this ...
/////////////////// *message copying stuff* ///////////////////////
//out.println(id() + " setting the size in the next line to <"+ 
                //                                       matchingSend.buffer.getSize() +">") ;
                buf.setSize(matchingSend.buffer.getSize());
                if (matchingSend.buffer.getDynamicBuffer() != null) {
                    buf.setDynamicBuffer(matchingSend.buffer.getDynamicBuffer());
                }
        ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer().limit(
            matchingSend.buffer.getSize() + matchingSend.buffer.offset()); // HYB
        // ((NIOBuffer)
        // matchingSend.buffer.getStaticBuffer()).getBuffer().position(0);
        ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer()
            .position(SMPDevice.SEND_OVERHEAD); // HYB
        // ((NIOBuffer) buf.getStaticBuffer()).getBuffer().limit(buf.getSize()
        // );
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().limit(
            buf.getSize() + buf.offset()); // HYB
        // ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(0);
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(
            SMPDevice.RECV_OVERHEAD); // HYB
        // System.out.println ("xdev-MSend B$PUT: DST= limit:"+((NIOBuffer)
        // buf.getStaticBuffer()).getBuffer().limit()
        // +" SRC= Limit:"+((NIOBuffer)
        
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().put(
            ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer());
        ((NIOBuffer) buf.getStaticBuffer()).getBuffer().flip();

        /*
         * if (commDone) { ((NIOBuffer)
         * matchingSend.buffer.getStaticBuffer()).getBuffer().flip();
         * ((NIOBuffer)
         * matchingSend.buffer.getStaticBuffer()).getBuffer().position(4); try{
         * int [] test = new int [6]; matchingSend.buffer.read(test, 0, 5);
         * for(int i=0;i<test.length;i++) {
         * System.out.print(" MS "+test[i]+" "); } }catch (BufferException e){
         * System.out.print("MS buffer read exception " ); } }
         */

        ((NIOBuffer) matchingSend.buffer.getStaticBuffer()).getBuffer().clear();


                recv.status =status;
               // System.out.println("UUID" + matchingSend.sourceID.uuid());
               // System.out.println("Status -- tag" + status.tag);
                recv.status.srcID = matchingSend.srcID.uuid();
                recv.status.tag = matchingSend.tag;
                recv.type = matchingSend.type;
                recv.numEls = matchingSend.numEls;
               // recv.status.numEls = matchingSend.numEls;  //temp
                //recv.status.type = matchingSend.type;  //temp


              //  System.out.println(" In irecv -- status " + status.index + "  " + recv.status.srcID + "  " + recv.status.tag );

                matchingSend.setPending(false);

                // Check if anybody is iwait-ing on `matchingSend'.
                // If so, remove all requests from wait set, and signal 
                // the waiting thread.

                SMPRequest.WaitSet waiting = matchingSend.getWaitSet();
                if (waiting != null) {
                    waiting.select(matchingSend);
                }

                recv.setPending(false);
            } else {
                //  System.out.println(" aint any matching send thing ");
                
//	        out.println(" added it to recvQue ... "+recvQueue);		
                recv.setPending(true);
                recvQueue.add(recv);
            }
        }
//System.out.println("Returning recv from smpdev ");
//	out.println(" end of irecv <"+myID+">=<"+id()+">");
//	out.println(id().uuid()+"-irecv-<"+tag+">-<"+context+">-end");	
        return recv;
    }


    public mpjdev.Status iprobe(ProcessID srcID, int tag,
            int context) throws XDevException {

        mpjdev.Status status = null;
         ProcessID myID = id();
        SMPSendRequest request = sendQueue.check(context, myID, srcID, tag);
//System.out.print(" request null? "+request);
        if (request != null) {
            //now this is a tricky one ...
          //  System.out.print(" mpjdev request numEls = "+request.numEls);
            status = new mpjdev.Status(request.srcID.uuid(),
                   request.tag, -1, request.type,
                    request.numEls); //jd
                    
        }

         return status;
       // return null;
    }




    /**
     * Initialize MPJ, and register current thread as a node in the new
     * MPJ world.  This assumes every thread knows in advance the total
     * number of threads, and also knows its own unique rank in the set of
     * threads.
     * <p>
     * <table>
     * <tr><td><tt> nprocs </tt></td><td> number of nodes in the MPJ world.</tr>
     * <tr><td><tt> myId </tt></td><td> rank the current thread should have
     *                                  in that world.</tr>
     * </table>
     * <p>
     */
    public synchronized static ProcessID[] init(String file, int rank)
            throws Exception {
        /* putting the debug initialization code here */
        //  String LOG_FILE = new String("../logs/"+rank+".log");
        out = System.out;

        //out.println("number of registered threads are "+numRegisteredThreads);
/*	FileOutputStream fos = null; 
        try {
        fos = new FileOutputStream(LOG_FILE);
        } catch (Exception fnfe) {
        throw new Exception("FileNotFoundException, LOG_FILE=" +
        LOG_FILE +
        " and the message is " + fnfe.getMessage());
        }

        out = new PrintStream(fos);
         */
        /* putting the debug initialization code here */

       
        //System.out.println("  -- File "+file);
        int nprocs = 0, psl = 0;
/*
        try {
             String mpjHome = System.getenv("MPJ_HOME");
            //ConfigReader reader = new ConfigReader(file);
         	ConfigReader reader = new ConfigReader(mpjHome+"/conf/mpjdev.conf");

            nprocs = (new Integer(reader.readNoOfProc())).intValue();
           //System.out.println(" nprocs "+nprocs);
            psl = (new Integer(reader.readIntAsString())).intValue();
            reader.close();
        } catch (Exception eex) {
            eex.printStackTrace();
        }
*/
        nprocs = Integer.parseInt(file); //FIXME: this variable needs to be
                                         //       renamed ... 
        /*      out.println("rank<"+rank+">,nprocs<"+nprocs+">");
        out.println("SMPDeviceImpl  "+Thread.currentThread() +"time"+
        System.currentTimeMillis() +"numRegistered"+
        numRegisteredThreads );
         */
        if (initialized) {
            throw new XDevException("Call to SMPDeviceImpl.init() after MPJ has " +
                    "already been successfully initialized");
        }

        if (nprocs < 0) {
            throw new XDevException("In SMPDeviceImpl.init(), requested negative " +
                    "world size " + nprocs);
        } else if (numRegisteredThreads == 0) {
            
            WORLD.size = nprocs;

            WORLD.pids = new ProcessID[WORLD.size];
            WORLD.threads = new Thread[WORLD.size];
            WORLD.ids = new HashMap();

            WORLD.context = 0;
            nextContext = 1;

            WORLD.barrierCount = 0;

        } else if (nprocs != WORLD.size) {
            throw new XDevException("In SMPDeviceImpl.init(), mismatch in number of " +
                    "nodes requested by threads: " +
                    WORLD.size + " vs " + nprocs);
        }

        /*
        if(myId < 0) {
        throw new XDevException("In SMPDeviceImpl.init(), requested negative " +
        "node id, " + myId) ;
        }
        else if(myId >= WORLD.size) {
        throw new XDevException("In SMPDeviceImpl.init(), requested node id, " +
        myId + " is too large for requested " +
        "world size, " + WORLD.size) ;
        }
         */
        Thread thread = Thread.currentThread();

        if (WORLD.threads[rank] != null) {

//	System.out.println("Current thread caused exception is "+Thread.currentThread());	

            throw new XDevException("In SMPDeviceImpl.init(), requested node id, " +
                    rank + " has already been registered");
        } else {


            UUID myuuid = UUID.randomUUID();
            //WORLD.id = new ProcessID(myuuid, rank);
            //FIXME: is there any effect of not having `rank' 
            WORLD.id = new ProcessID(myuuid);
            WORLD.pids[rank] = WORLD.id;
	    smpProcess = (SMPDevProcess)thread.getThreadGroup();
	    smpProcess.setProcessID(WORLD.id);

            WORLD.threads[rank] = thread;
            //WORLD.ids.put(thread, new Integer(myId)) ;
            //System.out.println("ids.put(thread) has thread value = "+thread.getId()+" and thread.getContextClassLoader() = "+thread.getContextClassLoader().toString());
            //WORLD.ids.put(thread.getContextClassLoader(), WORLD.id);


            /*      out.println("SMPDeviceImpl  "+Thread.currentThread() +"time"+
            System.currentTimeMillis() +"numRegistered"+
            numRegisteredThreads);
             */

            numRegisteredThreads++; 


            /*          out.println("SMPDeviceImpl  "+Thread.currentThread() +"time"+
            System.currentTimeMillis() +"numRegistered"+
            numRegisteredThreads);
            out.println("WORLD.size "+WORLD.size);
             */
            if (numRegisteredThreads == WORLD.size) {

                initialized = true;
                //  System.out.println(" || notifying wait <"+rank+">");
                //              out.println("notifying wait <"+rank+">");
                SMPDeviceImpl.class.notifyAll();

            } else {
                try {

                    //                out.println("calling wait (init) <"+rank+">");
                    SMPDeviceImpl.class.wait();
                //	    out.println("called wait  (init) <"+rank+">");
                //  System.out.println(" || called wait (init) <"+rank+">");
                } catch (InterruptedException e) {
                    throw new XDevException("In SMPDeviceImpl.init(), unexpected " +
                            "interuption during wait()??");
                }
            } //end else 
        } //end else
//	out.println("Last call to SMPDeviceImpl.init() <"+rank+">");
        return WORLD.pids;
    //TTD:- it may throw a null pointer exception ..
    }//end init() 

    /**
     * Finalize MPI.
     * <p>
     * Java binding of the MPI operation <tt>MPI_FINALIZE</tt>.
     */
    public synchronized static void finish() throws XDevException {
        //      out.println("Finish method Called");
//        System.out.println("finished called");
        // Need to clean up properly, so applets can be restarted.

        // Check current thread belongs to this communicator

        //if (WORLD.ids.get(Thread.currentThread()) == null) {
	if(!(Thread.currentThread().getThreadGroup()
                                       instanceof SMPDevProcess)) {
          //if (WORLD.ids.get(Thread.currentThread().getContextClassLoader()) 
            //                                                      == null) {
          throw new XDevException("SMPDeviceImpl.finish() invoked by thread " +
                    "outside MPJ world");
        }

        numRegisteredThreads--;

        if (numRegisteredThreads == 0) {

            // Reset MPJ initialized status and wake up other threads.

            initialized = false;

            SMPDeviceImpl.class.notifyAll();
        } else {

            try {
                SMPDeviceImpl.class.wait();
            } catch (InterruptedException e) {
                throw new XDevException("In SMPDeviceImpl.finish(), unexpected " +
                        "interuption during wait()??");
            }
        }
    }
    /**
     * Equivalent of MPI_ANY_SOURCE.
     * May be passed as `src' argument of `recv' or `irecv'.
     */
    public static final int ANY_SOURCE = -2;
    //OK this null is throwing exception 
    public static final ProcessID ANY_SRC =
            //new ProcessID(UUID.randomUUID(), -2);
            new ProcessID(UUID.randomUUID());
    /**
     * Equivalent of MPI_ANY_TAG.
     * May be passed as `tag' argument of `recv' or `irecv'.
     */
    public static final int ANY_TAG = -1;
    /**
     * The initial communicator.
     * Equivalent of MPI_COMM_WORLD.
     */
    public static final SMPDeviceImpl WORLD = new SMPDeviceImpl();
    static int MAX_PROCESSOR_NAME = 256;
    long handle;

    //private static native void init();  
    static class RecvQueue {

        /**
         *  Add a `SMPRecvRequest' to the front of the queue associated with
         *  its key.
         */
        public void add(SMPRecvRequest recv) {
            
            add(recv.key, recv);
        }

        /**
         *  Remove from its queue the next `SMPRecvRequest'
         *  that matches one of the keys in `send'.
         */
        public SMPRecvRequest rem(SMPSendRequest send) {

            SMPRecvRequest matchingRecv = null;

            Key[] keys = send.keys;

            long minSequenceNum = Long.MAX_VALUE;
            for (int i = 0; i < keys.length; i++) {
                SMPRecvRequest recv = get(keys[i]);
                if (recv != null && recv.sequenceNum < minSequenceNum) {
                    minSequenceNum = recv.sequenceNum;
                    matchingRecv = recv;
                }
             //  System.out.println("Remove keys -- " + send.hashCode());
             //  keys[i].tostring();

            }

            if (matchingRecv != null) {
                rem(matchingRecv.key, matchingRecv);
            }

            return matchingRecv;
        }

        private SMPRecvRequest get(Key key) {

            return (SMPRecvRequest) map.get(key);
        }

        private void add(Key key, SMPRecvRequest recv) {

            SMPRecvRequest head = (SMPRecvRequest) map.get(key);

            if (head == null) {
                recv.next = recv;
                recv.prev = recv;

                map.put(key, recv);
            } else {
                SMPRecvRequest last = head.prev;

                last.next = recv;
                head.prev = recv;

                recv.prev = last;
                recv.next = head;
            }
        }

        private void rem(Key key, SMPRecvRequest recv) {

            SMPRecvRequest head = (SMPRecvRequest) map.get(key);

            if (recv == head) {
                if (recv.next == recv) {

                    // Unique entry.

                    map.remove(key);
                } else {
                    SMPRecvRequest next = recv.next;
                    SMPRecvRequest last = recv.prev;

                    last.next = next;
                    next.prev = last;

                    map.put(key, next);
                }
            } else {
                SMPRecvRequest next = recv.next;
                SMPRecvRequest prev = recv.prev;

                prev.next = next;
                next.prev = prev;
            }
        }

  
 
        private HashMap map = new HashMap();
    }

    static class SendQueue {

        /**
         *  Add a `SendRequest' to the front of the queues associated with
         *  its keys.
         */
        public void add(SMPSendRequest send) {
            
            Key[] keys = send.keys;
            
            for (int i = 0; i < keys.length; i++) {
                add(i, keys[i], send);

//keys[i].tostring();
            }
        }

        private void add(int i, Key key, SMPSendRequest send) {
            SMPSendRequest head = (SMPSendRequest) map.get(key);
            //  SMPDeviceImpl.out.println("got the sort of head <"+head+">");
            if (head == null) {
                //     SMPDeviceImpl.out.println("creating the first element in the queues");
                //SMPDeviceImpl.out.println("..");
//		SMPDeviceImpl.out.println("map "+map);
//		SMPDeviceImpl.out.println(".... keys ....");
//		SMPDeviceImpl.out.println("send "+send);
                send.next[i] = send;
                send.prev[i] = send;
                map.put(key, send);
            } else {
                //     SMPDeviceImpl.out.println("managing ds coz head is still there ...");
                SMPSendRequest last = head.prev[i];
                last.next[i] = send;
                head.prev[i] = send;
                send.prev[i] = last;
                send.next[i] = head;
            }
        }

        /**
         *  Remove from its queues the next `SMPSendRequest' that matches the
         *  key in `recv'.
         */
        public SMPSendRequest rem(SMPRecvRequest recv) {
//            SMPDeviceImpl.out.println("rem method ...");
//            SMPDeviceImpl.out.println(".... keys ...."); 
//	    SMPDeviceImpl.out.println("map "+map);	    
            //          SMPDeviceImpl.out.println(".... keys ....");
            //    SMPDeviceImpl.out.println("recv "+recv);
            //  SMPDeviceImpl.out.println("recv.key "+recv.key);
            SMPSendRequest matchingSend = get(recv.key);
//            SMPDeviceImpl.out.println("matchingSend "+matchingSend);
            if (matchingSend != null) {
                Key[] keys = matchingSend.keys;
                for (int i = 0; i < keys.length; i++) {
                    rem(i, keys[i], matchingSend);
                }
            }
            return matchingSend;
        }

        private SMPSendRequest get(Key key) {
            return (SMPSendRequest) map.get(key);
        }

        private void rem(int i, Key key, SMPSendRequest send) {
            SMPSendRequest head = (SMPSendRequest) map.get(key);
            if (send == head) {
                if (send.next[i] == send) {
                    // Unique entry.    
                    map.remove(key);
                } else {
                    SMPSendRequest next = send.next[i];
                    SMPSendRequest last = send.prev[i];
                    last.next[i] = next;
                    next.prev[i] = last;
                    map.put(key, next);
                }
            } else {
                SMPSendRequest next = send.next[i];
                SMPSendRequest prev = send.prev[i];
                prev.next[i] = next;
                next.prev[i] = prev;
            }
        }

            SMPSendRequest check(int context, ProcessID destID, ProcessID srcID, int tag) {
            Key key = new Key(context, destID, srcID, tag);
          
            return (SMPSendRequest)map.get(key);

        }


        private HashMap map = new HashMap();
    }

    static class Key {

        private int context,  tag;
        private ProcessID destID,  srcID;

        Key(int context, ProcessID destID, ProcessID srcID, int tag) {

            this.context = context;
            this.destID = destID;
            this.srcID = srcID;
            this.tag = tag;
        }
/*
         Key(int context, ProcessID srcID, int tag) {

            this.context = context;
            this.srcID = srcID;
            this.tag = tag;
        }
*/
        // Typically the fields (except possibly `tag') will be small integers.
        // There's no particular rationale to this formula, but maybe it
        // gives a reasonable scatter...
        public int hashCode() {
            return -1;
        //return tag + context * 5 + dest * 11 + source * 17 ;

        }

        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key other = (Key) obj;

//System.out.println(" other.destID.uuid() "+other.destID.uuid());
//System.out.println(" destID.uuid() "+destID.uuid());
//System.out.println(" other.srcID.uuid() "+other.srcID.uuid());
//System.out.println(" srcID.uuid() "+srcID.uuid());

                return (other.context == context) &&
                        (other.destID.uuid().equals(destID.uuid())) &&
                        (other.srcID.uuid().equals(srcID.uuid())) &&
                        (other.tag == tag);
            }
            return false;
        }

        public void tostring() {
            System.out.println("Context " + context + " Tag " + tag + " srcID " + srcID.uuid());
        }
    }
}


