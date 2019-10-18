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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.persistence.config.BatchWriting;

/**
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public class OracleConnectionInfo extends AbstractHostBasedConnectionInfo {
    /**
     * the JDBC driver class name 
     */
    public static final String DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver"; //$NON-NLS-1$
    
    /**
     * the JDBC connection prefix 
     */
    public static final String JDBC_PRE = "jdbc:oracle:thin:@"; //$NON-NLS-1$

    /** name of <code>isService</code> property */
    public static final String PROP_NAME_SERVICE = "service"; //$NON-NLS-1$

    /** do batch writes in large chunks */
    private static final String ORACLE_BATCH_WRITING_SIZE = "1000"; //$NON-NLS-1$


    /** should be connect using a service url or a SID one */
    private boolean m_isService = false;

    /**
     * Constructor
     */
    public OracleConnectionInfo() {
        super(1521);
    }
    
    @Override
    public String getConnectionUrl() {
        StringBuilder sb = new StringBuilder(JDBC_PRE);
        if (m_isService) {
            sb.append(StringConstants.SLASH).append(StringConstants.SLASH);
        }
        sb.append(StringUtils.defaultString(getHostname()))
                .append(StringConstants.COLON).append(getPort())
                .append(m_isService ? StringConstants.SLASH
                        : StringConstants.COLON)
                .append(StringUtils.defaultString(getDatabaseName()));
        return sb.toString();
    }

    @Override
    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }
    
    @Override
    public String getBatchWriting() {
        return BatchWriting.OracleJDBC;
    }

    @Override
    public String getBatchWritingSize() {
        return ORACLE_BATCH_WRITING_SIZE;
    }
    
    @Override
    public String getStatisticsCommand() {
        return
            "begin\n" + //$NON-NLS-1$
            "for tab in (\n" + //$NON-NLS-1$
                "select * from USER_TABLES tab order by TAB.TABLE_NAME\n" + //$NON-NLS-1$
            ") loop\n" + //$NON-NLS-1$
                "execute immediate 'analyze table ' || TAB.TABLE_NAME || ' compute statistics';\n" + //$NON-NLS-1$
            "end loop;\n" + //$NON-NLS-1$
            "end;\n"; //$NON-NLS-1$
    }

    /**
     * @return the url should connect to a service
     */
    public boolean isService() {
        return m_isService;
    }

    /**
     * 
     * @param isService should it use a service url
     */
    public void setService(boolean isService) {
        m_isService = isService;
        fireConnectionUrlChanged();
    }

     
}
