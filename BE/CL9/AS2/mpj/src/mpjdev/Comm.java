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
 * File         : Comm.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

package mpjdev;

import mpjbuf.*;

public abstract class Comm {

  public int sendctxt = 0;
  public int recvctxt = 0;
  public int collctxt = 0;

  public mpjdev.Group group;
  public mpjdev.Group localgroup;

  /**
   * this method will create intracommunicators. One way is to use create(grp),
   * or get ids from grp at MPJ level and give the argument. contexts will be
   * managed in this device and intercomms will be created by create(grp1,grp2)
   * or whatever it may require.
   */

  public abstract Comm create(int[] ids) throws MPJDevException;

  /**
   * this method is used to create intra-communicators, not inter-communicators.
   */
  public abstract Comm create(mpjdev.Group ngroup) throws MPJDevException;

  public abstract Comm create(mpjdev.Comm localcomm, mpjdev.Group peergroup,
      int localleader, int remoteleader, int tag) throws MPJDevException;

  public abstract Comm clone();

  public abstract Status probe(int src, int tag) throws MPJDevException;

  public abstract Status iprobe(int src, int tag) throws MPJDevException;

  public abstract Request irecv(mpjbuf.Buffer buf, int src, int tag,
      mpjdev.Status status, boolean pt2pt) throws MPJDevException;

  public abstract Status recv(mpjbuf.Buffer buf, int src, int tag, boolean pt2pt)
      throws MPJDevException;

  // used in finish method in javampjdev
  public abstract void barrier() throws MPJDevException;

  public abstract Request isend(mpjbuf.Buffer buf, int dest, int tag,
      boolean pt2pt) throws MPJDevException;

  public abstract void send(mpjbuf.Buffer buf, int dest, int tag, boolean pt2pt)
      throws MPJDevException;

  public abstract Request issend(mpjbuf.Buffer buf, int dest, int tag,
      boolean pt2pt) throws MPJDevException;

  public abstract void ssend(mpjbuf.Buffer buf, int dest, int tag, boolean pt2pt)
      throws MPJDevException;

  public int size() throws MPJDevException {
    return this.group.size();
  }

  public int id() throws MPJDevException {

    return this.group.rank();
  }

  public void free() throws MPJDevException {
    // cleaning up resources ...
  }

  /*
   * all processes with same color would be form one sub-group ... all processes
   * send their color and key to all other processes ... all processes receive
   * color and key from all other processes ... now classify how many colors
   * you've ... assign ranks depending on keys ...lower the key, lower the new
   * rank .. but if the keys are same ..still its not a problem ..its my
   * responsibility to assigning new ascending ranks to each process ...
   */

  public abstract Comm split(int color, int key) throws MPJDevException;

}
