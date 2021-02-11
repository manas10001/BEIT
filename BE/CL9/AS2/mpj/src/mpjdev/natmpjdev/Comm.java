/*
 The MIT License

 Copyright (c) 2013 - 2014
   1. SEECS, National University of Sciences and Technology, Pakistan (2013 - 2014)
   2. Bibrak Qamar  (2013 - 2014)

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
 * File         : natmpjdev.Comm.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */
package mpjdev.natmpjdev;

import mpjbuf.*;
import mpjdev.*;// for MPJException

public class Comm extends mpjdev.Comm {

  private static int mpiTagUB;
  int ghostTag = 0;
  public long handle = -1;

  public long ghostHandle = -1;
  private int size;
  static boolean initialized = false;

  // these values as per mpiJava
  public static int world = 2; // used for GetComm
  public static int self = 1;

  /*
   * Static Block for loading the libnativempjdev.so
   */
  static {
    System.loadLibrary("nativempjdev");
    
    if(!loadGlobalLibraries()) {
        System.out.println("MPJ Express failed to load required libraries");
        System.exit(1);
    }
  }
  /**
   * The initial communicator. Equivalent of MPI_COMM_WORLD.
   */
  public static Comm WORLD;

  /**
   * Number of Processes Spawned by this communicator.
   */
  public int size() {
    return size;
  }

  public int id() {
    // we can also have a int id set and here just return its value rather
    // than calling the group.rank() method
    // TODO: OPTIMIZE later
    return this.group.rank();
  }

  public Comm(long handle) throws MPJException {
    setHandle(handle);

  }

  public Comm(long handle, mpjdev.Group localgroup_) throws MPJException {
    setHandle(handle);
    this.localgroup = localgroup_;

  }

  public Comm() {
    GetComm(world); // sets this Comm to MPI_COMM_WORLD: This sets
		    // handle natively

    long groupHandle = group(); // this returns the underlying group of this
				// Comm
    this.group = new Group(groupHandle);

    ghostHandle = dup(handle); // dup is native
    size = size(handle); // size is native..
  }

  private void setHandle(long handle) throws MPJException {
    this.handle = handle;
    ghostHandle = dup(handle); // dup is native
    // get group handle then set this.group = new Group(returnedGroupHandle)
    long groupHandle = group(); // this returns the underlying group of this
				// Comm
    this.group = new Group(groupHandle);
    // this.localgroup = this.group;

    size = size(handle); // size is native..
  }

  public long getHandle() {
    return this.handle;
  }

  public Comm create(int[] ids) throws MPJException {

    long newComm = create(handle, ids); // this create is native
    if (newComm == 0)
      // It means this process will be outside the group..
      return null;
    else
      return new Comm(newComm);
  }

  public void free() throws MPJException {
    if (handle != -1)
      free(handle); // free is native
    if (ghostHandle != -1)
      free(ghostHandle);
  }

  /*
   * The init() method.. This initializes the MPI.
   */
  public static void init(String[] args) throws MPJException {

    if (initialized) {
      return;
    }
    nativeInit(args);
    WORLD = new Comm();
    mpiTagUB = getTagUB(); // returns mpiTagUB (its basically implementation
    // dependant value for max Tag value, read the MPI Specs)
    initialized = true;
  }

  /*
   * The finish()() method.. This would finialize the MPI. Previously this
   * method was defined in MPJDev.java but now it has been moved in here for the
   * native case
   */
  public static void finish() throws MPJException {
    nativeFinish();
  }
  
  public int getMPI_TAG_UB(){
	  return mpiTagUB;
  }

  public Comm split(int color, int key) {
    long newCommHandle = nativeSplit(this.handle, color, key);
    return new Comm(newCommHandle);
  }

  public void send(Buffer buf, int dest, int tag, boolean pt2pt)
      throws MPJException {

    if (buf == null) {
      throw new MPJDevException("In Comm.isend(), buffer is null.");
    }

    if (dest < 0) {
      throw new MPJDevException(
	  "In Comm.isend(), requested negative message destination: " + dest);
    } else if (dest >= size()) {
      throw new MPJDevException("In Comm.isend(), requested destination "
	  + dest + " does not exist in communicator of size " + size());
    }

    int staticBufferLength = buf.getSize();
    int dynamicBufferLength;

    if (buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length;
    } else
      dynamicBufferLength = 0;

    nativeSend(handle, buf, dest, tag,
	staticBufferLength + MPJDev.getSendOverhead(), dynamicBufferLength);

  } // ends send()

