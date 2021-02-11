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
 * File         : MPJDev.java
 * Author       : Bibrak Qamar
 * Created      : Tue Sept  17 11:15:11 PKT 2013
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 2014/03/11 11:30:00 $
 *
 */

package mpjdev;

/*
 This calss does the switching between Native or javampjdev MPJDev. 
 The javampjdev then further decides which xdev device to use depending
 on the use selection.
 */
import xdev.Device;
import mpjdev.Constants;

public class MPJDev {

  public static String deviceName;
  public static mpjdev.Comm WORLD = null;
  public static Device dev = null;

  public static Device init(String args[]) throws MPJDevException {

    if (args.length < 3) {

      throw new MPJDevException("Usage (for javampjdev): "
	  + "java MPJDev <myrank> <conf_file> <device_name>"
	  + "conf_file can be, ../conf/xdev.conf <Local>"
	  + "OR http://holly.dsg.port.ac.uk:15000/xdev.conf <Remote>"
	  + "\nUsage (for natmpjdev): " + "java MPJDev <x> <x> <device_name>"
	  + "ignore the first two arguments" + "device name = native");

    }

    deviceName = args[2]; // get and set the device name

    if (deviceName.equals("native")) {
      // for Native
      Constants.isNative = true;

      mpjdev.natmpjdev.MPJDev.init(args);
      WORLD = mpjdev.natmpjdev.MPJDev.WORLD;


    } else {
      // for javampjdev
      Constants.isNative = false;

      dev = mpjdev.javampjdev.MPJDev.init(args);
      WORLD = mpjdev.javampjdev.MPJDev.WORLD;
      
    }

    return dev;

  }

  /**
   * Gets the overhead incurred by send methods. It should be called after
   * calling #init(String[] args) method
   * 
   * @return int An integer specifying the overhead incurred by send methods
   */

  public static int getSendOverhead() {
    if (Constants.isNative) {
      return mpjdev.natmpjdev.MPJDev.getRecvOverhead();
    } else
      return mpjdev.javampjdev.MPJDev.getSendOverhead();
  }

  public static int getRecvOverhead() {
    if (Constants.isNative) {
      return mpjdev.natmpjdev.MPJDev.getRecvOverhead();
    } else
      return mpjdev.javampjdev.MPJDev.getRecvOverhead();
  }

  public static void finish() throws MPJDevException {
    if (Constants.isNative) {
   
    mpjdev.natmpjdev.MPJDev.finish();
    } else {
      mpjdev.javampjdev.MPJDev.finish();
    }
  }

}
