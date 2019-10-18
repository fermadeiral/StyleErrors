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
package org.eclipse.jubula.client.core.preferences.database;

import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;

/**
 * Base class for Connection Info that uses a hostname, port, and some kind of
 * database name/id.
 * 
 * @author BREDEX GmbH
 * @created 04.02.2011
 */
public abstract class AbstractHostBasedConnectionInfo 
        extends DatabaseConnectionInfo {

    /** name of <code>hostname</code> property */
    public static final String PROP_NAME_HOSTNAME = "hostname"; //$NON-NLS-1$

    /** name of <code>port</code> property */
    public static final String PROP_NAME_PORT = "port"; //$NON-NLS-1$

    /** name of <code>databaseName</code> property */
    public static final String PROP_NAME_DB_NAME = "databaseName"; //$NON-NLS-1$

    /** hostname of the computer on which the database is running */
    private String m_hostname = EnvConstants.LOCALHOST_ALIAS;
    
    /** port on which the database is running */
    private int m_port;
    
    /** the SID of the database instance */
    private String m_databaseName = "jubula"; //$NON-NLS-1$

    /**
     * Constructor
     * 
     * @param port The initial port for the created object.
     */
    public AbstractHostBasedConnectionInfo(int port) {
        m_port = port;
    }
    
    /**
     * 
     * @return the hostname of the computer on which the database is running.
     */
    public final String getHostname() {
        return m_hostname;
    }

    /**
     * 
     * @param hostname The hostname of the computer on which the 
     *                 database is running.
     */
    public final void setHostname(String hostname) {
        m_hostname = hostname;
        fireConnectionUrlChanged();
    }

    /**
     * 
     * @return the port on which the database is running.
     */
    public final int getPort() {
        return m_port;
    }

    /**
     * 
     * @param port The port on which the database is running.
     */
    public final void setPort(int port) {
        m_port = port;
        fireConnectionUrlChanged();
    }

    /**
     * 
     * @return the name of the database instance.
     */
    public final String getDatabaseName() {
        return m_databaseName;
    }

    /**
     * 
     * @param databaseName the name of the database instance.
     */
    public final void setDatabaseName(String databaseName) {
        m_databaseName = databaseName;
        fireConnectionUrlChanged();
    }
}
