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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.I18nStringCombo;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author BREDEX GmbH
 * @created 10.01.2005
 */
public class TestResultPreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage {

    /** 1 column */
    private static final int NUM_COLUMNS = 4;

    /** 10 horizontal spaces */
    private static final int HORIZONTAL_SPACING_10 = 10;

    /** 10 vertical spaces */
    private static final int VERTICAL_SPACING_10 = 10;

    /** margin height = 10 */
    private static final int MARGIN_HEIGHT_10 = 10;

    /** margin width = 10 */
    private static final int MARGIN_WIDTH_10 = 10;
    
    /** Available report styles (i18n keys)*/
    private static final String [] REPORT_STYLES = {
        "StyleComplete", //$NON-NLS-1$
        "StyleErrorsOnly" //$NON-NLS-1$
    };

    /** Available report style prefix (for i18n) */
    private static final String REPORT_STYLE_BASIC_KEY = 
        "TestResultViewPreferencePage"; //$NON-NLS-1$
    
    /** textfield to define path to xml Generation */
    private Text m_path = null;

    /**  Checkbox to decide Open ResultView */
    private Button m_openResultView = null;

    /**  Checkbox to decide Open ResultView */
    private Button m_trackResults = null;

    /**  Checkbox to decide to automatically take screenshots */
    private Button m_autoScreenshots = null;
    
    /** Checkbox to decide Report Generation */
    private Button m_generateReport = null;
    
    /** Checkbox to decide Monitoring Report Generation */
    private Button m_generateMonitoringReport = null;

    /** ComboBox to decide Report Generation style */
    private Combo m_reportStyle = null;
    
    /** Browse Button */
    private Button m_browser = null;
        
    /**
     * textfield to define maximum number of results in the test result summary
     * view
     */
    private Text m_numberOfDays = null;
    
    /** textfield to define maximum number of iterations */
    private Text m_iterMaxField = null;

    /**
     * <code>m_testExecRememberValue</code>
     */
    private boolean m_testExecRememberValue;

    /** LogView Radio Button for always */
    private Button m_openLogButton;
    /** LogView Radio Button for never */
    private Button m_openLog1Button;
    /** LogView Radio Button for prompt */
    private Button m_openLog2Button;
    /** the value */
    private int m_openLogValue;
    /** listener for changes in the open logview settings */
    private SelectionListener m_logButtonListener = 
            new WidgetSelectionListener();
    
    /**
     * Default Constructor  
     */
    public TestResultPreferencePage() { 
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
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
        /** Add layer to parent widget */
        setMessage(Messages.TestResultViewPreferencePageTitle, NONE);
        final ScrolledComposite scrollComposite = new ScrolledComposite(parent,
                SWT.V_SCROLL | SWT.H_SCROLL);
        final Composite composite = new Composite(scrollComposite, SWT.NONE);

        /** Define laout rules for widget placement */
        compositeGridData(composite);
        // add widgets to composite
        createOpenResultView(composite);
        createRememberGroup(composite);
        createGenerateReport(composite);
        

        createMaxNumberOfResults(composite);
        createIterMaxField(composite);
        // context sensitive help
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.PREFPAGE_TESTRESULT);
        initPreferences();

