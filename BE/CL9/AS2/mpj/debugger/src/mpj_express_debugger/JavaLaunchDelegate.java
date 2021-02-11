package mpj_express_debugger;

/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/*
 The MIT License

 Copyright (c) 2005 - 2013
 1. SEECS National University of Sciences and Technology (NUST), Pakistan
 2. Amjad Aziz (2013 - 2013)
 3. Rizwan Hanif (2013 - 2013)
 4. Mohsan Jameel (2013 - 2013)
 5. Aamir Shafi (2005 -2013) 
 6. Bryan Carpenter (2005 - 2013)

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


 * File         : JavaLaunchDelegate.java 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;

import com.ibm.icu.text.MessageFormat;
import com.sun.jdi.connect.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.internal.launching.StandardVMDebugger;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * A launch delegate for launching local Java applications.
 * <p>
 * Clients may subclass and instantiate this class.
 * </p>
 * 
 * @since 3.1
 */
@SuppressWarnings("restriction")
public class JavaLaunchDelegate extends AbstractJavaLaunchConfigurationDelegate {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse
   * .debug.core.ILaunchConfiguration, java.lang.String,
   * org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
   */
  static private final String CONF_FILE_NAME = "mpjdev.conf";

  String mpjDev;
  String port;

  public void launch(ILaunchConfiguration configuration1, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {
    int port = 0;
    ILaunchConfiguration configuration = null;
    ILaunchConfigurationWorkingCopy copy = configuration1.copy(configuration1
        .getName() + "_1");

    configuration = appendMPJArguments(copy);
    if (monitor == null) {
      monitor = new NullProgressMonitor();
    }
    monitor.beginTask(MessageFormat.format(
        "{0}...", new String[] { configuration.getName() }), 3); //$NON-NLS-1$
    // check for cancellation
    if (monitor.isCanceled()) {
      return;
    }
    try {
      monitor
          .subTask(LaunchingMessages.JavaLocalApplicationLaunchConfigurationDelegate_Verifying_launch_attributes____1);
      String mainTypeName = verifyMainTypeName(configuration);
      IVMInstall vm = verifyVMInstall(configuration);
      IVMRunner runner;// = getVMRunner(configuration, mode);
      // Chanin
      /*
       * if(mode.equals("debug")) { runner = new StandardParallelVMDebugger(vm);
       * }
       */
      // else {
      runner = getVMRunner(configuration, "run");
      // }
      File workingDir = verifyWorkingDirectory(configuration);
      String workingDirName = null;
      if (workingDir != null) {
        workingDirName = workingDir.getAbsolutePath();
      }
      String[] envp = getEnvironment(configuration);
      String pgmArgs = getProgramArguments(configuration);
      String vmArgs = getVMArguments(configuration);

      ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
      Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
      String[] classpath = getClasspath(configuration);
      VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName,
          classpath);
      runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
      runConfig.setEnvironment(envp);
      runConfig.setVMArguments(execArgs.getVMArgumentsArray());
      runConfig.setWorkingDirectory(workingDirName);
      runConfig.setVMSpecificAttributesMap(vmAttributesMap);
      runConfig.setBootClassPath(getBootpath(configuration));

      // check for cancellation
      if (monitor.isCanceled()) {
        return;
      }

      // stop in main
      prepareStopInMain(configuration);

      // done the verification phase
      monitor.worked(1);

      monitor
          .subTask(LaunchingMessages.JavaLocalApplicationLaunchConfigurationDelegate_Creating_source_locator____2);
      // set the default source locator if required
      setDefaultSourceLocator(launch, configuration);
      monitor.worked(1);

      // Launch the configuration - 1 unit of work
      runner.run(runConfig, launch, monitor);

      if (mode.equals("debug")) {
        try {
          Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        JavaRemoteParallelApplicationLaunchConfigurationDelegate javaRmotAppConfigDlgt = new JavaRemoteParallelApplicationLaunchConfigurationDelegate();
        javaRmotAppConfigDlgt.argMap = configuration.getAttribute(
            IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null);
        ArrayList<String> portsMap = new ArrayList<String>();
        if (mpjDev.equals("multicore"))
          setMulticoreMap(portsMap);
        else
          // if(mpjDev.equals("hybdev"))
          setclusterMap(portsMap);

        // System.out.println("Totall mapping size is of"+portsMap.size());
        for (int i = 0; i < portsMap.size(); i++) {
          String host = portsMap.get(i);
          javaRmotAppConfigDlgt.setPort(host.split(":")[0], host.split(":")[1]);
          // System.out.println("Connecting to "+host);
          javaRmotAppConfigDlgt.launch(configuration, mode, launch, monitor);
        }
      }
      // check for cancellation
      if (monitor.isCanceled()) {
        return;
      }
    } finally {
      if (configuration != null)

        configuration.delete();

      monitor.done();
    }
  }

  private String[] readConfigFile(String filename) {
    String array[] = null;
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      StringTokenizer conf_file_tokenizer = new StringTokenizer(
          reader.readLine(), ";");
      reader.close();
      // System.out.println("Totall lines in config file are <"+conf_file_tokenizer.countTokens());
      array = new String[conf_file_tokenizer.countTokens() - 6];
      int i = 0;
      for (i = 0; i < 6; i++)
        conf_file_tokenizer.nextToken();
      i = 0;
      while (conf_file_tokenizer.hasMoreTokens()) {
        String line = conf_file_tokenizer.nextToken();
        String[] lineSegments = line.split("@");
        array[i++] = lineSegments[0] + ":" + lineSegments[3];
      }
      return array;
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private void setclusterMap(ArrayList<String> portsMap) {
    // TODO Auto-generated method stub
    String[] mapping = readConfigFile(System.getProperty("user.home")
        + File.separator + CONF_FILE_NAME);
    // System.out.println("mapping length from file reader is"+mapping.length);
    for (int i = 0; i < mapping.length; i++) {
      portsMap.add(mapping[i]);
    }
  }

  private void setMulticoreMap(ArrayList<String> portsMap) {
    // TODO Auto-generated method stub
    portsMap.add("localhost" + ":" + port);
  }

  private ILaunchConfiguration appendMPJArguments(
      ILaunchConfigurationWorkingCopy copy) {

    String userArgs = "";
    try {
      userArgs = copy.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
    } catch (CoreException e) {
      userArgs = " ";
    }
    String home;
    try {
      home = copy.getAttribute(IMPJLaunchConfigurationConstants.ATTR_MPJ_HOME,
          System.getenv("MPJ_HOME"));
    } catch (CoreException e) {
      home = System.getenv("MPJ_HOME");
    }

    copy.setAttribute(IMPJLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
        getMPJArguments(copy) + " " + userArgs);
    copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE,
        new Boolean(true).toString());
    Map env;
    // adding MPJ_HOME to environment variable
    try {

      env = copy.getAttribute(
          org.eclipse.debug.core.ILaunchManager.ATTR_ENVIRONMENT_VARIABLES,
          (Map) null);
    } catch (CoreException e) {
      env = null;
    }
    if (env == null)
      env = new HashMap();

    env.put("MPJ_HOME", home);
    copy.setAttribute(
        org.eclipse.debug.core.ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, env);
    copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE,
        true);
    IVMConnector connector = JavaRuntime
        .getVMConnector(IJavaLaunchConfigurationConstants.ID_SOCKET_ATTACH_VM_CONNECTOR);
    Map def = null;
    try {
      def = connector.getDefaultArguments();
    } catch (CoreException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    Map argMap = new HashMap(def.size());
    Iterator iter = connector.getArgumentOrder().iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      Connector.Argument arg = (Connector.Argument) def.get(key);
      argMap.put(key, arg.toString());
    }
    copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP,
        argMap);
    // ILaunchConfiguration attachConfig = config.doSave();
    try {
      return copy.doSave();

    } catch (CoreException e) {
      // TODO Auto-generated catch block
      return null;
    }

  }

  private String getMPJArguments(ILaunchConfiguration configuration) {
    String MPJArguments = "";
    String dev, home;
    Map mpjParameters = null;
    int np;
    try {
      dev = configuration.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_MPJ_DEV, "multicore");
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      dev = "multicore";
    }
    mpjDev = dev;
    try {
      np = configuration.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_MPJ_NP, 2);
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      np = 2;
    }
    try {
      home = configuration.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_MPJ_HOME,
          System.getenv("MPJ_HOME"));
    } catch (CoreException e) {
      home = System.getenv("MPJ_HOME");
    }
    try {
      mpjParameters = configuration.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_MPJ_PARAMETERS, (Map) null);
    } catch (CoreException e) {
      mpjParameters = null;

    }
    MPJArguments = "-jar " + home + "/lib/starter.jar" + " -np " + np
        + " -dev " + dev + " " + formatMPJArguments(mpjParameters);
    return MPJArguments;
  }

  private String formatMPJArguments(Map envp) {
    String args = "";
    if (envp != null) {
      Set keys = envp.keySet();
      Iterator iter = keys.iterator();
      String key_iter;
      for (; iter.hasNext();) {
        key_iter = iter.next().toString();
        args = args + "-" + key_iter + " " + envp.get(key_iter) + " ";
        if (key_iter.equals("debug"))
          port = envp.get(key_iter).toString();

      }
    }
    return args;
  }

  /*
   * public static int findFreePort(){ int port =0; ServerSocket server; try {
   * server = new ServerSocket(0);
   * 
   * port = server.getLocalPort(); server.close(); } catch (IOException e) { //
   * TODO Auto-generated catch block e.printStackTrace(); } return port; }
   */
  private void printVals(Map envp) {
    if (envp != null) {
      Set keys = envp.keySet();
      Iterator iter = keys.iterator();
      String key_iter;// = iter.next().toString();
      for (; iter.hasNext();) {
        key_iter = iter.next().toString();
        System.out.println("Key " + key_iter + "  is  " + envp.get(key_iter));

      }
    }
  }

}