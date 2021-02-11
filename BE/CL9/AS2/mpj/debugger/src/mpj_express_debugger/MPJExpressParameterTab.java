package mpj_express_debugger;

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


 * File         : MPJExpressParameterTab.java 
 * Author       : Amjad Aziz, Rizwan Hanif, Aleem Akhtar
 * Created      : December 30, 2013 6:00:57 PM 2013
 * Revision     : $
 * Updated      : $
 *

 */
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JavaDebugImages;
import org.eclipse.jdt.internal.debug.ui.launcher.NameValuePairDialog;
import org.eclipse.swt.events.MouseAdapter;

@SuppressWarnings("restriction")
public class MPJExpressParameterTab extends JavaLaunchTab {

  private Text npText;
  private Text devText;
  private Text mpjHomeText;
  private Button fParametersAddButton;
  private Button fParametersRemoveButton;
  private Button fParametersEditButton;
  private Button fBrowseMPJHomeButton;
  /**
   * The default value for the 'np' attribute.
   */
  public static final int DEFAULT_MPJ_NP = 2;

  /**
   * The default value for the 'dev' attribute.
   */
  public static final String DEFAULT_MPJ_DEV = "multicore";
  /**
   * The parameters table viewer
   */
  private TableViewer fViewer;

  /**
   * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(Composite)
   */
  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
  // ////////////////////////////////////////////////

  private MPJExpressTabListener fListener = new MPJExpressTabListener();

  // ///////////////////////////////////////////////////////////////////////////////
  @Override
  public void createControl(Composite parent) {
    Composite comp = SWTFactory.createComposite(parent, 1, 1,
        GridData.FILL_HORIZONTAL);
    setControl(comp);
    PlatformUI
        .getWorkbench()
        .getHelpSystem()
        .setHelp(
            getControl(),
            IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_APPLET_PARAMETERS_TAB);

    @SuppressWarnings("restriction")
    Composite namecomp = SWTFactory.createComposite(comp, comp.getFont(), 5, 1,
        GridData.FILL_HORIZONTAL, 0, 0);

    SWTFactory.createLabel(namecomp,
        MPJLauncherMessages.mpjlauncher_argumenttab_nplabel_text, 1);

    npText = SWTFactory.createSingleText(namecomp, 1);
    npText.addModifyListener(fListener);

    SWTFactory.createLabel(namecomp,
        MPJLauncherMessages.mpjlauncher_argumenttab_homelabel_text, 1);

    mpjHomeText = SWTFactory.createSingleText(namecomp, 1);
    mpjHomeText.addModifyListener(fListener);
    fBrowseMPJHomeButton = SWTFactory
        .createPushButton(namecomp, "Browse", null);
    fBrowseMPJHomeButton.addSelectionListener(fListener);
    SWTFactory.createLabel(namecomp,
        MPJLauncherMessages.mpjlauncher_argumenttab_devlabel_text, 1);

    devText = SWTFactory.createSingleText(namecomp, 1);
    devText.addModifyListener(fListener);

    Label blank = new Label(namecomp, SWT.NONE);
    blank.setText(EMPTY_STRING);
    Label hint = SWTFactory.createLabel(namecomp,
        MPJLauncherMessages.MPJParametersTab__mandatory_mpj_home_path, 1);
    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    hint.setLayoutData(gd);

    SWTFactory.createVerticalSpacer(comp, 1);

    Composite paramcomp = SWTFactory.createComposite(comp, comp.getFont(), 2,
        1, GridData.FILL_BOTH, 0, 0);

    SWTFactory.createLabel(paramcomp,
        MPJLauncherMessages.appletlauncher_argumenttab_parameterslabel_text, 2);

    Table ptable = new Table(paramcomp, SWT.FULL_SELECTION | SWT.BORDER);
    fViewer = new TableViewer(ptable);
    gd = new GridData(GridData.FILL_BOTH);
    ptable.setLayoutData(gd);
    TableColumn column1 = new TableColumn(ptable, SWT.NONE);
    column1
        .setText(MPJLauncherMessages.appletlauncher_argumenttab_parameterscolumn_name_text);
    TableColumn column2 = new TableColumn(ptable, SWT.NONE);
    column2
        .setText(MPJLauncherMessages.appletlauncher_argumenttab_parameterscolumn_value_text);
    TableLayout tableLayout = new TableLayout();
    ptable.setLayout(tableLayout);
    tableLayout.addColumnData(new ColumnWeightData(100));
    tableLayout.addColumnData(new ColumnWeightData(100));
    ptable.setHeaderVisible(true);
    ptable.setLinesVisible(true);
    ptable.addSelectionListener(fListener);
    ptable.addMouseListener(new MouseAdapter() {
      public void mouseDoubleClick(MouseEvent e) {
        setParametersButtonsEnableState();
        if (fParametersEditButton.isEnabled()) {
          handleParametersEditButtonSelected();
        }
      }
    });

    fViewer.setContentProvider(new IStructuredContentProvider() {
      public Object[] getElements(Object inputElement) {
        Map params = (Map) inputElement;
        return params.keySet().toArray();
      }

      public void dispose() {
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }
    });

    fViewer.setLabelProvider(new ITableLabelProvider() {
      public Image getColumnImage(Object element, int columnIndex) {
        return null;
      }

      public String getColumnText(Object element, int columnIndex) {
        if (columnIndex == 0) {
          return element.toString();
        }

        String key = (String) element;
        Map params = (Map) fViewer.getInput();
        Object object = params.get(key);
        if (object != null)
          return object.toString();
        return null;
      }

      public void addListener(ILabelProviderListener listener) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object element, String property) {
        return false;
      }

      public void removeListener(ILabelProviderListener listener) {
      }
    });

