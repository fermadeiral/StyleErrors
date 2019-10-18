/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.widgets.autconfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.core.model.IAUTConfigPO.ActivationMethod;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.rcp.businessprocess.RemoteFileBrowserBP;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.I18nEnumCombo;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.toolkit.common.monitoring.MonitoringRegistry;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 13.02.2006
 * 
 */
public class JavaAutConfigComponent extends AutConfigComponent {
    /** Value for the default AUT Config mode */
    public static final String AUT_CONFIG_DIALOG_MODE_KEY_DEFAULT = 
        JavaAutConfigComponent.Mode.BASIC.name();
    
    /** String constant for getting the main class from a JAR manifest */
    private static final String MAIN_CLASS = "Main-Class"; //$NON-NLS-1$
    
    /** default jre path for Windows */
    private static final String DEFAULT_WIN_JRE = "../jre/bin/java.exe"; //$NON-NLS-1$
     
    /** for check if the executable text field is empty */
    private static boolean isExecFieldEmpty = true;
       
    /** path of the executable file directory */
    private static String executablePath;

    /** the logger */
    private static Logger log = LoggerFactory
        .getLogger(JavaAutConfigComponent.class);

    /** for check if the executable text field is valid */
    private boolean m_isExecFieldValid = true;

    // internally used classes for data handling
    // internally used GUI components
    /** gui component */
    private Text m_jarTextField;
    /** gui component */
    private Button m_jarButton;
    /** text field for the executable that will launch the AUT */
    private Text m_execTextField;
    /** browse button for the executable */
    private Button m_execButton;
    /** gui component */
    private Text m_autArgsTextField;
    /** gui component */
    private I18nEnumCombo<ActivationMethod> m_activationMethodCombo;
    /** gui component */
    private Text m_autJreTextField;
    /** gui component */
    private Button m_autJreButton;
    /** gui component */
    private Composite m_autJreComposite;
    /** gui component */
    private Text m_autJreParamTextField;
    /** gui component */
    private DirectCombo<String> m_monitoringCombo;
    /** gui component */
    private Text m_envTextArea;
    /** the WidgetModifyListener */
    private WidgetModifyListener m_modifyListener;
    /** the WidgetFocusListener */
    private WidgetFocusListener m_focusListener;
    /** the the WidgetSelectionListener */
    private WidgetSelectionListener m_selectionListener;
    /** Button to toggle the error highlighting of components */
    private Button m_errorHighlightButton;
    
    /**
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param autConfig data to be displayed/edited
     * @param autName the name of the AUT that will be using this configuration.
     */
    public JavaAutConfigComponent(Composite parent, int style,
        Map<String, String> autConfig, String autName) {
        
        super(parent, style, autConfig, autName, true);
    }
    
    /**
     * Request for the NagDialog called in NewProject
     * @return true if the field for executable files is empty, false otherwise
     */
    public static boolean isExecFieldEmpty() {
        return isExecFieldEmpty;
    }
    
    /**
     * @param basicAreaComposite The composite that represents the basic area.
     */
    protected void createBasicArea(Composite basicAreaComposite) {
        super.createBasicArea(basicAreaComposite);
        initGUIAutConfigSettings(basicAreaComposite);
    }
    
    /**
     * Inits the AUT configuration settings area.
     * 
     * @param parent The parent Composite.
     */
    private void initGUIAutConfigSettings(Composite parent) {
        // executable chooser
        UIComponentHelper.createLabel(parent, 
            "AUTConfigComponent.exec"); //$NON-NLS-1$ 
        m_execTextField = UIComponentHelper.createTextField(
                parent, 1);
        LayoutUtil.setMaxChar(m_execTextField,
                IPersistentObject.MAX_STRING_LENGTH);
        m_execButton = new Button(
                UIComponentHelper.createLayoutComposite(parent),
                SWT.PUSH);
        m_execButton.setText(Messages.AUTConfigComponentBrowse);
        m_execButton.setLayoutData(BUTTON_LAYOUT);
        m_execButton.setEnabled(Utils.isLocalhost());
    }

    
    /**
     * {@inheritDoc}
     */
    protected boolean putConfigValue(String key, String value) {
        boolean hasChanged = super.putConfigValue(key, value); 

        if (hasChanged) {
            
            for (Control field : getJavaRelatedFields()) {
                field.setEnabled(true);
            }
        
            boolean enableJarField = true;
            m_jarTextField.setEnabled(enableJarField);
            m_jarButton.setEnabled(enableJarField
                    && (checkLocalhostServer() || isRemoteRequest()));

            String exe = getConfigValue(AutConfigConstants.EXECUTABLE);
            boolean isEmpty = exe == null || exe.length() == 0;
            for (Control field : getJavaRelatedFields()) {
                field.setEnabled(isEmpty 
                    && field.isEnabled());
            }
            
            String jar = getConfigValue(AutConfigConstants.JAR_FILE);
            boolean isJarEmpty =  
                jar == null || jar.length() == 0;
            boolean enableExe = isJarEmpty;
            m_execTextField.setEnabled(enableExe);
            m_execButton.setEnabled(enableExe
                    && (checkLocalhostServer() || isRemoteRequest()));
        }
        
        return hasChanged;
    }

