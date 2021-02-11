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
 * File         : NativeIntracomm.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 */

package mpi;

import mpjdev.*;
import mpjbuf.*;
import java.nio.*;

public class NativeIntracomm extends PureIntracomm {

  mpjdev.natmpjdev.Intracomm nativeIntracomm = null;

  /*
   * Copy pasted these things (ENUMs for Types) from mpi/dataType.java These
   * have to be consistent with mpi/dataType.java some how have to expose them
   * in dataType so that they be accessible to other classes
   */

  final static int UNDEFINED = -1;
  final static int NULL = 0;
  final static int BYTE = 1;
  final static int CHAR = 2;
  final static int SHORT = 3;
  final static int BOOLEAN = 4;
  final static int INT = 5;
  final static int LONG = 6;
  final static int FLOAT = 7;
  final static int DOUBLE = 8;
  final static int PACKED = 9;
  // after this non primitive types start
  final static int PRIMITIVE_TYPE_RANGE_UB = 9;

  final static int LB = 10;
  final static int UB = 11;
  final static int OBJECT = 12;

  final static int SHORT2 = 3;
  final static int INT2 = 5;
  final static int LONG2 = 6;
  final static int FLOAT2 = 7;
  final static int DOUBLE2 = 8;

  NativeIntracomm() {
  }

  /**
   * Constructor used to create an intracomm
   * 
   * @param mpjdev
   *          .Comm The "obvious" way for allocating contexts is each process
   *          independently increments a "next context" variable (a static
   *          variable if you like, but local to the process). Then the
   *          processes in the group do a collective "maxval" of the candidates,
   *          and agree to accept the largest proposal. This is returned as the
   *          result of the collective context allocation, and all processes
   *          also store it in the static variable they use as a starting point
   *          next time round. This algorithm can also allocate several
   *          consecutive contexts in a single phase, which is useful here.
   **/

  NativeIntracomm(mpjdev.Comm _mpjdevComm, mpjdev.Group _group) {

    super(_mpjdevComm, _group);

    nativeIntracomm = new mpjdev.natmpjdev.Intracomm(
	(mpjdev.natmpjdev.Comm) mpjdevComm);
  }

