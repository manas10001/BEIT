/*
 The MIT License
 
 Copyright (c) 2005 - 2008
 1. Distributed Systems Group, University of Portsmouth (2005)
 2. Community Grids Laboratory, Indiana University (2005)
 3. Aamir Shafi (2005 - 2008)
 4. Bryan Carpenter (2005 - 2008)
 5. Mark Baker (2005 - 2008)
 
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
 * File         : MPI.java
 * Author       : Aamir Shafi, Bryan Carpenter
 * Created      : Fri Sep 10 12:22:15 BST 2004
 * Revision     : $Revision: 1.5 $
 * Updated      : $Date: 2014/07/11 13:26:15 PKT $
 */

package mpi;

import mpjdev.*;
import mpjbuf.*;

import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.spi.LoggerRepository;

public class MPI {
 
  public static final boolean DEBUG = true;
  static Logger logger = null;

  public static Intracomm COMM_WORLD;
  private static boolean initialized = false;
  static boolean debug = true;
  public static boolean isOldSelected = false; // check for Flat Tree
					       // Collectives
  static ByteBuffer buffer = null;
  static ArrayList<Request> pendingRequests = new ArrayList<Request>();

  static final int OP_BSEND = 1;
  static final int OP_RSEND = 2;
  static final int OP_SEND = 3;
  static final int OP_RECV = 4;
  static final int OP_SSEND = 5;
  public static final int NUM_OF_PROCESSORS = Runtime.getRuntime()
      .availableProcessors();

  static Status UNDEFINED_STATUS = new Status(-1, -1, -1);

  // public static Datatype UNDEFINED = new BasicType( -1);
  public static int UNDEFINED = -1;
  public static Datatype NULL = new BasicType(0);
  public static Datatype BYTE = new BasicType(1);
  public static Datatype CHAR = new BasicType(2);
  public static Datatype SHORT = new BasicType(3);
  public static Datatype BOOLEAN = new BasicType(4);
  public static Datatype INT = new BasicType(5);
  public static Datatype LONG = new BasicType(6);
  public static Datatype FLOAT = new BasicType(7);
  public static Datatype DOUBLE = new BasicType(8);
  public static Datatype PACKED = new BasicType(9);
  public static Datatype LB = new BasicType(10);
  public static Datatype UB = new BasicType(11);
  public static Datatype OBJECT = new BasicType(12);

  public static int THREAD_SINGLE = 1;
  public static int THREAD_FUNNELED = 2;
  public static int THREAD_SERIALIZED = 3;
  public static int THREAD_MULTIPLE = 4;

  /**
   * Its actually not good to call the following basic datatypes because they
   * are not ... this will be changed to sometyhing else ...need to think about
   * it
   */
  public static Datatype SHORT2 = Datatype.Contiguous(2, MPI.SHORT);
  public static Datatype INT2 = Datatype.Contiguous(2, MPI.INT);
  public static Datatype LONG2 = Datatype.Contiguous(2, MPI.LONG);
  public static Datatype FLOAT2 = Datatype.Contiguous(2, MPI.FLOAT);
  public static Datatype DOUBLE2 = Datatype.Contiguous(2, MPI.DOUBLE);

  public static Op MAX = new Max();
  public static Op MIN = new Min();
  public static Op SUM = new Sum();
  public static Op PROD = new Prod();
  public static Op LAND = new Land();
  public static Op BAND = new Band();
  public static Op LOR = new Lor();
  public static Op BOR = new Bor();
  public static Op LXOR = new Lxor();
  public static Op BXOR = new Bxor();
  public static Op MAXLOC = new Op(new Maxloc(), true,
      mpjdev.Constants.MAXLOC_CODE);
  public static Op MINLOC = new Op(new Minloc(), true,
      mpjdev.Constants.MINLOC_CODE);

  public static int ANY_SOURCE = -2, ANY_TAG = -2;

  public static Status EMPTY_STATUS = new Status(MPI.ANY_SOURCE, MPI.ANY_TAG,
      0, 0);
  public static int PROC_NULL = -3;

  /**
   * Overhead incurred by buffered send. This variable should be accessed after
   * calling #Init(String[] args) method.
   */
  public static int BSEND_OVERHEAD;

  /**
   * These should be accessed after calling MPI.Init()
   */
  public static int SEND_OVERHEAD, RECV_OVERHEAD;
  // public static Datatype UNDEFINED;
  // This is initialized in the MPJDev.Init() and is then assigned from
  // Constants.GROUP_EMPTY
  public static Group GROUP_EMPTY;// = new mpi.Group(
  // new mpjdev.Group(new xdev.ProcessID[0], null,-1));

