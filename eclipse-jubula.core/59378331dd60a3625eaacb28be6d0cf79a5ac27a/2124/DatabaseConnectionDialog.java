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
package org.eclipse.jubula.client.ui.dialogs;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnection;
import org.eclipse.jubula.client.core.preferences.database.H2ConnectionInfo;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.wizards.pages.DatabaseConnectionWizardPage;


/**
 * @author BREDEX GmbH
 * @created 17.01.2011
 */
public class DatabaseConnectionDialog extends Wizard {

    /** ID for the "Database Connection" page */
    private static final String DB_CONN_PAGE_ID = 
        "org.eclipse.jubula.client.ui.wizards.pages.DatabaseConnectionWizardPage"; //$NON-NLS-1$
    
    /** the "Database Connection" page */
    private DatabaseConnectionWizardPage m_databaseConnectionPage;

    /** the database connection being edited in the wizard */
    private DatabaseConnection m_editedConnection;
    
    /**
     * Constructor
     */
    public DatabaseConnectionDialog() {
        this(new DatabaseConnection(
                Messages.DatabaseConnectionDialogDefaultName,
                new H2ConnectionInfo()));
    }

    /**
     * Constructor
     * 
     * @param connectionToEdit The connection to edit within this wizard.
     */
    public DatabaseConnectionDialog(DatabaseConnection connectionToEdit) {
        m_editedConnection = connectionToEdit;
        m_databaseConnectionPage = 
            new DatabaseConnectionWizardPage(DB_CONN_PAGE_ID, connectionToEdit);
    }

    /**
     * {@inheritDoc}
     */
    public void addPages() {
        super.addPages();
        addPage(m_databaseConnectionPage);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performFinish() {
        return true;
    }

    /**
     * 
     * @return the edited database connection.
     */
    public DatabaseConnection getEditedConnection() {
        return m_editedConnection;
    }
}
