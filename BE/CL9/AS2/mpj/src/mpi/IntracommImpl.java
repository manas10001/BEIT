/*
 The MIT License

 Copyright (c) 2005 - 2008
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Community Grids Laboratory, Indiana University (2005)
   3. Aamir Shafi (2005 - 2008)
   4. Bryan Carpenter (2005 - 2008)
   5. Mark Baker (2005 - 2008)

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
 * Updated      : $Date: 2006/10/20 17:24:47 $
 */

package mpi;

import mpjdev.*;
import mpjbuf.*;

public abstract class IntracommImpl
    extends Comm {
 
  IntracommImpl() {
  }



  /**
   * Partition the group associated with this communicator and create
   * a new communicator within each subgroup.
   * <p>
   * <table>
   * <tr><td><tt> color    </tt></td><td> control of subset assignment </tr>
   * <tr><td><tt> key      </tt></td><td> control of rank assignment </tr>
   * <tr><td><em> returns: </em></td><td> new communicator </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_SPLIT</tt>.
   */
  public abstract IntracommImpl Split(int color, int key);

  /**
   * Clone the communicator
   * This method will be called only by intracommunicator ....
   * changed the return value to Intracomm ...(instead of Object) ...
   */
  public abstract Object clone();
  /**
   * Create a new communicator.
   * <p>
   * <table>
   * <tr><td><tt> group    </tt></td><td> group which is a subset of the
   *                                      group of this communicator </tr>
   * <tr><td><em> returns: </em></td><td> new communicator </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_COMM_CREATE</tt>.
   */
  public abstract IntracommImpl Create(Group group);


  /**
   * Broadcast a message from the process with rank <tt>root</tt>
   * to all processes of the group.
   * <p>
   * <table>
   * <tr><td><tt> buf      </tt></td><td> buffer array </tr>
   * <tr><td><tt> offset   </tt></td><td> initial offset in buffer </tr>
   * <tr><td><tt> count    </tt></td><td> number of items in buffer </tr>
   * <tr><td><tt> datatype </tt></td><td> datatype of each item in
   *                                      buffer </tr>
  /**
   * A call to <tt>Barrier</tt> blocks the caller until all process
   * in the group have called it.
   * <p>
   * Java binding of the MPI operation <tt>MPI_BARRIER</tt>.
   */
  public abstract void Barrier();

  /**
   * Broadcast a message from the process with rank <tt>root</tt>
   * to all processes of the group.
   * <p>
   * <table>
   * <tr><td><tt> buf      </tt></td><td> buffer array </tr>
   * <tr><td><tt> offset   </tt></td><td> initial offset in buffer </tr>
   * <tr><td><tt> count    </tt></td><td> number of items in buffer </tr>
   * <tr><td><tt> datatype </tt></td><td> datatype of each item in
   *                                      buffer </tr>
   * <tr><td><tt> root     </tt></td><td> rank of broadcast root </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_BCST</tt>.
   */

  public abstract void Bcast(Object buf,
                    int offset,
                    int count,
                    Datatype type,
                    int root) ;


  /**
   * Each process sends the contents of its send buffer to the
   * root process.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcount  </tt></td><td> number of items to send </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcount  </tt></td><td> number of items to receive </tr>
   * <tr><td><tt> recvtype   </tt></td><td> datatype of each item in receive
   *                                        buffer </tr>
   * <tr><td><tt> root       </tt></td><td> rank of receiving process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GATHER</tt>.
   */
  public abstract void Gather(Object sendbuf,
                     int sendoffset,
                     int sendcount,
                     Datatype sendtype,
                     Object recvbuf,
                     int recvoffset,
                     int recvcount,
                     Datatype recvtype,
                     int root);


  /**
   * Extends functionality of <tt>Gather</tt> by allowing varying
   * counts of data from each process.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcount  </tt></td><td> number of items to send </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcounts </tt></td><td> number of elements received from
   *                                        each process </tr>
   * <tr><td><tt> displs     </tt></td><td> displacements at which to place
   *                                        incoming data </tr>
   * <tr><td><tt> recvtype   </tt></td><td> datatype of each item in receive
   *                                        buffer </tr>
   * <tr><td><tt> root       </tt></td><td> rank of receiving process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GATHERV</tt>.
   * <p>
   * The sizes of arrays <tt>recvcounts</tt> and <tt>displs</tt> should be the
   * size of the group.  Entry <em>i</em> of <tt>displs</tt> specifies the
   * displacement relative to element <tt>recvoffset</tt> of <tt>recvbuf</tt>
   * at which to place incoming data.
   */
  public abstract void Gatherv(Object sendbuf,
                      int sendoffset,
                      int sendcount,
                      Datatype sendtype,
                      Object recvbuf,
                      int recvoffset,
                      int[] recvcount,
                      int[] displs,
                      Datatype recvtype,
                      int root) ;

  /**
   * Inverse of the operation <tt>Gather</tt>.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcount  </tt></td><td> number of items to send </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcount  </tt></td><td> number of items to receive </tr>
   * <tr><td><tt> recvtype   </tt></td><td> datatype of each item in receive
   *                                        buffer </tr>
   * <tr><td><tt> root       </tt></td><td> rank of sending process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SCATTER</tt>.
   */
  

  public abstract void Scatter(Object sendbuf,
                      int sendoffset,
                      int sendcount,
                      Datatype sendtype,
                      Object recvbuf,
                      int recvoffset,
                      int recvcount,
                      Datatype recvtype,
                      int root);


  /**
   * Inverse of the operation <tt>Gatherv</tt>.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcounts </tt></td><td> number of items sent to each
   *                                        process </tr>
   * <tr><td><tt> displs     </tt></td><td> displacements from which to take
   *                                        outgoing data </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcount  </tt></td><td> number of items to receive </tr>
   * <tr><td><tt> recvtype   </tt></td><td> datatype of each item in receive
   *                                        buffer </tr>
   * <tr><td><tt> root       </tt></td><td> rank of sending process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SCATTERV</tt>.
   */
  public abstract void Scatterv(Object sendbuf,
                       int sendoffset,
                       int[] sendcount,
                       int[] displs,
                       Datatype sendtype,
                       Object recvbuf,
                       int recvoffset,
                       int recvcount,
                       Datatype recvtype,
                       int root);


  /**
   * Similar to <tt>Gather</tt>, but all processes receive the result.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcount  </tt></td><td> number of items to send </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcount  </tt></td><td> number of items to receive </tr>
   * <tr><td><tt> recvtype   </tt></td><td> datatype of each item in receive
   *                                        buffer </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLGATHER</tt>.
   */

  public abstract void Allgather(Object sendbuf,
                        int sendoffset,
                        int sendcount,
                        Datatype sendtype,
                        Object recvbuf,
                        int recvoffset,
                        int recvcount,
                        Datatype recvtype) ;



  /**
   * Similar to <tt>Gatherv</tt>, but all processes receive the result.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcount  </tt></td><td> number of items to send </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcounts </tt></td><td> number of elements received from
   *                                        each process </tr>
   * <tr><td><tt> displs     </tt></td><td> displacements at which to place
   *                                        incoming data </tr>
   * <tr><td><tt> recvtype   </tt></td><td> datatype of each item in receive
   *                                        buffer </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLGATHERV</tt>.
   */
  public abstract void Allgatherv(Object sendbuf,
                         int sendoffset,
                         int sendcount,
                         Datatype sendtype,
                         Object recvbuf,
                         int recvoffset,
                         int[] recvcount,
                         int[] displs,
                         Datatype recvtype);

  /**
   * Extension of <tt>Allgather</tt> to the case where each process sends
   * distinct data to each of the receivers.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcount  </tt></td><td> number of items sent to each
   *                                        process </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype send buffer items </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcount  </tt></td><td> number of items received from any
   *                                        process
   * <tr><td><tt> recvtype   </tt></td><td> datatype of receive buffer
   *                                        items </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLTOALL</tt>.
   */


  public abstract void Alltoall(Object sendbuf,
                       int sendoffset,
                       int sendcount,
                       Datatype sendtype,
                       Object recvbuf,
                       int recvoffset,
                       int recvcount,
                       Datatype recvtype);

  /**
   * Adds flexibility to <tt>Alltoall</tt>: location of data for send is
   * specified by <tt>sdispls</tt> and location to place data on receive
   * side is specified by <tt>rdispls</tt>.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> sendcounts </tt></td><td> number of items sent to each
   *                                        process </tr>
   * <tr><td><tt> sdispls    </tt></td><td> displacements from which to take
   *                                        outgoing data </tr>
   * <tr><td><tt> sendtype   </tt></td><td> datatype send buffer items </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcounts </tt></td><td> number of elements received from
   *                                        each process
   * <tr><td><tt> rdispls    </tt></td><td> displacements at which to place
   *                                        incoming data </tr>
   * <tr><td><tt> recvtype   </tt></td><td> datatype of each item in receive
   *                                        buffer </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLTOALLV</tt>.
   */

  public abstract void Alltoallv(Object sendbuf,
                        int sendoffset,
                        int[] sendcount,
                        int[] sdispls,
                        Datatype sendtype,
                        Object recvbuf,
                        int recvoffset,
                        int[] recvcount,
                        int[] rdispls,
                        Datatype recvtype);


  /**
   * Combine elements in input buffer of each process using the reduce
   * operation, and return the combined value in the output buffer of the
   * root process.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> count      </tt></td><td> number of items in send buffer </tr>
   * <tr><td><tt> datatype   </tt></td><td> data type of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> op         </tt></td><td> reduce operation </tr>
   * <tr><td><tt> root       </tt></td><td> rank of root process </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_REDUCE</tt>.
   * <p>
   * The predefined operations are available in Java as <tt>MPI.MAX</tt>,
   * <tt>MPI.MIN</tt>, <tt>MPI.SUM</tt>, <tt>MPI.PROD</tt>, <tt>MPI.LAND</tt>,
   * <tt>MPI.BAND</tt>, <tt>MPI.LOR</tt>, <tt>MPI.BOR</tt>, <tt>MPI.LXOR</tt>,
   * <tt>MPI.BXOR</tt>, <tt>MPI.MINLOC</tt> and <tt>MPI.MAXLOC</tt>.
   */

  public abstract void Reduce(Object sendbuf, int sendoffset,
                     Object recvbuf, int recvoffset, int count,
                     Datatype datatype, Op op, int root);
  


  /**
   * Same as <tt>reduce</tt> except that the result appears in receive
   * buffer of all process in the group.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> count      </tt></td><td> number of items in send buffer </tr>
   * <tr><td><tt> datatype   </tt></td><td> data type of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> op         </tt></td><td> reduce operation </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_ALLREDUCE</tt>.
   */

  

  public abstract void Allreduce(Object sendbuf, int sendoffset,
                        Object recvbuf, int recvoffset, int count,
                        Datatype datatype, Op op);

  /**
   * Combine elements in input buffer of each process using the reduce
   * operation, and scatter the combined values over the output buffers
   * of the processes.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> recvcounts </tt></td><td> numbers of result elements
   *                                        distributed to each process </tr>
   * <tr><td><tt> datatype   </tt></td><td> data type of each item in send
   *                                        buffer </tr>
   * <tr><td><tt> op         </tt></td><td> reduce operation </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_REDUCE_SCATTER</tt>.
   */
  public abstract void Reduce_scatter(Object sendbuf, int sendoffset,
                             Object recvbuf, int recvoffset, int[] recvcounts,
                             Datatype datatype, Op op);
                             
                             
  /**
   * Perform a prefix reduction on data distributed across the group.
   * <p>
   * <table>
   * <tr><td><tt> sendbuf    </tt></td><td> send buffer array </tr>
   * <tr><td><tt> sendoffset </tt></td><td> initial offset in send buffer </tr>
   * <tr><td><tt> recvbuf    </tt></td><td> receive buffer array </tr>
   * <tr><td><tt> recvoffset </tt></td><td> initial offset in receive
   *                                        buffer </tr>
   * <tr><td><tt> count      </tt></td><td> number of items in input
   *                                        buffer </tr>
   * <tr><td><tt> datatype   </tt></td><td> data type of each item in input
   *                                        buffer </tr>
   * <tr><td><tt> op         </tt></td><td> reduce operation </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_SCAN</tt>.
   */

  public abstract void Scan(Object sendbuf, int sendoffset,
                   Object recvbuf, int recvoffset, int count,
                   Datatype datatype, Op op) ;
                             
}

