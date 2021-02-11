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
 * File         : MPJProcessTicket.java 
 * Author(s)    : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, 
 * 		  Bryan Carpenter, Farrukh Khan
 * Created      : Oct 10, 2013
 * Revision     : $
 * Updated      : Aug 27, 2014 
 */

package runtime.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

public class MPJProcessTicket {

  private UUID ticketID;
  private String classPath;
  private int processCount;
  private int startingRank;
  private ArrayList<String> jvmArgs;
  private String workingDirectory;
  private String mainClass;
  private boolean zippedSource;
  private String sourceCode;
  private String deviceName;
  private String confFileContents; 
  private ArrayList<String> appArgs;
  private String userID;

  // masterNode is the IP for MPJRun.java server
  private String masterNode;
  // masterNode is the port at which MPJRun.java server is listening
  private String masterPort;

  /* Hybrid Device */
  private int networkProcessCount;
  private int totalProcessCount;
  private String networkDevice;
  /* Debugger & Profiler */
  private boolean isDebug;
  private boolean isProfiler;
  private int debugPort;
  private String mpjHomeDir;

  public String getMasterNode() {
    return masterNode;
  }

  public void setMasterNode(String masterNode){
    this.masterNode = masterNode;
  }

  public String getMasterPort() {
    return masterPort;
  }
 
  public void setMasterPort(String masterPort){
    this.masterPort = masterPort;
  }

  public String getClassPath() {
    return classPath;
  }
  
  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

  public int getProcessCount() {
    return processCount;
  }

  public void setProcessCount(int processCount) {
    this.processCount = processCount;
  }

  public int getStartingRank() {
    return startingRank;
  }

  public void setStartingRank(int startingRank) {
    this.startingRank = startingRank;
  }

  public ArrayList<String> getJvmArgs() {
    return jvmArgs;
  }

  public void setJvmArgs(ArrayList<String> jvmArgs) {
    this.jvmArgs = jvmArgs;
  }

  public String getWorkingDirectory() {
    return workingDirectory;
  }

