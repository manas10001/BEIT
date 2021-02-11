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
 * File         : natmpjdev.Intracomm.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */
package mpjdev.natmpjdev;

import mpjbuf.*;
import mpi.*;
import java.nio.*;

public class Intracomm {

  private static native void init();

  static {
    init();
  }
  mpjdev.natmpjdev.Comm mpjdevNativeComm = null;

  public Intracomm(mpjdev.natmpjdev.Comm _mpjdevComm) {
    this.mpjdevNativeComm = _mpjdevComm;
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

  public static int Compare(mpjdev.Comm comm1, mpjdev.Comm comm2)
      throws MPIException { //it throws mpi.MPIException? 

    return nativeCompare(((mpjdev.natmpjdev.Comm) comm1).getHandle(),
	((mpjdev.natmpjdev.Comm) comm2).getHandle());
  }

  private static native int nativeCompare(long handleComm1, long handleComm2);

  /**
   * Clone the communicator This method will be called only by intracommunicator
   * .... changed the return value to Intracomm ...(instead of Object) ...
   */
  public Object clone() {
    System.out
	.println("mpjdev.natmpjdev.Intracomm: clone yet to be implemented");
    return null;
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
    nativeBarrier(mpjdevNativeComm.handle);
  }

  /*
   * int MPI_Barrier( MPI_Comm comm )
   */
  private native void nativeBarrier(long commHandle);

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

  public void Bcast(Object buf, int size, int root) {

    nativeBcast(mpjdevNativeComm.handle, (ByteBuffer) buf, size, root);

  }

  /*
   * int MPI_Bcast( void *buffer, int count, MPI_Datatype datatype, int root,
   * MPI_Comm comm )
   */
  private native void nativeBcast(long commHandle, ByteBuffer buffer,
      int count, int root);

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
  public void Gather(Object sendbuf, int sendSize, Object recvbuf,
      int recvSize, int root, boolean isRoot) {

    // System.out.println("mpjdev.natmpjdev.Intracomm: Gather yet to be implemented");
    nativeGather(mpjdevNativeComm.handle, (ByteBuffer) sendbuf, sendSize,
	(ByteBuffer) recvbuf, recvSize, root, isRoot);
  }

  /*
   * int MPI_Gather(void *sendbuf, int sendcnt, MPI_Datatype sendtype, void
   * *recvbuf, int recvcnt, MPI_Datatype recvtype, int root, MPI_Comm comm)
   */
  private native void nativeGather(long commHandle, ByteBuffer sendbuf,
      int sendcount, ByteBuffer recvbuf, int recvcount, int root, boolean isRoot);

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

    System.out
	.println("mpjdev.natmpjdev.Intracomm: Gatherv yet to be implemented but don't know how -_-");

  }

  /*
   * 
   * int MPI_Gatherv(void *sendbuf, int sendcnt, MPI_Datatype sendtype, void
   * *recvbuf, int *recvcnts, int *displs, MPI_Datatype recvtype, int root,
   * MPI_Comm comm)
   */
  private native void nativeGatherv(long commHandle, mpjbuf.Buffer sendbuf,
      int sendcount, mpjbuf.Buffer recvbuf, int[] recvcount, int[] displs,
      int root);

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

  public void Scatter(Object sendbuf, int sendSize, Object recvbuf,
      int recvSize, int root) {

    nativeScatter(mpjdevNativeComm.handle, (ByteBuffer) sendbuf, sendSize,
	(ByteBuffer) recvbuf, recvSize, root);
  }

  /*
   * MPI_Scatter(void* send_data, int send_count, MPI_Datatype send_datatype,
   * void* recv_data, int recv_count, MPI_Datatype recv_datatype, int root,
   * MPI_Comm communicator)
   */
  private native void nativeScatter(long commHandle, ByteBuffer sendbuf,
      int sendcount, ByteBuffer recvbuf, int recvcount, int root);

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

