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
package org.eclipse.jubula.client.ui.rcp.preferences;

/**
 * @author BREDEX GmbH
 * @created 01.09.2004
 */
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedDirnameText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedIntText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * Custom preference page implementation. Uses the <code>Constants</code> API to
 * access the <code>Preferences</code> object for storing preference values.
 * <p>
 * Preference keys are defined in the <code>JubulaPlugin</code> interface.
 * </p>
 */
public class PrefPageBasic extends PreferencePage implements
        IWorkbenchPreferencePage {

    /** 1 column */
    private static final int NUM_COLUMNS_1 = 1;

    /** 10 horizontal spaces */
    private static final int HORIZONTAL_SPACING_10 = 10;

    /** 10 vertical spaces */
    private static final int VERTICAL_SPACING_10 = 10;

    /** margin height = 10 */
    private static final int MARGIN_HEIGHT_10 = 10;

    /** margin width = 10 */
    private static final int MARGIN_WIDTH_10 = 10;

    /** widgets used in preference page to define preference values private */
    private Button m_minimize;
    /** widgets used in preference page to define preference values private */
    private Button m_treeScroll;
    /** widgets used in preference page to define preference values private */
    private Button m_askStopAUT;
    /** checkbox to decide if CAP infos should be displayed after CAP-name in testCaseEditor */
    private Button m_capInfoCheckbox;
    /** checkbox to decide if CAP infos should be displayed after CAP-name in testCaseEditor */
    private Button m_showTransientChildrenCheckBox;
    /** checkbox to decide if load most recent project is active or not */
    private Button m_loadDefaultProjectCheckBox;
    /** Update reused project */
    private Button m_updateReusedProject;
    /** widgets used in preference page to define preference values private */
    private Button m_perspChange0Button;
    /** widgets used in preference page to define preference values private */
    private Button m_perspChange1Button;
    /** widgets used in preference page to define preference values private */
    private Button m_perspChange2Button;
    /** widgets used in preference page to define preference values private */
    private Button m_dataDirIsWorkspaceButton;
    /** if the data is not in the workspace, it's in this path */   
    private CheckedText m_dataDirPathTextfield;
    /** open a file selection dialog */
    private Button m_dataDirPathButton;
    /** time after which the content assist for the component names view opens itself*/
    private CheckedIntText m_compNamesContentAssistTime;

    /** Yes = 0; No = 1; Prompt = 2 */
    private int m_perspChangeValue;
    /** true or false */
    private boolean m_rememberValue;
    /** should data be retrieved from workspace */
    private boolean m_dataDirIsWorkspaceValue;

    /** The preference store to hold the existing preference values. */
    private IPreferenceStore m_store = Plugin.getDefault()
            .getPreferenceStore();
    /** a new selection listener */
    private final WidgetSelectionListener m_selectionListener = 
        new WidgetSelectionListener();

    /** watches changes on the ws selection button */
    private final SelectionListener m_dataDirIsWsButtonListener = 
        new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                dataDirIsWsChanged(m_dataDirIsWorkspaceButton.getSelection());
            }
    
            public void widgetSelected(SelectionEvent e) {
                dataDirIsWsChanged(m_dataDirIsWorkspaceButton.getSelection());
            }
        };
        
    /** show a file selection dialog if the button is pressed */
    private final SelectionListener m_dataDirPathButtonListener = 
        new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                browseForDir();
            }
    
            public void widgetSelected(SelectionEvent e) {
                browseForDir();
            }
        };


    /**
     * The constructor.
     * 
     */
    public PrefPageBasic() { 
        setPreferenceStore(m_store);
    }

    /**
     * Implement the user interface for the preference page. Returns a control
     * that should be used as the main control for the page.
     * <p>
     * User interface defined here supports the definition of preference
     * settings used by the management logic.
     * </p>
     * 
     * {@inheritDoc}
     */

    protected Control createContents(Composite parent) {
        final ScrolledComposite scrollComposite = 
            new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        final Composite composite = new Composite(scrollComposite, SWT.NONE);
        setGridLayout(composite, NUM_COLUMNS_1);

        createTreeScrollButton(composite);
        createMinimizeClientButton(composite);
        createAskStopAUTButton(composite);
        createShowCAPInfosCheckbox(composite);
        createShowTransientChildrensCheckbox(composite);
        createDefaultProjectCheckbox(composite);
        createUpdateReusedProjectsCheckbox(composite);
        createSeparator(composite, 3);
        createComponentNamesViewSettings(composite);
        createSeparator(composite, 3);
        createRememberGroup(composite);
        createSeparator(composite, 3);
        createDataDirGroup(composite);

        Label hint = new Label(composite, SWT.NONE);
        hint.setText(Messages.JubulaPrefPageBasicHint);

        addListener();

        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.PREFPAGE_BASIC);
        /** return the widget used as the base for the user interface */
        scrollComposite.setContent(composite);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinSize(composite.computeSize(
                SWT.DEFAULT, SWT.DEFAULT));
        
        validatePage();
        
        return scrollComposite;
    }
    
    /**
     * @param composite the parent composite
     */
    private void createShowCAPInfosCheckbox(Composite composite) {              
        m_capInfoCheckbox = new Button(composite, SWT.CHECK);
        m_capInfoCheckbox.setText(Messages.EditorPreferencePageShowCapInfo);
        m_capInfoCheckbox.setSelection(Plugin.getDefault()
                .getPreferenceStore().getBoolean(
                        Constants.SHOWCAPINFO_KEY));
        ControlDecorator.createInfo(m_capInfoCheckbox,  
                I18n.getString("ControlDecorator.ShowCapInfo"), false); //$NON-NLS-1$
    }
    
    /**
     * @param composite
     *            the parent composite
     */
    private void createShowTransientChildrensCheckbox(Composite composite) {
        m_showTransientChildrenCheckBox = new Button(composite, SWT.CHECK);
        m_showTransientChildrenCheckBox.setText(
                Messages.EditorPreferencePageShowTransientChildrenCheckBox);
        m_showTransientChildrenCheckBox.setSelection(Plugin.getDefault()
                .getPreferenceStore().getBoolean(
                        Constants.SHOW_TRANSIENT_CHILDREN_KEY));
        ControlDecorator.createInfo(m_showTransientChildrenCheckBox,
                I18n.getString("ControlDecorator.showTransientChildrenCheckBox"), false); //$NON-NLS-1$
    }
    
    /**
     * creates the check box so set load most recent project as active or inactive
     * @param composite the parent composite
     */
    private void createDefaultProjectCheckbox(Composite composite) {
        m_loadDefaultProjectCheckBox = new Button(composite, SWT.CHECK);
        m_loadDefaultProjectCheckBox.setText(
                Messages.LoadDefaultProject);
        m_loadDefaultProjectCheckBox.setSelection(m_store.getBoolean(
                        Constants.PERFORM_AUTO_PROJECT_LOAD_KEY));
    }
    
    /**
     * @param composite parent composite
     */
    private void createUpdateReusedProjectsCheckbox(Composite composite) {
        m_updateReusedProject = new Button(composite, SWT.CHECK);
        m_updateReusedProject.setText(
                Messages.UpdateReusedProjectsLabel);
        m_updateReusedProject.setSelection(m_store.getBoolean(
                        Constants.UPDATE_REUSED_PROJECT_KEY));
    }
    
    /**
     * creates the settings for the component names view
     * @param composite the parent composite
     */
    private void createComponentNamesViewSettings(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.PrefPageBasicComponentNamesView);
        group.setLayout(new GridLayout());
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        
        Composite content = new Composite(group, SWT.None);
        setGridLayout(content, 2);
        Label l = new Label(content, SWT.NONE);
        l.setText(Messages.SetContentAssistTimeForCompNames);
        m_compNamesContentAssistTime = new CheckedIntText(
                content, SWT.SINGLE | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        m_compNamesContentAssistTime.setLayoutData(data);
        m_compNamesContentAssistTime.setText(m_store.getString(
                        Constants.MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL));
        m_compNamesContentAssistTime.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                validatePage();
            }
        });
        ControlDecorator.createInfo(m_compNamesContentAssistTime,
                I18n.getString("ControlDecorator.ComponentNamesContentAssistInfo"), false); //$NON-NLS-1$
    }

    /**
     * create a grid layout with a selectable number of columns
     * @param composite composite for the grid
     * @param numColumns number of columns in the grid
     */
    private void setGridLayout(final Composite composite, int numColumns) {
        /** Define layout rules for widget placement */
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.horizontalSpacing = HORIZONTAL_SPACING_10;
        compositeLayout.verticalSpacing = VERTICAL_SPACING_10;
        compositeLayout.marginHeight = MARGIN_HEIGHT_10;
        compositeLayout.marginWidth = MARGIN_WIDTH_10;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(compositeData);
    }

    /**
     * @param composite the parent composite
     * @param horSpan the horizontal span
     */
    private void createSeparator(Composite composite, int horSpan) {
        Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sepData = new GridData();
        sepData.horizontalAlignment = GridData.FILL;
        sepData.horizontalSpan = horSpan;
        sep.setLayoutData(sepData);
    }
    
    
    /**
     * @param composite The parent composite.
     */
    private void createRememberGroup(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.PrefPageBasicOpenPerspective);
        RowLayout layout = new RowLayout();
        group.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        m_perspChange0Button = new Button(group, SWT.RADIO);
        m_perspChange0Button.setText(Messages.PrefPageBasicAlways);
        m_perspChange1Button = new Button(group, SWT.RADIO);
        m_perspChange1Button.setText(Messages.PrefPageBasicNever);
        m_perspChange2Button = new Button(group, SWT.RADIO);
        m_perspChange2Button.setText(Messages.PrefPageBasicPrompt);
        m_perspChangeValue = m_store.getInt(Constants.PERSP_CHANGE_KEY);
        setRadioSelection();
    }
    
    /**
     * GUI components for specifying the base directory for external data.
     * 
     * @param parent parent composite
     */
    private void createDataDirGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.PrefPageBasicSelectDataDir);
        group.setLayout(new GridLayout());
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        Composite content = new Composite(group, SWT.None);
        setGridLayout(content, 3);
        m_dataDirIsWorkspaceButton = new Button(content, SWT.CHECK);
        m_dataDirIsWorkspaceButton.setText(
            Messages.PrefPageBasicDataDirWSLabel);
        GridData data = new GridData();
        data.horizontalSpan = 3;
        m_dataDirIsWorkspaceButton.setLayoutData(data);
                
        Label l = new Label(content, SWT.None);
        l.setText(Messages.PrefPageBasicDataDirLabel);
        m_dataDirPathTextfield = new CheckedDirnameText(content, 
            SWT.SINGLE | SWT.BORDER);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        m_dataDirPathTextfield.setLayoutData(data);

        m_dataDirPathButton = new Button(content, SWT.PUSH);
        m_dataDirPathButton.
            setText(Messages.PrefPageBasicDataDirBrowse);
        
        m_store.setDefault(Constants.DATADIR_WS_KEY, true);
        m_store.setDefault(Constants.DATADIR_PATH_KEY, Platform.getLocation()
            .toOSString());
        m_dataDirIsWorkspaceValue = m_store
            .getBoolean(Constants.DATADIR_WS_KEY);
        
        setDataDirFields();
    }
    
    /**
     * Set the controls from the supplied data
     */
    private void setDataDirFields() {
        m_dataDirIsWorkspaceButton.setSelection(m_dataDirIsWorkspaceValue);
        m_dataDirPathTextfield.setEnabled(!m_dataDirIsWorkspaceValue);
        m_dataDirPathTextfield.setText(m_store
            .getString(Constants.DATADIR_PATH_KEY));
    }

    /**
     * Adds listener to swt widgets.
     */
    private void addListener() {
        m_perspChange0Button.addSelectionListener(m_selectionListener);
        m_perspChange1Button.addSelectionListener(m_selectionListener);
        m_perspChange2Button.addSelectionListener(m_selectionListener);       
        m_dataDirIsWorkspaceButton
            .addSelectionListener(m_dataDirIsWsButtonListener);
        m_dataDirPathButton.addSelectionListener(m_dataDirPathButtonListener);
        m_dataDirPathTextfield.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validatePage();
            }
        });
    }

    /**
     * the selection on this button changed
     * @param selection the new value
     */
    private void dataDirIsWsChanged(boolean selection) {
        m_dataDirPathButton.setEnabled(!selection);
        m_dataDirPathTextfield.setEnabled(!selection);
        m_dataDirIsWorkspaceValue = selection;
        m_dataDirPathTextfield.setText(m_dataDirPathTextfield.getText());
        validatePage();
    }
    
    /** 
     * display a file selection dialog and write the result into the
     * data path field.
     */
    private void browseForDir() {
        DirectoryDialog dialog = new DirectoryDialog(getShell(),
            SWT.APPLICATION_MODAL | SWT.OPEN);
        dialog.setFilterPath(m_dataDirPathTextfield.getText());
        dialog.setText(Messages.PrefPageBasicDataDirFileDialogTitle);
        String path = dialog.open();
        if (path != null) {
            m_dataDirPathTextfield.setText(path);
        }
    }
    
    /**
     * validate the page depending on its components
     */
    private void validatePage() {
        if (!m_dataDirIsWorkspaceValue) {
            if (!m_dataDirPathTextfield.isValid()) {
                setErrorMessage(Messages.PrefPageBasicDataDirInvalid);
                setValid(false);
                return;
            }
        }
        String compNamesContentAssistTime = 
                m_compNamesContentAssistTime.getText().trim();
        try {
            int time = Integer.parseInt(compNamesContentAssistTime);
            if (time < 0) {
                setErrorMessage(Messages
                        .CompNamesViewPreferencePageInvalidContentAssistTime);
                setValid(false);
                return;
            }
        } catch (NumberFormatException e) {
            setErrorMessage(Messages
                    .CompNamesViewPreferencePageInvalidContentAssistTime);
            setValid(false);
            return;
        }
        
        setErrorMessage(null);
        setValid(true);
    }

    /**
     * Removes listener from swt widgets.
     */
    private void removeListener() {
        m_perspChange0Button.removeSelectionListener(m_selectionListener);
        m_perspChange1Button.removeSelectionListener(m_selectionListener);
        m_perspChange2Button.removeSelectionListener(m_selectionListener);
        m_dataDirIsWorkspaceButton
        .removeSelectionListener(m_dataDirIsWsButtonListener);
    }

    /**
     * Sets the selection the radio buttons.
     */
    private void setRadioSelection() {
        if (m_perspChangeValue == Constants.PERSPECTIVE_CHANGE_YES) {
            m_perspChange0Button.setSelection(true);
            m_perspChange1Button.setSelection(false);
            m_perspChange2Button.setSelection(false);
        }
        if (m_perspChangeValue == Constants.PERSPECTIVE_CHANGE_NO) {
            m_perspChange1Button.setSelection(true);
            m_perspChange0Button.setSelection(false);
            m_perspChange2Button.setSelection(false);
        }
        if (m_perspChangeValue == Constants.PERSPECTIVE_CHANGE_PROMPT) {
            m_perspChange2Button.setSelection(true);
            m_perspChange0Button.setSelection(false);
            m_perspChange1Button.setSelection(false);
        }
    }

    /**
     * @param composite The parent composite.
     */
    private void createMinimizeClientButton(Composite composite) {
        m_minimize = new Button(composite, SWT.CHECK);
        m_minimize.setText(Messages.JubulaPrefPageBasicMinimize);
        m_minimize.setSelection(getPreferenceStore()
                .getBoolean(Constants.MINIMIZEONSUITESTART_KEY));
    }

    /**
     * @param composite The parent composite.
     */
    private void createAskStopAUTButton(Composite composite) {
        m_askStopAUT = new Button(composite, SWT.CHECK);
        m_askStopAUT.setText(Messages.JubulaPrefPageBasicAskStopAUT);
        m_askStopAUT.setSelection(getPreferenceStore()
                .getBoolean(Constants.ASKSTOPAUT_KEY));
    }
    
    /**
     * @param composite The parent composite.
     */
    private void createTreeScrollButton(Composite composite) {
        m_treeScroll = new Button(composite, SWT.CHECK);
        m_treeScroll.setText(Messages.JubulaPrefPageBasicScroll);
        m_treeScroll.setSelection(getPreferenceStore()
                .getBoolean(Constants.TREEAUTOSCROLL_KEY));
    }

    /**
     * Initializes the preference page
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        // do nothing
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void performDefaults() {
        m_minimize.setSelection(m_store
                .getDefaultBoolean(Constants.MINIMIZEONSUITESTART_KEY));
        m_treeScroll.setSelection(m_store
                .getDefaultBoolean(Constants.TREEAUTOSCROLL_KEY));
        m_askStopAUT.setSelection(m_store
                .getDefaultBoolean(Constants.ASKSTOPAUT_KEY));
        m_capInfoCheckbox.setSelection(m_store
                .getDefaultBoolean(Constants.SHOWCAPINFO_KEY));
        m_showTransientChildrenCheckBox.setSelection(m_store
                .getDefaultBoolean(Constants.SHOW_TRANSIENT_CHILDREN_KEY));
        m_compNamesContentAssistTime.setText(m_store.getDefaultString(
                Constants.MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL));
        m_loadDefaultProjectCheckBox.setSelection(m_store.getDefaultBoolean(
                Constants.PERFORM_AUTO_PROJECT_LOAD_KEY));
        m_updateReusedProject.setSelection(m_store.getDefaultBoolean(
                Constants.UPDATE_REUSED_PROJECT_KEY));
        m_perspChangeValue = m_store.getDefaultInt(Constants.PERSP_CHANGE_KEY);
        m_rememberValue = m_store.getDefaultBoolean(Constants.REMEMBER_KEY);
        setRadioSelection();

        m_dataDirIsWorkspaceValue = m_store
                .getDefaultBoolean(Constants.DATADIR_WS_KEY);
        setDataDirFields();
        m_dataDirPathTextfield.setText(m_store
                .getDefaultString(Constants.DATADIR_PATH_KEY));

        validatePage();
    }

    /**
     * Method declared on IPreferencePage. 
     * 
     * @return performOK
     */
    public boolean performOk() {
        getPreferenceStore().setValue(Constants.MINIMIZEONSUITESTART_KEY,
                m_minimize.getSelection());
        getPreferenceStore().setValue(Constants.REMEMBER_KEY, m_rememberValue);
        getPreferenceStore().setValue(Constants.PERSP_CHANGE_KEY,
                m_perspChangeValue);
        getPreferenceStore().setValue(
                Constants.MILLIS_TO_OPEN_COMP_NAMES_CONTENT_PROPOSAL,
                m_compNamesContentAssistTime.getValue());
        getPreferenceStore().setValue(Constants.TREEAUTOSCROLL_KEY,
                m_treeScroll.getSelection());
        getPreferenceStore().setValue(Constants.ASKSTOPAUT_KEY,
                m_askStopAUT.getSelection());
        getPreferenceStore().setValue(Constants.DATADIR_WS_KEY,
                m_dataDirIsWorkspaceValue);
        getPreferenceStore().setValue(Constants.DATADIR_PATH_KEY,
                m_dataDirPathTextfield.getText());
        getPreferenceStore().setValue(Constants.SHOWCAPINFO_KEY,
                m_capInfoCheckbox.getSelection());
        getPreferenceStore().setValue(Constants.SHOW_TRANSIENT_CHILDREN_KEY,
                m_showTransientChildrenCheckBox.getSelection());
        getPreferenceStore().setValue(Constants.PERFORM_AUTO_PROJECT_LOAD_KEY, 
                m_loadDefaultProjectCheckBox.getSelection());
        getPreferenceStore().setValue(Constants.UPDATE_REUSED_PROJECT_KEY, 
                m_updateReusedProject.getSelection());
        removeListener();
        return super.performOk();
    }
    
    /**
     * This inner class creates a new SelectionListener.
     * @author BREDEX GmbH
     * @created 09.08.2005
     */
    private class WidgetSelectionListener extends SelectionAdapter {
        /** @param e The selection event. */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o == m_perspChange0Button) {
                m_perspChangeValue = Constants.PERSPECTIVE_CHANGE_YES;
                return;
            } else if (o == m_perspChange1Button) {
                m_perspChangeValue = Constants.PERSPECTIVE_CHANGE_NO;
                return;
            } else if (o == m_perspChange2Button) {
                m_perspChangeValue = Constants.PERSPECTIVE_CHANGE_PROMPT;
                return;
            }
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                    + StringConstants.LEFT_PARENTHESIS + o 
                    + StringConstants.RIGHT_PARENTHESIS + StringConstants.DOT);
        }
    }
}