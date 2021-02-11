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
//package org.eclipse.jdt.debug.ui.launchConfigurations;

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


 * File         : JavaJRETab.java 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar, Mohsan Jameel, Aamir Shafi
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.internal.debug.ui.jres.JREDescriptor;
import org.eclipse.jdt.internal.debug.ui.jres.JREsComboBlock;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.ibm.icu.text.MessageFormat;

/**
 * A launch configuration tab that displays and edits the VM install launch
 * configuration attributes.
 * <p>
 * This class may be instantiated.
 * </p>
 * 
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */

public class JavaJRETab extends JavaLaunchTab {

  // JRE Block
  protected JREsComboBlock fJREBlock;

  // Dynamic JRE UI widgets
  protected ILaunchConfigurationTab fDynamicTab;
  protected Composite fDynamicTabHolder;
  protected boolean fUseDynamicArea = true;

  protected ILaunchConfigurationWorkingCopy fWorkingCopy;
  protected ILaunchConfiguration fLaunchConfiguration;

  // State
  protected boolean fIsInitializing = false;

  // Selection changed listener (checked JRE)
  private IPropertyChangeListener fCheckListener = new IPropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent event) {
      handleSelectedJREChanged();
    }
  };

  // Constants
  protected static final String EMPTY_STRING = ""; //$NON-NLS-1$

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#dispose()
   */
  public void dispose() {
    super.dispose();
    if (fJREBlock != null) {
      fJREBlock.removePropertyChangeListener(fCheckListener);
    }
  }

  /**
   * @see ILaunchConfigurationTab#createControl(Composite)
   */
  public void createControl(Composite parent) {
    Font font = parent.getFont();
    Composite topComp = SWTFactory.createComposite(parent, font, 1, 1,
        GridData.FILL_HORIZONTAL, 0, 0);

    fJREBlock = new JREsComboBlock(fIsInitializing);
    fJREBlock.setDefaultJREDescriptor(getDefaultJREDescriptor());
    fJREBlock.setSpecificJREDescriptor(getSpecificJREDescriptor());
    fJREBlock.createControl(topComp);
    Control control = fJREBlock.getControl();
    fJREBlock.addPropertyChangeListener(fCheckListener);
    control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    setDynamicTabHolder(SWTFactory.createComposite(topComp, font, 1, 1,
        GridData.FILL_BOTH, 0, 0));
    setControl(topComp);
    PlatformUI
        .getWorkbench()
        .getHelpSystem()
        .setHelp(getControl(),
            IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_JRE_TAB);
  }

  protected void setDynamicTabHolder(Composite tabHolder) {
    this.fDynamicTabHolder = tabHolder;
  }

  protected Composite getDynamicTabHolder() {
    return fDynamicTabHolder;
  }

  protected void setDynamicTab(ILaunchConfigurationTab tab) {
    fDynamicTab = tab;
  }

  protected ILaunchConfigurationTab getDynamicTab() {
    return fDynamicTab;
  }

  /**
   * @see ILaunchConfigurationTab#setDefaults(ILaunchConfigurationWorkingCopy)
   */
  public void setDefaults(ILaunchConfigurationWorkingCopy config) {
    setLaunchConfigurationWorkingCopy(config);
    ILaunchConfigurationTab dynamicTab = getDynamicTab();
    if (dynamicTab != null) {
      dynamicTab.setDefaults(config);
    }
  }

  /**
   * @see ILaunchConfigurationTab#initializeFrom(ILaunchConfiguration)
   */
  public void initializeFrom(ILaunchConfiguration configuration) {
    fIsInitializing = true;
    getControl().setRedraw(false);
    setLaunchConfiguration(configuration);
    updateJREFromConfig(configuration);
    fJREBlock.setDefaultJREDescriptor(getDefaultJREDescriptor());
    ILaunchConfigurationTab dynamicTab = getDynamicTab();
    if (dynamicTab != null) {
      dynamicTab.initializeFrom(configuration);
    }
    getControl().setRedraw(true);
    fIsInitializing = false;
  }

  /**
   * @see ILaunchConfigurationTab#performApply(ILaunchConfigurationWorkingCopy)
   */
  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    if (fJREBlock.isDefaultJRE()) {
      configuration.setAttribute(
          IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,
          (String) null);
    } else {
      IPath containerPath = fJREBlock.getPath();
      String portablePath = null;
      if (containerPath != null) {
        portablePath = containerPath.toPortableString();
      }
      configuration.setAttribute(
          IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,
          portablePath);
    }
    // erase old attributes in case the user changed from 'specific JRE' to
    // 'default' - see bug 152446
    configuration.setAttribute(
        IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, (String) null);
    configuration.setAttribute(
        IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, (String) null);

    // Handle any attributes in the VM-specific area
    ILaunchConfigurationTab dynamicTab = getDynamicTab();
    if (dynamicTab == null) {
      configuration
          .setAttribute(
              IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP,
              (Map) null);
    } else {
      dynamicTab.performApply(configuration);
    }
  }

  /**
   * @see ILaunchConfigurationTab#isValid(ILaunchConfiguration)
   */
  public boolean isValid(ILaunchConfiguration config) {

    setErrorMessage(null);
    setMessage(null);

    IStatus status = fJREBlock.getStatus();
    if (!status.isOK()) {
      setErrorMessage(status.getMessage());
      return false;
    }
    if (!isExternalToolConfiguration(fLaunchConfiguration)) {
      status = checkCompliance();
      if (!status.isOK()) {
        setErrorMessage(status.getMessage());
        return false;
      }
    }

    ILaunchConfigurationTab dynamicTab = getDynamicTab();
    if (dynamicTab != null) {
      return dynamicTab.isValid(config);
    }
    return true;
  }

  /**
   * Returns if the specified
   * <code>ILaunchConfiguration is an ant or external tool
   * type.
   * 
   * @param configuration
   * @return true if the specified <code>ILaunchConfiguration is an ant or
   *         external tools type configuration
   * 
   * @since 3.4
   */
  private boolean isExternalToolConfiguration(ILaunchConfiguration configuration) {
    try {
      ILaunchConfigurationType type = configuration.getType();
      String id = type.getIdentifier();
      return id != null
          && (id.equals("org.eclipse.ant.AntLaunchConfigurationType") || //$NON-NLS-1$
              id.equals("org.eclipse.ant.AntBuilderLaunchConfigurationType") || //$NON-NLS-1$
              id.equals("org.eclipse.ui.externaltools.ProgramLaunchConfigurationType") || //$NON-NLS-1$
          id.equals("org.eclipse.ui.externaltools.ProgramBuilderLaunchConfigurationType")); //$NON-NLS-1$
    } catch (CoreException e) {
      return false;
    }
  }

  /**
   * Checks to make sure the class file compliance level and the selected VM are
   * compatible i.e. such that the selected JRE can run the currently compiled
   * code
   * 
   * @since 3.3
   */
  private IStatus checkCompliance() {
    IJavaProject javaProject = getJavaProject();
    if (javaProject == null) {
      return Status.OK_STATUS;
    }
    String source = LauncherMessages.JavaJRETab_3;
    String compliance = javaProject.getOption(
        JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, false);
    if (compliance == null) {
      compliance = javaProject.getOption(
          JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, true);
      source = LauncherMessages.JavaJRETab_4;
    }
    IPath vmPath = fJREBlock.getPath();
    IVMInstall vm = null;
    if (JavaRuntime.newDefaultJREContainerPath().equals(vmPath)) {
      if (javaProject.isOpen()) {
        try {
          vm = JavaRuntime.getVMInstall(getJavaProject());
        } catch (CoreException e) {
          JDIDebugUIPlugin.log(e);
          return Status.OK_STATUS;
        }
        if (vm == null) {
          vm = JavaRuntime.getVMInstall(vmPath);
        }
      }
    } else {
      vm = JavaRuntime.getVMInstall(vmPath);
    }
    String environmentId = JavaRuntime.getExecutionEnvironmentId(vmPath);
    if (vm instanceof AbstractVMInstall) {
      AbstractVMInstall install = (AbstractVMInstall) vm;
      String vmver = install.getJavaVersion();
      if (vmver != null) {
        int val = compliance.compareTo(vmver);
        if (val > 0) {
          String setting = null;
          if (environmentId == null) {
            setting = LauncherMessages.JavaJRETab_2;
          } else {
            setting = LauncherMessages.JavaJRETab_1;
          }
          return new Status(IStatus.ERROR, IJavaDebugUIConstants.PLUGIN_ID,
              IStatus.ERROR, MessageFormat.format(
                  LauncherMessages.JavaJRETab_0, new String[] { setting,
                      source, compliance }), null);
        }
      }
    }
    return Status.OK_STATUS;
  }

  /**
   * @see ILaunchConfigurationTab#getName()
   */
  public String getName() {
    return LauncherMessages.JavaJRETab__JRE_1;
  }

  /**
   * @see ILaunchConfigurationTab#getImage()
   */
  public Image getImage() {
    return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_LIBRARY);
  }

  /**
   * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getId()
   * 
   * @since 3.3
   */
  public String getId() {
    return "org.eclipse.jdt.debug.ui.javaJRETab"; //$NON-NLS-1$
  }

  /**
   * This method updates the jre selection from the <code>ILaunchConfiguration
   * 
   * @param config
   *          the config to update from
   */
  protected void updateJREFromConfig(ILaunchConfiguration config) {
    try {
      String path = config.getAttribute(
          IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,
          (String) null);
      if (path != null) {
        fJREBlock.setPath(Path.fromPortableString(path));
        return;
      }
    } catch (CoreException e) {
      JDIDebugUIPlugin.log(e);
    }
    String vmName = null;
    String vmTypeID = null;
    try {
      vmTypeID = config
          .getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE,
              (String) null);
      vmName = config
          .getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
              (String) null);
    } catch (CoreException ce) {
      JDIDebugUIPlugin.log(ce);
    }
    selectJRE(vmTypeID, vmName);
  }

  /**
   * Notification that the user changed the selection in the JRE combination
   * box.
   */
  protected void handleSelectedJREChanged() {
    loadDynamicJREArea();

    // always set the newly created area with defaults
    ILaunchConfigurationWorkingCopy wc = getLaunchConfigurationWorkingCopy();
    if (getDynamicTab() == null) {
      // remove any VM specific arguments from the config
      if (wc == null) {
        if (getLaunchConfiguration().isWorkingCopy()) {
          wc = (ILaunchConfigurationWorkingCopy) getLaunchConfiguration();
        }
      }
      if (!fIsInitializing) {
        if (wc != null) {
          wc.setAttribute(
              IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP,
              (Map) null);
        }
      }
    } else {
      if (wc == null) {
        try {
          ILaunchConfiguration config = getLaunchConfiguration();
          if (config.isWorkingCopy()) {
            // get a fresh copy to work on, only if there is an
            // original to do so
            wc = (ILaunchConfigurationWorkingCopy) config;
            if (wc.getOriginal() != null) {
              wc.getOriginal().getWorkingCopy();
            }
          } else {
            wc = getLaunchConfiguration().getWorkingCopy();
          }
        } catch (CoreException e) {
          IStatus status = e.getStatus();
          JDIDebugUIPlugin.statusDialog(status);
          JDIDebugUIPlugin.log(status);
          return;
        }
      }
      if (!fIsInitializing) {
        getDynamicTab().setDefaults(wc);
        getDynamicTab().initializeFrom(wc);
      }
    }

    updateLaunchConfigurationDialog();
  }

  protected void selectJRE(String typeID, String vmName) {
    if (typeID == null) {
      fJREBlock.setPath(JavaRuntime.newDefaultJREContainerPath());
    } else {
      fJREBlock.setPath(JavaRuntime.newJREContainerPath(typeID, vmName));
    }
  }

  /**
   * Return the class that implements <code>ILaunchConfigurationTab
   * that is registered against the install type of the currently selected VM.
   */
  protected ILaunchConfigurationTab getTabForCurrentJRE() {
    if (!fJREBlock.isDefaultJRE()) {
      IPath path = fJREBlock.getPath();
      System.out.println("Selected JRE : " + path);
      if (path != null && JavaRuntime.getExecutionEnvironmentId(path) == null) {
        IVMInstall vm = fJREBlock.getJRE();
        if (vm != null) {
          String vmInstallTypeID = vm.getVMInstallType().getId();
          return JDIDebugUIPlugin.getDefault().getVMInstallTypePage(
              vmInstallTypeID);
        }
      }
    }
    return null;
  }

  /**
   * Show the contributed piece of UI that was registered for the install type
   * of the currently selected VM.
   */
  protected void loadDynamicJREArea() {

    // Dispose of any current child widgets in the tab holder area
    Control[] children = getDynamicTabHolder().getChildren();
    for (int i = 0; i < children.length; i++) {
      children[i].dispose();
    }

    if (isUseDynamicJREArea()) {
      // Retrieve the dynamic UI for the current JRE
      setDynamicTab(getTabForCurrentJRE());
      if (getDynamicTab() == null) {
        return;
      }

      // Ask the dynamic UI to create its Control
      getDynamicTab().setLaunchConfigurationDialog(
          getLaunchConfigurationDialog());
      getDynamicTab().createControl(getDynamicTabHolder());
      getDynamicTabHolder().layout();
    }

  }

  protected ILaunchConfigurationWorkingCopy getLaunchConfigurationWorkingCopy() {
    return fWorkingCopy;
  }

  /**
   * Overridden here so that any error message in the dynamic UI gets returned.
   * 
   * @see ILaunchConfigurationTab#getErrorMessage()
   */
  public String getErrorMessage() {
    ILaunchConfigurationTab tab = getDynamicTab();
    if ((super.getErrorMessage() != null) || (tab == null)) {
      return super.getErrorMessage();
    }
    return tab.getErrorMessage();
  }

  protected void setLaunchConfigurationWorkingCopy(
      ILaunchConfigurationWorkingCopy workingCopy) {
    fWorkingCopy = workingCopy;
  }

  protected ILaunchConfiguration getLaunchConfiguration() {
    return fLaunchConfiguration;
  }

  protected void setLaunchConfiguration(ILaunchConfiguration launchConfiguration) {
    fLaunchConfiguration = launchConfiguration;
  }

  /**
   * Sets whether this tab will display the VM specific arguments area if a JRE
   * supports VM specific arguments.
   * 
   * @param visible
   *          whether this tab will display the VM specific arguments area if a
   *          JRE supports VM specific arguments
   */
  public void setVMSpecificArgumentsVisible(boolean visible) {
    fUseDynamicArea = visible;
  }

  protected boolean isUseDynamicJREArea() {
    return fUseDynamicArea;
  }

  protected JREDescriptor getDefaultJREDescriptor() {
    return new JREDescriptor() {

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.jdt.internal.debug.ui.jres.DefaultJREDescriptor#
       * getDescription()
       */
      public String getDescription() {
        IJavaProject project = getJavaProject();
        String name = LauncherMessages.JavaJRETab_7;
        if (project == null) {
          IVMInstall vm = JavaRuntime.getDefaultVMInstall();
          if (vm != null) {
            name = vm.getName();
          }
          return MessageFormat.format(LauncherMessages.JavaJRETab_8,
              new String[] { name });
        }
        try {
          IVMInstall vm = JavaRuntime.getVMInstall(project);
          if (vm != null) {
            name = vm.getName();
          }
        } catch (CoreException e) {
        }
        return MessageFormat.format(LauncherMessages.JavaJRETab_9,
            new String[] { name });
      }
    };
  }

  protected JREDescriptor getSpecificJREDescriptor() {
    return null;
  }

  /**
   * Returns the Java project associated with the current config being edited,
   * or <code>null if none.
   * 
   * @return java project or <code>null
   */
  protected IJavaProject getJavaProject() {
    if (getLaunchConfiguration() != null) {
      try {
        String name = getLaunchConfiguration().getAttribute(
            IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
        if (name != null && name.length() > 0) {
          IProject project = ResourcesPlugin.getWorkspace().getRoot()
              .getProject(name);
          if (project.exists()) {
            return JavaCore.create(project);
          }
        }
      } catch (CoreException e) {
        JDIDebugUIPlugin.log(e);
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.debug.ui.ILaunchConfigurationTab#activated(org.eclipse.debug
   * .core.ILaunchConfigurationWorkingCopy)
   */
  public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
    // update the default JRE description, in case it has changed
    // based on the selected project
    fJREBlock.refresh();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#deactivated(org.eclipse.
   * debug.core.ILaunchConfigurationWorkingCopy)
   */
  public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
    // do nothing when deactivated
  }
}
