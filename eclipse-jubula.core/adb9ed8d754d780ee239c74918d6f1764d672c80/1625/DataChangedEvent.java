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
package org.eclipse.jubula.client.core.events;

import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * @author BREDEX GmbH
 * @created Aug 12, 2011
 */
public class DataChangedEvent {
    /** <code>m_po</code> */
    private final IPersistentObject m_po;

    /** <code>m_dataState</code> */
    private final DataState m_dataState;

    /** <code>m_updateState</code> */
    private final UpdateState m_updateState;

    /**
     * @param po
     *            changed persistent object
     * @param dataState
     *            kind of modification
     * @param updateState
     *            determines the parts to update
     */
    public DataChangedEvent(IPersistentObject po, DataState dataState,
            UpdateState updateState) {
        m_po = po;
        m_dataState = dataState;
        m_updateState = updateState;
    }

    /**
     * @return the po
     */
    public IPersistentObject getPo() {
        return m_po;
    }

    /**
     * @return the dataState
     */
    public DataState getDataState() {
        return m_dataState;
    }

    /**
     * @return the updateState
     */
    public UpdateState getUpdateState() {
        return m_updateState;
    }
}
