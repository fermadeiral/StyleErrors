/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.properties.dialogs.contexts;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;
import org.eclipse.jubula.client.teststyle.i18n.Messages;
import org.eclipse.jubula.client.teststyle.properties.dialogs.DlgUtils;
import org.eclipse.jubula.client.teststyle.properties.dialogs.contexts.provider.ContextCheckProvider;
import org.eclipse.jubula.client.teststyle.properties.dialogs.contexts.provider.ContextContentProvider;
import org.eclipse.jubula.client.teststyle.properties.dialogs.contexts.provider.ContextLabelProvider;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;


/**
 * @author marcell
 * @created Oct 25, 2010
 */
public class EditContextDialog extends TrayDialog {

    // Constants
    /** Style for the CheckboxTableViewer */
    public static final int CON_STL =
        SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
    /** Name of the edit contexts window */
    public static final String CONT_TITLE = Messages.EditContextDialogTitle;
    /** Header text for the name column */
    private static final String NAME_COLUMN = Messages.EditContextColumnName;
    /** Header text for the description column */
    private static final String DESCRIPTION_COLUMN = 
        Messages.EditContextColumnDescription;
    /** Column width for the name column */
    private static final int NAME_COLUMN_WIDTH = 200;
    /** Column width for the description column */
    private static final int DESCRIPTION_COLUMN_WIDTH = 100;
    
    /** ContextHelpId for TESTSTYLE_PROPERTY_PAGE_EDIT_CONTEXT */
    private static final String TESTSTYLE_PROPERTY_PAGE_EDIT_CONTEXT = 
            ContextHelpIds.PRAEFIX + "testStylePropertyPageEditContextContextId"; //$NON-NLS-1$
    
    // Members
    /** Check that will be edited */
    private BaseCheck m_check;
    /** TreeMap copy of the context activity */
    private Map<BaseContext, Boolean> m_contexts;
    
    // GUI-Components
    /** The listviewer with the elements */
    private CheckboxTableViewer m_tablView;
    

    /**
     * @param parentShell
     *            The parent shell
     * @param check
     *            The check that will be edited.
     */
    public EditContextDialog(Shell parentShell, BaseCheck check) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER 
                | SWT.APPLICATION_MODAL | SWT.RESIZE);
        this.m_check = check;
        this.m_contexts = 
            new TreeMap<BaseContext, Boolean>(check.getContexts());
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        // Creates a nicely fitting composite
        Composite composite = DlgUtils.createFillComposite(parent);
        // Creates the list
        createTable(composite);
        // Events for the list
        setTableEvents();
        
        // Set an approriate Title
        getShell().setText(CONT_TITLE);
        
        setHelpAvailable(true);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(),
                TESTSTYLE_PROPERTY_PAGE_EDIT_CONTEXT);
        return super.createDialogArea(parent);
    }

    /**
     * 
     * @param parent
     *            The composite where the list will be stored.
     */
    private void createTable(Composite parent) {
        m_tablView = CheckboxTableViewer.newCheckList(parent, CON_STL);
        
        m_tablView.setColumnProperties(new String[] { 
            NAME_COLUMN,
            DESCRIPTION_COLUMN,
        });
        Table table = m_tablView.getTable();

        // Then we configure the table approriate
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        

        // Now we add columns to this table
        TableColumn nameColumn = new TableColumn(table, SWT.NONE);
        nameColumn.setWidth(NAME_COLUMN_WIDTH);
        nameColumn.setText(NAME_COLUMN);

        TableColumn descriptionColumn = new TableColumn(table, SWT.NONE);
        descriptionColumn.setWidth(DESCRIPTION_COLUMN_WIDTH);
        descriptionColumn.setText(DESCRIPTION_COLUMN);
        
        m_tablView.setContentProvider(new ContextContentProvider());
        m_tablView.setCheckStateProvider(new ContextCheckProvider(m_contexts));
        m_tablView.setLabelProvider(new ContextLabelProvider());
        
        m_tablView.setInput(m_contexts);
        
        // Let the name column fit nicely
        nameColumn.pack();
        nameColumn.setWidth(nameColumn.getWidth() + 5);
        
        descriptionColumn.pack();
        descriptionColumn.setWidth(descriptionColumn.getWidth() + 5);
        
    }
    
    /**
     * Sets the events for the listviewer.
     */
    public void setTableEvents() {
        m_tablView.addCheckStateListener(new ICheckStateListener() {
            
            @SuppressWarnings("synthetic-access")
            public void checkStateChanged(CheckStateChangedEvent event) {
                BaseContext context = (BaseContext)event.getElement();
                m_contexts.put(context, event.getChecked());
                m_tablView.refresh();
            }
        });
    }
    
    /**
     * {@inheritDoc}
     */
    protected void okPressed() {
        super.okPressed();
        this.m_check.setContexts(m_contexts);
    }

}
