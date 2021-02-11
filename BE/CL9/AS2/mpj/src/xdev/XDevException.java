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

package xdev;

/*
 * File         : XDevException.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Wed Jun 22 11:13:42 BST 2005
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2005/07/29 14:03:10 $
 */

/**
 * XDevException class
 * @author Aamir Shafi
 */
public class XDevException
    extends RuntimeException {

  public XDevException() {
    super();
  }
  
  public XDevException(String msg) {
    super(msg);
  }

  public XDevException(Throwable cause){
    super(cause);	  
  }
}