    System.out
	.println("mpjdev.natmpjdev.Intracomm: Scatterv yet to be implemented but don't know how -_-");

  }

  /*
   * int MPI_Scatterv( void *sendbuf, int *sendcnts, int *displs, MPI_Datatype
   * sendtype, void *recvbuf, int recvcnt, MPI_Datatype recvtype, int root,
   * MPI_Comm comm)
   */
  private native void nativeScatterv(long commHandle, ByteBuffer sendbuf,
      int[] sendcount, int[] displs, ByteBuffer recvbuf, int recvcount, int root);

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

  public void Allgather(Object sendbuf, int sendSize, Object recvbuf,
      int recvSize) {

    nativeAllgather(mpjdevNativeComm.handle, (ByteBuffer) sendbuf, sendSize,
	(ByteBuffer) recvbuf, recvSize);
  }

  /*
   * 
   * int MPI_Allgather(void *sendbuf, int sendcount, MPI_Datatype sendtype, void
   * *recvbuf, int recvcount, MPI_Datatype recvtype, MPI_Comm comm)
   */
  private native void nativeAllgather(long commHandle, ByteBuffer sendbuf,
      int sendcount, ByteBuffer recvbuf, int recvcount);

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

    System.out
	.println("mpjdev.natmpjdev.Intracomm: Allgatherv yet to be implemented but don't know how -_-");
  }

  /*
   * int MPI_Allgatherv(void *sendbuf, int sendcount, MPI_Datatype sendtype,
   * void *recvbuf, int *recvcounts, int *displs, MPI_Datatype recvtype,
   * MPI_Comm comm)
   */

  private native void nativeAllgatherv(long commHandle, ByteBuffer sendbuf,
      int sendcount, ByteBuffer recvbuf, int[] recvcount, int[] displs);

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

  public void Alltoall(Object sendbuf, int sendSize, Object recvbuf,
      int recvSize) {

    nativeAlltoall(mpjdevNativeComm.handle, (ByteBuffer) sendbuf, sendSize,
	(ByteBuffer) recvbuf, recvSize);

  }

  /*
   * int MPI_Alltoall(void *sendbuf, int sendcount, MPI_Datatype sendtype, void
   * *recvbuf, int recvcount, MPI_Datatype recvtype, MPI_Comm comm)
   */
  private native void nativeAlltoall(long commHandle, ByteBuffer sendbuf,
      int sendcount, ByteBuffer recvbuf, int recvcount);

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
  public void Alltoallv(Object sendbuf, int[] sendcount, int[] sdispls,
      Object recvbuf, int[] recvcount, int[] rdispls) {

    nativeAlltoallv(mpjdevNativeComm.handle, (ByteBuffer) sendbuf, sendcount,
	sdispls, (ByteBuffer) recvbuf, recvcount, rdispls);
  }

  /*
   * int MPI_Alltoallv(void *sendbuf, int *sendcnts, int *sdispls, MPI_Datatype
   * sendtype, void *recvbuf, int *recvcnts, int *rdispls, MPI_Datatype
   * recvtype, MPI_Comm comm)
   */
  private native void nativeAlltoallv(long commHandle, ByteBuffer sendbuf,
      int[] sendcount, int[] sdispls, ByteBuffer recvbuf, int[] recvcount,
      int[] rdispls);

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

  public void Reduce(Object sendbuf, Object recvbuf, int count, Datatype type,
      Op op, int root) {

    nativeReduce(mpjdevNativeComm.handle, sendbuf, (ByteBuffer) recvbuf, count,
	type.baseType, op.opCode, root);

  }

  /*
   * int MPI_Reduce(void *sendbuf, void *recvbuf, int count, MPI_Datatype
   * datatype, MPI_Op op, int root, MPI_Comm comm)
   */

  private native void nativeReduce(long commHandle, Object sendbuf,
      ByteBuffer recvbuf, int count, int datatype, int op, int root);

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
    System.out
	.println("mpjdev.natmpjdev.Intracomm: Allreduce yet to be implemented but don't know how -_-");
  }

  /*
   * int MPI_Allreduce(void* sendbuf, void* recvbuf, int count, MPI_Datatype
   * datatype, MPI_Op op, MPI_Comm comm)
   */
  private native void nativeAllreduce(long commHandle, ByteBuffer sendbuf,
      ByteBuffer recvbuf,
      /* int recvoffset, */
      int count,
      /* Datatype datatype, */
      Op op);

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

    System.out
	.println("mpjdev.natmpjdev.Intracomm: Reduce_scatter yet to be implemented but don't know how -_-");
  }

  /*
   * int MPI_Reduce_scatter(void* sendbuf, void* recvbuf, int *recvcounts,
   * MPI_Datatype datatype, MPI_Op op, MPI_Comm comm)
   */
  private native void nativeReduce_scatter(long commHandle, ByteBuffer sendbuf,
      ByteBuffer recvbuf,
      /* int recvoffset, */
      int[] recvcounts,
      /* Datatype datatype, */
      Op op);

  // TODO: As we are using the MPJ Express top layer for Topologies we at the
  // moment do not need Cartcomm and Graphcomm creation here.
  /**
   * Create a Cartesian topology communicator whose group is a subset of the
   * group of this communicator.
   * <p>
   * <table>
   * <tr>
   * <td><tt> dims     </tt></td>
   * <td>the number of processes in each dimension
   * </tr>
   * <tr>
   * <td><tt> periods  </tt></td>
   * <td> <tt>true</tt> if grid is periodic, <tt>false</tt> if not, in each
   * dimension
   * </tr>
   * <tr>
   * <td><tt> reorder  </tt></td>
   * <td> <tt>true</tt> if ranking may be reordered, <tt>false</tt> if not
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new Cartesian topology communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_CART_CREATE</tt>.
   * <p>
   * The number of dimensions of the Cartesian grid is taken to be the size of
   * the <tt>dims</tt> argument. The array <tt>periods</tt> must be the same
   * size.
   */
  public Cartcomm Create_cart(int[] dims, boolean[] periods, boolean reorder) {

    System.out
	.println("mpjdev.natmpjdev.Intracomm: Create_cart yet to be implemented but don't know how -_-");
    return null;
  }

  /**
   * Create a graph topology communicator whose group is a subset of the group
   * of this communicator.
   * <p>
   * <table>
   * <tr>
   * <td><tt> index    </tt></td>
   * <td>node degrees
   * </tr>
   * <tr>
   * <td><tt> edges    </tt></td>
   * <td>graph edges
   * </tr>
   * <tr>
   * <td><tt> reorder  </tt></td>
   * <td> <tt>true</tt> if ranking may be reordered, <tt>false</tt> if not
   * </tr>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>new graph topology communicator
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GRAPH_CREATE</tt>.
   * <p>
   * The number of nodes in the graph, <em>nnodes</em>, is taken to be size of
   * the <tt>index</tt> argument. The size of array <tt>edges</tt> must be
   * <tt>index [nnodes} - 1]</tt>.
   */
  public Graphcomm Create_graph(int[] index, int[] edges, boolean reorder) {

    System.out
	.println("mpjdev.natmpjdev.Intracomm: Create_graph yet to be implemented but don't know how -_-");
    return null;
  }

} // ends class mpjdev.natmpjdev.Intracomm.java
