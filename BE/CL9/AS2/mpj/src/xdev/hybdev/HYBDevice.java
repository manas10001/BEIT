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

import java.lang.*;
import java.net.*;
import java.util.*;
import mpjbuf.*;
import mpjdev.*;
import xdev.*;
import xdev.smpdev.*;

import org.apache.log4j.Logger;



public class HYBDevice implements Device {

  
  class WildcardMessageManager implements Runnable {
  
    HYBRecvRequest recvRequest  ;
    
    WildcardMessageManager (HYBRecvRequest recvReq) {
      this.recvRequest = recvReq ;      
    }
    public void run () {
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug(" Host:"+localHostName+" WildcardMessageManager thread is starting ");
      }
      String  localHostName=null;
      mpjdev.Status sts = null;
      try {
        InetAddress localaddr = InetAddress.getLocalHost();
         localHostName= localaddr.getHostName();
      }
      catch (UnknownHostException unkhe) {
        throw new XDevException(unkhe);
      }
      
      ProcessID srcID = new ProcessID ( recvRequest.srcUUID) ;
      ProcessID dstID = new ProcessID ( recvRequest.dstUUID) ;
      
      while (true){
      
        sts = nioHybDev.iprobeAndFetch(srcID, dstID, recvRequest.tag,
                        recvRequest.context, recvRequest.buffer ) ;
      
        if (sts!=null){
          recvRequest.srcUUID = sts.srcID;
          recvRequest.tag = sts.tag;
          recvRequest.numEls = sts.numEls;
          recvRequest.type = sts.type;
          
        
          recvRequest.setCompleted(true);
          
          recvRequest.notifyMe(); 
          
          if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(" Host:"+localHostName+" ANY_SRC message Recvd from network");
          }
          
          break;
        }
       // System.out.println (" hybdev: incomplete Request in niodev, calling smpdev ");
        sts = smpHybDev.iprobeAndFetch(srcID, dstID, recvRequest.tag,
                        recvRequest.context, recvRequest.buffer ) ;
      
        if (sts!=null){
          recvRequest.srcUUID = sts.srcID;
          recvRequest.tag = sts.tag;
          recvRequest.numEls = sts.numEls;
          recvRequest.type = sts.type;
          if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(" Host:"+localHostName+" ANY_SRC Recvd from smpdev ");
          }
        
          recvRequest.setCompleted(true);
          
          recvRequest.notifyMe();      
          
          break;
        }
        try{
          Thread.currentThread().sleep(2000);
        }catch (Exception e){
          e.printStackTrace();
        }
        
      }
      
    }
  }
 

    // SMP processes in the currenet node
    static int smpProcs = -1;
    // overall processes in cluster i.e. np
    static int nProcs = -1;
    // NIO processes in the cluster
    static int nioProcs = -1;
    // NIO Rank of process
    static int netID = -1;
