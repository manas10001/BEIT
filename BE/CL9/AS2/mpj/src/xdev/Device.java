/*
 The MIT License

 Copyright (c) 2005 - 2007
   1. Distributed Systems Group, University of Portsmouth (2005)
   2. Aamir Shafi (2005 - 2007)
   3. Bryan Carpenter (2005 - 2007)
   4. Mark Baker (2005 - 2007)

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
 * File         : Device.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Mon Jan 24 09:44:40 BST 2005
 * Revision     : $Revision: 1.22 $
 * Updated      : $Date: 2005/12/21 13:18:17 $
 */
package xdev;

import java.util.UUID ;
import mpjbuf.BufferFactory ;

/**
 * <p>
 * <i> xdev </i> is an abstract class representing an abstract device.
 * The implementations of this abstract
 * class would provide various different communication protocols that can
 * be used by MPJ at the higher-levels. The aim is to allow, flexible swapping
 * of communication protocols, and also to keep the API simple and small, thus
 * minimizing the overall development time of the device.
 * </p>
 * <p>
 * Instead of using integer ranks as arguments to send/recv, <i> xdev </i> uses
 * {@link xdev.ProcessID xdev.ProcessID} as arguments to send/recv.
 * This essentially means that
 * xdev doesnot deal with the higher level abstractions of MPI, like
 * groups, communicators, and contexts -- it only focuses on providing
 * communication methods, on top of which these higher abstractions of
 * MPI can be implemented.
 * </p>
 */
public interface Device {

  /** 
   * A wild-card that can be used with the recv methods. 
   */
  public static final int ANY_SOURCE = -2;

  //public String deviceName ;
  
  /**
   * A wild-card for ANY_SOURCE at xdev level. It is a {@link xdev.ProcessID
   * xdev.ProcessID} object with UUID set to random UUID , and rank set to 
   * ANY_SRC (-2)
   */
  public static ProcessID ANY_SRC = new ProcessID(UUID.randomUUID());
      
  /** 
   * A wild-card that can be used with the recv method to receive the
   * message from any source within the communicator. 
   */
  public static final int ANY_TAG = -2;
  
  /**
   * This method returns an instance of xdev specified by the argument.
   * In future, this method may search the classpath to load the
   * libraries for the appropriate device to use and returns an
   * instance of that device.
   * @param dev The name of the xdev Device. The one for Java New I/O is
   *            named 'niodev'. The name of the device is provided to xdev
   *            by MPJ runtime, which in turn gets it from 'mpjrun' script.
   * @return Device An instance of an implementation of the device.
   * @throws XDevException If there is no corresponding device to the string
   *                       argument provided to this method
   *
  public static Device newInstance(String dev) throws XDevException {
    Device device = null;

    if (dev.equals("niodev")) {
      device = new xdev.niodev.NIODevice();
    } 
    else if(dev.equals("mxdev")) {
      device = new xdev.mxdev.MXDevice();
    } 
    else {
      throw new XDevException("No matching device found for <"+dev+">");    
    }

    return device;
  }
  */

  /**
   * Initialize the xdev device. Specific implementations perform
   * initialization of the device in this method.
   * @param args Argument array.
   * @return xdev.ProcessID[] An array with length equal to total number
   * of processes, and each ProcessID element representing a process.
   * @throws XDevException If there is an error initializing the device
   */
  public abstract ProcessID[] init(String[] args) throws XDevException;

  /* 
   * Get send message over head ...
   */
  public abstract int getSendOverhead() ;
  
  /* 
   * Get recv message over head ...
   */
  public abstract int getRecvOverhead() ;

  /**
   * This method returns the id of the current process
   * @return xdev.ProcessID ProcessID of the current process.
   */
  public abstract ProcessID id();

  /**
   * This method shutdowns the device.
   */
  public abstract void finish() throws XDevException;

