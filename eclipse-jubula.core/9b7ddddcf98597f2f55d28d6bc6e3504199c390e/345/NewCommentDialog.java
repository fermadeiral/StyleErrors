/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;



/**
 * @author BREDEX GmbH
 * @created 27.10.2015
 */
public class NewCommentDialog extends TitleAreaDialog {
       
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;    
    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;
    /** horizontal span = 3 */
    private static final int HORIZONTAL_SPAN = 3;
    /** the text field for the comment text */
    private Text m_commentTextField;
    /** The text of the comment */
    private String m_commentText;
    /** The comment */
    private ICommentPO m_comment;
    
    /**
     * The constructor.
     * @param parentShell the parent shell
     */
    public NewCommentDialog(Shell parentShell) {
        super(parentShell);
    }
    
    /**
     * The constructor.
     * @param parentShell the parent shell
     * @param comment a comment to edit
     */
    public NewCommentDialog(Shell parentShell, ICommentPO comment) {
        this(parentShell);
        m_comment = comment;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.NewCommentDialogTitle);
        setMessage(Messages.NewCommentDialogMessage);
        getShell().setText(Messages.NewCommentDialogTitle);
//      new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = 1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        area.setLayoutData(gridData);
        createFields(area);
        Plugin.getHelpSystem().setHelp(area, ContextHelpIds
                .NEW_COMMENT_DIALOG);
        return area;
    }

    /**
     *  This method is called, when the OK button or ENTER was pressed.
     */
    protected void okPressed() {
        m_commentText = m_commentTextField.getText();
        setReturnCode(OK);
        close();
    }

    /**
     * {@inheritDoc}
     */
    public boolean close() {
        return super.close();
    }

    /**
     * @param area The composite.
     * creates the editor widgets
     */
    private void createFields(Composite area) {
        createCommentTextField(area);
    }
    
    /**
     * @param area The composite.
     * creates the text field to edit the comment text
     */
    private void createCommentTextField(Composite area) {
        m_commentTextField = new Text(area, SWT.MULTI | SWT.BORDER | SWT.WRAP
                | SWT.V_SCROLL);
        if (m_comment != null) {
            m_commentTextField.setText(StringUtils
                    .defaultIfBlank(m_comment.getName(), StringUtils.EMPTY));
        }
        GridData gridData = newGridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = SWT.FILL;
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_commentTextField);
        m_commentTextField.setLayoutData(gridData);
        LayoutUtil.setMaxChar(m_commentTextField,
                org.eclipse.jubula.client.core.model.
                IPersistentObject.MAX_STRING_LENGTH);
    }
    
    /**
     * Creates a new GridData.
     * @return grid data
     */
    private GridData newGridData() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = HORIZONTAL_SPAN;
        return gridData;
    }
    
    /**
     * @return Returns the comment text.
     */
    public String getCommentText() {
        return m_commentText;
    }
}