//  private static boolean isOnceInitialized = false;
  
  private static int SMP_INITIALIZED_COUNT = 0;
  //private static long packUuids[] = null;
  private static ProcessID[] allPids = null;  
  public static boolean allPidsInitialized = false;
  // Hashtable of uuid to NetID mapping
  private static Hashtable<UUID, Integer> uuidToNetID = null;
  // used for finish
  private static int numRegisteredThreads ; 
  private static Object lock = new Object() ;
  
  // nioHybDev instance
  public static Device nioHybDev = null;  
  private static boolean nioInitialized = false;
  private static ProcessID[] Npids = null;
  
  // smpHybDev instance
  private Device smpHybDev = null;
  private int SMPID=-1; // SMPID is Rank of SMP Thread
  

  //NIO device overhead is 49, it is used as starting point to write
   // Hybrid Device related information in the buffer.
  static final int NET_SEND_OVERHEAD = 49;
  static int SEND_OVERHEAD = 81;
  
  // for Hybrid Dev Recv overhead
  static final int RECV_OVERHEAD = 0;
  
  // sending two UUID (sender & receiver) in Long values
  static final int HYB_SEND_OVERHEAD = 32;
  
  static Logger logger = Logger.getLogger("mpj");
  static String localHostName = null;
  private final int longLength = 8 ; 

  /**
   * Initializes hybdev.
   * 
   * @param args
   *          Arguments to HYBDevice.
   * @return ProcessID[] An array of ProcessIDs.
   */
  public ProcessID[] init(String args[]) throws XDevException {
  
    
    /*
     * The init method does the following tasks
     * 1. Initializes an NIO device Instance that is shared among SMP processes
     * 2. Initializes SMP device Instance
     * 3. development of Global Process ID array that will be returned to MPJDev.
     *   a. Process netID 0 & SMPID 0 acts as global root and SMPID 0 acts
     *      as local root at each node.
     *   b. Global root receives Local SMP ProcessID table from roots at all 
     *      nodes and merge them to make a global process table.
     *   c. Global process table is then communicated to local roots at all nodes.
     */
    
    /*
     * Args usage is
     *  Args[0] = SMPID
     *  Args[1] = SMP Threads on current node 
     *  Args[2] = hybdev
     *  Args[3] = total process for job, np 
     *  Args[4] = Total NIO process to be used
     *  Args[5] = NIO Rank of Host
     *  Args[6] = NIO Config file path
     *  Args[7] = niodev
     * */
    
    xdev.niodev.NIODevice.isHybrid = true;
    smpHybDev=nioHybDev = null ;     //just to make sure device is clean if repeat init.
    ProcessID[] Spids = null;
    String SMPArgs[] = new String [3] ;
    
    SMPID = Integer.parseInt(args[0]); // It contains rank of SMP thread
    
    try {
      localHostName = InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
      e.printStackTrace() ;
    }
    
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.info("--init method of hybdev is called--");
      logger.info("Name :" + localHostName);
      logger.info("net rank :" + netID);
      logger.info("smp rank :" + SMPID);
    }
      
    synchronized (lock) {
      
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.init(), acquired Init lock, my rank "+myGid() ) ;
      }
      
      numRegisteredThreads++;
      
      if (numRegisteredThreads == Integer.parseInt(args[1]) ) {
        
        if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug("HYB.Init(), Preparing data for Init() " );
        }
        
        // total SMP thread on current host
        smpProcs = Integer.parseInt(args[1]); 
        // overall processes in cluster i.e. np
        nProcs = Integer.parseInt(args[3]); 
        // NIO processes in the cluster
        nioProcs = Integer.parseInt(args[4]);
        // NIO Rank of process
        netID = Integer.parseInt(args[5]);
        
        allPids = new ProcessID[nProcs];
        uuidToNetID = new Hashtable<UUID, Integer>();
        
        /* setting SMP overheads according to the overhead used for Network */
        xdev.smpdev.SMPDevice.SEND_OVERHEAD = SEND_OVERHEAD;
        xdev.smpdev.SMPDevice.RECV_OVERHEAD = RECV_OVERHEAD;
        
        
        String niostr [] = new String[3];
        for (int i = 0; i < niostr.length; i++) {
          niostr[i] = args[5 + i];
        }
        
        nioHybDev = new xdev.niodev.NIODevice();
        Npids = nioHybDev.init(niostr);
        
        numRegisteredThreads = 0 ;
        
        lock.notifyAll();
      
      } else {
        try {
          if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(" Host:"+localHostName+" SMPID "+SMPID+" putting itself to wait for NIO Init ");
          }
          lock.wait();
        } catch (InterruptedException e) {
          throw new XDevException("In HYBDevice.Init(), unexpected "
            + "interuption during wait()??");
        } catch (IllegalMonitorStateException ex) {
          ex.printStackTrace();
          throw new XDevException(
            "HYBDevice.init(), IllegalMonitorStateException during wait()");
        }
      }
    }
    
    long splittedUuids[] = new long [nProcs * 2];
    
    for (int i = 0; i < 3; i++) {
          SMPArgs[i] = args[i];
    }
    
    smpHybDev = new xdev.smpdev.SMPDevice();
    Spids = smpHybDev.init(SMPArgs);
    
     /* root Receiving in loop from root processes of each node */
    try {
      if (netID == 0 && SMPID == 0) {
        int procPerHost[] = getProcPerHost();
        int offset[] = new int[nioProcs];
        
        offset[0] = 0;
        for (int i = 1; i < nioProcs; i++) {
          offset[i] = offset[i - 1] + (procPerHost[i - 1] * 2);

        }
        
        
        ProcessID temppid = null;
        UUID temp = null;
        int n = 0;
        for (int i = 0; i < Spids.length; i++) {
          temp = Spids[i].uuid();
          splittedUuids[n] = temp.getMostSignificantBits();
          n++;
          splittedUuids[n] = temp.getLeastSignificantBits();
          n++;
        }

        mpjdev.Status status = new mpjdev.Status();
        Type sectionHeader = null;
        // Receiving in loop
        for (int i = 1; i < nioProcs; i++) {
          int bufSize = -1;
          // multiplying by 2 because each UUID consists of 2 long numbers
          int recvOverhead = nioHybDev.getRecvOverhead();
          int sectionSize = 0;
          bufSize = procPerHost[i] * 2 * longLength; 
          RawBuffer rawBuffer = BufferFactory.create(bufSize);
          mpjbuf.Buffer recvBuf = new mpjbuf.Buffer(rawBuffer, recvOverhead,
              recvOverhead + bufSize);
              
          //updateBuffer(buf, id(), dstID, NET_SEND_OVERHEAD, SEND_OVERHEAD);
          updateBuffer(recvBuf, Npids[i], Npids[0], 0, HYB_SEND_OVERHEAD);          
          try {
            recvBuf.commit();
          } catch (Exception e) {
            e.printStackTrace();
          }
          //setting a random tag & context to send & recv a message 
          status = nioHybDev.recv(recvBuf, Npids[i], 10, 100);

          recvBuf.commit();
          sectionHeader = recvBuf.getSectionHeader();
          sectionSize = recvBuf.getSectionSize();

          recvBuf.read(splittedUuids, offset[i], procPerHost[i] * 2);
          recvBuf.clear();
          BufferFactory.destroy(rawBuffer);
        }

        n = 0;
        
        /*
         * Global root received process tables from each node level root and 
         * then making a global ProcessID array.
         */
        for (int i = 0; i < splittedUuids.length; i += 2) {
          temp = new UUID(splittedUuids[i], splittedUuids[i + 1]);
          allPids[n] = new ProcessID(temp);
          n++;
        }

        /* Sending global ProcessID array to all node level root processes */
        
        int bcastBufSize = (splittedUuids.length) * longLength + SEND_OVERHEAD;
        RawBuffer bcastSendRawBuffer = BufferFactory.create(bcastBufSize);
        mpjbuf.Buffer bcastSendBuffer = new mpjbuf.Buffer(bcastSendRawBuffer,
            SEND_OVERHEAD, bcastBufSize);
        
        for (int i = 1; i < nioProcs; i++) {
          bcastSendBuffer.putSectionHeader(Type.LONG);
          bcastSendBuffer.write(splittedUuids, 0, splittedUuids.length);
          bcastSendBuffer.commit();
          
          updateBuffer(bcastSendBuffer, Npids[0], Npids[i], NET_SEND_OVERHEAD, SEND_OVERHEAD);
          
          nioHybDev.send(bcastSendBuffer, Npids[i], 100, 1000);
          
          bcastSendBuffer.clear();
        }
        BufferFactory.destroy(bcastSendRawBuffer);
       
      }
      /*
       * 1. sending current hostuuids to global root. 
       * Then receiving complete uuid list form root
       */
      else if (netID != 0 && SMPID == 0) {
        int sendBufSize = (smpProcs * 2 * longLength) + SEND_OVERHEAD;
        
        RawBuffer sendRawBuffer = BufferFactory.create(sendBufSize);

        mpjbuf.Buffer sendBufByAll = new mpjbuf.Buffer(sendRawBuffer,
            SEND_OVERHEAD, sendBufSize);

        sendBufByAll.putSectionHeader(Type.LONG);

        long packUuids [] = new long[smpProcs * 2];

        long msb, lsb;
        UUID myuuid;
        int n = 0;
        // to pack uuids of smpids into long array
        for (int i = 0; i < smpProcs; i++) {
          myuuid = Spids[i].uuid();
          msb = myuuid.getMostSignificantBits();
          lsb = myuuid.getLeastSignificantBits();
          packUuids[n] = msb;
          n++;
          packUuids[n] = lsb;
          n++;
        }

        sendBufByAll.write(packUuids, 0, packUuids.length);

        sendBufByAll.commit();
        
        updateBuffer(sendBufByAll, Npids[netID], Npids[0], NET_SEND_OVERHEAD, SEND_OVERHEAD) ;
        //tag and context are decided at random
        nioHybDev.send(sendBufByAll, Npids[0], 10, 100); // sending to root

        BufferFactory.destroy(sendRawBuffer);

        // Receiving from root
        mpjdev.Status status = null;
        Type sectionHeader = null;

        int bufSizeFromRoot = (splittedUuids.length * longLength)+RECV_OVERHEAD;

        RawBuffer rawBufferFromRoot = BufferFactory.create(bufSizeFromRoot);

        mpjbuf.Buffer recvBufFromRoot = new mpjbuf.Buffer(rawBufferFromRoot,
            RECV_OVERHEAD, bufSizeFromRoot);
        
        updateBuffer(recvBufFromRoot, Npids[0], Npids[netID], 0, HYB_SEND_OVERHEAD );
        status = nioHybDev.recv(recvBufFromRoot, Npids[0], 100, 1000);

        recvBufFromRoot.commit();
        sectionHeader = recvBufFromRoot.getSectionHeader();
        
        recvBufFromRoot.read(splittedUuids, 0, splittedUuids.length);
        recvBufFromRoot.clear();

        BufferFactory.destroy(rawBufferFromRoot);

        if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" HYB.init(), Receieved all from root by rank"+netID );
        }

        n = 0;
        UUID temp = null;

        for (int i = 0; i < splittedUuids.length; i += 2) {
          temp = new UUID(splittedUuids[i], splittedUuids[i + 1]);
          allPids[n] = new ProcessID(temp);
          // System.out.println (" Rank "+n+" : "+allPids[n].uuid() ) ;
          n++;
        }

        if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug(" HYB.init(), all hosts have complete pids");
        }
        // System.out.println (" all hosts have complete pids");

      }
    } catch (BufferException e) {
      e.printStackTrace();
    }

    // fill hosttable on each node
    if (SMPID == 0) {
      fillUuidToNetID();
      allPidsInitialized = true;
    }

    // make hashtable of (uuid, netID)
    int procPerHost[] = getProcPerHost();

    synchronized (uuidToNetID) {
      SMP_INITIALIZED_COUNT++;
      if (SMP_INITIALIZED_COUNT == smpProcs && allPidsInitialized == true) {
        uuidToNetID.notifyAll();
      } else {
        try {
          uuidToNetID.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
          throw new XDevException(
              "HYBDevice.init(), unexpected interruption during wait()");
        } catch (IllegalMonitorStateException ex) {
          ex.printStackTrace();
          throw new XDevException(
              "HYBDevice.init(), IllegalMonitorStateException during wait()");
        }
      }
    }
   
    return allPids; // Global Pids

  }
  
  public void cleanUp(){
    uuidToNetID=null;
    allPids=Npids=null;
    nioProcs=nProcs=netID=smpProcs=-1;
    allPidsInitialized = nioInitialized = false;
    SMP_INITIALIZED_COUNT= numRegisteredThreads =0;
    
  }
  /*
   * Fill HashTable for UUID to NetID transation. Hashtable returns netID of a
   * given UUID.
   */   
  public void fillUuidToNetID() {
    int processesPerHost[] = getProcPerHost();
    int n = 0;
    for (int i = 0; i < nioProcs; i++) {
      for (int j = 0; j < processesPerHost[i]; j++) {
        uuidToNetID.put(allPids[n].uuid(), i);
        //System.out.println( localHostName+ " UUID "+allPids[n].uuid()+" NetID "+i) ;
        n++;
      }
    }
  }

  /* it returns the Global Rank of calling process */
  public int myGid() {
    // get GID starting rank for each node then add SMPID id (local smp rank)
    int gid = SMPID + startRank();
    return gid;
  }

  // get starting of Global rank of the SMP prcesses running at current host
  public int startRank() {
    int strRank = -1;
    int perHost = nProcs / nioProcs;
    if (netID < (nProcs % nioProcs)) {
      perHost++;
      strRank = netID * perHost;
    } else {
      strRank = (netID * perHost) + (nProcs % nioProcs);
    }
    return strRank;
  }

  /*
   * This methods gives the number of processes running the each host of the
   * cluster, It is replica implementation of runtime where it is decided that
   * how many processes should be launchged at one host
   */
  public int[] getProcPerHost() {
    int counts[] = new int[nioProcs];

    for (int i = 0; i < nioProcs; i++) {
      counts[i] = nProcs / nioProcs;
    }
    int rem = nProcs % nioProcs;
    for (int j = 0; j < rem; j++) {
      counts[j] = counts[j] + 1;
    }
    return counts;
  }
  

  
  /* It returns true if both source and destination are on same node
   * and returns false if both source and destination are not on same node
  */
  public static boolean isLocal(ProcessID id) {
    boolean local = false;
    //System.out.println(" islocal.uuid "+id.uuid() );
    int myNetId = uuidToNetID.get(id.uuid());
    if (myNetId == netID) {
      local = true;
    }
    return local;
  }

  /*
   * It is used to get network ProcessID of any SMP process
  */
  public ProcessID getNetworkPid(ProcessID id) {
    int myNetId = uuidToNetID.get(id.uuid());
    return Npids[myNetId];
  }
  
  /*
   * It populates buffer with source ProcessID, destination ProcessID, 
   * Data is written from startPosition to endPosition
   */
  public void updateBuffer(mpjbuf.Buffer buf, ProcessID srcID, ProcessID dstID,
      int startPos, int endPos) {
      
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug(" Setting start position to write in buffer: "+startPos);
    }
        
    ((NIOBuffer) buf.getStaticBuffer()).getBuffer().position(startPos);
    
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug(" Setting the limmit to write in buffer: "+endPos);
    }
    
    ((NIOBuffer) buf.getStaticBuffer()).getBuffer().limit(endPos);

    
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug(" Adding Source Information in Buffer, Source MSB: "+
        srcID.uuid().getMostSignificantBits()+" Source LSB: "+
        srcID.uuid().getLeastSignificantBits() );
    }
    // adding source ID information
    // System.out.println("Src msb: "+srcID.uuid().getMostSignificantBits()) ;
    // System.out.println("Src lsb: "+srcID.uuid().getLeastSignificantBits()) ;
    ((NIOBuffer) buf.getStaticBuffer()).getBuffer().putLong(
        srcID.uuid().getMostSignificantBits());
    ((NIOBuffer) buf.getStaticBuffer()).getBuffer().putLong(
        srcID.uuid().getLeastSignificantBits());


    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug(" Adding Destination Information in Buffer, Destination MSB: "+
        dstID.uuid().getMostSignificantBits()+" Destination LSB: "+
        dstID.uuid().getLeastSignificantBits() );
    }
    
    // adding destination ID information
    // System.out.println("Dst msb: "+dstID.uuid().getMostSignificantBits()) ;
    // System.out.println("Dst lsb: "+dstID.uuid().getLeastSignificantBits()) ;
    ((NIOBuffer) buf.getStaticBuffer()).getBuffer().putLong(
        dstID.uuid().getMostSignificantBits());
    ((NIOBuffer) buf.getStaticBuffer()).getBuffer().putLong(
        dstID.uuid().getLeastSignificantBits());
  }
  
   /**
   * Returns the id of this process.
   * 
   * @return ProcessID An object containing UUID of the process, 
   * UUID value will be that of current SMP process 
   */
  public ProcessID id() {
    return xdev.smpdev.SMPDeviceImpl.WORLD.id();
  }


  /* it returns Send Overhead of Hybrid Device */
  public int getSendOverhead() {
    return SEND_OVERHEAD;
  }

  /* it returns Receive Overhead of Hybrid Device */
  public int getRecvOverhead() {
    return RECV_OVERHEAD;
  }


  /**
   * Blocking send method.
   * 
   * @param buf
   *          The mpjbuf.Buffer object containing the data.
   * @param dstID
   *          ProcessID of the destination
   * @param tag
   *          The unique identifier of the message
   * @param context
   *          An integer providing "safe universe" for messages.
   * @throws MPJException
   *           If the buffer is null, dest process ID is insane.
   * @throws java.nio.BufferOverflowException
   * @throws ReadOnlyBufferException
   * @throws IOException
   *           If some I/O error occurs
   */
  public void send(mpjbuf.Buffer buf, ProcessID dstID, int tag, int context)
      throws XDevException {
      
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("HYB.send, calling isend method");
    }
    mpjdev.Request req = isend(buf, dstID, tag, context);
    req.iwait();
  }
  
  
  /**
   * Non-blocking send method
   * 
   * @param buf
   *          The mpjbuf.Buffer object containing the data.
   * @param dstID
   *          ProcessID of the destination process.
   * @param tag
   *          The unique identifier of the message.
   * @param context
   *          An integer providing "safe universe" for messages.
   * @return mpjdev.Request The Request object, which is later used to check the
   *         status of the message.
   */
  public mpjdev.Request isend(mpjbuf.Buffer buf, ProcessID dstID, int tag,
      int context) throws XDevException {
    if (isLocal(dstID)) {
    
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.isend, using SMPDevice \n Source:"+id().uuid()+
       " \n Destination:"+dstID.uuid()+" \n tag: "+tag+" \n Context: "+context);
      }
      // System.out.println(" HYB.Isend, using SMP => Src:"+id().uuid()+
      // " Dst:"+dstID.uuid()+" tag: "+tag+" Context: "+context );

      return smpHybDev.isend(buf, dstID, tag, context);
      
    } else {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.isend, using NIODevice \n Source:"+id().uuid()+
       " \n Destination:"+dstID.uuid()+" \n tag: "+tag+" \n Context: "+context);
      }
      // System.out.println(" HYB.Isend, using NIO => Src:"+id().uuid()+
      // " Dst:"+dstID.uuid()+" tag: "+tag+" Context: "+context );
      
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.isend, calling updateBuffer method");
      }
      updateBuffer(buf, id(), dstID, NET_SEND_OVERHEAD, SEND_OVERHEAD);
      
      mpjdev.Request req = nioHybDev.isend(buf, getNetworkPid(dstID), tag,
          context);
      return req;
    }
  }

  /**
   * This method is the blocking recv method.
   * 
   * @param buf
   *          The mpjbuf.Buffer object where the user wishes to receive the
   *          actual message
   * @param srcID
   *          The process id of the sending process
   * @param tag
   *          The unique identifier of the message
   * @return Status The status object containing the details of recv
   * @throws MPJException
   *           If the buffer is null or the src is insane
   * @throws IOException
   *           If some I/O error occurs
   * @throws java.lang.IllegalArgumentException
   */
  public mpjdev.Status recv(mpjbuf.Buffer buf, ProcessID srcID, int tag,
      int context) throws XDevException {
      
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.recv, using NIODevice \n Source:"+srcID.uuid()+
        " \n Destination:"+id().uuid()+
        " \n tag: "+tag+" \n Context: "+context);
    }
    
    //System.out.println("HYB.recv, using NIODevice \n Source:"+srcID.uuid()+
    //  " \n Destination:"+id().uuid().uuid()+
    //  " \n tag: "+tag+" \n Context: "+context);
    
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.recv, Posting irecv call") ;
    }
    mpjdev.Status status = new mpjdev.Status(srcID.uuid(), tag, -1);

    mpjdev.Request request = irecv(buf, srcID, tag, context, status);

    status = request.iwait();

    return status;
  }
  
  
  /**
   * Non-Blocking receive method.
   * 
   * @param buf
   *          The mpjbuf.Buffer objereceive the actual message
   * @param srcID
   *          The process id of the sending process
   * @param tag
   *          The unique identifier of the message
   * @param context
   *          An integer that provides "safe communication" universe
   * @return mpjdev.Status The status object containing the details of recv
   */
  public mpjdev.Request irecv(mpjbuf.Buffer buf, ProcessID srcID, int tag,
      int context, mpjdev.Status status) throws XDevException {

    
    //This code will execute if message is a for ANY_SOURCE
    if(srcID.uuid().equals(Device.ANY_SRC.uuid() ) ) {
      
      updateBuffer(buf, srcID, id(), 0, HYB_SEND_OVERHEAD);
      try {
        buf.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      HYBRecvRequest recvRequest = new HYBRecvRequest ( srcID, id(), tag, 
          context, buf, false ) ; 
      
      WildcardMessageManager  recvThread = new WildcardMessageManager(recvRequest) ;    
      new Thread (recvThread).start() ;
      
      return recvRequest;
    }
    else if (isLocal(srcID)) {
    
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.irecv, using SMPDevice \n Source:"+srcID.uuid()+
       " \n Destination:"+id().uuid()+" \n tag: "+tag+" \n Context: "+context);
      }
      // System.out.println(" HYB.Irecv, using SMP: Src:"+srcID.uuid()+
      // " Dst:"+id().uuid()+" tag: "+tag+" Context: "+context );

      return smpHybDev.irecv(buf, srcID, tag, context, status);
      
    }
    else {
      /*
       * For posting irecv on Network device we need to use 
       * network address of destination, so we need to add details of Hybrid 
       * (original) source and destination into buffer, 
       * buffer is read and cleared in Network device irecv method to retrieve 
       * orignal source and destination. 
       * Same buffer reused to keep received data.
       */
      
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.irecv, using NIODevice \n Source:"+srcID.uuid()+
       " \n Destination:"+id().uuid()+" \n tag: "+tag+" \n Context: "+context);
      }
      
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.irecv, calling updateBuffer method" ) ;
      }
      
      //System.out.println(" Calling update Buffer for irecv") ;
      
      updateBuffer(buf, srcID, id(), 0, HYB_SEND_OVERHEAD);
      
      try {
        buf.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      
     // System.out.println(" HYB.Irecv, using NIO: Src:"+srcID.uuid()+
      // " Dst:"+id().uuid()+" tag:"+tag+" Context:"+context );

      mpjdev.Request req = nioHybDev.irecv(buf, getNetworkPid(srcID), tag,
          context, status);

      return req;
    }
  }

  
  /**
   * Blocking synchronous send.
   */
  public void ssend(mpjbuf.Buffer buf, ProcessID dstID, int tag, int context)
      throws XDevException {
    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("HYB.ssend, posting issend method call ");
    }
    mpjdev.Request req = issend(buf, dstID, tag, context);
    req.iwait();
  }


  /**
   * Non-blocking synchronous send.
   */
  public mpjdev.Request issend(mpjbuf.Buffer buf, ProcessID dstID, int tag,
      int context) throws XDevException {

    if (isLocal(dstID)) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.issend, using SMPDevice \n Source:" + id().uuid()
            + " \n Destination:" + dstID.uuid() + " \n tag: " + tag
            + " \n Context: " + context);
      }
      //System.out.println(" HYB.issend, using SMP => Src:"+id().uuid()+
        //" Dst:"+dstID.uuid()+" tag: "+tag+" Context: "+context );

      return smpHybDev.issend(buf, dstID, tag, context);

    } else {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.issend, using NIODevice \n Source:" + id().uuid()
            + " \n Destination:" + dstID.uuid() + " \n tag: " + tag
            + " \n Context: " + context);
      }
      //System.out.println(" HYB.issend, using NIO => Src:"+id().uuid()+
        //" Dst:"+dstID.uuid()+" tag: "+tag+" Context: "+context );

      updateBuffer(buf, id(), dstID, NET_SEND_OVERHEAD, SEND_OVERHEAD);

      return nioHybDev.issend(buf, getNetworkPid(dstID), tag, context);

    }

  }


  /**
   * Blocking probe method
   * 
   * @param srcID
   *          The sourceID of the sender
   * @param tag
   *          The tag of the message
   * @param context
   *          The integer specifying the context
   * @return mpjdev.Status The status object
   */
  public mpjdev.Status probe(ProcessID srcID, int tag, int context)
      throws XDevException {
    mpjdev.Status status = null;
    boolean comp = false;

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("HYB.probe, posting iprove method call");
    }

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
   * 
   * @param srcID
   * @param tag
   * @param context
   * @return mpjdev.Status
   */
  public mpjdev.Status iprobe(ProcessID srcID, int tag, int context)
      throws XDevException {

    if (isLocal(srcID)) {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.iprobe, using SMPDevice \n Source:" + srcID.uuid()
            + " \n Destination:" + id().uuid() + " \n tag: " + tag
            + " \n Context: " + context);
      }
      // System.out.println(" HYB.iprobe, using SMP => Src:"+id().uuid()+"
      // +" tag: "+tag+" Context: "+context );

      return smpHybDev.iprobe(srcID, tag, context);
    } else {

      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.issend, using NIODevice \n Source:" + srcID.uuid()
            + " \n Destination:" + id().uuid() + " \n tag: " + tag
            + " \n Context: " + context);
      }
      // calling overloaded iprobe method of NIO Device.
      return nioHybDev.iprobe(srcID, id(), tag, context);
    }

  }


  /**
   * Non-Blocking overloaded probe method.
   * 
   * @param srcID
   * @param tag
   * @param context
   * @return mpjdev.Status
   */
  public mpjdev.Status iprobe(ProcessID srcID, ProcessID dstID, int tag,
      int context) throws XDevException {
    /*
    * Its implementation is required in in rest of the devices being used 
    * whinin hybdev
    */
    return null;
  }
  
  /**
   * Non-Blocking probe and fetch method.
   * 
   * @param srcID
   * @param dstID
   * @param tag
   * @param context
   * @param buf
   *          The mpjbuf.Buffer objereceive the actual message
   * @return mpjdev.Status
   */
  public mpjdev.Status iprobeAndFetch(ProcessID srcID, ProcessID dstID, int tag,
      int context, mpjbuf.Buffer buf ) throws XDevException {
    /*
    * Its implementation is required in Network device only
    * while working for Hybrid device
    */
    return null;
  }
  

  public mpjdev.Request peek() throws XDevException {

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug("HYB.peek, posting NIODevice.peek() method call,"
          + " SMPDevice peek returns null");
    }
    
    return nioHybDev.peek();
  }
  
  /**
   * This method shuts down the device.
   * 
   * @throws MPJException
   * @throws IOException
   *           If some I/O error occurs
   */
  public void finish() throws XDevException {
    int myGRank = myGid();

    if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
      logger.debug( myGid() + " HYB.finish(), acquiring finish lock, my rank") ;
    }
    smpHybDev.finish();
    smpHybDev=null;
    
    
    synchronized (lock) {
      
      if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
        logger.debug("HYB.finish(), acquired NIO finish lock, my rank "+myGid() ) ;
      }
      
      numRegisteredThreads++;
      
      if (numRegisteredThreads == smpProcs) {
        xdev.niodev.NIODevice.isHybrid = false;
        if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
          logger.debug("HYB.finish(), posting NIODevice.finish(),rank "+myGid());
        }
        
        cleanUp();
        SMPID=-1;
        
        nioHybDev.finish();
        
        nioHybDev=null;
        
        lock.notifyAll();
      } else {
        try {
          if (mpi.MPI.DEBUG && logger.isDebugEnabled()) {
            logger.debug(myGid()+" putting itself to wait for NIO finish ");
          }
          SMPID=-1;
          lock.wait();
          
        } catch (InterruptedException e) {
          throw new XDevException("In HYBDevice.finish(), unexpected "
            + "interuption during wait()??");
        } catch (IllegalMonitorStateException ex) {
          ex.printStackTrace();
          throw new XDevException(
            "HYBDevice.finish(), IllegalMonitorStateException during wait()");
        }
      }
    }    
  }
}
