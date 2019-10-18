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
package org.eclipse.jubula.client.core.persistence;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;

import org.eclipse.persistence.config.BatchWriting;

/**
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public abstract class DatabaseConnectionInfo {

    /** name of <code>connectionUrl</code> property */
    public static final String PROP_NAME_CONN_URL = "connectionUrl"; //$NON-NLS-1$

    /** property change support */
    private PropertyChangeSupport m_propChangeSupport = 
        new PropertyChangeSupport(this);

    /** properties to use when initializing the JPA provider */
    private Properties m_jpaProperties = new Properties();
    
    /**
     * Sets the given property for the receiver, overwriting if the property 
     * is already defined.
     * 
     * @param key The property key.
     * @param value The new value for the property.
     */
    protected void setProperty(String key, String value) {
        m_jpaProperties.setProperty(key, value);
    }
 
    /**
     * 
     * @param key The property key.
     * @return the value of the property with the given key. Returns 
     *         <code>null</code> if no such property is defined or if the 
     *         value of the property is <code>null</code>.
     */
    public String getProperty(String key) {
        return m_jpaProperties.getProperty(key);
    }
    
    /**
     * 
     * @return the connection URL constructed based on the receiver's current
     *         information.
     */
    public abstract String getConnectionUrl();

    /**
     * 
     * @return the name of the JDBC driver class to use with receiver.
     */
    public abstract String getDriverClassName();

    /**
     * Informs all property change listeners that the 
     * <code>connectionUrl</code> property has changed.
     */
    protected final void fireConnectionUrlChanged() {
        m_propChangeSupport.firePropertyChange(
                PROP_NAME_CONN_URL, null, getConnectionUrl());
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

    /**
     * Get a configuration parameter. This method is intended to be overwritten
     * in DB specific subclasses.
     * 
     * @return the batch writing value. EclipseLinks default is "None".
     */
    public String getBatchWriting() {
        return BatchWriting.DEFAULT;
    }

    /**
     * Get a configuration parameter. This method is intended to be overwritten 
     * in DB specific subclasses.
     * @return the batch writing size value. EclipseLinks default depends on 
     * the kind of BatchWriting. Null should be interpreted as "Don't use the value,
     * stick to the default". The return value is a String because it is used in
     * a properties map.
     */
    public String getBatchWritingSize() {
        return null;
    }
    
    /**
     * We discovered that on initial db load or after large imports the Oracle query
     * optimizer would use outdated statistics. This is especially annoying when
     * setting up new schemas. To update the statistics a SQL/PLSQL statement is needed.
     * @return a statement which when send to the db will update the statistics.
     */
    public String getStatisticsCommand() {
        return null;
    }
}
