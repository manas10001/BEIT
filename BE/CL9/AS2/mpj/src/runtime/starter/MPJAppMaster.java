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
 * File         : MPJAppMaster.java 
 * Author       : Hamza Zafar
 */

package runtime.starter;

import java.util.Collections;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.protocolrecords.
                                             RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Apps;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.*;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MPJAppMaster {

  private Configuration conf;
  private Socket appMasterSock;
  private int rank;
  private Options opts;
  private CommandLine cliParser;
  private int np;
  private String serverName;
  private int ioServerPort;
  private String deviceName;
  private String className;
  private String wdir;
  private String  psl;
  private String wireUpPort;
  private String wrapperPath;
  private String userJarPath;
  private String [] appArgs;
  private boolean debugYarn = false;
  private int containerMem;
  private int maxMem;
  private int containerCores;
  private int maxCores;
  private int mpjContainerPriority;
  private int allocatedContainers;
  private int completedContainers;
  private List<Container> mpiContainers = new ArrayList<Container>();

  public  MPJAppMaster(){
     
    conf = new YarnConfiguration();
    opts = new Options();
  
    opts.addOption("np",true,"Number of Processes");
    opts.addOption("serverName",true,"Hostname required for Server Socket");
    opts.addOption("ioServerPort",true,"Port required for a socket"+
                                                         " redirecting IO");
    opts.addOption("wireUpPort",true,"Port forwarded to Wrappers for "+
                                     "sharing read,write ports and ranks");
    opts.addOption("deviceName",true,"Specifies the MPJ device name");
    opts.addOption("className",true,"Main Class name");
    opts.addOption("wdir",true,"Specifies the current working directory");
    opts.addOption("psl",true,"Specifies the Protocol Switch Limit");
    opts.addOption("wrapperPath",true,"Specifies the wrapper jar path in hdfs");
    opts.addOption("userJarPath",true,"Specifies the user jar path in hdfs");
    opts.addOption("appArgs",true,"Specifies the User Application args");
    opts.getOption("appArgs").setArgs(Option.UNLIMITED_VALUES);
    opts.addOption("containerMem",true,"Specifies mpj containers memory");
    opts.addOption("containerCores",true,"Specifies mpj containers v-cores");
    opts.addOption("mpjContainerPriority",true,"Specifies the prioirty of" +
                                       "containers running MPI processes");
    opts.addOption("debugYarn",false,"Specifies the debug flag");

  }

  public void init(String [] args){
    try{

       cliParser = new GnuParser().parse(opts, args);
 
       np = Integer.parseInt(cliParser.getOptionValue("np"));
       serverName = cliParser.getOptionValue("serverName");
       ioServerPort =Integer.parseInt(cliParser.getOptionValue("ioServerPort"));
       wireUpPort = cliParser.getOptionValue("wireUpPort");
       deviceName = cliParser.getOptionValue("deviceName");
       className = cliParser.getOptionValue("className");
       wdir = cliParser.getOptionValue("wdir");
       psl = cliParser.getOptionValue("psl");
       wrapperPath = cliParser.getOptionValue("wrapperPath");
       userJarPath = cliParser.getOptionValue("userJarPath");

       containerMem = Integer.parseInt(cliParser.getOptionValue
                                                     ("containerMem","1024"));

       containerCores = Integer.parseInt(cliParser.getOptionValue
                                                     ("containerCores","1"));

       mpjContainerPriority = Integer.parseInt(cliParser.getOptionValue
						("mpjContainerPriority","0"));

       if(cliParser.hasOption("appArgs")){
         appArgs = cliParser.getOptionValues("appArgs");
       }
   
       if(cliParser.hasOption("debugYarn")){
         debugYarn = true;
       }
    }
    catch(Exception exp){
      exp.printStackTrace();
    }
  }
  public void run() throws Exception {
    try{ 
      appMasterSock = new Socket(serverName,ioServerPort);
   
      //redirecting stdout and stderr
      System.setOut(new PrintStream(appMasterSock.getOutputStream(),true));
      System.setErr(new PrintStream(appMasterSock.getOutputStream(),true));
    }
    catch(Exception exp){
      exp.printStackTrace();
    }
   
    FileSystem fs = FileSystem.get(conf);
    Path wrapperDest = new Path(wrapperPath);
    FileStatus destStatus = fs.getFileStatus(wrapperDest);
    
    Path userFileDest = new Path(userJarPath);
    FileStatus destStatusClass = fs.getFileStatus(userFileDest); 
    
    // Initialize AM <--> RM communication protocol
    AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
    rmClient.init(conf);
    rmClient.start();

    // Initialize AM <--> NM communication protocol
    NMClient nmClient = NMClient.createNMClient();
    nmClient.init(conf);
    nmClient.start();
    
    // Register with ResourceManager
    RegisterApplicationMasterResponse registerResponse = 
                              rmClient.registerApplicationMaster("", 0, "");
    // Priority for containers - priorities are intra-application
    Priority priority = Records.newRecord(Priority.class);
    priority.setPriority(mpjContainerPriority);

    maxMem =registerResponse.getMaximumResourceCapability().getMemory();
  
    if(debugYarn){
      System.out.println("[MPJAppMaster]: Max memory capability resources "+
			                              "in cluster: "+maxMem);
    }

    if(containerMem > maxMem){
      System.out.println("[MPJAppMaster]: container  memory specified above "+
                            "threshold of cluster! Using maximum memory for "+
                                                 "containers: "+containerMem);
      containerMem = maxMem;
    }

    maxCores=registerResponse.getMaximumResourceCapability().getVirtualCores();

    if(debugYarn){
      System.out.println("[MPJAppMaster]: Max v-cores capability resources "+
                                                      "in cluster: "+maxCores);
    }

    if(containerCores > maxCores){
     System.out.println("[MPJAppMaster]: virtual cores specified above "+
                            "threshold of cluster! Using maximum v-cores for "+
                                                 "containers: "+containerCores);
      containerCores = maxCores;
    }

    // Resource requirements for containers
    Resource capability = Records.newRecord(Resource.class);
    capability.setMemory(containerMem);
    capability.setVirtualCores(containerCores);

    // Make container requests to ResourceManager
    for (int i = 0; i < np; ++i) {
      ContainerRequest containerReq =
        		new ContainerRequest(capability, null, null, priority);
      
      rmClient.addContainerRequest(containerReq);
    }	
    
    Map<String,LocalResource> localResources = 
			 	new HashMap <String,LocalResource> ();
    // Creating Local Resource for Wrapper   
    LocalResource wrapperJar = Records.newRecord(LocalResource.class);

    wrapperJar.setResource(ConverterUtils.getYarnUrlFromPath(wrapperDest));
    wrapperJar.setSize(destStatus.getLen());
    wrapperJar.setTimestamp(destStatus.getModificationTime());
    wrapperJar.setType(LocalResourceType.ARCHIVE);
    wrapperJar.setVisibility(LocalResourceVisibility.APPLICATION);
 
    // Creating Local Resource for UserClass
    LocalResource userClass = Records.newRecord(LocalResource.class);
  
    userClass.setResource(ConverterUtils.getYarnUrlFromPath(userFileDest));
    userClass.setSize(destStatusClass.getLen());
    userClass.setTimestamp(destStatusClass.getModificationTime());
    userClass.setType(LocalResourceType.ARCHIVE);
    userClass.setVisibility(LocalResourceVisibility.APPLICATION);

    localResources.put("mpj-yarn-wrapper.jar", wrapperJar);
    localResources.put("user-code.jar",userClass);
    
    while (allocatedContainers < np){
      AllocateResponse response = rmClient.allocate(0);
      mpiContainers.addAll(response.getAllocatedContainers());
      allocatedContainers = mpiContainers.size();
      
      if(allocatedContainers!=np){Thread.sleep(100);}
    }
 
    if(debugYarn){
      System.out.println("[MPJAppMaster]: launching "+allocatedContainers+
                                                             " containers");
    }
 
    for (Container container : mpiContainers) {
 
        ContainerLaunchContext ctx =
                              Records.newRecord(ContainerLaunchContext.class);

        List <String> commands = new ArrayList<String>();

        commands.add(" $JAVA_HOME/bin/java");
        commands.add(" -Xmx"+containerMem+"m");
        commands.add(" runtime.starter.MPJYarnWrapper");
        commands.add("--serverName");
        commands.add(serverName);          // server name
        commands.add("--ioServerPort");
        commands.add(Integer.toString(ioServerPort)); // IO server port
        commands.add("--deviceName");
        commands.add(deviceName);          // device name
        commands.add("--className");
        commands.add(className);           // class name
        commands.add("--psl");
        commands.add(psl);                 // protocol switch limit
        commands.add("--np");
        commands.add(Integer.toString(np));   // no. of containers
        commands.add("--rank");
        commands.add(" " + Integer.toString(rank++)); // rank
 
        //temp sock port to share rank and ports
        commands.add("--wireUpPort");
        commands.add(wireUpPort);

        if( appArgs != null){
          commands.add("--appArgs");
          for(int i = 0; i < appArgs.length; i++){
            commands.add(appArgs[i]);
          }
        }

        ctx.setCommands(commands);

        // Set local resource for containers
        ctx.setLocalResources(localResources);

        // Set environment for container
        Map<String, String> containerEnv = new HashMap<String, String>();
        setupEnv(containerEnv);
        ctx.setEnvironment(containerEnv);

        // Time to start the container
        nmClient.startContainer(container, ctx);

      }


    while (completedContainers < np) {
      // argument to allocate() is the progress indicator
      AllocateResponse response = rmClient.allocate(completedContainers/np);

      for (ContainerStatus status : response.getCompletedContainersStatuses()){
        if(debugYarn){
          System.out.println("\n[MPJAppMaster]: Container Id - "+
                                                     status.getContainerId());
          System.out.println("[MPJAppMaster]: Container State - "+
                                                status.getState().toString());
          System.out.println("[MPJAppMaster]: Container Diagnostics - "+
                                                status.getDiagnostics());

         }
        ++completedContainers;
      }
	
      if(completedContainers!=np){Thread.sleep(100);};
    }
    // Un-register with ResourceManager 
    rmClient.unregisterApplicationMaster(FinalApplicationStatus.SUCCEEDED, 
								     "", "");
    //shutDown AppMaster IO
    System.out.println("EXIT");
  }

  private void setupEnv(Map<String, String> containerEnv) {
    for (String c : conf.getStrings(
      YarnConfiguration.YARN_APPLICATION_CLASSPATH,
      YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH)) {
                    
      Apps.addToEnvironment(containerEnv, 
				Environment.CLASSPATH.name(), c.trim());
                    
    }

    Apps.addToEnvironment(containerEnv,
                          Environment.CLASSPATH.name(),
                          Environment.PWD.$() + File.separator + "*");
  }

  

  public static void main(String[] args) throws Exception {
    for(String x: args){
      System.out.println(x);
    }
    MPJAppMaster am =new MPJAppMaster();
    am.init(args);
    am.run();
  }

}
