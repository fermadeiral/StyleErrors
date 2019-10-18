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
package org.eclipse.jubula.client.teststyle.properties.dialogs.attributes;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.i18n.Messages;
import org.eclipse.jubula.client.teststyle.properties.dialogs.DlgUtils;
import org.eclipse.jubula.client.teststyle.properties.dialogs.attributes.provider.AttrCellModifier;
import org.eclipse.jubula.client.teststyle.properties.dialogs.attributes.provider.AttrContentProvider;
import org.eclipse.jubula.client.teststyle.properties.dialogs.attributes.provider.AttrLabelProvider;
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
 * @created Oct 22, 2010
 */
public class EditAttributeDialog extends TrayDialog {
    // Constants
    /** Style of the TableViewer */
    public static final int TABLE_STYLE = 
        SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER;
    /** Well tested column width! */
    public static final int COLUMN_WIDTH = 95; 
    /** Name of the second column */
    public static final String VALUE_COLUMN = Messages.EditAttributeColumnValue;
    /** Name of the third column */
    public static final String DESCRIPTION_COLUMN = 
        Messages.EditAttributeColumnDescription;
    /** Name of the edit attributes window */
    public static final String TITLE = Messages.EditAttributeDialogTitle;
    
    /** ContextHelpId for TESTSTYLE_PROPERTY_PAGE_EDIT_ATTRIBUTE */
    private static final String TESTSTYLE_PROPERTY_PAGE_EDIT_ATTRIBUTE = 
            ContextHelpIds.PRAEFIX + "testStylePropertyPageEditAttributeContextId"; //$NON-NLS-1$
    
    //Members
    /** Check which will be edited */
    private BaseCheck m_chk;
    /** Attributes which will be changed and saved */
    private Map<String, String> m_attributes;
    /** descriptions of the attributes */
    private Map<String, String> m_descriptions;
    
    // GUI-Components
    /** TableViewer of the dialog */
    private TableViewer m_tView;
    

    /**
     * @param parentShell
     *            The parent shell.
     * @param check
     *            check which will be edited
     */
    public EditAttributeDialog(Shell parentShell, BaseCheck check) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER 
                | SWT.APPLICATION_MODAL | SWT.RESIZE);
        m_chk = check;
        m_attributes = new HashMap<String, String>(check.getAttributes());
        m_descriptions = new HashMap<String, String>(check.getDescriptions());
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        // Create a nice area for my controls
        Composite composite = DlgUtils.createFillComposite(parent);

        // create the table
        createTable(composite);
        
        // Set an approriate title
        getShell().setText(TITLE);
        setHelpAvailable(true);
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(),
                TESTSTYLE_PROPERTY_PAGE_EDIT_ATTRIBUTE);
        return super.createDialogArea(parent);
    }

    /**
     * 
     * @param parent
     *            The composite where the table is set.
     */
    private void createTable(Composite parent) {

        // First we create the viewer and get the table
        m_tView = new TableViewer(parent, TABLE_STYLE);
        m_tView.setColumnProperties(new String[] { 
            DESCRIPTION_COLUMN,
            VALUE_COLUMN,
        });
        Table table = m_tView.getTable();

        // Then we configure the table approriate
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        // Now we add columns to this table        
        TableColumn descriptionColumn = new TableColumn(table, SWT.NONE);
        descriptionColumn.setWidth(COLUMN_WIDTH);
        descriptionColumn.setText(DESCRIPTION_COLUMN);

        TableColumn valueColumn = new TableColumn(table, SWT.NONE);
        valueColumn.setWidth(COLUMN_WIDTH);
        valueColumn.setText(VALUE_COLUMN);

        // Now we set the approriate provider and input
        m_tView.setContentProvider(new AttrContentProvider());
        m_tView.setLabelProvider(
                new AttrLabelProvider(m_attributes, m_descriptions));
        m_tView.setCellModifier(new AttrCellModifier(m_attributes, m_tView));

        // Setting the cell editors
        m_tView.setCellEditors(new CellEditor[] {
            new TextCellEditor(m_tView.getTable()),
            new TextCellEditor(m_tView.getTable()),
            new TextCellEditor(m_tView.getTable()),
        });

        // Now giving the contentprovider some input :3
        m_tView.setInput(m_attributes);
        
        // let them fit nicely
        descriptionColumn.pack();
        descriptionColumn.setWidth(descriptionColumn.getWidth() + 5);
    }

    /**
     * {@inheritDoc}
     */
    protected void okPressed() {
        super.okPressed();
        m_chk.setAttributes(m_attributes);
    }

}