  NativeIntracomm(mpjdev.Comm _mpjdevComm, mpi.Group _group) {

    super(_mpjdevComm, _group);

    nativeIntracomm = new mpjdev.natmpjdev.Intracomm(
	(mpjdev.natmpjdev.Comm) mpjdevComm);
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

  public static int Compare(mpi.Comm comm1, mpi.Comm comm2) throws MPIException {

    return mpjdev.natmpjdev.Intracomm.Compare(comm1.mpjdevComm,
	comm2.mpjdevComm);
  }

  /**
   * Partition the group associated with this communicator and create a new
   * communicator within each subgroup.
   * <p>
   * <table>
   * <tr>
   * <td><tt> color    </tt></td>
   * <td>control of subset assignment
   * </tr>
   * <tr>
   * <td><tt> key      </tt></td>
   * <td>control of rank assignment
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_SPLIT</tt>.
   */
  public IntracommImpl Split(int color, int key) {

    try {
      mpjdev.Comm ncomm = mpjdevComm.split(color, key);
      return new NativeIntracomm(ncomm, ncomm.group);
    }
    catch (Exception e) {
      throw new MPIException(e);
    }
  }

  /**
   * Clone the communicator This method will be called only by intracommunicator
   * .... changed the return value to Intracomm ...(instead of Object) ...
   */
  public Object clone() {

    return this.Create(this.group);
  }

  /**
   * Create a new communicator.
   * <p>
   * <table>
   * <tr>
   * <td><tt> group    </tt></td>
   * <td>group which is a subset of the group of this communicator
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_CREATE</tt>.
   */
  public IntracommImpl Create(Group group) {

    mpjdev.Comm tcomm = mpjdevComm.create(group.mpjdevGroup);

    if (tcomm == null) {
      // if I uncomment the retuen null; then mpi.group.group test case doesn't
      // work. The testcases are working so I am leaving this as is
      // System.out.println("NativeIntracomm Create - tcomm returned as  null");
      // return null;
    }
    return (new NativeIntracomm(tcomm, group.mpjdevGroup));
  }

  /**
   * Broadcast a message from the process with rank <tt>root</tt> to all
   * processes of the group.
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
   * <td>number of items in buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in buffer
   * </tr>
   * /** A call to <tt>Barrier</tt> blocks the caller until all process in the
   * group have called it.
   * <p>
   * Java binding of the MPI operation <tt>MPI_BARRIER</tt>.
   */
  public void Barrier() {

    nativeIntracomm.Barrier();

  }

  /**
   * Broadcast a message from the process with rank <tt>root</tt> to all
   * processes of the group.
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
   * <td>number of items in buffer
   * </tr>
   * <tr>
   * <td><tt> datatype </tt></td>
   * <td>datatype of each item in buffer
   * </tr>
   * <tr>
   * <td><tt> root     </tt></td>
   * <td>rank of broadcast root
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_BCST</tt>.
   */

  public void Bcast(Object buf, int offset, int count, Datatype type, int root) {

    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (type.baseType > PRIMITIVE_TYPE_RANGE_UB || type.Size() > 1) {
      super.Bcast(buf, offset, count, type, root);
      return;
    }

    int index = this.mpjdevComm.id();// Get the rank of the current process
    int size = this.mpjdevComm.size();

    int numBytes = count * type.getByteSize();
    ByteBuffer wBuffer = ByteBuffer.allocateDirect(numBytes);

    try {
      if (index == root) {
	byteBufferSetData(buf, wBuffer, 0, offset, count, type.getType());
	wBuffer.flip();
	// wBuffer.limit(numBytes);
      }
      wBuffer.limit(numBytes);

      nativeIntracomm.Bcast(wBuffer, numBytes, root);

      // FIXME: Optimization tip root shouldn't do this
      byteBufferGetData(wBuffer, buf, 0, offset, count, type.getType());
      wBuffer.clear();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return;

  }

  /**
   * Each process sends the contents of its send buffer to the root process.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvcount  </tt></td>
   * <td>number of items to receive
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> root       </tt></td>
   * <td>rank of receiving process
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GATHER</tt>.
   */
  public void Gather(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, Object recvbuf, int recvoffset, int recvcount,
      Datatype recvtype, int root) {

    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Gather(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	  recvoffset, recvcount, recvtype, root);
      return;

    }

    int index = this.mpjdevComm.id();// Get the rank of the current process
    int size = this.mpjdevComm.size();

    int numSendBytes = sendcount * sendtype.getByteSize();
    ByteBuffer wBuffer = ByteBuffer.allocateDirect(numSendBytes);

    ByteBuffer rBuffer = null;
    int numRecvBytes = -1;

    if (index == root) {
      numRecvBytes = recvcount * recvtype.getByteSize() * size;
      rBuffer = ByteBuffer.allocateDirect(numRecvBytes);
    }

    try {
      byteBufferSetData(sendbuf, wBuffer, 0, sendoffset, sendcount,
	  sendtype.getType());
      wBuffer.flip();
      wBuffer.limit(numSendBytes);

      nativeIntracomm.Gather(wBuffer, numSendBytes, rBuffer, numSendBytes,
	  root, index == root ? true : false);

      if (index == root) { // root is the source point of gather
	rBuffer.limit(numRecvBytes);
	byteBufferGetData(rBuffer, recvbuf, 0, recvoffset, recvcount * size,
	    recvtype.getType());
	rBuffer.clear();

      } // ends if root

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return;
  }

  /**
   * Extends functionality of <tt>Gather</tt> by allowing varying counts of data
   * from each process.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvcounts </tt></td>
   * <td>number of elements received from each process
   * </tr>
   * <tr>
   * <td><tt> displs     </tt></td>
   * <td>displacements at which to place incoming data
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> root       </tt></td>
   * <td>rank of receiving process
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GATHERV</tt>.
   * <p>
   * The sizes of arrays <tt>recvcounts</tt> and <tt>displs</tt> should be the
   * size of the group. Entry <em>i</em> of <tt>displs</tt> specifies the
   * displacement relative to element <tt>recvoffset</tt> of <tt>recvbuf</tt> at
   * which to place incoming data.
   */
  public void Gatherv(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, Object recvbuf, int recvoffset, int[] recvcount,
      int[] displs, Datatype recvtype, int root) {

    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Gatherv(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	  recvoffset, recvcount, displs, recvtype, root);
      return;

    }
    super.Gatherv(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	recvoffset, recvcount, displs, recvtype, root);
  }

  /**
   * Inverse of the operation <tt>Gather</tt>.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvcount  </tt></td>
   * <td>number of items to receive
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> root       </tt></td>
   * <td>rank of sending process
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SCATTER</tt>.
   */

  public void Scatter(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, Object recvbuf, int recvoffset, int recvcount,
      Datatype recvtype, int root) {

    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Scatter(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	  recvoffset, recvcount, recvtype, root);
      return;

    }

    /*
     * super.Scatter(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
     * recvoffset, recvcount, recvtype, root);
     */
    // System.out.println("Native Scatter");
    int index = this.mpjdevComm.id();// Get the rank of the current process
    int size = this.mpjdevComm.size();

    ByteBuffer wBuffer = null;
    int numSendBytes = -1;

    if (index == root) {
      numSendBytes = sendcount * sendtype.getByteSize() * size;
      wBuffer = ByteBuffer.allocateDirect(numSendBytes);
    }

    ByteBuffer rBuffer = null;
    int numRecvBytes = -1;

    numRecvBytes = recvcount * recvtype.getByteSize();
    rBuffer = ByteBuffer.allocateDirect(numRecvBytes);

    try {
      if (index == root) {
	byteBufferSetData(sendbuf, wBuffer, 0, sendoffset, sendcount * size,
	    sendtype.getType());
	wBuffer.flip();
	wBuffer.limit(numSendBytes);
      }

      nativeIntracomm.Scatter(wBuffer, numRecvBytes, rBuffer, numRecvBytes,
	  root);

      rBuffer.limit(numRecvBytes);
      byteBufferGetData(rBuffer, recvbuf, 0, recvoffset, recvcount,
	  recvtype.getType());
      rBuffer.clear();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return;

  }

  /**
   * Inverse of the operation <tt>Gatherv</tt>.
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
   * <td><tt> sendcounts </tt></td>
   * <td>number of items sent to each process
   * </tr>
   * <tr>
   * <td><tt> displs     </tt></td>
   * <td>displacements from which to take outgoing data
   * </tr>
   * <tr>
   * <td><tt> sendtype   </tt></td>
   * <td>datatype of each item in send buffer
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
   * <td>number of items to receive
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * <tr>
   * <td><tt> root       </tt></td>
   * <td>rank of sending process
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SCATTERV</tt>.
   */
  public void Scatterv(Object sendbuf, int sendoffset, int[] sendcount,
      int[] displs, Datatype sendtype, Object recvbuf, int recvoffset,
      int recvcount, Datatype recvtype, int root) {
    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Scatterv(sendbuf, sendoffset, sendcount, displs, sendtype, recvbuf,
	  recvoffset, recvcount, recvtype, root);
      return;

    }
    super.Scatterv(sendbuf, sendoffset, sendcount, displs, sendtype, recvbuf,
	recvoffset, recvcount, recvtype, root);

  }

