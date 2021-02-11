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
 * File         : MPJYarnClient.java 
 * Author       : Hamza Zafar
 */

package runtime.starter;

import java.io.*;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.util.*;

import org.apache.hadoop.conf.Configuration; //apache configuration

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileContext;

import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration; //YARN configuration
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.hadoop.fs.BlockLocation;

public class MPJYarnClient {
  //conf fetches information from yarn-site.xml and yarn-default.xml.
  Configuration conf;
  String mpjHomeDir;

  //Number of conatiners
  private int np;
  private String serverName;
  private int serverPort;
  private String deviceName;
  private String className;
  private String workingDirectory;
  private int psl;
  private String jarPath;
  private String [] appArgs;
  private int amMem;
  private int amCores;
  private String containerMem;
  private String containerCores;
  private String yarnQueue;
  private String appName;
  private int amPriority;
  private String  mpjContainerPriority; 
  private String hdfsFolder;
  private boolean debugYarn = false;
  private Log logger = null;
  private int SERVER_PORT = 0;
  private int TEMP_PORT = 0;
  private String [] peers;
  private Vector<Socket> socketList;
  private ServerSocket servSock = null;
  private ServerSocket infoSock = null;
  private Socket sock = null;
  public static boolean isRunning = false;
  private ApplicationReport appReport ;
  private YarnApplicationState appState ;
  private FinalApplicationStatus fStatus;
  private  Options opts = null;
  private  CommandLine cliParser = null;

  public MPJYarnClient(){
    logger = LogFactory.getLog(MPJYarnClient.class);
    conf = new YarnConfiguration();

    opts = new Options();

    opts.addOption("np",true,"Number of Processes");
    opts.addOption("server",true,"Hostname required for Server Socket");
    opts.addOption("serverPort",true,"Port required for Server Socket");
    opts.addOption("dev",true,"Specifies the MPJ device name");
    opts.addOption("className",true,"Main Class name");
    opts.addOption("wdir",true,"Specifies the current working directory");
    opts.addOption("psl",true,"Specifies the Protocol Switch Limit");
    opts.addOption("jarPath",true,"Specifies the Path to user's Jar File");
    opts.addOption("appArgs",true,"Specifies the User Application args");
    opts.getOption("appArgs").setArgs(Option.UNLIMITED_VALUES);
    opts.addOption("amMem",true,"Specifies AM container memory");
    opts.addOption("amCores",true,"Specifies AM container virtual cores");
    opts.addOption("containerMem",true,"Specifies mpj containers memory");
    opts.addOption("containerCores",true,"Specifies mpj containers v-cores");
    opts.addOption("yarnQueue",true,"Specifies the yarn queue");
    opts.addOption("appName",true,"Specifies the application name");
    opts.addOption("amPriority",true,"Specifies AM container priority");
    opts.addOption("mpjContainerPriority",true,"Specifies the prioirty of" + 
                                       "containers running MPI processes");
    opts.addOption("hdfsFolder",true,"Specifies the HDFS folder where AM,"+
                         "Wrapper and user code jar files will be uploaded");
    opts.addOption("debugYarn",false,"Specifies the debug flag");
  }
  
  public void init(String [] args){
    try{   
      cliParser = new GnuParser().parse(opts, args);

      np = Integer.parseInt(cliParser.getOptionValue("np"));

      serverName = cliParser.getOptionValue("server");

      serverPort = Integer.parseInt(cliParser.getOptionValue("serverPort"));

      deviceName = cliParser.getOptionValue("dev");

      className = cliParser.getOptionValue("className");

      workingDirectory = cliParser.getOptionValue("wdir");

      psl = Integer.parseInt(cliParser.getOptionValue("psl"));

      jarPath = cliParser.getOptionValue("jarPath");
      
      amMem = Integer.parseInt(cliParser.getOptionValue("amMem","2048"));

      amCores = Integer.parseInt(cliParser.getOptionValue("amCores","1"));

      containerMem = cliParser.getOptionValue("containerMem","1024");
 
      containerCores = cliParser.getOptionValue("containerCores","1");

      yarnQueue = cliParser.getOptionValue("yarnQueue","default");

      appName = cliParser.getOptionValue("appName","MPJ-YARN-Application");

      amPriority = Integer.parseInt(cliParser.getOptionValue
							("amPriority","0"));

      mpjContainerPriority = cliParser.getOptionValue
                                             ("mpjContainerPriority","0");
      
      hdfsFolder = cliParser.getOptionValue("hdfsFolder","/");

      if(cliParser.hasOption("appArgs")){  
        appArgs = cliParser.getOptionValues("appArgs");
      }
      
      if(cliParser.hasOption("debugYarn")){
        debugYarn = true;
      }
    }catch(Exception  exp){
      exp.printStackTrace();
    }
  }

