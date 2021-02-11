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
 * File         : NativeRecvRequest.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */
package mpjdev.natmpjdev;

import mpjdev.Status;

public class NativeRecvRequest extends NativeRequest {

  private static native void initNativeRecvRequest();

  static {
    initNativeRecvRequest();
  }

  protected NativeRecvRequest() {

  }

  public NativeRecvRequest(long commHandle_) {
    this.commHandle = commHandle_;

  }

  public Status iwait() {

    if (completed) {
      return this.status;
    }

    Status status_ = new Status();
    Wait(status_);
    try {
      bufferHandle.commit();
      // Whats happening?
      // Actually in the native method Wait() the bufferHandle field is being
      // set so here we need to commit it and read other info like type and size
      // as below

      bufferHandle.setSize(status_.numEls);

      status_.type = bufferHandle.getSectionHeader();
      status_.numEls = bufferHandle.getSectionSize();

    } catch (Exception e) {
      e.printStackTrace();
    }
    complete(status_);
    this.completed = true;
    this.status = status_;
    return status_;
  }

  private native Status Wait(Status stat);

  public Status itest() {

    if (this.completed) {
      return null;
    }

    Status result = new Status();
    // res1 and result are the same ..
    Status res1 = Test(result);
    if (res1 == null) {
      return null;
    } else {
      try {
	bufferHandle.commit();
	// Whats happening?
	// Actually in the native method Wait() the bufferHandle field is being
	// set so here we need to commit it and read other info like type and
	// size as below

	bufferHandle.setSize(res1.numEls);

	res1.type = bufferHandle.getSectionHeader();
	res1.numEls = bufferHandle.getSectionSize();

      } catch (Exception e) {
	e.printStackTrace();
      }
      complete(res1);
      this.completed = true;
      this.status = res1;
      return res1;
    }

  }

  private native Status Test(Status stat);

}
