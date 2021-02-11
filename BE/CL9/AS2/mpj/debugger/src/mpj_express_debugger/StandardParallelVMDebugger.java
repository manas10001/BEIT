/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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


 * File         : StandardParallelVMDebugger.java 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */
package mpj_express_debugger;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.jdi.Bootstrap;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.internal.launching.LibraryInfo;
import org.eclipse.jdt.internal.launching.StandardVM;
import org.eclipse.jdt.internal.launching.StandardVMRunner;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

/**
 * A launcher for debugging Java main classes. Uses JDI to launch a vm in debug
 * mode.
 */
@SuppressWarnings("restriction")
public class StandardParallelVMDebugger extends StandardVMRunner {

  /**
   * @since 3.3 OSX environment variable specifying JRE to use
   */
  protected static final String JAVA_JVM_VERSION = "JAVA_JVM_VERSION"; //$NON-NLS-1$

  /**
   * Jre path segment descriptor
   * 
   * String equals the word: <code>jre</code>
   * 
   * @since 3.3.1
   */
  protected static final String JRE = "jre"; //$NON-NLS-1$

  /**
   * Bin path segment descriptor
   * 
   * String equals the word: <code>bin</code>
   * 
   * @since 3.3.1
   */
  protected static final String BIN = "bin"; //$NON-NLS-1$

  /**
   * Used to attach to a VM in a separate thread, to allow for cancellation and
   * detect that the associated System process died before the connect occurred.
   */
  class ConnectRunnable implements Runnable {

    private VirtualMachine[] fVirtualMachine = null;
    private AttachingConnector fConnector = null;
    private Map fConnectionMap = null;
    private Exception fException = null;
    private int Processes;
    private int currTarget = 0;
    private int currVM = 0;

    /**
     * Constructs a runnable to connect to a VM via the given connector with the
     * given connection arguments.
     * 
     * @param connector
     * @param map
     */
    public ConnectRunnable(AttachingConnector connector, Map map) {
      fConnector = connector;
      fConnectionMap = map;
    }

    public void setProcesses(int processes) {
      Processes = processes;
      fVirtualMachine = new VirtualMachine[Processes];
    }

    public int getProcesses() {
      return Processes;
    }

    public void run() {
      while (currTarget < Processes) {
        try {

          fVirtualMachine[currTarget++] = fConnector.attach(fConnectionMap);

        } catch (IOException e) {
          fException = e;
        } catch (IllegalConnectorArgumentsException e) {
          fException = e;
        }
      }// end of while currTarget<Processes
    }

    /**
     * Returns the VM that was attached to, or <code>null</code> if none.
     * 
     * @return the VM that was attached to, or <code>null</code> if none
     */
    public VirtualMachine getVirtualMachine() {
      if (currVM < Processes)
        return fVirtualMachine[currVM++];
      else
        return null;
    }

    /**
     * Returns any exception that occurred while attaching, or <code>null</code>
     * .
     * 
     * @return IOException or IllegalConnectorArgumentsException
     */
    public Exception getException() {
      return fException;
    }
  }