  /**
   * Similar to <tt>Gather</tt>, but all processes receive the result.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvcount  </tt></td>
   * <td>number of items to receive
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLGATHER</tt>.
   */

  public void Allgather(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, Object recvbuf, int recvoffset, int recvcount,
      Datatype recvtype) {

    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Allgather(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	  recvoffset, recvcount, recvtype);
      return;

    }
    int index = this.mpjdevComm.id();// Get the rank of the current process
    int size = this.mpjdevComm.size();

    ByteBuffer wBuffer = null;
    int numSendBytes = -1;

    numSendBytes = sendcount * sendtype.getByteSize();
    wBuffer = ByteBuffer.allocateDirect(numSendBytes);

    ByteBuffer rBuffer = null;
    int numRecvBytes = -1;

    numRecvBytes = recvcount * recvtype.getByteSize() * size;
    rBuffer = ByteBuffer.allocateDirect(numRecvBytes);

    try {

      byteBufferSetData(sendbuf, wBuffer, 0, sendoffset, sendcount,
	  sendtype.getType());
      wBuffer.flip();
      wBuffer.limit(numSendBytes);

      nativeIntracomm.Allgather(wBuffer, numSendBytes, rBuffer, numSendBytes);

      rBuffer.limit(numRecvBytes);
      byteBufferGetData(rBuffer, recvbuf, 0, recvoffset, recvcount * size,
	  recvtype.getType());
      rBuffer.clear();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return;

  }

