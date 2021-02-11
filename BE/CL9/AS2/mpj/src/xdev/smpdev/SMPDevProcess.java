/* 
 The MIT License

 Copyright (c) 2010
   1. Bryan Carpenter (2010)

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
 * File         : SMPDevProcess.java
 * Author       : Kamran Hameed, Bryan Carpenter
 * Created      : Thu Dec 24 10:47:35 PKT 2009
 * Revision     : $Revision: 1.0 $
 * Updated      : $Thu Dec 24 10:47:35 PKT 2009$
 */

package xdev.smpdev;

import java.util.UUID;
import xdev.ProcessID;

public class SMPDevProcess extends ThreadGroup  {

  private ProcessID myID;

  public SMPDevProcess(String name) {
    super(name);
  }

  public SMPDevProcess(ThreadGroup parent,String name) {
    super(parent,name);
  }
  public ProcessID getID() {
    return myID;
  }
  public void setProcessID(ProcessID id) {
    this.myID = id;
  }

}