  public void run() throws Exception {
 
      Map<String, String> map = System.getenv();

      try{
           mpjHomeDir = map.get("MPJ_HOME");

           if (mpjHomeDir == null) {
              throw new Exception("[MPJRun.java]:MPJ_HOME environment found..");
           }
      }
      catch (Exception exc) {
           System.out.println("[MPJRun.java]:" + exc.getMessage());
           exc.printStackTrace();
           return;
      }

      // Copy the application master jar to HDFS
      // Create a local resource to point to the destination jar path
      FileSystem fs = FileSystem.get(conf);
/*
      Path dataset = new Path(fs.getHomeDirectory(),"/dataset");
      FileStatus datasetFile = fs.getFileStatus(dataset);
     
      BlockLocation myBlocks [] = fs.getFileBlockLocations(datasetFile,0,datasetFile.getLen());
      for(BlockLocation b : myBlocks){
        System.out.println("\n--------------------");
        System.out.println("Length "+b.getLength());
        for(String host : b.getHosts()){
          System.out.println("host "+host);
        }
      }
*/
      Path source = new Path(mpjHomeDir+"/lib/mpj-app-master.jar");
      String pathSuffix = hdfsFolder+"mpj-app-master.jar";
      Path dest = new Path(fs.getHomeDirectory(), pathSuffix);
    
      if(debugYarn){ 
        logger.info("Uploading mpj-app-master.jar to: "+dest.toString());
      }

      fs.copyFromLocalFile(false, true, source, dest);
      FileStatus destStatus = fs.getFileStatus(dest);
     
      Path wrapperSource = new Path(mpjHomeDir+"/lib/mpj-yarn-wrapper.jar");
      String wrapperSuffix = hdfsFolder+"mpj-yarn-wrapper.jar";
      Path wrapperDest = new Path(fs.getHomeDirectory(), wrapperSuffix);
    
      if(debugYarn){
        logger.info("Uploading mpj-yarn-wrapper.jar to: "+
                                                     wrapperDest.toString());
      }

      fs.copyFromLocalFile(false, true, wrapperSource, wrapperDest);
     
      Path userJar = new Path(jarPath);
      String userJarSuffix = hdfsFolder+"user-code.jar";
      Path userJarDest = new Path(fs.getHomeDirectory(),userJarSuffix);

      if(debugYarn){
      logger.info("Uploading user-code.jar to: "+userJarDest.toString());
      }

      fs.copyFromLocalFile(false,true,userJar,userJarDest);

      YarnConfiguration conf = new YarnConfiguration();
      YarnClient yarnClient = YarnClient.createYarnClient();
      yarnClient.init(conf);
      yarnClient.start();

      if(debugYarn){
        YarnClusterMetrics metrics = yarnClient.getYarnClusterMetrics();
        logger.info("\nNodes Information");
        logger.info("Number of NM: "+metrics.getNumNodeManagers()+"\n");
      
        List<NodeReport> nodeReports = yarnClient.getNodeReports
							(NodeState.RUNNING);
        for(NodeReport n: nodeReports){
          logger.info("NodeId: "+n.getNodeId());
          logger.info("RackName: "+n.getRackName());
          logger.info("Total Memory: "+n.getCapability().getMemory());
          logger.info("Used Memory: "+n.getUsed().getMemory());
          logger.info("Total vCores: "+n.getCapability().getVirtualCores());
          logger.info("Used vCores: "+n.getUsed().getVirtualCores()+"\n");
        }
      }
      
      logger.info("Creating server socket at HOST "+serverName+" PORT "+
                 serverPort+" \nWaiting for "+ np +" processes to connect...");


      // Creating a server socket for incoming connections
      try {
        servSock = new ServerSocket(serverPort);
        infoSock = new ServerSocket();
        TEMP_PORT = findPort(infoSock);
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      // Create application via yarnClient
      YarnClientApplication app = yarnClient.createApplication();
      GetNewApplicationResponse appResponse = app.getNewApplicationResponse();

      int maxMem = appResponse.getMaximumResourceCapability().getMemory();
      
      if(debugYarn){
        logger.info("Max memory capability resources in cluster: "+maxMem);
      }

      if(amMem > maxMem){
        amMem = maxMem;
        logger.info("AM memory specified above threshold of cluster "+
                    "Using maximum memory for AM container: "+amMem);
      }
      int maxVcores = appResponse.getMaximumResourceCapability().
							getVirtualCores();
 
      if(debugYarn){ 
        logger.info("Max vCores capability resources in cluster: "+maxVcores);
      }

      if(amCores > maxVcores){
        amCores = maxVcores;
        logger.info("AM virtual cores specified above threshold of cluster "+
                    "Using maximum virtual cores for AM container: "+amCores);
      }

      // Set up the container launch context for the application master
      ContainerLaunchContext amContainer =
              Records.newRecord(ContainerLaunchContext.class);

      List <String> commands= new ArrayList<String>();
      commands.add("$JAVA_HOME/bin/java");
      commands.add("-Xmx"+amMem+"m");
      commands.add("runtime.starter.MPJAppMaster");
      commands.add("--np");
      commands.add(String.valueOf(np));
      commands.add("--serverName");
      commands.add(serverName); //server name
      commands.add("--ioServerPort");
      commands.add(Integer.toString(serverPort)); //server port
      commands.add("--deviceName");
      commands.add(deviceName); //device name
      commands.add("--className");
      commands.add(className); //class name
      commands.add("--wdir");
      commands.add(workingDirectory); //wdir
      commands.add("--psl");
      commands.add(Integer.toString(psl)); //protocol switch limit
      commands.add("--wireUpPort");
      commands.add(String.valueOf(TEMP_PORT)); //for sharing ports & rank
      commands.add("--wrapperPath");
      commands.add(wrapperDest.toString());//MPJYarnWrapper.jar HDFS path
      commands.add("--userJarPath");
      commands.add(userJarDest.toString());//User Jar File HDFS path
      commands.add("--mpjContainerPriority");
      commands.add(mpjContainerPriority);// priority for mpj containers 
      commands.add("--containerMem");
      commands.add(containerMem);
      commands.add("--containerCores");   
      commands.add(containerCores);
      
      if(debugYarn){
        commands.add("--debugYarn");
      }

      if(appArgs != null){

        commands.add("--appArgs");

        for(int i=0; i < appArgs.length; i++){
          commands.add(appArgs[i]);
        }
      }

      amContainer.setCommands(commands); //set commands

      // Setup local Resource for ApplicationMaster
      LocalResource appMasterJar = Records.newRecord(LocalResource.class);

      appMasterJar.setResource(ConverterUtils.getYarnUrlFromPath(dest));
      appMasterJar.setSize(destStatus.getLen());
      appMasterJar.setTimestamp(destStatus.getModificationTime());
      appMasterJar.setType(LocalResourceType.ARCHIVE);
      appMasterJar.setVisibility(LocalResourceVisibility.APPLICATION);

      amContainer.setLocalResources(
            Collections.singletonMap("mpj-app-master.jar", appMasterJar));

      // Setup CLASSPATH for ApplicationMaster
      // Setting up the environment
      Map<String, String> appMasterEnv = new HashMap<String, String>();
      setupAppMasterEnv(appMasterEnv);
      amContainer.setEnvironment(appMasterEnv);
      
      // Set up resource type requirements for ApplicationMaster
      Resource capability = Records.newRecord(Resource.class);
      capability.setMemory(amMem);
      capability.setVirtualCores(amCores);
      
      // Finally, set-up ApplicationSubmissionContext for the application
      ApplicationSubmissionContext appContext =
                                    app.getApplicationSubmissionContext();

      appContext.setApplicationName(appName);
      appContext.setAMContainerSpec(amContainer);
      appContext.setResource(capability);
      appContext.setQueue(yarnQueue); // queue
      
      Priority priority = Priority.newInstance(amPriority);
      appContext.setPriority(priority);
 
      ApplicationId appId = appContext.getApplicationId();

      //Adding ShutDown Hook
      Runtime.getRuntime().addShutdownHook(
               new KillYarnApp(appId,yarnClient));

      // Submit application
      System.out.println("Submitting Application: " +
                           appContext.getApplicationName()+"\n");
    
      try{
        isRunning = true;
        yarnClient.submitApplication(appContext);
      }
      catch(Exception exp){
        System.err.println("Error Submitting Application");
        exp.printStackTrace();
      }
      
      // np = number of processes , + 1 for Application Master container
      IOMessagesThread [] ioThreads = new IOMessagesThread[np+1];

      peers = new String[np];
      socketList = new Vector<Socket>();
      int wport = 0;
      int rport = 0;
      int rank = 0;
     
      // np + 1 IOThreads
      for(int i = 0; i < (np+1); i++){
        try{
          sock = servSock.accept();

          //start IO thread to read STDOUT and STDERR from wrappers
          IOMessagesThread io = new IOMessagesThread(sock);
          ioThreads[i] = io;
          ioThreads[i].start();
        }
        catch (Exception e){
          System.err.println("Error accepting connection from peer socket..");
          e.printStackTrace();
        }
      } 
    
      // Loop to read port numbers from Wrapper.java processes
      // and to create WRAPPER_INFO (containing all IPs and ports)
      String WRAPPER_INFO ="#Peer Information";
      for(int i = np; i > 0; i--){
        try{
          sock = infoSock.accept();

          DataOutputStream out = new DataOutputStream(sock.getOutputStream());
          DataInputStream in = new DataInputStream(sock.getInputStream());
          if(in.readUTF().startsWith("Sending Info")){
            wport = in.readInt();
            rport = in.readInt();
            rank = in.readInt();
            peers[rank]=";" + sock.getInetAddress().getHostAddress() +
                      "@" + rport + "@" + wport + "@" + rank ;
            socketList.add(sock);
          }
        }
        catch (Exception e){
          System.err.println("[MPJYarnClient.java]: Error accepting"+
                                               " connection from peer socket!");
          e.printStackTrace();
        }
      }

      for (int i = 0; i < np; i++){
        WRAPPER_INFO += peers[i];
      }
      // Loop to broadcast WRAPPER_INFO to all Wrappers
      for(int i = np; i > 0; i--){
        try{
          sock = socketList.get(np - i);
          DataOutputStream out = new DataOutputStream(sock.getOutputStream());

          out.writeUTF(WRAPPER_INFO);
          out.flush();

          sock.close();
        }
        catch (Exception e){
          System.err.println("[MPJYarnClient.java]: Error closing"+
                                              " connection from peer socket..");
          e.printStackTrace();
        }
      }

      try{
        infoSock.close();
      }
      catch(IOException exp){
        exp.printStackTrace();
      }

      // wait for all IO Threads to complete 
      for(int i=0;i<(np+1);i++){
        ioThreads[i].join();
      }
      isRunning = true;
    
      System.out.println("\nApplication Statistics!");
      while (true) {
        appReport = yarnClient.getApplicationReport(appId);
        appState = appReport.getYarnApplicationState();
        fStatus = appReport.getFinalApplicationStatus();
        if(appState == YarnApplicationState.FINISHED){
          isRunning = false;
          if(fStatus == FinalApplicationStatus.SUCCEEDED){
            System.out.println("State: "+fStatus);
          }
          else{
            System.out.println("State: "+fStatus);
          }
          break;
        }
        else if(appState == YarnApplicationState.KILLED){
          isRunning = false;
          System.out.println("State: "+appState);
          break;
        }
        else if(appState == YarnApplicationState.FAILED){
          isRunning = false;
          System.out.println("State: "+appState);
          break;
        }
        Thread.sleep(100);
      }
      
      try{

        if(debugYarn){
          logger.info("Cleaning the files from hdfs: ");
          logger.info("1) "+dest.toString());
          logger.info("2) "+wrapperDest.toString());
          logger.info("3) "+userJarDest.toString());
        }

        fs.delete(dest);
        fs.delete(wrapperDest);
        fs.delete(userJarDest);
      } 
      catch(IOException exp){
        exp.printStackTrace();
      }
      System.out.println("Application ID: " + appId + "\n" +
                         "Application User: "+ appReport.getUser() + "\n" +
                         "RM Queue: "+appReport.getQueue() + "\n" +
	       	         "Start Time: "+appReport.getStartTime() + "\n" +	
                         "Finish Time: " + appReport.getFinishTime()); 
    }

  private void setupAppMasterEnv(Map<String, String> appMasterEnv) {

   for (String c : conf.getStrings(
        YarnConfiguration.YARN_APPLICATION_CLASSPATH,
        YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH)) {
              Apps.addToEnvironment(appMasterEnv, Environment.CLASSPATH.name(),
              c.trim());
    }

    Apps.addToEnvironment(appMasterEnv,
        Environment.CLASSPATH.name(),
        Environment.PWD.$() + File.separator + "*");
  }
  private int findPort(ServerSocket sock){
    int minPort = 25000;
    int maxPort = 40000;
    int selectedPort;

    /* The loop generates a random port number, opens a socket on 
     * the generated port
     */

    while(true){
      Random rand = new Random();
      selectedPort = (rand.nextInt((maxPort - minPort) + 1) + minPort);

      try {
        sock.bind(new InetSocketAddress(selectedPort));
      }
      catch (IOException e) {
        System.err.println("[MPJYarnClient.java]:- "+ selectedPort+
                  "]Port already in use. Checking for a new port..");
        continue;
      }
      break;
    }

    return selectedPort;
  }

  public static void main(String[] args) throws Exception {
      MPJYarnClient client = new MPJYarnClient();
      client.init(args);
      client.run();
  }
}
