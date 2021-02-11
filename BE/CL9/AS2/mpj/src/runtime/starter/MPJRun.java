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
 * File         : MPJRun.java 
 * Author       : Aamir Shafi, Bryan Carpenter,Khurram Shahzad,Hamza Zafar,
 *                Mohsan Jameel, Farrukh Khan
 * Created      : Sun Dec 12 12:22:15 BST 2004
 * Revision     : $Revision: 1.35 $
 * Updated      : $Date: Mon Mar 9 17:54:00 PKT 2015$
 */

package runtime.starter;

//import org.apache.hadoop.yarn.api.ApplicationConstants;

import java.io.*;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggerRepository;

import runtime.common.IOHelper;
import runtime.common.MPJProcessTicket;
import runtime.common.MPJRuntimeException;
import runtime.common.MPJUtil;
import runtime.common.RTConstants;

import java.lang.ProcessBuilder;
import java.lang.Process;

public class MPJRun {

  final String DEFAULT_MACHINES_FILE_NAME = "machines";
  final int DEFAULT_PROTOCOL_SWITCH_LIMIT = 128 * 1024; // 128K
  private String CONF_FILE_CONTENTS="";
  private String WRAPPER_INFO = "#Peer Information";
  private int mxBoardNum = 0;
  private int D_SER_PORT = 0;
  private int DEBUG_PORT = 0;
  private int portManagerPort = 0;

  /* Variables to store master node information.
   * This needs to be forwarded to the wrapper processes.
   */
  private int SERVER_PORT = 0;
  private String localhostName = null;

  // Adding YARN related variables here
  private boolean isYarn = false;
  private boolean debugYarn = false;
  static String hadoopHomeDir = null;
  private String amMem;
  private String amCores;
  private String containerMem;
  private String containerCores;
  private String yarnQueue;
  private String appName;
  private String amPriority;
  private String mpjContainerPriority;
  private String hdfsFolder;

  String machinesFile = DEFAULT_MACHINES_FILE_NAME;
  private int psl = DEFAULT_PROTOCOL_SWITCH_LIMIT;

  ArrayList<String> jvmArgs = new ArrayList<String>();
  ArrayList<String> appArgs = new ArrayList<String>();
  String[] jArgs = null;
  String[] aArgs = null;
  static Logger logger = null;
  private Vector<Socket> peerSockets;

  private ArrayList<String> machineList = new ArrayList<String>();
  int nprocs = Runtime.getRuntime().availableProcessors();
  String deviceName = "multicore";
  private String networkDevice = "niodev";

  static String mpjHomeDir = null;
  byte[] urlArray = null;
  Hashtable procsPerMachineTable = new Hashtable();
  int endCount = 0;
  int streamEndedCount = 0;
  String wdir;
  String className = null;
  String applicationClassPathEntry = null;

  private static String VERSION = "";
  private static int RUNNING_JAR_FILE = 2;
  private static int RUNNING_CLASS_FILE = 1;
  private boolean zippedSource = false;
  private String sourceFolder = "";
  int networkProcesscount = -1;

  private boolean ADEBUG = false;
  private boolean APROFILE = false;

  static final boolean DEBUG = true;
  private String logLevel = "OFF";

  public MPJRun(String args[]) throws Exception {

    java.util.logging.Logger logger1 = java.util.logging.Logger.getLogger("");

    // remove all existing log handlers: remove the ERR handler
    for (java.util.logging.Handler h : logger1.getHandlers()) {
      logger1.removeHandler(h);
    }

    Map<String, String> map = System.getenv();
    try{
      mpjHomeDir = map.get("MPJ_HOME");
      RTConstants.MPJ_HOME_DIR = mpjHomeDir;
      if (mpjHomeDir == null) {
        throw new Exception("[MPJRun.java]:MPJ_HOME environment found..");
      }
    }
    catch (Exception exc) {
      System.out.println("[MPJRun.java]:" + exc.getMessage());
      exc.printStackTrace();
      return;
    }

    readValuesFromMPJExpressConf();
    localhostName = InetAddress.getLocalHost().getHostName();
    createLogger(args);

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug(" --MPJRun.java invoked--");
      logger.debug("[MPJRun.java]: processInput called ...");
    }

    // Processing input
    processInput(args);

    // YARN runtime invoked here
    if(isYarn) {

      System.out.println("MPJ Express (" + VERSION + ") is started in the "
          + "Hadoop YARN configuration with " + deviceName);

      hadoopHomeDir = map.get("HADOOP_HOME");
      RTConstants.HADOOP_YARN_HOME = hadoopHomeDir;

      if (hadoopHomeDir == null) {
        throw new MPJRuntimeException(" HADOOP_HOME not set");
      }

      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("HADOOP HOME is set to: " + hadoopHomeDir);
        logger.debug("YARN Client jar: "+mpjHomeDir+"/lib/mpj-yarn-client.jar");
      }


