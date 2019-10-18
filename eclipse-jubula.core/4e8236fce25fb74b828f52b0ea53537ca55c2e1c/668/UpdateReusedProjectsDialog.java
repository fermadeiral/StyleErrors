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
package org.eclipse.jubula.client.ui.rcp.wizards.pages;

import java.awt.Desktop;
import java.net.URI;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author BREDEX GmbH
 * @created 01.07.2016
 */
public class UpdateReusedProjectsDialog extends TitleAreaDialog
    implements ISelectionChangedListener {

    /** Title of project name column */
    private static final String PROJECT_NAME_COLUMN = "Project Name"; //$NON-NLS-1$
    
    /** Title of current project version column */
    private static final String CURRENT_VERSION = "Current version"; //$NON-NLS-1$
    
    /** Title of new project version column */
    private static final String NEW_VERSION = "New version"; //$NON-NLS-1$
    
    /** Margin size */
    private static final int MARGIN = 20; 
    
    /** info about reused projects and newest versions */
    private Map<IReusedProjectPO, ProjectVersion> m_oldReusedProjects;
    
    /** don´t ask again */
    private Button m_dontAskAgain;
    
    /** Project name label provider */
    private ColumnLabelProvider m_nameProvider = new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
            if (element instanceof Map.Entry) {
                Map.Entry project = (Map.Entry) element;
                if (project.getKey() instanceof IReusedProjectPO) {
                    return ((IReusedProjectPO) project.getKey()).getName();
                }
            }
            return StringConstants.EMPTY;
        }
    };
    
    /** Current version of reused project */
    private ColumnLabelProvider m_currentVersionProvider =
            new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
            if (element instanceof Map.Entry) {
                Map.Entry project = (Map.Entry) element;
                if (project.getKey() instanceof IReusedProjectPO) {
                    return ((IReusedProjectPO) project.getKey())
                            .getVersionString();
                }
            }
            return StringConstants.EMPTY;
        }
    };
    
    /** Newest version of reused project */
    private ColumnLabelProvider m_newVersionProvider =
            new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
            if (element instanceof Map.Entry) {
                Map.Entry project = (Map.Entry) element;
                if (project.getValue() instanceof ProjectVersion) {
                    return ((ProjectVersion) project.getValue()).toString();
                }
            }
            return StringConstants.EMPTY;
        }
    };

    /**
     * @param shell active shell
     * @param oldReusedProjects info about reused projects and newest versions
     */
    public UpdateReusedProjectsDialog(Shell shell, Map<IReusedProjectPO,
            ProjectVersion> oldReusedProjects) {
        super(shell);
        m_oldReusedProjects = oldReusedProjects;
    }

    @Override
    public Control createDialogArea(Composite parent) {
        setTitle(Messages.UpdateReusedProjectsTitle);
        setTitleImage(IconConstants.getImage(IconConstants.BIG_PROJECT_STRING));
        setMessage(Messages.UpdateReusedProjectsMessage);

        GridData gridData;
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = true;
        parent.setLayout(layout);

        LayoutUtil.createSeparator(createComposite(parent, 0, MARGIN));

        TableViewer viewer = new TableViewer(createComposite(parent), SWT.NONE);
        viewer.addSelectionChangedListener(this);
        viewer.setUseHashlookup(true);
        viewer.setContentProvider(new GeneralContentProvider());
        createColumns(viewer);
        viewer.setInput(m_oldReusedProjects);
        Table table = viewer.getTable();
        table.setData(SwtToolkitConstants.WIDGET_NAME,
                Messages.UpdateReusedProjectsWidgetName);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        GridData tableGridData = new GridData(GridData.FILL_BOTH);
        tableGridData.grabExcessVerticalSpace = true;
        tableGridData.horizontalSpan = 3;
        table.setLayoutData(tableGridData);

        createComposite(parent, MARGIN, MARGIN);
        createDontAskCheckBox(createComposite(parent));
        createOkLabel(createComposite(parent));
        
        LayoutUtil.createSeparator(createComposite(parent, MARGIN, 0));
        
        Plugin.getHelpSystem().setHelp(parent,
                ContextHelpIds.PROJECT_USED_PROPERTY_PAGE);
        return parent;
    }
    
    /**
     * @param parent parent composite
     * @return the new composite
     */
    private Composite createComposite(Composite parent) {
        return createComposite(parent, 0, 0);
    }
    
    /**
     * @param parent parent composite
     * @param top margin
     * @param bottom margin
     * @return new composite
     */
    private Composite createComposite(Composite parent, int top, int bottom) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(gridData);
        GridLayout layout = new GridLayout();
        layout.marginTop = top;
        layout.marginBottom = bottom;
        composite.setLayout(layout);
        return composite;
    }
    
    /**
     * Creates the OK label.
     * @param parent the parent composite
     */
    private void createOkLabel(Composite parent) {
        String text = Messages.UpdateReusedProjectsMessage;
        Label nextLabel = UIComponentHelper.createLabelWithText(parent, text);
        Link link = new Link(parent, SWT.NONE);
        link.setText(Messages.DatabaseMigrationWebsiteLinkLabel);
        link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    openUri(Messages.DatabaseMigrationWebsiteLink);
                }
            });
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.END;
        nextLabel.setLayoutData(gridData);
    }
    
    /**
     * @param uriString string of url
     */
    private void openUri(String uriString) {
        Desktop desktop = Desktop.isDesktopSupported()
                ? Desktop.getDesktop() : null;
        if (desktop != null
                && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                URI uri = new URI(uriString);
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Column creator
     * @param tableViewer parent viewer
     */
    private void createColumns(TableViewer tableViewer) {
        createTableViewerColumn(tableViewer, PROJECT_NAME_COLUMN, 200)
            .setLabelProvider(m_nameProvider);
        createTableViewerColumn(tableViewer, CURRENT_VERSION, 150)
            .setLabelProvider(m_currentVersionProvider);
        createTableViewerColumn(tableViewer, NEW_VERSION, 150)
            .setLabelProvider(m_newVersionProvider);
    }

    /**
     * @param tableViewer parent viewer
     * @param title of column
     * @param bound of column
     * @return column
     */
    private TableViewerColumn createTableViewerColumn(TableViewer tableViewer,
            String title, int bound) {
        final TableViewerColumn viewerColumn =
                new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }

    /** Content provider */
    private static class GeneralContentProvider
        implements IStructuredContentProvider {
        @Override
        public void dispose() {
            // noting
        }
        @Override
        public void inputChanged(Viewer viewer, Object oldInput,
                Object newInput) {
            // noting
        }
        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement != null && inputElement instanceof Map) {
                return ((Map) inputElement).entrySet().toArray();
            }
            return null;
        }
    }

    /** Creates the don´t ask again check box
     * @param parent 
     */
    private void createDontAskCheckBox(Composite parent) {
        m_dontAskAgain = new Button(parent, SWT.CHECK);
        m_dontAskAgain.setText(Messages.InfoNaggerDialogToggleMsg);
        m_dontAskAgain.setSelection(Plugin.getDefault().getPreferenceStore()
                .getBoolean(Constants.UPDATE_REUSED_PROJECT_KEY));
        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = GridData.END;
        m_dontAskAgain.setLayoutData(data);
    }

    /** Disable the selection */
    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
        if (!event.getSelection().isEmpty()
                && event.getSource() instanceof TableViewer) {
            ((TableViewer)event.getSource())
                .setSelection(StructuredSelection.EMPTY);
        }
    }
    
    /** Save the check box selection*/
    protected void okPressed() {
        Plugin.getDefault().getPreferenceStore().setValue(
                Constants.UPDATE_REUSED_PROJECT_KEY,
                m_dontAskAgain.getSelection());
        super.okPressed();
    }
}