  /**
   * Creates a new launcher
   */
  public StandardParallelVMDebugger(IVMInstall vmInstance) {
    super(vmInstance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jdt.launching.IVMRunner#run(org.eclipse.jdt.launching.
   * VMRunnerConfiguration, org.eclipse.debug.core.ILaunch,
   * org.eclipse.core.runtime.IProgressMonitor)
   */
  @SuppressWarnings("restriction")
  public void run(VMRunnerConfiguration config, ILaunch launch,
      IProgressMonitor monitor) throws CoreException {

    if (monitor == null) {
      monitor = new NullProgressMonitor();
    }
    int Processes = 1;
    // System.out.println("Hello ------------------------");
    IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
    subMonitor.beginTask(
        LaunchingMessages.StandardVMDebugger_Launching_VM____1, 4);
    subMonitor
        .subTask(LaunchingMessages.StandardVMDebugger_Finding_free_socket____2);

    int port = SocketUtil.findFreePort();
    if (port == -1) {
      abort(
          LaunchingMessages.StandardVMDebugger_Could_not_find_a_free_socket_for_the_debugger_1,
          null, IJavaLaunchConfigurationConstants.ERR_NO_SOCKET_AVAILABLE);
    }

    subMonitor.worked(1);

    // check for cancellation
    if (monitor.isCanceled()) {
      return;
    }

    subMonitor
        .subTask(LaunchingMessages.StandardVMDebugger_Constructing_command_line____3);

    String program = constructProgramString(config);

    List arguments = new ArrayList(12);

    arguments.add(program);

    String[] allVMArgs = combineVmArgs(config, fVMInstance);
    // print(allVMArgs);
    addArguments(ensureEncoding(launch, allVMArgs), arguments);

    if (fVMInstance instanceof StandardVM
        && ((StandardVM) fVMInstance).getDebugArgs() != null) {
      String debugArgString = ((StandardVM) fVMInstance).getDebugArgs()
          .replaceAll(
              "\\Q" + StandardVM.VAR_PORT + "\\E", Integer.toString(port)); //$NON-NLS-1$ //$NON-NLS-2$
      String[] debugArgs = DebugPlugin.parseArguments(debugArgString);
      for (int i = 0; i < debugArgs.length; i++) {
        arguments.add(debugArgs[i]);
      }
    } else {
      // VM arguments are the first thing after the java program so that users
      // can specify
      // options like '-client' & '-server' which are required to be the first
      // options
      double version = getJavaVersion();
      if (version < 1.5) {
        arguments.add("-Xdebug"); //$NON-NLS-1$
        arguments.add("-Xnoagent"); //$NON-NLS-1$
      }

      // check if java 1.4 or greater
      if (version < 1.4) {
        arguments.add("-Djava.compiler=NONE"); //$NON-NLS-1$
      }
      if (version < 1.5) {
        arguments
            .add("-Xrunjdwp:transport=dt_socket,suspend=y,server=y,address=" + port); //$NON-NLS-1$
      } else {
        arguments
            .add("-agentlib:jdwp=transport=dt_socket,suspend=y,server=y,address=" + port); //$NON-NLS-1$
      }

    }

    addBootClassPathArguments(arguments, config);

    String[] cp = config.getClassPath();
    if (cp.length > 0) {
      arguments.add("-classpath"); //$NON-NLS-1$
      arguments.add(convertClassPath(cp));
    }

    arguments.add(config.getClassToLaunch());
    addArguments(config.getProgramArguments(), arguments);
    String[] cmdLine = new String[arguments.size()];
    arguments.toArray(cmdLine);
    /**
     * print(cmdLine); With the newer VMs and no backwards compatibility we have
     * to always prepend the current env path (only the runtime one) with a
     * 'corrected' path that points to the location to load the debug dlls from,
     * this location is of the standard JDK installation format: <jdk
     * path>/jre/bin
     */
    String[] envp = prependJREPath(config.getEnvironment(), new Path(program));

    // check for cancellation
    if (monitor.isCanceled()) {
      return;
    }

    subMonitor.worked(1);
    subMonitor
        .subTask(LaunchingMessages.StandardVMDebugger_Starting_virtual_machine____4);

    AttachingConnector connector = getConnector();
    if (connector == null) {
      abort(
          LaunchingMessages.StandardVMDebugger_Couldn__t_find_an_appropriate_debug_connector_2,
          null, IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE);
    }
    Map map = connector.defaultArguments();
    // System.out.println(connector.name()+" is Connector Name .. ");
    // printMap(map);
    specifyArguments(map, port);
    // printMap(map);
    Process p = null;
    try {
      try {
        // check for cancellation
        if (monitor.isCanceled()) {
          return;
        }
        /*
         * if(connector.supportsMultipleConnections())
         * System.out.println("Supports Multiple");
         */
        // connector.startListening(map);
        print(cmdLine);
        File workingDir = getWorkingDir(config);
        p = exec(cmdLine, workingDir, envp);
        if (p == null) {
          return;
        }

        // check for cancellation
        if (monitor.isCanceled()) {
          p.destroy();
          return;
        }

        IProcess process = newProcess(launch, p,
            renderProcessLabel(cmdLine, program), getDefaultProcessMap());
        process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));
        subMonitor.worked(1);
        // connector.startListening(map);
        for (int iProcess = 0; iProcess < 1; iProcess++) {
          subMonitor
              .subTask(LaunchingMessages.StandardVMDebugger_Establishing_debug_connection____5);
          boolean retry = false;
          do {
            try {

              ConnectRunnable runnable = new ConnectRunnable(connector, map);
              runnable.setProcesses(Processes);

              Thread connectThread = new Thread(runnable, "Listening Connector"); //$NON-NLS-1$
              connectThread.setDaemon(true);
              connectThread.start();
              while (connectThread.isAlive()) {
                if (monitor.isCanceled()) {
                  p.destroy();
                  return;
                }
                try {
                  p.exitValue();
                  checkErrorMessage(process);
                } catch (IllegalThreadStateException e) {
                  // expected while process is alive
                }
                try {
                  Thread.sleep(100);
                } catch (InterruptedException e) {
                }
              }

              Exception ex = runnable.getException();
              if (ex instanceof IllegalConnectorArgumentsException) {
                throw (IllegalConnectorArgumentsException) ex;
              }
              if (ex instanceof InterruptedIOException) {
                throw (InterruptedIOException) ex;
              }
              if (ex instanceof IOException) {
                throw (IOException) ex;
              }
              for (int iTarget = 0; iTarget < Processes; iTarget++) {
                VirtualMachine vm = runnable.getVirtualMachine();

                if (vm != null) {
                  createDebugTarget(config, launch, port, process, vm);

                }
              }
              subMonitor.worked(1);
              subMonitor.done();
              return;
              // continue;
            } catch (InterruptedIOException e) {
              checkErrorMessage(process);

              // timeout, consult status handler if there is one
              IStatus status = new Status(IStatus.ERROR,
                  LaunchingPlugin.getUniqueIdentifier(),
                  IJavaLaunchConfigurationConstants.ERR_VM_CONNECT_TIMEOUT,
                  "", e); //$NON-NLS-1$
              IStatusHandler handler = DebugPlugin.getDefault()
                  .getStatusHandler(status);

              retry = false;
              if (handler == null) {
                // if there is no handler, throw the exception
                throw new CoreException(status);
              }
              Object result = handler.handleStatus(status, this);
              if (result instanceof Boolean) {
                retry = ((Boolean) result).booleanValue();
              }
            }
          } while (retry);
        }// end of for iProcess

      } finally {
        // connector.stopListening(map);
      }
    } catch (IOException e) {
      abort(LaunchingMessages.StandardVMDebugger_Couldn__t_connect_to_VM_4, e,
          IJavaLaunchConfigurationConstants.ERR_CONNECTION_FAILED);
    } catch (IllegalConnectorArgumentsException e) {
      abort(LaunchingMessages.StandardVMDebugger_Couldn__t_connect_to_VM_5, e,
          IJavaLaunchConfigurationConstants.ERR_CONNECTION_FAILED);
    }
    if (p != null) {
      p.destroy();
    }

  }

  /*
   * private void printMap(Map map) { Set set = map.entrySet(); Iterator
   * iterator = set.iterator();
   * 
   * while(iterator.hasNext()) { Map.Entry entry = (Map.Entry)iterator.next();
   * System.out.println(entry.getKey()+"="+entry.getValue()); }
   * 
   * }
   */

  private void print(String[] cmdLine) {
    // TODO Auto-generated method stub
    System.out.println();
    for (int i = 0; i < cmdLine.length; i++)
      System.out.println(cmdLine[i]);
    System.out.println();
  }

  /**
   * This method performs platform specific operations to modify the runtime
   * path for JREs prior to launching. Nothing is written back to the original
   * system path.
   * 
   * <p>
   * For Windows: Prepends the location of the JRE bin directory for the given
   * JDK path to the PATH variable in Windows. This method assumes that the JRE
   * is located within the JDK install directory in:
   * <code><JDK install dir>/jre/bin/</code> where the JRE itself would be
   * located in: <code><JDK install dir>/bin/</code> where the JDK itself is
   * located
   * </p>
   * <p>
   * For Mac OS: Searches for and sets the correct state of the JAVA_VM_VERSION
   * environment variable to ensure it matches the currently chosen VM of the
   * launch config
   * </p>
   * 
   * @param env
   *          the current array of environment variables to run with
   * @param jdkpath
   *          the path to the executable (javaw).
   * @since 3.3
   */
  @SuppressWarnings("restriction")
  protected String[] prependJREPath(String[] env, IPath jdkpath) {
    if (Platform.OS_WIN32.equals(Platform.getOS())) {
      IPath jrepath = jdkpath.removeLastSegments(1);
      if (jrepath.lastSegment().equals(BIN)) {
        if (!jrepath.segment(jrepath.segmentCount() - 2).equals(JRE)) {
          jrepath = jrepath.removeLastSegments(1).append(JRE).append(BIN);
        }
      } else {
        jrepath = jrepath.append(JRE).append(BIN);
      }
      if (jrepath.toFile().exists()) {
        String jrestr = jrepath.toOSString();
        if (env == null) {
          Map map = DebugPlugin.getDefault().getLaunchManager()
              .getNativeEnvironment();
          env = new String[map.size()];
          String var = null;
          int index = 0;
          for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            var = (String) iter.next();
            String value = (String) map.get(var);
            if (value == null) {
              value = ""; //$NON-NLS-1$
            }
            if (var.equalsIgnoreCase("path")) { //$NON-NLS-1$
              if (value.indexOf(jrestr) == -1) {
                value = jrestr + ';' + value;
              }
            }
            env[index] = var + "=" + value; //$NON-NLS-1$
            index++;
          }
        } else {
          String var = null;
          int esign = -1;
          for (int i = 0; i < env.length; i++) {
            esign = env[i].indexOf('=');
            if (esign > -1) {
              var = env[i].substring(0, esign);
              if (var != null && var.equalsIgnoreCase("path")) { //$NON-NLS-1$
                if (env[i].indexOf(jrestr) == -1) {
                  env[i] = var
                      + "=" + jrestr + ';' + (esign == env.length ? "" : env[i].substring(esign + 1)); //$NON-NLS-1$ //$NON-NLS-2$
                  break;
                }
              }
            }
          }
        }
      }
    }
    return super.prependJREPath(env);
  }

  /**
   * Creates a new debug target for the given virtual machine and system process
   * that is connected on the specified port for the given launch.
   * 
   * @param config
   *          run configuration used to launch the VM
   * @param launch
   *          launch to add the target to
   * @param port
   *          port the VM is connected to
   * @param process
   *          associated system process
   * @param vm
   *          JDI virtual machine
   */
  @SuppressWarnings("restriction")
  protected IDebugTarget createDebugTarget(VMRunnerConfiguration config,
      ILaunch launch, int port, IProcess process, VirtualMachine vm) {
    return JDIDebugModel.newDebugTarget(launch, vm,
        renderDebugTarget(config.getClassToLaunch(), port), process, true,
        false, config.isResumeOnStartup());
  }

  /**
   * Returns the version of the current VM in use
   * 
   * @return the VM version
   */
  @SuppressWarnings("restriction")
  private double getJavaVersion() {
    String version = null;
    if (fVMInstance instanceof IVMInstall2) {
      version = ((IVMInstall2) fVMInstance).getJavaVersion();
    } else {
      LibraryInfo libInfo = LaunchingPlugin.getLibraryInfo(fVMInstance
          .getInstallLocation().getAbsolutePath());
      if (libInfo == null) {
        return 0D;
      }
      version = libInfo.getVersion();
    }
    if (version == null) {
      // unknown version
      return 0D;
    }
    int index = version.indexOf("."); //$NON-NLS-1$
    int nextIndex = version.indexOf(".", index + 1); //$NON-NLS-1$
    try {
      if (index > 0 && nextIndex > index) {
        return Double.parseDouble(version.substring(0, nextIndex));
      }
      return Double.parseDouble(version);
    } catch (NumberFormatException e) {
      return 0D;
    }

  }

  /**
   * Checks and forwards an error from the specified process
   * 
   * @param process
   * @throws CoreException
   */
  protected void checkErrorMessage(IProcess process) throws CoreException {
    IStreamsProxy streamsProxy = process.getStreamsProxy();
    if (streamsProxy != null) {
      String errorMessage = streamsProxy.getErrorStreamMonitor().getContents();
      if (errorMessage.length() == 0) {
        errorMessage = streamsProxy.getOutputStreamMonitor().getContents();
      }
      if (errorMessage.length() != 0) {
        abort(errorMessage, null,
            IJavaLaunchConfigurationConstants.ERR_VM_LAUNCH_ERROR);
      }
    }
  }

  /**
   * Allows arguments to be specified
   * 
   * @param map
   * @param portNumber
   */
  protected void specifyArguments(Map map, int portNumber) {
    // XXX: Revisit - allows us to put a quote (") around the classpath
    Connector.IntegerArgument port = (Connector.IntegerArgument) map
        .get("port"); //$NON-NLS-1$
    port.setValue(portNumber);
    Connector.IntegerArgument timeoutArg = (Connector.IntegerArgument) map
        .get("timeout"); //$NON-NLS-1$
    if (timeoutArg != null) {
      int timeout = JavaRuntime.getPreferences().getInt(
          JavaRuntime.PREF_CONNECT_TIMEOUT);
      timeoutArg.setValue(timeout);
    }
  }

  /**
   * Returns the default 'com.sun.jdi.SocketListen' connector
   * 
   * @return
   */
  protected AttachingConnector getConnector() {
    // List connectors= Bootstrap.virtualMachineManager().listeningConnectors();
    List connectors = Bootstrap.virtualMachineManager().attachingConnectors();
    for (int i = 0; i < connectors.size(); i++) {
      AttachingConnector c = (AttachingConnector) connectors.get(i);
      // System.out.println("Connector Name : "+i+" :"+c.name());
      if ("com.sun.jdi.SocketAttach".equals(c.name())) //$NON-NLS-1$
        return c;
      // com.sun.jdi.SocketListen
    }
    return null;
  }

  public String getLocalIp() {
    String hostName = "";
    try {
      hostName = InetAddress.getLocalHost().getHostName();
      InetAddress addrs[] = InetAddress.getAllByName(hostName);
      for (InetAddress address : addrs) {
        if (!address.isLoopbackAddress()) {
          return address.getCanonicalHostName();
        }

      }
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return hostName;
  }
}
