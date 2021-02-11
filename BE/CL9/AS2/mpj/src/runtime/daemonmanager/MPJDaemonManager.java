/*
 The MIT License

 Copyright (c) 2013 - 2013
   1. High Performance Computing Group, 
   School of Electrical Engineering and Computer Science (SEECS), 
   National University of Sciences and Technology (NUST)
   2. Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter (2013 - 2013)
   

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
 * File         : MPJdaemonmanager.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
 * Created      : January 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 */

package runtime.daemonmanager;

import java.io.IOException;

import runtime.common.MPJUtil;
public class MPJDaemonManager {

  static boolean DEBUG = false; //FIXME: must be read from CLI

  public static void main(String[] args) {
    MPJUtil.readConfigFile();
    CLOptions options = new CLOptions();
    options.parseCommandLineArgs(args);

    if (options.getCmdType().equals(DMConstants.HELP)) {
      options.PrintHelp();
      return;
    }

    /* mpjboot - linux*/
    if (options.getCmdType().toLowerCase().equals(DMConstants.BOOT)) {
      MPJBoot mpBoot = new MPJBoot();
      mpBoot.bootMPJExpress(options);

    /* mpjhalt -linux */
    } else if (options.getCmdType().equals(DMConstants.HALT)) {
      MPJHalt mpHalt = new MPJHalt();
      mpHalt.haltMPJExpress(options);

    /* mpjclean */
    } else if (options.getCmdType().equals(DMConstants.CLEAN)) {
      MPJCleanup mpCleanup = new MPJCleanup();
      mpCleanup.cleanupMPJEnviroment(options);

    /* mpjstatus */
    } else if (options.getCmdType().equals(DMConstants.STATUS)) {
      MPJStatus mpQuery = new MPJStatus();
      mpQuery.getMPJExpressStatus(options);

    /* mpjinfo */
    } else if (options.getCmdType().equals(DMConstants.INFO)) {
      MPJProcessInfo mpInfo = new MPJProcessInfo();
      mpInfo.getJavaProcessInfo(options);

    /* mpjboot - windows */
    } else if (options.getCmdType().equals(DMConstants.WIN_BOOT)) {
      WinBoot winBoot = new WinBoot();
      try {
	winBoot.startMPJExpress(options.getPort());
      }
      catch (IOException e) {
	e.printStackTrace();
      }
    } 
    /* mpjhalt - windows */
    else if (options.getCmdType().equals(DMConstants.WIN_HALT)) {
      WinHalt winHalt = new WinHalt();
      winHalt.haltMPJExpress();
    /* otherwise print Help */
    } else {
      options.PrintHelp();
    }

    System.exit(0);

  }
}
