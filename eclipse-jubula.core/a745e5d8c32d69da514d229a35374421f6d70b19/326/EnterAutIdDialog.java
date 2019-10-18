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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jubula.client.ui.dialogs.AbstractValidatedDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog for entering the value for an AUT ID.
 *
 * @author BREDEX GmbH
 * @created Jan 20, 2010
 */
public class EnterAutIdDialog extends AbstractValidatedDialog {

    /** observable (bindable) value for AUT ID */
    private WritableValue<String> m_autId;
    
    /** initial value for AUT ID, null if none */
    private String m_initialAutId;

    /** 
     * the project within which the AUT ID will exist
     * this is used to validate that the AUT ID is unique within the project 
     */
    private IValidator m_validator;
    
    /**
     * Constructor
     * 
     * @param parentShell The Shell to use as a parent for the dialog.
     * @param initialValue The initial value for the AUT ID.
     * @param autIdValidator The validator to use for the AUT ID.
     */
    public EnterAutIdDialog(Shell parentShell, 
            String initialValue, IValidator autIdValidator) {
        super(parentShell);
        m_validator = autIdValidator;
        m_initialAutId = initialValue;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.EnterAutIdDialogTitle);
        setMessage(Messages.EnterAutIdDialogMessage);
        getShell().setText(Messages.EnterAutIdDialogTitle);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Composite area = new Composite(parent, SWT.BORDER);
        area.setLayoutData(gridData);
        area.setLayout(new GridLayout(2, false));

        Text autIdField = createAutIdText(area);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        LayoutUtil.addToolTipAndMaxWidth(gridData, autIdField);
        autIdField.setLayoutData(gridData);
        LayoutUtil.setMaxChar(autIdField);
        
        IObservableValue autIdFieldText = 
                WidgetProperties.text(SWT.Modify).observe(autIdField);
        m_autId = WritableValue.withValueType(String.class);
        
        getValidationContext().bindValue(
                autIdFieldText,
                m_autId,
                new UpdateValueStrategy().setAfterGetValidator(m_validator),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
        
        if (m_initialAutId != null) {
            setAutId(m_initialAutId);
        }
        autIdField.selectAll();

        return area;
    }

    /**
     * Creates the label and text field for the component name.
     * 
     * @param area The parent for the created widgets.
     * @return the created text field.
     */
    private Text createAutIdText(Composite area) {
        new Label(area, SWT.NONE).setText(Messages.EnterAutIdDialogAutIdLabel);
        return new Text(area, SWT.SINGLE | SWT.BORDER);
    }

    /**
     * This method must be called from the GUI thread.
     * 
     * @return the value of the AUT ID being modified.
     */
    public String getAutId() {
        return m_autId.getValue();
    }

    /**
     * @param value the value to set
     */
    public void setAutId(String value) {
        m_autId.setValue(value);
    }
}
