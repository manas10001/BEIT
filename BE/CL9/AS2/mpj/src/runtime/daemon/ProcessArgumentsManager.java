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
 * File         : ProcessArgumentsManager.java 
 * Author(s)    : Aamir Shafi, Bryan Carpenter, Khurram Shahzad,
 *		  Farrukh Khan
 * Created      : Oct 10, 2013
 * Revision     : $
 * Updated      : Aug 27, 2014
 */


package runtime.daemon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;

import runtime.common.IOHelper;
import runtime.common.MPJProcessTicket;

public class ProcessArgumentsManager {

  ArrayList<Integer> ports = new ArrayList<Integer>();
  String sourceFolder = "";
  MPJProcessTicket pTicket;
  String configFilePath = "";
  String ticketDir = "";
  int rankArgumentIndex;
  int debug_argument_index;

  String userDir = "";
  String usersDir = "";
  String sourcePath = "";

  public void setSourcePath(String sourcePath) {
    File f = new File(sourcePath);
    String files[] = f.list();
    this.sourcePath = sourcePath + "/" + files[0];
  }

  public String getUsersDir() {
    return usersDir;
  }

  public void setUsersDir(String usersDir) {
    this.usersDir = usersDir;
  }

  public String getTicketDir() {
    return ticketDir;
  }

  public void setTicketDir(String ticketDir) {
    this.ticketDir = ticketDir;
  }

  public String getUserDir() {
    return userDir;
  }

  public void setUserDir(String userDir) {
    this.userDir = userDir;
  }

  public ProcessArgumentsManager(MPJProcessTicket pTicket) {
    this.pTicket = pTicket;
  }

  public ArrayList<Integer> getProcessesPorts() {
    return ports;
  }

  public int getRankArgumentIndex() {
    return rankArgumentIndex;
  }

  public void setRankArgumentIndex(int rankArgumentIndex) {
    this.rankArgumentIndex = rankArgumentIndex;
  }

  public int getDebugArgumentIndex() {
    return debug_argument_index;
  }

  public void setDebugArgumentIndex(int debug_argument_index) {
    this.debug_argument_index = debug_argument_index;
  }

  public String[] GetArguments(MPJProcessTicket pTicket) {
    if (pTicket.getDeviceName().equals("niodev")
	|| pTicket.getDeviceName().equals("mxdev")) {
      return GetNIODeviceArguments();
    } else if (pTicket.getDeviceName().equals("hybdev")) {
      return GetHybridDeviceArguments();
    }
    return null;

  }

  public String[] GetNIODeviceArguments() {

    WriteSourceFile();
    WriteConfigFile();

    Map<String, String> map = System.getenv();
    String mpjHomeDir = pTicket.getMpjHomeDir();
    String workingDirectory = pTicket.getWorkingDirectory();
    if (pTicket.isZippedSource()) {
      if (pTicket.getClassPath().endsWith(".jar")) {
	File f = new File(pTicket.getClassPath());
	pTicket.setClassPath(sourcePath + "/" + f.getName());

      } else
        pTicket.setClassPath(sourcePath);
      workingDirectory = sourcePath;
    }

    boolean now = false;
    boolean noSwitch = true;
    for (int e = 0; e < pTicket.getJvmArgs().size(); e++) {

      if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	MPJDaemon.logger.debug("jArgs[" + e + "]="
	    + pTicket.getJvmArgs().get(e));
      }

      if (now) {
	String cp = pTicket.getJvmArgs().remove(e);
	cp = "." + File.pathSeparator + "" + mpjHomeDir + "/lib/loader1.jar"
	    + File.pathSeparator + "" + mpjHomeDir + "/lib/mpj.jar"
	    + File.pathSeparator + "" + mpjHomeDir + "/lib/log4j-1.2.11.jar"
	    + File.pathSeparator + "" + mpjHomeDir + "/lib/wrapper.jar"
	    + File.pathSeparator + pTicket.getClassPath() + File.pathSeparator
	    + workingDirectory + File.pathSeparator + cp;

	pTicket.getJvmArgs().add(e, cp);
	now = false;
      }

      if (pTicket.getJvmArgs().get(e).equals("-cp")) {
	now = true;
	noSwitch = false;
      }
    }

