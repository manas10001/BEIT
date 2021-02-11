/*
 The MIT License

 Copyright (c) 2013 - 2013
   1. High Performance Computing Group, 
   School of Electrical Engineering and Computer Science (SEECS), 
   National University of Sciences and Technology (NUST)
   2. Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter (2013 - 2013)
   

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
 * File         : CLOptions.java 
 * Author       : Khurram Shahzad, Mohsan Jameel, Aamir Shafi, Bryan Carpenter
 * Created      : January 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 */

package runtime.daemonmanager;

import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import runtime.common.MPJUtil;
import runtime.common.RTConstants;

public class CLOptions {

  private ArrayList<String> machineList;
  private int threadCount;
  private String cmdType;
  private String userCmd;
  private String machineFilePath;
  private String port;

  public CLOptions(ArrayList<String> machineList, int threadCount,
      boolean bThreading, String cmdType, String userCmd, String port) {
    super();
    this.machineList = machineList;
    this.threadCount = threadCount;
    this.cmdType = cmdType;
    this.userCmd = userCmd;
    this.port = port;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public CLOptions() {
    super();
    this.machineList = new ArrayList<String>();
    this.threadCount = (int) (Math.log(2) / Math.log(2));
    this.cmdType = DMConstants.STATUS;
    this.userCmd = "";
    this.machineFilePath = "";
    this.port = RTConstants.MPJ_DAEMON_PORT;
    this.machineFilePath = MPJUtil.getMachineFilePath();
  }

  public ArrayList<String> getMachineList() {
    return machineList;
  }

  public void setMachineList(ArrayList<String> machineList) {
    this.machineList = machineList;
  }

  public int getThreadCount() {
    return threadCount;
  }

  public void setThreadCount(int nThreads) {
    this.threadCount = nThreads;
  }

  public String getCmdType() {
    return cmdType;
  }

  public void setCmdType(String cmdType) {
    this.cmdType = cmdType;
  }

  public String getUserCmd() {
    return userCmd;
  }

  public void setUserCmd(String userCmd) {
    this.userCmd = userCmd;
  }

  public String getMachineFilePath() {
    return machineFilePath;
  }

  public void setMachineFilePath(String machineFilePath) {
    this.machineFilePath = machineFilePath;
  }

  public void PrintOptions() {
    System.out.println("Command Type: " + this.cmdType);
    System.out.println("Machine File Path: " + this.machineFilePath);
    for (String hostname : this.machineList)
      System.out.println("Host Name: " + hostname);
    System.out.println("No of Threads: " + this.threadCount);

  }

  public void PrintHelp() {
    System.out.println(FormatCMDoptionMessages("", DMConstants.HELP,
	DMMessages.CMD_OPT_HELP));
    System.out.println(FormatCMDoptionMessages(DMConstants.BOOT_OPT,
	DMConstants.BOOT, DMMessages.CMD_OPT_BOOT));
    System.out.println(FormatCMDoptionMessages(DMConstants.HALT_OPT,
	DMConstants.HALT, DMMessages.CMD_OPT_HALT));
    System.out.println(FormatCMDoptionMessages(DMConstants.CLEAN_OPT,
	DMConstants.CLEAN, DMMessages.CMD_OPT_CLEAN));
    System.out.println(FormatCMDoptionMessages(DMConstants.STATUS_OPT,
	DMConstants.STATUS, DMMessages.CMD_OPT_STATUS));
    System.out.println(FormatCMDoptionMessages(DMConstants.INFO_OPT,
	DMConstants.INFO, DMMessages.CMD_OPT_INFO));

    System.out.println(FormatCMDoptionMessages(DMConstants.MACHINE_FILE_OPT,
	DMConstants.MACHINE_FILE, DMMessages.CMD_OPT_MACHINE_FILE));
    System.out.println(FormatCMDoptionMessages(DMConstants.HOSTS_OPT,
	DMConstants.HOSTS, DMMessages.CMD_OPT_HOSTS));
    System.out.println(FormatCMDoptionMessages(DMConstants.THREAD_COUNT_OPT,
	DMConstants.THREAD_COUNT, DMMessages.CMD_OPT_THREAD_COUNT));
    System.out.println(FormatCMDoptionMessages(DMConstants.THREADED_OPT,
	DMConstants.THREADED, DMMessages.CMD_OPT_THREADED));
    System.out.println(FormatCMDoptionMessages(DMConstants.PORT_OPT,
	DMConstants.PORT, DMMessages.CMD_OPT_PORT));
    System.out.println("");

  }

  public String FormatCMDoptionMessages(String shortOption, String longOption,
      String description) {
    String message = "";

    String option = "- ";
    if (shortOption == "")
      option = option + longOption + ": ";
    else
      option += shortOption + "|" + longOption + ": ";

    for (int i = option.length(); i <= 15; i++)
      option += " ";
    message = option + description;
    return message;
  }

  @SuppressWarnings("static-access")
  public void parseCommandLineArgs(String[] args) {

    CommandLineParser parser = new PosixParser();

    Options options = new Options();
    options.addOption(new Option(DMConstants.HELP_OPT, DMConstants.HELP, false,
	DMMessages.CMD_OPT_HELP));
    options.addOption(new Option(DMConstants.BOOT_OPT, DMConstants.BOOT, false,
	DMMessages.CMD_OPT_BOOT));
    options.addOption(new Option(DMConstants.HALT_OPT, DMConstants.HALT, false,
	DMMessages.CMD_OPT_HALT));
    options.addOption(new Option(DMConstants.CLEAN_OPT, DMConstants.CLEAN,
	false, DMMessages.CMD_OPT_CLEAN));
    options.addOption(new Option(DMConstants.STATUS_OPT, DMConstants.STATUS,
	false, DMMessages.CMD_OPT_STATUS));
    options.addOption(new Option(DMConstants.INFO_OPT, DMConstants.INFO, false,
	DMMessages.CMD_OPT_INFO));
    options.addOption(new Option(DMConstants.MACHINE_FILE_OPT,
	DMConstants.MACHINE_FILE, true, DMMessages.CMD_OPT_MACHINE_FILE));
    options.addOption(OptionBuilder.withLongOpt(DMConstants.HOSTS).hasArgs()
	.withDescription(DMMessages.CMD_OPT_HOSTS).withValueSeparator(' ')
	.create(DMConstants.HOSTS_OPT));
    options.addOption(new Option(DMConstants.THREAD_COUNT_OPT,
	DMConstants.THREAD_COUNT, true, DMMessages.CMD_OPT_THREAD_COUNT));
    options.addOption(new Option(DMConstants.PORT_OPT, DMConstants.PORT, true,
	DMMessages.CMD_OPT_PORT));
    options.addOption(new Option(DMConstants.WIN_BOOT_OPT,
	DMConstants.WIN_BOOT, false, DMMessages.CMD_OPT_BOOT));
    options.addOption(new Option(DMConstants.WIN_HALT_OPT,
	DMConstants.WIN_HALT, false, DMMessages.CMD_OPT_HALT));

    try {
      CommandLine line = parser.parse(options, args);
      if (line.hasOption(DMConstants.HELP))
	this.setCmdType(DMConstants.HELP);
      else if (line.hasOption(DMConstants.BOOT))
	this.setCmdType(DMConstants.BOOT);
      else if (line.hasOption(DMConstants.HALT))
	this.setCmdType(DMConstants.HALT);
      else if (line.hasOption(DMConstants.STATUS))
	this.setCmdType(DMConstants.STATUS);
      else if (line.hasOption(DMConstants.INFO))
	this.setCmdType(DMConstants.INFO);
      else if (line.hasOption(DMConstants.CLEAN))
	this.setCmdType(DMConstants.CLEAN);
      else if (line.hasOption(DMConstants.WIN_BOOT))
	this.setCmdType(DMConstants.WIN_BOOT);
      else if (line.hasOption(DMConstants.WIN_HALT))
	this.setCmdType(DMConstants.WIN_HALT);

      if (line.hasOption(DMConstants.HOSTS)) {
	String[] hosts = line.getOptionValues(DMConstants.HOSTS);
	for (String host : hosts) {
	  this.getMachineList().add(host);
	  this.setThreadCount(this.getMachineList().size());
	}
      } else if (line.hasOption(DMConstants.MACHINE_FILE)) {
	String machineFilePath = line.getOptionValue(DMConstants.MACHINE_FILE);
	this.setMachineFilePath(machineFilePath);
	int nSize = MPJUtil.readMachineFile(machineFilePath).size();
	int nThreads = 1;
	if (nSize > 1)
	  nThreads = (int) (Math.log(nSize) / Math.log(2));
	this.setThreadCount(nThreads);
      }

      if (line.hasOption(DMConstants.THREAD_COUNT)) {
	int nThreads = Integer.parseInt(line
	    .getOptionValue(DMConstants.THREAD_COUNT));
	this.setThreadCount(nThreads);
      }

      if (line.hasOption(DMConstants.PORT)) {
	Integer port = Integer.parseInt(line.getOptionValue(DMConstants.PORT));
	this.setPort(port.toString());
      }

    }
    catch (ParseException e) {
      e.printStackTrace();
    }

  }

  /* Main only for testing purposes */
  /*
   * public static void main(String[] args) { CLOptions options = new
   * CLOptions(); options.parseCommandLineArgs(args); options.PrintOptions(); }
   */

}
