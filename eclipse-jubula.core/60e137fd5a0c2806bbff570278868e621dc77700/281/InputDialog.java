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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;



/**
 * This class creates a dialog, where you can fill in the name of a new test suite.
 * @author BREDEX GmbH
 * @created 22.02.2005
 */
public class InputDialog extends TitleAreaDialog {
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;
    /** number of columns = 4 */
    private static final int NUM_COLUMNS_4 = 4;
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
    /** = m_nameField.getText() */
    private String m_name = StringConstants.EMPTY;
    /** the name textfield */
    private Text m_nameField;
    /** the message depends on the object that is selected */
    private String m_message = StringConstants.EMPTY;
    /** the errormessage depends on the object that is selected */
    private String m_wrongNameError = StringConstants.EMPTY;
    /** the m_doubleNameError depends on the object that is selected */
    private String m_doubleNameError = StringConstants.EMPTY;
    /** the label depends on the object that is selected */
    private String m_label = StringConstants.EMPTY;
    /** the image depends on the object that is selected */
    private String m_image = StringConstants.EMPTY;
    /** the shell depends on the object that is selected */
    private String m_shell = StringConstants.EMPTY;
    /** the title depends on the object that is selected */
    private String m_title = StringConstants.EMPTY;
    /** the oldName depends on the object that is selected */
    private String m_oldName = StringConstants.EMPTY;
    /** True, if you want to add a browse button in the dialog */
    private boolean m_browseable;
    /** maximum length of input */
    private int m_length = 255;
    /** the browse button */
    private Button m_button;

    /**
     * @param parentShell The parent shell.
     * @param title The name of the title.
     * @param oldName The old name of the selected item.
     * @param message The message.
     * @param label The label of the textfield.
     * @param wrongNameError The wrongNameError message.
     * @param doubleNameError The doubleNameError message.
     * @param image The image of the dialog.
     * @param shell The name of the shell.
     * @param browseable True, if you want to add a browse button in the dialog.
     */
    public InputDialog(Shell parentShell, String title,
            String oldName, String message, String label,
            String wrongNameError, String doubleNameError, String image,
            String shell, boolean browseable) {

        this(parentShell, title, oldName, message, label, wrongNameError, 
                doubleNameError, image, shell, browseable, 255);
    }

    /**
     * @param parentShell The parent shell.
     * @param title The name of the title.
     * @param oldName The old name of the selected item.
     * @param message The message.
     * @param label The label of the textfield.
     * @param wrongNameError The wrongNameError message.
     * @param doubleNameError The doubleNameError message.
     * @param image The image of the dialog.
     * @param shell The name of the shell.
     * @param maxLength Maximum Length of input
     * @param browseable True, if you want to add a browse button in the dialog.
     */
    public InputDialog(Shell parentShell, String title,
            String oldName, String message, String label,
            String wrongNameError, String doubleNameError, String image,
            String shell, boolean browseable, int maxLength) {

        super(parentShell);
        m_browseable = browseable;
        m_oldName = oldName;
        m_message = message;
        m_label = label;
        m_wrongNameError = wrongNameError;
        m_doubleNameError = doubleNameError;
        m_image = image;
        m_shell = shell;
        m_title = title;
        m_length = maxLength;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(m_title);
        
        if (StringUtils.isNotBlank(m_image)) {
            setTitleImage(IconConstants.getImage(m_image));
        }
       
        getShell().setText(m_shell);
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        LayoutUtil.createSeparator(parent);
        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_4;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        area.setLayoutData(gridData);
        createNameField(area);
        createAdditionalComponents(area);
        LayoutUtil.createSeparator(parent);
        return area;
    }
    