      List<String> commands = new ArrayList<String>();

      commands.add(hadoopHomeDir+"/bin/hadoop");
      commands.add("jar");
      commands.add(mpjHomeDir+"/lib/mpj-yarn-client.jar");
      commands.add("--np");
      commands.add(Integer.toString(nprocs));      // no. of containers
      commands.add("--server");
      commands.add(localhostName);                 // server name
      commands.add("--serverPort");
      commands.add(Integer.toString(SERVER_PORT)); // server port
      commands.add("--dev");
      commands.add(deviceName);                    // device name
      commands.add("--className");
      commands.add(appArgs.get(0));                // class name
      commands.add("--wdir");
      commands.add(wdir);                          // working directory
      commands.add("--psl");
      commands.add(Integer.toString(psl));         // protocol switch limit
      commands.add("--jarPath");
      commands.add(applicationClassPathEntry);     // user jar file path
    
      // application arguments
      if(appArgs.size() > 1){
        commands.add("--appArgs");
        for(int i=1 ;i<appArgs.size(); i++){
          commands.add(appArgs.get(i));            
        }
      }

      // AM container memory
      if(amMem != null){
        commands.add("--amMem");
        commands.add(amMem);                   
      }

      // AM container virtual cores
      if(amCores !=null){
        commands.add("--amCores");
        commands.add(amCores);
      }

      // MPJ containers memory
      if(containerMem != null){
        commands.add("--containerMem");
        commands.add(containerMem);  
      }

      // MPJ containers virtual cores
      if(containerCores != null){
        commands.add("--containerCores");
        commands.add(containerCores);         
      }

      // YARN scheduler queue 
      if(yarnQueue != null){
        commands.add("--yarnQueue");
        commands.add(yarnQueue);            
      }

      // YARN Application name
      if(appName != null){
        commands.add("--appName");
        commands.add(appName);                 
      }
 
      // AM container priority
      if(amPriority != null){
        commands.add("--amPriority");
        commands.add(amPriority);          
      }
 
      // MPJ containers priority
      if(mpjContainerPriority != null){
        commands.add("--mpjContainerPriority");
        commands.add(mpjContainerPriority);    
      }
    
      //hdfs folder where AM , Wrapper and User jar files will be uploaded
      if(hdfsFolder != null){
        commands.add("--hdfsFolder");
        commands.add(hdfsFolder);
      }

      //debugYarn flag
      if(debugYarn == true){
        commands.add("--debugYarn");
      }

      ProcessBuilder processBuilder = new ProcessBuilder(commands);
     
      // merge the stdout and stderr stream
      processBuilder.redirectErrorStream(true);
     
      Process p = processBuilder.start();

