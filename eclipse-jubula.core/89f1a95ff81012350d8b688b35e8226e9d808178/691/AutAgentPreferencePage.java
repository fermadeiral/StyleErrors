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
package org.eclipse.jubula.client.ui.rcp.preferences;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager;
import org.eclipse.jubula.client.ui.validator.cell.PortCellEditorValidator;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * Preference page for configuring available AUT-Agents.
 *
 * @author BREDEX GmbH
 * @created Jan 19, 2010
 */
public class AutAgentPreferencePage extends PreferencePage 
    implements IWorkbenchPreferencePage {
    /** <code>LAYOUT_NUM_COLUMNS</code> */
    private static final int LAYOUT_NUM_COLUMNS = 2;

    /**
     * @author BREDEX GmbH
     * @created Jan 18, 2010
     */
    private class ServerPortEditingSupport extends EditingSupport {

        /**
         * Constructor
         * 
         * @param viewer The viewer
         */
        public ServerPortEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }

        /**
         * {@inheritDoc}
         */
        protected boolean canEdit(Object element) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        protected CellEditor getCellEditor(Object element) {
            final TextCellEditor editor = 
                new TextCellEditor((Composite)getViewer().getControl());
            editor.setValidator(new PortCellEditorValidator(
                    Messages.AutAgentPreferencePageColumnHeaderPort));
            editor.addListener(new ICellEditorListener() {
                public void applyEditorValue() {
                    AutAgentPreferencePage.this.setErrorMessage(null);
                }
                public void cancelEditor() {
                    AutAgentPreferencePage.this.setErrorMessage(null);
                }
                public void editorValueChanged(
                        boolean oldValidState, boolean newValidState) {
                    
                    if (newValidState) {
                        editor.getControl().setBackground(null);
                    } else {
                        editor.getControl().setBackground(
                                editor.getControl().getDisplay()
                                    .getSystemColor(SWT.COLOR_RED));
                    }
                    AutAgentPreferencePage.this.setErrorMessage(
                            editor.getErrorMessage());
                }
            });
            return editor;
        }

        /**
         * {@inheritDoc}
         */
        protected Object getValue(Object element) {
            return String.valueOf(
                    ((AutAgentManager.AutAgent)element).getPort());
        }

        /**
         * {@inheritDoc}
         */
        protected void setValue(Object element, Object value) {
            try {
                ((AutAgentManager.AutAgent)element).setPort(
                        getPortValue(value));
                getViewer().update(element, null);
            } catch (NumberFormatException nfe) {
                // The value cannot be set to the model.
                // Do nothing.
            }
        }

        /**
         * 
         * @param value The value to parse into an integer.
         * @return the integer value of <code>value</code>.
         * @throws NumberFormatException if the given value cannot be parsed
         *                               to an integer.
         */
        private int getPortValue(Object value) throws NumberFormatException {
            return Integer.parseInt(String.valueOf(value));
        }
    }

    /**
     *
     * @author BREDEX GmbH
     * @created Jan 18, 2010
     */
    private static class ServerHostNameEditingSupport extends EditingSupport {

        /**
         * Constructor
         * 
         * @param viewer The viewer
         */
        public ServerHostNameEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }

        /**
         * {@inheritDoc}
         */
        protected boolean canEdit(Object element) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        protected CellEditor getCellEditor(Object element) {
            return new TextCellEditor((Composite)getViewer().getControl());
        }

        /**
         * {@inheritDoc}
         */
        protected Object getValue(Object element) {
            return ((AutAgentManager.AutAgent)element).getName();
        }

        /**
         * {@inheritDoc}
         */
        protected void setValue(Object element, Object value) {
            String hostNameValue = String.valueOf(value);
            ((AutAgentManager.AutAgent)element).setName(hostNameValue);
            getViewer().update(element, null);
        }

    }


    /**
     * @author BREDEX GmbH
     * @created Jan 15, 2010
     */
    private static class AddressContentProvider 
            implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object input) {
            return ((List<AutAgentManager.AutAgent>)input).toArray();
        }

        /**
         * {@inheritDoc}
         */
        public void dispose() {
            // Nothing to dispose
        }

        /**
         * {@inheritDoc}
         */
        public void inputChanged(
                Viewer viewer, Object oldInput, Object newInput) {
            
            // No listeners to deregister / register
        }

    }

    /** viewer for AUT Agent addresses */
    private TableViewer m_addressViewer;

    /** 
     * Model for the information shown in the dialog. This model is persisted
     * when the dialog's changes are applied. 
     */
    private List<AutAgentManager.AutAgent> m_viewModel;
    
    
    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        noDefaultAndApplyButton();
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.PREFPAGE_SERVER);

        /** Add layer to parent widget */
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(LAYOUT_NUM_COLUMNS, false);
        composite.setLayout(layout);

        m_addressViewer = createTableViewer(composite);
        
        TableViewerFocusCellManager focusCellManager = 
            new TableViewerFocusCellManager(m_addressViewer,
                    new FocusCellOwnerDrawHighlighter(m_addressViewer));
        ColumnViewerEditorActivationStrategy actSupport = 
            new ColumnViewerEditorActivationStrategy(m_addressViewer) {
                protected boolean isEditorActivationEvent(
                        ColumnViewerEditorActivationEvent event) {
                    return event.eventType 
                            == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType 
                            == ColumnViewerEditorActivationEvent.
                                MOUSE_DOUBLE_CLICK_SELECTION
                        || (event.eventType 
                                == ColumnViewerEditorActivationEvent.KEY_PRESSED
                            && event.keyCode == SWT.CR)
                        || event.eventType 
                            == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
                }
            };
        
        TableViewerEditor.create(m_addressViewer, focusCellManager, 
                actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
                    | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                    | ColumnViewerEditor.TABBING_VERTICAL 
                    | ColumnViewerEditor.KEYBOARD_ACTIVATION);


        
        
        m_viewModel = new LinkedList<AutAgentManager.AutAgent>();
        if (AutAgentManager.getInstance().getAutAgents() != null) {
            m_viewModel.addAll(AutAgentManager.getInstance().getAutAgents());
        }
        m_addressViewer.setInput(m_viewModel);
        
        createButtons(composite);
        
        return composite;
    }

    /**
     * Adds buttons for managing server addresses to the given composite.
     * 
     * @param parent The parent composite. The parent must have a GridLayout.
     */
    private void createButtons(Composite parent) {
        Button addButton = new Button(parent, SWT.NONE);
        addButton.setText(Messages.AutAgentPreferencePageButtonAdd);
        addButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }

            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent event) {
                AutAgentManager.AutAgent newAUTAgent = 
                    new AutAgentManager.AutAgent(
                        EnvConstants.LOCALHOST_ALIAS, 
                        EnvConstants.AUT_AGENT_DEFAULT_PORT);
                m_viewModel.add(newAUTAgent);
                m_addressViewer.refresh();
                m_addressViewer.editElement(newAUTAgent, 0);
            }
            
        });
        final Button deleteButton = new Button(parent, SWT.NONE);
        deleteButton.setText(Messages.AutAgentPreferencePageButtonDelete);
        deleteButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }

            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent event) {
                IStructuredSelection sel = 
                    (IStructuredSelection)m_addressViewer.getSelection();
                m_viewModel.remove(sel.getFirstElement());
                m_addressViewer.refresh();
            }
            
        });
    }

    

    /**
     * {@inheritDoc}
     */
    public boolean performOk() {
        AutAgentManager.getInstance().setAutAgents(
              new TreeSet<AutAgentManager.AutAgent>(m_viewModel));
        AutAgentManager.getInstance().storeAutAgentList();

        return super.performOk();
    }

    /**
     * {@inheritDoc}
     */
    public void init(IWorkbench workbench) {
        setDescription(Messages.AutAgentPreferencePageDescription);
    }

    /**
     * Creates and initializes a table viewer for managing server addresses.
     * 
     * @param parent The parent composite. The parent must have a GridLayout.
     * @return the initialized table viewer.
     */
    @SuppressWarnings("synthetic-access")
    private TableViewer createTableViewer(Composite parent) {
        TableViewer viewer = 
            new TableViewer(parent, SWT.FULL_SELECTION | SWT.SINGLE);
        Table addressTable = viewer.getTable();
        addressTable.setLinesVisible(true);
        addressTable.setHeaderVisible(true);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.horizontalSpan = LAYOUT_NUM_COLUMNS;
        addressTable.setLayoutData(layoutData);
        viewer.setContentProvider(new AddressContentProvider());
        TableLayout tableLayout = new TableLayout();
        addressTable.setLayout(tableLayout);

        tableLayout.addColumnData(new ColumnWeightData(15, 100));
        TableViewerColumn column = new TableViewerColumn(viewer,
                SWT.NONE);
        column.getColumn().setText(Messages
                .AutAgentPreferencePageColumnHeaderHostName);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                return ((AutAgentManager.AutAgent)element).getName();
            }
        });
        column.setEditingSupport(
                new ServerHostNameEditingSupport(viewer));
        
        tableLayout.addColumnData(new ColumnWeightData(10, 100));
        column = new TableViewerColumn(viewer,
                SWT.NONE);
        column.getColumn().setText(Messages
                .AutAgentPreferencePageColumnHeaderPort);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                return String.valueOf(
                        ((AutAgentManager.AutAgent)element).getPort());
            }
        });
        column.setEditingSupport(new ServerPortEditingSupport(viewer));

        // Prevents the "Return / Enter" key from closing the dialog when the 
        // table has focus. The event should be handled by the table itself
        // (by activating a cell editor).
        addressTable.addListener(SWT.Traverse, new Listener() {
            public void handleEvent(Event event) {
                if (event.detail == SWT.TRAVERSE_RETURN) {
                    // don't forward to the default button
                    event.doit = false;
                }
            }
        });

        return viewer;
    }
}