    /**
     * Sets the shell style bits. This method has no effect after the shell iscreated.
     * <p>
     * The shell style bits are used by the framework method
     * <code>createShell</code> when creating this window's shell.
     * </p>
     * @param style the new shell style bits
     */
    public void setStyle(int style) {
        setShellStyle(style);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void initializeBounds() {
        super.initializeBounds();
        modifyNameFieldAction();
        setMessage(m_message);
    }

    /**
     * @param area The composite. creates the text field to edit the TestSuite name.
     */
    private void createNameField(Composite area) {
        new Label(area, SWT.NONE).setText(m_label);
        m_nameField = new Text(area, SWT.SINGLE | SWT.BORDER);
        GridData gridData = newGridData(false);
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_nameField);
        m_nameField.setLayoutData(gridData);
        if (m_oldName != null) {
            m_nameField.setText(m_oldName);
        }
        LayoutUtil.setMaxChar(m_nameField, m_length);
        m_nameField.selectAll();
        
        m_nameField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (getButton(IDialogConstants.OK_ID) != null) {
                    modifyNameFieldAction();
                }
            }
        });
        if (m_browseable) {
            m_button = new Button(area, SWT.NONE);
            m_button.setText(Messages.InputDialogBrowse);
            m_button.setLayoutData(newGridData(true));
            m_button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleButtonEvent();
                }
            });
        }
    }
    
    /**
     * Handles the event of the button.
     */
    void handleButtonEvent() {
        FileDialog dialog = new FileDialog(getShell(),
            SWT.APPLICATION_MODAL);
        dialog.setFilterPath(Utils.getLastDirPath());
        dialog.setText(Messages.InputDialogSelectJRE);
        String path = dialog.open();
        if (path != null) {
            Utils.storeLastDirPath(dialog.getFilterPath());
            m_nameField.setText(path);
        }
    }

    /**
     * the action of the name field
     * @return false, if the name field contents an error: the name starts or end with a blank, or the field is empty
     */
    boolean modifyNameFieldAction() {
        boolean isCorrect = validateTCName(m_nameField.getText());
        if (isCorrect) {
            enableOKButton();
            if (!isInputAllowed()) {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
                setErrorMessage(m_doubleNameError);
                isCorrect = false;
            }
        } else {

            getButton(IDialogConstants.OK_ID).setEnabled(false);
            setErrorMessage(m_wrongNameError);
        }
        return isCorrect;
    }

    /**
     * A simple TC name validator - should not be empty or start / end with space
     * @param name the TC name
     * @return whether the name is valid
     */
    public static boolean validateTCName(String name) {
        return name.length() > 0 && !name.startsWith(StringConstants.SPACE)
                && !name.endsWith(StringConstants.SPACE);
    }

    /**
     * @return False, if the input name already exists.
     */
    protected boolean isInputAllowed() {
        return true;
    }

    /**
     * enables the OK button and makes a non-error title message
     */
    private void enableOKButton() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        setErrorMessage(null);
    }

    /**
     * This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        if (!modifyNameFieldAction()) {
            return;
        }
        m_name = m_nameField.getText();
        setReturnCode(OK);
        close();
    }

    /**
     * Creates a new GridData.
     * @param isButton True, if this is a grid for a button.
     * @return grid data
     */
    private GridData newGridData(boolean isButton) {
        GridData gridData = new GridData();
        if (!m_browseable) {
            gridData.grabExcessHorizontalSpace = true;
            gridData.horizontalAlignment = GridData.FILL;
            gridData.horizontalSpan = HORIZONTAL_SPAN;
        } else {
            if (!isButton) {
                gridData.grabExcessHorizontalSpace = true;
                gridData.horizontalAlignment = GridData.FILL;
                gridData.horizontalSpan = HORIZONTAL_SPAN - 1;
            } else {
                gridData.horizontalAlignment = GridData.FILL;
                gridData.horizontalSpan = HORIZONTAL_SPAN - 2;
            }
        }
        return gridData;
    }

    /**
     * @return Returns the entered name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return Returns the text of the input field.
     */
    public String getInputFieldText() {
        return m_nameField.getText();
    }

    /**
     * @param text the text to set in the input field
     */
    protected void setInputFieldText(String text) {
        m_nameField.setText(text);
    }
    
    /**
     * Subclasses can add new guiComponents to the given layout.
     * @param parent the parent composite
     */
    protected void createAdditionalComponents(Composite parent) {
        parent.setEnabled(true); // placeholder
    }
}