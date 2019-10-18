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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.TestCaseTreeComposite;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;


/**
 * @author BREDEX GmbH
 * @since 12.10.2004
 */
public class TestCaseTreeDialog extends TitleAreaDialog {
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;    
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;    
    /** margin width = 2 */
    private static final int MARGIN_WIDTH = 2;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 2;
      
    /** List of ISelectionListener */
    private List < ISelectionListener > m_selectionListenerList = 
        new ArrayList < ISelectionListener > ();
    
    /** the title */
    private String m_title = Messages.TestCaseTableDialogTitle;
    
    /** the message */
    private String m_message = Messages.TestCaseTableDialogMessage;
    
    /** the shell title */
    private String m_shellTitle = Messages.TestCaseTableDialogShellTitle;

    /** the add button text */
    private String m_addButtonText = Messages.TestCaseTableDialogAdd;
    
    /** the TestCase which should be parent of the shown TestCases */
    private ISpecTestCasePO m_parentTestCase;

    /** The last selection to allow simple (non-listener) usage of the dialog */
    private ISelection m_lastSel;

    /** the style of the tree */
    private int m_treeStyle = SWT.SINGLE;
    /** the add button */
    private Button m_addButton;
    /** the image of the title area */
    private Image m_image = IconConstants.ADD_TC_DIALOG_IMAGE;

    /** Whether to show only categories */
    private boolean m_onlyCategories = false;

    /** Whether to show reused projects */
    private boolean m_reuseds = true;

    /** The node to select upon opening the dialog */
    private Object m_preSelect = null;

    /**
     * The text shown above the optional Text field.
     * If null, the text field is not shown.
     */
    private String m_enterTextLabel = null;

    /** The text entered into the optional text field */
    private String m_enteredText = null;

    /** The preset text */
    private String m_presetText = null;

    /**
     * <code>testcaseTreeComposite</code>
     */
    private TestCaseTreeComposite m_testcaseTreeComposite;
    