  public void setWorkingDirectory(String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public String getConfFileContents() {
    return confFileContents;
  }

  public void setConfFileContents(String confFileContents) {
    this.confFileContents = confFileContents;
  }

  public ArrayList<String> getAppArgs() {
    return appArgs;
  }

  public void setAppArgs(ArrayList<String> appArguments) {
    this.appArgs = appArguments;
  }

  public UUID getTicketID() {
    return ticketID;
  }

  public void setTicketID(UUID ticketID) {
    this.ticketID = ticketID;
  }

  public boolean isZippedSource() {
    return zippedSource;
  }

  public void setZippedSource(boolean zippedSource) {
    this.zippedSource = zippedSource;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public int getNetworkProcessCount() {
    return networkProcessCount;
  }

  public void setNetworkProcessCount(int networkProcessCount) {
    this.networkProcessCount = networkProcessCount;
  }

  public int getTotalProcessCount() {
    return totalProcessCount;
  }

  public void setTotalProcessCount(int totalProcessCount) {
    this.totalProcessCount = totalProcessCount;
  }

  public String getNetworkDevice() {
    return networkDevice;
  }

  public void setNetworkDevice(String networkDevice) {
    this.networkDevice = networkDevice;
  }

  public boolean isDebug() {
    return isDebug;
  }

  public void setDebug(boolean isDebug) {
    this.isDebug = isDebug;
  }

  public boolean isProfiler() {
    return isProfiler;
  }

  public void setProfiler(boolean isProfiler) {
    this.isProfiler = isProfiler;
  }

  public int getDebugPort() {
    return debugPort;
  }

  public void setDebugPort(int debugPort) {
    this.debugPort = debugPort;
  }
  
  public String getMpjHomeDir() {
    return mpjHomeDir;
  }

  public void setMpjHomeDir(String mpjHomeDir) {
    this.mpjHomeDir = mpjHomeDir;
  }

  public MPJProcessTicket() {
    this.classPath = "";
    this.processCount = 0;
    this.startingRank = 0;
    this.jvmArgs = new ArrayList<String>();
    this.workingDirectory = "";
    this.mainClass = "";
    this.deviceName = "";
    this.confFileContents = "";
    this.appArgs = new ArrayList<String>();
 
    this.masterNode = "";
    this.masterPort = "";

    zippedSource = false;
    ticketID = UUID.randomUUID();
    sourceCode = "";
    userID = "";
    totalProcessCount = -1;
    networkProcessCount = -1;
    networkDevice = "niodev";
    isDebug = false;
    isProfiler = false;
    debugPort = 24500;
    mpjHomeDir = null;
  }

  public MPJProcessTicket(UUID ticketID, String classPath, int processCount,
      int startingRank, ArrayList<String> jvmArgs, String workingDirectory,
      String mainClass, boolean zippedCode, String codeFolder,
      String deviceName, String confFileContents, ArrayList<String> appArgs,
      int clientPort, String clientHostAddress, String userID,
      int nioProcessCount, int totalProcessCount, String networkDevice, String mpjHomeDir,
      boolean isDebug, boolean isProfiler, int debugPort, String masterNode, String masterPort) {
    super();
    this.ticketID = ticketID;
    this.classPath = classPath;
    this.processCount = processCount;
    this.startingRank = startingRank;
    this.jvmArgs = jvmArgs;
    this.workingDirectory = workingDirectory;
    this.mainClass = mainClass;
    this.zippedSource = zippedCode;
    this.sourceCode = codeFolder;
    this.deviceName = deviceName;
    this.confFileContents = confFileContents;
    this.appArgs = appArgs;
    this.totalProcessCount = totalProcessCount;
    this.networkProcessCount = nioProcessCount;
    this.networkDevice = networkDevice;
    this.isDebug = isDebug;
    this.isProfiler = isProfiler;
    this.debugPort = debugPort;
    this.mpjHomeDir = mpjHomeDir;
    this.masterNode = masterNode;
    this.masterPort = masterPort;
  }

  public MPJXml ToXML() {

    // Send true as parameter in case you want sourceCode tag in XML Ticket
    return ToXML(true);
  }

  public MPJXml ToXML(boolean src) {
    MPJXml processInfoXML = new MPJXml(getTag(RTConstants.MPJ_PROCESS_INFO));

    MPJXml ticketIDXML = new MPJXml(getTag(RTConstants.TICKET_ID));
    ticketIDXML.setText(this.ticketID.toString());
    processInfoXML.appendChild(ticketIDXML);

    MPJXml classPathXML = new MPJXml(getTag(RTConstants.CLASS_PATH));
    classPathXML.setText(this.classPath);
    processInfoXML.appendChild(classPathXML);

    // MasterNode information
    MPJXml masterNodeXML = new MPJXml(getTag(RTConstants.MASTER_NODE));
    masterNodeXML.setText(this.masterNode);
    processInfoXML.appendChild(masterNodeXML);

    MPJXml masterPortXML = new MPJXml(getTag(RTConstants.MASTER_PORT));
    masterPortXML.setText(this.masterPort);
    processInfoXML.appendChild(masterPortXML);
    //-----------------------------------------

    MPJXml processCountXML = new MPJXml(getTag(RTConstants.PROCESS_COUNT));
    processCountXML.setText(Integer.toString(this.processCount));
    processInfoXML.appendChild(processCountXML);

    MPJXml startingRankXML = new MPJXml(getTag(RTConstants.STARTING_RANK));
    startingRankXML.setText(Integer.toString(this.startingRank));
    processInfoXML.appendChild(startingRankXML);

    MPJXml jvmArgsXML = new MPJXml(getTag(RTConstants.JVM_ARGS));
    for (String argument : this.jvmArgs) {
      MPJXml argumentXML = new MPJXml(getTag(RTConstants.ARGUMENT));
      argumentXML.setText(argument);
      jvmArgsXML.appendChild(argumentXML);
    }
    processInfoXML.appendChild(jvmArgsXML);

    MPJXml workingDirectoryXML = new MPJXml(
	getTag(RTConstants.WORKING_DIRECTORY));
    workingDirectoryXML.setText(this.workingDirectory);
    processInfoXML.appendChild(workingDirectoryXML);

    MPJXml zippedCodeXML = new MPJXml(getTag(RTConstants.ZIPPED_SOURCE));
    zippedCodeXML.setText(Boolean.toString(this.zippedSource));
    processInfoXML.appendChild(zippedCodeXML);

    if (src) {
      MPJXml codeFolderXML = new MPJXml(getTag(RTConstants.SOURCE_CODE));
      codeFolderXML.setText(this.sourceCode);
      processInfoXML.appendChild(codeFolderXML);
    }
    MPJXml mainClassXML = new MPJXml(getTag(RTConstants.MAIN_CLASS));
    mainClassXML.setText(this.mainClass);
    processInfoXML.appendChild(mainClassXML);

    MPJXml deviceNameXML = new MPJXml(getTag(RTConstants.DEVICE_NAME));
    deviceNameXML.setText(this.deviceName);
    processInfoXML.appendChild(deviceNameXML);

    MPJXml confFileContentsXML = new MPJXml(
	getTag(RTConstants.CONF_FILE_CONTENTS));
    confFileContentsXML.setText(this.confFileContents);
    processInfoXML.appendChild(confFileContentsXML);

    MPJXml appArgsXML = new MPJXml(getTag(RTConstants.APP_ARGS));
    for (String argument : this.appArgs) {
      MPJXml argumentXML = new MPJXml(getTag(RTConstants.ARGUMENT));
      argumentXML.setText(argument);
      appArgsXML.appendChild(argumentXML);
    }
    processInfoXML.appendChild(appArgsXML);

    MPJXml userIDXML = new MPJXml(getTag(RTConstants.USER_ID));
    userIDXML.setText(this.userID);
    processInfoXML.appendChild(userIDXML);

    MPJXml nioProcessCountXML = new MPJXml(
	getTag(RTConstants.NETWORK_PROCESS_COUNT));
    nioProcessCountXML.setText(Integer.toString(this.networkProcessCount));
    processInfoXML.appendChild(nioProcessCountXML);

    MPJXml totalProcessCountXML = new MPJXml(
	getTag(RTConstants.TOTAL_PROCESS_COUNT));
    totalProcessCountXML.setText(Integer.toString(this.totalProcessCount));
    processInfoXML.appendChild(totalProcessCountXML);

    MPJXml networkDeviceXML = new MPJXml(getTag(RTConstants.NETWORK_DEVICE));
    networkDeviceXML.setText(this.networkDevice);
    processInfoXML.appendChild(networkDeviceXML);

    MPJXml mpjHomeXML = new MPJXml(getTag(RTConstants.MPJ_HOME));
    mpjHomeXML.setText(this.mpjHomeDir);
    processInfoXML.appendChild(mpjHomeXML);
    
    MPJXml debugXML = new MPJXml(getTag(RTConstants.DEBUG));
    debugXML.setText(Boolean.toString(this.isDebug));
    processInfoXML.appendChild(debugXML);

    MPJXml debugPortXML = new MPJXml(getTag(RTConstants.DEBUG_PORT));
    debugPortXML.setText(Integer.toString(this.debugPort));
    processInfoXML.appendChild(debugPortXML);

    MPJXml profilerXML = new MPJXml(getTag(RTConstants.PROFILER));
    profilerXML.setText(Boolean.toString(this.isProfiler));
    processInfoXML.appendChild(profilerXML);

    return processInfoXML;

  }

  public void FromXML(String xmlString) {
    if (xmlString != null) {
      MPJXml processInfoXml = new MPJXml(xmlString);

      MPJXml ticketIDXML = processInfoXml.getChild(RTConstants.TICKET_ID);
      this.ticketID = UUID.fromString(ticketIDXML.getText());

      MPJXml classPathXML = processInfoXml.getChild(RTConstants.CLASS_PATH);
      this.classPath = classPathXML.getText();

      // Obtaining masterNode information from XML 
      MPJXml masterNodeXML = processInfoXml.getChild(RTConstants.MASTER_NODE);
      this.masterNode = masterNodeXML.getText();

      MPJXml masterPortXML = processInfoXml.getChild(RTConstants.MASTER_PORT);
      this.masterPort = masterPortXML.getText();
      //---------------------------------------------

      MPJXml processCountXML = processInfoXml
	  .getChild(RTConstants.PROCESS_COUNT);
      this.processCount = Integer.parseInt(processCountXML.getText());

      MPJXml startingRankXML = processInfoXml
	  .getChild(RTConstants.STARTING_RANK);
      this.startingRank = Integer.parseInt(startingRankXML.getText());

      MPJXml jvmArgsXML = processInfoXml.getChild(RTConstants.JVM_ARGS);
      if (jvmArgsXML != null) {
	ArrayList<MPJXml> arguments = jvmArgsXML
	    .getChildren(RTConstants.ARGUMENT);
	for (MPJXml argumentXML : arguments) {
	  this.jvmArgs.add(argumentXML.getText());
	}
      }

      MPJXml workingDirectoryXML = processInfoXml
	  .getChild(RTConstants.WORKING_DIRECTORY);
      this.workingDirectory = workingDirectoryXML.getText();

      MPJXml zippedCodeXML = processInfoXml.getChild(RTConstants.ZIPPED_SOURCE);
      this.zippedSource = Boolean.parseBoolean(zippedCodeXML.getText());

      MPJXml codeFolderXML = processInfoXml.getChild(RTConstants.SOURCE_CODE);
      this.sourceCode = codeFolderXML.getText();

      MPJXml mainClassXML = processInfoXml.getChild(RTConstants.MAIN_CLASS);
      this.mainClass = mainClassXML.getText();

      MPJXml deviceNameXML = processInfoXml.getChild(RTConstants.DEVICE_NAME);
      this.deviceName = deviceNameXML.getText();

      MPJXml mpjHomeXML = processInfoXml.getChild(RTConstants.MPJ_HOME);
      this.mpjHomeDir = mpjHomeXML.getText();
      
      MPJXml confFileContentsXML = processInfoXml
	  .getChild(RTConstants.CONF_FILE_CONTENTS);
      this.confFileContents = confFileContentsXML.getText();

      MPJXml appArgsXML = processInfoXml.getChild(RTConstants.APP_ARGS);
      if (appArgsXML != null) {
	ArrayList<MPJXml> arguments = appArgsXML
	    .getChildren(RTConstants.ARGUMENT);
	for (MPJXml argumentXML : arguments) {
	  this.appArgs.add(argumentXML.getText());
	}
      }

      MPJXml userIDXML = processInfoXml.getChild(RTConstants.USER_ID);
      this.userID = userIDXML.getText();

      MPJXml nioProcessCountXML = processInfoXml
	  .getChild(RTConstants.NETWORK_PROCESS_COUNT);
      this.networkProcessCount = Integer.parseInt(nioProcessCountXML.getText());

      MPJXml totalProcessCountXML = processInfoXml
	  .getChild(RTConstants.TOTAL_PROCESS_COUNT);
      this.totalProcessCount = Integer.parseInt(totalProcessCountXML.getText());

      MPJXml networkDeviceXML = processInfoXml
	  .getChild(RTConstants.NETWORK_DEVICE);
      this.networkDevice = networkDeviceXML.getText();

      MPJXml debugXML = processInfoXml.getChild(RTConstants.DEBUG);
      this.isDebug = Boolean.parseBoolean(debugXML.getText());

      MPJXml debugPortXML = processInfoXml.getChild(RTConstants.DEBUG_PORT);
      this.debugPort = Integer.parseInt(debugPortXML.getText());

      MPJXml profilerXML = processInfoXml.getChild(RTConstants.PROFILER);
      this.isProfiler = Boolean.parseBoolean(profilerXML.getText());
    }

  }

  public String getTag(String tagText) {
    return "<" + tagText.trim() + "/>";
  }

  /*
   * public static void main(String args[]) {
   * 
   * ArrayList<String> jvmArgs = new ArrayList<String>(); jvmArgs.add("arg0");
   * jvmArgs.add("arg1"); ArrayList<String> appArgs = new ArrayList<String>();
   * appArgs.add("arg0"); appArgs.add("arg1"); MPJProcessTicket ticket = new
   * MPJProcessTicket(UUID.randomUUID(), "ClassPath", 10, 1, jvmArgs,
   * "workingDirectory", "mainClass", false, "", "deviceName",
   * "confFileContents", appArgs, 1000, "localhost", "Khurram", 5, 10, "niodev",
   * false, false, 24500); IOHelper.writeCharacterFile("D:\\Test.xml",
   * ticket.ToXML().toXmlString()); String xmlString =
   * IOHelper.readCharacterFile("D:\\Test.xml"); MPJProcessTicket ticket2 = new
   * MPJProcessTicket(); ticket2.FromXML(xmlString);
   * IOHelper.writeCharacterFile("D:\\Test2.xml",
   * ticket2.ToXML().toXmlString());
   * 
   * }
   */

}