  public mpjdev.Status recv(Buffer buf, int src, int tag, boolean pt2pt)
      throws MPJException {

    if (buf == null) {
      throw new MPJDevException("In Comm.irecv(), buffer is null.");
    }

    if (src < 0 && src != -2) {
      throw new MPJDevException(
	  "In Comm.irecv(), requested negative message destination: " + src);
    }

    else if (src >= this.size() && src != -2) {
      throw new MPJDevException("In Comm.irecv(), requested source " + src
	  + " does not exist in communicator of size " + this.size());
    }

    mpjdev.Status status = new mpjdev.Status();

    int staticBufferLength = buf.getCapacity();
    nativeRecv(handle, buf, staticBufferLength, src, tag, status);

    int sectionSize = -1; // MPI.UNDEFINED
    Type sectionHeader = null;
    try {
      buf.commit();
      sectionHeader = buf.getSectionHeader();
      sectionSize = buf.getSectionSize();
    }
    catch (Exception e) {
    }

    status.type = sectionHeader;
    status.numEls = sectionSize;

    return status;
  }

  public NativeRequest isend(Buffer buf, int dest, int tag, boolean pt2pt)
      throws MPJException {

    if (buf == null) {
      throw new MPJDevException("In Comm.isend(), buffer is null.");
    }

    if (dest < 0) {
      throw new MPJDevException(
	  "In Comm.isend(), requested negative message destination: " + dest);
    } else if (dest >= size()) {
      throw new MPJDevException("In Comm.isend(), requested destination "
	  + dest + " does not exist in communicator of size " + size());
    }

    NativeSendRequest req = new NativeSendRequest();

    int staticBufferLength = buf.getSize();
    int dynamicBufferLength;

    if (buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length;
    } else
      dynamicBufferLength = 0;

    nativeIsend(handle, buf, dest, tag,
	staticBufferLength + MPJDev.getSendOverhead(), dynamicBufferLength, req);

    return req;

  } // ends isend()

  public NativeRequest irecv(Buffer buffer, int src, int tag,
      mpjdev.Status status, boolean pt2pt) throws MPJException {

    if (buffer == null) {
      throw new MPJDevException("In Comm.irecv(), buffer is null.");
    }

    if (src < 0 && src != -2) {
      throw new MPJDevException(
	  "In Comm.irecv(), requested negative message destination: " + src);
    }

    else if (src >= this.size() && src != -2) {
      throw new MPJDevException("In Comm.irecv(), requested source " + src
	  + " does not exist in communicator of size " + this.size());
    }

    NativeRecvRequest req = new NativeRecvRequest(handle);

    nativeIrecv(handle, buffer, src, tag, status, req);

    req.addCompletionHandler(new mpjdev.CompletionHandler() {
      public void handleCompletion(mpjdev.Status status) {
	/* Check for the matching receive */
	if (status.source != -1) {

	}
	// actually the source is already populated by
	// then native iwait() so just let go

      }
    });

    return req;

  } // ends irecv()

  public void ssend(Buffer buf, int dest, int tag, boolean pt2pt)
      throws MPJException {
    if (buf == null) {
      throw new MPJDevException("In Comm.isend(), buffer is null.");
    }

    if (dest < 0) {
      throw new MPJDevException(
	  "In Comm.isend(), requested negative message destination: " + dest);
    } else if (dest >= size()) {
      throw new MPJDevException("In Comm.isend(), requested destination "
	  + dest + " does not exist in communicator of size " + size());
    }

    int staticBufferLength = buf.getSize();
    int dynamicBufferLength;

    if (buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length;
    } else
      dynamicBufferLength = 0;

    nativeSsend(handle, buf, dest, tag,
	staticBufferLength + MPJDev.getSendOverhead(), dynamicBufferLength);

  }

  public mpjdev.Request issend(Buffer buf, int dest, int tag, boolean pt2pt)
      throws MPJException {

    if (buf == null) {
      throw new MPJDevException("In Comm.isend(), buffer is null.");
    }

    if (dest < 0) {
      throw new MPJDevException(
	  "In Comm.isend(), requested negative message destination: " + dest);
    } else if (dest >= size()) {
      throw new MPJDevException("In Comm.isend(), requested destination "
	  + dest + " does not exist in communicator of size " + size());
    }

    NativeSendRequest req = new NativeSendRequest();

    int staticBufferLength = buf.getSize();
    int dynamicBufferLength;

    if (buf.getDynamicBuffer() != null) {
      dynamicBufferLength = buf.getDynamicBuffer().length;
    } else
      dynamicBufferLength = 0;

    nativeIssend(handle, buf, dest, tag,
	staticBufferLength + MPJDev.getSendOverhead(), dynamicBufferLength, req);

    return req;
  }

