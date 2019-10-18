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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author BREDEX GmbH
 * @created 10.01.2005
 */
public class EditorPreferencePage extends PreferencePage
        implements IWorkbenchPreferencePage {

    /** 1 column */
    private static final int NUM_COLUMNS = 1;

    /** 10 horizontal spaces */
    private static final int HORIZONTAL_SPACING_10 = 10;

    /** 10 vertical spaces */
    private static final int VERTICAL_SPACING_10 = 10;

    /** margin height = 10 */
    private static final int MARGIN_HEIGHT_10 = 10;

    /** margin width = 10 */
    private static final int MARGIN_WIDTH_10 = 10;

    /** button for insert node in testCaseEditor */
    private Button m_nodeInsertButton;
    /** button for add node in testCaseEditor */
    private Button m_nodeAddButton;
    /** button for counters */
    private Button m_showCountersButton;
    /** button for save reminder enable */
    private Button m_reminderEnableButton;
    /** spinner for save reminder interval */
    private Spinner m_reminderIntervalSpinner;

    /** The preferece store to hold the existing preference values. */
    private IPreferenceStore m_store = Plugin.getDefault().getPreferenceStore();

    /**
     * Default Constructor
     * 
     */
    public EditorPreferencePage() { //
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
        Composite composite = new Composite(parent, SWT.NONE);

        /** Define layout rules for widget placement */
        compositeGridData(composite);

        createShowCountersButton(composite);
        createInsertNodeAfterSelectedNodeButton(composite);
        createSaveReminderSettings(composite);

        updateWidgets();

        /** return the widget used as the base for the user interface */
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.EDITOR_PREF_PAGE);
        return composite;
    }

    /**
     * @param composite
     *            the parent composite
     */
    private void createShowCountersButton(Composite composite) {
        m_showCountersButton = new Button(composite, SWT.CHECK);
        m_showCountersButton
                .setText(Messages.EditorPreferencePageShowCountersCheckBox);
        m_showCountersButton.setSelection(Plugin.getDefault()
                .getPreferenceStore().getBoolean(Constants.SHOWCOUNTERS_KEY));
    }

    /**
     * @param composite
     *            the parent composite
     */
    private void createInsertNodeAfterSelectedNodeButton(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.PrefPageBasicNodeInsertionGroup);
        RowLayout layout = new RowLayout();
        layout.type = SWT.VERTICAL;
        group.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        Label label = new Label(group, SWT.NONE);
        label.setText(Messages.EditorPreferencePageAddPositionText);
        new Label(group, SWT.NONE);
        m_nodeAddButton = new Button(group, SWT.RADIO);
        m_nodeAddButton.setText(Messages.PrefPageBasicAddNewNode);
        m_nodeAddButton
                .setSelection(!m_store.getBoolean(Constants.NODE_INSERT_KEY));
        m_nodeInsertButton = new Button(group, SWT.RADIO);
        m_nodeInsertButton.setText(Messages.PrefPageBasicInsertNewNode);
        m_nodeInsertButton
                .setSelection(m_store.getBoolean(Constants.NODE_INSERT_KEY));
    }

    /**
     * @param composite
     *            the parent composite
     */
    private void createSaveReminderSettings(Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.PrefPageBasicSaveReminderGroup);
        RowLayout layout = new RowLayout();
        layout.type = SWT.VERTICAL;
        group.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        Label label = new Label(group, SWT.NONE);
        label.setText(Messages.EditorPreferencePageSaveReminderText);
        new Label(group, SWT.NONE);
        m_reminderEnableButton = new Button(group, SWT.CHECK);
        m_reminderEnableButton
                .setText(Messages.PrefPageBasicEnableSaveReminder);
        m_reminderEnableButton.setSelection(
                m_store.getBoolean(Constants.SAVE_REMINDER_ENABLE_KEY));
        m_reminderEnableButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateWidgets();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing
            }
        });
        Composite c = new Composite(group, SWT.NONE);
        c.setLayout(new RowLayout());
        Label l1 = new Label(c, SWT.NONE);
        l1.setText(Messages.PrefPageBasicReminderInterval1);
        m_reminderIntervalSpinner = new Spinner(c, SWT.BORDER);
        m_reminderIntervalSpinner.setMinimum(1);
        m_reminderIntervalSpinner.setMaximum(60);
        m_reminderIntervalSpinner.setSelection(
                m_store.getInt(Constants.SAVE_REMINDER_INTERVAL_KEY));
        m_reminderIntervalSpinner.setIncrement(1);
        Label l2 = new Label(c, SWT.NONE);
        l2.setText(Messages.PrefPageBasicReminderInterval2);
    }

    /**
     * Update the widgets, for example enable or disable the interval spinner.
     */
    private void updateWidgets() {
        m_reminderIntervalSpinner
                .setEnabled(m_reminderEnableButton.getSelection());
    }

    /**
     * @param composite
     *            The composite.
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
        composite.setLayoutData(compositeData);
    }

    /**
     * Initializes the preference page
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setDescription(Messages.EditorPreferencePageDescription);
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void performDefaults() {
        m_showCountersButton.setSelection(
                m_store.getDefaultBoolean(Constants.SHOWCOUNTERS_KEY));
        m_nodeInsertButton.setSelection(
                m_store.getDefaultBoolean(Constants.NODE_INSERT_KEY));
        m_nodeAddButton.setSelection(
                !m_store.getDefaultBoolean(Constants.NODE_INSERT_KEY));
        m_reminderEnableButton.setSelection(
                m_store.getDefaultBoolean(Constants.SAVE_REMINDER_ENABLE_KEY));
        m_reminderIntervalSpinner.setSelection(
                m_store.getDefaultInt(Constants.SAVE_REMINDER_INTERVAL_KEY));
        updateWidgets();
    }

    /**
     * Method declared on IPreferencePage.
     * 
     * @return performOK
     */
    public boolean performOk() {
        m_store.setValue(Constants.SHOWCOUNTERS_KEY,
                m_showCountersButton.getSelection());
        m_store.setValue(Constants.NODE_INSERT_KEY,
                m_nodeInsertButton.getSelection());
        m_store.setValue(Constants.SAVE_REMINDER_ENABLE_KEY,
                m_reminderEnableButton.getSelection());
        m_store.setValue(Constants.SAVE_REMINDER_INTERVAL_KEY,
                m_reminderIntervalSpinner.getSelection());
        return super.performOk();
    }

    /**
     * Can be used to implement any special processing, such as notification, if
     * required. Logic to actually change preference values should be in the
     * <code>performOk</code> method as that method will also be triggered when
     * the Apply push button is selected.
     * <p>
     * If others are interested in tracking preference changes they can use the
     * <code>addPropertyChangeListener</code> method available for for an
     * <code>IPreferenceStore</code> or <code>Preferences</code>.
     * </p>
     */
    protected void performApply() {
        super.performApply();
    }

}