      InputStream is = p.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);

      String line;

      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("Hadoop Command: " + commands.toString());
      }
      // print output of the process
      while ((line = br.readLine()) != null) {
        System.out.println(line);
        if (DEBUG && logger.isDebugEnabled()){
          logger.debug(line);
        }
      }

      try {
        int exitValue = p.waitFor();

        if (DEBUG && logger.isDebugEnabled()){
          logger.debug("Exit Value is " + exitValue);
          logger.debug("Shutting Down MPJ YARN runtime...");
        }
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      return;
    } // YARN runtime ends here

    // Check for MPJE configuration
    // Multicore mode
    if (deviceName.equals("multicore")) {

      System.out.println("MPJ Express (" + VERSION + ") is started in the "
                                                + "multicore configuration");
      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("className " + className);
      }

      int jarOrClass = (applicationClassPathEntry.endsWith(".jar")
                                  ? RUNNING_JAR_FILE
          : RUNNING_CLASS_FILE);

      MulticoreDaemon multicoreDaemon = new MulticoreDaemon(className,
          applicationClassPathEntry, jarOrClass, nprocs, wdir, jvmArgs,
          appArgs, mpjHomeDir, ADEBUG, APROFILE, DEBUG_PORT);
      return;

    }
    // Cluster mode
   
    System.out.println("MPJ Express (" + VERSION + ") is started in the "
                            + "cluster configuration with " + deviceName);
    

    // Read the machine file and set machineList
    machineList = MPJUtil.readMachineFile(machinesFile);
    for (int i = machineList.size(); i > nprocs; i--) {
      machineList.remove(i - 1);
    }

    machinesSanityCheck();

    // Changed to incorporate hybrid device configuration
    if (deviceName.equals("hybdev")){
      assignTasksHyb();
    }
    else{
      assignTasks();
    }
    
    //directory where class is present
    urlArray = applicationClassPathEntry.getBytes();

    /*
     * Create a peer socket vector, connect to daemon on each
     * machine and if connection is successful, save the socket reference
     * inside this vector
     */
    peerSockets = new Vector<Socket>();
    clientSocketInit();

    int peersStartingRank = 0;

    for (int j = 0; j < peerSockets.size(); j++) {
      Socket peerSock = peerSockets.get(j);

      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("procsPerMachineTable " + procsPerMachineTable);
      }

      String hAddress = peerSock.getInetAddress().getHostAddress();
      String hName = peerSock.getInetAddress().getHostName();

      Integer nProcessesInt = ((Integer) procsPerMachineTable.get(hName));

      if (nProcessesInt == null) {
        nProcessesInt = ((Integer) procsPerMachineTable.get(hAddress));
      }

      int nProcesses = nProcessesInt.intValue();

      // Create xml ticket, pass it to each daemon which then launches
      // wrapper.java

      if (deviceName.equals("hybdev")) {
        pack(nProcesses, j, peerSock);
      }
      else {
        pack(nProcesses, peersStartingRank, peerSock);
        peersStartingRank += nProcesses;
      }
      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("Sending to " + peerSock);
      }
    }
    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("procsPerMachineTable " + procsPerMachineTable);
    }
    //mxdev does not needs the read/write ports 
    //so skipping the port information sharing mechanism
    if(!deviceName.equals("mxdev")){
      collectPortInfo();
    }
  }

  // Parses the input ...
  private void processInput(String args[]) {

    if (args.length < 1) {
      printUsage();
      System.exit(0);
    }

    boolean parallelProgramNotYetEncountered = true;

    for (int i = 0; i < args.length; i++) {

      if (args[i].equals("-np")) {
        try {
          nprocs = new Integer(args[i + 1]).intValue();
          if (nprocs < 1) {
            System.out.println("Number of Processes should be equal to"+
					            " or greater than 1");
            System.out.println("exiting ...");
            System.exit(0);
          }
        }
        catch (NumberFormatException e) {
          nprocs = Runtime.getRuntime().availableProcessors();
        }

        i++;
      }

      else if (args[i].equals("-h")) {
        printUsage();
        System.exit(0);
      }

      else if (args[i].equals("-yarn")) {

        isYarn = true;

        if (DEBUG && logger.isDebugEnabled()){
          logger.debug("YARN Runtime invoked");
        }
        

        RTConstants.HADOOP_YARN = "true";
      }
      else if(args[i].equals("-debugYarn")){
        debugYarn = true;
      }

      else if (args[i].equals("-amMem")){
        amMem = args[i + 1];
        i++;
      }

      else if (args[i].equals("-amCores")){
        amCores = args[i + 1];
        i++;
      }
 
      else if (args[i].equals("-containerMem")){
        containerMem = args[i + 1];
        i++;
      }

      else if (args[i].equals("-containerCores")){
        containerCores = args[i + 1];
        i++;
      }

      else if (args[i].equals("-yarnQueue")){
        yarnQueue = args[i + 1];
        i++;
      } 

      else if (args[i].equals("-appName")){
        appName = args[i + 1];
        i++;
      }

      else if (args[i].equals("-amPriority")){
        amPriority = args[i + 1];
        i++;
      }

      else if (args[i].equals("-mpjContainerPriority")) {
        mpjContainerPriority = args[i + 1];
        i++;
      }
      
      else if (args[i].equals("-hdfsFolder")){
        hdfsFolder = args[i + 1];
        if(!hdfsFolder.endsWith("/")){
          hdfsFolder = hdfsFolder+"/";
        }
        i++;
      }
      else if (args[i].equals("-dport")) {
        D_SER_PORT = new Integer(args[i + 1]).intValue();
        i++;
      }

      else if (args[i].equals("-dev")) {
        deviceName = args[i + 1];
        i++;
        if (!(deviceName.equals("niodev") || deviceName.equals("hybdev")
            || deviceName.equals("mxdev") || deviceName.equals("multicore"))) {
          System.out.println("MPJ Express currently does not support the <"
              + deviceName + "> device.");
          System.out
              .println("Possible options are niodev, hybdev, mxdev, native, and "
                  + "multicore devices.");
          System.out.println("exiting ...");
          System.exit(0);
        }
      }

      else if (args[i].equals("-machinesfile")) {
        machinesFile = args[i + 1];
        i++;
      }

      else if (args[i].equals("-wdir")) {
        wdir = args[i + 1];
        i++;
      }

      else if (args[i].equals("-psl")) {
        psl = new Integer(args[i + 1]).intValue();
        i++;
      }

      else if (args[i].equals("-mxboardnum")) {
        mxBoardNum = new Integer(args[i + 1]).intValue();
        i++;
      }

      else if (args[i].equals("-cp") | args[i].equals("-classpath")) {
        jvmArgs.add("-cp");
        jvmArgs.add(args[i + 1]);
        i++;
      }

      else if (args[i].equals("-jar")) {
        File tFile = new File(args[i + 1]);
        String absJarPath = tFile.getAbsolutePath();

        if (tFile.exists()) {
          applicationClassPathEntry = new String(absJarPath);
          try {
            JarFile jarFile = new JarFile(absJarPath);
            Attributes attr = jarFile.getManifest().getMainAttributes();
            className = attr.getValue(Attributes.Name.MAIN_CLASS);
          }
          catch (IOException ioe) {
            ioe.printStackTrace();
          }
          parallelProgramNotYetEncountered = false;
          i++;
        }
        else {
          throw new MPJRuntimeException("mpjrun cannot find the jar file <"
              + args[i + 1] + ">. Make sure this is the right path.");
        }

      }

      else if (args[i].equals("-src")) {
        this.zippedSource = true;
      }

      else if (args[i].equals("-debug")) {
        DEBUG_PORT = new Integer(args[i + 1]).intValue();
        i++;
        ADEBUG = true;
      }

      else if (args[i].equals("-profile")) {
        APROFILE = true;
      }

      else {
        // these are JVM options ..
        if (parallelProgramNotYetEncountered) {
          if (args[i].startsWith("-")) {
            jvmArgs.add(args[i]);
          }
          else {
            applicationClassPathEntry = System.getProperty("user.dir");
            className = args[i];
            parallelProgramNotYetEncountered = false;
          }
        }

        // these have to be app arguments ...
        else {
          appArgs.add(args[i]);
        }
      }
    }

    jArgs = jvmArgs.toArray(new String[0]);
    aArgs = appArgs.toArray(new String[0]);

    if (DEBUG && logger.isDebugEnabled()) {

      logger.debug("###########################");
      logger.debug("-dport: <" + D_SER_PORT + ">");
      logger.debug("-np: <" + nprocs + ">");
      logger.debug("$MPJ_HOME: <" + mpjHomeDir + ">");
      logger.debug("-dir: <" + wdir + ">");
      logger.debug("-dev: <" + deviceName + ">");
      logger.debug("-psl: <" + psl + ">");
      logger.debug("jvmArgs.length: <" + jArgs.length + ">");
      logger.debug("className : <" + className + ">");
      logger.debug("applicationClassPathEntry:"+
					  " <"+applicationClassPathEntry+">");
      // Logs for YARN
      logger.debug("-yarn: <" + isYarn + ">");
      logger.debug("$HADOOP_HOME: <" + hadoopHomeDir + ">");
      logger.debug("-amMem: <" + amMem + ">");
      logger.debug("-amCores: <" + amCores + ">");
      logger.debug("-containerMem: <" + containerMem + ">");
      logger.debug("-yarnQueue: <" + yarnQueue + ">");
      logger.debug("-appName: <" + appName + ">");
      logger.debug("-amPriority: <" + amPriority + ">");
      logger.debug("-mpjContainerPriority: <" + mpjContainerPriority + ">");
      logger.debug("-hdfsFolder: <" + hdfsFolder + ">");


      for (int i = 0; i < jArgs.length; i++) {
        if (DEBUG && logger.isDebugEnabled())
          logger.debug(" jvmArgs[" + i + "]: <" + jArgs[i] + ">");
      }
      if (DEBUG && logger.isDebugEnabled())
        logger.debug("appArgs.length: <" + aArgs.length + ">");

      for (int i = 0; i < aArgs.length; i++) {
        if (DEBUG && logger.isDebugEnabled())
          logger.debug(" appArgs[" + i + "]: <" + aArgs[i] + ">");
      }

      if (DEBUG && logger.isDebugEnabled())
        logger.debug("###########################");
    }
  }

  /*
   * 1. Application Classpath Entry (urlArray). This is a String classpath entry
   * which will be appended by the MPJ Express daemon before starting a user
   * process (JVM). In the case of JAR file, it's the absolute path and name. In
   * the case of a class file, its the name of the working directory where
   * mpjrun command was launched. 2. nProcs- [# of processes] to be started by a
   * particular MPJ Express daemon. 3. start_rank [starting #(rank) of process]
   * to be started by a particular MPJ Express daemon. 4. jvmArgs- args to JVM
   * 5. wdir Working Directory 6. className- Classname to be executed. In the
   * case of JAR file, this name is taken from the manifest file. In the case of
   * class file, the class name is specified on the command line by the user. 7.
   * CONF_FILE_CONTENTS- Configuration File name. This is a ';' delimeted string
   * of config file contents 8. deviceName-: what device to use? 9. appArgs-:
   * Application arguments .. 10. networkDevice- niodev in case of Hybdrid 11.
   * ADEBUG- Flag for launching application in debug mode 12. APROFILE- Flag for
   * launching application in Profiling mode. 13. MasterNode - hostname of the
   * machine running the MPJRun.java class. 14. MasterPort -port number at which
   * MPJRun.java is awaiting connections from the wrapper class.
   */
  private void pack(int nProcesses, int start_rank, Socket sockClient) {

    if (wdir == null) {
      wdir = System.getProperty("user.dir");
    }

    MPJProcessTicket ticket = new MPJProcessTicket();
    ticket.setMpjHomeDir(mpjHomeDir);
    // MasterNode information being appended to ticket
    ticket.setMasterNode(localhostName);
    ticket.setMasterPort(Integer.toString(SERVER_PORT));

    ticket.setClassPath(new String(urlArray));
    ticket.setProcessCount(nProcesses);
    ticket.setStartingRank(start_rank);
    ticket.setWorkingDirectory(wdir);
    ticket.setUserID(System.getProperty("user.name"));
    if (this.zippedSource) {
      String zipFileName = UUID.randomUUID() + ".zip";
      this.sourceFolder = wdir;
      IOHelper.zipFolder(this.sourceFolder, zipFileName);
      byte[] zipContents = IOHelper.ReadBinaryFile(zipFileName);
      String encodedString = Base64.encodeBase64String(zipContents);
      ticket.setSourceCode(encodedString);
      IOHelper.deleteFile(zipFileName);
      ticket.setZippedSource(true);
    }
    ticket.setMainClass(className);
    ticket.setConfFileContents(CONF_FILE_CONTENTS);
    ticket.setDeviceName(deviceName);
    IOMessagesThread ioMessages = new IOMessagesThread(sockClient);
    ioMessages.start();
    ArrayList<String> jvmArgs = new ArrayList<String>();
    for (int j = 0; j < jArgs.length; j++) {
      jvmArgs.add(jArgs[j]);
    }
    ticket.setJvmArgs(jvmArgs);

    ArrayList<String> appArgs = new ArrayList<String>();
    for (int j = 0; j < aArgs.length; j++) {
      appArgs.add(aArgs[j]);
    }
    ticket.setAppArgs(appArgs);

    if (deviceName.equals("hybdev")) {
      ticket.setNetworkProcessCount(networkProcesscount);
      ticket.setTotalProcessCount(nprocs);
      ticket.setNetworkDevice(networkDevice);
    }

    if (ADEBUG) {
      ticket.setDebug(true);
      ticket.setDebugPort(DEBUG_PORT);
    }

    if (APROFILE) {
      ticket.setProfiler(true);
    }
    // Conversion into XML and then transmission over the network
    String ticketString = ticket.ToXML().toXmlString();
    OutputStream outToServer = null;
    try {
      outToServer = sockClient.getOutputStream();
    }
    catch (IOException e) {
      logger.info(" Unable to get deamon stream-");
      e.printStackTrace();
    }
    DataOutputStream out = new DataOutputStream(outToServer);

    try {
      int length = ticketString.getBytes().length;
      out.writeInt(length);
      if (DEBUG && logger.isDebugEnabled()) {
        logger.info("Machine Name: "
            + sockClient.getInetAddress().getHostName() + " Starting Rank: "
            + ticket.getStartingRank() + " Process Count: "
            + ticket.getProcessCount());
      }
      out.write(ticketString.getBytes(), 0, length);
      out.flush();
    }
    catch (IOException e) {

      logger.info(" Unable to write on deamon stream-");
      e.printStackTrace();
    }
  }

  private void createLogger(String[] args) throws MPJRuntimeException {
    String userDir = System.getProperty("user.dir");
    if (DEBUG && logger == null) {

      DailyRollingFileAppender fileAppender = null;

      try {
        if (logLevel.toUpperCase().equals("DEBUG")) {
          fileAppender = new DailyRollingFileAppender(new PatternLayout(
              " %-5p %c %x - %m\n"), userDir + "/mpjrun.log", "yyyy-MM-dd-a");

          Logger rootLogger = Logger.getRootLogger();
          rootLogger.addAppender(fileAppender);
          LoggerRepository rep = rootLogger.getLoggerRepository();
          rootLogger.setLevel((Level) Level.ALL);
        }
        // rep.setThreshold((Level) Level.OFF ) ;
        logger = Logger.getLogger("runtime");
        logger.setLevel(Level.toLevel(logLevel.toUpperCase(), Level.OFF));
      }
      catch (Exception e) {
        throw new MPJRuntimeException(e);
      }
    }
  }

  // FK>> Help print statement for YARN added
  private void printUsage() {
    System.out
        .println("MPJ Express version " + VERSION
            + "\n\nmpjrun.[bat/sh] [options] class [args...]"
            + "\n                (to execute a class)"
            + "\nmpjrun.[bat/sh] [options] -jar jarfile [args...]"
            + "\n                (to execute a jar file)"
            + "\n\nwhere options include:"
            + "\n   -np val            -- <# of cores>"
            + "\n   -dev val           -- <multicore>"
            + "\n   -dport val         -- <read from mpjexpress.conf>"
            + "\n   -wdir val          -- $MPJ_HOME/bin"
            + "\n   -mpjport val       -- Deprecated"
            + "\n   -yarn              -- to run application using Hadoop YARN"
            + "\n   -mxboardnum val    -- 0"
            + "\n   -headnodeip val    -- ..."
            + "\n   -psl val           -- 128Kbytes"
            + "\n   -machinesfile val  -- machines"
            + "\n   -debug val         -- 24500"
            + "\n   -src val           -- false"
            + "\n   -profile val       -- false"
            + "\n   -h                 -- print this usage information"
            + "\n   ...any JVM arguments..."
            + "\n Note: Value on the right in front of each option is the default value"
            + "\n Note: 'MPJ_HOME' variable must be set");

  }
  /*
   * In the previous release (0.43) of the MPJ Express runtime system, the 
   *  mpjrun module was responsible for gathering the list of ports for all 
   *  processes. It did so by contacting the daemon process running on each
   *  compute node involved in execution of a parallel jobs.The task of the 
   *  daemon process was to find an unused port and report its number back to
   *  mpjrun. The mechanism for finding an unused port was to start from a 
   *  specific port and open a server socket using it. If the attempt to 
   *  create such a socket was successful, we assumed the port to be available.
   *  Otherwise, daemon incremented the port number and repeated the step
   *  of opening a server socket. As a consequence of this port selection 
   *  at each daemon, the mpjrun module ended up having a list of ports 
   *  against names and identities (ranks) of MPJ processes.
   *
   *  In the newer MPJ and YARN-based runtime system,the port selection 
   *  procedure is now performed by the niodev communication (and not runtime
   *  system) and the server socket is kept open throughout the execution of 
   *  a parallel process. In the YARN-based version, the port information 
   *  is aggregated by MPJYarnClient instead of mpjrun. The conf file now
   *  specifies read and write port as 0, This is done to comply with the 
   *  debugger project!
   */
  private void assignTasks() throws Exception {

    int rank = 0;

    int noOfMachines = machineList.size();

    CONF_FILE_CONTENTS = "#temp line";
    CONF_FILE_CONTENTS += ";" + "#Number of Processes";
    CONF_FILE_CONTENTS += ";" + nprocs;
    CONF_FILE_CONTENTS += ";" + "#Protocol Switch Limit";
    CONF_FILE_CONTENTS += ";" + psl;
    
    CONF_FILE_CONTENTS += ";"
        + "#Entry, HOST_NAME/IP@READPORT@WRITEPORT@RANK@DEBUGPORT";
    
    /*
     * number of requested parallel processes are less than or equal to compute
     * nodes
     */
    if (nprocs <= noOfMachines) {

      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("Processes Requested " + nprocs
            + " are less than than machines " + noOfMachines);
        logger.debug("Adding 1 processes to the first " + nprocs + " items");
      }

    /*
     * Since the number of processes is less than machines, so
     * allocate each machine a single process
     */
      for (int i = 0; i < nprocs; i++) {
        procsPerMachineTable
            .put(InetAddress.getByName((String) machineList.get(i))
                .getHostAddress(), new Integer(1));

        if (deviceName.equals("niodev")) {

          CONF_FILE_CONTENTS += ";"
              + InetAddress.getByName((String) machineList.get(i))
                  .getHostAddress() + "@0@0@"+ (rank++);
         
        } 
        else 
        if (deviceName.equals("mxdev")) {
          CONF_FILE_CONTENTS += ";" + (String) machineList.get(i) + "@"
              + mxBoardNum + "@" + (rank++);
        }
         CONF_FILE_CONTENTS += "@" + (DEBUG_PORT);

        if (DEBUG && logger.isDebugEnabled()) {
          logger.debug("procPerMachineTable==>" + procsPerMachineTable);
        }
      }

      /*
       * number of processes are greater than compute nodes available. we'll
       * start more than one process on compute nodes to deal with this
       */
    }
    else if (nprocs > noOfMachines) {

      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("Processes Requested " + nprocs
            + " are greater than than machines " + noOfMachines);
      }
      int divisor = nprocs / noOfMachines;
      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("divisor " + divisor);
      }
      int remainder = nprocs % noOfMachines;

      if (DEBUG && logger.isDebugEnabled()) {
        logger.debug("remainder " + remainder);
      }

      for (int i = 0; i < noOfMachines; i++) {

        if (i < remainder) {

          procsPerMachineTable.put(
              InetAddress.getByName((String) machineList.get(i))
                  .getHostAddress(), new Integer(divisor + 1));
          if (DEBUG && logger.isDebugEnabled()) {
            logger.debug("procPerMachineTable==>" + procsPerMachineTable);
          }

          for (int j = 0; j < (divisor + 1); j++) {
            if (deviceName.equals("niodev")) {

              CONF_FILE_CONTENTS += ";"
                  + InetAddress.getByName((String) machineList.get(i))
                      .getHostAddress() + "@0@0@" + (rank++);
            } 
            else if (deviceName.equals("mxdev")) {
              CONF_FILE_CONTENTS += ";" + (String) machineList.get(i) + "@"
                  + (mxBoardNum + j) + "@" + (rank++);
            }
            CONF_FILE_CONTENTS += "@" + (DEBUG_PORT + j * 2);
          }
        }
        else if (divisor > 0) {
          procsPerMachineTable.put(
              InetAddress.getByName((String) machineList.get(i))
                  .getHostAddress(), new Integer(divisor));

          if (DEBUG && logger.isDebugEnabled()) {
            logger.debug("procPerMachineTable==>" + procsPerMachineTable);
          }

          for (int j = 0; j < divisor; j++) {
            if (deviceName.equals("niodev")) {
              CONF_FILE_CONTENTS += ";"
                  + InetAddress.getByName((String) machineList.get(i))
                      .getHostAddress() + "@0@0@" + (rank++);

            } 
            else if (deviceName.equals("mxdev")) {
              CONF_FILE_CONTENTS += ";" + (String) machineList.get(i) + "@"
                  + (mxBoardNum + j) + "@" + (rank++);
            }
              CONF_FILE_CONTENTS += "@" + (DEBUG_PORT + j * 2);
          }
        }
      }
    }
    // Write mpjdev.conf if debugger is enabled
    if (ADEBUG) {
      writeFile(CONF_FILE_CONTENTS + "\n");
    }
    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("conf file contents " + CONF_FILE_CONTENTS);
    }

  }

  // Hybrid Device Assign Tasks

  private void assignTasksHyb() throws Exception {

    int noOfMachines = machineList.size();
    networkProcesscount = -1;
    if (nprocs <= noOfMachines) {
      networkProcesscount = nprocs;
    } else { // when np is higher than the nodes available
      networkProcesscount = noOfMachines;
    }
    int netID = 0;
    String DEBUGGER_FILE_CONTENTS = "";

    CONF_FILE_CONTENTS = "#temp line";
    CONF_FILE_CONTENTS += ";" + "#Number of Processes";
    CONF_FILE_CONTENTS += ";" + networkProcesscount;
    CONF_FILE_CONTENTS += ";" + "#Protocol Switch Limit";
    CONF_FILE_CONTENTS += ";" + psl + ";";
    
    DEBUGGER_FILE_CONTENTS = CONF_FILE_CONTENTS;

    CONF_FILE_CONTENTS += ";" + "#Server Name";
    CONF_FILE_CONTENTS += ";" + localhostName;
    CONF_FILE_CONTENTS += ";" + "#Server Port";
    CONF_FILE_CONTENTS += ";" + Integer.toString(SERVER_PORT);

    if(ADEBUG){
      DEBUGGER_FILE_CONTENTS +=
                   "#Entry, HOST_NAME/IP@READPORT@WRITEPORT@NETID@DEBUGPORT";
    }

    // One NIO Process per machine is being implemented, SMP Threads per
    // node will be decided in SMPDev
    for (int i = 0; i < networkProcesscount; i++) {
      procsPerMachineTable.put(
          InetAddress.getByName((String) machineList.get(i)).getHostAddress(),
          new Integer(1));

      DEBUGGER_FILE_CONTENTS += ";"
          + InetAddress.getByName((String) machineList.get(i)).getHostAddress()
          + "@0@0@" + (netID++);
 
      DEBUGGER_FILE_CONTENTS += "@" + (DEBUG_PORT);
    }

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("procPerMachineTable==>" + procsPerMachineTable);
    }
     // Write mpjdev.conf if debugger is enabled
    if (ADEBUG) {
      writeFile(DEBUGGER_FILE_CONTENTS + "\n");
    }
    if (DEBUG && logger.isDebugEnabled()) {
       logger.debug("debugger file contents " + DEBUGGER_FILE_CONTENTS);
    }

  }

  private void machinesSanityCheck() throws Exception {

    for (int i = 0; i < machineList.size(); i++) {
      String host = (String) machineList.get(i);
      try {
        InetAddress add = InetAddress.getByName(host);
      }
      catch (Exception e) {
        throw new MPJRuntimeException(e);
      }
    }
  }

  private void readValuesFromMPJExpressConf() {

    FileInputStream in = null;
    DataInputStream din = null;
    BufferedReader reader = null;
    String line = "";

    try {

      String path = mpjHomeDir + File.separator + RTConstants.MPJEXPRESS_CONF_FILE;
      in = new FileInputStream(path);
      din = new DataInputStream(in);
      reader = new BufferedReader(new InputStreamReader(din));

      while ((line = reader.readLine()) != null) {
        if (line.startsWith(RTConstants.MPJ_DAEMON_PORT_KEY)) {
          D_SER_PORT = Integer.parseInt(MPJUtil.confValue(line));
        } else if (line.startsWith(RTConstants.MPJ_PORTMANAGER_PORT_KEY)) {
          portManagerPort = Integer.parseInt(MPJUtil.confValue(line));
        } else if (line.startsWith(RTConstants.MPJ_RUN_LOGLEVEL_KEY)) {
          logLevel = MPJUtil.confValue(line);
        } else if (line.startsWith(RTConstants.MPJEXPRESS_VERSION_KEY)) {
          VERSION = MPJUtil.confValue(line);
        } else if (line.startsWith(RTConstants.MPJ_RUN_SERVER_PORT_KEY)){
          SERVER_PORT = Integer.parseInt(MPJUtil.confValue(line));
        }
      }

      in.close();

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void clientSocketInit() throws Exception {

    for (int i = 0; i < machineList.size(); i++) {
      String daemon = (String) machineList.get(i);
      try {

        if (DEBUG && logger.isDebugEnabled()) {
          logger.debug("Connecting to " + daemon + "@" + D_SER_PORT);
        }
        try {
          Socket sockClient = new Socket(daemon, D_SER_PORT);
          if (sockClient.isConnected())
            peerSockets.add(sockClient);
          else {

            throw new MPJRuntimeException("Cannot connect to the daemon "
                + "at machine <" + daemon + "> and port <" + D_SER_PORT + ">."
                + "Please make sure that the machine is reachable "
                + "and running the daemon in 'sane' state");

          }
        }
        catch (IOException e3) {

          throw new MPJRuntimeException("Cannot connect to the daemon "
              + "at machine <" + daemon + "> and port <" + D_SER_PORT + ">."
              + "Please make sure that the machine is reachable "
              + "and running the daemon in 'sane' state");
        }

      }
      catch (Exception ccn1) {
        System.out.println(" rest of the exceptions ");
        throw ccn1;
      }
    }

  }

  private void writeFile(String configurationFileData) {
    // Method to write CONF_FILE in user directory that will be later used by
    // MPJ Express Debugger
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(
          System.getProperty("user.home") + File.separator + RTConstants.MPJDEV_CONF_FILE));
      out.write(configurationFileData);
      out.close();
    }
    catch (IOException e) {

    }

  }

   // collect and share port information from all wrapper's NIODevice
  private void collectPortInfo(){
    int rank = 0;
    int wport = 0;
    int rport = 0;
    ServerSocket servSock = null;
    Socket sock = null;
    Vector<Socket> socketList;
    socketList = new Vector<Socket>();
    byte[] dataFrame = null;
    String[] peers = new String[nprocs];

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("[MPJRun.java]:Creating server..");
    }

    // Creating a server socket for incoming connections
    try {
      servSock = new ServerSocket(SERVER_PORT);
    }
    catch (Exception e) {
      System.err.println("[MPJRun.java]: Error opening server port..");
      e.printStackTrace();
    }
    
    if (deviceName.equals("hybdev")) {
      nprocs = networkProcesscount;
    }

    if (DEBUG && logger.isDebugEnabled()) {
      logger.debug("[MPJRun.java]:Waiting for <"+nprocs+"> to connect");
    }
    // Loop to read port numbers from Wrapper.java processes
    // and to create WRAPPER_INFO (containing all IPs and ports)
    for(int i = nprocs; i > 0; i--){
      try{
        sock = servSock.accept(); 
        if(DEBUG && logger.isDebugEnabled()){
          logger.debug("Socket "+sock.getInetAddress().getHostName()+
                       " connected to share Rank and Ports");
        }        
        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
        DataInputStream in = new DataInputStream(sock.getInputStream());

        if(in.readUTF().startsWith("Sending Info")){
          wport = in.readInt();
          rport = in.readInt();
          rank = in.readInt();

          peers[rank] = ";" + sock.getInetAddress().getHostAddress() +
                      "@" + rport + "@" + wport + "@" + rank + "@" + DEBUG_PORT;

          socketList.add(sock);
        }
      }
      catch (Exception e){
        System.err.println(
               "[MPJRun.java]: Error accepting connection from peer socket..");
        e.printStackTrace();
      }
    }

    // Loop to sort contents of mpjdev.conf according to rank
    for(int i=0; i < nprocs; i++) {
      WRAPPER_INFO += peers[i];
    }

    // Loop to broadcast WRAPPER_INFO to all Wrappers
    for(int i = nprocs; i > 0; i--){
      try{
        sock = socketList.get(nprocs - i);
        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
        out.writeUTF(WRAPPER_INFO);
        out.flush();

        if(DEBUG && logger.isDebugEnabled()){
          logger.debug("Sending info to Socket "+sock.getInetAddress()
			.getHostName()+"\nInfo: "+WRAPPER_INFO);
        }
        sock.close();
      }
      catch (Exception e){
        System.err.println("[MPJRun.java]: Error closing connection from "+
                                                           "peer socket..");
        e.printStackTrace();
      }
    }
  }

  public static void main(String args[]) throws Exception {
    try {
      MPJRun client = new MPJRun(args);
    }
    catch (Exception exp) {
      throw exp;
    }
  }
}