    /**
     * installs all listeners to the gui components. All components visualizing
     * a property do have some sort of modification listeners which store edited
     * data in the edited instance. Some gui components have additional
     * listeners for data validation or permission reevaluation.
     */
    protected void installListeners() {
        super.installListeners();

        WidgetModifyListener modifyListener = getModifyListener();
        WidgetSelectionListener selectionListener = getSelectionListener();

        m_activationMethodCombo.addSelectionListener(selectionListener);
        m_autJreParamTextField.addModifyListener(modifyListener);
        m_envTextArea.addModifyListener(modifyListener);
        m_jarButton.addSelectionListener(selectionListener);
        m_jarTextField.addModifyListener(modifyListener);
        getAUTAgentHostNameCombo().addModifyListener(modifyListener);
        m_autJreButton.addSelectionListener(selectionListener);
        m_autJreTextField.addModifyListener(modifyListener);
        m_autArgsTextField.addModifyListener(modifyListener);
        m_execTextField.addFocusListener(getFocusListener());
        m_execTextField.addModifyListener(modifyListener);
        m_execButton.addSelectionListener(selectionListener);
        m_monitoringCombo.addSelectionListener(selectionListener);
        m_errorHighlightButton.addSelectionListener(selectionListener);
        
    }

    /**
     * deinstalls all listeners to the gui components. All components
     * visualizing a property do have some sort of modification listeners which
     * store edited data in the edited instance. Some gui components have
     * additional listeners for data validatuion or permission reevaluation.
     */
    protected void deinstallListeners() {
        super.deinstallListeners();

        WidgetModifyListener modifyListener = getModifyListener();
        WidgetSelectionListener selectionListener = getSelectionListener();

        m_activationMethodCombo.removeSelectionListener(selectionListener);
        m_autJreParamTextField.removeModifyListener(modifyListener);
        m_envTextArea.removeModifyListener(modifyListener);
        m_autJreButton.removeSelectionListener(selectionListener);
        m_autJreTextField.removeModifyListener(modifyListener);
        m_jarButton.removeSelectionListener(selectionListener);
        m_jarTextField.removeModifyListener(modifyListener);
        m_autArgsTextField.removeModifyListener(modifyListener);
        getAUTAgentHostNameCombo().removeModifyListener(modifyListener);
        m_execTextField.removeFocusListener(getFocusListener());
        m_execTextField.removeModifyListener(modifyListener);
        m_execButton.removeSelectionListener(selectionListener);
        m_monitoringCombo.removeSelectionListener(selectionListener);

    }
    /**
     * 
     * @param parent The parent Composite.
     */
    private void initGuiEnvironmentEditor(Composite parent) {
        UIComponentHelper.createLabel(parent, "AUTConfigComponent.envVariables"); //$NON-NLS-1$ 
        m_envTextArea = new Text(parent, 
                LayoutUtil.MULTI_TEXT | SWT.V_SCROLL);
        LayoutUtil.setMaxChar(m_envTextArea,
                IPersistentObject.MAX_STRING_LENGTH);
        GridData textGridData = new GridData();
        textGridData.horizontalAlignment = GridData.FILL;
        textGridData.horizontalSpan = 2;
        textGridData.grabExcessHorizontalSpace = false;
        textGridData.widthHint = COMPOSITE_WIDTH;
        textGridData.heightHint = Dialog.convertHeightInCharsToPixels(LayoutUtil
            .getFontMetrics(m_envTextArea), 2);
        m_envTextArea.setLayoutData(textGridData);
    }

