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

import java.util.List;

import org.eclipse.jubula.client.core.model.DBVersionPO;


/**
 * Exception indicating that the version of the database to which the Client 
 * is trying to connect could not be determined (missing or ambiguous).
 *
 * @author BREDEX GmbH
 * @created May 25, 2010
 */
public class AmbiguousDatabaseVersionException extends Exception {

    /** the versions reported by the database */
    private DBVersionPO[] m_versionEntries;
    
    /**
     * @param versionEntries The versions reported by the database.
     */
    public AmbiguousDatabaseVersionException(List<DBVersionPO> versionEntries) {
        m_versionEntries = 
            versionEntries.toArray(new DBVersionPO[versionEntries.size()]);
    }

    /**
     * 
     * @return The versions reported by the database.
     */
    public DBVersionPO[] getVersionEntries() {
        return m_versionEntries;
    }
}
