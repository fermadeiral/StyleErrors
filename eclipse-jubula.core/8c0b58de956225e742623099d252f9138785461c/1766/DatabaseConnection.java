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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;

/**
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public class DatabaseConnection {

    /** name of <code>name</code> property */
    public static final String PROP_NAME_NAME = "name"; //$NON-NLS-1$

    /** name of <code>connectionInfo</code> property */
    public static final String PROP_NAME_CONN_INFO = "connectionInfo"; //$NON-NLS-1$

    /** property change support */
    private PropertyChangeSupport m_propChangeSupport =
        new PropertyChangeSupport(this);
    
    /** the name by which the database is referenced */
    private String m_name;

    /** the actual information that will be used to establish the connection */
    private DatabaseConnectionInfo m_connectionInfo;

    /**
     * Constructor
     * 
     * @param name The name by which the database will be referenced.
     * @param connectionInfo The actual information that will be used to 
     *                       establish the connection.
     */
    public DatabaseConnection(
            String name, DatabaseConnectionInfo connectionInfo) {
        setName(name);
        setConnectionInfo(connectionInfo);
    }

    /**
     * Constructor
     * 
     * @param toCopy The source object to copy from. The created object is a 
     *               recursively deep copy of <code>toCopy</code>.
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public DatabaseConnection(DatabaseConnection toCopy) 
        throws IllegalAccessException, InstantiationException, 
               InvocationTargetException, NoSuchMethodException {
        setName(toCopy.getName());
        DatabaseConnectionInfo infoToCopy = 
            toCopy.getConnectionInfo();
        setConnectionInfo(
                (DatabaseConnectionInfo)BeanUtils.cloneBean(infoToCopy));
        
    }

    /**
     * 
     * @return the name by which the database is referenced.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * 
     * @param name The new name by which the database will be referenced.
     */
    public void setName(String name) {
        Validate.notEmpty(name);
        String oldValue = m_name;
        m_name = name;
        m_propChangeSupport.firePropertyChange(
                PROP_NAME_NAME, oldValue, m_name);
    }

    /**
     * Assigns a completely new set of connection information to the receiver.
     * 
     * @param connectionInfo The new connection information.
     */
    public void setConnectionInfo(DatabaseConnectionInfo connectionInfo) {
        Validate.notNull(connectionInfo);
        DatabaseConnectionInfo oldValue = m_connectionInfo;
        m_connectionInfo = connectionInfo;
        m_propChangeSupport.firePropertyChange(
                PROP_NAME_CONN_INFO, oldValue, m_connectionInfo);
    }

    /**
     * 
     * @return the object currently responsible for managing connection 
     *         information.
     */
    public DatabaseConnectionInfo getConnectionInfo() {
        return m_connectionInfo;
    }

    /**
     * standard bean support
     * @param l standard bean support
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        m_propChangeSupport.addPropertyChangeListener(l);
    }
    
    /**
     * standard bean support
     * @param l standard bean support
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        m_propChangeSupport.removePropertyChangeListener(l);
    }
}
