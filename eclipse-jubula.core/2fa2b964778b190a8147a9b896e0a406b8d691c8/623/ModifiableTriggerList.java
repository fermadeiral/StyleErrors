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

import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jface.bindings.keys.KeySequenceText;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IContentAddedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IContentChangedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IContentRemovedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.IOptionalButtonSelectedListener;
import org.eclipse.jubula.client.ui.rcp.widgets.ModifiableListObservable.ISelectionChangedListener;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Aug 29, 2008
 */
public class ModifiableTriggerList extends Composite implements 
    IModifiableListObservable {
    
    /** for log messages */
    private static Logger log = 
        LoggerFactory.getLogger(ModifiableTriggerList.class);
    
    /** GUI component */
    private Text m_editor;
    /** GUI component */
    private Button m_addButton;
    /** GUI component */
    private List m_list;
    /** GUI component */
    private Button m_changeButton;
    /** GUI component */
    private Button m_removeButton;
    /** optional button */
    private Button m_optionalButton;
    
    /** object for execution of all operations regarding to observation of 
     * this class
     */
    private IModifiableListObservable m_observable;
    
    /** determines, if the list might be empty or not */
    private boolean m_isEmptyListAllowed;
    
    /**
     * The manager for the text widget that traps incoming key events. This
     * manager should be used to access the widget, rather than accessing the
     * widget directly.
     */
    private KeySequenceText m_textTriggerSequenceManager;
    
    
    /**
     * @param parent see Composite
     * @param style see Composite
     * @param headerText the text to display
     * and change operations
     * @param values initial values to display
     * @param emptyListAllowed allows, that the list is empty or not
     */
    public ModifiableTriggerList(
        Composite parent, 
        int style, 
        String headerText,
        java.util.Set<String> values,
        boolean emptyListAllowed) {
        super(parent, style);
        if (!emptyListAllowed && values != null && values.isEmpty()) {
            String msg = Messages.ValuesForListMustNotBeEmpty 
                + StringConstants.DOT;
            log.error(msg);
            throw new IllegalArgumentException(msg); 
        }
        m_observable = new ModifiableListObservable();
        m_isEmptyListAllowed = emptyListAllowed;
        initControls(headerText, values);
        enableButtons();
        addListeners();
    }
    
    // code for delegation of all operations regarding to observation
    // --------------------------------------------------------------
    
    /**
     * @param listener listener for add events in the container
     */
    public void addContentAddedListener(IContentAddedListener listener) {
        m_observable.addContentAddedListener(listener);
    }
    /**
     * @param listener listener for modification of an item in the container
     */
    public void addContentChangedListener(IContentChangedListener listener) {
        m_observable.addContentChangedListener(listener);
    }
    /**
     * @param listener listener for removal of an item in the container
     */
    public void addContentRemovedListener(IContentRemovedListener listener) {
        m_observable.addContentRemovedListener(listener);
    }
    /**
     * @param listener listener for change of selection in the container
     */
    public void addSelectionChangedListener(
        ISelectionChangedListener listener) {
        m_observable.addSelectionChangedListener(listener);
    }
    
    /**
     * @param listener listener for selection of optional button
     */
    public void addOptionalButtonSelectedListener(
        IOptionalButtonSelectedListener listener) {
        m_observable.addOptionalButtonSelectedListener(listener);
    }
    
    /**
     * @param listener listener for add events in the container
     */
    public void removeContentAddedListener(IContentAddedListener listener) {
        m_observable.removeContentAddedListener(listener);
    }
    /**
     * @param listener listener for modification of an item in the container
     */
    public void removeContentChangedListener(IContentChangedListener listener) {
        m_observable.removeContentChangedListener(listener);
    }
    /**
     * @param listener listener for removal of an item in the container
     */
    public void removeContentRemovedListener(IContentRemovedListener listener) {
        m_observable.removeContentRemovedListener(listener);
    }
    /**
     * @param listener listener for change of selection in the container
     */
    public void removeSelectionChangedListener(
        ISelectionChangedListener listener) {
        m_observable.removeSelectionChangedListener(listener);
    }
    
    /**
     * @param listener listener for selection of optional button
     */
    public void removeOptionalButtonSelectedListener(
        IOptionalButtonSelectedListener listener) {
        m_observable.removeOptionalButtonSelectedListener(listener);
    }
    
    /**
     * @param newValue newly added content
     */
    public void fireContentAdded(String newValue) {
        m_observable.fireContentAdded(newValue);
    }
    
    /**
     * @param oldValue this value was just changed
     * @param newValue this is the new value
     */
    public void fireContentChanged(String oldValue, String newValue) {
        m_observable.fireContentChanged(oldValue, newValue);
    }
    /**
     * @param oldValue the value which was just removed from the list
     */
    public void fireContentRemoved(String oldValue) {
        m_observable.fireContentRemoved(oldValue);
    }
    /**
     * @param value which value is selected
     */
    public void fireSelectionChanged(String value) {
        m_observable.fireSelectionChanged(value);
    }
    
    /**
     * 
     */
    public void fireOptionalButtonSelected() {
        m_observable.fireOptionalButtonSelected();
        
    }
       
    // end code for delegation of all operations regarding to observation
    // -------------------------------------------------------------------

    
    /**
     * tooltip Listener;
     */
    private abstract class ToolTipListener implements Listener {
        /***/
        private Control m_toolTipOwner;
        /***/
        private Text m_toolTipContent;
        /***/
        private Shell m_tip;
        /***/
        private Listener m_labelListener = new LabelListener();

        /**
         * constructor
         * @param c Control
         */
        public ToolTipListener(Control c) {
            m_toolTipOwner = c;
            c.addListener(SWT.MouseHover, this);
            c.addListener(SWT.Dispose, this);
            c.addListener(SWT.KeyDown, this);
            c.addListener(SWT.MouseMove, this);
            c.addListener(SWT.Selection, this);
            c.addListener(SWT.FocusOut, this);

        }
        
        /**
         * abstract getText
         * @param event
         *      Event
         * @return
         *      String
         */
        public abstract String getText(Event event);

        /**
         * @return
         *      Owner of Tooltip
         */
        public Control getToolTipOwner() {
            return m_toolTipOwner;
        }
        /**
         * switch event
         * @param event Event
         */
        public void handleEvent(Event event) {
            switch (event.type) {
                case SWT.Dispose:
                case SWT.KeyDown:
                case SWT.FocusOut:
                case SWT.Selection:
                case SWT.MouseMove: {
                    if (m_tip == null) { 
                        break;
                    }
                    m_tip.dispose();
                    m_tip = null;
                    m_toolTipContent = null;
                    break;
                }
                case SWT.MouseHover: {
                    if (m_tip != null) { 
                        m_tip.dispose();
                    }
                    Point point = toDisplay(m_toolTipOwner.getLocation());
                    m_tip = new Shell(m_toolTipOwner.getShell(), 
                        SWT.ON_TOP | SWT.TOOL);       
                    FillLayout layout = new FillLayout();
                    layout.marginHeight = 2;
                    layout.marginWidth = 2;
                    m_tip.setLayout(layout);
                    m_toolTipContent = new Text(m_tip, LayoutUtil.MULTI_TEXT 
                        | SWT.READ_ONLY);
                    m_tip.setForeground(m_toolTipOwner.getDisplay()
                        .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                    m_tip.setBackground(m_toolTipOwner.getDisplay()
                        .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                    m_toolTipContent.setForeground(m_toolTipOwner.getDisplay()
                            .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                    m_toolTipContent.setBackground(m_toolTipOwner.getDisplay()
                        .getSystemColor(SWT.COLOR_INFO_BACKGROUND));

                    String text = getText(event);
                    if (text.length() == 0) {
                        return;
                    }
                    m_toolTipContent.setText(text);
                    m_toolTipContent.addListener(SWT.MouseExit, 
                        m_labelListener);
                    m_toolTipContent.addListener(SWT.MouseDown, 
                        m_labelListener);
                    m_toolTipContent.addListener(SWT.FocusOut, 
                            m_labelListener);
                    
                    m_tip.setBounds(m_toolTipOwner.getBounds());
                    int height = Dialog.convertHeightInCharsToPixels(
                        LayoutUtil.getFontMetrics(m_toolTipContent), 
                        m_toolTipContent.getLineCount() + 1);
                    Rectangle itemBounds = m_toolTipOwner.getBounds();
                    m_tip.setBounds(point.x + event.x, 
                        point.y + event.y + 20, 
                        itemBounds.width, height);
                    m_tip.setVisible(true);
                }
                default : {
                    break;
                }

            }
            
        }
        
        /**
         *
         * @author BREDEX GmbH
         * @created 13.02.2006
         */
        private class LabelListener implements Listener {

            /**
             * {@inheritDoc}
             */
            public void handleEvent(Event event) {
                Text label = (Text) event.widget;
                Shell shell = label.getShell();
                switch (event.type) {
                    case SWT.FocusOut:    
                    case SWT.MouseExit:
                        shell.dispose();
                        break;
                    default: {
                        break;
                    }
                }
            }
        }
    }

    /**
     * @author BREDEX GmbH
     * @created 12.06.2006
     */
    public class EditorModified implements ModifyListener {

        /**
         * {@inheritDoc}
         * @param e
         */
        public void modifyText(ModifyEvent e) {
            handleEditorChanged();
        }
        /**
         * 
         */
        private void handleEditorChanged() {
            enableButtons();        
        }

    }

    /**
     * @author BREDEX GmbH
     * @created 12.06.2006
     */
    public class ItemSelected implements SelectionListener {

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetSelected(SelectionEvent e) {
            handleListSelection();
        }

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            // nothing to be done, this component doesn't have a default button.
        }
        /**
         * 
         */
        @SuppressWarnings("synthetic-access") 
        public void handleListSelection() {
            if (m_list.getSelectionCount() > 0) {
                final String selection = m_list.getSelection()[0];
                m_editor.setText(selection);
                fireSelectionChanged(selection);
            }
            enableButtons();
        }

    }

    /**
     * @author BREDEX GmbH
     * @created 12.06.2006
     */
    public class AddSelected implements SelectionListener {

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetSelected(SelectionEvent e) {      
            handleAdd();
        }

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            // nothing to be done, this component doesn't have a default button.
        }
        /**
         * handle the add button
         */
        @SuppressWarnings("synthetic-access") 
        private void handleAdd() {
            String newValue = m_editor.getText();
            if (!StringUtils.isEmpty(newValue)) {
                final java.util.List<String> listContent = 
                    Arrays.asList(m_list.getItems());
                if (!(listContent.contains(newValue))) {
                    SortedSet<String> content = 
                        new TreeSet<String>(listContent);
                    content.add(newValue);
                    setValues(content);
                    setSelection(m_list.indexOf(newValue));
                    m_list.showSelection();
                    fireContentAdded(newValue);        
                }
            }
        }

    }

    /**
     * @author BREDEX GmbH
     * @created 12.06.2006
     */
    public class RemoveSelected implements SelectionListener {

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetSelected(SelectionEvent e) {      
            handleRemove();
        }

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            // nothing to be done, this component doesn't have a default button.
        }
        /**
         * handle the remove button
         *
         */
        private void handleRemove() {
            final int selectionIndex = m_list.getSelectionIndex();
            if (selectionIndex != -1) {
                String oldValue = m_list.getItem(selectionIndex);
                m_list.remove(selectionIndex);
                enableButtons();
                fireContentRemoved(oldValue);
            }
        }
    }
    
    /**
     * @author BREDEX GmbH
     * @created 12.06.2006
     */
    @SuppressWarnings("synthetic-access") 
    public class ChangeSelected implements SelectionListener {

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetSelected(SelectionEvent e) {      
            handleChange();
        }

        /**
         * {@inheritDoc}
         * @param e
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            // nothing to be done, this component doesn't have a default button.
        }
        /**
         * handle the change button
         */
        private void handleChange() {
            final int selectionIndex = m_list.getSelectionIndex();
            String oldValue = m_list.getItem(selectionIndex);
            if (selectionIndex != -1) {
                final String newValue = m_editor.getText();                
                final java.util.List<String> listContent = 
                    Arrays.asList(m_list.getItems());
                if (!(listContent.contains(newValue))) {
                    SortedSet<String> content = 
                        new TreeSet<String>(listContent);
                    content.remove(oldValue);
                    content.add(newValue);
                    setValues(content);
                    m_list.select(m_list.indexOf(newValue));
                    m_list.showSelection();
                    m_editor.setText(newValue);
                    fireContentChanged(oldValue, newValue);
                }
            }            
        }
    }
    
    /**
     * @author BREDEX GmbH
     * @created 19.06.2006
     */
    public class OptionalButtonSelected implements SelectionListener {
        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            handleOptionalButtonSelected();
        }

        /**
         * callback method
         */
        private void handleOptionalButtonSelected() {
            fireOptionalButtonSelected();            
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
        // nothing
        }

    }
    
    
    /**
     * 
     */
    private void enableButtons() {
        boolean validSelection = m_list.getSelectionIndex() != -1;
        boolean emptyEditor = StringConstants.EMPTY.equals(m_editor.getText());
        if (!m_isEmptyListAllowed && m_list.getItemCount() <= 1) {
            m_removeButton.setEnabled(false);
        } else {
            m_removeButton.setEnabled(validSelection);
        }
        m_changeButton.setEnabled(validSelection && !emptyEditor);
        m_addButton.setEnabled(!emptyEditor);
    }
    
    
    
    /**
     * set the internal listeners
     */
    private void addListeners() {
        m_addButton.addSelectionListener(new AddSelected());
        m_removeButton.addSelectionListener(new RemoveSelected());
        m_changeButton.addSelectionListener(new ChangeSelected());        
        m_list.addSelectionListener(new ItemSelected());
        m_editor.addModifyListener(new EditorModified());
        if (m_optionalButton != null) {
            m_optionalButton.addSelectionListener(new OptionalButtonSelected());
        }
    }
    
    /**
     * create all controls for this component
     * @param headerText the text to display
     * @param values initial values to display
     */
    private void initControls(String headerText, Set <String> values) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 10;
        this.setLayout(layout);
        
        Label header = new Label(this, SWT.NONE);
        header.setText(headerText);
        FormData headerData = new FormData();
        headerData.left = new FormAttachment(0, 0);
        headerData.top = new FormAttachment(0, 0);
        headerData.right = new FormAttachment(100, 0);
        header.setLayoutData(headerData);
        
        m_editor = new Text(this, SWT.BORDER);
        FormData editorData = new FormData();
        editorData.left = new FormAttachment(0, 0);
        editorData.top = new FormAttachment(header, 5, SWT.BOTTOM);
        m_editor.setLayoutData(editorData);
        
        m_textTriggerSequenceManager = new KeySequenceText(m_editor);
        m_textTriggerSequenceManager.setKeyStrokeLimit(1); 
        
        m_addButton = new Button(this, SWT.PUSH);
        m_addButton.setText(Messages.ModifiableListAdd);
        FormData addButtonData = new FormData();
        m_addButton.setLayoutData(addButtonData);
        
        editorData.right = new FormAttachment(74, 0);
        addButtonData.top = new FormAttachment(m_editor, -2, SWT.TOP);
        addButtonData.left = new FormAttachment(76, 0);
        addButtonData.right = new FormAttachment(100, 0);
                
        createButtonGroup();        
        m_list = new List(this, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        addToolTipListener();
        FormData listData = new FormData();
        listData.left = new FormAttachment(0, 0);
        listData.top = new FormAttachment(m_addButton, 10, SWT.BOTTOM);
        listData.right = new FormAttachment(m_addButton, 0, SWT.RIGHT);
        listData.bottom = new FormAttachment(m_changeButton, -10, SWT.TOP);
        m_list.setLayoutData(listData);
        setValues(values);
//        pack(true);
    }
    
    /**
     * create a group with edit and remove button
     */
    private void createButtonGroup() {
        
        m_changeButton = new Button(this, SWT.NONE);
        m_changeButton.setText(Messages.ModifiableListEdit);
        FormData changeBtData = new FormData();
        changeBtData.left = new FormAttachment(0, 0);
        changeBtData.top = new FormAttachment(100, -30);
        changeBtData.right = new FormAttachment(49, 0);
        m_changeButton.setLayoutData(changeBtData);
        
        m_removeButton = new Button(this, SWT.NONE);
        m_removeButton.setText(Messages.ModifiableListRemove);
        FormData removeBtData = new FormData();
        removeBtData.left = new FormAttachment(51, 0);
        removeBtData.top = new FormAttachment(m_changeButton, 0, SWT.TOP);
        removeBtData.right = new FormAttachment(100, 0);
        m_removeButton.setLayoutData(removeBtData);
    }
    
    /**
     * adds a tooltip listener
     *
     */
    private void addToolTipListener() {
        new ToolTipListener(m_list) {
            @Override
            public String getText(Event event) {
                List list = ((List)getToolTipOwner());
                int start = list.getTopIndex();
                int heightPerItem = list.getItemHeight();
                int itemIndex = start + event.getBounds().y / heightPerItem;
                if (itemIndex >= list.getItemCount()) {
                    return StringConstants.EMPTY;
                }
                String item = list.getItem(itemIndex);
                return item;
            }
        };
    }
    

    /**
     * @param values values to set
     */
    public void setValues(Set <String> values) {
        m_list.removeAll();
        m_editor.setText(StringConstants.EMPTY);
        if (values != null) {
            for (String value : values) {
                m_list.add(value);
            }
        }
        enableButtons();
    }
    
    /**
     * @return values values of List
     */
    public String[] getValues() {
        return m_list.getItems();
    }
    
    
    /**
     * @param pos index for selection to set
     */
    public void setSelection(int pos) {
        Validate.isTrue(pos >= 0, "Invalid index for selection."); //$NON-NLS-1$
        if (m_list.getItemCount() > pos) {
            m_list.setSelection(pos);
            fireSelectionChanged(m_list.getSelection()[0]);
            enableButtons();
        }
    }
    
    /**
     * @param isEnabled flag to set the enabled status
     */
    public void setOptionalButtonEnabled(boolean isEnabled) {
        if (m_optionalButton != null) {
            m_optionalButton.setEnabled(isEnabled);
        }
    }
    
    /**
     * @param value value to set in textfield
     */
    public void setEditorText(String value) {
        m_editor.setText(value);
    }
    
    /**
     * @return number of items in list
     */
    public int getItemCount() {
        return m_list.getItemCount();
    }
}
