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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.pages;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @author Markus Tiede
 * @created Jul 25, 2011
 */
public class AdditionalInformationPage extends WizardPage {
    /**
     * <code>m_execName</code>
     */
    private Text m_execName;

    /**
     * <code>m_execComment</code>
     */
    private Text m_execComment;
    
    /**
     * <code>m_dbc</code>
     */
    private DataBindingContext m_dbc = new DataBindingContext();

    /**
     * @param pageName
     *            the page name
     */
    public AdditionalInformationPage(String pageName) {
        super(pageName, Messages.ReplaceTCRWizard_additionalInformation_title,
                null);
    }

    /** {@inheritDoc} */
    public void createControl(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        new Label(c, SWT.NONE)
            .setText(
                Messages.ExecTestCaseGUIPropertySourceTestCaseReferenceName);
        m_execName = new Text(c, SWT.NONE);
        m_execName.setLayoutData(GridDataFactory.fillDefaults()
                .grab(true, false).create());

        new Label(c, SWT.NONE)
            .setText(Messages.AbstractGuiNodePropertySourceComment);
        m_execComment = new Text(c, SWT.NONE);
        m_execComment.setLayoutData(GridDataFactory.fillDefaults()
                .grab(true, false).create());
        setControl(c);
    }

    /** {@inheritDoc} */
    public void performHelp() {
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(
            ContextHelpIds.REFACTOR_REPLACE_ADDITIONAL_INFORMATION_WIZARD_PAGE);
    }

    /**
     * @param newExec
     *            the newExec to set
     */
    public void bindNewExec(IExecTestCasePO newExec) {
        m_dbc.dispose();
        m_dbc = new DataBindingContext();
        m_dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(m_execName),
                PojoProperties.value("name").observe(newExec)); //$NON-NLS-1$
        m_dbc.bindValue(WidgetProperties.text(SWT.Modify)
                .observe(m_execComment),
                PojoProperties.value("comment").observe(newExec)); //$NON-NLS-1$
        m_dbc.updateTargets();
    }
}
