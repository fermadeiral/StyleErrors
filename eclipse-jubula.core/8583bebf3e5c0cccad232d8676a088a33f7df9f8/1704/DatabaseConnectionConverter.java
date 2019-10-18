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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jubula.client.core.Activator;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.persistence.DatabaseConnectionInfo;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for converting Database Connection preferences 
 * to/from Strings.
 * 
 * @author BREDEX GmbH
 * @created 02.02.2011
 */
public class DatabaseConnectionConverter {

    /** ID of preference containing configured database connections */
    public static final String PREF_DATABASE_CONNECTIONS = 
        "org.eclipse.jubula.client.preference.databaseConnections"; //$NON-NLS-1$
    
    /** 
     * bidirectional mapping from a database connection type identifier 
     * (String) to the corresponding connection info class (Class) 
     */
    public static final BidiMap CONNECTION_CLASS_LOOKUP = 
        new DualHashBidiMap();
    
    static {
        // these values are used for storing / retrieving Database Connection
        // preferences, so change them with care
        CONNECTION_CLASS_LOOKUP.put("H2", H2ConnectionInfo.class); //$NON-NLS-1$
        CONNECTION_CLASS_LOOKUP.put("Oracle", OracleConnectionInfo.class); //$NON-NLS-1$
        CONNECTION_CLASS_LOOKUP.put("PostGreSQL", PostGreSQLConnectionInfo.class); //$NON-NLS-1$
        CONNECTION_CLASS_LOOKUP.put("MySQL", MySQLConnectionInfo.class); //$NON-NLS-1$
    }
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(DatabaseConnectionConverter.class);
    
    /** string for delimiting serialized Database Connections */
    private static final String CONNECTION_SEPARATOR = "\n\n"; //$NON-NLS-1$
    
    /** string for splitting serialized Database Connections */
    private static final String CONNECTION_SPLIT_REGEX = 
        Pattern.quote(CONNECTION_SEPARATOR);

    /** string for delimiting serialized Database Connection properties */
    private static final String PROPERTY_SEPARATOR = "\n"; //$NON-NLS-1$

    /** string for splitting serialized Database Connection properties */
    private static final String PROPERTY_SPLIT_REGEX = 
        Pattern.quote(PROPERTY_SEPARATOR);

    /**
     * Private constructor for utility class.
     */
    private DatabaseConnectionConverter() {
        // nothing to initialize
    }

    /**
     * 
     * @return the 0..n Database Connections found in the Preferences.
     */
    public static List<DatabaseConnection> computeAvailableConnections() {
        return DatabaseConnectionConverter.convert(
                Platform.getPreferencesService().getString(
                        Activator.PLUGIN_ID, 
                        DatabaseConnectionConverter.PREF_DATABASE_CONNECTIONS, 
                        StringUtils.EMPTY, null));
    }
    
    /**
     * 
     * @param preferenceValue String representation of 
     *                        0..n Database Connections.
     * @return the 0..n Database Connections represented by the given 
     *         parameter.
     */
    public static List<DatabaseConnection> convert(String preferenceValue) {
        List<DatabaseConnection> connectionList = 
            new LinkedList<DatabaseConnection>();
        if (StringUtils.isNotBlank(preferenceValue)) {
            for (String connection 
                    : preferenceValue.split(CONNECTION_SPLIT_REGEX)) {
                String[] connInfo = connection.split(PROPERTY_SPLIT_REGEX);
                if (connInfo.length < 2 || connInfo.length % 2 != 0) {
                    // either there are not enough entries, or the number of 
                    // property names and values do not match
                    LOG.error(NLS.bind(
                            Messages.DatabaseConnectionInvalidPreferenceString, 
                            connection));
                    continue;
                }
                Map<String, Object> beanProps = new HashMap<String, Object>();
                for (int i = 2; i < connInfo.length; i = i + 2) {
                    beanProps.put(connInfo[i], connInfo[i + 1]);
                }
                Class<? extends DatabaseConnectionInfo> infoClass = 
                    (Class)CONNECTION_CLASS_LOOKUP.get(connInfo[0]);
                if (infoClass == null) {
                    // no corresponding class could be found for the 
                    // connection type
                    LOG.error(NLS.bind(
                            Messages.DatabaseConnectionInvalidPreferenceString, 
                            connection));
                    continue;
                }
                try {
                    DatabaseConnectionInfo infoBean = infoClass.newInstance();
                    BeanUtils.populate(infoBean, beanProps);
                    connectionList.add(
                            new DatabaseConnection(connInfo[1], infoBean));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                }
                
            }
        }

        return connectionList;
    }

    /**
     * 
     * @param elements 0..n Database Connections.
     * @return String representation of the given Database Connections.
     */
    public static String convert(
            DatabaseConnection[] elements) {

        return serializeDatabaseList(elements);
    }

    /**
     * 
     * @param connections The connections to serialize.
     * @return a String containing all of the information provided in the 
     *         method argument. 
     */
    private static String serializeDatabaseList(
            DatabaseConnection[] connections) {
        
        StringBuilder sb = new StringBuilder();
        for (DatabaseConnection conn : connections) {
            sb.append(CONNECTION_CLASS_LOOKUP.getKey(
                    conn.getConnectionInfo().getClass()));
            sb.append(PROPERTY_SEPARATOR);
            sb.append(serialize(conn));
            sb.append(CONNECTION_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * 
     * @param connection The connection to represent as a string. Must not be 
     *                   <code>null</code>.
     * @return a persistable String representation of the given object.
     */
    private static String serialize(DatabaseConnection connection) {
        Validate.notNull(connection);

        StringBuilder sb = new StringBuilder();
        sb.append(connection.getName()).append(PROPERTY_SEPARATOR);
        for (PropertyDescriptor propDesc 
                : PropertyUtils.getPropertyDescriptors(
                        connection.getConnectionInfo())) {
            String propName = propDesc.getName();
            try {
                // only save writable properties, as we will not be able to 
                // set read-only properties when reading connection info back 
                // in from the preference store 
                if (PropertyUtils.isWriteable(
                        connection.getConnectionInfo(), propName)) {
                    sb.append(propName).append(PROPERTY_SEPARATOR)
                        .append(BeanUtils.getProperty(
                                connection.getConnectionInfo(), propName))
                        .append(PROPERTY_SEPARATOR);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
