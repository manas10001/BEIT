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
 * File         : RTConstants.java 
 * Author(s)    : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, 
 *		  Bryan Carpenter, Farrukh Khan
 * Created      : Oct 28, 2013
 * Revision     : $
 * Updated      : Aug 27, 2014 
 */

package runtime.common;

public class RTConstants {

  public static String MPJ_PROCESS_INFO = "MPJProcessInfo";
  public static String CLASS_PATH = "ClassPath";

  // For master node(MPJRun.java server) information
  public static String MASTER_NODE = "MasterNode";
  public static String MASTER_PORT = "MasterPort";
  // ----------------------------------

  public static String PROCESS_COUNT = "ProcessCount";
  public static String STARTING_RANK = "StartingRank";
  public static String JVM_ARGS = "JvmArgs";
  public static String ARGUMENT = "Argument";
  public static String WORKING_DIRECTORY = "WorkingDirectory";
  public static String MAIN_CLASS = "MainClass";
  public static String DEVICE_NAME = "DeviceName";
  public static String CONF_FILE_CONTENTS = "ConfFileContents";
  public static String APP_ARGS = "AppArgs";
  public static String CLIENT_PORT = "ClientPort";
  public static String CLIENT_HOST_NAME = "ClientHostName";
  public static String TICKET_ID = "TicketID";
  public static String ZIPPED_SOURCE = "ZippedSource";
  public static String SOURCE_CODE = "sourceCode";
  public static String USER_ID = "UserID";
  public static String TOTAL_PROCESS_COUNT = "TotalProcessCount";
  public static String NETWORK_PROCESS_COUNT = "NetworkProcessCount";
  public static String NETWORK_DEVICE = "NetworkDevice";
  public static String MPJ_HOME = "MPJ_HOME";
  public static String MPJ_HOME_DIR = System.getenv("MPJ_HOME");
  public static String DEBUG = "Debug";
  public static String DEBUG_PORT = "DebugPort";
  public static String PROFILER = "Profiler";
  
  public static String MPJ_DAEMON_PORT_KEY = "mpjexpress.mpjdaemon.port.1";
  public static String MPJ_PORTMANAGER_PORT_KEY = "mpjexpress.mpjdaemon.port.2";
  public static String MPJ_DAEMON_PORT = "10000";
  public static String MPJ_PORTMANAGER_PORT = "10002";
  public static String MPJ_DAEMON_LOGLEVEL_KEY = "mpjexpress.mpjdaemon.loglevel";
  public static String MPJ_RUN_LOGLEVEL_KEY = "mpjexpress.mpjrun.loglevel";
  public static String MPJ_DAEMON_LOGLEVEL = "OFF";
  public static String MPJEXPRESS_VERSION_KEY = "mpjexpress.version";
  public static String MPJEXPRESS_CONF_FILE = "conf/mpjexpress.conf";
  public static String MPJDEV_CONF_FILE = "mpjdev.conf";

  // Variables added for Hadoop YARN support
  public static String HADOOP_YARN_HOME = System.getenv("HADOOP_HOME");
  public static String HADOOP_YARN = "false";
  // -------------------------------------------------
 
  public static String MPJ_RUN_SERVER_PORT_KEY = "mpjexpress.mpjrun.port.1";
  public static String MPJ_RUN_SERVER_PORT = "40003";
}
