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
package org.eclipse.jubula.client.core.utils;

/**
 * @author BREDEX GmbH
 * @created 25.08.2005
 */
public class DatabaseStateEvent {
    /** The available database states */
    public enum DatabaseState {
        /** the database scheme has just been created */
        DB_SCHEME_CREATED,
        /** successful login */
        DB_LOGIN_SUCCEEDED,
        /** successful logout */
        DB_LOGOUT_SUCCEEDED
    }

    /** The ID of the event */
    private DatabaseState m_state;

    /**
     * The constructor.
     * 
     * @param state
     *            the database state event
     */
    public DatabaseStateEvent(DatabaseState state) {
        m_state = state;
    }

    /**
     * @return Returns the ID of the Event.
     */
    public DatabaseState getState() {
        return m_state;
    }
}