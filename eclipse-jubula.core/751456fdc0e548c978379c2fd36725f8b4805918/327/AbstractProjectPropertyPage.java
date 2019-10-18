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
package org.eclipse.jubula.client.ui.rcp.properties;

import javax.persistence.EntityNotFoundException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedIntText;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBFatalAbortException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * @author BREDEX GmbH
 * @created 09.11.2005
 */
public abstract class AbstractProjectPropertyPage extends PropertyPage {
    /**
     * <code>m_editSupport</code>
     */
    private EditSupport m_editSupport = null;
    
    /** work version for this session */
    private IProjectPO m_workProject = null;
    /**
     * @param es the editSupport
     */
    public AbstractProjectPropertyPage(EditSupport es) {
        m_editSupport = es;
    }
    
    /**
     * This constructor is needed for the extension point.
     * 
     * setEditSupport should be called after that.
     */
    public AbstractProjectPropertyPage() {
        // nothing
    }

    /**
     * @return es The editsupport.
     * @throws PMException if editSupport cannot 
     */
    public static EditSupport createEditSupport() throws PMException {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        EditSupport editSupport = 
            new EditSupport(project.getProjectProperties(), null);
        editSupport.lockWorkVersion();
        ProjectNameBP.getInstance().clearCache();
        return editSupport;
    }

    /**
     * @return shared project
     */
    public IProjectPO getProject() {
        if (m_workProject == null) {
            try {
                m_workProject = getEditSupport().getWorkProject();
            } catch (PMException e) {
                throw new JBFatalAbortException(
                        Messages.CantLoadProjectInEditSession, e,
                        MessageIDs.E_DATABASE_GENERAL);
            }
        }
        return m_workProject;
    }

    /**
     * Refreshes the project.
     */
    protected void refreshProject() throws ProjectDeletedException {
        GeneralStorage storage = GeneralStorage.getInstance();
        try {
            storage.getMasterSession().refresh(storage.getProject());
        } catch (EntityNotFoundException enfe) {
            // Occurs if any Object Mapping information has been deleted while
            // the Project Properties were being edited.
            // Refresh the entire master session to ensure that AUT settings
            // and Object Mappings are in sync
            storage.reloadMasterSession(new NullProgressMonitor());
        }
    }
    
    /**
     * @return shared edit support
     */
    protected EditSupport getEditSupport() {
        return m_editSupport;
    }
    
    /**
     * @param es - the new editsupport
     */
    public void setEditSupport(EditSupport es) {
        m_editSupport = es;
    }

    /**
     * {@inheritDoc}
     */
    public boolean performCancel() {
        Plugin.stopLongRunning();
        return super.performCancel();
    }
    
    /**
     * @param buttonToSyncTo
     *            the button to sync to
     * @param widgetToEnable
     *            the widget to enable / disable
     */
    protected void enableSelectionAndEnablementDependent(Button buttonToSyncTo,
        Control widgetToEnable) {
        if (buttonToSyncTo.isEnabled()) {
            widgetToEnable.setEnabled(buttonToSyncTo.getSelection());
            
            if (widgetToEnable instanceof CheckedIntText) {
                if (buttonToSyncTo.getSelection()) {
                    ((CheckedIntText) widgetToEnable).validate();
                } else {
                    widgetToEnable.setBackground(null);
                }
            }
        } else {
            widgetToEnable.setEnabled(false);
        }
    }
    
    /**
     * Creates a new composite.
     * @param parent The parent composite.
     * @param numColumns the number of columns for this composite.
     * @param alignment The horizontalAlignment (grabExcess).
     * @param horizontalSpace The horizontalSpace.
     * @return The new composite.
     */
    protected Composite createComposite(Composite parent, int numColumns, 
            int alignment, boolean horizontalSpace) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalAlignment = alignment;
        compositeData.grabExcessHorizontalSpace = horizontalSpace;
        composite.setLayoutData(compositeData);
        return composite;       
    }
    
    /**
     * @param parent the parent composite
     */
    protected void createEmptyLabel(Composite parent) {
        createLabel(parent, StringConstants.EMPTY);
    }

    /**
     * Creates a label for this page.
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    protected Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER,
                false, false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;
    }
    

    /**
     * Creates a separator line.
     * @param composite The parent composite.
     * @param horSpan The horizontal span.
     */
    protected void separator(Composite composite, int horSpan) {
        createLabel(composite, StringConstants.EMPTY);
        Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sepData = new GridData();
        sepData.horizontalAlignment = GridData.FILL;
        sepData.horizontalSpan = horSpan;
        sep.setLayoutData(sepData);
        createLabel(composite, StringConstants.EMPTY);
    }
}