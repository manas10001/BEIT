/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * File         : Comm.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.60 $
 * Updated      : $Date: 2014/03/11 13:36:40 $
 */

package mpi;

import mpjdev.*;
import mpjbuf.*;
import java.util.Hashtable;
import java.nio.ByteBuffer;

public class Comm {

  Hashtable attrTable = new Hashtable();
  public mpi.Group group = null;
  mpi.Group localgroup = null;
  public mpjdev.Comm mpjdevComm = null;
  boolean intercomm = false;
  static final int NULL = 2;
  int code = -1;

  Comm() {
  }

  Comm(int code) {
    this.code = code;
  }

  /**
   * Size of group of this communicator.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>number of processors in the group of this communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_SIZE</tt>.
   */
  public int Size() throws MPIException {
    return (intercomm ? this.localgroup.Size() : this.group.Size());
  }

  /**
   * Rank of this process in group of this communicator.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>rank of the calling process in the group of this communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_RANK</tt>.
   */
  public int Rank() throws MPIException {
    return (intercomm ? this.localgroup.Rank() : this.group.Rank());
  }

  /**
   * Return group associated with a communicator.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>group corresponding to this communicator group
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_GROUP</tt>.
   */
  public Group Group() throws MPIException {
    return (intercomm ? this.localgroup : this.group);
  }

  /**
   * Compare two communicators.
   * <p>
   * <table>
   * <tr>
   * <td><tt> comm1    </tt></td>
   * <td>first communicator
   * </tr>
   * <tr>
   * <td><tt> comm2    </tt></td>
   * <td>second communicator
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>result
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_COMPARE</tt>.
   * <p>
   * <tt>MPI.IDENT</tt>(0) results if the <tt>comm1</tt> and <tt>comm2</tt> are
   * references to the same object (ie, if <tt>comm1 == comm2</tt>).
   * <tt>MPI.CONGRUENT</tt>(3) results if the underlying groups are identical
   * but the communicators differ by context. <tt>MPI.SIMILAR</tt>(1) results if
   * the underlying groups are similar but the communicators differ by context.
   * <tt>MPI.UNEQUAL</tt>(2) results otherwise.
   */

  public static int Compare(Comm comm1, Comm comm2) throws MPIException {

    if (Constants.isNative) {
      // in case of native
      return mpi.NativeIntracomm.Compare(comm1, comm2);
    }

    // put in a new condition check which produces an error messages if both
    // the comms are not of the same type, i.e by type i mean intra/inter comms
    if (!comm1.intercomm && !comm2.intercomm) {
      // what about collective context ..?
      int val = 0;
      val = Group.Compare(comm1.group, comm2.group);

      /*
       * if the return val is IDENT ..then we need to compare the contexts ...if
       * the contexts are equal ...then IDENT else CONGRUENT ...
       */

      if (val == MPI.IDENT) {
	if (comm1.mpjdevComm.sendctxt == comm2.mpjdevComm.sendctxt) {
	  return MPI.IDENT;
	} else {
	  return MPI.CONGRUENT;
	}
      }

      /*
       * if UNEQUAL, then just return MPI.UNEQ.. if SIMILAR ..then just return
       * MPI.SIMILAR ..
       */
      return val;
    } else {
      int val1, val2;
      val1 = Group.Compare(comm1.group, comm1.group);
      val2 = Group.Compare(comm1.localgroup, comm1.localgroup);

      if (val1 == MPI.IDENT && val2 == MPI.IDENT) {
	if ((comm1.mpjdevComm.sendctxt == comm2.mpjdevComm.sendctxt)
	    && (comm1.mpjdevComm.recvctxt == comm2.mpjdevComm.recvctxt)) {
	  return MPI.IDENT;
	} else {
	  return MPI.CONGRUENT;
	}

      } else if (val1 == val2) {
	return val1;
      } else if (val1 == MPI.SIMILAR && val2 == MPI.SIMILAR) {
	return MPI.SIMILAR;
      }

      return MPI.UNEQUAL;
    }
  }

  /**
   * Destroy this communicator.
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_FREE</tt>.
   */
  public void Free() throws MPIException {
  }

  /**
   * Test if this communicator is an inter-communicator.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td> <tt>true</tt> if this is an inter-communicator, <tt>false</tt>
   * otherwise
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_TEST_INTER</tt>.
   */
  public boolean Test_inter() throws MPIException {
    return intercomm;
  }

