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


 * File         : JavaRemoteParallelApplicationLaunchConfigurationDelegate.java 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */
import com.ibm.icu.text.MessageFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * Launch configuration delegate for a remote Java application.
 */
public class JavaRemoteParallelApplicationLaunchConfigurationDelegate extends
    AbstractJavaLaunchConfigurationDelegate {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse
   * .debug.core.ILaunchConfiguration, java.lang.String,
   * org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
   */
  public static Map argMap;

  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {

    if (monitor == null) {
      monitor = new NullProgressMonitor();
    }

    monitor
        .beginTask(
            MessageFormat
                .format(
                    LaunchingMessages.JavaRemoteApplicationLaunchConfigurationDelegate_Attaching_to__0_____1,
                    new String[] { configuration.getName() }), 3);
    // check for cancellation
    if (monitor.isCanceled()) {
      return;
    }
    try {
      monitor
          .subTask(LaunchingMessages.JavaRemoteApplicationLaunchConfigurationDelegate_Verifying_launch_attributes____1);

      String connectorId = "org.eclipse.jdt.launching.socketAttachConnector";// getVMConnectorId(configuration);
      IVMConnector connector = null;
      // if (connectorId == null) {
      // connector = JavaRuntime.getDefaultVMConnector();
      // } else {
      connector = JavaRuntime.getVMConnector(connectorId);
      // }
      if (connector == null) {
        abort(
            LaunchingMessages.JavaRemoteApplicationLaunchConfigurationDelegate_Connector_not_specified_2,
            null, IJavaLaunchConfigurationConstants.ERR_CONNECTOR_NOT_AVAILABLE);
      }

      int connectTimeout = JavaRuntime.getPreferences().getInt(
          JavaRuntime.PREF_CONNECT_TIMEOUT);
      argMap.put("timeout", Integer.toString(connectTimeout)); //$NON-NLS-1$

      // check for cancellation
      if (monitor.isCanceled()) {
        return;
      }

      monitor.worked(1);

      monitor
          .subTask(LaunchingMessages.JavaRemoteApplicationLaunchConfigurationDelegate_Creating_source_locator____2);
      // set the default source locator if required
      setDefaultSourceLocator(launch, configuration);
      monitor.worked(1);
      // printVals(argMap);
      // connect to remote VM
      connector.connect(argMap, monitor, launch);

      // check for cancellation
      if (monitor.isCanceled()) {
        IDebugTarget[] debugTargets = launch.getDebugTargets();
        for (int i = 0; i < debugTargets.length; i++) {
          IDebugTarget target = debugTargets[i];
          if (target.canDisconnect()) {
            target.disconnect();
          }
        }
        return;
      }
    } finally {
      monitor.done();
    }
  }

  public void println(Map argMap) {
    Set keys = argMap.keySet();
  }

  public void setPort(String host, String port) {
    argMap.put("port", port);
    argMap.put("hostname", host);

    return;
  }

  private void printVals(Map envp) {
    if (envp != null) {
      Set keys = envp.keySet();
      Iterator iter = keys.iterator();
      String key_iter;// = iter.next().toString();
      for (; iter.hasNext();) {
        key_iter = iter.next().toString();
        System.out.println("Key " + key_iter + "  is : " + envp.get(key_iter));

      }
    }
  }
}
