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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog for entering the comment e.g for a test result summary.
 *
 * @author BREDEX GmbH
 * @created Aug 23, 2010
 */
public class EnterCommentAndDetailsDialog extends EnterCommentDialog {
    /** observable (bindable) value for comment detail */
    private WritableValue<String> m_commentDetail;

    /**
     * <code>m_initialDetail</code>
     */
    private String m_initialDetail = null;
    
    /**
     * Constructor
     * 
     * @param parentShell
     *            The Shell to use as a parent for the dialog.
     * @param commentValidator
     *            The validator to use for the commentary values
     * @param title
     *            the initial comment title
     * @param detail
     *            the initial comment detail
     */
    public EnterCommentAndDetailsDialog(Shell parentShell,
        IValidator commentValidator, String title, String detail) {
        super(parentShell, commentValidator, title);
        m_initialDetail = detail;
    }

    @Override
    protected void createDialogAdditionalArea(Composite area) {
        GridData gridData;
        Text commentDetailField = createCommentDetailText(area);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        LayoutUtil.addToolTipAndMaxWidth(gridData, commentDetailField);
        commentDetailField.setLayoutData(gridData);

        IObservableValue<?> commentDetailFieldText =
                WidgetProperties.text(SWT.Modify).observe(commentDetailField);
        m_commentDetail = WritableValue.withValueType(String.class);

        getValidationContext().bindValue(commentDetailFieldText,
                m_commentDetail,
                new UpdateValueStrategy().setAfterGetValidator(getValidator()),
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

        if (!StringUtils.isEmpty(m_initialDetail)) {
            m_commentDetail.setValue(m_initialDetail);
        }
        LayoutUtil.setMaxChar(commentDetailField,
                IPersistentObject.MAX_STRING_LENGTH);
    }
    
    /**
     * @param area The parent for the created widgets.
     * @return the created text field.
     */
    private Text createCommentDetailText(Composite area) {
        new Label(area, SWT.NONE).setText(
                Messages.EnterCommentDialogDetailLabel);
        return new Text(area, SWT.V_SCROLL | SWT.BORDER);
    }
    
    /**
     * This method must be called from the GUI thread.
     * 
     * @return the comment detail
     */
    public String getCommentDetail() {
        return m_commentDetail.getValue();
    }
}
