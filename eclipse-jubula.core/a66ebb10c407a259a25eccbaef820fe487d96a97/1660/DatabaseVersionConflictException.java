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

/**
 * Exception indicating that the database version expected by Jubula does not
 * match the version of the database to which the Client is trying to connect.
 *
 * @author BREDEX GmbH
 * @created May 21, 2010
 */
public class DatabaseVersionConflictException extends Exception {

    /** 
     * the major version number of the database for which the conflict 
     * occurred
     */
    private Integer m_majorVersion;

    /** 
     * the minor version number of the database for which the conflict 
     * occurred
     */
    private Integer m_minorVersion;

    /**
     * Constructor
     * 
     * @param majorVersion The major version number of the database for which 
     *                     the conflict occurred.
     * @param minorVersion The minor version number of the database for which 
     *                     the conflict occurred.
     */
    public DatabaseVersionConflictException(Integer majorVersion,
            Integer minorVersion) {

        m_majorVersion = majorVersion;
        m_minorVersion = minorVersion;
    }

    /**
     * 
     * @return the major version number of the database for which the conflict 
     *         occurred.
     */
    public Integer getDatabaseMajorVersion() {
        return m_majorVersion;
    }

    /**
     * 
     * @return the minor version number of the database for which the conflict 
     *         occurred
     */
    public Integer getDatabaseMinorVersion() {
        return m_minorVersion;
    }
}