    /**
     * 
     * @param parent The parent Composite.
     */
    private void initGuiJarChooser(Composite parent) {
        UIComponentHelper.createLabel(parent, "AUTConfigComponent.jar"); //$NON-NLS-1$ 
        m_jarTextField = UIComponentHelper.createTextField(parent, 1);
        LayoutUtil.setMaxChar(m_jarTextField,
                IPersistentObject.MAX_STRING_LENGTH);
        m_jarButton = new Button(UIComponentHelper
                .createLayoutComposite(parent), SWT.PUSH);
        m_jarButton.setText(Messages.AUTConfigComponentBrowse);
        m_jarButton.setLayoutData(BUTTON_LAYOUT);
        m_jarButton.setEnabled(Utils.isLocalhost());
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void initState() {
        m_activationMethodCombo.setEnabled(true);
        m_autJreParamTextField.setEnabled(true);
        m_envTextArea.setEnabled(true);
        m_jarTextField.setEnabled(true);
        m_jarButton.setEnabled(true);
        checkLocalhostServer();
        RemoteFileBrowserBP.clearCache(); // avoid all caches
    }
    
    /** 
     * The action of the environment field.
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyEnvFieldAction() {
        putConfigValue(AutConfigConstants.ENVIRONMENT, m_envTextArea.getText());
        return null;
    }
    
    /** 
     * The action of the jre parameter field.
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyJreParamFieldAction() {
        putConfigValue(AutConfigConstants.JRE_PARAMETER, 
                m_autJreParamTextField.getText());
        return null;
    }
    
    /** 
     * The action of the aut parameter field.
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyAutParamFieldAction() {
        String params = m_autArgsTextField.getText();
        putConfigValue(AutConfigConstants.AUT_ARGUMENTS, params);
        return null;
    }
    
    /** 
     * The action of the activation combo
     * @return true
     */
    boolean handleActivationComboEvent() {
        putConfigValue(AutConfigConstants.ACTIVATION_METHOD,
                ActivationMethod.getRCString(m_activationMethodCombo
                        .getSelectedObject()));
        return true;
    }

    /** 
     * The action of the JAR name field.
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyJarFieldAction() {

        DialogStatusParameter error = null;
        boolean isEmpty = m_jarTextField.getText().length() == 0;
        if (isValid(m_jarTextField, true) && !isEmpty) {
            if (checkLocalhostServer()) {
                String filename = m_jarTextField.getText();
                File file = new File(filename);
                String workingDir = StringUtils.defaultString(
                        getConfigValue(AutConfigConstants.WORKING_DIR));
                if (!file.isAbsolute()
                    && workingDir.length() != 0) {
                    
                    filename = workingDir + "/" + filename; //$NON-NLS-1$
                    file = new File(filename);
                }
                JarFile jarFile = null;
                try {
                    if (!file.exists()) {
                        error = createWarningStatus(
                            Messages.AUTConfigComponentFileNotFound);
                    } else {
                        jarFile = new JarFile(file);
                        Manifest jarManifest = jarFile.getManifest();
                        if (jarManifest == null) {
                            // no manifest for JAR
                            error = createErrorStatus(
                                    Messages.AUTConfigComponentNoManifest);
                        } else if (
                            jarManifest.getMainAttributes().getValue(MAIN_CLASS)
                                == null) {
                            
                            // no main class defined in JAR manifest
                            error = createErrorStatus(
                                    Messages.AUTConfigComponentNoMainClass);
                        }
                    }
                } catch (ZipException ze) {
                    // given file is not a jar file
                    error = createErrorStatus(
                        NLS.bind(Messages.AUTConfigComponentFileNotJar,
                                filename));
                } catch (IOException e) {
                    // could not find jar file
                    error = createWarningStatus(NLS.bind(
                            Messages.AUTConfigComponentFileNotFound,
                                filename));
                } finally {
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        } catch (IOException e) {
                            log.error(e.getLocalizedMessage(), e);
                        }
                    }
                }
            }
        } else if (!isEmpty) {
            error = createErrorStatus(Messages.AUTConfigComponentWrongJAR);
        }

        putConfigValue(AutConfigConstants.JAR_FILE, m_jarTextField.getText());
        
        return error;
    }
    /**
     * Populates GUI for the advanced configuration section, and Displays the
     * current Values of the Activation_METHOD and monitoring agents in the 
     * ComboBoxes
     * @param data map representing the data to use for population.
     */    
    
    protected void populateExpertArea(Map<String, String> data) {

        m_activationMethodCombo.setSelectedObject(
                ActivationMethod.getEnum(data
                        .get(AutConfigConstants.ACTIVATION_METHOD)));
        m_errorHighlightButton.setSelection(Boolean.valueOf(
                data.get(AutConfigConstants.ERROR_HIGHLIGHT)));
        
        String monitoringAgentId = data.get(
                AutConfigConstants.MONITORING_AGENT_ID);
        if (StringUtils.isEmpty(monitoringAgentId)) { 
            m_monitoringCombo.deselectAll();
        } else {
            m_monitoringCombo.setSelectedObject(monitoringAgentId);
            if (m_monitoringCombo.getSelectedObject() == null) {
                // additional handling for missing Monitoring extension
                ArrayList<String> values = 
                    new ArrayList<String>(m_monitoringCombo.getValues());
                ArrayList<String> displayValues = new ArrayList<String>(
                        Arrays.asList(m_monitoringCombo.getItems()));
                values.add(0, monitoringAgentId);
                values.remove(null);
                displayValues.add(0, monitoringAgentId);
                displayValues.remove(StringUtils.EMPTY);
                
                m_monitoringCombo.setItems(values, displayValues);
                m_monitoringCombo.setSelectedObject(monitoringAgentId);
            }
        }
        
        if (!isDataNew(data)) {
            m_autJreParamTextField.setText(StringUtils.defaultString(data
                    .get(AutConfigConstants.JRE_PARAMETER)));
            m_envTextArea.setText(StringUtils.defaultString(data
                    .get(AutConfigConstants.ENVIRONMENT)));
        }

    }
    /**
     * {@inheritDoc}
     */
    protected void populateMonitoringArea(Map<String, String> data) {
        Composite composite = getMonitoringAreaComposite();
        Control[] ca = composite.getChildren();
        
        for (int i = 0; i < ca.length; i++) {                       
            if (ca[i].getData(MonitoringConstants.MONITORING_KEY) != null) {
                if (ca[i] instanceof Text) {
                    Text t = (Text) ca[i];
                    String value = data.get(String.valueOf(
                            t.getData(MonitoringConstants.MONITORING_KEY)));
                    if (value != null && !value.equals(StringConstants.EMPTY)) {
                        t.setText(value);
                    }
                }
                if (ca[i] instanceof Button) {
                    Button b = (Button) ca[i];
                    String value = data.get(String.valueOf(b
                            .getData(MonitoringConstants.MONITORING_KEY)));
                    if (value != null) {
                        b.setSelection(Boolean.valueOf(value));
                    }
                }
        
            }
        }
    }
    /**
     * Populates GUI for the advanced configuration section.
     * 
     * @param data Map representing the data to use for population.
     */
    protected void populateAdvancedArea(Map<String, String> data) {
        // aut arguments
        m_autArgsTextField.setText(
            StringUtils.defaultString(data.get(
                    AutConfigConstants.AUT_ARGUMENTS)));
        // JRE
        String jreDirectory = StringUtils.defaultString(
                getConfigValue(AutConfigConstants.JRE_BINARY));
        if (isConfigNew()) {
            jreDirectory = getDefaultJreBin();
            modifyJREFieldAction();
        }
        m_autJreTextField.setText(jreDirectory);
    }

    /**
     * 
     * @return the absolute path and filename of the executable used to start
     *         the current JVM. Returns the empty string if the executable 
     *         cannot be found for any reason.
     */
    private String getDefaultJreBin() {
        File jreBin = new File(DEFAULT_WIN_JRE);
        if (jreBin.exists()) {
            return DEFAULT_WIN_JRE;
        }

        return StringConstants.EMPTY;
    }
    
    /**
     * Populates GUI for the basic configuration section.
     * 
     * @param data Map representing the data to use for population.
     */
    protected void populateBasicArea(Map<String, String> data) {

        super.populateBasicArea(data);
        
        if (!isDataNew(data)) {
            getAUTAgentHostNameCombo().select(
                getAUTAgentHostNameCombo().indexOf(
                    StringUtils.defaultString(data
                        .get(AutConfigConstants.AUT_CONFIG_AUT_HOST_NAME))));
            m_jarTextField.setText(StringUtils.defaultString(data
                .get(AutConfigConstants.JAR_FILE)));
            m_execTextField.setText(StringUtils.defaultString(data
                .get(AutConfigConstants.EXECUTABLE)));

        }
 
    }

    /** 
     * The action of the JRE name field.
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyJREFieldAction() {
        DialogStatusParameter error = null;
        putConfigValue(AutConfigConstants.JRE_BINARY, 
                m_autJreTextField.getText());
        if (!isValid(m_autJreTextField, true) 
            && m_autJreTextField.getText().length() != 0) {
            
            error = createErrorStatus(Messages.AUTConfigComponentWrongJRE);
        }
        return error;
    }

    /**
     * Writes the path of the executable file in the AUT Working Dir field.
     * @param directory The dir path of the executable file as string.
     */
    private void setWorkingDirToExecFilePath(String directory) {
        if ((StringUtils.isEmpty(getAutWorkingDirField().getText()) 
                || isBasicMode())
                && isFilePathAbsolute(directory) && m_isExecFieldValid) {
            File wd = new File(directory);
            wd = wd.getParentFile();
            if (wd != null) {
                String execPath = wd.getAbsolutePath();

                getAutWorkingDirField().setText(execPath);
                putConfigValue(AutConfigConstants.WORKING_DIR, execPath);
            }
        }
    }
    
    /**
     * @param filename to check
     * @return true if the path of the given executable file is absolute
     */
    private static boolean isFilePathAbsolute(String filename) {
        final File execFile = new File(filename);
        return execFile.isAbsolute();
    }
    
    /**
     * handle the browse request locally
     * 
     * @param extensionFilters Only files with one of the 
     *                         provided extensions will be shown in the dialog.
     *                         May be <code>null</code>, in which case all 
     *                         files will be shown.
     * @param configVarKey key for storing the result
     * @param textField control for visualizing the value
     * @param title window title
     */
    void browseLocal(String [] extensionFilters, String title, 
            Text textField, String configVarKey) {
        String directory;
        FileDialog fileDialog = 
            new FileDialog(getShell(), SWT.APPLICATION_MODAL | SWT.ON_TOP);
        if (extensionFilters != null) {
            fileDialog.setFilterExtensions(extensionFilters);
        }
        fileDialog.setText(title);
        String filterPath = Utils.getLastDirPath();
        File path = new File(textField.getText());
        if (!path.isAbsolute()) {
            path = new File(getConfigValue(AutConfigConstants.WORKING_DIR), 
                textField.getText());
        }
        if (path.exists()) {
            try {
                if (path.isDirectory()) {
                    filterPath = path.getCanonicalPath();
                } else {
                    filterPath = new File(path.getParent()).getCanonicalPath();
                }
            } catch (IOException e) {
                // Just use the default filter path which is already set
            }
        }
        fileDialog.setFilterPath(filterPath);
        directory = fileDialog.open();
        if (directory != null) {
            textField.setText(directory);
            Utils.storeLastDirPath(fileDialog.getFilterPath());
            putConfigValue(configVarKey, directory);
        }
    }

    /**
     * Handles the button event.
     * @param fileDialog The file dialog.
     */
    void handleExecButtonEvent(FileDialog fileDialog) {
        String directory;
        fileDialog.setText(
            Messages.AUTConfigComponentSelectExecutable);
        String filterPath = Utils.getLastDirPath();
        File path = new File(getConfigValue(AutConfigConstants.EXECUTABLE));
        if (!path.isAbsolute()) {
            path = new File(getConfigValue(AutConfigConstants.WORKING_DIR), 
                getConfigValue(AutConfigConstants.EXECUTABLE));
        }
        if (path.exists()) {
            try {
                if (path.isDirectory()) {
                    filterPath = path.getCanonicalPath();
                } else {
                    filterPath = new File(path.getParent()).getCanonicalPath();
                }
            } catch (IOException e) {
                // Just use the default filter path which is already set
            }
        }
        fileDialog.setFilterPath(filterPath);
        directory = fileDialog.open();
        if (directory != null) {
            m_execTextField.setText(directory);
            Utils.storeLastDirPath(fileDialog.getFilterPath());
            putConfigValue(AutConfigConstants.EXECUTABLE, directory);
            executablePath = directory;
            
            setWorkingDirToExecFilePath(executablePath);
        }
    }
    
    /**
     * handler for remote browsing
     */
    private void handleExecButtonEventForRemote() {

        if (remoteBrowse(false, AutConfigConstants.EXECUTABLE, m_execTextField,
                Messages.AUTConfigComponentSelectExecutable)) { 
            setWorkingDirToExecFilePath(executablePath);
        }
    }
    
    /**
     * The action of the working directory field.
     * @return <code>null</code> if the new value is valid. Otherwise, returns
     *         a status parameter indicating the cause of the problem.
     */
    DialogStatusParameter modifyExecTextField() {
        DialogStatusParameter error = null;
        m_isExecFieldValid = true;
        isExecFieldEmpty = m_execTextField.getText().length() == 0;
        String filename = m_execTextField.getText();
        if (isValid(m_execTextField, true) && !isExecFieldEmpty) { 
            if (checkLocalhostServer()) {
                File file = new File(filename);
                if (!file.isAbsolute()) {
                    String workingDirString = 
                        getConfigValue(AutConfigConstants.WORKING_DIR);
                    if (workingDirString != null 
                        && workingDirString.length() != 0) {
                        
                        filename = workingDirString + "/" + filename; //$NON-NLS-1$
                        file = new File(filename);
                    }
                }

                try {
                    if (!file.isFile()) {
                        error = createWarningStatus(
                            Messages.AUTConfigComponentFileNotFound);
                    } else {
                        // Make sure that the user has not entered an executable
                        // JAR file in the wrong field.
                        new JarFile(file);
                        error = createErrorStatus(NLS.bind(
                            Messages.AUTConfigComponentFileJar,
                                file.getCanonicalPath()));
                    }
                } catch (ZipException ze) {
                    // Expected. This occurs if the given file exists but is not 
                    // a JAR file.
                } catch (IOException e) {
                    // could not find file
                    error = createWarningStatus(NLS.bind(
                        Messages.AUTConfigComponentFileNotFound,
                            filename));
                }
            }
        } else if (!isExecFieldEmpty) {
            error = createErrorStatus(
                    Messages.AUTConfigComponentWrongExecutable);
        }
        if (error != null) {
            m_isExecFieldValid = false;
        }
        putConfigValue(AutConfigConstants.EXECUTABLE, 
                m_execTextField.getText());
        executablePath = filename;
        
        return error;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected boolean checkLocalhostServer() {
        boolean enable = super.checkLocalhostServer();
        boolean browseEnabled = enable || isRemoteRequest();
        m_jarButton.setEnabled(browseEnabled && m_jarTextField.getEnabled());
        m_execButton.setEnabled(browseEnabled && m_execButton.isEnabled());
        m_autJreButton.setEnabled(browseEnabled && m_autJreButton.isEnabled());
        return enable;
    }
    
    /**
     * This private inner class contains a new ModifyListener.
     * 
     * @author BREDEX GmbH
     * @created 22.11.2006
     */
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void modifyText(ModifyEvent e) {
            Object source = e.getSource();
            if (source.equals(getAUTAgentHostNameCombo())) {
                checkLocalhostServer();
                boolean checkListeners = m_selectionListener != null;
                if (checkListeners) {
                    deinstallListeners();
                }
                if (checkListeners) {
                    installListeners();
                }
            } 

            checkAll();         
        }
    }
        
    /**
     * This private inner class contains a new FocusListener.
     * 
     * @author BREDEX GmbH
     * @created 03.07.2008
     */
    @SuppressWarnings("synthetic-access")
    private class WidgetFocusListener implements FocusListener {

        /**
         * {@inheritDoc}
         */
        public void focusGained(FocusEvent e) {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void focusLost(FocusEvent e) {
            Object source = e.getSource();
            if (source.equals(m_execTextField)) {
                setWorkingDirToExecFilePath(executablePath);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean isJavaAut() {
        return true;
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * 
     * @author BREDEX GmbH
     * @created 13.07.2005
     */
    private class WidgetSelectionListener implements SelectionListener {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();

            if (source.equals(m_autJreButton)) {
                if (isRemoteRequest()) {
                    remoteBrowse(false, AutConfigConstants.JRE_BINARY,
                            m_autJreTextField, 
                                Messages.AUTConfigComponentSelectJRE);
                } else {
                    browseLocal(null, 
                            Messages.AUTConfigComponentSelectJRE,
                            m_autJreTextField, AutConfigConstants.JRE_BINARY);
                }
                return;
            } else if (source.equals(m_jarButton)) {
                if (isRemoteRequest()) {
                    remoteBrowse(false, AutConfigConstants.JAR_FILE,
                        m_jarTextField, Messages.AUTConfigComponentSelectJAR);
                } else {
                    browseLocal(new String[] { "*.jar" }, //$NON-NLS-1$
                            Messages.AUTConfigComponentSelectJAR,
                            m_jarTextField, AutConfigConstants.JAR_FILE);
                }
                return;
            } else if (source.equals(m_execButton)) {
                if (isRemoteRequest()) {
                    handleExecButtonEventForRemote();
                } else {
                    handleExecButtonEvent(new FileDialog(getShell(),
                            SWT.APPLICATION_MODAL | SWT.ON_TOP));
                }
                return;
            } else if (source.equals(m_activationMethodCombo)) {
                handleActivationComboEvent();
                return;
            } else if (source.equals(m_monitoringCombo)) {
                handleMonitoringComboEvent();
                return;
            } else if (source.equals(m_errorHighlightButton)) {
                boolean errorHighlightSelesctState = 
                        m_errorHighlightButton.getSelection();
                putConfigValue(AutConfigConstants.ERROR_HIGHLIGHT,
                     Boolean.toString(errorHighlightSelesctState));
                return;
            }

            Assert.notReached(Messages.EventActivatedByUnknownWidget 
                    + StringConstants.LEFT_PARENTHESIS + source 
                    + StringConstants.RIGHT_PARENTHESIS + StringConstants.DOT);
        }

        /**
         * Reacts, when an object is double clicked.
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void widgetDefaultSelected(SelectionEvent e) {
            Object source = e.getSource();
            DirectoryDialog directoryDialog = new DirectoryDialog(getShell(), 
                    SWT.APPLICATION_MODAL);
            String directory;
            Assert.notReached(Messages.EventActivatedByUnknownWidget 
                    + StringConstants.LEFT_PARENTHESIS + source 
                    + StringConstants.RIGHT_PARENTHESIS + StringConstants.DOT);
        }
    }
    
    /**
     * This method handles the event which was fired when an item in the
     * Combobox is selected.    
     */
    protected void handleMonitoringComboEvent() {  
        
        if (m_monitoringCombo.getSelectedObject() != null) {         
            
            putConfigValue(AutConfigConstants.MONITORING_AGENT_ID,   
                    m_monitoringCombo.getSelectedObject().toString()); 
          
            cleanComposite(getMonitoringAreaComposite());
            createActualMonitoringArea(getMonitoringAreaComposite());    
            
        } else {
            cleanComposite(getMonitoringAreaComposite());
            putConfigValue(AutConfigConstants.MONITORING_AGENT_ID, 
                    StringConstants.EMPTY);            
        }        
        String autId = getConfigValue(AutConfigConstants.AUT_ID);
        showMonitoringInfoDialog(autId);
    }
    
    /**
     * Create this dialog's advanced area component.
     * 
     * @param advancedAreaComposite Composite representing the advanced area.
     */
    protected void createAdvancedArea(Composite advancedAreaComposite) {
        // jar chooser
        initGuiJarChooser(advancedAreaComposite); 
        
        // AUT directory editor
        createAutDirectoryEditor(advancedAreaComposite);

        // parameter editor
        ControlDecorator.createInfo(UIComponentHelper.createLabel(
                advancedAreaComposite, "AUTConfigComponent.autArguments"), //$NON-NLS-1$
                I18n.getString("ControlDecorator.AUTArguments"), false); //$NON-NLS-1$
        m_autArgsTextField = 
            UIComponentHelper.createTextField(advancedAreaComposite, 2); 
        // JRE directory editor
        UIComponentHelper.createLabel(
            advancedAreaComposite, "AUTConfigComponent.jre"); //$NON-NLS-1$ 
        m_autJreTextField = 
            UIComponentHelper.createTextField(advancedAreaComposite, 1);
        LayoutUtil.setMaxChar(m_autJreTextField,
                IPersistentObject.MAX_STRING_LENGTH);
        GridData comboGrid = new GridData(GridData.FILL, GridData.CENTER, 
            true, false, 1, 1);
        LayoutUtil.addToolTipAndMaxWidth(comboGrid, m_autJreTextField);
        m_autJreTextField.setLayoutData(comboGrid);
        ((GridData)m_autJreTextField.getLayoutData()).widthHint = 
            COMPOSITE_WIDTH;
        m_autJreComposite = 
            UIComponentHelper.createLayoutComposite(advancedAreaComposite);
        m_autJreButton = 
            new Button(m_autJreComposite, SWT.PUSH);
        m_autJreButton.setText(Messages.AUTConfigComponentBrowse);
        m_autJreButton.setLayoutData(BUTTON_LAYOUT);

        super.createAdvancedArea(advancedAreaComposite);
    }

    /**
     * Create this dialog's expert area component.
     * 
     * @param expertAreaComposite Composite representing the expert area.
     */
    protected void createExpertArea(Composite expertAreaComposite) {
        
        // JRE parameter editor
        ControlDecorator.createInfo(UIComponentHelper.createLabel(
                expertAreaComposite, "AUTConfigComponent.jreArguments"), //$NON-NLS-1$
                I18n.getString("ControlDecorator.JREArguments"), false); //$NON-NLS-1$
        m_autJreParamTextField = UIComponentHelper.createTextField(
                expertAreaComposite, 2);
        // Environment editor
        initGuiEnvironmentEditor(expertAreaComposite);
        // activation method editor
        UIComponentHelper.createLabel(expertAreaComposite,
                "AUTConfigComponent.activationMethod"); //$NON-NLS-1$ 
        m_activationMethodCombo = UIComponentHelper.createEnumCombo(
                expertAreaComposite, 2,
                "AUTConfigComponent.ActivationMethod", ActivationMethod.class); //$NON-NLS-1$

        UIComponentHelper.createSeparator(expertAreaComposite, 3);
        
        ControlDecorator.createInfo(UIComponentHelper.createLabel(
                expertAreaComposite, "AUTConfigComponent.labelMonitoring"), //$NON-NLS-1$, 
                I18n.getString("AUTConfigComponent.labelMonitoring.helpText"), false); //$NON-NLS-1$
                        
        m_monitoringCombo = UIComponentHelper.createCombo(
                expertAreaComposite, 2, 
                MonitoringRegistry.getAllRegisteredMonitoringIds(), 
                MonitoringRegistry.getAllRegisteredMonitoringNames(), true); 
        
        createErrorHighlightButton(expertAreaComposite);
        
        super.createExpertArea(expertAreaComposite);
       
    }
    
    /**
     * @param composite The parent composite
     */
    private void createErrorHighlightButton(Composite composite) {
        m_errorHighlightButton = UIComponentHelper.
                createToggleButton(composite, composite.getBorderWidth());
        m_errorHighlightButton.setText(Messages.JubulaErrorHighlight);
        m_errorHighlightButton.setTextDirection(SWT.LEFT);
        m_errorHighlightButton.setSelection(false);
        ControlDecorator.createInfo(m_errorHighlightButton,
                Messages.JubulaErrorHighlightDetails, false);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void openServerPrefPage() {
        super.openServerPrefPage();
        boolean checkListeners = m_selectionListener != null;

        if (checkListeners) {
            deinstallListeners();
        }
        if (checkListeners) {
            installListeners();
        }
    }

    /**
     * 
     * @return a List containing all components that configure Java-related
     *         settings.
     */
    protected java.util.List<Control> getJavaRelatedFields() {
        java.util.List<Control> javaFields = new ArrayList<Control>();
        
        javaFields.add(m_autJreButton);
        javaFields.add(m_autJreTextField);
        javaFields.add(m_autJreComposite);
        javaFields.add(m_autJreParamTextField);
        javaFields.add(m_jarButton);
        javaFields.add(m_jarTextField);
        
        return javaFields;
    }

    /**
     * 
     * @return the single instance of the selection listener.
     */
    @SuppressWarnings("synthetic-access")
    private WidgetSelectionListener getSelectionListener() {
        if (m_selectionListener == null) {
            m_selectionListener = new WidgetSelectionListener();
        }
        
        return m_selectionListener;
    }

    /**
     * 
     * @return the single instance of the modify listener.
     */
    @SuppressWarnings("synthetic-access")
    private WidgetModifyListener getModifyListener() {
        if (m_modifyListener == null) {
            m_modifyListener = new WidgetModifyListener();
        }
        
        return m_modifyListener;
    }
    
    /**
     * 
     * @return the single instance of the modify listener.
     */
    @SuppressWarnings("synthetic-access")
    private WidgetFocusListener getFocusListener() {
        if (m_focusListener == null) {
            m_focusListener = new WidgetFocusListener();
        }
        
        return m_focusListener;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void checkAll(java.util.List<DialogStatusParameter> paramList) {
        super.checkAll(paramList);
        addError(paramList, modifyAutConfigFieldAction());
        addError(paramList, modifyAutParamFieldAction());
        addError(paramList, modifyEnvFieldAction());
        addError(paramList, modifyJarFieldAction());
        addError(paramList, modifyExecTextField());
        addError(paramList, modifyJREFieldAction());
        addError(paramList, modifyJreParamFieldAction());
        addError(paramList, modifyServerComboAction());

    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        RemoteFileBrowserBP.clearCache(); // get rid of cached directories
        super.dispose();
    }
    
    @Override
    protected void createMonitoringArea(Composite monitoringComposite) {
        super.createActualMonitoringArea(monitoringComposite);
        setCompositeVisible(monitoringComposite, true);
    }
} 