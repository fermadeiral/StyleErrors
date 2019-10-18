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
package org.eclipse.jubula.client.ui.dialogs;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.ui.databinding.DialogStatusMessageListener;
import org.eclipse.jubula.client.ui.databinding.StatusToEnablementConverter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Jan 12, 2009
 */
public abstract class AbstractValidatedDialog extends TitleAreaDialog {

    /** data binding context containing all bindings that require validation */
    private DataBindingContext m_validationContext;
    
    /**
     * Constructor
     * 
     * @param parentShell
     *            the parent SWT shell
     */
    public AbstractValidatedDialog(Shell parentShell) {
        super(parentShell);
        m_validationContext = new DataBindingContext();
    }

    /**
     * 
     * @return the data binding context that must contain all bindings
     *         that have validation relevant to the dialog.
     */
    protected DataBindingContext getValidationContext() {
        return m_validationContext;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        IObservableValue<?> okButtonEnablement = 
            WidgetProperties.enabled()
                .observe(getButton(IDialogConstants.OK_ID));
        AggregateValidationStatus validationStatus = 
            new AggregateValidationStatus(
                    m_validationContext.getValidationStatusProviders(),
                    AggregateValidationStatus.MAX_SEVERITY);
        validationStatus.addValueChangeListener(
                new DialogStatusMessageListener(this));
        new DataBindingContext().bindValue(
                okButtonEnablement, validationStatus, null, 
                new UpdateValueStrategy().setConverter(
                        new StatusToEnablementConverter()));
    }
    
}
