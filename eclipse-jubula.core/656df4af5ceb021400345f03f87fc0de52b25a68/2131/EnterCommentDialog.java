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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog for entering the comment e.g for a test result summary.
 *
 * @author BREDEX GmbH
 * @created Aug 23, 2010
 */
public class EnterCommentDialog extends AbstractValidatedDialog {
    /** 
     * the validator used for validation of value correctness 
     */
    private IValidator m_validator;

    /** observable (bindable) value for comment title */
    private WritableValue<String> m_commentTitle;

    /**
     * <code>m_initialTitle</code>
     */
    private String m_initialTitle = null;
    

    /**
     * Constructor
     * 
     * @param parentShell
     *            The Shell to use as a parent for the dialog.
     * @param commentValidator
     *            The validator to use for the commentary values
     * @param title
     *            the initial comment title
     */
    public EnterCommentDialog(Shell parentShell, IValidator commentValidator,
        String title) {
        super(parentShell);
        setValidator(commentValidator);
        m_initialTitle = title;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.EnterCommentDialogTitle);
        setMessage(Messages.EnterCommentDialogMessage);
        getShell().setText(Messages.EnterCommentDialogTitle);
        
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        Composite area = new Composite(parent, SWT.BORDER);
        area.setLayoutData(gridData);
        area.setLayout(new GridLayout(2, false));

        createCommentTitleField(area);
        createDialogAdditionalArea(area);
        
        return area;
    }
    
    /**
     * @param area the area to create some additional content on
     */
    protected void createDialogAdditionalArea(Composite area) {
        // currently empty
    }

    /**
     * {@inheritDoc}
     */
    protected Point getInitialSize() {
        Point shellSize = super.getInitialSize();
        return new Point(Math.max(
                convertHorizontalDLUsToPixels(450), shellSize.x),
                Math.max(convertVerticalDLUsToPixels(100),
                        shellSize.y));
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean isResizable() {
        return true;
    }

    /**
     * @param area the parent area
     */
    private void createCommentTitleField(Composite area) {
        GridData gridData;
        Text commentTitleField = createCommentTitleText(area);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        LayoutUtil.addToolTipAndMaxWidth(gridData, commentTitleField);
        commentTitleField.setLayoutData(gridData);
        
        IObservableValue<?> commentTitleFieldText = 
                WidgetProperties.text(SWT.Modify).observe(commentTitleField);
        m_commentTitle = WritableValue.withValueType(String.class);
        getValidationContext().bindValue(
                commentTitleFieldText,
                m_commentTitle,
                new UpdateValueStrategy().setAfterGetValidator(getValidator()),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
        
        if (!StringUtils.isEmpty(m_initialTitle)) {
            m_commentTitle.setValue(m_initialTitle);
        }
        LayoutUtil.setMaxChar(commentTitleField,
                IPersistentObject.MAX_STRING_LENGTH);
        
        commentTitleField.selectAll();
    }
    
    /**
     * @param area The parent for the created widgets.
     * @return the created text field.
     */
    private Text createCommentTitleText(Composite area) {
        new Label(area, SWT.NONE).setText(
                Messages.EnterCommentDialogTitleLabel);
        return new Text(area, SWT.SINGLE | SWT.BORDER);
    }
    
    /**
     * This method must be called from the GUI thread.
     * 
     * @return the comment title
     */
    public String getCommentTitle() {
        return m_commentTitle.getValue();
    }

    /**
     * @return the validator
     */
    protected IValidator getValidator() {
        return m_validator;
    }

    /**
     * @param validator the validator to set
     */
    private void setValidator(IValidator validator) {
        m_validator = validator;
    }
}
