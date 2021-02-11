/*
 The MIT License

 Copyright (c) 2005 
   1. Distributed Systems Group, University of Portsmouth
   2. Community Grids Laboratory, Indiana University 

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
 * File         : MulticoreDaemon.java 
 * Author       : Aamir Shafi
 * Created      : Wed Nov 18 13:35:21 PKT 2009
 * Revision     : $Revision: 1.0 $
 * Updated      : $Date: $
 */

package runtime.starter;

import java.nio.channels.*;
import java.nio.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.spi.LoggerRepository;

import runtime.common.MPJRuntimeException;
import runtime.daemon.*;
import java.util.concurrent.Semaphore;
import java.util.regex.*;

public class MulticoreDaemon {

  private BufferedReader reader = null;
  private InputStream outp = null;
  private String hostName = null;
  private PrintStream out = null;
  private Semaphore outputHandlerSem = new Semaphore(1, true);

  private String wdir = null;
  private int numOfProcs = 0;
  private int pos = 0;
  private String deviceName = null;
  private String className = null;
  private String mpjHome = null;
  private ArrayList<String> jvmArgs = new ArrayList<String>();
  private ArrayList<String> appArgs = new ArrayList<String>();
  private int processes = 0;
  private String cmd = null;
  private Process[] processVector = null;
  private static Logger logger = null;
  private String mpjHomeDir = null;
  private String loader = null;
  private boolean ADEBUG = false;
  private boolean APROFILE = false;
  private int DEBUG_PORT = 24500;

  public MulticoreDaemon(String mcClassName, String mcJarName, int classOrJar,
      int numOfProcessors, String workingDirectory, ArrayList<String> jvmArgs,
      ArrayList<String> appArgs, String mpjHomeDir, boolean ADEBUG, boolean APROFILE,
      int DEBUG_PORT) throws Exception {

    this.jvmArgs = jvmArgs;
    this.appArgs = appArgs;

    /* FIXME: It's a dirty hack .. */
    if (mcJarName.endsWith(".jar"))
      this.className = mcJarName;
    else
      this.className = mcClassName;

    this.processes = numOfProcessors;
    this.deviceName = "smpdev";
    this.loader = "useLocalLoader"; // don't need this

    this.mpjHomeDir = mpjHomeDir;
    this.ADEBUG = ADEBUG;
    this.APROFILE = APROFILE;
    this.DEBUG_PORT = DEBUG_PORT;

    if (workingDirectory == null) {
      this.wdir = System.getProperty("user.dir");
    } else {
      this.wdir = workingDirectory;
    }

    startNewProcess(mcClassName, numOfProcessors, workingDirectory, mcJarName,
	classOrJar);

  }

