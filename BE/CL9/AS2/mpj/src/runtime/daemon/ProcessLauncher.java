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
 * File         : ProcessLauncher.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, 
 *		  Bryan Carpenter, Farrukh Khan
 * Created      : Oct 10, 2013
 * Revision     : $
 * Updated      : Aug 27, 2014
 */

package runtime.daemon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import runtime.common.MPJProcessTicket;

public class ProcessLauncher extends Thread {

  boolean DEBUG = true;
  private Process p[] = null;
  private Socket sockserver = null;
  private Logger logger = MPJDaemon.logger;
  int JvmProcessCount = 0;
  ProcessArgumentsManager argManager;

  public ProcessLauncher(Socket sockServer) {

    this.sockserver = sockServer;
  }

  @Override
  public void run() {
    ExecuteJob();
  }

  private void ExecuteJob() {

    if (DEBUG && logger.isDebugEnabled())
      logger.debug("Job Started");

    MPJProcessTicket pTicket = new MPJProcessTicket();

    /* The numbered comments followed by my name throughout this code 
     * have been added for better understanding only. They can be 
     * removed. (~ Farrukh)
     */

    // #1 Takes socket as an input and reads the ticket on the socket
    // being sent by the pack function on the other end (Farrukh)
    try {
      String ticketString = getStringFromInputStream(sockserver
	  .getInputStream());

      if (ticketString != "")
	pTicket.FromXML(ticketString);
      if (DEBUG && logger.isDebugEnabled()) {
	logger.debug(pTicket.ToXML(false).toXmlString());
      }

    }
    catch (IOException e3) {
      e3.printStackTrace();
      return;
    }

    if (pTicket.getDeviceName().equals("niodev")
	|| pTicket.getDeviceName().equals("mxdev")) {
      JvmProcessCount = pTicket.getProcessCount();
    } else if (pTicket.getDeviceName().equals("hybdev")) {
      JvmProcessCount = 1;
    }
    
    // #2 Initiate output handler thread to handle stdout (Farrukh)
    OutputHandler[] outputThreads = new OutputHandler[JvmProcessCount];
    p = new Process[JvmProcessCount];

    // #3 Passing the ticket to arguments manager for parsing (Farrukh)
    argManager = new ProcessArgumentsManager(pTicket);
    String[] arguments = argManager.GetArguments(pTicket);

    for (int j = 0; j < JvmProcessCount; j++) {
      if (pTicket.getDeviceName().equals("niodev")
	  || pTicket.getDeviceName().equals("mxdev")) {
	String rank = new String("" + (pTicket.getStartingRank() + j));
	arguments[argManager.getRankArgumentIndex()] = rank;
	if (pTicket.isProfiler())
	  arguments[1] = "-tau:node=" + rank;
      }
      if (pTicket.isDebug()
	  && (pTicket.getDeviceName().equals("niodev") || pTicket
	      .getDeviceName().equals("mxdev"))) {
	arguments[argManager.getDebugArgumentIndex()] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address="
	    + (pTicket.getDebugPort() + j * 2);
      }

      else if (pTicket.isDebug() && pTicket.getDeviceName().equals("hybdev")) {
	arguments[argManager.getDebugArgumentIndex()] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address="
	    + (pTicket.getDebugPort());
      }

      if (DEBUG && logger.isDebugEnabled()) {
	for (int i = 0; i < arguments.length; i++) {
	  logger.debug("arguments[" + i + "] = " + arguments[i]);
	}
      }
      // Process builder is used to launch the wrapper process
      ProcessBuilder pb = new ProcessBuilder(arguments);
      pb.directory(new File(pTicket.getWorkingDirectory()));
      pb.redirectErrorStream(true);

      if (DEBUG && logger.isDebugEnabled()) {
	logger.debug("starting the process ");
      }
      try {
	p[j] = pb.start();
      }
      catch (IOException e) {
	e.printStackTrace();
      }

      /*
       * Start a new thread to handle output from this particular JVM.
       * FIXME: Now this seems like a good amount of overhead. If we start 4
       * JVMs on a quad-core CPU, we also start 4 additional threads to handle
       * I/O. Is it possible to get rid of this overhead?
       */
      outputThreads[j] = new OutputHandler(p[j], sockserver);
      outputThreads[j].start();

      if (DEBUG && logger.isDebugEnabled()) {
	logger.debug("started the process ");
      }
    } 

    // Wait for the I/O threads to finish. They finish when
    // their corresponding JVMs finish.
    for (int j = 0; j < JvmProcessCount; j++) {
      try {
	outputThreads[j].join();
      }
      catch (InterruptedException e) {
	e.printStackTrace();
      }
    }

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("Stopping the output");
    }

    if (sockserver != null && !sockserver.isClosed()
	&& !sockserver.isOutputShutdown()) {
      OutputStream outToServer = null;
      try {
	outToServer = sockserver.getOutputStream();

	DataOutputStream out = new DataOutputStream(outToServer);
	out.write("EXIT".getBytes(), 0, "EXIT".getBytes().length);

        if (DEBUG && logger.isDebugEnabled())
          logger.debug("Job Finished");

	if (!DEBUG || !logger.isDebugEnabled()) {
         FileUtils.deleteDirectory(new File(argManager.getUsersDir()));
	}
      }
      catch (IOException e1) {
	e1.printStackTrace();
      }
      finally {
	if (!sockserver.isClosed())
	  try {
	    outToServer.close();
	    sockserver.close();
	  }
	  catch (IOException e) {
	    e.printStackTrace();
	  }
      }
    }
    try {
      killProcesses();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    MPJDaemon.servSockets.remove(sockserver);
    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("\n\n ** .. execution ends .. ** \n\n");
    }

  }

  public void killProcesses() {
    synchronized (p) {
      for (int i = 0; i < JvmProcessCount; i++)
	p[i].destroy();

    }
    for (Integer port : argManager.getProcessesPorts()) {
      PortManager.usedPorts.remove(port);
    }
    try {
      if (!sockserver.isClosed())
	sockserver.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    try {
      if (!DEBUG || !logger.isDebugEnabled()) {
      FileUtils.deleteDirectory(new File(argManager.getUsersDir()));
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

  private String getStringFromInputStream(InputStream is) {

    DataInputStream in = new DataInputStream(is);
    int len = 0;
    try {
      len = in.readInt();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("Ticket length " + len);
    }
    byte[] xml = new byte[len];
    try {

      in.readFully(xml, 0, len);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return new String(xml);
  }
}
