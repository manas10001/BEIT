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
 * File         : Prequest.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.10 $
 * Updated      : $Date: 2005/11/27 19:37:22 $
 */

package mpi;

import mpjdev.*;
import mpjbuf.*;

public class Prequest
    extends Request {

  /* */
  Object buf = null;
  int offset = 0, count = 0, dest = 0, tag = 0, op = 0;
  Datatype datatype = null;
  mpi.Comm comm = null;

  Prequest(Object buf, int offset, int count, Datatype datatype,
           int dest, int tag, int op, mpi.Comm comm) {
    this.buf = buf;
    this.offset = offset;
    this.count = count;
    this.datatype = datatype;
    this.dest = dest;
    this.tag = tag;
    this.op = op;
    this.comm = comm;
  }

  /**
   * Activate a persistent communication request.
   * Java binding of the MPI operation <tt>MPI_START</tt>.
   * The communication is completed by using the request in
   * one of the <tt>wait</tt> or <tt>test</tt> operations.
   * On successful completion the request becomes inactive again.
   * It can be reactivated by a further call to <tt>Start</tt>.
   */
  public void Start() throws MPIException {
    Request request = null;
    switch (op) {

      case MPI.OP_SEND:
        request = comm.Isend(buf, offset, count, datatype, dest, tag);
        this.req = request.req;
	this.isNull = false;
        //this.datatype = request.datatype;
        //this.isNull = request.isNull;
        break;

      case MPI.OP_BSEND:
        request = comm.Ibsend(buf, offset, count, datatype, dest, tag);
        this.req = request.req;
	this.isNull = false;
        //this.datatype = request.datatype;
        //this.isNull = request.isNull;
        break;

      case MPI.OP_RSEND:
        request = comm.Irsend(buf, offset, count, datatype, dest, tag);
        this.req = request.req;
	this.isNull = false;
        //this.datatype = request.datatype;
        //this.isNull = request.isNull;
        break;

      case MPI.OP_SSEND:
        request = comm.Issend(buf, offset, count, datatype, dest, tag);
        this.req = request.req;
	this.isNull = false;
        //this.datatype = request.datatype;
        //this.isNull = request.isNull;
        break;

      case MPI.OP_RECV:
        request = comm.Irecv(buf, offset, count, datatype, dest, tag);
        this.req = request.req;
	this.isNull = false;
        //this.datatype = request.datatype;
        //this.isNull = request.isNull;
        break;

      default:
        throw new MPIException("Invalid persistent request operation");
    }
  }

  /**
   * Activate a list of communication requests.
   * <p>
   * <table>
   * <tr><td><tt> array_of_requests </tt></td><td> array of requests </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_STARTALL</tt>.
   */
  public static void Startall(Prequest[] array_of_request) throws MPIException {

    for (int i = 0; i < array_of_request.length; i++) {
      if (array_of_request[i] != null)
        array_of_request[i].Start();
    }
  }
}