  /**
   * This method is the non-blocking send using standard-mode.
   * @param buf {@link mpjbuf.Buffer mpjbuf.Buffer} object containing the data
   * @param destID {@link xdev.ProcessID ProcessID} of the destination process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @return mpjdev.Request Request object that can be used to check the status
   *                        and/or progress of the communication.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract mpjdev.Request isend(mpjbuf.Buffer buf, ProcessID destID,
                                       int tag, int context) 
	                               throws XDevException;

  /**
   * This method is the blocking send using standard-mode.
   * @param buf {@link mpjbuf.Buffer mpjbuf.Buffer} object containing the data
   * @param destID {@link xdev.ProcessID ProcessID} of the destination process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract void send(mpjbuf.Buffer buf, ProcessID destID,
                            int tag, int context) throws XDevException;

  /**
   * This method is the non-blocking send using synchronous-mode.
   * @param buf {@link mpjbuf.Buffer mpjbuf.Buffer} object containing the data
   * @param destID {@link xdev.ProcessID ProcessID} of the destination process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @return mpjdev.Request Request object that can be used to check the status
   *                        and/or progress of the communication.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract mpjdev.Request issend(mpjbuf.Buffer buf, ProcessID destID,
                                        int tag, int context) 
	  				throws XDevException;

  /**
   * This method is the blocking send using synchronous-mode.
   * @param buf {@link mpjbuf.Buffer mpjbuf.Buffer} object containing the data
   * @param destID {@link xdev.ProcessID ProcessID} of the destination process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract void ssend(mpjbuf.Buffer buf, ProcessID destID,
                             int tag, int context) throws XDevException;

  /**
   * This method is the blocking recv.
   * @param buf {@link mpjbuf.Buffer mpjbuf.Buffer} object containing the data
   * @param srcID {@link xdev.ProcessID ProcessID} of the source process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @return mpjdev.Status Status object that can be used to check the status
   *                        of the communication.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract mpjdev.Status recv(mpjbuf.Buffer buf, ProcessID srcID,
                                     int tag, int context) 
	                             throws XDevException;

  /**
   * This method is the non-blocking recv.
   * @param buf {@link mpjbuf.Buffer mpjbuf.Buffer} object containing the data
   * @param srcID {@link xdev.ProcessID ProcessID} of the source process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @param status A {@link mpjdev.Status mpjdev.Status} object initialized
   *               at mpjdev/MPJ level.
   * @return mpjdev.Request Request object that can be used to check the status
   *                        of the communication.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract mpjdev.Request irecv(mpjbuf.Buffer buf, ProcessID srcID,
                                       int tag, int context,
                                       mpjdev.Status status) 
	                               throws XDevException;

  /**
   * This method is the blocking probe.
   * @param srcID {@link xdev.ProcessID ProcessID} of the source process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @return mpjdev.Status Status object that can be used to check the status
   *                        of the communication.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract mpjdev.Status probe(ProcessID srcID, int tag,
                                      int context) throws XDevException;

  /**
   * This method is the non-blocking probe.
   * @param srcID {@link xdev.ProcessID ProcessID} of the source process
   * @param tag An integer representing the tag (id) of the message
   * @param context An integer specifying context.
   * @return mpjdev.Status Status object if the communication has completed or
   *                       null otherwise.
   * @throws XDevException If there is an exception. The specific exception 
   *                       depends on the device.
   */
  public abstract mpjdev.Status iprobe(ProcessID srcID, int tag,
                                       int context) throws XDevException;

  /**
   * This method is sam as iprobe method but added to facilitate 
   * Hybrid device, only difference is destination Process id is added.
   * In Hybrid Device we may not know Destination ProcessID 
   * while multiple processes are running on destination host 
   */
  public abstract mpjdev.Status iprobe(ProcessID srcID, ProcessID dstID, 
                                  int tag, int context) throws XDevException;
                                  
  public abstract mpjdev.Status iprobeAndFetch(ProcessID srcID, ProcessID dstID, 
                  int tag, int context, mpjbuf.Buffer buf ) throws XDevException;
                                                                    
  public abstract mpjdev.Request peek() throws XDevException; 

}