  /**
   * Similar to <tt>Gatherv</tt>, but all processes receive the result.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvcounts </tt></td>
   * <td>number of elements received from each process
   * </tr>
   * <tr>
   * <td><tt> displs     </tt></td>
   * <td>displacements at which to place incoming data
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLGATHERV</tt>.
   */
  public void Allgatherv(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, Object recvbuf, int recvoffset, int[] recvcount,
      int[] displs, Datatype recvtype) {
    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Allgatherv(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	  recvoffset, recvcount, displs, recvtype);
      return;

    }
    super.Allgatherv(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	recvoffset, recvcount, displs, recvtype);

  }

  /**
   * Extension of <tt>Allgather</tt> to the case where each process sends
   * distinct data to each of the receivers.
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
   * <td>number of items sent to each process
   * </tr>
   * <tr>
   * <td><tt> sendtype   </tt></td>
   * <td>datatype send buffer items
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
   * <td>number of items received from any process
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of receive buffer items
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLTOALL</tt>.
   */

  public void Alltoall(Object sendbuf, int sendoffset, int sendcount,
      Datatype sendtype, Object recvbuf, int recvoffset, int recvcount,
      Datatype recvtype) {

    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Alltoall(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	  recvoffset, recvcount, recvtype);
      return;

    }
    // int index = this.mpjdevComm.id();// Get the rank of the current
    // process

    int size = this.mpjdevComm.size();

    ByteBuffer wBuffer = null;
    int numSendBytes = -1;

    numSendBytes = sendcount * sendtype.getByteSize() * size;
    wBuffer = ByteBuffer.allocateDirect(numSendBytes);

    ByteBuffer rBuffer = null;
    int numRecvBytes = -1;

    numRecvBytes = recvcount * recvtype.getByteSize() * size;
    rBuffer = ByteBuffer.allocateDirect(numRecvBytes);

    try {

      byteBufferSetData(sendbuf, wBuffer, 0, sendoffset, sendcount * size,
	  sendtype.getType());
      wBuffer.flip();
      wBuffer.limit(numSendBytes);

      numSendBytes = sendcount * sendtype.getByteSize();

      nativeIntracomm.Alltoall(wBuffer, numSendBytes, rBuffer, numSendBytes);

      rBuffer.limit(numRecvBytes);
      byteBufferGetData(rBuffer, recvbuf, 0, recvoffset, recvcount * size,
	  recvtype.getType());
      rBuffer.clear();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return;

  }

  /**
   * Adds flexibility to <tt>Alltoall</tt>: location of data for send is
   * specified by <tt>sdispls</tt> and location to place data on receive side is
   * specified by <tt>rdispls</tt>.
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
   * <td><tt> sendcounts </tt></td>
   * <td>number of items sent to each process
   * </tr>
   * <tr>
   * <td><tt> sdispls    </tt></td>
   * <td>displacements from which to take outgoing data
   * </tr>
   * <tr>
   * <td><tt> sendtype   </tt></td>
   * <td>datatype send buffer items
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
   * <td><tt> recvcounts </tt></td>
   * <td>number of elements received from each process
   * <tr>
   * <td><tt> rdispls    </tt></td>
   * <td>displacements at which to place incoming data
   * </tr>
   * <tr>
   * <td><tt> recvtype   </tt></td>
   * <td>datatype of each item in receive buffer
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLTOALLV</tt>.
   */

  public void Alltoallv(Object sendbuf, int sendoffset, int[] sendcount,
      int[] sdispls, Datatype sendtype, Object recvbuf, int recvoffset,
      int[] recvcount, int[] rdispls, Datatype recvtype) {
    // for UB, LB, Object and Derived Datatypes (Vector, Struct etc when
    // size>1)
    if (sendtype.baseType > PRIMITIVE_TYPE_RANGE_UB || sendtype.Size() > 1) {
      super.Alltoallv(sendbuf, sendoffset, sendcount, sdispls, sendtype,
	  recvbuf, recvoffset, recvcount, rdispls, recvtype);
      return;

    }
    int index = this.mpjdevComm.id();// Get the rank of the current process

    int size = this.mpjdevComm.size();

    ByteBuffer wBuffer = null;
    int numSendBytes = -1;
    int totalSendCount = 0;
    int totalRecvCount = 0;

    for (int i = 0; i < size; i++) {
      totalSendCount += sendcount[i];
    }
    numSendBytes = totalSendCount * sendtype.getByteSize();
    wBuffer = ByteBuffer.allocateDirect(numSendBytes);

    ByteBuffer rBuffer = null;
    int numRecvBytes = -1;

    for (int i = 0; i < size; i++) {
      totalRecvCount += recvcount[i];
    }
    numRecvBytes = totalRecvCount * recvtype.getByteSize();
    rBuffer = ByteBuffer.allocateDirect(numRecvBytes);

    int sendcountBytes[] = new int[sendcount.length];// or size?
    int sdisplsBytes[] = new int[sdispls.length];// or size?
    int recvcountBytes[] = new int[recvcount.length];// or size?
    int rdisplsBytes[] = new int[rdispls.length];// or size?

    // convert into bytes
    for (int i = 0; i < sendcountBytes.length; i++) {
      sendcountBytes[i] = sendcount[i] * sendtype.getByteSize();
    }
    sdisplsBytes[0] = 0;
    for (int i = 1; i < sdisplsBytes.length; i++) {
      sdisplsBytes[i] = sendcountBytes[i - 1] + sdisplsBytes[i - 1];
    }

    for (int i = 0; i < recvcountBytes.length; i++) {
      recvcountBytes[i] = recvcount[i] * recvtype.getByteSize();
    }
    rdisplsBytes[0] = 0;
    for (int i = 1; i < rdisplsBytes.length; i++) {
      rdisplsBytes[i] = recvcountBytes[i - 1] + rdisplsBytes[i - 1];
    }

    try {

      for (int i = 0; i < size; i++)
	byteBufferSetData(sendbuf, wBuffer, 0, sdispls[i], sendcount[i],
	    sendtype.getType());

      wBuffer.flip();
      wBuffer.limit(numSendBytes);

      nativeIntracomm.Alltoallv(wBuffer, sendcountBytes, sdisplsBytes, rBuffer,
	  recvcountBytes, rdisplsBytes);

      rBuffer.limit(numRecvBytes);
      for (int i = 0; i < size; i++)
	byteBufferGetData(rBuffer, recvbuf, 0, rdispls[i], recvcount[i],
	    recvtype.getType());

      rBuffer.clear();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return;

  }

  /**
   * Combine elements in input buffer of each process using the reduce
   * operation, and return the combined value in the output buffer of the root
   * process.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> count      </tt></td>
   * <td>number of items in send buffer
   * </tr>
   * <tr>
   * <td><tt> datatype   </tt></td>
   * <td>data type of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> op         </tt></td>
   * <td>reduce operation
   * </tr>
   * <tr>
   * <td><tt> root       </tt></td>
   * <td>rank of root process
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_REDUCE</tt>.
   * <p>
   * The predefined operations are available in Java as <tt>MPI.MAX</tt>,
   * <tt>MPI.MIN</tt>, <tt>MPI.SUM</tt>, <tt>MPI.PROD</tt>, <tt>MPI.LAND</tt>,
   * <tt>MPI.BAND</tt>, <tt>MPI.LOR</tt>, <tt>MPI.BOR</tt>, <tt>MPI.LXOR</tt>,
   * <tt>MPI.BXOR</tt>, <tt>MPI.MINLOC</tt> and <tt>MPI.MAXLOC</tt>.
   */

  public void Reduce(Object sendbuf, int sendoffset, Object recvbuf,
      int recvoffset, int count, Datatype type, Op op, int root) {

    // for UB, LB, Object and Vector
    if (type.baseType > PRIMITIVE_TYPE_RANGE_UB || type.Size() > 1
	|| op.opCode > 10) { // 10 -- because MAXLOC MINLOC not yet
			     // implemented
      super.Reduce(sendbuf, sendoffset, recvbuf, recvoffset, count, type, op,
	  root);
      return;

    }

    int index = this.mpjdevComm.id();// Get the rank of the current process

    // int size = this.mpjdevComm.size();

    ByteBuffer rBuffer = null;

    int numRecvBytes = -1;
    int recvcount = count; // naive case, TODO: fix this for MAXLOC etc?
    numRecvBytes = recvcount * type.getByteSize();
    rBuffer = ByteBuffer.allocateDirect(numRecvBytes);
    rBuffer.limit(numRecvBytes);
    try {

      nativeIntracomm.Reduce(sendbuf, rBuffer, count, type, op, root);

      if (index == root) { // root is the source point of gather

	// this should copy the values compatably
	byteBufferGetData(rBuffer, recvbuf, 0, recvoffset, recvcount,
	    type.getType());
	rBuffer.clear();

      } // ends if root

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return;

  }

  /**
   * Same as <tt>reduce</tt> except that the result appears in receive buffer of
   * all process in the group.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> count      </tt></td>
   * <td>number of items in send buffer
   * </tr>
   * <tr>
   * <td><tt> datatype   </tt></td>
   * <td>data type of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> op         </tt></td>
   * <td>reduce operation
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLREDUCE</tt>.
   */

  public void Allreduce(Object sendbuf, int sendoffset, Object recvbuf,
      int recvoffset, int count, Datatype datatype, Op op) {
    // for UB, LB, Object and Vector
    if (datatype.baseType > PRIMITIVE_TYPE_RANGE_UB || datatype.Size() > 1

    || op.opCode > 10) { // 10 -- because MAXLOC MINLOC not yet
			 // implemented
			 // anyways for MAXLOC and MINLOC we use
			 // MPI.INT2
			 // and its contiguous type so for that
			 // Size()>1
      super.Allreduce(sendbuf, sendoffset, recvbuf, recvoffset, count,
	  datatype, op);
      return;

    }

    super.Allreduce(sendbuf, sendoffset, recvbuf, recvoffset, count, datatype,
	op);

  }

  /**
   * Combine elements in input buffer of each process using the reduce
   * operation, and scatter the combined values over the output buffers of the
   * processes.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> recvcounts </tt></td>
   * <td>numbers of result elements distributed to each process
   * </tr>
   * <tr>
   * <td><tt> datatype   </tt></td>
   * <td>data type of each item in send buffer
   * </tr>
   * <tr>
   * <td><tt> op         </tt></td>
   * <td>reduce operation
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_REDUCE_SCATTER</tt>.
   */
  public void Reduce_scatter(Object sendbuf, int sendoffset, Object recvbuf,
      int recvoffset, int[] recvcounts, Datatype datatype, Op op) {
    // for UB, LB, Object and Vector
    if (datatype.baseType > PRIMITIVE_TYPE_RANGE_UB || datatype.Size() > 1) {
      super.Reduce_scatter(sendbuf, sendoffset, recvbuf, recvoffset,
	  recvcounts, datatype, op);
      return;

    }
    super.Reduce_scatter(sendbuf, sendoffset, recvbuf, recvoffset, recvcounts,
	datatype, op);

  }

  /**
   * Perform a prefix reduction on data distributed across the group.
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
   * <td><tt> recvbuf    </tt></td>
   * <td>receive buffer array
   * </tr>
   * <tr>
   * <td><tt> recvoffset </tt></td>
   * <td>initial offset in receive buffer
   * </tr>
   * <tr>
   * <td><tt> count      </tt></td>
   * <td>number of items in input buffer
   * </tr>
   * <tr>
   * <td><tt> datatype   </tt></td>
   * <td>data type of each item in input buffer
   * </tr>
   * <tr>
   * <td><tt> op         </tt></td>
   * <td>reduce operation
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SCAN</tt>.
   */

  public void Scan(Object sendbuf, int sendoffset, Object recvbuf,
      int recvoffset, int recvcounts, Datatype datatype, Op op)
      throws MPIException {
    // for UB, LB, Object and Vector
    if (datatype.baseType > PRIMITIVE_TYPE_RANGE_UB || datatype.Size() > 1) {
      super.Scan(sendbuf, sendoffset, recvbuf, recvoffset, recvcounts,
	  datatype, op);

      return;

    }
    super.Scan(sendbuf, sendoffset, recvbuf, recvoffset, recvcounts, datatype,
	op);

  }

  /*
   * Utility and helping functions
   */

  private void byteBufferSetData(Object src, ByteBuffer dest, int destOffset,
      int offset, int count, int typeCode) {

    if (typeCode == this.BYTE) {
      dest.position(destOffset);
      dest.put((byte[]) src, offset, count);
    } else if (typeCode == this.CHAR) {
      dest.position(destOffset);
      CharBuffer CharBuffer = dest.asCharBuffer();
      CharBuffer.put((char[]) src, offset, count);
    } else if (typeCode == this.SHORT) {
      dest.position(destOffset);
      ShortBuffer ShortBuffer = dest.asShortBuffer();
      ShortBuffer.put((short[]) src, offset, count);
    } else if (typeCode == this.BOOLEAN) {
      dest.position(destOffset);
      boolean srcB[] = (boolean[]) src;
      for (int i = 0; i < count; i++) {
	dest.put((byte) (srcB[i + offset] ? 1 : 0)); // boolean
      }

    } else if (typeCode == this.INT) {
      dest.position(destOffset);
      IntBuffer IntBuffer = dest.asIntBuffer();
      IntBuffer.put((int[]) src, offset, count);
    } else if (typeCode == this.LONG) {
      dest.position(destOffset);
      LongBuffer LongBuffer = dest.asLongBuffer();
      LongBuffer.put((long[]) src, offset, count);
    } else if (typeCode == this.FLOAT) {
      dest.position(destOffset);
      FloatBuffer FloatBuffer = dest.asFloatBuffer();
      FloatBuffer.put((float[]) src, offset, count);
    } else if (typeCode == this.DOUBLE) {
      dest.position(destOffset);
      DoubleBuffer DoubleBuffer = dest.asDoubleBuffer();
      DoubleBuffer.put((double[]) src, offset, count);
    }

  }

  private void byteBufferGetData(ByteBuffer src, Object dest, int srcOffset,
      int offset, int count, int typeCode) {

    if (typeCode == this.BYTE) {
      src.position(srcOffset);
      src.get((byte[]) dest, offset, count);
    } else if (typeCode == this.CHAR) {
      src.position(srcOffset);
      CharBuffer CharBuffer = src.asCharBuffer();
      CharBuffer.get((char[]) dest, offset, count);
    } else if (typeCode == this.SHORT) {
      src.position(srcOffset);
      ShortBuffer ShortBuffer = src.asShortBuffer();
      ShortBuffer.get((short[]) dest, offset, count);
    } else if (typeCode == this.BOOLEAN) {
      src.position(srcOffset);
      boolean destB[] = (boolean[]) dest;

      for (int i = 0; i < count; i++) {
	destB[i + offset] = (src.get() == 1); // boolean
      }

    } else if (typeCode == this.INT) {
      src.position(srcOffset);
      IntBuffer IntBuffer = src.asIntBuffer();
      IntBuffer.get((int[]) dest, offset, count);
    } else if (typeCode == this.LONG) {
      src.position(srcOffset);
      LongBuffer LongBuffer = src.asLongBuffer();
      LongBuffer.get((long[]) dest, offset, count);
    } else if (typeCode == this.FLOAT) {
      src.position(srcOffset);
      FloatBuffer FloatBuffer = src.asFloatBuffer();
      FloatBuffer.get((float[]) dest, offset, count);
    } else if (typeCode == this.DOUBLE) {
      src.position(srcOffset);
      DoubleBuffer DoubleBuffer = src.asDoubleBuffer();
      DoubleBuffer.get((double[]) dest, offset, count);
    }

  }

}
