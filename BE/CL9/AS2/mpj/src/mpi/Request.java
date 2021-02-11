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
 * File         : Request.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.26 $
 * Updated      : $Date: 2014/03/11 13:26:15 PKT $
 */

package mpi;

import mpjdev.*;
import java.util.ArrayList;

public class Request {
  public mpjdev.Request req = null;
  Datatype datatype = null;
  boolean isNull = false;
  static final int NULL = 2;
  int code = -1;

  Request() {
  }

  Request(int code) {
    this.code = code;
  }

  Request(boolean isNull) {
    this.isNull = isNull;
  }

  /**
   */
  public Request(mpjdev.Request req) {
    this.req = req;
  }

  /**
   * Blocks until the operation identified by the request is complete.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>status object
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_WAIT</tt>.
   * <p>
   * After the call returns, the request object becomes inactive.
   */
  public Status Wait() throws MPIException {

    if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
      MPI.logger.debug("--Wait--");
    }

    if (isNull) {
      return MPI.EMPTY_STATUS;
    }

    if (this.req == null) {
      System.out.println(" mpjdev Request is null in Wait() ");
      return null;
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug("calling wait of niodev  ");

    mpjdev.Status mpjdevStatus = this.req.iwait();

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug("called wait of niodev  ");

    isNull = true;

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug("--Wait ends --");

    Status status = new mpi.Status(mpjdevStatus);
    // status.source = ? ;
    return status;

  }

  private Status Test(boolean nothing) throws MPIException {

    if (isNull == true) {
      return new Status();
    }

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug("-- Test (boolean)  ");
    mpi.Status status = null;
    mpjdev.Status devStatus = null;

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug("this.req  " + this.req);
    if (this.req != null) {
      devStatus = this.req.itest();
    } else
      return null;

    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug(" returned devStatus ");

    if (devStatus == null) {
      return null;
    }

    return new Status();
  }

  /**
   * Returns a status object if the operation identified by the request is
   * complete, or a null reference otherwise.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>status object or null reference
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TEST</tt>.
   * <p>
   * After the call, if the operation is complete (ie, if the return value is
   * non-null), the request object becomes inactive.
   */
  public Status Test() throws MPIException {

    if (isNull) {
      return MPI.EMPTY_STATUS;
    }

    mpi.Status status = null;
    mpjdev.Status devStatus = null;

    if (this.req != null) {
      devStatus = this.req.itest();

    } else
      return null;

    if (devStatus == null) {

      return null;
    }
    if (mpjdev.Constants.isNative) {
      // for native device case
      isNull = true;

      status = new mpi.Status(devStatus);
      // status.source = ? ;
      return status;

    } else {

      status = Wait();
      return status;
    }
  }

  /**
   * Tests for completion of either one or none of the operations associated
   * with active requests.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_requests </tt></td>
   * <td>array of requests
   * </tr>
   * <tr>
   * <td><em> returns:          </em></td>
   * <td>status object or null reference
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TESTANY</tt>.
   * <p>
   * If some request completed, the index in <tt>array_of_requests</tt> for that
   * request can be obtained from the returned status object through the. The
   * corresponding element of <tt>array_of_requests</tt> becomes inactive. If no
   * request completed, <tt>Testany</tt> returns a null reference.
   */
  public static Status Testany(Request[] r) throws MPIException {

    Status s = null;

    for (int i = 0; i < r.length; i++) {
      if (r[i] == null) {
	continue;
      }
      if (r[i].Is_null()) {
	continue;
      }

      s = r[i].Test();

      if (s != null) {
	s.index = i;
	return s;
      }

    }

    return null;

  }

  /**
   */
  public void finalize() throws MPIException {
  }

  /**
   * Test if request object is void.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>true if the request object is void, false otherwise
   * </tr>
   * </table>
   */
  public boolean Is_null() throws MPIException {
    return isNull;
  }

  /**
   * Blocks until all of the operations associated with the active requests in
   * the array have completed.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_requests </tt></td>
   * <td>array of requests
   * </tr>
   * <tr>
   * <td><em> returns:          </em></td>
   * <td>array of status objects
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_WAITALL</tt>.
   * <p>
   * The result array will be the same size as <tt>array_of_requests</tt>. On
   * exit, requests become inactive. If the <em>input</em> value of
   * <tt>arrayOfRequests</tt> contains inactive requests, corresponding elements
   * of the result array will contain null status references.
   */
  public static Status[] Waitall(Request[] r) throws MPIException {

    Status[] s = new Status[r.length];

    for (int i = 0; i < r.length; i++) {
      if (r[i] != null) {

	s[i] = r[i].Wait();
      }
    }

    return s;

  }

