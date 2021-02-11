/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)
   6. Bibrak Qamar (2013 - 2014)

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
 * File         : Intracomm.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.30 $
 * Updated      : $Date: 2014/03/11 13:13:10 PKT $
 */

package mpi;

import mpjdev.*;
import mpjbuf.*;

public class Intracomm extends Comm {

  IntracommImpl intracommImpl = null;

  Intracomm() {
  }

  /**
   * Constructor used to create an intracomm
   * 
   * @param mpjdev
   *          .Comm mpjdev.Group
   * 
   **/

  Intracomm(mpjdev.Comm mpjdevComm, mpjdev.Group _group) throws MPIException {
    this.mpjdevComm = mpjdevComm;
    this.group = new Group(_group);

    if (Constants.isNative) {
      intracommImpl = new NativeIntracomm(mpjdevComm, this.group);
    } else {
      intracommImpl = new PureIntracomm(mpjdevComm, this.group);
    }

  }

  /**
   * Constructor used to create an intracomm
   * 
   * @param IntracommImpl
   * 
   **/
  Intracomm(IntracommImpl _IntracommImpl) throws MPIException {

    // set the IntracommImpl
    intracommImpl = _IntracommImpl;
    this.mpjdevComm = intracommImpl.mpjdevComm;
    this.group = intracommImpl.group;
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

    // return intracommImpl.Compare(comm1, comm2);
    if (Constants.isNative) {
      // System.out.println("calling nativeIntracomm compare ");
      // in case of native
      return mpi.NativeIntracomm.Compare(comm1, comm2);
    } else {
      return mpi.Comm.Compare(comm1, comm2);
    }

    // return IntracommImpl.Compare(comm1, comm2);
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
  public Intracomm Split(int color, int key) throws MPIException {

    IntracommImpl nIntracommImpl = intracommImpl.Split(color, key);

    return new Intracomm(nIntracommImpl);
  }

  /**
   * Clone the communicator This method will be called only by intracommunicator
   * .... changed the return value to Intracomm ...(instead of Object) ...
   */
  public Object clone() throws MPIException {

    IntracommImpl nIntracommImpl = (IntracommImpl) intracommImpl.clone();
    return (Object) new Intracomm(nIntracommImpl);
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

  public Intracomm Create(Group group) throws MPIException {

    IntracommImpl nIntracommImpl = intracommImpl.Create(group);
    return new Intracomm(nIntracommImpl);

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
  public void Barrier() throws MPIException {
    intracommImpl.Barrier();
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

  public void Bcast(Object buf, int offset, int count, Datatype type, int root)
      throws MPIException {

    intracommImpl.Bcast(buf, offset, count, type, root);

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
      Datatype recvtype, int root) throws MPIException {

    intracommImpl.Gather(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	recvoffset, recvcount, recvtype, root);

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
      int[] displs, Datatype recvtype, int root) throws MPIException {

    intracommImpl.Gatherv(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
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
      Datatype recvtype, int root) throws MPIException {

    intracommImpl.Scatter(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	recvoffset, recvcount, recvtype, root);

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
      int recvcount, Datatype recvtype, int root) throws MPIException {

    intracommImpl.Scatterv(sendbuf, sendoffset, sendcount, displs, sendtype,
	recvbuf, recvoffset, recvcount, recvtype, root);

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
      Datatype recvtype) throws MPIException {

    intracommImpl.Allgather(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	recvoffset, recvcount, recvtype);

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
      int[] displs, Datatype recvtype) throws MPIException {

    intracommImpl.Allgatherv(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
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
      Datatype recvtype) throws MPIException {

    intracommImpl.Alltoall(sendbuf, sendoffset, sendcount, sendtype, recvbuf,
	recvoffset, recvcount, recvtype);
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
      int[] recvcount, int[] rdispls, Datatype recvtype) throws MPIException {

    intracommImpl.Alltoallv(sendbuf, sendoffset, sendcount, sdispls, sendtype,
	recvbuf, recvoffset, recvcount, rdispls, recvtype);

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
      int recvoffset, int count, Datatype datatype, Op op, int root)
      throws MPIException {

    intracommImpl.Reduce(sendbuf, sendoffset, recvbuf, recvoffset, count,
	datatype, op, root);

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
      int recvoffset, int count, Datatype datatype, Op op) throws MPIException {

    intracommImpl.Allreduce(sendbuf, sendoffset, recvbuf, recvoffset, count,
	datatype, op);

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
      int recvoffset, int[] recvcounts, Datatype datatype, Op op)
      throws MPIException {

    intracommImpl.Reduce_scatter(sendbuf, sendoffset, recvbuf, recvoffset,
	recvcounts, datatype, op);

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
      int recvoffset, int count, Datatype datatype, Op op) throws MPIException {

    intracommImpl.Scan(sendbuf, sendoffset, recvbuf, recvoffset, count,
	datatype, op);

  }

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
  public Cartcomm Create_cart(int[] dims, boolean[] periods, boolean reorder)
      throws MPIException {

    int totalProcess = this.group.Size();
    int places = 1;
    mpjdev.Comm ncomm = mpjdevComm;
    mpi.Group ngroup = this.group;

    for (int i = 0; i < dims.length; i++) {
      if (dims[i] < 0) {
	throw new MPIException("dims[" + i + "] is less than than zero");
      }
      places *= dims[i];
    }

    // MPI.logger.debug("places "+places);
    // MPI.logger.debug("totalProcesses "+totalProcess);

    if (places > totalProcess) {
      throw new MPIException(" Error in Intracomm.Create_cart: "
	  + "total grid positions <" + places + "> are greater than "
	  + " total processes <" + totalProcess + ">");
    } else if (places < totalProcess) {

      // MPI.logger.debug("dropping some processes ...");
      int[] excl = new int[totalProcess - places];
      // MPI.logger.debug("length of excl array "+excl.length);

      for (int i = 0; i < excl.length; i++) {
	excl[i] = i + places;
      }
      try {
	// MPI.logger.debug("calling excl method .");

	ngroup = ngroup.Excl(excl);

	// MPI.logger.debug( "called excl .. creating "+
	// "new group now" );
	ncomm = ncomm.create(ngroup.mpjdevGroup);

	// MPI.logger.debug("created the new comm");

      }
      catch (Exception e) {
	throw new MPIException(e);
      }

    } else {

      // MPI.logger.debug("All OK");
      ngroup = group;

      try {

	ncomm = ncomm.create(ngroup.mpjdevGroup);

      }
      catch (Exception e) {
	throw new MPIException(e);
      }

    }

    // MPI.logger.debug("oldRank "+group.Rank()+">,nrank<"+ngroup.Rank());
    if (ngroup.Rank() == -1) {
      // MPI.logger.debug("means this process is not in the "+
      // "new group");
      return null; // comm stuff ...
    }

    return new Cartcomm(dims, periods, reorder, ncomm, ngroup.mpjdevGroup);
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
    int size = this.group.Size();
    int nnodes = index.length;
    mpjdev.Comm ncomm = mpjdevComm;
    mpi.Group ngroup = this.group;

    // MPI.logger.debug("nnodes "+nnodes);
    // MPI.logger.debug("size "+size);

    if (nnodes > size) {
      throw new MPIException(" Error in Intracomm.Create_graphs: "
	  + "total nodes <" + nnodes + "> are greater than "
	  + " total processes <" + size + ">");
    } else if (nnodes < size) {
      // MPI.logger.debug("dropping some processes ...");
      int[] excl = new int[size - nnodes];
      // MPI.logger.debug("length of excl array "+excl.length);

      for (int i = 0; i < excl.length; i++) {
	excl[i] = i + nnodes;
      }

      try {
	// MPI.logger.debug("calling excl method .");
	ngroup = ngroup.Excl(excl);
	// MPI.logger.debug( "called excl .. creating "+
	// "new group now" );
	ncomm = ncomm.create(ngroup.mpjdevGroup);
	// MPI.logger.debug("created the new comm");
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

    } else {
      // MPI.logger.debug("All OK");
      ngroup = group;

      try {
	ncomm = ncomm.create(ngroup.mpjdevGroup);
      }
      catch (Exception e) {
	throw new MPIException(e);
      }

    }

    // MPI.logger.debug("oldRank "+group.Rank()+">,nrank<"+ngroup.Rank());

    if (ngroup.Rank() == -1) {
      // MPI.logger.debug("means this process is not in the "+
      // "new group");
      return null; // comm stuff ...
    }

    return new Graphcomm(index, edges, reorder, ncomm, ngroup.mpjdevGroup);
  }

}
