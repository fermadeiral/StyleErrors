/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

/**
 * @author BREDEX GmbH
 * @created 05.03.2014
 */
public class JavaFXAutConfigComponent extends AutConfigComponent {
    /** default AUT configuration mode */
    public static final String AUT_CONFIG_DIALOG_MODE_KEY_DEFAULT = 
        JavaAutConfigComponent.Mode.BASIC.name();

    /** for check if the executable text field is empty */
    private static boolean isExecFieldEmpty = true;
       
    /** path of the executable file directory */
    private static String executablePath;

    /** for check if the executable text field is valid */
    private boolean m_isExecFieldValid = true;
    
    // internally used classes for data handling
    // internally used GUI components
    /** text field for the executable that will launch the AUT */
    private Text m_execTextField;
    /** browse button for the executable */
    private Button m_execButton;
    /** GUI component */
    private Text m_autArgsTextField;
    /** GUI component */
    private I18nEnumCombo<ActivationMethod> m_activationMethodCombo;
    /** gui component */
    private DirectCombo<String> m_monitoringCombo;
    /** GUI component */
    private Text m_envTextArea;
    /** the WidgetModifyListener */
    private WidgetModifyListener m_modifyListener;
    /** the WidgetFocusListener */
    private WidgetFocusListener m_focusListener;
    /** the the WidgetSelectionListener */
    private WidgetSelectionListener m_selectionListener;
    /** GUI component */
    private Button m_errorHighlightButton;
    
    /**
     * @param parent
     *            the parent
     * @param style
     *            the style
     * @param autConfig
     *            data to be displayed/edited
     * @param autName
     *            the name of the AUT that will be using this configuration.
     */
    public JavaFXAutConfigComponent(Composite parent, int style,
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
     * initializes the AUT configuration settings area.
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
            m_execTextField.setEnabled(true);
            m_execButton
                .setEnabled((checkLocalhostServer() || isRemoteRequest()));

        }

        return hasChanged;
    }

    /**
     * installs all listeners to the GUI components. All components visualizing
     * a property do have some sort of modification listeners which store edited
     * data in the edited instance. Some GUI components have additional
     * listeners for data validation or permission reevaluation.
     */
    protected void installListeners() {
        super.installListeners();

        WidgetModifyListener modifyListener = getModifyListener();
        WidgetSelectionListener selectionListener = getSelectionListener();

        m_activationMethodCombo.addSelectionListener(selectionListener);
        m_envTextArea.addModifyListener(modifyListener);
        getAUTAgentHostNameCombo().addModifyListener(modifyListener);
        m_autArgsTextField.addModifyListener(modifyListener);
        m_execTextField.addFocusListener(getFocusListener());
        m_execTextField.addModifyListener(modifyListener);
        m_execButton.addSelectionListener(selectionListener);
        m_errorHighlightButton.addSelectionListener(selectionListener);
        m_monitoringCombo.addSelectionListener(selectionListener);
    }

    /**
     * de-installs all listeners to the GUI components. All components
     * visualizing a property do have some sort of modification listeners which
     * store edited data in the edited instance. Some GUI components have
     * additional listeners for data validation or permission reevaluation.
     */
    protected void deinstallListeners() {
        super.deinstallListeners();

        WidgetModifyListener modifyListener = getModifyListener();
        WidgetSelectionListener selectionListener = getSelectionListener();

        m_activationMethodCombo.removeSelectionListener(selectionListener);
        m_envTextArea.removeModifyListener(modifyListener);
        m_autArgsTextField.removeModifyListener(modifyListener);
        getAUTAgentHostNameCombo().removeModifyListener(modifyListener);
        m_execTextField.removeFocusListener(getFocusListener());
        m_execTextField.removeModifyListener(modifyListener);
        m_execButton.removeSelectionListener(selectionListener);
        m_errorHighlightButton.removeSelectionListener(selectionListener);
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
     * {@inheritDoc}
     */
    protected void initState() {
        m_activationMethodCombo.setEnabled(true);
        m_envTextArea.setEnabled(true);
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
     * The action of the AUT parameter field.
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
            m_envTextArea.setText(StringUtils.defaultString(data
                    .get(AutConfigConstants.ENVIRONMENT)));
        }
    }
    
    /**creates a Button to toggle whether or not the screenshot that
     * is taken when an error at a component occures will be edited
     * to highlight the component at which the error occurred
     * @param parent the composite the button is being addet to
     */
    public void createErrorHighlighting(Composite parent) {
        m_errorHighlightButton = UIComponentHelper.
                createToggleButton(parent, parent.getBorderWidth());
        m_errorHighlightButton.setText(Messages.JubulaErrorHighlight);
        m_errorHighlightButton.setTextDirection(SWT.LEFT);
        m_errorHighlightButton.setSelection(false);
        ControlDecorator.createInfo(m_errorHighlightButton,
                Messages.JubulaErrorHighlightDetails, false);
    }

    /**
     * Populates GUI for the advanced configuration section.
     * 
     * @param data Map representing the data to use for population.
     */
    protected void populateAdvancedArea(Map<String, String> data) {
        // AUT arguments
        m_autArgsTextField.setText(
            StringUtils.defaultString(data.get(
                    AutConfigConstants.AUT_ARGUMENTS)));
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
            m_execTextField.setText(StringUtils.defaultString(data
                .get(AutConfigConstants.EXECUTABLE)));
        }
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
        m_execButton.setEnabled(browseEnabled && m_execButton.isEnabled());
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

            if (source.equals(m_execButton)) {
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
                putConfigValue(AutConfigConstants.ERROR_HIGHLIGHT,   
                        Boolean.toString(
                                m_errorHighlightButton.getSelection()));
                return;
            }

            Assert.notReached(Messages.EventActivatedByUnknownWidget 
                    + StringConstants.LEFT_PARENTHESIS + source 
                    + StringConstants.RIGHT_PARENTHESIS + StringConstants.DOT);
        }

        /** {@inheritDoc} */
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
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
        // AUT directory editor
        createAutDirectoryEditor(advancedAreaComposite);

        // parameter editor
        ControlDecorator.createInfo(
            UIComponentHelper.createLabelWithText(advancedAreaComposite,
                I18n.getString("AUTConfigComponent.autArguments")), //$NON-NLS-1$
            "ControlDecorator.AUTArguments", false); //$NON-NLS-1$
        m_autArgsTextField = 
            UIComponentHelper.createTextField(advancedAreaComposite, 2); 

        super.createAdvancedArea(advancedAreaComposite);
    }

    /**
     * Create this dialog's expert area component.
     * 
     * @param expertAreaComposite Composite representing the expert area.
     */
    protected void createExpertArea(Composite expertAreaComposite) {
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
        createErrorHighlighting(expertAreaComposite);
        
        super.createExpertArea(expertAreaComposite);
    }

    /**
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
     * {@inheritDoc}
     */
    protected void checkAll(java.util.List<DialogStatusParameter> paramList) {
        super.checkAll(paramList);
        addError(paramList, modifyAutConfigFieldAction());
        addError(paramList, modifyAutParamFieldAction());
        addError(paramList, modifyEnvFieldAction());
        addError(paramList, modifyExecTextField());
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