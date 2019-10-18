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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.extensions.wizard.edit.ActionEditingSupport;
import org.eclipse.jubula.extensions.wizard.i18n.Messages;
import org.eclipse.jubula.extensions.wizard.model.Action;
import org.eclipse.jubula.extensions.wizard.model.Parameter;
import org.eclipse.jubula.extensions.wizard.model.Storage;
import org.eclipse.jubula.extensions.wizard.utils.Status;
import org.eclipse.jubula.extensions.wizard.utils.Tools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * The fourth New Jubula Extension Wizard page in which the user can specify
 * the tester class name and the tester actions.
 * 
 * @author BREDEX GmbH
 */
public final class NewJubulaExtensionWizardPageFour extends WizardPage {

    /** The page's ID */
    private static final String PAGE_NAME = Messages.PageFour_PageName;
    
    /** The page's title */
    private static final String PAGE_TITLE = Messages.PageFour_PageTitle;
    
    /** The page's description */
    private static final String PAGE_DESCRIPTION = 
            Messages.PageFour_PageDescription;
    
    /** The parent container */
    private Composite m_container;
    
    /** The class name textbox */
    private Text m_className;
    
    /** 
     * The actions group which contains the actions table and
     * the control buttons.
     */
    private Group m_actionsGroup;
    
    /** The tableviewer */
    private TableViewer m_tableViewer;
    
    /** The "New" button */
    private Button m_newButton;
    
    /** The "Remove" button */
    private Button m_removeButton;
    
    /** The "Properties" button */
    private Button m_parametersButton;
    
    /** The "Up" button */
    private Button m_upButton;
    
    /** The "Down" button */
    private Button m_downButton;
    
    /** The actions list */
    private List<Action> m_actions = new ArrayList<>();
    
    /** The instance of this wizard's storage */
    private final Storage m_storage;
    
