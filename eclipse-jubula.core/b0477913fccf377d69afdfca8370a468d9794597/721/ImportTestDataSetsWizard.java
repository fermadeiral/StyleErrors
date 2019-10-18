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
package org.eclipse.jubula.client.ui.rcp.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.wizards.pages.ImportXLSTestdataWizardPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;


/**
 * Wizard for exporting Test Result details.
 * 
 * @author BREDEX GmbH
 * @created Jun 25, 2010
 */
public class ImportTestDataSetsWizard extends Wizard {
    /**
     * <code>WIZARD_ID</code>
     */
    public static final String ID = "org.eclipse.jubula.client.ui.rcp.importWizard.ImportTestDataSetsWizard"; //$NON-NLS-1$

    /** ID for the "Import CSV Data Set" page */
    private static final String IMPORT_XLS_DATA_SET_PAGE_ID = "ImportTestDataSetsWizard.ImportXLSPage"; //$NON-NLS-1$

    /**
     * <code>m_importCSVData</code>
     */
    private ImportXLSTestdataWizardPage m_importCSVData;

    /**
     * <code>m_selection</code>
     */
    private IStructuredSelection m_selection;

    /**
     * <code>m_ctde</code>
     */
    private CentralTestDataEditor m_ctde;
    
    /**
     * @param activeWorkbenchWindow activeWorkbenchWindow
     */
    public ImportTestDataSetsWizard(IWorkbenchWindow activeWorkbenchWindow) {
        super();
        setNeedsProgressMonitor(true);
        setWindowTitle(Messages.ImportTestDataSetsWizardWindowTitle);
        IEditorPart activeEditor = activeWorkbenchWindow
                .getActivePage().getActiveEditor();
        if (activeEditor instanceof CentralTestDataEditor) {
            m_ctde = (CentralTestDataEditor)activeEditor;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        m_importCSVData = new ImportXLSTestdataWizardPage(
                IMPORT_XLS_DATA_SET_PAGE_ID);
        addPage(m_importCSVData);
    }

    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        return m_importCSVData.finish(m_selection, m_ctde);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (m_ctde != null && !m_ctde.isDirty()) {
            try {
                m_ctde.getEditorHelper().resetEditableState();
                m_ctde.getEditorHelper().getEditSupport().reloadEditSession();
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForEditor(e, m_ctde);
            }
        }
    }

}
