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
 * File         : MXDevice.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Sun Oct 23 18:15:07 BST 2005
 * Revision     : 
 * Updated      : 
 */
package xdev.mxdev;

import xdev.Device ;
import xdev.ProcessID ;
import xdev.XDevException ;
import java.util.UUID ;
import mpjbuf.BufferFactory ;
import java.io.IOException ;
import java.util.StringTokenizer; 
import xdev.niodev.ConfigReader; // this will definitely goto xdev pack.
import mpi.ProcTree ; 
import mpjbuf.Type;
import java.util.HashMap ; 

public class MXDevice implements Device {
  int rank ; 	
  int nprocs = 0; 
  int SEND_OVERHEAD ; 
  int RECV_OVERHEAD ; 
  String[] processNames = null; 
  int [] ranks = null;
  ProcTree procTree ; 
  int index, root, extent, places;
  MXProcessID[] pids;
  Device nioDevice ; 
  static HashMap<Long, MXRequest> requestMap = 
	  new HashMap<Long, MXRequest> (); 

  public MXDevice() {
    //this.deviceName = "mxdev"; 	  
  }
  
  public ProcessID[] init(String[] args) throws XDevException { 

    // \/\/\/\/\/\
    //nioDevice = Device.newInstance("niodev"); 
    //nioDevice.init(args);
    // \/\/\/\/\/\
    rank = Integer.parseInt(args[0]); 	  
    SEND_OVERHEAD = getSendOverhead(); 
    RECV_OVERHEAD = getRecvOverhead();
    ConfigReader reader = null;  

    try {
      reader = new ConfigReader(args[1]);
      nprocs = (new Integer(reader.readNoOfProc())).intValue();
      int psl = (new Integer(reader.readIntAsString())).intValue();
    }
    catch (Exception config_error) {
      throw new XDevException(config_error);
    }

    int count = 0;
    processNames = new String[nprocs];
    ranks = new int[nprocs]; 
    pids = new MXProcessID[nprocs]; 

    while (count < nprocs) {

      String line = null;

      try {
        line = reader.readLine();
      }
      catch (IOException ioe) {
        throw new XDevException(ioe);
      }

      if (line == null || line.equals("") || line.equals("#")) {
        continue;
      }

      line = line.trim();
      StringTokenizer tokenizer = new StringTokenizer(line, "@");
      processNames [count] = tokenizer.nextToken();
      //processNames[count] = processNames[count]+":"+tokenizer.nextToken(); 
      //processNames[count] = processNames[count]+":0";
      processNames[count] = processNames[count]+":"+tokenizer.nextToken() ;
      //tokenizer.nextToken(); //this will return the default port number
                             //because the runtime does not know what 
			     //to write in the conf file according to the
			     //device that is being used ...may be make 
			     //mx_board_num another entry ...
			     //at the moment ..this is hard coded as you can
			     //see above :0 ;-)
      ranks[count] = (new Integer(tokenizer.nextToken())).intValue();
      count++;

    }

    reader.close();

    /* Make a tree structure */
    index = rank;
    root = 0;
    procTree = new ProcTree();
    extent = nprocs;
    places = ProcTree.PROCTREE_A * index;

    for (int i = 1; i <= ProcTree.PROCTREE_A; i++) {
      ++places;
      int ch = (ProcTree.PROCTREE_A * index) + i + root;
      ch %= extent;

      if (places < extent) {
        procTree.child[i - 1] = ch;
        procTree.numChildren++;
      }
    }

    if (index == root) {
      procTree.isRoot = true;
    }
    else {
      procTree.isRoot = false;
      int pr = (index - 1) / ProcTree.PROCTREE_A;
      procTree.parent = pr;
    }

    procTree.root = root;

    System.loadLibrary("mxdev"); 	  
    
    pids[rank] = new MXProcessID(UUID.randomUUID());
    UUID myUUID = pids[rank].uuid();
    long msb = myUUID.getMostSignificantBits();
    long lsb = myUUID.getLeastSignificantBits();
    nativeInit(args,rank,processNames,ranks,nprocs, pids, msb, lsb); 
    //System.out.println("rank "+rank+"calling barrier");
    barrier();
    //System.out.println("rank "+rank+"called barrier");
    return pids;
  }