  public static Comm COMM_SELF;
  public static final int IDENT = 0, CONGRUENT = 3, SIMILAR = 1, UNEQUAL = 2;
  public static int GRAPH = 1, CART = 2;
  public static Errhandler ERRORS_ARE_FATAL, ERRORS_RETURN;
  public static int TAG_UB, HOST, IO;
  public static Request REQUEST_NULL = new mpi.Request(true);
  public static Comm COMM_NULL;
  public static Group GROUP_NULL;

  /**
   * Provides to MPI a buffer in user's memory to be used for buffering outgoing
   * messages. Java binding of the MPI operation <tt>MPI_BUFFER_ATTACH</tt>.
   */
  public static void Buffer_attach(ByteBuffer userBuffer) throws MPIException {
    buffer = userBuffer;
  }

  /**
   * Detach the buffer currently associated with MPI. Java binding of the MPI
   * operation <tt>MPI_BUFFER_DETACH</tt>.
   */
  public static void Buffer_detach() throws MPIException {

    synchronized (pendingRequests) {

      for (Request pending : pendingRequests) {
	// apparently may not make sense for bsend, but under the hoods
	// bsend is using isend for comms, and there is a request
	// object attached

	pending.Wait();
      }

      buffer = null;

    }

    Comm.PendingMessage.clearFront();

  }

  /**
   * Used to initialize MPI with certain level of threadedness ...
   */
  public static String[] initThread(int required, int provided, String[] argv) {
    return null;
  }

  /**
   * Returns true if this thread initialized MPI
   */
  public static boolean isMainThread() {
    return true;
  }

  /**
   * Returns the level of thread support provided by the MPI library and the
   * underlying device selected
   */
  public static int queryThread() {
    return MPI.THREAD_MULTIPLE;
  }

  /**
   * Initialize MPI.
   * <p>
   * <table>
   * <tr>
   * <td><tt> args </tt></td>
   * <td>arguments to <tt>main</tt> method.
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_INIT</tt>.
   */
  public static String[] Init(String[] argv) throws MPIException {

    if (argv.length < 3) {
      throw new MPIException("Usage: "
	  + "java MPI <myrank> <conf_file> <device_name> "
	  + "conf_file can be, ../conf/xdev.conf <Local>"
	  + "OR http://holly.dsg.port.ac.uk:15000/xdev.conf <Remote>");
    }

    DailyRollingFileAppender fileAppender = null;
    int rank = Integer.parseInt(argv[0]);
    Map<String, String> map = System.getenv();
    String mpjHomeDir = map.get("MPJ_HOME");
    String username = System.getProperty("user.name");
    String level = "";
    FileInputStream in = null;
    DataInputStream din = null;
    BufferedReader reader = null;
    String line = "";

    try {

      String path = mpjHomeDir + "/conf/mpjexpress.conf";
      in = new FileInputStream(path);
      din = new DataInputStream(in);
      reader = new BufferedReader(new InputStreamReader(din));

      while ((line = reader.readLine()) != null) {
	if (line.startsWith("mpjexpress.mpi.loglevel")) {
	  String trimmedLine = line.replaceAll("\\s+", "");
	  StringTokenizer tokenizer = new StringTokenizer(trimmedLine, "=");
	  tokenizer.nextToken();
	  level = tokenizer.nextToken();
	} else if (line.startsWith("mpjexpress.mpi.old.collectives")) {
	  String trimmedLine = line.replaceAll("\\s+", "");
	  StringTokenizer tokenizer = new StringTokenizer(trimmedLine, "=");
	  tokenizer.nextToken();
	  isOldSelected = Boolean.parseBoolean(tokenizer.nextToken());
	}
      }

      in.close();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    if (logger == null && DEBUG) {
      try {
	if (level.toUpperCase().equals("DEBUG")) {
	  fileAppender = new DailyRollingFileAppender(new PatternLayout(
	      " %-5p %c %x - %m\n"), mpjHomeDir + "/logs/" + username + "-mpj-"
	      + rank + ".log", "yyyy-MM-dd-HH");

	  Logger rootLogger = Logger.getRootLogger();
	  rootLogger.addAppender(fileAppender);
	  LoggerRepository rep = rootLogger.getLoggerRepository();
	  rootLogger.setLevel((Level) Level.ALL);
	}
	// rep.setThreshold((Level) Level.OFF ) ;
	logger = Logger.getLogger("mpj");
	logger.setLevel(Level.toLevel(level.toUpperCase(), Level.OFF));
      }
      catch (Exception e) {
	throw new MPIException(e);
      }
    }

    try {
      mpjdev.MPJDev.init(argv);
      GROUP_EMPTY = new mpi.Group(null);

      BSEND_OVERHEAD = MPJDev.getSendOverhead();
      SEND_OVERHEAD = BSEND_OVERHEAD;
      RECV_OVERHEAD = MPJDev.getRecvOverhead();

      COMM_WORLD = new Intracomm(mpjdev.MPJDev.WORLD, mpjdev.MPJDev.WORLD.group);

      int tagub = Integer.MAX_VALUE;

      if (mpjdev.Constants.isNative)
	tagub = ((mpjdev.natmpjdev.Comm) COMM_WORLD.mpjdevComm).getMPI_TAG_UB();

      COMM_WORLD.Attr_put(MPI.TAG_UB, tagub);
      COMM_WORLD.Attr_put(MPI.HOST, PROC_NULL);
      COMM_WORLD.Attr_put(MPI.IO, COMM_WORLD.Rank());

    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    int[] self = { COMM_WORLD.Rank() };
    Group selfGroup = COMM_WORLD.group.Incl(self);
    COMM_SELF = COMM_WORLD.Create(selfGroup);

    int[] empty = new int[0];
    GROUP_EMPTY = COMM_WORLD.group.Incl(empty);
    GROUP_EMPTY.code = Group.EMPTY;

    // use these three in case of errors..
    GROUP_NULL = new Group(Group.NULL);
    COMM_NULL = new Comm(Comm.NULL);
    // REQUEST_NULL = new Request(Request.NULL) ;

    ERRORS_ARE_FATAL = new Errhandler(Errhandler.FATAL);
    ERRORS_ARE_FATAL = new Errhandler(Errhandler.RETURN);

    /*
     * this is 3 coz i know MPI/MPJDev/xdev init methods only use the first four
     * arguments ..if in future, we write a device which takes more argument,
     * then this '3' may have to be changed
     */
    String[] nargs = null;
    if (argv[2].equals("hybdev")) {
      nargs = new String[(argv.length - 8)];
      System.arraycopy(argv, 8, nargs, 0, nargs.length);
    } else {
      nargs = new String[(argv.length - 3)];
      System.arraycopy(argv, 3, nargs, 0, nargs.length);
    }
    initialized = true;

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("pendingQue.size " + pendingRequests.size());
      logger.debug(" queue_size (init) " + Comm.PendingMessage.queue_size);
    }

    return nargs;
  }

  /**
   * Finalize MPI.
   * <p>
   * Java binding of the MPI operation <tt>MPI_FINALIZE</tt>.
   */
  public static void Finalize() throws MPIException {
    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("calling detach");
    }

    MPI.Buffer_detach();

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("called detach");
      logger.debug(" queue_size (exit) " + Comm.PendingMessage.queue_size);
      logger.debug("pendingQue.size " + pendingRequests.size());
      logger.debug("calling last barrier");
    }

