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
package org.eclipse.jubula.client.ui.rcp.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IAutStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IRecordModeStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.ui.ISources;

/**
 * @author BREDEX GmbH
 * @created Nov 9, 2006
 */
public class ObservationModeSourceProvider extends AbstractJBSourceProvider
    implements IAutStateListener, IRecordModeStateListener, IOMStateListener {
    /**
     * ID of variable that indicates whether the observation mode is currently
     * running
     */
    public static final String IS_OBSERVATION_RUNNING = "org.eclipse.jubula.client.ui.rcp.variable.isObservationRunning"; //$NON-NLS-1$

    /** is Record Mode running */
    private boolean m_isRecordModeRunning;

    /**
     * Private constructor
     */
    public ObservationModeSourceProvider() {
        m_isRecordModeRunning = false;

        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.addRecordModeStateListener(this, true);
        dispatch.addAutStateListener(this, true);
        dispatch.addOMStateListener(this, true);
    }

    /**
     * @param state
     *            state from AUT
     */
    public void handleAutStateChanged(AutState state) {
        switch (state) {
            case running:
                break;
            case notRunning:
                m_isRecordModeRunning = false;
                break;
            default:
                Assert.notReached(Messages.UnhandledAutState);
        }
        fireModeChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleRecordModeStateChanged(RecordModeState state) {
        m_isRecordModeRunning = (state == RecordModeState.running);
        fireModeChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleOMStateChanged(OMState state) {
        switch (state) {
            case running:
                // Starting of Object Mapping mode implicitly stops the
                // Observation mode
                m_isRecordModeRunning = false;
                break;
            case notRunning:
                break;
            default:
                Assert.notReached(Messages.UnsupportedRecordModeState);
        }
        fireModeChanged();
    }

    /**
     * Fires a source changed event for <code>IS_OBJECT_MAPPING_RUNNING</code>.
     */
    private void fireModeChanged() {
        gdFireSourceChanged(ISources.WORKBENCH, IS_OBSERVATION_RUNNING,
                isRunning());
    }

    /**
     * @return whether it's running or not
     */
    public boolean isRunning() {
        return m_isRecordModeRunning;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.removeRecordModeStateListener(this);
        dispatch.removeAutStateListener(this);
        dispatch.removeOMStateListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(IS_OBSERVATION_RUNNING, isRunning());
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String[] { IS_OBSERVATION_RUNNING };
    }
}
