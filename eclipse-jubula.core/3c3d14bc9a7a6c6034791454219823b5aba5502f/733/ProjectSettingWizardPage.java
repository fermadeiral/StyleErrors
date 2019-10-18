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
package org.eclipse.jubula.client.ui.rcp.wizards.pages;

import java.awt.im.InputContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.Languages;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedProjectNameText;
import org.eclipse.jubula.client.ui.rcp.wizards.ProjectWizard;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.I18nEnumCombo;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.html.Browser;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created 18.05.2005
 */
public class ProjectSettingWizardPage extends WizardPage {
    /** */
    private static final String EMPTY = StringConstants.EMPTY;
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;
    /** Toolkit that is selected by default */
    private static final String DEFAULT_TOOLKIT =
            CommandConstants.CONCRETE_TOOLKIT;
    /** main container */
    private Composite m_mainComp;
    /** top container */
    private Composite m_topComposite;
    /** new name for the project */
    private String m_newProjectName = null;
    /** the combo box for the project toolkit */
    private DirectCombo<String> m_projectToolKitComboBox;
    /** the text field for the project name */
    private Text m_projectNameTF;
    /** Aut executable container */
    private Composite m_autExecutable;
    /** the text field for AUT executable command*/
    private Text m_autExecutableTF;
    /** browse button for the executable */
    private Button m_autExecButton;
    /** Aut URL label container */
    private Composite m_autUrl;
    /** the text field for AUT executable command*/
    private Text m_autUrlTF;
    /** Aut toolkit label container */
    private Composite m_autToolkit;
    /** the combo box for the aut toolkit */
    private DirectCombo<String> m_autToolKitComboBox;
    /** Browser label container */
    private Composite m_browser;
    /** the combo box for browser */
    private I18nEnumCombo<Browser> m_browserComboBox;
    /** Browser path label container */
    private Composite m_browserPath;
    /** the text field for browser path*/
    private Text m_browserPathTF;
    /** browse button for the executable */
    private Button m_browserPathButton;
    /** the selectionListener */
    private final WidgetSelectionListener m_selectionListener = 
            new WidgetSelectionListener();
    /** the modifyListener */
    private final WidgetModifyListener m_modifyListener =
            new WidgetModifyListener();
    /** the new AUT to create */
    private IAUTMainPO m_autMain;
    /** the name of the selected aut configuration */
    private IAUTConfigPO m_autConfig;
    /** project template check box */
    private Button m_projectTemplate;
    
    
    /**
     * @param pageName The name of the wizard page.
     * @param autMain 
     * @param autConfig 
     */
    public ProjectSettingWizardPage(String pageName, IAUTMainPO autMain,
            IAUTConfigPO autConfig) {
        super(pageName);
        setPageComplete(false);
        m_autMain = autMain;
        m_autConfig = autConfig;
    }
    
    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        m_mainComp = UIComponentHelper.createLayoutComposite(parent);
        ((GridLayout)m_mainComp.getLayout()).marginWidth = 10;
        m_topComposite = UIComponentHelper.createLayoutComposite(m_mainComp);
        Composite bottomComp = UIComponentHelper.createLayoutComposite(
                m_mainComp);
        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.END;
        bottomComp.setLayoutData(data);
        setMargin((GridLayout)bottomComp.getLayout());
        UIComponentHelper.createSeparator(m_topComposite, NUM_COLUMNS_2);
        setMessage(Messages.ProjectWizardNewProject);
        createProjectNameField(); 
        createProjectToolKit();
        Plugin.getHelpSystem().setHelp(m_mainComp, ContextHelpIds
            .PROJECT_WIZARD);
        createAutToolKit();
        createExecutableCommandField();
        createBrowserCombo();
        createBrowserPathField();
        validation();
        createProjectTemplateCheckBox(bottomComp);
        createNextLabel(bottomComp);
        setControl(m_mainComp);
        handleProjectToolkitCombo();
    }

    /**
     * Creates the text field for the project name.
     */
    private void createProjectNameField() {
        Composite line = createLineComposite();
        Composite leftComp = createLeftComposite(line,
                Messages.ProjectSettingWizardPageProjectName);
        Composite rightComp = createRightComposite(line);
        m_projectNameTF = createTextField(rightComp, Messages
                .ProjectSettingWizardPageDefaultProjectName, true);
        m_projectNameTF.setSelection(0, m_projectNameTF.getText().length());
        m_projectNameTF.addModifyListener(m_modifyListener);
        m_projectNameTF.setData(SwtToolkitConstants.WIDGET_NAME,
                "NewProjectWizard.ProjectNameTextField"); //$NON-NLS-1$
    }

    /**
     * Creates the AUT executable command line.
     */
    private void createExecutableCommandField() {
        m_autExecutable = createLineComposite();
        Composite leftComp = createLeftComposite(m_autExecutable,
                Messages.ProjectSettingWizardAUTExecutableCommand);
        Composite rightComp = createRightComposite(m_autExecutable,
                NUM_COLUMNS_2);
        m_autExecutableTF = createTextField(rightComp, null, false);
        m_autExecutableTF.addModifyListener(m_modifyListener);
        m_autExecutableTF.setData(SwtToolkitConstants.WIDGET_NAME,
                "NewProjectWizard.ExecutablePathField"); //$NON-NLS-1$
        m_autExecButton = createBrowseButton(rightComp);
        refresh();
    }

    /**
     * Creates the text field for the AUT url.
     */
    private void createUrlField() {
        m_autUrl = createLineComposite();
        Composite leftComp = createLeftComposite(m_autUrl,
                Messages.ProjectSettingWizardAUTUrl);
        Composite rightComp = createRightComposite(m_autUrl);
        m_autUrlTF = createTextField(rightComp, null, false);
        m_autUrlTF.addModifyListener(m_modifyListener);
        m_autUrlTF.setData(SwtToolkitConstants.WIDGET_NAME,
                "NewProjectWizard.BrowserURLField"); //$NON-NLS-1$
        refresh();
    }

    /**
     * Creates the project toolkit line
     */
    private void createProjectToolKit() {
        Composite line = createLineComposite();
        Composite leftComp = createLeftComposite(line,
                Messages.ProjectSettingWizardPageProjectToolKitLabel,
                I18n.getString("ControlDecorator.NewProjectToolkit")); //$NON-NLS-1$
        Composite rightComp = createRightComposite(line);
        m_projectToolKitComboBox = ControlFactory.createToolkitCombo(rightComp);
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(comboGridData,
                m_projectToolKitComboBox);
        m_projectToolKitComboBox.setLayoutData(comboGridData);
        m_projectToolKitComboBox.setSelectedObject(DEFAULT_TOOLKIT);
        m_projectToolKitComboBox.addSelectionListener(m_selectionListener);
        m_projectToolKitComboBox.setData(SwtToolkitConstants.WIDGET_NAME,
                "NewProjectWizard.ProjectToolkit"); //$NON-NLS-1$
    }

    /** Creates the aut toolkit line */
    private void createAutToolKit() {
        m_autToolkit = createLineComposite();
        Composite leftComp = createLeftComposite(m_autToolkit,
                Messages.ProjectSettingWizardPageAutToolKitLabel,
                I18n.getString("ControlDecorator.NewProjectAUTToolkit")); //$NON-NLS-1$
        Composite rightComp = createRightComposite(m_autToolkit);
        try {
            m_autToolKitComboBox = ControlFactory.createAutToolkitCombo(
                    rightComp, null, null, true);
            GridData comboGridData = new GridData();
            comboGridData.grabExcessHorizontalSpace = true;
            LayoutUtil.addToolTipAndMaxWidth(comboGridData,
                    m_autToolKitComboBox);
            m_autToolKitComboBox.setLayoutData(comboGridData);
            m_autToolKitComboBox.addSelectionListener(m_selectionListener);
            m_autToolKitComboBox.setData(SwtToolkitConstants.WIDGET_NAME,
                    "NewProjectWizard.AutToolkit"); //$NON-NLS-1$
        } catch (ToolkitPluginException e) {
            e.printStackTrace();
            removeAutToolKit();
        } 
        refresh();
    }

    /** Creates the browser combo line */
    private void createBrowserCombo() {
        m_browser = createLineComposite();
        Composite leftComp = createLeftComposite(m_browser,
                I18n.getString("WebAutConfigComponent.browser")); //$NON-NLS-1$
        Composite rightComp = createRightComposite(m_browser);
        m_browserComboBox = UIComponentHelper.createEnumCombo(rightComp, 2,
                "WebAutConfigComponent.Browser", Browser.class); //$NON-NLS-1$
        GridData comboGridData = new GridData();
        comboGridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(comboGridData, m_browserComboBox);
        m_browserComboBox.setLayoutData(comboGridData);
        m_browserComboBox.addSelectionListener(m_selectionListener);
        m_browserComboBox.setData(SwtToolkitConstants.WIDGET_NAME,
                "NewProjectWizard.BrowserComboBox"); //$NON-NLS-1$
        refresh();
    }

    /** Creates the browser path line */
    private void createBrowserPathField() {
        m_browserPath = createLineComposite();
        Composite leftComp = createLeftComposite(m_browserPath,
                I18n.getString("WebAutConfigComponent.browserPath")); //$NON-NLS-1$
        Composite rightComp = createRightComposite(m_browserPath,
                NUM_COLUMNS_2);
        m_browserPathTF = createTextField(rightComp, null, false);
        m_browserPathTF.addModifyListener(m_modifyListener);
        m_browserPathButton = createBrowseButton(rightComp);
        m_browserPathTF.setData(SwtToolkitConstants.WIDGET_NAME,
                "NewProjectWizard.BrowserPathField"); //$NON-NLS-1$
        refresh();
    }

    /** Creates the project template check box
     * @param parent 
     */
    private void createProjectTemplateCheckBox(Composite parent) {
        m_projectTemplate = new Button(parent, SWT.CHECK);
        m_projectTemplate.setText(Messages.ProjectWizardProjectTemplate);
        m_projectTemplate.setSelection(false);
        m_projectTemplate.addSelectionListener(m_selectionListener);
        ControlDecorator.createInfo(m_projectTemplate,
                Messages.ProjectWizardProjectTemplateInfo, false);
        m_projectTemplate.setData(SwtToolkitConstants.WIDGET_NAME,
                "NewProjectWizard.ProjectTemplateCheckBox"); //$NON-NLS-1$
        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.END;
        m_projectTemplate.setLayoutData(data);
        refresh();
        createLeftComposite(parent, null);
    }
    
    /**
     * Creates the next label.
     * @param parent the parent composite
     */
    private void createNextLabel(Composite parent) {
        Label nextLabel = UIComponentHelper.createLabelWithText(parent,
                Messages.ProjectSettingWizardPageClickFinish);
        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.END;
        nextLabel.setLayoutData(data);
        refresh();
    }
    
    /**
     * @param parent component
     * @return a button
     */
    private Button createBrowseButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(Messages.AUTConfigComponentBrowse);
        button.addSelectionListener(m_selectionListener);
        GridData buttonGridData = new GridData();
        buttonGridData.grabExcessHorizontalSpace = false;
        buttonGridData.horizontalAlignment = SWT.RIGHT;
        button.setLayoutData(buttonGridData);
        return button;
    }
    
    /**
     * @return a composite line containing two columns
     */
    private Composite createLineComposite() {
        return UIComponentHelper.createLayoutComposite(m_topComposite,
                NUM_COLUMNS_2);
    }
    
    /**
     * @param parent parent composite
     * @param text label text
     * @return a composite containing a label
     */
    private Composite createLeftComposite(Composite parent, String text) {
        return createLeftComposite(parent, text, null);
    }
    
    /**
     * @param parent parent composite
     * @param text label text
     * @param info info text
     * @return a composite containing a label and info decoration
     */
    private Composite createLeftComposite(Composite parent, String text,
            String info) {
        Composite comp = UIComponentHelper.createLayoutComposite(parent);
        setMargin((GridLayout)comp.getLayout());
        GridData rightGridData = (GridData)comp.getLayoutData();
        rightGridData.grabExcessHorizontalSpace = true;
        rightGridData.widthHint = 120;
        Label label = text != null ? UIComponentHelper
                .createLabelWithText(comp, text) : null;
        if (info != null && label != null) {
            ControlDecorator.createInfo(label, info, false);
        }
        return comp;
    }
    
    /**
     * @param parent parent composite
     * @return a composite line containing a column
     */
    private Composite createRightComposite(Composite parent) {
        return createRightComposite(parent, 1); 
    }
    
    /**
     * @param parent parent composite
     * @param column number of columns
     * @return a composite line may containing one or more column(s)
     */
    private Composite createRightComposite(Composite parent, int column) {
        Composite comp = UIComponentHelper
                .createLayoutComposite(parent, column);
        setMargin((GridLayout)comp.getLayout());
        GridData rightGridData = (GridData)comp.getLayoutData();
        rightGridData.grabExcessHorizontalSpace = true;
        rightGridData.widthHint = 500;
        return comp;
    }
    
    /**
     * @param parent parent composite
     * @param text of text field
     * @param checked if <code>true</code> then the return text field will be
     *                  an CheckedProjectNameText.
     * @return a text field
     */
    private Text createTextField(Composite parent, String text,
            boolean checked) {
        Text textField = checked ? new CheckedProjectNameText(
                parent, SWT.BORDER) :  new Text(parent, SWT.BORDER);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = SWT.FILL;
        textField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(textField);
        if (text != null) {
            textField.setText(text);
        }
        return textField;
    }
    
    /** remove the aut toolkit line */
    private void removeAutToolKit() {
        if (m_autToolkit != null) {
            m_autToolkit.dispose();
            m_autToolkit = null;
        }
        m_autToolKitComboBox = null;
        refresh();
    }
    
    /** remove the aut executable line */
    private void removeAutExecutable() {
        if (m_autExecutable != null) {
            m_autExecutable.dispose();
            m_autExecutable = null;
        }
        m_autExecutableTF = null;
        putConfigValue(AutConfigConstants.EXECUTABLE, EMPTY);
        putConfigValue(AutConfigConstants.WORKING_DIR, EMPTY);
        refresh();
    }
    
    /** remove the aut url line */
    private void removeAutUrl() {
        if (m_autUrl != null) {
            m_autUrl.dispose();
            m_autUrl = null;
        }
        m_autUrlTF = null;
        putConfigValue(AutConfigConstants.AUT_URL, EMPTY);
        refresh();
    }
    
    /** remove the aut browser combo line */
    private void removeBrowserCombo() {
        if (m_browser != null) {
            m_browser.dispose();
            m_browser = null;
        }
        m_browserComboBox = null;
        putConfigValue(AutConfigConstants.BROWSER, EMPTY);
        refresh();
    }
    
    /** remove the aut browser path line */
    private void removeBrowserPathField() {
        if (m_browserPath != null) {
            m_browserPath.dispose();
            m_browserPath = null;
        }
        m_browserPathTF = null;
        putConfigValue(AutConfigConstants.BROWSER_PATH, EMPTY);
        refresh();
    }
    
    /** Handle aut config */
    private void handleAutConfig() {
        String projectToolkit = m_projectToolKitComboBox.getSelectedObject();
        String autToolkit = m_autToolKitComboBox == null ? null
                : m_autToolKitComboBox.getSelectedObject();
        boolean isPTConcret = projectToolkit.equals(DEFAULT_TOOLKIT);
        if (!isPTConcret || isPTConcret && autToolkit != null) {
            fillAutConfig();
        } else {
            cleanAutConfig();
        }
    }
    
    /** Clear the default aut configs */
    private void cleanAutConfig() {
        m_autMain.setName(EMPTY);
        putConfigValue(AutConfigConstants.AUT_CONFIG_NAME, EMPTY);
        putConfigValue(AutConfigConstants.AUT_ID, EMPTY);
        putConfigValue(AutConfigConstants.AUT_CONFIG_AUT_HOST_NAME, EMPTY);
    }
    
    /** Fill the default aut configs */
    private void fillAutConfig() {
        m_autMain.setName(m_newProjectName);
        String localhost = EnvConstants.LOCALHOST_ALIAS;
        String[] strings = new String [] {m_newProjectName, localhost};
        String configName = NLS.bind(
                Messages.AUTConfigComponentDefaultAUTConfigName, strings);
        putConfigValue(AutConfigConstants.AUT_CONFIG_NAME, configName);
        putConfigValue(AutConfigConstants.AUT_ID, m_newProjectName);
        putConfigValue(AutConfigConstants.AUT_CONFIG_AUT_HOST_NAME, localhost);
    }
    
    /** 
     * The action of the project name field.
     * the project name starts or end with a blank, or the field is empty
     * @throws Exception 
     */
    private void modifyProjectNameField() throws DialogValidationException {
        boolean isCorrect = true;
        String projectName = m_projectNameTF.getText();
        int projectNameLength = projectName.length();
        if ((projectNameLength == 0) || (projectName.startsWith(" ")) //$NON-NLS-1$
            || (projectName.charAt(projectNameLength - 1) == ' ')) {
            
            isCorrect = false;
        }
        if (isCorrect) {
            if (ProjectPM.doesProjectNameExist(projectName)) {
                throw new DialogValidationException(
                        Messages.ProjectSettingWizardPageDoubleProjectName);
            }
            m_newProjectName = projectName;
            handleAutConfig();
        } else {
            throw new DialogValidationException(projectNameLength == 0
                    ? Messages.ProjectWizardEmptyProject
                            : Messages.ProjectWizardNotValidProject);
        }
    }

    /**
     * Handles the file browser button event.
     * @param title The file dialog title.
     * @param text texField
     * @param extension extension
     * @return directory
     */
    private String fileBrowser(String title, Text text, String[] extension) {
        FileDialog fileDialog = new FileDialog(getShell(),
                SWT.APPLICATION_MODAL | SWT.ON_TOP);
        fileDialog.setText(title);
        String directory;
        String filterPath = Utils.getLastDirPath();
        try {
            File path = new File(text.getText());
            if (path.exists()) {
                if (path.isDirectory()) {
                    filterPath = path.getCanonicalPath();
                } else {
                    filterPath = new File(path.getParent()).getCanonicalPath();
                }
            }
        } catch (IOException e) {
            // Just use the default filter path which is already set
        }
        fileDialog.setFilterExtensions(extension);
        fileDialog.setFilterPath(filterPath);
        directory = fileDialog.open();
        if (directory != null) {
            Utils.storeLastDirPath(fileDialog.getFilterPath());
        }
        return directory;
    }
    
    /** Handle the executable path button */
    private void handleExecButtonEvent() {
        String directory = fileBrowser(Messages
                .AUTConfigComponentSelectExecutable, m_autExecutableTF, null);
        if (directory != null) {
            m_autExecutableTF.setText(directory);
            setWorkingDirToExecFilePath(directory);
            validation();
        }
    }
    
    /** Handle the browser path button */
    private void handleBrowserPathButtonEvent() {
        String directory = fileBrowser(
                I18n.getString("WebAutConfigComponent.SelectBrowserPath"), //$NON-NLS-1$
                m_browserPathTF, null);
        if (directory != null) {
            m_browserPathTF.setText(directory);
            validation();
        }
    }

    /**
     * Writes the path of the executable file in the AUT Working Dir field.
     * @param file The dir path of the executable file as string.
     */
    private void setWorkingDirToExecFilePath(String file) {
        String execPath = EMPTY;
        File wd = new File(file);
        if (wd.isAbsolute() && wd.getParentFile() != null) {
            execPath = wd.getParentFile().getAbsolutePath();
        }
        putConfigValue(AutConfigConstants.WORKING_DIR, execPath);
    }

    /** Check and modify the executable path */
    private void modifyExecutableTextField() {
        String executable = EMPTY;
        if (m_autExecutableTF != null
                && !m_autExecutableTF.getText().isEmpty()) {
            executable =  m_autExecutableTF.getText();
            File file = new File(executable);
            if (!file.exists()) {
                warningMessage(
                        Messages.AUTConfigComponentFileNotFound);
            }
        }
        putConfigValue(AutConfigConstants.EXECUTABLE, executable);
        setWorkingDirToExecFilePath(executable);
    }
    
    /**
     * Check and modify the url
     * @throws DialogValidationException
     */
    private void modifyUrlTextField() throws DialogValidationException {
        String urlText = EMPTY;
        if (m_autUrlTF != null
                && !m_autUrlTF.getText().isEmpty()) {
            try {
                new URL(m_autUrlTF.getText());
                urlText = m_autUrlTF.getText();
            } catch (MalformedURLException e) {
                throw new DialogValidationException(I18n.getString(
                        "WebAutConfigComponent.wrongUrl") //$NON-NLS-1$
                        + StringConstants.NEWLINE + e.getMessage());
            }
        }
        putConfigValue(AutConfigConstants.AUT_URL, urlText);
    }
    
    /** Modify the browser */
    private void modifyBrowser() {
        final String browser = (m_browserComboBox == null || m_browserComboBox
                .getSelectedObject() == null ? EMPTY
                        : m_browserComboBox.getSelectedObject().toString());
        putConfigValue(AutConfigConstants.BROWSER, browser);
    }
    
    /** Check and modify the browser path */
    private void modifyBrowserPathTextField() {
        String txt = EMPTY;
        if (m_browserPathTF != null && !m_browserPathTF.getText().isEmpty()) {
            txt = m_browserPathTF.getText();
            File file = new File(txt);
            if (!file.exists()) {
                warningMessage(Messages.AUTConfigComponentFileNotFound);
            }
        }
        putConfigValue(AutConfigConstants.BROWSER_PATH, txt);
    }
    
    /** set default message */
    private void noMessage() {
        setMessage(Messages.ProjectWizardNewProject);
    }

    /**
     * @param errorMessage the error message
     */
    private void errorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return;
        }
        setMessage(errorMessage, IMessageProvider.ERROR);
    }
    
    /**
     * @param warningMessage the warning message
     */
    private void warningMessage(String warningMessage) {
        if (warningMessage == null || warningMessage.isEmpty()) {
            return;
        }
        setMessage(warningMessage, IMessageProvider.WARNING);
    }
    
    /** Checks validity of all fields. */
    protected void validation() {
        noMessage();
        setPageComplete(false);
        try {
            modifyProjectNameField();
            modifyUrlTextField();
            modifyExecutableTextField();
            modifyBrowser();
            modifyBrowserPathTextField();
            setPageComplete(true);
        } catch (DialogValidationException e) {
            e.errorMessageHandling();
        }
    }
    
    /**
     * @param key key
     * @param value value
     * @return <code>true</code> if the configuration was changed.
     *          Otherwise <code>false</code>.  
     */
    private boolean putConfigValue(String key, String value) {
        String previousValue = StringUtils.defaultString(
                m_autConfig.getConfigMap().put(key, value));
        boolean wasEmpty = previousValue.length() == 0; 
        boolean isEmpty = StringUtils.defaultString(value).length() == 0;
        boolean areBothEmpty = wasEmpty && isEmpty;
        if (isEmpty) {
            m_autConfig.getConfigMap().remove(key);
        }
        return (!areBothEmpty) || !value.equals(previousValue);
    }
    
    /** Handle the combobox of project toolkit */
    private void handleProjectToolkitCombo() {
        String toolkit = m_projectToolKitComboBox.getSelectedObject();
        removeAutToolKit();
        putConfigValue(AutConfigConstants.KEYBOARD_LAYOUT, EMPTY);
        handleAutToolkitCombo(toolkit);
    }
    
    /**
     * Handle the combobox of aut toolkits
     * @param toolkit 
     */
    private void handleAutToolkitCombo(String toolkit) {
        m_autMain.setToolkit(toolkit);
        removeAutExecutable();
        removeAutUrl();
        removeBrowserCombo();
        removeBrowserPathField();
        if (toolkit == null) {
            return;
        }
        switch (toolkit) {
            case CommandConstants.RCP_TOOLKIT:
            case CommandConstants.SWT_TOOLKIT:
                keyboardLayout();
            case CommandConstants.SWING_TOOLKIT:
            case CommandConstants.JAVAFX_TOOLKIT:
                createExecutableCommandField();
                break;
            case CommandConstants.HTML_TOOLKIT:
                createUrlField();
                createBrowserCombo();
                break;
            default:
                m_autMain.setToolkit(null);
                createAutToolKit();
                break;
        }
        validation();
    }
    
    /** Handle the combobox of browser */
    private void handleBrowserCombo() {
        Browser browser = m_browserComboBox.getSelectedObject();
        removeBrowserPathField();
        if (!browser.equals(Browser.InternetExplorer)) {
            createBrowserPathField();
        }
    }
    
    /** Set the default keyboard layout on */
    private void keyboardLayout() {
        String local = InputContext.getInstance().getLocale().toString();  
        local = Arrays.asList(Languages.getInstance()
                .getKeyboardLayouts()).contains(local)
                ? local : Locale.getDefault().toString();
        putConfigValue(AutConfigConstants.KEYBOARD_LAYOUT, local);
    }
    
    /**
     * {@inheritDoc}
     */
    public ProjectWizard getWizard() {
        return (ProjectWizard)super.getWizard();
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     */
    private class WidgetSelectionListener implements SelectionListener {
        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_autExecButton)) {
                handleExecButtonEvent();
            } else if (o.equals(m_browserPathButton)) {
                handleBrowserPathButtonEvent();
            } else if (o.equals(m_projectToolKitComboBox)) {
                handleProjectToolkitCombo();
                validation();
            } else if (o.equals(m_autToolKitComboBox)) {
                handleAutToolkitCombo(m_autToolKitComboBox
                        .getSelectedObject());
                validation();
            } else if (o.equals(m_browserComboBox)) {
                handleBrowserCombo();
                validation();
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            //do nothing
        }
    }
    
    /**
     * This private inner class contains a new ModifyListener.
     * @author BREDEX GmbH
     */
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            validation();
        }       
    }
    
    /**
     * Handles the error messages
     * @author BREDEX GmbH
     */
    protected class DialogValidationException extends Exception {
        /** @param message the error message */
        public DialogValidationException(String message) {
            super(message);
        }
        /** error handling*/
        public void errorMessageHandling() {
            setPageComplete(false);
            errorMessage(super.getMessage());
        }
    }
    
    /**
     * @param layout the GridLayout where the margin should be set on
     */
    private void setMargin(GridLayout layout) {
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 20;
    }
    
    /**
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem()
        .displayHelp(ContextHelpIds.PROJECT_WIZARD);
    }

    /**
     * @return the newProjectName
     */
    public String getNewProjectName() {
        return m_newProjectName;
    }

    /**
     * @return the toolkit of project
     */
    public String getProjectToolkit() {
        return m_projectToolKitComboBox.getSelectedObject();
    }

    /**
     * @return the toolkit of aut
     */
    public String getAutToolkit() {
        return m_autMain.getToolkit();
    }

    /**
     * @return need project template
     */
    public boolean needProjectTamplet() {
        return m_projectTemplate.getSelection();
    }
    
    /** resize the wizard */
    private void refresh() {
        if (m_projectNameTF.isVisible()) {
            getShell().pack(true);
        }
    }
}