    if (noSwitch) {
      pTicket.getJvmArgs().add("-cp");
      pTicket.getJvmArgs().add(
	  "." + File.pathSeparator + "" + mpjHomeDir + "/lib/loader1.jar"
	      + File.pathSeparator + "" + mpjHomeDir + "/lib/mpj.jar"
	      + File.pathSeparator + "" + mpjHomeDir + "/lib/log4j-1.2.11.jar"
	      + File.pathSeparator + "" + mpjHomeDir + "/lib/wrapper.jar"
	      + File.pathSeparator + pTicket.getClassPath()
	      + File.pathSeparator + workingDirectory);
    }

    for (int e = 0; e < pTicket.getJvmArgs().size(); e++) {
      if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	MPJDaemon.logger.debug("modified: jArgs[" + e + "]="
	    + pTicket.getJvmArgs().get(e));
      }
    }

    // This segment of the code reads values from ticket and makes an
    // argument array
    int N_ARG_COUNT = 9;
    int increment = 1;

    int nArgumentIncrement = 0;
    if (pTicket.isProfiler())
      nArgumentIncrement++;
    if (pTicket.isDebug())
      nArgumentIncrement++;

    String[] arguments = new String[(N_ARG_COUNT + pTicket.getJvmArgs().size() + pTicket
	.getAppArgs().size()) + nArgumentIncrement];
    if (pTicket.isProfiler()) {
      arguments[0] = "tau_java";
      increment++;
    } else
      arguments[0] = "java";

    for (int i = 0; i < pTicket.getJvmArgs().size(); i++) {
      arguments[i + increment] = pTicket.getJvmArgs().get(i);
    }

    int indx = pTicket.getJvmArgs().size() + increment;

    if (pTicket.isDebug()) {
      setDebugArgumentIndex(indx);
      indx++;
    }

    arguments[indx] = "runtime.daemon.Wrapper";
    indx++;

    // Modifying argument processing for NIODevInit
    if(pTicket.getDeviceName().equals("mxdev")){
      arguments[indx] = configFilePath;
    }
    else{
      arguments[indx] = pTicket.getConfFileContents();
      arguments[indx] = arguments[indx].replace(' ','|');
    }
    indx++;
    arguments[indx] = Integer.toString(pTicket.getProcessCount());
    indx++;
    arguments[indx] = pTicket.getDeviceName();
    indx++;

    // Two extra arguments added to include MPJRun.java server
    // IP and port number
    arguments[indx] = pTicket.getMasterNode();
    indx++;
    arguments[indx] = pTicket.getMasterPort();
    indx++;

    arguments[indx] = "" + (-1);
    
    rankArgumentIndex = indx;
    indx++;
    arguments[indx] = pTicket.getMainClass();
    
    for (int i = 0; i < pTicket.getAppArgs().size(); i++) {
      arguments[i + N_ARG_COUNT + pTicket.getJvmArgs().size()
	  + nArgumentIncrement] = pTicket.getAppArgs().get(i);
    }
    return arguments;

  }

  public String[] GetHybridDeviceArguments() {
    WriteSourceFile();
    WriteConfigFile();

    Map<String, String> map = System.getenv();
    String mpjHomeDir = pTicket.getMpjHomeDir();
    boolean now = false;
    boolean noSwitch = true;
    String cmdClassPath = " ";
    String workingDirectory = pTicket.getWorkingDirectory();
    if (pTicket.isZippedSource()) {
      if (pTicket.getClassPath().endsWith(".jar")) {
	File f = new File(pTicket.getClassPath());
	pTicket.setClassPath(sourcePath + "/" + f.getName());

      } else
        pTicket.setClassPath(sourcePath);
      workingDirectory = sourcePath;
    }
    String[] jArgs = pTicket.getJvmArgs().toArray(new String[0]);
    for (int e = 0; e < jArgs.length; e++) {

      if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	MPJDaemon.logger.debug("jArgs[" + e + "]=" + jArgs[e]);
      }

      if (now) {
	cmdClassPath = pTicket.getJvmArgs().remove(e);

	if (cmdClassPath.matches("(?i).*mpj.jar.*")) {
	  // System.out.println("before <"+cmdClassPath+">");
	  // System.out.println("mpj.jar is present ...") ;
	  cmdClassPath = cmdClassPath.replaceAll("mpj\\.jar", "mpi.jar");
	  // cmdClassPath.replaceAll(Pattern.quote("mpj.jar"),
	  // Matcher.quoteReplacement("mpi.jar")) ;
	  // System.out.println("after <"+cmdClassPath+">");
	  // System.exit(0) ;
	}
	// adding hybdev.jar and niodev.jar
	String cp = mpjHomeDir + "/lib/hybdev.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/xdev.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/smpdev.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/niodev.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/mpjbuf.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/loader2.jar" + File.pathSeparator + ""
	    + mpjHomeDir + "/lib/starter.jar" + File.pathSeparator
	    // + ""+ pTicket.getClassPath() +File.pathSeparator
	    + "" + mpjHomeDir + "/lib/mpiExp.jar";

	if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	  MPJDaemon.logger.debug("cp = " + cp);
	}

	pTicket.getJvmArgs().add(e, cp);
	now = false;
      }

      if (jArgs[e].equals("-cp")) {
	now = true;
	noSwitch = false;
      }

    }

    if (noSwitch) {
      pTicket.getJvmArgs().add("-cp");

      // adding hybdev.jar and niodev.jar
      String cp = mpjHomeDir + "/lib/hybdev.jar" + File.pathSeparator + ""
	  + mpjHomeDir + "/lib/xdev.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/smpdev.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/niodev.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/mpjbuf.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/loader2.jar" + File.pathSeparator + "" + mpjHomeDir
	  + "/lib/starter.jar" + File.pathSeparator
	  // + "" + pTicket.getWorkingDirectory() +File.pathSeparator
	  // + ""+ pTicket.getClassPath() +File.pathSeparator
	  + "" + mpjHomeDir + "/lib/mpiExp.jar";

      pTicket.getJvmArgs().add(cp);

      if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	MPJDaemon.logger.debug("cp = " + cp);
      }
    }

    jArgs = pTicket.getJvmArgs().toArray(new String[0]);

    for (int e = 0; e < jArgs.length; e++) {
      if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	MPJDaemon.logger.debug("modified: jArgs[" + e + "]=" + jArgs[e]);
      }
    }

    int CMD_WORDS = 8;
    int HYB_ARGS = 5;
    int increment = 1;

    String[] aArgs = pTicket.getAppArgs().toArray(new String[0]);
    int nArgumentIncrement = 0;
    if (pTicket.isProfiler())
      nArgumentIncrement++;
    if (pTicket.isDebug())
      nArgumentIncrement++;

    String[] arguments = new String[(CMD_WORDS + jArgs.length + HYB_ARGS + aArgs.length)
	+ nArgumentIncrement];
    if (pTicket.isProfiler()) {
      arguments[0] = "tau_java";
      arguments[1] = "-tau:node=" + Integer.toString(pTicket.getStartingRank());
      increment++;
    } else
      arguments[0] = "java";

    for (int i = 0; i < jArgs.length; i++) {
      arguments[i + increment] = jArgs[i];
    }

    int indx = jArgs.length + increment;

    if (pTicket.isDebug()) {
      setDebugArgumentIndex(indx);
      indx++;
    }

    arguments[indx] = "runtime.daemon.HybridStarter";
    indx++;
    arguments[indx] = workingDirectory;
    indx++;
    int threadPerHost = getThreadsPerHost(pTicket.getTotalProcessCount(),
	pTicket.getNetworkProcessCount(), pTicket.getStartingRank());
    arguments[indx] = Integer.toString(threadPerHost);
    indx++;
    arguments[indx] = pTicket.getDeviceName();
    indx++;
    arguments[indx] = "useLocalLoader";
    indx++;
    arguments[indx] = cmdClassPath;
    indx++;

    if (pTicket.getClassPath().endsWith(".jar"))
      arguments[indx] = pTicket.getClassPath();
    else if (pTicket.getMainClass() != null)
      arguments[indx] = pTicket.getMainClass();
    else
      arguments[indx] = pTicket.getClassPath();

    if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
      MPJDaemon.logger.debug("HybridDaemon: Value of Indx: " + indx
	  + " Count of args till now: "
	  + (CMD_WORDS + jArgs.length + nArgumentIncrement));
    }
    indx = CMD_WORDS + jArgs.length + nArgumentIncrement;
    // args for hybrid device
    arguments[indx + 0] = Integer.toString(pTicket.getTotalProcessCount());
    arguments[indx + 1] = Integer.toString(pTicket.getNetworkProcessCount());
    arguments[indx + 2] = Integer.toString(pTicket.getStartingRank());
   // arguments[indx + 3] = configFilePath;
    arguments[indx + 3] = pTicket.getConfFileContents().replace(' ','|');
    arguments[indx + 4] = "niodev";

    for (int i = 0; i < aArgs.length; i++) {
      arguments[i + CMD_WORDS + jArgs.length + HYB_ARGS + nArgumentIncrement] = aArgs[i];
    }

    if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
      MPJDaemon.logger
	  .debug("HybridDaemon: Command for process-builder object index: value ");
    }

    if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
      MPJDaemon.logger.debug("HybridDaemon: creating process-builder object ");
    }

    return arguments;
  }

  public ArrayList<Integer> WriteConfigFile() {
    {
      String CONF_FILE_NAME = "mpjdev.conf";
      configFilePath = ticketDir + File.separator + CONF_FILE_NAME;
      if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	MPJDaemon.logger.debug("configFilePath");
      }

      File configFile = new File(configFilePath);
      try {
	configFile.createNewFile();
      }
      catch (IOException e1) {
	System.out.println("Unable to create config file ");
	System.out.println(e1.getMessage() + "\r\n" + e1.getStackTrace());
	MPJDaemon.logger.debug(e1.getMessage());
      }

      configFilePath = ticketDir + File.separator + CONF_FILE_NAME;
      if (MPJDaemon.DEBUG && MPJDaemon.logger.isDebugEnabled()) {
	MPJDaemon.logger.debug("Config file created :");
      }

      StringTokenizer conf_file_tokenizer = new StringTokenizer(
	  pTicket.getConfFileContents(), ";");
      PrintStream cout;
      FileOutputStream cfos = null;

      try {
	cfos = new FileOutputStream(configFile);
      }
      catch (FileNotFoundException e1) {
	e1.printStackTrace();
      }
      cout = new PrintStream(cfos);

      while (conf_file_tokenizer.hasMoreTokens()) {
	String token = conf_file_tokenizer.nextToken();
	if (token.contains("@") && !token.startsWith("#")) {
	  String[] tokens = token.split("@");
	  ports.add(Integer.parseInt(tokens[1]));
	  if (pTicket.getDeviceName() != "mxdev")
	    ports.add(Integer.parseInt(tokens[2]));
	}
	cout.println(token);
      }

      cout.close();
      try {
	cfos.close();
      }
      catch (IOException e1) {
	e1.printStackTrace();
      }

      return ports;
    }
  }

  private void WriteSourceFile() {
    String USERS = "mpj_users";
    String SRC_DIR = "src";
    String SRC_ZIP = "src.zip";
    Map<String, String> map = System.getenv();
    String mpjHomeDir = map.get("MPJ_HOME");
    usersDir = System.getProperty("user.home") + File.separator + "." + USERS;
    IOHelper.CreateDirectory(usersDir);
    userDir = usersDir + File.separator + pTicket.getUserID();
    IOHelper.CreateDirectory(userDir);
    ticketDir = userDir + File.separator + pTicket.getTicketID().toString();
    IOHelper.CreateDirectory(ticketDir);

    sourceFolder = ticketDir + File.separator + SRC_DIR;
    String sourceZip = ticketDir + File.separator + SRC_ZIP;

    if (pTicket.isZippedSource()) {
      byte[] contents = Base64.decodeBase64(pTicket.getSourceCode());
      IOHelper.writeFile(sourceZip, contents);
      IOHelper.ExtractZip(sourceZip, sourceFolder);
      setSourcePath(sourceFolder);
    }

  }

  public int getThreadsPerHost(int pro, int boxes, int netRank) {
    int proPerHost = pro / boxes;
    int rem = pro % boxes;
    if ((netRank + 1) <= rem) {
      proPerHost++;
    }
    return proPerHost;
  }

}
