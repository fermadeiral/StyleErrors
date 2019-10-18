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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ImportFileBP.IProjectImportInfoProvider;
import org.eclipse.jubula.client.ui.rcp.wizards.pages.ImportProjectsWizardPage;


/**
 * @author BREDEX GmbH
 * @created May 20, 2010
 */
public class ImportProjectDialog extends Wizard {

    /** ID for the "Import Projects" page */
    private static final String IMPORT_PROJECTS_PAGE_ID =
        "ImportProjectDialog.ImportProjectsPage"; //$NON-NLS-1$
    
    /** the "Import Projects" page */
    private ImportProjectsWizardPage m_importProjectsPage;
    
    /**
     * Constructor
     */
    public ImportProjectDialog() {
        m_importProjectsPage = 
            new ImportProjectsWizardPage(IMPORT_PROJECTS_PAGE_ID);
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        addPage(m_importProjectsPage);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        return true;
    }

    /**
     * 
     * @return am object containing the information entered into this dialog.
     */
    public IProjectImportInfoProvider getImportInfoProvider() {
        return m_importProjectsPage;
    }
}