  public int getSendOverhead() {
    return 8;	  
  }
  
  public int getRecvOverhead() {
    return 8;	  
  }

  public ProcessID id() {
    return pids[rank];
  }

  private void barrier() { 
    int offset = 0;
    int[] data = new int[1];
    int count = 1;
    int btag = 34*1000 ; 
    int context = 50;

    mpjbuf.Buffer sbuf = new mpjbuf.Buffer(
                    BufferFactory.create(23+SEND_OVERHEAD),
                    SEND_OVERHEAD , 23+SEND_OVERHEAD );

    mpjbuf.Buffer rbuf = new mpjbuf.Buffer(
		    BufferFactory.create(
			    16+RECV_OVERHEAD), 
		            RECV_OVERHEAD, 
		            RECV_OVERHEAD+16) ;

    if (procTree.numChildren == -1 || !procTree.isRoot) {
      try {
        sbuf.putSectionHeader(Type.INT);
        sbuf.write(data, offset, count);
        sbuf.commit();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (procTree.isRoot) {
      for (int i = 0; i < procTree.child.length; i++) {
        if (procTree.child[i] != -1) {
          recv(rbuf, pids[procTree.child[i]], btag, context);
          try {
            rbuf.clear() ;
          }
          catch(Exception e) {}
        }
      }
    }
    else {
      if (procTree.parent == -1) {
        System.out.println("non root's node parent doesn't exist");
      }

      for (int i = 0; i < procTree.child.length; i++) {
        if (procTree.child[i] != -1) {
          recv(rbuf, pids[procTree.child[i]], btag, context);
          try {
            rbuf.clear() ;
          }
          catch(Exception e) {}
        }
      }

      send(sbuf, pids[procTree.parent], btag, context);
    }

    if (procTree.isRoot) {
      //realFinish();
    }

    BufferFactory.destroy( sbuf.getStaticBuffer()) ;
    BufferFactory.destroy( rbuf.getStaticBuffer()) ;
  } 

  public void finish() throws XDevException { 
    //System.out.println(" mxdev.finish() "); 	  
    barrier();
    //nioDevice.finish(); 
    nativeFinish();	  
  }

  public mpjdev.Request isend(mpjbuf.Buffer buf, ProcessID dstID,
                                       int tag, int context) 
	                               throws XDevException {
    MXSendRequest sendRequest = new MXSendRequest();			       
    int staticBufferLength = buf.getSize() ;
    int dynamicBufferLength;   
    int offset = buf.offset(); 

    if(buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length ;	    
    }
    else {
      dynamicBufferLength = 0;
    }

    //buf.getStaticBuffer().putInt( staticBufferLength, 4 );
    //buf.getStaticBuffer().put( encoding, 0); .. .. 
    //buf.getStaticBuffer().putInt( dynamicBufferLength, 0 );
    
    //System.out.println("offset "+offset);
    //System.out.println("staticBufferLength "+staticBufferLength );
    //System.out.println("dynamicBufferLength"+dynamicBufferLength);
    
    nativeIsend(buf, dstID, tag, context, staticBufferLength, 
		    dynamicBufferLength, sendRequest, offset ); 
    requestMap.put( new Long(sendRequest.handle), sendRequest) ; 
    return sendRequest ;	  
  }

  public void send(mpjbuf.Buffer buf, ProcessID dstID,
                            int tag, int context) throws XDevException {
    int staticBufferLength = buf.getSize() ;
    int dynamicBufferLength;    
    int offset = buf.offset(); 
    //System.out.println("offset "+offset);

    if(buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length ;	    
    }
    else {
      dynamicBufferLength = 0;
    }

    //we can write the control-section here ..	  
    nativeSend(buf, dstID, tag, context, staticBufferLength,
		    dynamicBufferLength);
  }

  public mpjdev.Request issend(mpjbuf.Buffer buf, ProcessID dstID,
                                        int tag, int context) 
	  				throws XDevException { 
    MXSendRequest sendRequest = new MXSendRequest();			       
    int staticBufferLength = buf.getSize() ;
    int dynamicBufferLength;    

    if(buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length ;	    
    }
    else {
      dynamicBufferLength = 0;
    }
    nativeIssend(buf, dstID, tag, context, staticBufferLength, 
		    dynamicBufferLength, sendRequest );
    requestMap.put( new Long(sendRequest.handle), sendRequest) ; 
    return sendRequest ;	  
  }

  public void ssend(mpjbuf.Buffer buf, ProcessID dstID,
                             int tag, int context) throws XDevException {
    int staticBufferLength = buf.getSize() ;
    int dynamicBufferLength;    

    if(buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length ;	    
    }
    else {
      dynamicBufferLength = 0;
    }
    
    nativeSsend(buf, dstID, tag, context, staticBufferLength,
		    dynamicBufferLength);
  }

  public mpjdev.Status recv(mpjbuf.Buffer buf, ProcessID srcID,
                                     int tag, int context) 
	                             throws XDevException {

    mpjdev.Status status = new mpjdev.Status();
    //we do not need any of this bollocks if the native code can set 
    // "status.srcID and status.tag"
    int ANY_SRC = 0; //0 is false and 1 is true
    if(srcID.uuid().equals(xdev.Device.ANY_SRC.uuid()) ) {
      ANY_SRC = 1;	    
    }
    //else if(srcID.uuid().equals(xdev.Device.ANY_SRC.uuid())) { 
    //  ANY_SRC = 1;      	    
    //  status.tag = tag;
    //} else if(tag == Device.ANY_TAG) {
    //  status.srcID = srcID.uuid();
    //} else {
    //  status.srcID = srcID.uuid();
    //  status.tag = tag;
    //}

    nativeRecv( buf, srcID, tag, context, status, ANY_SRC);
    status.srcID = pids[status.source].uuid(); 
    try { 
      buf.commit(); 
      status.type = buf.getSectionHeader() ; 
      status.numEls = buf.getSectionSize() ; 
    }
    catch(Exception e) { 
      throw new XDevException(e); 	    
    } 
    return status;	  
    
  }

  public mpjdev.Request irecv(mpjbuf.Buffer buf, ProcessID srcID,
                                       int tag, int context,
                                       mpjdev.Status status) 
	                               throws XDevException { 
    MXRecvRequest recvRequest = new MXRecvRequest(this);
    int ANY_SRC = 0; //0 is false and 1 is true
    
    if(srcID.uuid().equals(xdev.Device.ANY_SRC.uuid())) { 
      ANY_SRC = 1;      	    
    }
    
    recvRequest.status = new mpjdev.Status(); 
    nativeIrecv(buf, srcID, tag, context, status, recvRequest,
	    ANY_SRC); 
    requestMap.put( new Long(recvRequest.handle), recvRequest) ; 
    return recvRequest;	  
  }

  public mpjdev.Status probe(ProcessID srcID, int tag,
                                      int context) throws XDevException {
    mpjdev.Status status = new mpjdev.Status();	  
    int ANY_SRC = 0; //0 is false and 1 is true
    if(srcID.uuid().equals(xdev.Device.ANY_SRC.uuid())) { 
      ANY_SRC = 1;      	    
    } 
    //else { 
    //  status.srcID = srcID.uuid();
    //  status.tag = tag;
    //}
    nativeProbe(srcID, tag, context, status, ANY_SRC);
    status.srcID = pids[status.source].uuid(); 
    return status;	  
  }
  
  public mpjdev.Status iprobe(ProcessID srcID, ProcessID dstID, int tag,
            int context) throws XDevException { 
    return null;
  }
  
  public mpjdev.Status iprobeAndFetch(ProcessID srcID, ProcessID dstID, int tag,
    int context, mpjbuf.Buffer buf) throws XDevException {
    return null;
  }

  public mpjdev.Status iprobe(ProcessID srcID, int tag,
                                       int context) throws XDevException {
    mpjdev.Status status = new mpjdev.Status();	  
    int ANY_SRC = 0; //0 is false and 1 is true
    if(srcID.uuid().equals(xdev.Device.ANY_SRC.uuid())) { 
      ANY_SRC = 1;      	    
    }
    int isCompleted = 0 ; 
    //System.out.println(" context(iprobe) "+context); 
    isCompleted = nativeIprobe(srcID, tag, 
		    context, status, ANY_SRC, isCompleted );
    //System.out.println(" isCompleted (after probe) <"+isCompleted+">");
    //System.out.println(" isCompleted (after probe) <"+status+">");
    if(isCompleted == 1) { 
      status.srcID = pids[status.source].uuid(); 
    }
    //we need to return null in case there is no message that has been 
    //probed.
    return ((isCompleted == 1) ? status : null); 
  }
 

  // TODO: need to get rid of isPeeked flag ..its not required anymore ...
  public mpjdev.Request peek() throws XDevException { 
    long natPeekedReqHandle ; 	  
    //this status is dummy ...no use ...
    mpjdev.Status completedStatus = new mpjdev.Status() ; 
    natPeekedReqHandle = nativePeek(completedStatus) ; 
    //note that peekedRequest is not deleted from requestMap ...
    //iwait is supposed to do this ...
    MXRequest peekedRequest = requestMap.get(new Long(natPeekedReqHandle)) ; 
    //peekedRequest.isPeeked = 1 ; 
    //peekedRequest.status = completedStatus ; 
    return peekedRequest ; 
  }

/*
  public static mpjdev.Status iwaitany(mpjdev.Request[] requests) { 

    MXRequest peekedRequest ;
    long natPeekedReqHandle ; 
    mpjdev.Status completedStatus = new mpjdev.Status() ; 
    boolean found = false;  
    boolean inActive = true ; 

    // check if there is a valid request which could be peeked 
    for(int i=0 ; i< requests.length ; i++) { 
      if(requests[i] != null) {  	 
        inActive = false; 	      
      }
    }

    if(inActive) { 
      return null;  	    
    }
    
    do {
      natPeekedReqHandle = nativePeek(completedStatus) ; 
      //iwait will delete it ...
      peekedRequest = requestMap.get(new Long(natPeekedReqHandle)) ; 
      //peekedRequest = (MXRequest) nativePeek(completedStatus);
      //peekedRequest.isPeeked = 1;
      completedStatus = peekedRequest.iwait() ; 
      //deletePeekedRequest(peekedRequest, peekedRequest.requestStruct) ; 
      // see if the peekedRequest is in the argument 
      for(int j=0 ;j<requests.length ; j++) { 
	if(requests[j] != null) {       
          if(peekedRequest == requests[j]) { 
	    completedStatus.index = j ; 	  
	    found = true; 
            break;
	  }
	}
      }
      if(!found) { 
        System.out.println(" The message peeked is not what we are looking for"+
	  	           "... trying more ... "); 
      }
    } while(!found); 
   
    //completedStatus = peekedRequest.iwait(); 
    // need to delete global reference ...
    return completedStatus ; 

  }
  */ //iwaitany ends ...

  native void nativeInit(String[] args, int rank,
		  String[] processNames, int[] ranks, int nprocs,
		  MXProcessID[] pids, long msb, long lsb) ; 
  native void nativeIsend(mpjbuf.Buffer buf, ProcessID dstID, int tag, 
		  int context, int staticBufferLength,
		  int dynamicBufferLength, MXSendRequest req, 
		  int offset );
  native void nativeSend(mpjbuf.Buffer buf, ProcessID dstID, int tag, 
		  int context, int staticBufferLength,
		  int dynamicBufferLength);
  native void nativeIssend(mpjbuf.Buffer buf, ProcessID dstID, int tag, 
		  int context, int staticBufferLength,
		  int dynamicBufferLength, MXSendRequest req);
  native void nativeSsend(mpjbuf.Buffer buf, ProcessID dstID, int tag, 
		  int context, int staticBufferLength,
		  int dynamicBufferLength);
  native void nativeIrecv(mpjbuf.Buffer buf, ProcessID srcID, int tag, 
		  int context, mpjdev.Status status, MXRecvRequest req, 
		  int anySrc);
  native void nativeRecv(mpjbuf.Buffer buf, ProcessID srcID, int tag,
		  int context, mpjdev.Status status, int anySrc);
  native void nativeProbe(ProcessID srcID, int tag, int context, 
		  mpjdev.Status status, int anySrc);
  native int nativeIprobe(ProcessID srcID, int tag, int context,
		  mpjdev.Status status, int anySrc, int isCompleted);
  native long nativePeek(mpjdev.Status completedStatus) ; 
  static native void deletePeekedRequest(MXRequest request, 
		  long requestStruct) ; 
  native void nativeFinish();

}
