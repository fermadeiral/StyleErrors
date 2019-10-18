/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Collections;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jubula.extensions.wizard.edit.ParameterNameEditingSupport;
import org.eclipse.jubula.extensions.wizard.edit.ParameterTypeEditingSupport;
import org.eclipse.jubula.extensions.wizard.edit.ParameterValueSetEditingSupport;
import org.eclipse.jubula.extensions.wizard.i18n.Messages;
import org.eclipse.jubula.extensions.wizard.model.Action;
import org.eclipse.jubula.extensions.wizard.model.Parameter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

/**
 * The method properties window in which the user can specify the action's
 * parameters.
 * 
 * @author BREDEX GmbH
 */
final class ActionParametersWindow {

    /** The window's title */
    private static final String WINDOW_TITLE = 
            Messages.ParametersWindow_WindowTitle;
    
    /** The tableviewer */
    private TableViewer m_tableViewer;
    
    /** The "New" button */
    private Button m_newButton;
    
    /** The "Remove" button */
    private Button m_removeButton;
    
    /** The "Down" button */
    private Button m_downButton;
    
    /** The "Up" button */
    private Button m_upButton;
    
    /** The shell */
    private Shell m_shell;
    
    /** The method whose properties are being edited */
    private Action m_method;
    
    /**
     * The constructor.
     * @param parent the parent shell
     * @param method the method whose parameters should be made visible and
     *               editable
     * @param page the instance of the fourth page
     */
    public ActionParametersWindow(Shell parent, final Action method,
            final NewJubulaExtensionWizardPageFour page) {
        m_method = method;
        
        m_shell = new Shell(parent, parent.getStyle() | SWT.APPLICATION_MODAL);
        m_shell.setText(WINDOW_TITLE);
        m_shell.setLayout(new FormLayout());
        m_shell.setSize(500, 300);
        m_shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                page.updateMethodData(method);
            }
        });
        
        createNewButton();
        createTable();
        createRemoveButton();
        createUpButton();
        createDownButton();
        createOkButton();
        createSeparatorLine();
        
        setCenterPosition(m_shell);
        m_shell.open();
        m_shell.setActive();
    }

    /**
     * Creates the "New" button and its controls.
     */
    private void createNewButton() {
        m_newButton = new Button(m_shell, SWT.NONE);
        FormData fdNewButton = new FormData();
        fdNewButton.width = 75;
        fdNewButton.height = 25;
        fdNewButton.top = new FormAttachment(0, 10);
        fdNewButton.right = new FormAttachment(100, -10);
        m_newButton.setLayoutData(fdNewButton);
        m_newButton.setText(Messages.ParametersWindow_NewBtn);
        m_newButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int lastIndex = m_tableViewer.getTable().getItems().length;
                m_method.addParameter(
                    new Parameter("Parameter " + (lastIndex + 1), "String")); //$NON-NLS-1$ //$NON-NLS-2$
                refreshTableData();
                m_tableViewer.getTable().select(lastIndex);
            }
        });
    }

    /**
     * Creates the table and initializes it with the method's parameters.
     */
    private void createTable() {
        m_tableViewer = new TableViewer(m_shell, SWT.BORDER | SWT.SINGLE 
                | SWT.FULL_SELECTION | SWT.V_SCROLL);
        
        createColumns(m_tableViewer);
        
        Table table = m_tableViewer.getTable();
        table.setLayoutData(new FormData());
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        FormData fdTableViewer = new FormData();
        fdTableViewer.left = new FormAttachment(0, 10);
        fdTableViewer.top = new FormAttachment(m_newButton, 0, SWT.TOP);
        fdTableViewer.bottom = new FormAttachment(100, -10);
        fdTableViewer.right = new FormAttachment(m_newButton, -10, SWT.LEFT);
        m_tableViewer.getControl().setLayoutData(fdTableViewer);
        
        ArrayContentProvider provider = new ArrayContentProvider();
        m_tableViewer.setContentProvider(provider);
        m_tableViewer.getColumnViewerEditor().addEditorActivationListener(
                new ColumnViewerEditorActivationListener() {
                    @Override
                    public void beforeEditorDeactivated(
                            ColumnViewerEditorDeactivationEvent event) {
                        // Not needed
                    }
                    @Override
                    public void beforeEditorActivated(
                            ColumnViewerEditorActivationEvent event) {
                     // Not needed
                    }
                    @Override
                    public void afterEditorDeactivated(
                            ColumnViewerEditorDeactivationEvent event) {
                        refreshTableData();
                    }
                    @Override
                    public void afterEditorActivated(
                            ColumnViewerEditorActivationEvent event) {
                     // Not needed
                    }
                });
        refreshTableData();
    }

    /**
     * Creates the table's name and type column.
     * @param tableViewer the table's TableViewer
     */
    private void createColumns(TableViewer tableViewer) {
        TableViewerColumn name = new TableViewerColumn(tableViewer, SWT.NONE);
        name.getColumn().setWidth(140);
        name.getColumn().setText(Messages.ParametersWindow_NameColumnLbl);
        name.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Parameter) {
                    Parameter p = (Parameter) element;
                    return p.getName();
                }
                return null;
            }
        });
        name.setEditingSupport(new ParameterNameEditingSupport(tableViewer));
        
        TableViewerColumn type = new TableViewerColumn(tableViewer, SWT.NONE);
        type.getColumn().setWidth(80);
        type.getColumn().setText(Messages.ParametersWindow_TypeColumnLbl);
        type.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Parameter) {
                    Parameter p = (Parameter) element;
                    return p.getType().toString();
                }
                return null;
            }
        });
        type.setEditingSupport(new ParameterTypeEditingSupport(tableViewer));
        
        TableViewerColumn valueset = 
                new TableViewerColumn(tableViewer, SWT.NONE);
        valueset.getColumn()
            .setText(Messages.ParametersWindow_ValueSetColumnLbl);
        valueset.getColumn().setWidth(150);
        valueset.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Parameter) {
                    Parameter p = (Parameter) element;
                    return p.getValueSet().toString();
                }
                return null;
            }
        });
        valueset.setEditingSupport(
                new ParameterValueSetEditingSupport(tableViewer));
    }

    /**
     * Creates the "Remove" button and its controls.
     */
    private void createRemoveButton() {
        m_removeButton = new Button(m_shell, SWT.NONE);
        m_removeButton.setText(Messages.ParametersWindow_RemoveBtn);
        FormData fdRemoveButton = new FormData();
        fdRemoveButton.top = new FormAttachment(m_newButton, 6);
        fdRemoveButton.left = new FormAttachment(m_newButton, 0, SWT.LEFT);
        fdRemoveButton.right = new FormAttachment(m_newButton, 0, SWT.RIGHT);
        m_removeButton.setLayoutData(fdRemoveButton);
        m_removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedItems();
                refreshTableData();
                selectLastItem();
            }
        });
    }

    /**
     * Creates the "Up" button and its controls.
     */
    private void createUpButton() {
        m_upButton = new Button(m_shell, SWT.NONE);
        m_upButton.setText(Messages.ParametersWindow_UpBtn);
        FormData fdUpButton = new FormData();
        fdUpButton.top = new FormAttachment(m_removeButton, 16);
        fdUpButton.left = new FormAttachment(m_newButton, 0, SWT.LEFT);
        fdUpButton.right = new FormAttachment(m_newButton, 0, SWT.RIGHT);
        m_upButton.setLayoutData(fdUpButton);
        m_upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = m_tableViewer.getTable().getSelectionIndex();
                if (index > 0) {
                    Collections.swap(m_method.getParameters(), 
                            index, index - 1);
                    refreshTableData();
                    m_tableViewer.getTable().select(index - 1);  
                }
            }
        });
    }

    /**
     * Creates the "Down" button and its controls.
     */
    private void createDownButton() {
        m_downButton = new Button(m_shell, SWT.NONE);
        m_downButton.setText(Messages.ParametersWindow_DownBtn);
        FormData fdDownButton = new FormData();
        fdDownButton.left = new FormAttachment(m_newButton, 0, SWT.LEFT);
        fdDownButton.right = new FormAttachment(m_newButton, 0, SWT.RIGHT);
        fdDownButton.top = new FormAttachment(m_upButton, 6);
        m_downButton.setLayoutData(fdDownButton);
        m_downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = m_tableViewer.getTable().getSelectionIndex();
                Table table = m_tableViewer.getTable();
                if (index > -1 && index < (table.getItemCount() - 1)) {
                    Collections.swap(m_method.getParameters(), 
                            index, index + 1);
                    refreshTableData();
                    m_tableViewer.getTable().select(index + 1);  
                }
            }
        });
    }
    
    /**
     * Creates the "Ok" button and its controls.
     */
    private void createOkButton() {
        Button okButton = new Button(m_shell, SWT.NONE);
        okButton.setText(Messages.ParametersWindow_OkBtn);
        FormData fdOkButton = new FormData();
        fdOkButton.right = new FormAttachment(m_newButton, 0, SWT.RIGHT);
        fdOkButton.bottom = new FormAttachment(100, -10);
        fdOkButton.left = new FormAttachment(m_newButton, 0, SWT.LEFT);
        okButton.setLayoutData(fdOkButton);
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                m_shell.close();
            }
        });
    }
    
    /**
     * Creates the horizontal separator line between the "Remove" and "Up"
     * button.
     */
    private void createSeparatorLine() {
        Label label = new Label(m_shell, SWT.SEPARATOR | SWT.HORIZONTAL);
        FormData fdLabel = new FormData();
        fdLabel.bottom = new FormAttachment(m_upButton, -7);
        fdLabel.top = new FormAttachment(m_removeButton, 7);
        fdLabel.left = new FormAttachment(m_newButton, 0, SWT.LEFT);
        fdLabel.right = new FormAttachment(100, -10);
        label.setLayoutData(fdLabel);
    }

    /**
     * Moves the window to the display's center position.
     * @param shell the shell
     */
    private void setCenterPosition(Shell shell) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        shell.setLocation((width / 2) - (shell.getSize().x / 2),
                (height / 2) - (shell.getSize().y / 2));
    }

    /**
     * Fills the table with the method's properties.
     */
    private void refreshTableData() {
        m_tableViewer.setInput(m_method.getParameters());
    }

    /** Selects the last item of the list */
    private void selectLastItem() {
        m_tableViewer.getTable()
            .select(m_method.getParametersCount() - 1);
    }
    
    /** Removes the selected items */
    private void removeSelectedItems() {
        int[] selectionIndices = 
                m_tableViewer.getTable().getSelectionIndices();
        if (selectionIndices.length > 0) {
            m_method.removeParameters(selectionIndices);
        }
    }
}