  public void startNewProcess(String mcClassName, int numOfProcessors,
      String workingDirectory, String jarName, int classOrJar) throws Exception {

    String cmdClassPath = "EMPTY";

    numOfProcs = Runtime.getRuntime().availableProcessors();
    InetAddress localaddr = InetAddress.getLocalHost();
    hostName = localaddr.getHostName();


    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("mpjHomeDir " + mpjHomeDir);
      MPJRun.logger.debug("McDaemon is waiting to accept connections ... ");
      MPJRun.logger.debug("wdir " + wdir);
      MPJRun.logger.debug("A client has connected");
    }

    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("the daemon will start <" + processes + "> threads");
    }

    String[] jArgs = jvmArgs.toArray(new String[0]);

    boolean now = false;
    boolean noSwitch = true;

    for (int e = 0; e < jArgs.length; e++) {

      if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
	MPJRun.logger.debug("jArgs[" + e + "]=" + jArgs[e]);
      }

      if (now) {
	cmdClassPath = jvmArgs.remove(e);

	if (cmdClassPath.matches("(?i).*mpj.jar.*")) {
	  // System.out.println("before <"+cmdClassPath+">");
	  // System.out.println("mpj.jar is present ...") ;
	  cmdClassPath = cmdClassPath.replaceAll("mpj\\.jar", "mpi.jar");
	  // cmdClassPath.replaceAll(Pattern.quote("mpj.jar"),
	  // Matcher.quoteReplacement("mpi.jar")) ;
	  // System.out.println("after <"+cmdClassPath+">");
	  // System.exit(0) ;
	}

	String cp = mpjHomeDir + "/lib/smpdev.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/xdev.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/mpjbuf.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/loader2.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/starter.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/mpiExp.jar";

	if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
	  MPJRun.logger.debug("cp = " + cp);
	}

	jvmArgs.add(e, cp);
	now = false;
      }

      if (jArgs[e].equals("-cp")) {
	now = true;
	noSwitch = false;
      }

    }

    if (noSwitch) {
      jvmArgs.add("-cp");

      String cp = mpjHomeDir + "/lib/smpdev.jar" + File.pathSeparator + ""
	  + mpjHomeDir + "/lib/xdev.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/mpjbuf.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/loader2.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/starter.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/mpiExp.jar";

      jvmArgs.add(cp);

      if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
	MPJRun.logger.debug("cp = " + cp);
      }
    }

    jArgs = jvmArgs.toArray(new String[0]);

    for (int e = 0; e < jArgs.length; e++) {
      if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
	MPJRun.logger.debug("modified: jArgs[" + e + "]=" + jArgs[e]);
      }
    }

    int CMD_WORDS = 8;
    /*
     * FIX ME BY AMJAD AZIZ : When launched in Debug Mode
     */
    if (ADEBUG)
      CMD_WORDS++;
    String[] aArgs = appArgs.toArray(new String[0]);
    String[] ex = new String[(CMD_WORDS + jArgs.length + aArgs.length)];
    if (APROFILE)
      ex[0] = "tau_java";
    else
      ex[0] = "java";

    for (int i = 0; i < jArgs.length; i++) {
      ex[i + 1] = jArgs[i];
    }

    int indx = jArgs.length + 1;
    /*
     * FIX ME BY AMJAD AZIZ : When launched in Debug Mode
     */
    if (ADEBUG)
      ex[indx++] = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address="
	  + DEBUG_PORT;
    ex[indx] = "runtime.starter.MulticoreStarter";
    indx++;
    ex[indx] = wdir;
    indx++;
    ex[indx] = Integer.toString(processes);
    indx++;
    ex[indx] = deviceName;
    indx++;
    ex[indx] = loader;
    indx++;
    ex[indx] = cmdClassPath;
    indx++;

    if (className != null) {
      ex[indx] = className;
    } else {
      ex[indx] = jarName;
    }

    for (int i = 0; i < aArgs.length; i++) {
      ex[i + CMD_WORDS + jArgs.length] = aArgs[i];
    }

    for (int i = 0; i < ex.length; i++) {
      if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
	MPJRun.logger.debug(i + ": " + ex[i]);
      }
    }

    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("creating process-builder object ");
    }

    ProcessBuilder pb = new ProcessBuilder(ex);

    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("wdir =" + wdir);
    }

    pb.directory(new File(wdir));
    pb.redirectErrorStream(true);

    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("starting the MultithreadStarter.");
    }

    Process p = null;

    try {
      p = pb.start();
    }
    catch (Exception e) {
      e.printStackTrace();
      return;
    }

    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("started the MultithreadStarter.");
    }

    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("Stopping the output");
    }

    String line = "";
    InputStream outp = p.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(outp));

    if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
      MPJRun.logger.debug("outputting ...");
    }

    try {
      do {
	if (!line.equals("")) {
	  line.trim();

	  synchronized (this) {
	    System.out.println(line);
	  }
	}
      } while ((line = reader.readLine()) != null);
    }
    catch (Exception e) {
      if (MPJRun.DEBUG && MPJRun.logger.isDebugEnabled()) {
	MPJRun.logger.debug("outputHandler =>" + e.getMessage());
      }
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {
    try {
      MulticoreDaemon dae = null;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
