/*
 The MIT License

 Copyright (c) 2013 - 2014
   1. High Performance Computing Group, 
   School of Electrical Engineering and Computer Science (SEECS), 
   National University of Sciences and Technology (NUST)
   2. Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter (2013 - 2014)
   

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
 * File         : PMConstants.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
 * Created      : January 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 */

package runtime.daemonmanager;

import runtime.common.MPJUtil;

public class DMConstants {
  public static String BOOT = "boot";
  public static String WIN_BOOT = "winboot";
  public static String HALT = "halt";
  public static String WIN_HALT = "winhalt";
  public static String CLEAN = "clean";
  public static String STATUS = "status";
  public static String INFO = "info";
  public static String CONF = "conf";
  public static String LIB = "lib";
  public static String LOGS = "logs";
  public static String MPJEXPRESS_LOG = "mpjexpress.log";
  public static String MPJEXPRESS_CONF = "mpjexpress.conf";
  public static String EXT_JAR = ".jar";
  public static String MACHINES = "machines";
  public static String MPJDAEMON = "MPJDaemon";
  public static String GREP_JAVA = "ps aux|grep java";

  public static String BOOT_OPT = "b";
  public static String WIN_BOOT_OPT = "wb";
  public static String PORT_OPT = "p";
  public static String HALT_OPT = "h";
  public static String WIN_HALT_OPT = "wh";
  public static String CLEAN_OPT = "c";
  public static String INFO_OPT = "i";
  public static String STATUS_OPT = "s";
  public static String MACHINE_FILE_OPT = "m";
  public static String HOSTS_OPT = "ht";
  public static String HELP_OPT = "help";

  public static String THREAD_COUNT_OPT = "nt";
  public static String THREADED_OPT = "th";
  public static String MACHINE_FILE = "machinesfile";
  public static String THREAD_COUNT = "nthreads";
  public static String THREADED = "threaded";
  public static String HOSTS = "hosts";
  public static String PORT = "port";
  public static String HELP = "help";

}
