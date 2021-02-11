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
 * File         : NativeSendRequest.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

package mpjdev.natmpjdev;

import mpjdev.Status;

public class NativeSendRequest extends NativeRequest {

  long dbufHandle; // dynamicBuffer handle
  int dbuflen; // dynamicBuffer length

  private static native void initNativeSendRequest();

  static {
    initNativeSendRequest();
  }

  // constructor
  protected NativeSendRequest() {
    // nothing here
  }

  public Status iwait() {

    if (completed) {
      return this.status;
    }

    Status result = new Status();
    Wait(result); // native Wait()
    complete(result);
    this.completed = true;
    return result;

  }

  private native Status Wait(Status stat);

  public Status itest() {
    if (this.completed) {
      return null;
    }
    Status result = new Status();
    if (Test(result) == null)
      return null;
    else {
      complete(result);
      this.completed = true;
      this.status = result;
    }
    return result;
  }

  private native Status Test(Status stat);

}
