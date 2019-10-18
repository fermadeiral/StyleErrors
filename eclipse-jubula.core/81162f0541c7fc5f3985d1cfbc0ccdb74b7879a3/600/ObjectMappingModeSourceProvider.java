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
 * @created Nov 8, 2006
 */
public class ObjectMappingModeSourceProvider extends AbstractJBSourceProvider
    implements IOMStateListener, IAutStateListener, IRecordModeStateListener {
    /**
     * the id of this source provider
     */
    public static final String ID = "org.eclipse.jubula.client.ui.rcp.sourceprovider.ObjectMappingModeSourceProvider"; //$NON-NLS-1$

    /**
     * ID of variable that indicates whether the observation mode is currently
     * running
     */
    public static final String IS_OBJECT_MAPPING_RUNNING = "org.eclipse.jubula.client.ui.rcp.variable.isObjectMappingRunning"; //$NON-NLS-1$

    /** is the Object Mapping Mode running */
    private boolean m_isOMRunning;

    /**
     * Private constructor
     */
    public ObjectMappingModeSourceProvider() {
        m_isOMRunning = false;

        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.addOMStateListener(this, true);
        dispatch.addAutStateListener(this, true);
        dispatch.addRecordModeStateListener(this, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleOMStateChanged(OMState state) {
        m_isOMRunning = (state == OMState.running);
        fireModeChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleAutStateChanged(AutState state) {
        switch (state) {
            case running:
                break;
            case notRunning:
                m_isOMRunning = false;
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
        switch (state) {
            case running:
                // Starting of Observation mode implicitly stops the
                // Object Mapping mode
                m_isOMRunning = false;
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
        gdFireSourceChanged(ISources.WORKBENCH, IS_OBJECT_MAPPING_RUNNING,
                isRunning());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRunning() {
        return m_isOMRunning;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.removeOMStateListener(this);
        dispatch.removeAutStateListener(this);
        dispatch.removeRecordModeStateListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(IS_OBJECT_MAPPING_RUNNING, isRunning());
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String[] { IS_OBJECT_MAPPING_RUNNING };
    }
}