    /**
     * Constructor.
     * @param shell The parent of the dialog.
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     */  
    public TestCaseTreeDialog(Shell shell, ISpecTestCasePO parentTestCase,
            int treeStyle) {
        super(shell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        m_parentTestCase = parentTestCase;
        m_treeStyle = treeStyle;
    }
    
    /**
     * Constructor.
     * @param shell the parent shell.
     * @param title the title
     * @param message the message
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     * @param shellTitle the shell title
     * @param image The title image.
     */
    public TestCaseTreeDialog(Shell shell,
        String title, String message, ISpecTestCasePO parentTestCase, 
        String shellTitle, int treeStyle, Image image) {
        
        this(shell, parentTestCase, treeStyle);
        m_title = title;
        m_message = message;
        m_shellTitle = shellTitle;
        m_image = image;
    }
    
    /**
     * Constructor.
     * @param shell the parent shell.
     * @param title the title
     * @param message the message
     * @param parentTestCase the TestCase which should be parent of the shown TestCases.
     * <b>Can be null if parent is a Testsuite!</b>
     * @param treeStyle SWT.SINGLE or SWT.MULTI
     * @param shellTitle the shell title
     * @param image The title image.
     * @param addButtonText the text for the add / ok button
     */
    public TestCaseTreeDialog(Shell shell, String title, String message,
            ISpecTestCasePO parentTestCase, String shellTitle, int treeStyle,
            Image image, String addButtonText) {
        this(shell, title, message, parentTestCase, shellTitle, treeStyle,
                image);
        m_addButtonText = addButtonText;
    }
    
    
    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(m_title);
        setMessage(m_message);
        getShell().setText(m_shellTitle);
        setTitleImage(m_image); 
        // new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        
        LayoutUtil.createSeparator(parent);
        if (m_enterTextLabel != null) {
            Composite textPar = new Composite(parent, SWT.NONE);
            textPar.setLayoutData(
                    new GridData(SWT.FILL, SWT.FILL, true, false));
            GridLayout gr = new GridLayout();
            gr.numColumns = NUM_COLUMNS_1;
            gr.marginWidth = 5;
            textPar.setLayout(gr);
            Label lab = new Label(textPar, SWT.NONE);
            lab.setText(m_enterTextLabel);
            Text text = new Text(textPar, SWT.SINGLE | SWT.BORDER);
            GridData dat = new GridData(SWT.FILL, SWT.FILL, true, false);
            LayoutUtil.addToolTipAndMaxWidth(dat, text);
            text.setLayoutData(dat);
            if (m_presetText != null) {
                text.setText(m_presetText);
                m_enteredText = m_presetText;
            }
            text.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    m_enteredText = ((Text) e.getSource()).getText();
                    m_addButton.setEnabled(
                            checkDataValidityAndSetErrorMessage());
                }
                
            });
            LayoutUtil.createSeparator(parent);
        }

        if (m_parentTestCase != null) {
            m_testcaseTreeComposite = new TestCaseTreeComposite(parent, 
                m_treeStyle, m_parentTestCase);
        } else {
            m_testcaseTreeComposite = new TestCaseTreeComposite(parent,
                    m_treeStyle, m_reuseds, m_onlyCategories);
        }
        LayoutUtil.createSeparator(parent);
        return m_testcaseTreeComposite;
    }
    
    /**
     * {@inheritDoc}
     *      createButtonsForButtonBar(org.eclipse.swt.widgets.Composite) 
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // Add-Button
        String buttonText = m_addButtonText;
        if (m_onlyCategories) {
            buttonText = IDialogConstants.OK_LABEL;
        }
        m_addButton = createButton(parent, IDialogConstants.OK_ID,
                buttonText, true);
        m_addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(IDialogConstants.OK_ID);
                close();
            }
        });
        TreeViewer tv = m_testcaseTreeComposite.getTreeViewer(); 
        tv.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent e) {
                        if (e.getSelection() != null
                                && !e.getSelection().isEmpty()) {
                            m_lastSel = e.getSelection();
                            m_addButton.setEnabled(true);
                        }
                    }
                });
        
        tv.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        m_addButton.setEnabled(
                                checkDataValidityAndSetErrorMessage());
                    }
                });
        tv.addDoubleClickListener(
            new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent event) {
                    if (!m_addButton.getEnabled()) {
                        return;
                    }
                    notifyListener();
                    setReturnCode(IDialogConstants.OK_ID);
                    close();
                }
            });

        if (m_preSelect != null) {
            tv.getControl().setFocus();
            tv.setSelection(new StructuredSelection(m_preSelect));
        }
        // Cancel-Button
        Button cancelButton = createButton(parent, CANCEL,
                Messages.TestCaseTableDialogCancel, false);
        cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(CANCEL);
                close();
            }
        });
        m_addButton.setEnabled(checkDataValidityAndSetErrorMessage());
    }

    /**
     * @return whether the data entered to the dialog is valid. 
     */
    private boolean checkDataValidityAndSetErrorMessage() {
        boolean nameValid = true;
        if (m_enterTextLabel != null) {
            nameValid = InputDialog.validateTCName(m_enteredText);
            if (!nameValid) {
                setErrorMessage(Messages.RenameActionTCError);
            } else {
                setErrorMessage(null);
            }
        }
        return nameValid && m_testcaseTreeComposite.hasValidSelection();
    }
    
    /**
     * Adds the given ISelectionListener to this dialog
     * @param listener the listener to set.
     */
    public void addSelectionListener(ISelectionListener listener) {
        if (!m_selectionListenerList.contains(listener)) {
            m_selectionListenerList.add(listener);
        }
    }
    
    /**
     * Removes the given IselectionListener from this dialog.
     * @param listener the listener to be removed.
     */
    public void removeSelectionListener(ISelectionListener listener) {
        m_selectionListenerList.remove(listener);
    }
    
    /**
     * Notifies the listeners about the selected TestCases when the Add-button
     * is pressed. <br>
     * Note: The IWorkbenchPart-Parameter of the listener is set to null!
     */
    void notifyListener() {
        m_lastSel = m_testcaseTreeComposite.getTreeViewer().getSelection();
        for (ISelectionListener listener : m_selectionListenerList) {
            listener.selectionChanged(null, m_lastSel);
        }
    }

    /**
     * @param onlyCats whether to show only categories
     */
    public void setOnlyCategories(boolean onlyCats) {
        m_onlyCategories = onlyCats;
    }

    /**
     * @param reuseds whether to show reused projects
     */
    public void setReuseds(boolean reuseds) {
        m_reuseds = reuseds;
    }

    /**
     * Sets the node to be selected upon opening the dialog
     * @param pre the Node to be pre-selected.
     */
    public void setPreSelect(Object pre) {
        m_preSelect = pre;
    }

    /**
     * Sets the label text above the optional Text field.
     *    If null, no Text field is shown.
     * @param label the label text
     * @param preset text to preset the text field
     */
    public void setEnterTextLabel(String label, String preset) {
        m_enterTextLabel = label;
        m_presetText = preset;
    }

    /**
     * @return the list of the selected nodes
     */
    public List<INodePO> getSelection() {
        List<INodePO> res = new ArrayList<>();
        if (m_lastSel instanceof IStructuredSelection) {
            for (Iterator it = ((IStructuredSelection) m_lastSel).iterator();
                    it.hasNext(); ) {
                Object next = it.next();
                if (next instanceof INodePO) {
                    res.add((INodePO) next);
                }
            }
        }
        return res;
    }

    /**
     * Returns the optional entered text (can be null)
     * @return the text
     */
    public String getEnteredText() {
        return m_enteredText;
    }
}