    fViewer.setComparator(new ViewerComparator());

    Composite envcomp = SWTFactory.createComposite(paramcomp,
        paramcomp.getFont(), 1, 1, GridData.VERTICAL_ALIGN_BEGINNING
            | GridData.HORIZONTAL_ALIGN_FILL, 0, 0);

    fParametersAddButton = createPushButton(
        envcomp,
        MPJLauncherMessages.appletlauncher_argumenttab_parameters_button_add_text,
        null);
    fParametersAddButton.addSelectionListener(fListener);

    fParametersEditButton = createPushButton(
        envcomp,
        MPJLauncherMessages.appletlauncher_argumenttab_parameters_button_edit_text,
        null);
    fParametersEditButton.addSelectionListener(fListener);

    fParametersRemoveButton = createPushButton(
        envcomp,
        MPJLauncherMessages.appletlauncher_argumenttab_parameters_button_remove_text,
        null);
    fParametersRemoveButton.addSelectionListener(fListener);

    setParametersButtonsEnableState();
    Dialog.applyDialogFont(parent);
  }

  @Override
  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

  }

  @Override
  public void initializeFrom(ILaunchConfiguration config) {
    try {
      npText.setText(Integer.toString(config.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_MPJ_NP, DEFAULT_MPJ_NP)));
    } catch (CoreException ce) {
      npText.setText(Integer.toString(DEFAULT_MPJ_NP));
    }
    try {
      devText.setText(config.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_MPJ_DEV, DEFAULT_MPJ_DEV));
    } catch (CoreException ce) {
      devText.setText(DEFAULT_MPJ_DEV);
    }
    try {
      String mpjhome = System.getenv("MPJ_HOME");
      if (mpjhome != null)
        mpjHomeText.setText(config.getAttribute(
            IMPJLaunchConfigurationConstants.ATTR_MPJ_HOME, mpjhome));
      else
        mpjHomeText.setText(config.getAttribute(
            IMPJLaunchConfigurationConstants.ATTR_MPJ_HOME, EMPTY_STRING));

    } catch (CoreException ce) {
      mpjHomeText.setText(EMPTY_STRING);
    }

    Map input = new HashMap();
    try {
      Map params = config.getAttribute(
          IMPJLaunchConfigurationConstants.ATTR_MPJ_PARAMETERS, (Map) null);
      if (params != null)
        input.putAll(params);
    } catch (CoreException e) {
    }

    fViewer.setInput(input);
  }

  @Override
  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    try {
      configuration.setAttribute(IMPJLaunchConfigurationConstants.ATTR_MPJ_NP,
          Integer.parseInt(getNpText()));
    } catch (NumberFormatException e) {
    }

    configuration.setAttribute(IMPJLaunchConfigurationConstants.ATTR_MPJ_DEV,
        getDevText());
    configuration.setAttribute(IMPJLaunchConfigurationConstants.ATTR_MPJ_HOME,
        getMpjHomeText());
    configuration.setAttribute(
        IMPJLaunchConfigurationConstants.ATTR_MPJ_PARAMETERS,
        (Map) fViewer.getInput());
  }

  @Override
  public boolean isValid(ILaunchConfiguration launchConfig) {
    setErrorMessage(null);
    try {
      if (Integer.parseInt(getNpText()) < 1) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException nfe) {
      setErrorMessage(MPJLauncherMessages.mpjlauncher_argumenttab_np_error_notaninteger);
      return false;
    }
    try {
      if (getDevText().length() == 0)
        throw new NullPointerException();
    } catch (NullPointerException npe) {
      setErrorMessage(MPJLauncherMessages.mpjlauncher_argumenttab_dev_error_notaninteger);
      return false;
    }
    try {
      if (getMpjHomeText().length() == 0)
        throw new NullPointerException();
    } catch (NullPointerException npe) {
      setErrorMessage(MPJLauncherMessages.mpjlauncher_argumenttab_mpj_home_error_notaninteger);
      return false;
    }
    return true;
  }

  private String getMpjHomeText() {
    return mpjHomeText.getText().trim();
  }

  @Override
  public String getName() {
    return MPJLauncherMessages.mpjlauncher_argumenttab_name;

  }

  public String getId() {
    return "seecs.nust.debug.parallel.MPJExpressParameterTab"; //$NON-NLS-1$
  }

  @Override
  public Image getImage() {

    return JavaDebugImages.get(JavaDebugImages.IMG_VIEW_ARGUMENTS_TAB);
  }

  @Override
  public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
  }

  @Override
  public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {

  }

  /**
   * Returns the NP specified by the user
   * 
   * @return the NP specified by the user
   */
  private String getNpText() {
    return npText.getText().trim();
  }

  /**
   * Returns the DEV specified by the user
   * 
   * @return the DEV specified by the user
   */
  private String getDevText() {
    return devText.getText().trim();
  }

  private void handleParametersRemoveButtonSelected() {
    IStructuredSelection selection = (IStructuredSelection) fViewer
        .getSelection();
    Object[] keys = selection.toArray();
    for (int i = 0; i < keys.length; i++) {
      String key = (String) keys[i];
      Map params = (Map) fViewer.getInput();
      params.remove(key);
    }
    fViewer.refresh();
    setParametersButtonsEnableState();
    updateLaunchConfigurationDialog();
  }

  private void handleParametersAddButtonSelected() {
    NameValuePairDialog dialog = new NameValuePairDialog(
        getShell(),
        MPJLauncherMessages.appletlauncher_argumenttab_parameters_dialog_add_title,
        new String[] {
            MPJLauncherMessages.appletlauncher_argumenttab_parameters_dialog_add_name_text,
            MPJLauncherMessages.appletlauncher_argumenttab_parameters_dialog_add_value_text }, //
        new String[] { EMPTY_STRING, EMPTY_STRING });
    openNewParameterDialog(dialog, null);
    setParametersButtonsEnableState();
  }

  private void openNewParameterDialog(NameValuePairDialog dialog, String key) {
    if (dialog.open() != Window.OK) {
      return;
    }
    String[] nameValuePair = dialog.getNameValuePair();
    Map params = (Map) fViewer.getInput();
    params.remove(key);
    params.put(nameValuePair[0], nameValuePair[1]);
    fViewer.refresh();
    updateLaunchConfigurationDialog();
  }

  private void handleParametersEditButtonSelected() {
    IStructuredSelection selection = (IStructuredSelection) fViewer
        .getSelection();
    String key = (String) selection.getFirstElement();
    Map params = (Map) fViewer.getInput();
    String value = (String) params.get(key);

    NameValuePairDialog dialog = new NameValuePairDialog(
        getShell(),
        MPJLauncherMessages.appletlauncher_argumenttab_parameters_dialog_edit_title,
        new String[] {
            MPJLauncherMessages.appletlauncher_argumenttab_parameters_dialog_edit_name_text,
            MPJLauncherMessages.appletlauncher_argumenttab_parameters_dialog_edit_value_text }, //
        new String[] { key, value });

    openNewParameterDialog(dialog, key);
  }

  private void setParametersButtonsEnableState() {
    IStructuredSelection selection = (IStructuredSelection) fViewer
        .getSelection();
    int selectCount = selection.size();
    if (selectCount < 1) {
      fParametersEditButton.setEnabled(false);
      fParametersRemoveButton.setEnabled(false);
    } else {
      fParametersRemoveButton.setEnabled(true);
      if (selectCount == 1) {
        fParametersEditButton.setEnabled(true);
      } else {
        fParametersEditButton.setEnabled(false);
      }
    }
    fParametersAddButton.setEnabled(true);
  }

  private class MPJExpressTabListener extends SelectionAdapter implements
      ModifyListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events
     * .ModifyEvent)
     */
    public void modifyText(ModifyEvent e) {
      updateLaunchConfigurationDialog();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
     * .events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e) {
      Object source = e.getSource();
      if (source == fViewer.getTable() || source == fViewer) {
        setParametersButtonsEnableState();
      } else if (source == fParametersAddButton) {
        handleParametersAddButtonSelected();
      } else if (source == fParametersEditButton) {
        handleParametersEditButtonSelected();
      } else if (source == fParametersRemoveButton) {
        handleParametersRemoveButtonSelected();
      } else if (source == fBrowseMPJHomeButton) {
        handleBrowseMPJHomeButtonClicked();
      }
    }

    private void handleBrowseMPJHomeButtonClicked() {
      Display firstDisplay = Display.getDefault();
      Shell firstShell = new Shell(firstDisplay);
      DirectoryDialog mpjHomeDirectory = new DirectoryDialog(firstShell);
      String mpjHome = mpjHomeDirectory.open();
      if (mpjHome != null)
        mpjHomeText.setText(mpjHome);
    }

  }

}