    try {
      COMM_WORLD.Barrier();
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new MPIException(e);
    }

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("Called it");
    }

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("Calling MPJDev finalize ");
    }

    try {
      mpjdev.MPJDev.finish();
    }
    catch (Exception e) {
      throw new MPIException(e);
    }

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("Called it ...");
    }

    initialized = false;

  }

  /**
   * Returns the name of the processor on which it is called.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>A unique specifier for the actual node.
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_GET_PROCESSOR_NAME</tt>.
   */
  public static String Get_processor_name() throws MPIException {
    try {
      java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
      byte[] ipAddr = addr.getAddress();
      String hostname = addr.getHostName();
      return hostname;
    }
    catch (java.net.UnknownHostException e) {
      throw new MPIException(e);
    }
  }

  /**
   * Set Error Handler <font color="RED"> Not Implemented in the current release
   * </font>
   */
  public static void Errorhandler_set(Errhandler errhandler)
      throws MPIException {
  }

  /**
   * Gets the error handler <font color="RED"> Not Implemented in the current
   * release </font>
   */
  public static Errhandler Errorhandler_get() throws MPIException {
    return null;
  }

  /**
   * Returns wallclock time.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>elapsed wallclock time in seconds since some time in the past
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_WTIME</tt>.
   */
  public static double Wtime() throws MPIException {
    return (System.currentTimeMillis() / 1000);
  }

  /**
   * Returns resolution of timer.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td>resolution of <tt>wtime</tt> in seconds.
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_WTICK</tt>.
   */
  public static double Wtick() throws MPIException {
    double time = 0L;
    double newTime;
    double smaller = Double.MAX_VALUE;

    for (int i = 0; i < 100000; i++) {
      time = Wtime();
      newTime = Wtime();
      smaller = Math.min(smaller, (newTime - time));
    }

    return smaller;
  }

  /**
   * Test if MPI has been initialized.
   * <p>
   * <table>
   * <tr>
   * <td><em> returns: </em></td>
   * <td> <tt>true</tt> if <tt>Init</tt> has been called, <tt>false</tt>
   * otherwise.
   * </tr>
   * </table>
   * <p>
   * Java binding of the MPI operation <tt>MPI_INITIALIZED</tt>.
   */
  public static boolean Initialized() throws MPIException {
    return initialized;
  }

}
