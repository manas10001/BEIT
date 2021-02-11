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
 * File         : natmpjdev.MPJDev.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

package mpjdev.natmpjdev;

import mpjdev.*;
import org.apache.log4j.Logger;

public class MPJDev {

  public static Comm WORLD = null;

  static Logger logger = Logger.getLogger("mpj");

  public static void init(String args[]) throws MPJDevException {

    if (args.length < 3) {

      throw new MPJDevException("Usage: " + "java MPJDev <x> <x> <device_name>"
	  + "ignore the first two arguments x x device_name = native");

    }
    if (Comm.initialized) {
      return;
    }

    Comm.init(args);
    WORLD = mpjdev.natmpjdev.Comm.WORLD;

  }

  /**
   * Gets the overhead incurred by send methods. It should be called after
   * calling #init(String[] args) method
   * 
   * @return int An integer specifying the overhead incurred by send methods
   */
  public static int getSendOverhead() {

    return mpjdev.Constants.NATIVE_SEND_OVERHEAD;
  }

  /**
   * Gets the overhead incurred by recv methods. It should be called after
   * calling #init(String[] args) method
   * 
   * @return int Number of bytes overhead incurred by recv methods
   */
  public static int getRecvOverhead() {

    return mpjdev.Constants.NATIVE_RECV_OVERHEAD;
  }

  public static void finish() throws MPJDevException {
    try {
      Comm.finish();
    }
    catch (MPJDevException mdeve) {
      throw new MPJDevException(mdeve);
    }

  }

}
