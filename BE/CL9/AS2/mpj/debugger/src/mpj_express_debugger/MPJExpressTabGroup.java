/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.

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


 * File         : MPJExpressTabGroup.java 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */
package mpj_express_debugger;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class MPJExpressTabGroup extends AbstractLaunchConfigurationTabGroup {

  /**
   * Constructs a new Java applet tab group.
   */
  public MPJExpressTabGroup() {
  }

  /**
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog,
   *      java.lang.String)
   */
  public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
    if (mode.equals("run"))
      setTabs(runMode());
    else
      setTabs(debugMode());
  }

  public ILaunchConfigurationTab[] runMode() {
    ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
        new JavaMainTab(), new MPJExpressParameterTab(),
        new JavaArgumentsTab(), new JavaJRETab(), new JavaClasspathTab(),
        new SourceLookupTab(), new EnvironmentTab(), new CommonTab() };
    return tabs;
  }

  public ILaunchConfigurationTab[] debugMode() {
    ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
        new JavaMainTab(), new MPJExpressParameterTab(),
        new JavaArgumentsTab(), new JavaJRETab(), new JavaClasspathTab(),
        new SourceLookupTab(), new EnvironmentTab(), new CommonTab() };
    return tabs;
  }

}