        /** return the widget used as the base for the user interface */
        scrollComposite.setContent(composite);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                scrollComposite.setMinSize(composite.computeSize(SWT.DEFAULT,
                        SWT.DEFAULT));
            }
        });
        addListener();
        return scrollComposite;
    }

    /**
     * @param composite the parent composite
     */
    private void createMaxNumberOfResults(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setFont(LayoutUtil.BOLD_TAHOMA);
        label.setText(Messages.TestResultViewPreferencePageMaxNumberOfDaysText);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        label.setLayoutData(gridData);
        m_numberOfDays = newTextField(composite);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_numberOfDays);
        m_numberOfDays.setLayoutData(gridData);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_numberOfDays.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }
        });
    }
    
    /**
     * @param composite the parent composite
     */
    private void createIterMaxField(Composite composite) {
        Label label = new Label(composite, SWT.NONE);
        label.setFont(LayoutUtil.BOLD_TAHOMA);
        label.setText(Messages.TestResultViewPreferencePageMaxNumOfItersText);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        label.setLayoutData(gridData);
        m_iterMaxField = newTextField(composite);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_iterMaxField);
        m_iterMaxField.setLayoutData(gridData);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_iterMaxField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }
        });
    }
    
    /**
     * @param composite The composite.
     */
    private void compositeGridData(Composite composite) {
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS;
        compositeLayout.horizontalSpacing = HORIZONTAL_SPACING_10;
        compositeLayout.verticalSpacing = VERTICAL_SPACING_10;
        compositeLayout.marginHeight = MARGIN_HEIGHT_10;
        compositeLayout.marginWidth = MARGIN_WIDTH_10;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = true;
        compositeData.grabExcessVerticalSpace = true;
        composite.setLayoutData(compositeData);
    }
    
    /**
     * Checks if Preference Page is complete and valid
     */
    protected void checkCompleteness() {
        String path = m_path.getText().trim();
        boolean writeResult = m_generateReport.getSelection();
        if (writeResult 
            && path.equals(StringConstants.EMPTY)) {
            setErrorMessage(Messages.TestResultViewPreferencePagePathEmpty);
            setValid(false);
            return;
        }
        File dir = new File(path);
        boolean valid = true;
        
        if (writeResult) {
            if (dir.isDirectory()
                    && dir.exists()) {
                File file = new File(dir.getAbsolutePath() + "/tmp.xml"); //$NON-NLS-1$
                try {
                    boolean created = false;
                    if (!file.exists()) {
                        file.createNewFile();
                        created = true;
                    }
                    if (!file.canWrite()) {
                        valid = false;
                    }
                    if (created) {
                        file.delete();
                    }
                } catch (IOException e) {
                    valid = false;
                }
            } else {
                valid = false;
            }
        }
        if (!valid) {
            setErrorMessage(Messages.TestResultViewPreferencePagePathInvalid);
            setValid(false);
            return;
        }
        if (!checkValidNumber(m_numberOfDays.getText().trim())) {
            return;
        }
        if (!checkValidNumber(m_iterMaxField.getText().trim())) {
            return;
        }
        
        setErrorMessage(null);
        setMessage(Messages.TestResultViewPreferencePageTitle, NONE);
        setValid(true);
    }

    /**
     * Checks whether an input is a positive integer
     * @param text the text
     * @return whether it is a positive integer
     */
    private boolean checkValidNumber(String text) {
        try {
            int num = Integer.parseInt(text);
            if (num < 1) {
                setErrorMessage(Messages
                        .TestResultViewPreferencePageNegativeNumber);
                setValid(false);
                return false;
            }
        } catch (NumberFormatException e) {
            setErrorMessage(Messages
                    .TestResultViewPreferencePageInvalidNumber);
            setValid(false);
            return false;
        }
        return true;
    }
    
    /**
     * @param parent The composite.
     * @return a new m_text field
     */
    private Text newTextField(Composite parent) {
        final Text textField = new Text(parent, SWT.BORDER);
        GridData textGrid = new GridData(GridData.FILL, GridData.CENTER, 
            true, false, 3, 1);
        textGrid.widthHint = Dialog.convertWidthInCharsToPixels(
                LayoutUtil.getFontMetrics(textField), LayoutUtil.WIDTH);
        textField.setLayoutData(textGrid);
        return textField;
    }

    /**
     * @param parent parent of this textfield
     */
    private void createOpenResultView(Composite parent) {
        // create Widget
        m_openResultView = new Button(parent, SWT.CHECK);
        m_openResultView.setText(Messages
                .TestResultViewPreferencePageOpenResultView);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = false;
        m_openResultView.setLayoutData(gridData);

        m_trackResults = new Button(parent, SWT.CHECK);
        m_trackResults.setText(Messages
                .TestResultViewPreferencePageTrackResults);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = false;
        m_trackResults.setLayoutData(gridData);

        m_autoScreenshots = new Button(parent, SWT.CHECK);
        m_autoScreenshots.setText(Messages
                .TestResultViewPreferencePageAutoScreenshots);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = false;
        m_autoScreenshots.setLayoutData(gridData);
    }

    /**
     * @param parent parent of this textfield
     */
    private void createGenerateReport(Composite parent) {
        // create Widget
        m_generateReport = new Button(parent, SWT.CHECK);
        m_generateReport.setText(Messages
                .TestResultViewPreferencePageGenerateReport);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        m_generateReport.setLayoutData(gridData);
        m_generateReport.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                enableReportStyleCombo();
                enableMonitoringReportGeneration();
                enableLogFileBrowser();
                checkCompleteness();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing here
            }
        });
        
        createMonitoringReportButton(parent);
        
        createReportStyleCombo(parent);
        Label label = new Label(parent, SWT.NONE);
        label.setFont(LayoutUtil.BOLD_TAHOMA);
        label.setText(Messages
                .TestResultViewPreferencePagePathText);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        label.setLayoutData(gridData);
        m_path = newTextField(parent);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_path);
        m_path.setLayoutData(gridData);
        m_browser = new Button(parent, SWT.NONE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_browser.setLayoutData(gridData);
        m_browser.setText(Messages.TestResultViewPreferencePageBrowse);
        m_path.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }
            
        });
        m_browser.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog directoryDialog = 
                    new DirectoryDialog(getShell(), 
                            SWT.APPLICATION_MODAL);
                String directory;
                directoryDialog.setMessage(Messages
                        .TestResultViewPreferencePageDirSelector);
                directoryDialog.setFilterPath(
                    getPath());
                directory = directoryDialog.open();
                if (directory != null) {
                    setPath(directory);
                }
                checkCompleteness();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing
            }
        });
    }

    /**
     * Create view to decide automatic monitoring report generation.
     * @param parent The parent <code>Composite</code>
     */
    private void createMonitoringReportButton(Composite parent) {
        GridData gridData;
        m_generateMonitoringReport = new Button(parent, SWT.CHECK);
        m_generateMonitoringReport.setText(Messages
                .TestResultViewPreferencePageGenerateMonitoringReport);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        m_generateMonitoringReport.setLayoutData(gridData);
    }
    

    /**
     * Create the combo box that determines the style of report generation. 
    *
     * @param parent The parent <code>Composite</code>
     */
    private void createReportStyleCombo(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setFont(LayoutUtil.BOLD_TAHOMA);
        label.setText(Messages.TestResultViewPreferencePageStyleText);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        label.setLayoutData(gridData);

        m_reportStyle = new I18nStringCombo(
            parent, SWT.NONE, REPORT_STYLE_BASIC_KEY, 
            Arrays.asList(REPORT_STYLES), false, false); 
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_reportStyle);
        m_reportStyle.setLayoutData(gridData);
    }
    

    /**
     * Initializes the preference page
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setDescription(Messages.TestResultViewPreferencePageDescription);
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void performDefaults() {
        // sets widgets to default values
        m_openResultView.setSelection(getDefaultPrefsBool(Constants.
            OPENRESULTVIEW_KEY));
        m_generateReport.setSelection(getDefaultPrefsBool(
            Constants.GENERATEREPORT_KEY));
        m_generateMonitoringReport.setSelection(
                getDefaultPrefsBool(Constants.GENERATE_MONITORING_REPORT_KEY));
        m_trackResults.setSelection(getDefaultPrefsBool(
            Constants.TRACKRESULTS_KEY));
        m_autoScreenshots.setSelection(getDefaultPrefsBool(
                Constants.AUTO_SCREENSHOT_KEY));
        m_testExecRememberValue = getPreferenceStore().getDefaultBoolean(
                Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY);
        m_reportStyle.setText(getDefaultPrefsString(
            Constants.REPORTGENERATORSTYLE_KEY));
        m_reportStyle.setEnabled(m_generateReport.getSelection());
        m_path.setText(getDefaultPrefsString(Constants.RESULTPATH_KEY));
        m_generateMonitoringReport.setEnabled(m_generateReport.getSelection());
        m_path.setEnabled(m_generateReport.getSelection());
        m_numberOfDays.setText(getDefaultPrefsString(
                Constants.MAX_NUMBER_OF_DAYS_KEY));
        m_iterMaxField.setText(getDefaultPrefsString(
                Constants.MAX_ITERATION_KEY));
        setErrorMessage(null);
        setMessage(Messages.TestResultViewPreferencePageTitle,
            NONE); 
        setValid(true);
    }
    
    /**
     * @return default value
     * @param key
     *            preference key
     */
    private boolean getDefaultPrefsBool(String key) {
        return getPreferenceStore().getDefaultBoolean(key);
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void initPreferences() {
        // sets widgets to default values
        m_openResultView.setSelection(getPreferenceStore().getBoolean(
            Constants.OPENRESULTVIEW_KEY));
        m_generateReport.setSelection(getPreferenceStore().getBoolean(
            Constants.GENERATEREPORT_KEY));
        m_generateMonitoringReport.setSelection(getPreferenceStore()
                .getBoolean(Constants.GENERATE_MONITORING_REPORT_KEY));
        m_trackResults.setSelection(getPreferenceStore().getBoolean(
                Constants.TRACKRESULTS_KEY));
        m_autoScreenshots.setSelection(getPreferenceStore().getBoolean(
                Constants.AUTO_SCREENSHOT_KEY));
        m_reportStyle.setText(getPreferenceStore().getString(
            Constants.REPORTGENERATORSTYLE_KEY));
        m_path.setText(getPreferenceStore().getString(
                Constants.RESULTPATH_KEY));
        m_numberOfDays.setText(getPreferenceStore().getString(
                Constants.MAX_NUMBER_OF_DAYS_KEY));
        m_iterMaxField.setText(getPreferenceStore().getString(
                Constants.MAX_ITERATION_KEY));
        enableReportStyleCombo();
        enableLogFileBrowser();
        enableMonitoringReportGeneration();

    }


    /**
     * @return default value
     * @param key preference key
     */
    private String getDefaultPrefsString(String key) {
        return getPreferenceStore().getDefaultString(key);
    }

    /**
     * Method declared on IPreferencePage. 
     * 
     * @return performOK
     */
    public boolean performOk() {
        // read preferences from widgets
        // set preferences in store
        boolean openResult = m_openResultView.getSelection();
        boolean generateReport = m_generateReport.getSelection();
        boolean generateMonitoringReport = m_generateMonitoringReport
                .getSelection();
        boolean trackResults = m_trackResults.getSelection();
        boolean autoScreenshots = m_autoScreenshots.getSelection();
        // set preferences in store
        getPreferenceStore().setValue(
            Constants.OPENRESULTVIEW_KEY, openResult);
        getPreferenceStore().setValue(
            Constants.GENERATEREPORT_KEY, generateReport);
        getPreferenceStore().setValue(Constants.GENERATE_MONITORING_REPORT_KEY,
                generateMonitoringReport);
        getPreferenceStore().setValue(
            Constants.TRACKRESULTS_KEY, trackResults);
        getPreferenceStore().setValue(
                Constants.AUTO_SCREENSHOT_KEY, autoScreenshots);
        getPreferenceStore().setValue(
            Constants.REPORTGENERATORSTYLE_KEY, m_reportStyle.getText());
        getPreferenceStore().setValue(
            Constants.RESULTPATH_KEY, m_path.getText());
        getPreferenceStore().setValue(Constants.MAX_NUMBER_OF_DAYS_KEY,
                m_numberOfDays.getText());
        getPreferenceStore().setValue(Constants.MAX_ITERATION_KEY,
                m_iterMaxField.getText());
        getPreferenceStore().setValue(
                Constants.TEST_EXECUTION_RELEVANT_REMEMBER_KEY,
                m_testExecRememberValue);
        getPreferenceStore().setValue(Constants.OPEN_LOGVIEW_KEY,
                m_openLogValue);
        removeListener();
        return super.performOk();
    }

    /**
     * Can be used to implement any special processing, such as notification, if
     * required. Logic to actually change preference values should be in the
     * <code>performOk</code> method as that method will also be triggered
     * when the Apply push button is selected.
     * <p>
     * If others are interested in tracking preference changes they can use the
     * <code>addPropertyChangeListener</code> method available for for an
     * <code>IPreferenceStore</code> or <code>Preferences</code>.
     * </p>
     * 
     * {@inheritDoc}
     * {@inheritDoc}
     * {@inheritDoc}
     * {@inheritDoc}
     */
    protected void performApply() {
        performOk();
    }

    /**
     * Set the same enabled state to monitoring report generation like the
     * html/xml generation.
     */
    protected void enableMonitoringReportGeneration() {
        m_generateMonitoringReport
                .setEnabled(m_generateReport.getSelection());
    }
        
    /**
     * 
     */
    protected void enableLogFileBrowser() {
        m_path.setEnabled(m_generateReport.getSelection());
        m_browser.setEnabled(m_generateReport.getSelection());
    }

    /**
     * 
     */
    protected void enableReportStyleCombo() {
        m_reportStyle.setEnabled(m_generateReport.getSelection());
    }
    
    /**
     * @return Returns the path.
     */
    protected String getPath() {
        return m_path.getText();
    }

    /**
     * @param path The path to set.
     */
    protected void setPath(String path) {
        m_path.setText(path);
    }
    
    /**
     * @param composite The parent composite.
     */
    private void createRememberGroup(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.TestResultViewPreferencePageOpenLogView);
        RowLayout layout = new RowLayout();
        group.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        m_openLogButton = new Button(group, SWT.RADIO);
        m_openLogButton.setText(Messages.PrefPageBasicAlways);
        m_openLog1Button = new Button(group, SWT.RADIO);
        m_openLog1Button.setText(Messages.PrefPageBasicNever);
        m_openLog2Button = new Button(group, SWT.RADIO);
        m_openLog2Button.setText(Messages.PrefPageBasicPrompt);
        
        m_openLogValue =
                getPreferenceStore().getInt(Constants.OPEN_LOGVIEW_KEY);
        switch (m_openLogValue) {
            case Constants.UTILS_YES:
                m_openLogButton.setSelection(true);
                break;
            case Constants.UTILS_NO:
                m_openLog1Button.setSelection(true);
                break;
            default:
                m_openLog2Button.setSelection(true);
                break;
        }
    }
    /**
     * adds listeners to widgets
     */
    private void addListener() {
        m_openLogButton.addSelectionListener(m_logButtonListener);
        m_openLog1Button.addSelectionListener(m_logButtonListener);
        m_openLog2Button.addSelectionListener(m_logButtonListener);
    }
    
    /**
     * removes listener from widgets
     */
    private void removeListener() {
        m_openLogButton.removeSelectionListener(m_logButtonListener);
        m_openLog1Button.removeSelectionListener(m_logButtonListener);
        m_openLog2Button.removeSelectionListener(m_logButtonListener);
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
            if (o == m_openLogButton) {
                m_openLogValue = Constants.UTILS_YES;
                return;
            } else if (o == m_openLog1Button) {
                m_openLogValue = Constants.UTILS_NO;
                return;
            } else if (o == m_openLog2Button) {
                m_openLogValue = Constants.UTILS_PROMPT;
                return;
            }
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                    + StringConstants.LEFT_PARENTHESIS + o 
                    + StringConstants.RIGHT_PARENTHESIS + StringConstants.DOT);
        }
    }
}