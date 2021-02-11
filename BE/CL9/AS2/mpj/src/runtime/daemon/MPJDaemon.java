/*
 The MIT License

 Copyright (c) 2005 - 2014
   1. Distributed Systems Group, University of Portsmouth (2014)
   2. Aamir Shafi (2005 - 2014)
   3. Bryan Carpenter (2005 - 2014)
   4. Mark Baker (2005 - 2014)

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
 * File         : MPJDaemon.java 
 * Author(s)    : Aamir Shafi, Bryan Carpenter, Khurram Shahzad, 
 *		  Mohsan Jameel, Farrukh Khan
 * Created      : Dec 12, 2004
 * Revision     : $Revision: 1.29 $
 * Updated      : Aug 27, 2014
 */

package runtime.daemon;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggerRepository;

import runtime.common.MPJRuntimeException;
import runtime.common.MPJUtil;
import runtime.common.RTConstants;

import runtime.daemonmanager.DMConstants;
import runtime.daemonmanager.MPJHalt;

public class MPJDaemon {

  private int D_SER_PORT;
  private int portManagerPort;
  static final boolean DEBUG = true;
  private String logLevel = "OFF";
  static Logger logger = null;
  private static String mpjHomeDir = null;
  public volatile static ConcurrentHashMap<Socket, ProcessLauncher> servSockets;

  ConnectionManager connectionManager;
  PortManagerThread pManager;

  public MPJDaemon(String args[]) throws Exception {

    InetAddress localaddr = InetAddress.getLocalHost();
    String hostName = localaddr.getHostName();
    servSockets = new ConcurrentHashMap<Socket, ProcessLauncher>();
    Map<String, String> map = System.getenv();

    try {
      mpjHomeDir = map.get("MPJ_HOME");
      RTConstants.MPJ_HOME_DIR = mpjHomeDir;
      if (mpjHomeDir == null) {
	throw new Exception("MPJ_HOME environment variable not set!!!");
      }
    }

    catch (Exception exc) {
      System.out.println("Error: " + exc.getMessage());
      exc.printStackTrace();
      return;
    }

    // Reading values from conf/mpjexpress.conf

    readValuesFromMPJExpressConf();
    createLogger(mpjHomeDir, hostName);

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("mpjHomeDir " + mpjHomeDir);
    }

    if (args.length == 1) {
      if (DEBUG && logger.isDebugEnabled()) {
	logger.debug(" args[0] " + args[0]);
	logger.debug("setting daemon port to" + args[0]);
      }
      D_SER_PORT = new Integer(args[0]).intValue();
    } 

    else {
      throw new MPJRuntimeException("Usage: java MPJDaemon daemonServerPort");
    }

    if (DEBUG && logger.isDebugEnabled())
        logger.debug("Starting PortManager thread .. ");

    // Invoking port manager
      pManager = new PortManagerThread(portManagerPort);
      pManager.start();

    if (DEBUG && logger.isDebugEnabled())
        logger.debug("Starting ConnectionManager thread .. ");

    // Invoking connection manager thread 
    connectionManager = new ConnectionManager();
    connectionManager.start();

    if (DEBUG && logger.isDebugEnabled())
        logger.debug("serverSocketInit .. ");

    serverSocketInit();
  }

  private void createLogger(String homeDir, String hostName)
      throws MPJRuntimeException {

    if (logger == null) {
      DailyRollingFileAppender fileAppender = null;

      try {
	if (logLevel.toUpperCase().equals("DEBUG")) {
	  fileAppender = new DailyRollingFileAppender(new PatternLayout(
	      " %-5p %c %x - %m\n"), homeDir + "/logs/daemon-" + hostName
	      + ".log", "yyyy-MM-dd-a");

	  Logger rootLogger = Logger.getRootLogger();
	  rootLogger.addAppender(fileAppender);
	  LoggerRepository rep = rootLogger.getLoggerRepository();
	  rootLogger.setLevel((Level) Level.ALL);
	}
	logger = Logger.getLogger("mpjdaemon");
	logger.setLevel(Level.toLevel(logLevel.toUpperCase(), Level.OFF));
      }
      catch (Exception e) {
	throw new MPJRuntimeException(e);
      }
    }
  }

  private void serverSocketInit() {
    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("serverSocketInit called .. ");
    }

    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(D_SER_PORT);
      do {
        if (DEBUG && logger.isDebugEnabled()) {
          logger.debug("Accepting connection ..");
        }
	Socket servSock = null; 
        try {
           servSock = serverSocket.accept();
 	} catch(Exception eee) { 
	   eee.printStackTrace(); 
	} 

	// Connection is accepted and the socket passed onto 
        // ProcessLauncher.java which takes care of the rest
	ProcessLauncher pLaunch = new ProcessLauncher(servSock);
	servSockets.put(servSock, pLaunch);
	pLaunch.start();
      } while (true);
    }
    catch (IOException ioEx) {
      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("Unable to attach to port!");
      }
      System.out.println("Unable to attach to port!");
      System.exit(1);
    } 
    catch (Exception e) { 
      e.printStackTrace();
    }

    if (!serverSocket.isClosed())
      try {
	serverSocket.close();
      }
      catch (IOException e) {
	e.printStackTrace();
      }
    if (pManager != null) {
      pManager.isRun = false;
    }
    if (connectionManager != null) {
      connectionManager.isRun = false;
    }
  }

  private void readValuesFromMPJExpressConf() {
    FileInputStream in = null;
    DataInputStream din = null;
    BufferedReader reader = null;
    String line = "";

    try {
      String path = mpjHomeDir + File.separator
	  + RTConstants.MPJEXPRESS_CONF_FILE;
      in = new FileInputStream(path);
      din = new DataInputStream(in);
      reader = new BufferedReader(new InputStreamReader(din));

      while ((line = reader.readLine()) != null) {
	if (line.startsWith(RTConstants.MPJ_PORTMANAGER_PORT_KEY)) {
	  portManagerPort = Integer.parseInt(MPJUtil.confValue(line));
	} else if (line.startsWith(RTConstants.MPJ_DAEMON_LOGLEVEL_KEY)) {
	  logLevel = MPJUtil.confValue(line);
	}
      }
      in.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String args[]) {
    try {
      MPJDaemon dae = new MPJDaemon(args);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
