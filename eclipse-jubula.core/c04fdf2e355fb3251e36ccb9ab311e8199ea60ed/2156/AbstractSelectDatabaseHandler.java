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
package org.eclipse.jubula.client.ui.handlers.project;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnection;
import org.eclipse.jubula.client.core.preferences.database.DatabaseConnectionConverter;
import org.eclipse.jubula.client.core.preferences.database.H2ConnectionInfo;
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.businessprocess.SecurePreferenceBP;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.dialogs.DBLoginDialog;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 18.04.2005
 */
public abstract class AbstractSelectDatabaseHandler extends AbstractHandler {
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AbstractSelectDatabaseHandler.class);
    /** the "select database ..." command parameter */
    private static final  String DB_COMMAND_PARAMETER =
            "org.eclipse.jubula.client.ui.selectDatabaseParameter"; //$NON-NLS-1$
    
    /**
     * Checks if automatic database login is active
     * 
     * @return true if active and false otherwise
     */
    public static boolean shouldAutoConnectToDB() {
        return !StringConstants.EMPTY.equals(Plugin.getDefault()
                .getPreferenceStore()
                .getString(Constants.AUTOMATIC_DATABASE_CONNECTION_KEY));
    }
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        String explicitSelectionValue = event.getParameter(
                DB_COMMAND_PARAMETER);
        boolean explicitSelection = Boolean.valueOf(explicitSelectionValue);
        IStatus returnStatus = Status.CANCEL_STATUS;
        boolean performLogin = false;
        String userName = StringConstants.EMPTY;
        String pwd = StringConstants.EMPTY;
        DatabaseConnectionInfo dbInfo = null;
        
        Credential credentials = new Credential(DatabaseConnectionConverter
                .computeAvailableConnections());

        if (credentials.checkH2DatabaseConnection()) {
            credentials.setH2DatabaseCredentials();
            dbInfo = credentials.getDatabaseInfo();
            userName = credentials.getDBusername();
            pwd = credentials.getDBpassword();
            performLogin = true;
        } else if (explicitSelection || !shouldAutoConnectToDB()) {
            DBLoginDialog dialog = new DBLoginDialog(getActiveShell());
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                userName = dialog.getUser();
                pwd = dialog.getPwd();
                dbInfo = dialog.getDatabaseConnection().getConnectionInfo();
                performLogin = true;
            }
        } else if (shouldAutoConnectToDB()) {
            credentials.setAutoConnCredentials();
            dbInfo = credentials.getDatabaseInfo();
            userName = credentials.getDBusername();
            pwd = credentials.getDBpassword();
            performLogin = true;
        }
        if (performLogin) {
            try {
                returnStatus = connectToDatabase(userName, pwd, dbInfo);
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        while (performLogin && returnStatus == null) {
            returnStatus = createLoginDialogAgain();
        }
        Persistor.setUser(null);
        Persistor.setPw(null);
        return returnStatus;
    }
    
    /**
     * 
     * @author BREDEX GmbH
     * @created 24.04.2012
     *
     */
    private static class Credential {
        /** the available database connections */
        private List<DatabaseConnection> m_availableDbConnections;
        
        /** database connection type */
        private DatabaseConnectionInfo m_dbInfo;
        
        /** the database user name */
        private String m_userName;

        /** the database password */
        private String m_pwd;

        /**
         * the inner class constructor
         * 
         * @param connections list of the available database
         *        connections
         */
        private Credential(List<DatabaseConnection> connections) {
            m_availableDbConnections = connections;
        }
        
        /**
         * Checks if only an H2 database connection exists
         * 
         * @return true if only an H2 database connection
         * exists and false otherwise
         */
        private boolean checkH2DatabaseConnection() {
            boolean h2DB = false;
            if (m_availableDbConnections.size() == 1
                && m_availableDbConnections.get(0).getConnectionInfo() 
                    instanceof H2ConnectionInfo) {
                h2DB = true;
            }
            return h2DB;
        }
        
        /**
         * sets the database type, user name and password for
         * the H2 database connection
         */
        private void setH2DatabaseCredentials() {
            m_dbInfo = m_availableDbConnections.get(0).getConnectionInfo();
            m_userName = m_dbInfo.getProperty(
                    PersistenceUnitProperties.JDBC_USER);
            m_pwd = m_dbInfo.getProperty(
                    PersistenceUnitProperties.JDBC_PASSWORD);
            
        }
        
        /**
         * Sets the database type, user name and password for the automatic
         * database connection
         */
        private void setAutoConnCredentials() {
            IPreferenceStore store = Plugin.getDefault().getPreferenceStore();
            for (DatabaseConnection currentConnection 
                    : m_availableDbConnections) {
                String profileName = currentConnection.getName();
                if (profileName.equals(store
                        .getString(Constants
                                .AUTOMATIC_DATABASE_CONNECTION_KEY))) {
                    m_dbInfo = currentConnection.getConnectionInfo();
                    SecurePreferenceBP spBP = SecurePreferenceBP.getInstance();
                    m_userName = spBP.getUserName(profileName);
                    m_pwd = spBP.getPassword(profileName);
                }
            }
        }
        
        /**
         * Returns the database informations
         * 
         * @return the database connection type
         */
        private DatabaseConnectionInfo getDatabaseInfo() {
            return m_dbInfo;
        }
        
        /**
         * Returns the database user name
         * 
         * @return the database user name
         */
        private String getDBusername() {
            return m_userName;
        }
        
        /**
         * Returns the database password
         * 
         * @return the database password
         */
        private String getDBpassword() {
            return m_pwd;
        }
    }
        
    /**
     * 
     * @param username
     *            the username to use
     * @param pwd
     *            the password to use
     * @param info
     *            the database connection info
     * @return a status
     */
    private IStatus connectToDatabase(final String username, final String pwd,
        final DatabaseConnectionInfo info) throws InterruptedException {
        final AtomicReference<IStatus> returnStatus = 
            new AtomicReference<IStatus>(Status.CANCEL_STATUS);
        try {
            PlatformUI.getWorkbench().getProgressService()
                    .run(true, false, new IRunnableWithProgress() {
                        /** {@inheritDoc} */
                        public void run(IProgressMonitor monitor) {
                            monitor.beginTask(Messages.PluginConnectProgress,
                                    IProgressMonitor.UNKNOWN);
                            clearClient();
                            Persistor.setUser(username);
                            Persistor.setPw(pwd);
                            Persistor.setDbConnectionName(info);

                            if (Persistor.instance() != null) {
                                CompNamePM.dispose();
                                GeneralStorage.getInstance().dispose();
                                if (LockManager.isRunning()) {
                                    LockManager.instance().dispose();
                                }
                                Persistor.instance().dispose();
                            }
                            if (Persistor.init()) {
                                LockManager.instance();
                                writeLineToConsole(
                                    Messages.SelectDatabaseConnectSuccessful);
                                returnStatus.set(Status.OK_STATUS);
                            } else {
                                IPreferenceStore store = Plugin.getDefault().
                                        getPreferenceStore();
                                store.setToDefault(Constants.
                                        AUTOMATIC_DATABASE_CONNECTION_KEY);
                                returnStatus.set(null);
                                writeLineToConsole(
                                    Messages.SelectDatabaseConnectFailed);
                            }
                        }
                    });
        } catch (InvocationTargetException ite) {
            // Exception occurred during operation
            log.error(ite.getLocalizedMessage(), ite.getCause());
        } catch (InterruptedException ie) {
            throw ie;
        }

        return returnStatus.get();
    }
    
    /**
     * Creates a new database login dialog when database
     * connection failed
     * 
     * @return returns the status OK when database connection
     *         was successful and null otherwise
     */
    private IStatus createLoginDialogAgain() {
        IStatus returnStatus = Status.CANCEL_STATUS;
        boolean performLogin = false;
        String userName = StringConstants.EMPTY;
        String pwd = StringConstants.EMPTY;
        DatabaseConnectionInfo dbInfo = null;
        
        DBLoginDialog dialog = new DBLoginDialog(getActiveShell());
        dialog.create();
        dialog.setErrorMessage(Messages.DatabaseConnectionErrorMessage);
        dialog.getErrorMessage();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        if (dialog.getReturnCode() == Window.OK) {
            userName = dialog.getUser();
            pwd = dialog.getPwd();
            dbInfo = dialog.getDatabaseConnection().getConnectionInfo();
            performLogin = true;
        }
        if (performLogin) {
            try {
                returnStatus = connectToDatabase(userName, pwd, dbInfo);
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
        return returnStatus;
    }
    
    /**
     * Clears the ITE of all Project-related elements.
     */
    protected abstract void clearClient();

    /**
     * Writes the given line to the ITE's console.
     * 
     * @param line The text to write to the console.
     */
    protected abstract void writeLineToConsole(String line);
}