  /*
   * To create Intercomm
   */
  public Comm create(mpjdev.Comm localcomm, mpjdev.Group peergroup,
      int localleader, int remoteleader, int tag) throws MPJException {

    long newIntercomm = nativeCreateIntercomm(handle,
	((mpjdev.natmpjdev.Comm) localcomm).getHandle(), localleader,
	remoteleader, tag);

    return new Comm(newIntercomm, localcomm.group);

  }

  public Comm create(mpjdev.Group ngroup) throws MPJException {

    long ngroupHandle = ((mpjdev.natmpjdev.Group) ngroup).getHandle();
    long newCommHandle = nativeCreate(ngroupHandle);

    if (newCommHandle == 0)
      return null;
    else
      return new Comm(newCommHandle);

  }

  public void barrier() {
    // No need here in natmpjdev
  }

  // This is also null in javampjdev
  public Comm clone() throws MPJException {
    return null;
  }

  public mpjdev.Status iprobe(int src, int tag) throws MPJException {

    if (src < 0 && src != -2) {
      throw new MPJDevException(
	  "In Comm.iprobe(), requested negative message destination: " + src);
    } else if (src >= this.size() && src != -2) {
      throw new MPJDevException("In Comm.iprobe(), requested source " + src
	  + " does not exist in communicator of size " + this.size());
    }

    return probe(src, tag);
  }

  public mpjdev.Status probe(int src, int tag) throws MPJException {

    if (src < 0 && src != -2) {
      throw new MPJDevException(
	  "In Comm.iprobe(), requested negative message destination: " + src);
    } else if (src >= this.size() && src != -2) {
      throw new MPJDevException("In Comm.iprobe(), requested source " + src
	  + " does not exist in communicator of size " + this.size());
    }

    mpjdev.Status status = new mpjdev.Status(src, tag, -1);
    nativeProbe(handle, src, tag, status);

    // this status has numEls field which is not set - since its not
    // significant here, the countInBytes field is set
    return status;
  }

  /*
   * Native Methods
   */
   
  // used to load with dlopen() -- Mainly because Open MPI depends on it
  private static native boolean loadGlobalLibraries();  
   
  // why static. Because its called from static contenxt in MPJDev
  private static native int getTagUB();

  // this gets the handle as COMM_WORLD
  // perhaps rename it from getWorld() to getCOMM_WORLD()
  private static native long getWorld(); // TODO rename it to getCOMM_WORLD()

  private native long dup(long comm) throws MPJException;

  private native int rank(long handle) throws MPJException;

  private native int size(long comm) throws MPJException;

  private native long create(long comm, int[] ids) throws MPJException;

  private native long nativeSplit(long handle, int color, int key)
      throws MPJException;

  private native long nativeCreate(long ngroup) throws MPJException;

  private native long nativeCreateIntercomm(long peerCommHandle,
      long localCommHandle, int localleader, int remoteleader, int tag)
      throws MPJException;

  private native void free(long comm) throws MPJException;

  private static native void nativeInit(String[] args);

  public static native void nativeFinish();

  private native long group() throws MPJException; // gets the group handle of
						   // this Comm

  private native void GetComm(int Type) throws MPJException;

  private native void nativeSend(long handle, mpjbuf.Buffer buf, int dest,
      int tag, int slen, int dlen) throws MPJException;

  private native void nativeRecv(long handle, mpjbuf.Buffer buffer, int size,
      int src, int tag, mpjdev.Status status) throws MPJException;

  private native void nativeSsend(long handle, mpjbuf.Buffer buf, int dest,
      int tag, int slen, int dlen) throws MPJException;

  private native void nativeIssend(long handle, Buffer buffer, int dest,
      int tag, int staticBufferLength, int dynamicBufferLength,
      NativeSendRequest req) throws MPJException;

  private native void nativeIsend(long handle, Buffer buffer, int dest,
      int tag, int staticBufferLength, int dynamicBufferLength,
      NativeSendRequest req) throws MPJException;

  private native void nativeIrecv(long handle, Buffer buffer, int src, int tag,
      mpjdev.Status status, NativeRecvRequest req) throws MPJException;

  // I know the status already has the src and tag initialized but for the
  // simplicity
  // of implementing at the native level lets just pass the src and tag in the
  // arguments
  private native void nativeProbe(long handle, int src, int tag,
      mpjdev.Status status) throws MPJException;
} // ends Comm class

