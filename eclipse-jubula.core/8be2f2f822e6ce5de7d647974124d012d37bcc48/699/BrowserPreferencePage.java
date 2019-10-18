/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for the editors
 * @author BREDEX GmbH
 */
public class BrowserPreferencePage 
        extends PreferencePage implements IWorkbenchPreferencePage {

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
    
    
    /** The preference store to hold the existing preference values. */
    private IPreferenceStore m_store = Plugin.getDefault().getPreferenceStore();

    /** The button for enabling the background color filter */
    private Button m_filterColoringEnableButton;

    /** The color picker for the background color */
    private ColorFieldEditor m_colorFieldEditor;

    /** The background color group */
    private Composite m_colorComp;

    /**
     * Default Constructor
     * 
     */
    public BrowserPreferencePage() {
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {
        setDescription(Messages.BrowserPreferencePageDescription);
    }

    @Override
    protected Control createContents(Composite parent) {
        /** Add layer to parent widget */
        Composite composite = new Composite(parent, SWT.NONE);

        compositeGridData(composite);
        
        createFilterBackgroundColoringSettings(composite);

        return composite;
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
     * Creates the background coloring settings group
     * @param composite the composite to create the group in
     */
    private void createFilterBackgroundColoringSettings(
            final Composite composite) {
        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.BrowserPreferencePageFilterColoringGroup);
        RowLayout layout = new RowLayout();
        layout.type = SWT.VERTICAL;
        group.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        group.setLayoutData(layoutData);
        
        Label label = new Label(group, SWT.NONE);
        label.setText(Messages.BrowserPreferencePageFilterColoringDescription);
        
        new Label(group, SWT.NONE);
        
        m_filterColoringEnableButton = new Button(group, SWT.CHECK);
        m_filterColoringEnableButton.setText(
                Messages.BrowserPreferencePageFilterColoringButton);
        m_filterColoringEnableButton.setSelection(
                m_store.getBoolean(Constants.BACKGROUND_COLORING_KEY));
        
        m_colorComp = new Composite(group, SWT.NONE);
        
        m_filterColoringEnableButton.addSelectionListener(
                new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        updateWidgets();
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        // empty
                    }
                });
        
        
        m_colorComp.setLayout(new RowLayout());
        m_colorFieldEditor = new ColorFieldEditor(
            Constants.BACKGROUND_COLOR_KEY, 
            Messages.BrowserPreferencePageFilterColoringColorFieldLabel,
            m_colorComp);
        m_colorFieldEditor.getColorSelector().setColorValue(
            Utils.intToRgb(m_store.getInt(Constants.BACKGROUND_COLOR_KEY)));
        updateWidgets();
    }
    
    /**
     * Update the widgets, for example enable or disable the interval spinner.
     */
    private void updateWidgets() {
        m_colorFieldEditor.setEnabled(
            m_filterColoringEnableButton.getSelection(), m_colorComp);
    }
    
    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void performDefaults() {
        m_filterColoringEnableButton.setSelection(
            m_store.getDefaultBoolean(Constants.BACKGROUND_COLORING_KEY));
        m_colorFieldEditor.getColorSelector().setColorValue(
            Utils.intToRgb(
                m_store.getDefaultInt(Constants.BACKGROUND_COLOR_KEY)));
        updateWidgets();
    }

    /**
     * Method declared on IPreferencePage.
     * 
     * @return performOK
     */
    public boolean performOk() {
        m_store.setValue(Constants.BACKGROUND_COLORING_KEY,
            m_filterColoringEnableButton.getSelection());
        m_store.setValue(Constants.BACKGROUND_COLOR_KEY,
            Utils.rgbToInt(
                m_colorFieldEditor.getColorSelector().getColorValue()));
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