  /**
   * Create an inter-communicator.
   * <p>
   * <table>
   * <tr>
   * <td><tt> local_comm    </tt></td>
   * <td>local intra-communicator
   * </tr>
   * <tr>
   * <td><tt> local_leader  </tt></td>
   * <td>rank of local group leader in <tt>localComm</tt>
   * </tr>
   * <tr>
   * <td><tt> remote_leader </tt></td>
   * <td>rank of remote group leader in this communictor
   * </tr>
   * <tr>
   * <td><tt> tag           </tt></td>
   * <td>``safe'' tag
   * </tr>
   * <tr>
   * <td><em> returns:      </em></td>
   * <td>new inter-communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_INTERCOMM_CREATE</tt>.
   * <p>
   * (This operation is defined as a method on the ``peer communicator'', making
   * it analogous to a <tt>send</tt> or <tt>recv</tt> communication with the
   * remote group leader.)
   */
  public Intercomm Create_intercomm(mpi.Comm local_comm, int local_leader,
      int remote_leader, int tag) throws MPIException {

    if (local_comm.Rank() == -1) {
      throw new MPIException(" Error in Create_intercomm: " + " rank <"
	  + local_comm.Rank() + "> is -1");
    }

    mpjdev.Comm newIntercomm = null;

    try {
      newIntercomm = mpjdevComm.create(local_comm.mpjdevComm,
	  this.group.mpjdevGroup, local_leader, remote_leader, tag);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    return new Intercomm(newIntercomm, newIntercomm.localgroup,
	newIntercomm.group, this, local_leader, remote_leader, local_comm);
  }

  /**
   * Retrieves attribute value by key.
   * <p>
   * <table>
   * <tr>
   * <td><tt> keyval   </tt></td>
   * <td>one of the key values predefined by the implementation
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>attribute value
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ATTR_GET</tt>.
   */
  public Object Attr_get(int keyval) throws MPIException {
    Object value = attrTable.get(new Integer(keyval));

    if (value != null) {
      return value;
    } else {
      throw new MPIException(" Error in Attr_get: No corresponding value "
	  + "to key <" + keyval + ">");
    }

  }

  /**
   * Retrieves attribute value by key.
   * <p>
   * <table>
   * <tr>
   * <td><tt> keyval   </tt></td>
   * <td>one of the key values predefined by the implementation
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>attribute value
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ATTR_GET</tt>.
   */
  public void Attr_delete(int keyval) throws MPIException {
    try {
      attrTable.remove(new Integer(keyval));
    }
    catch (Exception e) {
      throw new MPIException(e);
    }
  }

  /**
   * Attribute put method which can be used to add attributed to this
   * communicator ... find out why is it not part of API? and if it should be
   * part of it?
   */
  void Attr_put(int key, int val) throws MPIException {
    try {
      attrTable.put(new Integer(key), new Integer(val));
    }
    catch (Exception e) {
      throw new MPIException(e);
    }
  }

  /**
   * Blocking send operation.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SEND</tt>.
   * <p>
   * The actual argument associated with <tt>buf</tt> must be one-dimensional
   * array. The value <tt>offset</tt> is a subscript in this array, defining the
   * position of the first item of the message.
   * <p>
   * If the <tt>datatype</tt> argument represents an MPI basic type, its value
   * must agree with the element type of <tt>buf</tt>---either a primitive type
   * or a reference (object) type. If the <tt>datatype</tt> argument represents
   * an MPI derived type, its <em>base type</em> must agree with the element
   * type of <tt>buf</tt>
   */
  public void Send(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    try {
      send(buf, offset, count, datatype, dest, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected void send(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag, boolean pt2pt) throws MPIException {

    /* this is MPI.PACKED case */
    if (datatype.baseType == 9) {
      mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      try {
	mpjbuf.commit();
	mpjdevComm.send(mpjbuf, dest, tag, pt2pt);
	mpjbuf.clear();
	mpjbuf.free();
      }
      catch (Exception e) {
	throw new MPIException(e);
      }
      return;
    }

    Packer packer = datatype.getPacker();

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug(" creatingBuffer ");
    }
    mpjbuf.Buffer wBuffer = datatype.createWriteBuffer(count);

    try {
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("count " + count);
	MPI.logger.debug("offset" + offset);
	MPI.logger.debug("buf"
	    + ((NIOBuffer) wBuffer.getStaticBuffer()).getBuffer());

      }
      packer.pack(wBuffer, buf, offset, count);
      wBuffer.commit();
      mpjdevComm.send(wBuffer, dest, tag, pt2pt);
      wBuffer.clear();
      wBuffer.free(); // gadget memory leak fix?
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug(" destroying buffer ");
      }
      BufferFactory.destroy(wBuffer.getStaticBuffer());
    }
    catch (Exception e) {
      throw new MPIException(e);
    }
  }

  /**
   * Send in synchronous mode.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SSEND</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public void Ssend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    try {
      ssend(buf, offset, count, datatype, dest, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected void ssend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag, boolean pt2pt) throws MPIException {

    /* this is MPI.PACKED case */
    if (datatype.baseType == 9) {
      mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      try {
	mpjbuf.commit();
	mpjdevComm.ssend(mpjbuf, dest, tag, pt2pt);
	mpjbuf.clear(); // bug-fix
	mpjbuf.free(); // gadget memory leak fix?
      }
      catch (Exception e) {
	throw new MPIException(e);
      }
      return;
    }

    Packer packer = datatype.getPacker();
    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug(" creatingBuffer ");
    }
    mpjbuf.Buffer wBuffer = datatype.createWriteBuffer(count);

