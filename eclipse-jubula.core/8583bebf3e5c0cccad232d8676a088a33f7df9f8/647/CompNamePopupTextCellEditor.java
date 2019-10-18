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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.CharacterConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * A cell editor that manages a m_popupText entry field.
 * The cell editor's value is the m_popupText string itself.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class CompNamePopupTextCellEditor extends CellEditor {

    /**
     * Default TextCellEditor style
     * specify no borders on m_popupText widget as cell outline in table already
     * provides the look of a border.
     */
    private static final int DEFAULT_STYLE = SWT.SINGLE;
    /**
     * The m_popupText control; initially <code>null</code>.
     */
    private CompNamePopUpTextField m_popupText;
    /***/
    private ModifyListener m_modifyListener;

    /**
     * State information for updating action enablement
     */
    private boolean m_isSelection = false;
    /***/
    private boolean m_isDeleteable = false;
    /***/
    private boolean m_isSelectable = false;

    /** cache of last valid stored value */
    private String m_lastValidValue;
    
    /** the component cache to use for finding and modifying components */
    private IComponentNameCache m_compCache;
    
    /**
     * Creates a new m_popupText string cell editor parented under the given control.
     * The cell editor value is the string itself, which is initially the empty string. 
     * Initially, the cell editor has no cell validator.
     * 
     * @param compCache The Component Names cache to use.
     * @param parent the parent control
     */
    public CompNamePopupTextCellEditor(IComponentNameCache compCache, 
            Composite parent) {
        
        super(parent, DEFAULT_STYLE);
        setComponentNameCache(compCache);
        m_lastValidValue = StringConstants.EMPTY;
    }

    /**
     * Checks to see if the "deleteable" state (can delete/
     * nothing to delete) has changed and if so fire an
     * enablement changed notification.
     */
    private void checkDeleteable() {
        boolean oldIsDeleteable = m_isDeleteable;
        m_isDeleteable = isDeleteEnabled();
        if (oldIsDeleteable != m_isDeleteable) {
            fireEnablementChanged(DELETE);
        }
    }

    /**
     * Checks to see if the "selectable" state (can select)
     * has changed and if so fire an enablement changed notification.
     */
    private void checkSelectable() {
        boolean oldIsSelectable = m_isSelectable;
        m_isSelectable = isSelectAllEnabled();
        if (oldIsSelectable != m_isSelectable) {
            fireEnablementChanged(SELECT_ALL);
        }
    }
    
    /**
     * @param filter the compType name (display-version)
     */
    public void setFilter(String filter) {
        m_popupText.setFilter(filter);
    }

    /**
     * Checks to see if the selection state (selection /
     * no selection) has changed and if so fire an
     * enablement changed notification.
     */
    private void checkSelection() {
        boolean oldIsSelection = m_isSelection;
        m_isSelection = m_popupText.getSelectionCount() > 0;
        if (oldIsSelection != m_isSelection) {
            fireEnablementChanged(COPY);
            fireEnablementChanged(CUT);
        }
    }

    /**
     * Method declared on CellEditor.
     * @param parent The parent composite.
     * @return The control.
     */
    protected Control createControl(Composite parent) {        
        m_popupText = new CompNamePopUpTextField(
                m_compCache, parent, getStyle());
        m_popupText.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
                handleDefaultSelection();
            }
        });
        m_popupText.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201  
            public void keyPressed(KeyEvent e) {
                keyReleaseOccurred(e);

                // as a result of processing the above call, clients may have
                // disposed this cell editor
                if ((getControl() == null) || getControl().isDisposed()) {
                    return;
                }
                checkSelection(); // see explanation below
                checkDeleteable();
                checkSelectable();
            }
        });
        m_popupText.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });
        // We really want a selection listener but it is not supported so we
        // use a key listener and a mouse listener to know when selection changes
        // may have occurred
        m_popupText.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {
                checkSelection();
                checkDeleteable();
                checkSelectable();
            }
        });
        m_popupText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                CompNamePopupTextCellEditor.this.focusLost();
            }
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
            }
        });
        m_popupText.setFont(parent.getFont());
        m_popupText.setBackground(parent.getBackground());
        m_popupText.setText(StringConstants.EMPTY);
        m_popupText.addModifyListener(getModifyListener());
        return m_popupText;
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the m_popupText string.
     *
     * @return the m_popupText string
     */
    protected Object doGetValue() {
        return m_popupText.getText();
    }

    /**
     * Method declared on CellEditor.
     */
    protected void doSetFocus() {
        if (m_popupText != null) {
            m_popupText.selectAll();
            m_popupText.setFocus();
            checkSelection();
            checkDeleteable();
            checkSelectable();
        }
    }

    /**
     * The <code>TextCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method accepts
     * a m_popupText string (type <code>String</code>).
     *
     * @param value a m_popupText string (type <code>String</code>)
     */
    protected void doSetValue(Object value) {
        Assert.verify(m_popupText != null);
        String v = (String)value;
        if (v == null) {
            v = StringConstants.EMPTY;
        }
        m_popupText.removeModifyListener(getModifyListener());
        m_popupText.setText(v);
        m_popupText.setData(CompNamePopUpTextField.INITPOPUP, false);
        m_popupText.addModifyListener(getModifyListener());
        m_lastValidValue = v;
    }

    /**
     * Processes a modify event that occurred in this m_popupText cell editor.
     * This framework method performs validation and sets the error message
     * accordingly, and then reports a change via <code>fireEditorValueChanged</code>.
     * Subclasses should call this method at appropriate times. Subclasses
     * may extend or reimplement.
     *
     */
    protected void editOccurred() {
        
        String value = m_popupText.getText();
        if (value == null) {
            value = StringConstants.EMPTY; 
        }
        boolean oldValidState = isValueValid();
        boolean newValidState = isCorrect(value);
        if (value == null && newValidState) {
            Assert.verify(false,
                    Messages.ValidatorIsntLimitingTheCellEditorsTypeRange);
        }
        if (!newValidState) {
            // try to insert the current value into the error message.
            setErrorMessage(MessageFormat.format(getErrorMessage(),
                    new Object[] { value }));
        }
        valueChanged(oldValidState, newValidState);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    protected void fireApplyEditorValue() {
        final String errorMsg = getErrorMessage();
        if (errorMsg != null) {
            // Show error message
            setValue(m_lastValidValue);
            ErrorHandlingUtil.createMessageDialog(
                MessageIDs.E_INCOMPATIBLE_COMP_TYPE, null, 
                new String[] {errorMsg});
        }
        super.fireApplyEditorValue();
    }
    
   

    /**
     * Since a m_popupText editor field is scrollable we don't
     * set a minimumSize.
     * @return LayoutData.
     */
    public LayoutData getLayoutData() {
        return new LayoutData();
    }

    /**
     * Return the modify listener.
     * @return the modify listener.
     */
    private ModifyListener getModifyListener() {
        if (m_modifyListener == null) {
            m_modifyListener = new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    editOccurred();
                }
            };
        }
        return m_modifyListener;
    }

    /**
     * Handles a default selection event from the m_popupText control by applying the editor
     * value and deactivating this cell editor.
     * 
     */
    protected void handleDefaultSelection() {
        // same with enter-key handling code in keyReleaseOccurred(e);
        fireApplyEditorValue();
        deactivate();
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method returns <code>true</code> if 
     * the current selection is not empty.
     * @return <code>true</code> if 
     * the current selection is not empty.
     */
    public boolean isCopyEnabled() {
        if (m_popupText == null || m_popupText.isDisposed()) {
            return false;
        }
        return m_popupText.getSelectionCount() > 0;
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method returns <code>true</code> if 
     * the current selection is not empty.
     * @return <code>true</code> if 
     * the current selection is not empty.
     */
    public boolean isCutEnabled() {
        if (m_popupText == null || m_popupText.isDisposed()) {
            return false;
        }
        return m_popupText.getSelectionCount() > 0;
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method returns <code>true</code>
     * if there is a selection or if the caret is not positioned 
     * at the end of the m_popupText.
     * @return <code>true</code>
     * if there is a selection or if the caret is not positioned 
     * at the end of the m_popupText.
     */
    public boolean isDeleteEnabled() {
        if (m_popupText == null || m_popupText.isDisposed()) {
            return false;
        }
        return m_popupText.getSelectionCount() > 0
                || m_popupText.getCaretPosition() < m_popupText.getCharCount();
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method always returns <code>true</code>.
     * @return <code>true</code>.
     */
    public boolean isPasteEnabled() {
        return !(m_popupText == null || m_popupText.isDisposed());
    }

    /**
     * The <code>TextCellEditor</code>  implementation of this 
     * <code>CellEditor</code> method always returns <code>true</code>.
     * @return <code>true</code>.
     */
    public boolean isSaveAllEnabled() {
        return !(m_popupText == null || m_popupText.isDisposed());
    }

    /**
     * Returns <code>true</code> if this cell editor is
     * able to perform the select all action.
     * <p>
     * This default implementation always returns 
     * <code>false</code>.
     * </p>
     * <p>
     * Subclasses may override
     * </p>
     * @return <code>true</code> if select all is possible,
     *  <code>false</code> otherwise
     */
    public boolean isSelectAllEnabled() {
        if (m_popupText == null || m_popupText.isDisposed()) {
            return false;
        }
        return m_popupText.getCharCount() > 0;
    }

    /**
     * Processes a key release event that occurred in this cell editor.
     * <p>
     * The <code>TextCellEditor</code> implementation of this framework method 
     * ignores when the RETURN key is pressed since this is handled in 
     * <code>handleDefaultSelection</code>.
     * An exception is made for Ctrl+Enter for multi-line texts, since
     * a default selection event is not sent in this case. 
     * </p>
     *
     * @param keyEvent the key event
     */
    protected void keyReleaseOccurred(KeyEvent keyEvent) {
        if (keyEvent.character == CharacterConstants.RETURN) {
            // Enter is handled in handleDefaultSelection.
            // Do not apply the editor value in response to an Enter key event
            // since this can be received from the IME when the intent is -not-
            // to apply the value.  
            // See bug http://eclip.se/39074 [CellEditors] [DBCS] canna input mode fires bogus event from Text Control
            //
            // An exception is made for Ctrl+Enter for multi-line texts, since
            // a default selection event is not sent in this case. 
            if (m_popupText != null && !m_popupText.isDisposed()
                && (m_popupText.getStyle() & SWT.MULTI) != 0
                && (keyEvent.stateMask & SWT.CTRL) != 0) {

                super.keyReleaseOccured(keyEvent);
            }
            return;
        }
        super.keyReleaseOccured(keyEvent);
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method copies the
     * current selection to the clipboard. 
     */
    public void performCopy() {
        m_popupText.copy();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method cuts the
     * current selection to the clipboard. 
     */
    public void performCut() {
        m_popupText.cut();
        checkSelection();
        checkDeleteable();
        checkSelectable();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method deletes the
     * current selection or, if there is no selection,
     * the character next character from the current position. 
     */
    public void performDelete() {
        if (m_popupText.getSelectionCount() > 0) {
            // remove the contents of the current selection
            m_popupText.insert(StringConstants.EMPTY); 
        } else {
            // remove the next character
            int pos = m_popupText.getCaretPosition();
            if (pos < m_popupText.getCharCount()) {
                m_popupText.setSelection(pos, pos + 1);
                m_popupText.insert(StringConstants.EMPTY); 
            }
        }
        checkSelection();
        checkDeleteable();
        checkSelectable();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method pastes the
     * the clipboard contents over the current selection. 
     */
    public void performPaste() {
        m_popupText.paste();
        checkSelection();
        checkDeleteable();
        checkSelectable();
    }

    /**
     * The <code>TextCellEditor</code> implementation of this
     * <code>CellEditor</code> method selects all of the
     * current m_popupText. 
     */
    public void performSelectAll() {
        m_popupText.selectAll();
        checkSelection();
        checkDeleteable();
    }

    /**
     * @param isSelectable set the cellEditor selectable
     */
    public void setSelectable(boolean isSelectable) {
        m_isSelectable = isSelectable;
    }
    
    /**
     * 
     * @param compCache The new Component Name cache to use.
     */
    public void setComponentNameCache(IComponentNameCache compCache) {
        m_popupText.setComponentNameCache(compCache);
        m_compCache = compCache;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void focusLost() {
        if (!m_popupText.isPopupOpen()) {
            super.focusLost();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean dependsOnExternalFocusListener() {
        // Always return false;
        // Otherwise, the ColumnViewerEditor will install an additional
        // focus listener
        // that cancels cell editing on focus lost, even if focus gets lost
        // due to
        // activation of the completion proposal popup. See also bug
        // http://eclip.se/58777.
        return false;
    }

    /**
     * @param selectedExecNode the selected {@link IExecTestCasePO}
     */
    public void setSelectedNode(IExecTestCasePO selectedExecNode) {
        if (m_popupText != null) {
            m_popupText.setSelectedNode(selectedExecNode);
        }
        
    }
}