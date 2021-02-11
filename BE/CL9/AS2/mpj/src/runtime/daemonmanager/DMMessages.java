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
 * File         : PMMessages.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
 * Created      : January 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 */
package runtime.daemonmanager;

import runtime.common.MPJUtil;

public class DMMessages {
  public static String MPJ_HOME_DIR_NOT_AVAILABLE = "MPJ_HOME directory is not available";
  public static String UNKNOWN_HOST = "Name or service not known";
  public static String INVALID_PORT = "Invalid port specified in mpjexpress.mpjdaemon.port.1 property of mpjexpress.conf file ";
  public static String BUSY_PORT = "Port is not available, Set a different port by modifying the mpjexpress.mpjdaemon.port.1 property in the "
      + MPJUtil.getMPJExpressConfPath();
  public static String MPJDAEMON_ALREADY_RUNNING = "MPJ daemon already running with process id: ";
  public static String MACHINE_FILE_NOT_SPECIFED = "Machine file is not specified";
  public static String MPJDAEMON_STARTED = "MPJ Daemon started successfully with process id: ";
  public static String MPJDAEMON_NOT_STARTED = "Unable to start MPJ Express daemon";
  public static String MPJDAEMON_NOT_AVAILABLE = "MPJ Daemon is not running ";
  public static String MPJDAEMON_AVAILABLE = "MPJ Daemon is running with process id: ";
  public static String MPJDAEMON_STOPPED = "MPJ Daemon stopped ";
  public static String HOST_INACESSABLE = " is not accessable";
  public static String JAVA_PROCESS_KILLED = " Killed all java processes";
  public static String NO_JAVA_PROCESS_RUNNING = " No java process is running";

  public static String CMD_OPT_BOOT = "Start MPJ Express daemons on given machine(s)";
  public static String CMD_OPT_HALT = "Stop MPJ Express daemons on given  machine(s)";
  public static String CMD_OPT_CLEAN = "Clean all java  process on given machine(s)";
  public static String CMD_OPT_STATUS = "Get status of MPJ Express daemons on given  machine(s) ";
  public static String CMD_OPT_INFO = "Get all java processes on given  machine(s)";

  public static String CMD_OPT_MACHINE_FILE = "Machine File e.g. <machines>";
  public static String CMD_OPT_THREAD_COUNT = "Number of threads";
  public static String CMD_OPT_THREADED = "<true|false> use multithreading";
  public static String CMD_OPT_HOSTS = "Host name seperated by space <localhost1 localhost2 ... >";
  public static String CMD_OPT_PORT = "Port no for MPJ Express Daemon listerner";
  public static String CMD_OPT_HELP = "Help for process manager command line option";

}
