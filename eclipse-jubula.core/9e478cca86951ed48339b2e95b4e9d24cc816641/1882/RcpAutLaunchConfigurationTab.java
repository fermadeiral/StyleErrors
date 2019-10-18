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
package org.eclipse.jubula.launch.rcp.ui;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.launch.rcp.RcpAutLaunchConfigurationConstants;
import org.eclipse.jubula.launch.rcp.i18n.Messages;
import org.eclipse.jubula.launch.ui.tab.AutLaunchConfigurationTab;
import org.eclipse.jubula.tools.internal.constants.AutEnvironmentConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launch Configuration tab for launching an Eclipse RCP application with 
 * support for automated testing (as an AUT).
 * 
 * @author BREDEX GmbH
 * @created 20.07.2011
 */
public class RcpAutLaunchConfigurationTab extends AutLaunchConfigurationTab {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(RcpAutLaunchConfigurationTab.class);
    
    /** 
     * text field for Keyboard Layout
     * 
     * @see RcpAutLaunchConfigurationConstants#KEYBOARD_LAYOUT_KEY
     */
    private Text m_keyboardLayoutText;
    
    /**
     * boolean whether component names should be generated
     */
    private Button m_generateNamesFlag;

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        composite.setLayout(new GridLayout(1, false));
        super.createControl(composite);
        Composite additionalComposite = new Composite(composite, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
                additionalComposite);
        additionalComposite.setLayout(new GridLayout(2, false));
        UIComponentHelper.createLabel(additionalComposite, 
                Messages.LaunchTab_KeyboardLayoutLabel, SWT.NONE);
        m_keyboardLayoutText = new Text(additionalComposite, SWT.BORDER);

        GridDataFactory.fillDefaults().grab(true, false).applyTo(
                m_keyboardLayoutText);

        m_keyboardLayoutText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
        });
        
        Composite workaroundComposite =
                new Composite(additionalComposite, SWT.NONE);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(
                workaroundComposite);
        workaroundComposite.setLayout(new GridLayout(2, false));
        
        Label label = new Label(workaroundComposite, SWT.NONE);
        label.setText(
                org.eclipse.jubula.client.ui.rcp.i18n.Messages.
                AUTPropertiesDialogGenerateNames);
        ControlDecorator.createInfo(label,
                I18n.getString("AUTPropertiesDialog.generateNamesDescription"), //$NON-NLS-1$
                false);
        m_generateNamesFlag = new Button(additionalComposite, SWT.CHECK);
        
        GridDataFactory.fillDefaults().grab(false, false).applyTo(
                m_generateNamesFlag);
        
        m_generateNamesFlag.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                setDirty(true);
                updateLaunchConfigurationDialog();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        setControl(composite);
    }
    
    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        super.initializeFrom(configuration);
        try {
            m_keyboardLayoutText.setText(configuration.getAttribute(
                    RcpAutLaunchConfigurationConstants.KEYBOARD_LAYOUT_KEY, 
                    Locale.getDefault().toString()));
        } catch (CoreException ce) {
            LOG.error("An error occurred while initializing Keyboard Layout text field.", ce); //$NON-NLS-1$
        }
        try {
            m_generateNamesFlag.setSelection(configuration.getAttribute(
                    AutEnvironmentConstants.GENERATE_COMPONENT_NAMES,
                    true));
        } catch (CoreException ce) {
            LOG.error("An error occurred while initializing Generate Names check box.", ce); //$NON-NLS-1$
        }
    }
    
    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        configuration.setAttribute(
                RcpAutLaunchConfigurationConstants.KEYBOARD_LAYOUT_KEY, 
                m_keyboardLayoutText.getText());
        configuration.setAttribute(
                AutEnvironmentConstants.GENERATE_COMPONENT_NAMES,
                m_generateNamesFlag.getSelection());
    }
    
    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        super.setDefaults(configuration);
        configuration.setAttribute(
                RcpAutLaunchConfigurationConstants.KEYBOARD_LAYOUT_KEY, 
                Locale.getDefault().toString());
    }
    
    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        setErrorMessage(null);
        boolean isValid = super.isValid(launchConfig);
        if (isValid) {
            try {
                String keyboardLayoutString = launchConfig.getAttribute(
                        RcpAutLaunchConfigurationConstants.KEYBOARD_LAYOUT_KEY, 
                        StringUtils.EMPTY);
                isValid = isKeyboardLayoutValid(keyboardLayoutString);
                if (!isValid) {
                    setErrorMessage(NLS.bind(
                            Messages.LaunchTab_KeyboardLayout_InvalidLocale,
                            keyboardLayoutString));
                }
            } catch (CoreException ce) {
                LOG.error("An error occurred during validation.", ce); //$NON-NLS-1$
                setErrorMessage(
                    Messages.LaunchTab_KeyboardLayout_ErrorDuringValidation);
                return false;
            }
        }
        
        return isValid;
    }

    /**
     * 
     * @param keyboardLayout The string to check.
     * @return <code>true</code> if the given string represents a valid locale.
     *         Otherwise, <code>false</code>. 
     */
    private boolean isKeyboardLayoutValid(String keyboardLayout) {
        try {
            return LocaleUtils.toLocale(keyboardLayout) != null;
        } catch (IllegalArgumentException iae) {
            // did not parse to a valid locale. fall through to return
            // false.
        }
        
        return false;
    }
}