  /**
   * Blocks until one of the operations associated with the active requests in
   * the array has completed.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_requests </tt></td>
   * <td>array of requests
   * </tr>
   * <tr>
   * <td><em> returns:          </em></td>
   * <td>status object
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_WAITANY</tt>.
   * <p>
   * The index in <tt>array_of_requests</tt> for the request that completed can
   * be obtained from the returned status object through the
   * <tt>Status.index</tt> field. The corresponding element of
   * <tt>array_of_requests</tt> becomes inactive.
   */
  public static Status Waitany(Request[] r) throws MPIException {

    /*
     * mpjdev.Request requests[] = new mpjdev.Request[r.length] ;
     * 
     * for(int i=0 ; i<r.length ; i++) { if(r[i] != null) { requests[i] =
     * r[i].req ; } }
     * 
     * mpjdev.Status completedStatus = mpjdev.Request.iwaitany(requests) ;
     * Status wrapperCompletedStatus = new Status(completedStatus) ;
     * 
     * for(int i=0 ; i<r.length ; i++) { if(r[i] != null) { r[i].isNull = true ;
     * } }
     * 
     * return wrapperCompletedStatus ;
     */

    /* Do not need this naive implementation ... */
    Status s = null;

    // BUG: if all requests are inactive, this method would never return
    for (int i = 0; i < r.length; i++) {

      if (r[i].Is_null()) {
	if (i == r.length - 1)
	  i = -1;
	continue;
      }

      s = r[i].Test(true);

      if (s != null) {
	s = r[i].Wait();
	s.index = i;
	return s;
      }

      if (i == r.length - 1)
	i = -1;
    }

    return null;

  }

  /**
   * Tests for completion of <em>all</em> of the operations associated with
   * active requests.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_requests </tt></td>
   * <td>array of requests
   * </tr>
   * <tr>
   * <td><em> returns:          </em></td>
   * <td>array of status objects
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TESTALL</tt>.
   * <p>
   * If all operations have completed, the exit value of the argument array and
   * the result array are as for <tt>Waitall</tt>. If any operation has not
   * completed, the result value is null and no element of the argument array is
   * modified.
   */

  public static Status[] Testall(Request[] r) throws MPIException {

    Status[] s = new Status[r.length];

    for (int i = 0; i < r.length; i++) {

      if (r[i] != null) {
	s[i] = r[i].Test(true);

	if (s[i] == null) {
	  /*
	   * for( int j=i-1; j>-1 ; j--) { r[j].isNull = false; }
	   */
	  try {
	    Thread.currentThread().sleep(500);
	  }
	  catch (Exception e) {
	  }
	  //
	  return null;
	}

      }
    }

    for (int i = 0; i < r.length; i++) {
      s[i] = r[i].Wait();
    }

    return s;

  }

  /**
   * Blocks until at least one of the operations associated with the active
   * requests in the array has completed.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_requests </tt></td>
   * <td>array of requests
   * </tr>
   * <tr>
   * <td><em> returns:          </em></td>
   * <td>array of status objects
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_WAITSOME</tt>.
   * <p>
   * The size of the result array will be the number of operations that
   * completed. The index in <tt>array_of_requests</tt> for each request that
   * completed can be obtained from the returned status objects through the
   * <tt>Status.index</tt> field. The corresponding element in
   * <tt>array_of_requests</tt> becomes inactive.
   */

  public static Status[] Waitsome(Request[] r) throws MPIException {

    ArrayList<Status> list = new ArrayList<Status>();
    boolean break_flag = false;

    for (int i = 0; i < r.length; i++) {
      Status s = null;

      if (r[i].Is_null()) {

	if (break_flag && i == r.length - 1) {
	  break;
	} else if (i == r.length - 1) {
	  i = -1;
	}

	continue;
      }

      s = r[i].Test(true);

      if (s != null) {
	s = r[i].Wait();
	s.index = i;
	list.add(s);
	break_flag = true;
      }

      if (break_flag && i == r.length - 1) {
	break;
      } else if (i == r.length - 1) {
	i = -1;
      }

    }

    return list.toArray(new Status[0]);
  }

  /**
   * Behaves like <tt>Waitsome</tt>, except that it returns immediately.
   * <p>
   * <table>
   * <tr>
   * <td><tt> array_of_requests </tt></td>
   * <td>array of requests
   * </tr>
   * <tr>
   * <td><em> returns:          </em></td>
   * <td>array of status objects
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TESTSOME</tt>.
   * <p>
   * If no operation has completed, <tt>TestSome</tt> returns an array of length
   * zero and elements of <tt>array_of_requests</tt> are unchanged. Otherwise,
   * arguments and return value are as for <tt>Waitsome</tt>.
   */
  public static Status[] Testsome(Request[] r) throws MPIException {
    if (MPI.DEBUG && MPI.logger.isDebugEnabled())
      MPI.logger.debug(" Testsome ");
    ArrayList<Status> list = new ArrayList<Status>();

    for (int i = 0; i < r.length; i++) {
      if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	MPI.logger.debug("i " + i);
      Status s = null;

      if (r[i].Is_null()) {
	if (MPI.DEBUG && MPI.logger.isDebugEnabled())
	  MPI.logger.debug(" its null, continuing");
	continue;
      }

      if (MPI.DEBUG && MPI.logger.isDebugEnabled()) {
	MPI.logger.debug("calling test again.");
      }

      s = r[i].Test();

      if (s != null) {
	s.index = i;
	list.add(s);
      }

    }

    return list.toArray(new Status[0]);

  }

  /**
   * Mark a pending nonblocking communication for cancellation. Java binding of
   * the MPI operation <tt>MPI_CANCEL</tt>.
   */
  public void Cancel() throws MPIException {

    System.out.println("Request.Cancel(): Not implemented in this release");

    if (this.req.cancel()) {
      isNull = true;
    } else {
      isNull = false;
    }

  }

}
