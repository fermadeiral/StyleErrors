/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.autagent.preferences.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jubula.client.autagent.Activator;
import org.eclipse.jubula.client.autagent.preferences.PreferenceInitializer;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.databinding.SimpleIntegerToStringConverter;
import org.eclipse.jubula.client.ui.databinding.SimpleStringToIntegerConverter;
import org.eclipse.jubula.client.ui.databinding.validators.StringToPortValidator;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * 
 * @author BREDEX GmbH
 * @created Jun 29, 2011
 */
public class EmbeddedAutAgentPreferencePage extends PreferencePage 
        implements IWorkbenchPreferencePage {

    /** the port number for the embedded AUT Agent */
    private WritableValue m_portNumber;
    
    /** the databinding context for this page */
    private DataBindingContext m_dbc;
    
    /**
     * 
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(
                InstanceScope.INSTANCE, Activator.PLUGIN_ID));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
        
        
        m_dbc = new DataBindingContext();
        
        UIComponentHelper.createLabel(composite, 
                I18n.getString("DatabaseConnection.HostBased.Port"), SWT.NONE); //$NON-NLS-1$
        Text portText = new Text(composite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(portText);
        m_portNumber = new WritableValue(
                getPreferenceStore().getInt(
                        PreferenceInitializer.PREF_EMBEDDED_AGENT_PORT), 
                int.class);

        UpdateValueStrategy portTargetToModelUpdateStrategy =
            new UpdateValueStrategy();
        portTargetToModelUpdateStrategy
            .setConverter(new SimpleStringToIntegerConverter())
            .setAfterGetValidator(new StringToPortValidator(
                    I18n.getString("DatabaseConnection.HostBased.Port"))); //$NON-NLS-1$
        m_dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(portText),
                m_portNumber, portTargetToModelUpdateStrategy,
                new UpdateValueStrategy().setConverter(
                        new SimpleIntegerToStringConverter()));

        PreferencePageSupport.create(this, m_dbc);
        
        // context sensitive help
        PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
                ContextHelpIds.PREFPAGE_EMBEDDED_AGENT);

        return composite;
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(
            PreferenceInitializer.PREF_EMBEDDED_AGENT_PORT, 
            (Integer)m_portNumber.getValue());
        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        // set model objects to default values
        getPreferenceStore().setToDefault(
                PreferenceInitializer.PREF_EMBEDDED_AGENT_PORT);
        m_portNumber.setValue(getPreferenceStore().getInt(
                PreferenceInitializer.PREF_EMBEDDED_AGENT_PORT));

        // update targets based on new model values
        m_dbc.updateTargets();

        super.performDefaults();
    }
    
}
