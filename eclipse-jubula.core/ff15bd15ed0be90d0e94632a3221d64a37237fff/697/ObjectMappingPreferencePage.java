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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.core.constants.InputCodeHelper;
import org.eclipse.jubula.client.core.constants.InputCodeHelper.UserInput;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.preferences.utils.InputComboUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;



/**
 * @author BREDEX GmbH
 * @created 10.01.2005
 */
public class ObjectMappingPreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage {
    /** 4 column */
    private static final int NUM_COLUMNS = 4;

    /** 10 horizontal spaces */
    private static final int HORIZONTAL_SPACING_10 = 10;

    /** 10 vertical spaces */
    private static final int VERTICAL_SPACING_10 = 10;

    /** show container count */
    private Button m_showContainerCount = null;
    
    /** combo for mapping keyboard shortcut */
    private Combo m_mappingModifier = null;

    /** combo for mapping keyboard shortcut */
    private Combo m_mappingWithParentsModifier = null;

    /** combo for mapping keyboard shortcut */
    private DirectCombo<UserInput> m_mappingKey = null;

    /** combo for mapping keyboard shortcut */
    private DirectCombo<UserInput> m_mappingWithParentsKey = null;
    
    /**
     * Default Constructor
     *  
     */
    public ObjectMappingPreferencePage() { 
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
        final ScrolledComposite scrollComposite = 
            new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        final Composite composite = new Composite(scrollComposite, SWT.NONE);

        /** Define laout rules for widget placement */
        compositeGridData(composite, 1);
        // add widgets to composite
        createShowContainerCount(composite);
        createShortCutsArea(composite);
        // context sensitive help
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.PREFPAGE_OBJECT_MAP);
        initPreferences();
        scrollComposite.setContent(composite);
        scrollComposite.setExpandHorizontal(true);
        scrollComposite.setExpandVertical(true);
        scrollComposite.setMinSize(composite.computeSize(
                SWT.DEFAULT, SWT.DEFAULT));
        scrollComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                scrollComposite.setMinSize(composite.computeSize(
                        SWT.DEFAULT, SWT.DEFAULT));
            }
        });
        return scrollComposite;

    }

    /**
     * @param composite
     *            The composite.
     * @param numberOfColumns
     *            the number of columns to use
     */
    private void compositeGridData(Composite composite, int numberOfColumns) {
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numberOfColumns;
        compositeLayout.horizontalSpacing = HORIZONTAL_SPACING_10;
        compositeLayout.verticalSpacing = VERTICAL_SPACING_10;
        compositeLayout.marginHeight = 10;
        compositeLayout.marginWidth = 10;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = true;
        compositeData.grabExcessVerticalSpace = false;
        composite.setLayoutData(compositeData);
    }

    /**
     * @param parent parent of this textfield
     */
    private void createShowContainerCount(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        compositeGridData(composite, NUM_COLUMNS);
        // create Widget
        m_showContainerCount = new Button(composite, SWT.CHECK);
        m_showContainerCount.setText(
                Messages.ObjectMappingPreferencePageShowContainerCount);
        GridData data2 = new GridData();
        data2.horizontalSpan = 4;
        m_showContainerCount.setLayoutData(data2);
    }
    
    /**
     * @param parent parent of this Combo
     */
    private void createShortCutsArea(Composite parent) {
        InputCodeHelper keys = InputCodeHelper.getInstance();
        
        Composite composite = new Composite(parent, SWT.NONE);
        compositeGridData(composite, NUM_COLUMNS);
        
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.ObjectMappingPreferencePageCollectShortcut);
        label.setFont(LayoutUtil.BOLD_TAHOMA);
        GridData data2 = new GridData();
        data2.horizontalSpan = 4;
        label.setLayoutData(data2);
        
        label = new Label(composite, SWT.NONE);
        m_mappingModifier = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        m_mappingModifier.setItems(keys.getModifierString());
        label = new Label(composite, SWT.NONE);
        label.setText(" + "); //$NON-NLS-1$
        m_mappingKey = InputComboUtil.createInputCombo(
                composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        label = new Label(composite, SWT.NONE);
        label.setText(Messages
                .ObjectMappingPreferencePageCollectWithParentsShortcut);
        label.setFont(LayoutUtil.BOLD_TAHOMA);
        label.setLayoutData(data2);
        
        label = new Label(composite, SWT.NONE);
        m_mappingWithParentsModifier = new Combo(composite,
                SWT.DROP_DOWN | SWT.READ_ONLY);
        m_mappingWithParentsModifier.setItems(keys.getModifierString());
        label = new Label(composite, SWT.NONE);
        label.setText(" + "); //$NON-NLS-1$
        m_mappingWithParentsKey = InputComboUtil.createInputCombo(
                composite, SWT.DROP_DOWN | SWT.READ_ONLY);
    }

    /**
     * Initializes the preference page
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setDescription(Messages.ObjectMappingPreferencePageDescription);
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void performDefaults() {
        // sets widgets to default values
        m_showContainerCount.setSelection(
                getDefaultPrefsBool(Constants.SHOWCHILDCOUNT_KEY));
        m_mappingModifier.select(InputCodeHelper.getInstance()
                .getIndexOfModifier(
                        getDefaultPrefsInt(Constants.MAPPING_MOD_KEY)));
        m_mappingWithParentsModifier.select(InputCodeHelper.getInstance()
                .getIndexOfModifier(
                        getDefaultPrefsInt(
                                Constants.MAPPING_WITH_PARENTS_MOD_KEY)));
        InputComboUtil.setSelectedInput(m_mappingKey,
                getDefaultPrefsInt(Constants.MAPPING_TRIGGER_KEY),
                getDefaultPrefsInt(Constants.MAPPING_TRIGGER_TYPE_KEY));
        InputComboUtil.setSelectedInput(m_mappingWithParentsKey,
                getDefaultPrefsInt(Constants.MAPPING_WITH_PARENTS_TRIGGER_KEY),
                getDefaultPrefsInt(Constants.
                        MAPPING_WITH_PARENTS_TRIGGER_TYPE_KEY));
    }

    /**
     * Performs special processing when this page's Restore Defaults button has
     * been pressed. Sets the contents of the nameEntry field to be the default
     */
    protected void initPreferences() {
        // sets widgets to default values
        m_showContainerCount.setSelection(getPreferenceStore().
            getBoolean(Constants.SHOWCHILDCOUNT_KEY));
        m_mappingModifier.select(
            InputCodeHelper.getInstance().getIndexOfModifier(
                getPreferenceStore().getInt(Constants.MAPPING_MOD_KEY)));
        m_mappingWithParentsModifier.select(
                InputCodeHelper.getInstance().getIndexOfModifier(
                    getPreferenceStore().getInt(
                            Constants.MAPPING_WITH_PARENTS_MOD_KEY)));
        InputComboUtil.setSelectedInput(m_mappingKey, 
            getPreferenceStore().getInt(Constants.MAPPING_TRIGGER_KEY), 
            getPreferenceStore().getInt(Constants.MAPPING_TRIGGER_TYPE_KEY));
        InputComboUtil.setSelectedInput(m_mappingWithParentsKey, 
            getPreferenceStore().getInt(
                    Constants.MAPPING_WITH_PARENTS_TRIGGER_KEY), 
            getPreferenceStore().getInt(
                    Constants.MAPPING_WITH_PARENTS_TRIGGER_TYPE_KEY));
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
     * @return default value
     * @param key preference key
     */
    private int getDefaultPrefsInt(String key) {
        return getPreferenceStore().getDefaultInt(key);
    }
    
    /**
     * Method declared on IPreferencePage.
     * 
     * @return performOK
     */
    public boolean performOk() {
        // read preferences from widgets
        boolean showCount = m_showContainerCount.getSelection();

        // set preferences in store
        getPreferenceStore().setValue(Constants.SHOWCHILDCOUNT_KEY, showCount);
        getPreferenceStore().setValue(
                Constants.MAPPING_MOD_KEY,
                InputCodeHelper.getInstance().getModifier()[
                    m_mappingModifier.getSelectionIndex()]);
        getPreferenceStore().setValue(
                Constants.MAPPING_WITH_PARENTS_MOD_KEY,
                InputCodeHelper.getInstance().getModifier()[
                    m_mappingWithParentsModifier.getSelectionIndex()]);
        
        InputComboUtil.setPrefCode(m_mappingKey, getPreferenceStore(),
                Constants.MAPPING_TRIGGER_KEY);
        InputComboUtil.setPrefType(m_mappingKey, getPreferenceStore(),
                Constants.MAPPING_TRIGGER_TYPE_KEY);
        
        InputComboUtil.setPrefCode(m_mappingWithParentsKey,
                getPreferenceStore(),
                Constants.MAPPING_WITH_PARENTS_TRIGGER_KEY);
        InputComboUtil.setPrefType(m_mappingWithParentsKey,
                getPreferenceStore(),
                Constants.MAPPING_WITH_PARENTS_TRIGGER_TYPE_KEY);
        return super.performOk();
    }
}
