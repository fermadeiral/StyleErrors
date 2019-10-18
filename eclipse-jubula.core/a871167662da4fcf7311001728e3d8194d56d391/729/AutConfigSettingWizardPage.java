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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDialogStatusListener;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.DialogStatusParameter;
import org.eclipse.jubula.client.ui.rcp.widgets.autconfig.AutConfigComponent;
import org.eclipse.jubula.client.ui.rcp.wizards.ProjectWizard;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.UnexpectedGenericTypeException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 18.05.2005
 */
public class AutConfigSettingWizardPage extends WizardPage 
    implements IDialogStatusListener {
    
    /** The logger */
    private static Logger log = LoggerFactory.getLogger(
        AutConfigSettingWizardPage.class);
    /** the name of the selected aut configuration */
    private IAUTConfigPO m_autConfig;
    /** the autConfigComposite */
    private Composite m_composite;
    
    /** validator for the AUT ID text field */
    private IValidator m_autIdValidator;
    
    /** listener to set buttons enabled */      
    
    /**
     * @param pageName The page name.
     * @param autConfig The new autConfig to create.
     * @param autIdValidator The validator for the AUT ID text field.
     */
    public AutConfigSettingWizardPage(String pageName, 
        IAUTConfigPO autConfig, IValidator autIdValidator) {
        
        super(pageName);
        setPageComplete(false);
        m_autConfig = autConfig;
        m_autIdValidator = autIdValidator;
    }

    /**
     * @param parent The parent composite.
     */
    public void createControl(Composite parent) {
        DataEventDispatcher.getInstance().getDialogStatusListenerMgr()
            .addListener(this);
        ScrolledComposite scroll = new ScrolledComposite(parent, 
                SWT.V_SCROLL | SWT.H_SCROLL);
        Plugin.getHelpSystem().setHelp(parent, 
                ContextHelpIds.AUT_CONFIG_SETTING_WIZARD_PAGE);
        m_composite = new Composite(parent, SWT.NONE);
        scroll.setContent(m_composite);
        scroll.setMinSize(m_composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        setControl(scroll);
    }

    /**
     * Creates the AutConfigComposite.
     */
    private void createAutConfigComposite() {
        try {
            ScrolledComposite scroll = (ScrolledComposite)getControl();
            m_composite = ToolkitSupportBP.getAutConfigComposite(getWizard()
                .getAutMain().getToolkit(), scroll, SWT.V_SCROLL, 
                m_autConfig.getConfigMap(), 
                getWizard().getAutMain().getName());
            final Control oldComposite = scroll.getContent();
            scroll.setContent(m_composite);

            // FIXME zeb This is necessary at the moment because the creator 
            //           of AutConfigComponents is in the ToolkitSupport project, which 
            //           is not aware of model (PO) objects nor of databinding classes 
            //           (IValidator). This dependency issue should be resolved, and
            //           the validator should be set in the constructor, rather than
            //           in a separate setter method.
            if (m_composite instanceof AutConfigComponent) {
                ((AutConfigComponent)m_composite).setAutIdValidator(
                        m_autIdValidator);
            }
            if (oldComposite != null && !oldComposite.isDisposed()) {
                oldComposite.dispose();                
            }
            setPageComplete(false);
        } catch (ToolkitPluginException e) {
            log.error(Messages.NoAUTConfigPageForToolkit + StringConstants.COLON
                + StringConstants.SPACE 
                + String.valueOf(getWizard().getAutMain().getToolkit()), e);
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_NO_AUTCONFIG_DIALOG);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ProjectWizard getWizard() {
        return (ProjectWizard)super.getWizard();
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(boolean visible) {
        if (visible) {
            createAutConfigComposite();
        }
        super.setVisible(visible);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher.getInstance().getDialogStatusListenerMgr()
            .removeListener(this);
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(List< ? extends Object> list) {
        DialogStatusParameter param = (DialogStatusParameter)list.get(0);
        setMessage(param.getMessage(), param.getStatusType());
        setPageComplete(param.getButtonState());
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(
            ContextHelpIds.AUT_CONFIG_SETTING_WIZARD_PAGE);
    }

    /**
     * {@inheritDoc}
     */
    public void checkGenericListElementType(List< ? extends Object> params) 
        throws UnexpectedGenericTypeException {
            
        Validate.noNullElements(params);
        Class type = null;
        int index = 0;
        if (!(params.get(0) instanceof DialogStatusParameter)) {
            type = DialogStatusParameter.class;
        }
        if (type != null) {
            StringBuilder msg = new StringBuilder();
            msg.append(Messages.GenericType)
                .append(StringConstants.SPACE)
                .append(type)
                .append(StringConstants.SPACE)
                .append(Messages.WasExpectedBut)
                .append(StringConstants.SPACE)
                .append(params.get(index).getClass())
                .append(StringConstants.SPACE)
                .append(Messages.WasFound)
                .append(StringConstants.DOT);
            throw new UnexpectedGenericTypeException(msg.toString(),
                    MessageIDs.E_UNEXPECTED_EXCEPTION);
        }
    }
}