    try {
      packer.pack(wBuffer, buf, offset, count);
      wBuffer.commit();
      mpjdevComm.ssend(wBuffer, dest, tag, pt2pt);
      wBuffer.clear(); // bug-fix
      wBuffer.free(); // gadget memory leak fix?
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug(" destroying buffer ");
      }
      BufferFactory.destroy(wBuffer.getStaticBuffer());
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

  }

  /**
   * Send in buffered mode.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_BSEND</tt>.
   * <p>
   * This operation copies message into a buffer (mpi.Buffer) specified by
   * MPI.Buffer_attach operation, and then sends using the standard mode of
   * communication. Further comments as for <tt>Send</tt>.
   */
  public void Bsend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    bsend(buf, offset, count, datatype, dest, tag, true);
  }

  protected void bsend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag, boolean pt2pt) throws MPIException {

    /* this is MPI.PACKED case */
    // in reality, this is not buffering the data ...
    if (datatype.baseType == 9) {
      mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      try {
	mpjbuf.commit();
	mpjdevComm.send(mpjbuf, dest, tag, pt2pt);
	mpjbuf.clear(); // bug-fix
	mpjbuf.free();
      }
      catch (Exception e) {
	throw new MPIException(e);
      }
      return;
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Bsend--");
    }

    if (mpi.MPI.buffer == null) {
      throw new MPIException(" Error in MPI.Bsend: "
	  + "No buffer has been attached ");
    }

    synchronized (mpi.MPI.buffer) {

      // This follows the "model implementation" given in the MPI
      // specification, quite literally.

      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("calling clearFront ");
      }
      PendingMessage.clearFront();
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug(" capacity " + mpi.MPI.buffer.capacity());
      }
      Packer packer = datatype.getPacker();
      int messageSize = datatype.packedSize(count) + MPI.BSEND_OVERHEAD;
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("messageSize " + messageSize);
	MPI.logger.debug("packer " + packer);
      }

      int ptr;

      if (PendingMessage.tailPtr + messageSize <= mpi.MPI.buffer.capacity()) {
	// Put after queue tail
	ptr = PendingMessage.tailPtr;
      } else {
	// No space after queue tail. Try start of buffer.
	if (messageSize <= PendingMessage.headPtr) {
	  ptr = 0;
	  // ptr = PendingMessage.headPtr - messageSize ;
	} else {
	  throw new MPIException("Error in MPI.Bsend: "
	      + "No space left in buffer.");
	}
      }

      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug(" creatingBuffer ");
      }
      ByteBuffer slicedBuffer = null;
      MPI.buffer.position(ptr);
      MPI.buffer.limit(messageSize);
      slicedBuffer = MPI.buffer.slice();

      mpjbuf.Buffer mpjbuf = datatype.createWriteBuffer(slicedBuffer,
	  messageSize);

      try {
	mpjbuf.putSectionHeader(datatype.bufferType);
	packer.pack(mpjbuf, buf, offset, count);
	mpjbuf.commit();
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      PendingMessage pending = new PendingMessage();

      try {
	pending.request = new Request(
	    mpjdevComm.isend(mpjbuf, dest, tag, pt2pt));
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      pending.ptr = ptr;
      pending.len = messageSize;
      pending.mpjbuf = mpjbuf;

      synchronized (MPI.pendingRequests) {
	MPI.pendingRequests.add(pending.request);
	if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	  MPI.logger.debug("added pending " + pending.request + "to "
	      + MPI.pendingRequests);
	}
      }

      PendingMessage.add(pending);

    } // end synchronized ()()()

  } // Bsend_finishes ...

  // Unclear following class is very well factored...
  static class PendingMessage {

    static void add(PendingMessage pending) {
      if (back != null) {
	back.next = pending;
	back = pending;
	tailPtr += back.len;
      } else {
	front = pending;
	back = pending;
	tailPtr = pending.len;
	pending.next = null;
	// tailPtr = len ; (Original line)
      }

      queue_size++;

    }

    static void clearFront() {
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug(" tailPtr " + tailPtr);
	MPI.logger.debug(" headPtr " + headPtr);
      }
      PendingMessage pending = front;

      while (pending != null) {
	if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	  MPI.logger.debug("calling test on pending ");
	if (pending.request.Test() != null) {
	  // pending.mpjbuf.free(); (breaking bsend!-aamir)a

	  if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	    MPI.pendingRequests.remove(pending.request);
	    MPI.logger.debug("removed pending " + pending.request + "from "
		+ MPI.pendingRequests);
	    MPI.logger.debug(" freeing the buffer related to this ");
	  }

	  pending = pending.next;

	  if (pending != null) {
	    tailPtr = pending.tailPtr;
	  } else {
	    tailPtr = 0;
	    front = null;
	    back = null;
	    headPtr = 0;
	  }
	  queue_size--;
	} else {
	  if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	    MPI.logger.debug("message is still pending ");
	  front = pending;
	  headPtr = front.ptr;
	}
      }

      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug(" tailPtr " + tailPtr);
	MPI.logger.debug(" headPtr " + headPtr);
      }

      // can't seem to understand why are we doing this? maybe the last two
      // makes sense when this is the first pending message?
      // front = null;
      // back = null;
      // headPtr = 0;
      // tailPtr = 0;
    }

    mpi.Request request;
    mpjbuf.Buffer mpjbuf;
    int ptr, len;
    PendingMessage next;

    public static int queue_size = 0;

    static int headPtr = 0, tailPtr = 0;
    static PendingMessage front = null, back = null;

  }

  /**
   * Send in ready mode.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_RSEND</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public void Rsend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    try {
      rsend(buf, offset, count, datatype, dest, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected void rsend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag, boolean pt2pt) throws MPIException {

    /* this is MPI.PACKED case */
    if (datatype.baseType == 9) {
      mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      try {
	mpjbuf.commit();
	mpjdevComm.send(mpjbuf, dest, tag, pt2pt);
	mpjbuf.clear(); // bug-fix
	mpjbuf.free();
      }
      catch (Exception e) {
	throw new MPIException(e);
      }
      return;
    }

    Packer packer = datatype.getPacker();
    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug(" creatingBuffer ");
    }
    mpjbuf.Buffer wBuffer = datatype.createWriteBuffer(count);

    try {
      packer.pack(wBuffer, buf, offset, count);
      wBuffer.commit();
      mpjdevComm.send(wBuffer, dest, tag, pt2pt);
      wBuffer.clear(); // bug-fix
      wBuffer.free();
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug(" destroying buffer ");
      }
      BufferFactory.destroy(wBuffer.getStaticBuffer());
    }
    catch (Exception e) {
      throw new MPIException(e);
    }
  }

  /**
   * Start a standard mode, nonblocking send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ISEND</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Request Isend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    try {
      return isend(buf, offset, count, datatype, dest, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected Request isend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag, boolean pt2pt) throws MPIException {

    /* this is MPI.PACKED case */
    if (datatype.baseType == 9) {

      final mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      mpjdev.Request request = null;

      try {
	mpjbuf.commit();
	request = mpjdevComm.isend(mpjbuf, dest, tag, pt2pt);
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      request.addCompletionHandler(new mpjdev.CompletionHandler() {
	public void handleCompletion(mpjdev.Status status) {
	  try {
	    mpjbuf.clear();
	    mpjbuf.free();
	  }
	  catch (Exception e) {
	    throw new MPIException(e);
	  }
	}
      });

      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("--Isend finishes ....--" + tag);
      }
      return new mpi.Request(request);
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Isend--" + tag);
      MPI.logger.debug(" creatingBuffer ");
    }
    Packer packer = datatype.getPacker();
    final mpjbuf.Buffer wBuffer = datatype.createWriteBuffer(count);
    // System.out.println (" creatingBuffer (isend) " + wBuffer );
    mpjdev.Request request = null;
    final int t = tag;
    final int d = dest;

    try {
      packer.pack(wBuffer, buf, offset, count);
      wBuffer.commit();
      request = mpjdevComm.isend(wBuffer, dest, tag, pt2pt);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("registering completion handler");
    }
    request.addCompletionHandler(new mpjdev.CompletionHandler() {
      public void handleCompletion(mpjdev.Status status) {
	if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	  MPI.logger.debug("executing handler for tag <" + t + "> and ");
	  MPI.logger.debug(" and dest <" + d + ">");
	}
	try {
	  wBuffer.clear();
	  wBuffer.free();
	  if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	    MPI.logger.debug(" destroying buffer ");
	  BufferFactory.destroy(wBuffer.getStaticBuffer());
	  // System.out.println (" destroying buffer (isend) " + wBuffer );
	}
	catch (Exception e) {
	  throw new MPIException(e);
	}

	if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	  MPI.logger.debug("executed handler for tag <" + t + "> and ");
	  MPI.logger.debug(" and dest <" + d + ">");
	}
      }
    });

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug("--Isend finishes ....--" + tag);
    return new mpi.Request(request);
  }

  /**
   * Start a buffered mode, nonblocking send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_IBSEND</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Request Ibsend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    try {
      return ibsend(buf, offset, count, datatype, dest, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected Request ibsend(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag, boolean pt2pt) throws MPIException {

    /* this is MPI.PACKED case */
    // really its not buffering the message! ...

    if (datatype.baseType == 9) {

      final mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      mpjdev.Request request = null;

      try {
	mpjbuf.commit();
	request = mpjdevComm.isend(mpjbuf, dest, tag, pt2pt);
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      request.addCompletionHandler(new mpjdev.CompletionHandler() {
	public void handleCompletion(mpjdev.Status status) {
	  try {
	    mpjbuf.clear();
	    mpjbuf.free();
	  }
	  catch (Exception e) {
	    throw new MPIException(e);
	  }
	}
      });

      if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	MPI.logger.debug("--Isend finishes ....--" + tag);
      return new mpi.Request(request);
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug("--Ibsend--");

    if (mpi.MPI.buffer == null) {
      throw new MPIException("Error:No buffer has been attached,"
	  + "can't use Bsend");
    }

    synchronized (mpi.MPI.buffer) {

      // This follows the "model implementation" given in the MPI
      // specification, quite literally.

      PendingMessage.clearFront();

      Packer packer = datatype.getPacker();
      int messageSize = datatype.packedSize(count) + MPI.BSEND_OVERHEAD;
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("messageSize " + messageSize);
	MPI.logger.debug("packer " + packer);
      }

      int ptr;

      if (PendingMessage.tailPtr + messageSize <= mpi.MPI.buffer.capacity()) {

	// Put after queue tail
	ptr = PendingMessage.tailPtr;
	if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	  MPI.logger.debug(" message can fit into the buffer ");
	  MPI.logger.debug(" ptr " + ptr);
	  MPI.logger.debug(" PendingMessage.tailPtr " + PendingMessage.tailPtr);
	}

      } else {

	// No space after queue tail. Try start of buffer.
	// If this sufficient ..think of first message longer than total buffer
	// size ..i guess it may not work!
	if (messageSize <= PendingMessage.headPtr) {
	  ptr = 0;
	} else {
	  throw new MPIException("No space left in buffer.");
	}

      }

      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("Allocating buffer at offset " + ptr);
	MPI.logger.debug(" creatingBuffer ");
      }
      ByteBuffer slicedBuffer = null;
      MPI.buffer.position(ptr);
      MPI.buffer.limit(messageSize);
      slicedBuffer = MPI.buffer.slice();

      mpjbuf.Buffer mpjbuf = datatype.createWriteBuffer(slicedBuffer,
	  messageSize);

      try {
	mpjbuf.putSectionHeader(datatype.bufferType);
	packer.pack(mpjbuf, buf, offset, count);
	mpjbuf.commit();
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      PendingMessage pending = new PendingMessage();

      try {
	pending.request = new Request(
	    mpjdevComm.isend(mpjbuf, dest, tag, pt2pt));
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      pending.ptr = ptr;
      pending.len = messageSize;
      pending.mpjbuf = mpjbuf;
      synchronized (MPI.pendingRequests) {
	MPI.pendingRequests.add(pending.request);
      }

      PendingMessage.add(pending);
      // Request r = new Request() ;
      // r.isNull = true ;
      // return r;
      return pending.request;

    } // end synchronized ()()()

  }

  /**
   * Start a synchronous mode, nonblocking send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ISSEND</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Request Issend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    try {
      return issend(buf, offset, count, datatype, dest, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected Request issend(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag, boolean pt2pt) throws MPIException {
    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Issend-- " + tag);
    }
    /* this is MPI.PACKED case */
    if (datatype.baseType == 9) {

      final mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      mpjdev.Request request = null;

      try {
	mpjbuf.commit();
	request = mpjdevComm.issend(mpjbuf, dest, tag, pt2pt);
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      request.addCompletionHandler(new mpjdev.CompletionHandler() {
	public void handleCompletion(mpjdev.Status status) {
	  try {
	    mpjbuf.clear();
	    mpjbuf.free();
	  }
	  catch (Exception e) {
	    throw new MPIException(e);
	  }
	}
      });

      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("--Isend finishes ....--" + tag);
      }
      return new mpi.Request(request);
    }

    Packer packer = datatype.getPacker();
    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug(" creatingBuffer ");
    }
    final mpjbuf.Buffer wBuffer = datatype.createWriteBuffer(count);
    // System.out.println("createdBuffer(issend) "+wBuffer);
    mpjdev.Request request = null;

    try {
      packer.pack(wBuffer, buf, offset, count);
      wBuffer.commit();
      request = mpjdevComm.issend(wBuffer, dest, tag, pt2pt);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    request.addCompletionHandler(new mpjdev.CompletionHandler() {
      public void handleCompletion(mpjdev.Status status) {
	try {
	  wBuffer.clear();
	  wBuffer.free();
	  if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	    MPI.logger.debug(" destroying buffer ");
	    // System.out.println("destroying buffer (issend) "+wBuffer );
	  }
	  BufferFactory.destroy(wBuffer.getStaticBuffer());
	}
	catch (Exception e) {
	  throw new MPIException(e);
	}

      }
    });

    return new mpi.Request(request);

  }

  /**
   * Start a ready mode, nonblocking send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_IRSEND</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Request Irsend(Object buf, int offset, int count, Datatype datatype,
      int dest, int tag) throws MPIException {
    try {
      return irsend(buf, offset, count, datatype, dest, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected Request irsend(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag, boolean pt2pt) throws MPIException {
    /* this is MPI.PACKED case */
    if (datatype.baseType == 9) {

      final mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      mpjdev.Request request = null;

      try {
	mpjbuf.commit();
	request = mpjdevComm.isend(mpjbuf, dest, tag, pt2pt);
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      request.addCompletionHandler(new mpjdev.CompletionHandler() {
	public void handleCompletion(mpjdev.Status status) {
	  try {
	    mpjbuf.clear();
	    mpjbuf.free();
	  }
	  catch (Exception e) {
	    throw new MPIException(e);
	  }
	}
      });

      if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	MPI.logger.debug("--Isend finishes ....--" + tag);
      return new mpi.Request(request);
    }

    Packer packer = datatype.getPacker();
    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug(" creatingBuffer ");
    final mpjbuf.Buffer wBuffer = datatype.createWriteBuffer(count);
    mpjdev.Request request = null;

    try {
      packer.pack(wBuffer, buf, offset, count);
      wBuffer.commit();
      request = mpjdevComm.isend(wBuffer, dest, tag, pt2pt);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    request.addCompletionHandler(new mpjdev.CompletionHandler() {
      public void handleCompletion(mpjdev.Status status) {
	try {
	  wBuffer.clear();
	  wBuffer.free();
	  if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	    MPI.logger.debug(" destroying buffer ");
	  BufferFactory.destroy(wBuffer.getStaticBuffer());
	}
	catch (Exception e) {
	  throw new MPIException(e);
	}

      }
    });

    return new mpi.Request(request);

  }

  /**
   * Blocking receive operation.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items in receive buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> source   </tt></td>
   * <td>rank of source
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>status object
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_RECV</tt>.
   * <p>
   * The actual argument associated with <tt>buf</tt> must be one-dimensional
   * array. The value <tt>offset</tt> is a subscript in this array, defining the
   * position into which the first item of the incoming message will be copied.
   * <p>
   * If the <tt>datatype</tt> argument represents an MPI basic type, its value
   * must agree with the element type of <tt>buf</tt>---either a primitive type
   * or a reference (object) type. If the <tt>datatype</tt> argument represents
   * an MPI derived type, its <em>base type</em> must agree with the element
   * type of <tt>buf</tt>
   */

  public Status Recv(Object buf, int offset, int count, Datatype datatype,
      int source, int tag) throws MPIException {
    try {
      return recv(buf, offset, count, datatype, source, tag, true);
    }
    catch (MPIException mpie) {
      throw new MPIException(mpie);
    }
  }

  protected Status recv(Object buf, int offset, int count, Datatype datatype,
      int source, int tag, boolean pt2pt) throws MPIException {

    /* MPI.PACK will have to be re-done */
    if (datatype.baseType == 9) {
      mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      mpjdev.Status status1 = null;
      try {
	status1 = mpjdevComm.recv(mpjbuf, source, tag, pt2pt);
	mpjbuf.commit();
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      mpi.Status st = new mpi.Status(status1);
      // set status.count and status.elements to undefined?
      return st;
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Recv--" + tag);
    }

    mpjbuf.Buffer rBuffer = datatype.createReadBuffer(count);
    Packer packer = datatype.getPacker();
    mpjdev.Status status = null;
    int count_received = 0;

    try {
      status = mpjdevComm.recv(rBuffer, source, tag, pt2pt);
      rBuffer.commit();
      // count_received = rBuffer.getSectionHeader(datatype.bufferType);
      Type receivedType = rBuffer.getSectionHeader();
      // may be a check to see if we type sent is equal to what the user
      // is trying to access ..
      count_received = rBuffer.getSectionSize();
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("count_received " + count_received);
	MPI.logger.debug("count " + count);
      }
      // System.out.println("recv: unpack : count_received = "+count_received+" count = "+count+" tag = "+tag
      // );
      packer.unpack(rBuffer, count_received, buf, offset, count);
      rBuffer.clear();
      rBuffer.free();
      BufferFactory.destroy(rBuffer.getStaticBuffer());
    }
    catch (Exception e) {
      e.printStackTrace();
      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("exception " + e.getMessage());
	MPI.logger.debug("count_received " + count_received);
	MPI.logger.debug("count " + count);
      }
      throw new MPIException(e);
    }

    mpi.Status st = new mpi.Status(status);
    // brute-forcing numEls/count vals for status.

    st.numEls = count_received;

    if (datatype.size != 0) {
      int temp = st.numEls / datatype.size;
      if (temp * datatype.size == st.numEls) {
	st.count = temp;
      } else {
	st.count = MPI.UNDEFINED;
      }
    } else {
      st.count = count;
      st.numEls = MPI.UNDEFINED; // according to MPI specs, this should be
				 // zero, but we are trusting our test-cases
				 // which are passed by LAM, MPICH, and mpiJava
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Recv completed--" + tag);
    }

    return st;
  }

  /**
   * Start a nonblocking receive.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items in receive buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> source   </tt></td>
   * <td>rank of source
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_IRECV</tt>.
   * <p>
   * Further comments as for <tt>Recv</tt>.
   */
  public Request Irecv(Object buf, int offset, int count, Datatype datatype,
      int src, int tag) throws MPIException {
    try {
      return irecv(buf, offset, count, datatype, src, tag, true);
    }
    catch (MPIException mpie) {
      throw mpie;
    }
  }

  protected Request irecv(Object buf, int offset, int count, Datatype datatype,
      int src, int tag, boolean pt2pt) throws MPIException {

    if (datatype.baseType == 9) {
      final mpjbuf.Buffer mpjbuf = (mpjbuf.Buffer) buf;
      mpjdev.Request mpjdevRequest = null;
      mpjdev.Status status = new mpjdev.Status(src, tag, -1);

      try {
	mpjdevRequest = mpjdevComm.irecv(mpjbuf, src, tag, status, pt2pt);
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

      mpjdevRequest.addCompletionHandler(new mpjdev.CompletionHandler() {
	public void handleCompletion(mpjdev.Status status) {
	  /*
	   * not sure, but at this stage for MPI.PACK, it is not possible to
	   * know count and numEls. This will be known when we call MPI.Unpack(
	   * .. ) method.
	   * 
	   * status.type .. ?
	   */
	  status.count = MPI.UNDEFINED;
	  status.numEls = MPI.UNDEFINED;
	  try {
	    mpjbuf.clear();
	    mpjbuf.free();
	  }
	  catch (Exception e) {
	    throw new MPIException(e);
	  }
	}
      });

      // mpi.Status st = new mpi.Status(status1);
      // set status.count and status.elements to undefined?
      mpi.Request req = new mpi.Request(mpjdevRequest);
      return req;
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Irecv--" + tag);
    }
    /* these final variables don't look pretty here though :( */
    final mpjbuf.Buffer rBuffer = datatype.createReadBuffer(count);
    // System.out.println("createdBuffer (irecv)"+rBuffer+" src <"+src);
    final Packer packer = datatype.getPacker();
    final int c = count;
    final Object buf1 = buf;
    final int off = offset;
    final int source = src;
    final int t = tag;
    final Datatype dtype = datatype;
    mpjdev.Request request = null;
    mpjdev.Status status = new mpjdev.Status(src, tag, -1);

    try {
      request = mpjdevComm.irecv(rBuffer, src, tag, status, pt2pt);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    request.addCompletionHandler(new mpjdev.CompletionHandler() {
      public void handleCompletion(mpjdev.Status status) {

	if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	  MPI.logger.debug("executing handler for tag <" + t + "> and ");
	  MPI.logger.debug(" and src <" + source + ">");
	}
	int count_received = 0;
	try {

	  rBuffer.commit();
	  // count_received = rBuffer.getSectionHeader(dtype.bufferType);
	  Type recType = rBuffer.getSectionHeader();

	  count_received = rBuffer.getSectionSize();

	  packer.unpack(rBuffer, count_received, buf1, off, c);
	  rBuffer.clear();
	  rBuffer.free();
	  // System.out.println("destroying buffer (irecv) "+rBuffer );
	  BufferFactory.destroy(rBuffer.getStaticBuffer());
	}
	catch (Exception e) {
	  System.out.println(" tag <" + t + ">");
	  // System.out.println("the buffer was "+rBuffer);
	  throw new MPIException(e);
	}

	status.numEls = count_received;

	if (dtype.size != 0) {

	  int temp = status.numEls / dtype.size;

	  if (temp * dtype.size == status.numEls) {
	    status.count = temp;
	  } else {
	    status.count = MPI.UNDEFINED;
	  }
	}

	else {
	  status.count = c;
	  status.numEls = MPI.UNDEFINED;
	  // according to MPI specs, this should be
	  // zero, but we are trusting our test-cases
	  // which are passed by LAM, MPICH, and mpiJava
	}

	status.type = dtype.bufferType;

	/*
	 * This loop is trying to find the matching receive FIXME: TODO: This
	 * loop shoud be moved to xdev level. Xdev has all of this information
	 * available with it. On the other side, native device (natmpjdev) would
	 * set the value of status in the C MPI library code. Basically status
	 * is passed as an argument to Irecv() and when this method returns, the
	 * value of source is set.
	 */
	if (Constants.isNative == false) { // Is is only for javampjdev
	  for (int j = 0; j < mpjdevComm.group.ids.length; j++) {
	    if (mpjdevComm.group.ids[j].uuid().equals(status.srcID)) {
	      status.source = j;
	      break;
	    }
	  }
	}

	if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	  MPI.logger.debug("executed handler for tag <" + t + "> and ");
	  MPI.logger.debug(" and src <" + source + ">");
	}
      }
    });

    mpi.Request req = new mpi.Request(request);
    req.datatype = datatype;
    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Irecv Ends--" + tag);
    }
    return req;

  }

  /**
   * Check if there is an incoming message matching the pattern specified.
   * <p>
   * <table>
   * <tr>
   * <td><tt> source   </tt></td>
   * <td>rank of source
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>status object or null handle
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_IPROBE</tt>.
   * <p>
   * If such a message is currently available, a status object similar to the
   * return value of a matching <tt>Recv</tt> operation is returned. Otherwise a
   * null handle is returned.
   */
  public Status Iprobe(int source, int tag) throws MPIException {
    mpjdev.Status mpjdevStatus = null;
    mpi.Status status = null;
    try {
      mpjdevStatus = mpjdevComm.iprobe(source, tag);

      if (mpjdevStatus == null) {
	// || mpjdevStatus.type == null) { Probe does not necessarily
	// knows the type ..it knows the type in niodev ..but not
	// in mxdev ...
	return null;
      } else {
	status = new Status(mpjdevStatus);
	// System.out.println("mpjdevStatus.numEls = "+mpjdevStatus.numEls);
	// for native mode this is zero
	status.count = mpjdevStatus.numEls;
	status.numEls = status.count;
	// status.numEls = status.count * status.type.size ;
	// status.numEls is the count of number of primitive-datatypes ...
	return status;
      }
    }
    catch (Exception e) {
      throw new MPIException(e);
    }
  }

  /**
   * Wait until there is an incoming message matching the pattern specified.
   * <p>
   * <table>
   * <tr>
   * <td><tt> source   </tt></td>
   * <td>rank of source
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>status object
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_PROBE</tt>.
   * <p>
   * Returns a status object similar to the return value of a matching
   * <tt>Recv</tt> operation.
   */
  public Status Probe(int source, int tag) throws MPIException {

    mpjdev.Status mpjdevStatus = null;
    mpi.Status status = null;
    try {
      mpjdevStatus = mpjdevComm.probe(source, tag);

      if (mpjdevStatus == null) {
	// || mpjdevStatus.type == null) {
	return null;
      } else {
	status = new Status(mpjdevStatus);
	status.count = mpjdevStatus.numEls;
	status.numEls = mpjdevStatus.numEls;
	// status.numEls = status.count * status.type.size ;
	// status.numEls is 'number of primitive-elements'
	return status;
      }
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

  }

  /**
   * Returns an upper bound on the increment of <tt>position</tt> effected by
   * <tt>pack</tt>.
   * <p>
   * <table>
   * <tr>
   * <td><tt> incount  </tt></td>
   * <td>number of items in input buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in input buffer
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>upper bound on size of packed message
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_PACK_SIZE</tt>.
   * <p>
   * <em>It is an error to call this function if the base type of
   * <tt>datatype</tt> is <tt>MPI.OBJECT</tt></em>.
   */
  public int Pack_size(int incount, Datatype datatype) throws MPIException {
    return datatype.packedSize(incount);
  }

  /**
   * Packs message in send buffer <tt>inbuf</tt> into space specified in
   * <tt>outbuf</tt>.
   * <p>
   * <table>
   * <tr>
   * <td><tt> inbuf    </tt></td>
   * <td>input buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in input buffer
   * </tr>
   * <tr>
   * <td><tt> incount  </tt></td>
   * <td>number of items in input buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in input buffer
   * </tr>
   * <tr>
   * <td><tt> outbuf   </tt></td>
   * <td>output buffer
   * </tr>
   * <tr>
   * <td><tt> position </tt></td>
   * <td>initial position in output buffer
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>final position in output buffer
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_PACK</tt>.
   * <p>
   * The return value is the output value of <tt>position</tt> - the inital
   * value incremented by the number of bytes written.
   */

  public int Pack(Object inbuf, int offset, int incount, Datatype datatype,
      mpjbuf.Buffer outbuf, int position) throws MPIException {

    outbuf.setSize(position);
    Packer packer = datatype.getPacker();
    int messageSize = datatype.packedSize(incount);

    try {
      outbuf.putSectionHeader(datatype.bufferType);
      packer.pack(outbuf, inbuf, offset, incount);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    return outbuf.getSize();

  }

  /**
   * Unpacks message in receive buffer <tt>outbuf</tt> into space specified in
   * <tt>inbuf</tt>.
   * <p>
   * <table>
   * <tr>
   * <td><tt> inbuf    </tt></td>
   * <td>input buffer
   * </tr>
   * <tr>
   * <td><tt> position </tt></td>
   * <td>initial position in input buffer
   * </tr>
   * <tr>
   * <td><tt> outbuf   </tt></td>
   * <td>output buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in output buffer
   * </tr>
   * <tr>
   * <td><tt> outcount </tt></td>
   * <td>number of items in output buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in output buffer
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>final position in input buffer
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_UNPACK</tt>.
   * <p>
   * The return value is the output value of <tt>position</tt> - the inital
   * value incremented by the number of bytes read.
   */
  public int Unpack(mpjbuf.Buffer inbuf, int position, Object outbuf,
      int offset, int outcount, Datatype datatype) throws MPIException {

    int expectedSize = MPI.COMM_WORLD.Pack_size(outcount, datatype);
    inbuf.setSize(expectedSize + position);

    try {
      Packer packer = datatype.getPacker();
      // int count_received = inbuf.getSectionHeader(datatype.bufferType);
      Type recType = inbuf.getSectionHeader();
      int count_received = inbuf.getSectionSize();
      packer.unpack(inbuf, count_received, outbuf, offset, outcount);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    return inbuf.getSize();

  }

  /**
   * Creates a persistent communication request for a buffered mode send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>persistent communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_BSEND_INIT</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Prequest Bsend_init(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag) throws MPIException {
    Prequest preq = new Prequest(buf, offset, count, datatype, dest, tag,
	MPI.OP_BSEND, this);
    return preq;
  }

  /**
   * Creates a persistent communication request for a standard mode send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>persistent communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SEND_INIT</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Prequest Send_init(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag) throws MPIException {

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("-- Send_init --");
      MPI.logger.debug("buf " + buf);
      MPI.logger.debug("offset " + offset);
      MPI.logger.debug(" count" + count);
      MPI.logger.debug(" datatype " + datatype);
      MPI.logger.debug(" dest " + dest);
      MPI.logger.debug(" tag " + tag);
      MPI.logger.debug("MPI.OP_SEND " + MPI.OP_SEND);
      MPI.logger.debug(" this " + this);
    }

    Prequest preq = new Prequest(buf, offset, count, datatype, dest, tag,
	MPI.OP_SEND, this);
    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("-- Send_init ends --");
    }
    return preq;
  }

  /**
   * Creates a persistent communication request for a synchronous mode send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>persistent communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SSEND_INIT</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Prequest Ssend_init(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag) throws MPIException {
    Prequest preq = new Prequest(buf, offset, count, datatype, dest, tag,
	MPI.OP_SSEND, this);
    return preq;
  }

  /**
   * Creates a persistent communication request for a ready mode send.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>persistent communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_RSEND_INIT</tt>.
   * <p>
   * Further comments as for <tt>Send</tt>.
   */
  public Prequest Rsend_init(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag) throws MPIException {
    Prequest preq = new Prequest(buf, offset, count, datatype, dest, tag,
	MPI.OP_RSEND, this);
    return preq;
  }

  /**
   * Creates a persistent communication request for a receive operation.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items in receive buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> source   </tt></td>
   * <td>rank of source
   * </tr>
   * <tr>
   * <td><tt> tag      </tt></td>
   * <td>message tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>communication request
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_RECV_INIT</tt>.
   * <p>
   * Further comments as for <tt>Recv</tt>.
   */
  public Prequest Recv_init(Object buf, int offset, int count,
      Datatype datatype, int dest, int tag) throws MPIException {
    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("-- Recv_init --");
      MPI.logger.debug(" buf " + buf);
      MPI.logger.debug(" offset " + offset);
      MPI.logger.debug(" count" + count);
      MPI.logger.debug(" datatype " + datatype);
      MPI.logger.debug(" dest " + dest);
      MPI.logger.debug(" tag " + tag);
      MPI.logger.debug(" MPI.OP_RECV " + MPI.OP_RECV);
      MPI.logger.debug(" this " + this);
    }

    Prequest preq = new Prequest(buf, offset, count, datatype, dest, tag,
	MPI.OP_RECV, this);

    return preq;
  }

  /**
   * Execute a blocking send and receive operation.
   * <p>
   * <table>
   * <tr>
   * <td><tt> sendbuf    </tt></td>
   * <td>send buffer array
   * </tr>
   * <tr>
   * <td><tt> sendoffset </tt></td>
   * <td>initial offset in send buffer
   * </tr>
   * <tr>
   * <td><tt> sendcount  </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> sendtype   </tt></td>
   * <td>datatype of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> dest       </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> sendtag    </tt></td>
   * <td>send tag
   * </tr>
   * <tr>
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvcount  </tt></td>
   * <td>number of items in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> source     </tt></td>
   * <td>rank of source
   * </tr>
   * <tr>
   * <td><tt> recvtag    </tt></td>
   * <td>receive tag
   * </tr>
   * <tr>
   * <td><em> returns:   </em></td>
   * <td>status object
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SENDRECV</tt>.
   * <p>
   * Further comments as for <tt>Send</tt> and <tt>Recv</tt>.
   */
  public Status Sendrecv(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, int dest, int sendtag, Object recvbuf, int recvoffset,
      int recvcount, Datatype recvtype, int source, int recvtag)
      throws MPIException {
    /*
     * Request request = Isend(sendbuf, sendoffset, sendcount, sendtype, dest,
     * sendtag); Status status = Recv(recvbuf, recvoffset, recvcount, recvtype,
     * source, recvtag); request.Wait(); return status;
     */
    /*
     * final Object rbuf = recvbuf ; final int roffset = recvoffset ; final int
     * rcount = recvcount ; final Datatype rtype = recvtype ; final int s =
     * source ; final int rtag = recvtag ;
     * 
     * Thread threadA = null ; Thread threadB = null;
     * 
     * Request request = Isend(sendbuf, sendoffset, sendcount, sendtype, dest,
     * sendtag);
     * 
     * Runnable senderThreadB = new Runnable() { public void run() { Status
     * status = Recv(rbuf, roffset, rcount, rtype, s, rtag); } };
     * 
     * threadB = new Thread(senderThreadB); threadB.start(); request.Wait() ;
     * 
     * try { threadB.join(); } catch(Exception e) { e.printStackTrace() ; }
     * 
     * return new Status() ;
     */

    Status status = null;
    Request recvRequest = null;
    Request request = null;

    recvRequest = Irecv(recvbuf, recvoffset, recvcount, recvtype, source,
	recvtag);

    request = Isend(sendbuf, sendoffset, sendcount, sendtype, dest, sendtag);

    status = recvRequest.Wait();

    request.Wait();

    return status;

  }

  /**
   * Execute a blocking send and receive operation, receiving message into send
   * buffer.
   * <p>
   * <table>
   * <tr>
   * <td><tt> buf      </tt></td>
   * <td>buffer array
   * </tr>
   * <tr>
   * <td><tt> offset   </tt></td>
   * <td>initial offset in buffer
   * </tr>
   * <tr>
   * <td><tt> count    </tt></td>
   * <td>number of items to send
   * </tr>
   * <tr>
   * <td><tt> type     </tt></td>
   * <td>datatype of each item in buffer
   * </tr>
   * <tr>
   * <td><tt> dest     </tt></td>
   * <td>rank of destination
   * </tr>
   * <tr>
   * <td><tt> sendtag  </tt></td>
   * <td>send tag
   * </tr>
   * <tr>
   * <td><tt> source   </tt></td>
   * <td>rank of source
   * </tr>
   * <tr>
   * <td><tt> recvtag  </tt></td>
   * <td>receive tag
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>status object
   * </tr>
   * </table>
   * <p>
   * Further comments as for <tt>Send</tt> and <tt>Recv</tt>.
   */
  public Status Sendrecv_replace(Object buf, int offset, int count,
      Datatype datatype, int dest, int sendtag, int source, int recvtag)
      throws MPIException {
    Request request = Isend(buf, offset, count, datatype, dest, sendtag);
    Status status = Recv(buf, offset, count, datatype, source, recvtag);
    request.Wait();
    return status;
  }

  protected Status sendrecv(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, int dest, int sendtag, Object recvbuf, int recvoffset,
      int recvcount, Datatype recvtype, int source, int recvtag)
      throws MPIException {
    Request recvRequest = Irecv(recvbuf, recvoffset, recvcount, recvtype,
	source, recvtag);
    Request request = Isend(sendbuf, sendoffset, sendcount, sendtype, dest,
	sendtag);
    request.Wait();
    Status status = recvRequest.Wait();
    return status;
  }

  /**
   * Returns the type of topology associated with the communicator.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>topology type of communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TOPO_TEST</tt>.
   * <p>
   * The return value will be one of <tt>MPI.GRAPH</tt>, <tt>MPI.CART</tt> or
   * <tt>MPI.UNDEFINED</tt>.
   */
  public int Topo_test() throws MPIException {
    // -1 represents no topology ...
    return -1;
  }

  /**
   * Abort MPI.
   * <p>
   * <table>
   * <tr>
   * <td><tt> errorcode </tt></td>
   * <td>error code for Unix or POSIX environments
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ABORT</tt>.
   */
  public void Abort(int errorcode) throws MPIException {
  }

  /**
   * Duplicate this communicator.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>copy of this communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_DUP</tt>.
   * <p>
   * The new communicator is ``congruent'' to the old one, but has a different
   * context.
   */
  public Object clone() throws MPIException {
    return this;
    // this method is over-written in both Intracomm/Intercomm
  }

}
