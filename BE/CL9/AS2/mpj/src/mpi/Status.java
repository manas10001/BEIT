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
 * File         : Status.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.14 $
 * Updated      : $Date: 2005/11/27 19:37:22 $
 */

package mpi;

import mpjdev.*;
import mpjbuf.*;
import java.util.Hashtable;

public class Status
    extends mpjdev.Status implements Freeable {

  Status(int s, int t, int numEls, int count) {
    this.source = s;
    this.tag = t;
    this.numEls = numEls;
    this.count = count;
  }

  public Status(mpjdev.Status s) {
    this.source = s.source;
    this.tag = s.tag;
    this.index = s.index;
    this.count = s.count;
    this.numEls = s.numEls;
    this.type = s.type;
    this.countInBytes = s.countInBytes ; //needed for mxdev 
  }

  Status(int s, int t, int i) {
    this.source = s;
    this.tag = t;
    this.index = i;
  }

  /**
   * Get the number of received entries.
   * <p>
   * <table>
   * <tr><td><tt> datatype </tt></td><td> datatype of each item in receive
   *                                      buffer </tr>
   * <tr><td><em> returns: </em></td><td> number of received entries </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GET_COUNT</tt>.
   */
  public int Get_count(Datatype datatype) throws MPIException {
    /* 
    System.out.println(" source "+ source);
    System.out.println(" tag "+ tag);
    System.out.println(" index "+ index );
    System.out.println(" count "+ count );
    System.out.println(" numEls "+ numEls );
    System.out.println(" type "+ type );
    /*
     * this (if loop) will force MPJ to work for mxdev. If a user calls probe
     * and then calls this method with the status returned by probe, 
     * there is no way, we can be sure about the datatype of the messages
     *
     * If the users have called irecv followed by wait ..then we can 
     * retrieve type and numEls from the buffer. In such a case, this 
     * loop would not get executed ..but we need to make it work in case
     * this method is called followed by a probe method ...
     *
     * The MPI specifications look ambigous to me about this ...it states 
     * the the datatype argument to this method *should* be the same 
     * as the receive methods. There is no way of doing error-checking 
     * to ensure this. We are overdoing this in niodev and are passing 
     * the type of the message as part of the control message ...but
     * what other MPI implementations do is to divide count (number of 
     * bytes in the message) by the datatype provided to this method ...
     * if the remainder is non-zero ...return MPI.UNDEFINED .. otherwise 
     * return the result of count/datatype.size ..note that there is no
     * guarantee that the message is of the datatype specified by the user
     * as an arg to this method. In MPJ, we actually ensure that this is the 
     * case ...
     */

    if(type == null) {  
      if(countInBytes % datatype.byteSize == 0) { 
        return countInBytes/datatype.byteSize ; 	      
      }
      else {
        return 0; //MPI.UNDEFINED .. 	      
      }
    }
    
    if (datatype.bufferType.getCode() == type.getCode() ) {
      return count;
    }
    else {
      System.out.println("This status object doesn't contain any count" +
                         "of this datatype");
      //maybe throw an exception in this case ...is it really an exception?
      return 0;
    }

  }

  /**
   * Constructor
   */
  public Status() {
  }

  /**
   * Test if communication was cancelled.
   * <p>
   * <table>
   * <tr><td><em> returns: </em></td><td> true if the operation was
   *                                      succesfully cancelled,
   *                                      false otherwise
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_TEST_CANCELLED</tt>.
   */
  public boolean Test_cancelled() throws MPIException {
    System.out.println("Cancel functionality is not implemented.");
    return false;
  }

  /**
   */
  public void free() {
  }

  /**
   * Retrieve number of basic elements from status.
   * <p>
   * <table>
   * <tr><td><tt> datatype </tt></td><td> datatype used by receive
   *                                      operation </tr>
   * <tr><td><em> returns: </em></td><td> number of received basic
   *                                      elements </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GET_ELEMENTS</tt>.
   */
  public int Get_elements(Datatype datatype) throws MPIException {
    if (datatype.bufferType.getCode() == type.getCode() ) {
      return numEls;
    }
    else {
      System.out.println("This status object doesn't contain any numEls" +
                         "of this datatype");
      //maybe throw an exception here ...is it really an exception?
      return 0;
    }
  }

}

// Things to do
// What about derived datatypes ...