    /** 
     * The constructor 
     * @param storage the storage instance this page instance should use
     */
    public NewJubulaExtensionWizardPageFour(Storage storage) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        
        m_storage = storage;
    }
    
    @Override
    public void createControl(Composite parent) {
        m_container = new Composite(parent, SWT.NONE);
        setControl(m_container);
        m_container.setLayout(new FormLayout());
        
        createClassName();
        createGroup();
        createNewButton();
        createTable();
        createParametersButton();
        createRemoveButton();
        createUpButton();
        createDownButton();
        createSeparatorLine();
        
        m_actionsGroup.setTabList(new Control[] {
            m_tableViewer.getTable(), m_newButton, m_parametersButton,
            m_removeButton, m_upButton, m_downButton
        });
        
        m_storage.setActions(m_actions);
    }
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setDefaultClassName();
            PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(),
                    Messages.PageFourQualifier);
        }
        super.setVisible(visible);
    }
    
    /** Creates the class name label, textbox and their controls */
    private void createClassName() {
        final Storage storage = m_storage;
        
        Label lblClassName = new Label(m_container, SWT.NONE);
        FormData fdLblClassName = new FormData();
        fdLblClassName.top = new FormAttachment(0, 13);
        lblClassName.setLayoutData(fdLblClassName);
        lblClassName.setText(Messages.PageFour_ClassNameTxt);
        
        m_className = new Text(m_container, SWT.BORDER);
        fdLblClassName.right = new FormAttachment(m_className, 0);
        fdLblClassName.left = new FormAttachment(0, 10);
        FormData fdClassName = new FormData();
        fdClassName.top = new FormAttachment(0, 10);
        fdClassName.left = new FormAttachment(lblClassName, 30);
        fdClassName.right = new FormAttachment(100, -10);
        m_className.setLayoutData(fdClassName);
        m_className.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (Tools
                        .isJavaIdentifier(m_className.getText())) {
                    setErrorMessage(null);
                    storage.setClassName(m_className.getText());
                } else {
                    setErrorMessage(Messages.PageFour_ClassnameInvalidMsg); 
                }
            }
        });
    }

    /**
     * Initializes the class name text field with a default value
     */
    private void setDefaultClassName() {
        Storage storage = m_storage;
        String component = storage.getComponent();
        String defaultClassName = "My" + component.substring(//$NON-NLS-1$
                component.lastIndexOf('.') + 1) + "Tester"; //$NON-NLS-1$
        m_className.setText(defaultClassName);
        storage.setClassName(defaultClassName);
    }

    /** Creates the actions group */
    private void createGroup() {
        m_actionsGroup = new Group(m_container, SWT.NONE);
        m_actionsGroup.setText(Messages.PageFour_ActionsTxt);
        m_actionsGroup.setLayout(new FormLayout());
        FormData fdMethodsGroup = new FormData();
        fdMethodsGroup.right = new FormAttachment(100, -10);
        fdMethodsGroup.bottom = 
                new FormAttachment(m_className, 218, SWT.BOTTOM);
        fdMethodsGroup.top = new FormAttachment(m_className, 17);
        fdMethodsGroup.left = new FormAttachment(0, 10);
        m_actionsGroup.setLayoutData(fdMethodsGroup);
    }

    /** Creates the "New" button and its controls */
    private void createNewButton() {
        m_newButton = new Button(m_actionsGroup, SWT.NONE);
        FormData fdNewButton = new FormData();
        fdNewButton.height = 25;
        fdNewButton.width = 75;
        fdNewButton.right = new FormAttachment(100, -10);
        fdNewButton.top = new FormAttachment(0, 9);
        m_newButton.setLayoutData(fdNewButton);
        m_newButton.setText(Messages.PageFour_NewButtonTxt);
        m_newButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int lastIndex = m_tableViewer.getTable().getItems().length;
                m_actions.add(new Action("Action " + (lastIndex + 1))); //$NON-NLS-1$
                refreshTableData();
                m_tableViewer.getTable().setSelection(lastIndex);
                controlButtonActivation();
                validatePageComplete();
            }
        });
    }

    /** Creates the table and its controls */
    private void createTable() {
        m_tableViewer = new TableViewer(m_actionsGroup, SWT.BORDER);
        FormData fdMethodsList = new FormData();
        fdMethodsList.left = new FormAttachment(0, 10);
        fdMethodsList.right = new FormAttachment(m_newButton, -10);
        fdMethodsList.bottom = new FormAttachment(100, -10);
        fdMethodsList.top = new FormAttachment(0, 10);
        m_tableViewer.getControl().setLayoutData(fdMethodsList);
        
        m_tableViewer.getTable().setHeaderVisible(true);
        m_tableViewer.getTable().setLinesVisible(false);
        m_tableViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (m_tableViewer.getTable().getSelectionCount() == 0) {
                            m_parametersButton.setEnabled(false);
                        } else {
                            m_parametersButton.setEnabled(true);
                        }
                    }
                });        
        createColumns();
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
        
        m_tableViewer.setContentProvider(new ArrayContentProvider());
        refreshTableData();
    }
    
    /** Fills the table with data */
    public void refreshTableData() {
        m_tableViewer.setInput(m_actions);
        validatePageComplete();
    }

    /** Creates the table's columns */
    private void createColumns() {
        TableViewerColumn nameColumn = 
                new TableViewerColumn(m_tableViewer, SWT.NONE);
        nameColumn.getColumn().setText(Messages.PageFour_NameColumnTxt);
        nameColumn.getColumn().setWidth(150);
        nameColumn.getColumn().setResizable(true);
        nameColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Action) {
                    Action action = (Action) element;
                    return action.getName();
                }
                return null;
            }
        });
        nameColumn.setEditingSupport(new ActionEditingSupport(m_tableViewer));
        
        TableViewerColumn parametersColumn =
                new TableViewerColumn(m_tableViewer, SWT.NONE);
        parametersColumn.getColumn()
            .setText(Messages.PageFour_ParametersColumnTxt);
        parametersColumn.getColumn().setWidth(150);
        parametersColumn.getColumn().setResizable(true);
        parametersColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Action) {
                    Action action = (Action) element;
                    return action.getParametersAsString();
                }
                return null;
            }
        });
        parametersColumn.getViewer().addDoubleClickListener(
                new IDoubleClickListener() {
                    @Override
                    public void doubleClick(DoubleClickEvent event) {
                        openPropertiesWindow();
                    }
                });
    }

    /** Creates the parameters button and its controls */
    private void createParametersButton() {
        m_parametersButton = new Button(m_actionsGroup, SWT.NONE);
        FormData fdParametersButton = new FormData();
        fdParametersButton.right = new FormAttachment(100, -10);
        fdParametersButton.top = new FormAttachment(0, 40);
        fdParametersButton.left = 
                new FormAttachment(m_tableViewer.getControl(), 10);
        m_parametersButton.setLayoutData(fdParametersButton);
        m_parametersButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openPropertiesWindow();
            }
        });
        m_parametersButton.setText(Messages.PageFour_ParametersButtonTxt);
        m_parametersButton.setEnabled(false);
    }

    /** Creates the "Remove" button and its controls */
    private void createRemoveButton() {
        m_removeButton = new Button(m_actionsGroup, SWT.NONE);
        FormData fdRemoveButton = new FormData();
        fdRemoveButton.right = new FormAttachment(100, -10);
        fdRemoveButton.top = new FormAttachment(0, 71);
        fdRemoveButton.left = 
                new FormAttachment(m_tableViewer.getControl(), 10);
        m_removeButton.setLayoutData(fdRemoveButton);
        m_removeButton.setText(Messages.PageFour_RemoveButonTxt);
        m_removeButton.setEnabled(false);
        m_removeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] selectionIndices = m_tableViewer.getTable()
                    .getSelectionIndices();
                if (selectionIndices.length > 0) {
                    removeActions(selectionIndices);
                    refreshTableData();
                    m_tableViewer.getTable().setSelection(m_actions.size() - 1);
                }
                controlButtonActivation();
                validatePageComplete();
            }
        });
    }
    
    /**
     * Creates the horizontal separator line between the "Remove" and "Up"
     * button.
     */
    private void createSeparatorLine() {
        Label label = new Label(m_actionsGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
        FormData fdLabel = new FormData();
        fdLabel.bottom = new FormAttachment(m_upButton, 7, SWT.TOP);
        fdLabel.top = new FormAttachment(m_removeButton, -7, SWT.BOTTOM);
        fdLabel.left = new FormAttachment(m_newButton, 0, SWT.LEFT);
        fdLabel.right = new FormAttachment(100, -10);
        label.setLayoutData(fdLabel);
    }
    
    /**
     * Removes the actions at the given indices
     * @param indices the indices of the actions to be deleted
     */
    private void removeActions(int[] indices) {
        for (int i : indices) {
            m_actions.remove(i);
        }
    }
    
    /** Creates the "Up" button and its controls */
    private void createUpButton() {
        m_upButton = new Button(m_actionsGroup, SWT.NONE);
        FormData fdUpButton = new FormData();
        fdUpButton.right = new FormAttachment(100, -10);
        fdUpButton.top = new FormAttachment(m_removeButton, 14);
        fdUpButton.left = 
                new FormAttachment(m_tableViewer.getControl(), 10);
        m_upButton.setLayoutData(fdUpButton);
        m_upButton.setText(Messages.PageFour_UpButtonTxt);
        m_upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = m_tableViewer.getTable().getSelectionIndex();
                if (index > 0) {
                    Collections.swap(m_actions, index, index - 1);
                    refreshTableData();
                    m_tableViewer.getTable().select(index - 1);  
                }
            }
        });
    }
    
    /** Creates the "Down" button and its controls */
    private void createDownButton() {
        m_downButton = new Button(m_actionsGroup, SWT.NONE);
        FormData fdDownButton = new FormData();
        fdDownButton.right = new FormAttachment(100, -10);
        fdDownButton.top = new FormAttachment(m_upButton, 7);
        fdDownButton.left = 
                new FormAttachment(m_tableViewer.getControl(), 10);
        m_downButton.setLayoutData(fdDownButton);
        m_downButton.setText(Messages.PageFour_DownButtonTxt);
        m_downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Table table = m_tableViewer.getTable();
                int index = m_tableViewer.getTable().getSelectionIndex();
                if (index > -1 && index < (table.getItemCount() - 1)) {
                    Collections.swap(m_actions, index, index + 1);
                    refreshTableData();
                    m_tableViewer.getTable().select(index + 1);  
                }
            }
        });
    }

    /** 
     * Updates the action's data 
     * @param action the action whose data should be updated
     */
    public void updateMethodData(Action action) {
        m_tableViewer.update(action, null);
        validatePageComplete();
    }

    /** Opens the "Properties" window */
    private void openPropertiesWindow() {
        if (m_tableViewer.getTable().getSelectionCount() == 1) {
            new ActionParametersWindow(m_container.getShell(), 
                    (Action) m_tableViewer.getElementAt(
                            m_tableViewer.getTable().getSelectionIndex()),
                            this);
        }
    }

    /** 
     * Activates the control buttons if there is more than one item in the list
     * and it is selected. Deactivates them otherwise.
     */
    private void controlButtonActivation() {
        if (m_tableViewer.getTable().getItemCount() == 0) {
            m_removeButton.setEnabled(false);
            m_parametersButton.setEnabled(false);
        } else {
            m_removeButton.setEnabled(true);
            m_parametersButton.setEnabled(true);
        }
    }
    
    /**
     * Validates whether the page is complete.
     * @return {@code true} if the page is complete, {@code false} otherwise
     */
    private boolean validatePageComplete() {
        Status status = validateActions();
        switch (status) {
            case ACTIONS_NAME_ALREADY_EXISTS:
                setErrorMessage(Messages.PageFour_ActionNamesDuplicatesMsg);
                setPageComplete(false);
                return false;
            case ACTIONS_OK:
                setErrorMessage(null);
                setPageComplete(true);
                return true;
            case ACTIONS_PARAMETERS_NAME_ALREADY_EXISTS:
                setErrorMessage(Messages.PageFour_ParameterNamesDuplicatesMsg);
                setPageComplete(false);
                return false;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    /**
     * Validates whether the actions and their parameters do not contain
     * duplicates.
     * @return the suitable Status
     */
    private Status validateActions() {
        Set<String> actionSet = new HashSet<>();
        for (Action action : m_actions) {
            if (!actionSet.add(Tools
                    .getCamelCase(action.getName()))) {
                return Status.ACTIONS_NAME_ALREADY_EXISTS;
            }
            
            List<Parameter> parameters = action.getParameters();
            Set<String> paramSet = new HashSet<>();
            for (Parameter param : parameters) {
                if (!paramSet.add(Tools.getCamelCase(param.getName()))) {
                    return Status.ACTIONS_PARAMETERS_NAME_ALREADY_EXISTS;
                }
            }
        }
        return Status.ACTIONS_OK;
    }
}
