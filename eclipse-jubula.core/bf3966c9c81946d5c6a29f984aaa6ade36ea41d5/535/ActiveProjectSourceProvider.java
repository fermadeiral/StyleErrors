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

import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.utils.DatabaseStateDispatcher;
import org.eclipse.jubula.client.core.utils.DatabaseStateEvent;
import org.eclipse.jubula.client.core.utils.IDatabaseStateListener;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the active Project.
 *
 * @author BREDEX GmbH
 * @created Apr 30, 2009
 */
public class ActiveProjectSourceProvider extends AbstractJBSourceProvider
        implements IProjectLoadedListener, IDataChangedListener,
        IDatabaseStateListener {
    /** 
     * ID of variable that indicates whether a Project is currently active/open.
     */
    public static final String IS_PROJECT_ACTIVE =
        "org.eclipse.jubula.client.ui.rcp.variable.isProjectActive"; //$NON-NLS-1$
    
    /** 
     * ID of variable that indicates whether a Project is currently protected.
     */
    public static final String IS_PROJECT_PROTECTED =
        "org.eclipse.jubula.client.ui.rcp.variable.isProjectProtected"; //$NON-NLS-1$
    
    /** 
     * ID of variable that indicates whether a database connection is currently established.
     */
    public static final String IS_DB_CONNECTION_ESTABLISHED =
        "org.eclipse.jubula.client.ui.rcp.variable.isConnectionToDatabaseEstablished"; //$NON-NLS-1$
    
    /** 
     * ID of variable that indicates whether multiple test case browser instances are open.
     */
    public static final String ARE_MULTIPLE_TCB_OPEN =
        "org.eclipse.jubula.client.ui.rcp.variable.areMultipleTCBOpen"; //$NON-NLS-1$
    
    /**
     * Constructor.
     */
    public ActiveProjectSourceProvider() {
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectLoadedListener(this, true);
        ded.addDataChangedListener(this, true);
        DatabaseStateDispatcher.addDatabaseStateListener(this);
        MultipleTCBTracker.getInstance().setProvider(this);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void dispose() {
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeProjectLoadedListener(this);
        ded.removeDataChangedListener(this);
        DatabaseStateDispatcher.removeDatabaseStateListener(this);
        MultipleTCBTracker.getInstance().setProvider(null);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = new HashMap<String, Object>();
        IProjectPO cuProject = GeneralStorage.getInstance().getProject();
        currentState.put(IS_PROJECT_ACTIVE, cuProject != null);
        boolean isProjectProtected = false;
        if (cuProject != null) {
            isProjectProtected = cuProject.getIsProtected();
        }
        currentState.put(IS_PROJECT_PROTECTED, isProjectProtected);
        currentState.put(IS_DB_CONNECTION_ESTABLISHED,
                Persistor.instance() != null ? true : false);
        currentState.put(ARE_MULTIPLE_TCB_OPEN, 
                MultipleTCBTracker.getInstance().getOpenTCBs().size() > 1);
        return currentState;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String[] {   IS_PROJECT_ACTIVE, 
                                IS_PROJECT_PROTECTED,
                                IS_DB_CONNECTION_ESTABLISHED,
                                ARE_MULTIPLE_TCB_OPEN };
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        fireSourceChanged();
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        fireSourceChanged();
    }
    
    /**
     * Fires a source changed event for <code>IS_PROJECT_ACTIVE</code>,
     * <code>IS_PROJECT_PROTECTED</code> and
     * <code>IS_DB_CONNECTION_ESTABLISHED</code>
     */
    private void fireSourceChanged() {
        IProjectPO cuProject = GeneralStorage.getInstance().getProject();
        boolean isProjectProtected = false;
        if (cuProject != null) {
            isProjectProtected = cuProject.getIsProtected();
        }
        gdFireSourceChanged(ISources.WORKBENCH, IS_PROJECT_ACTIVE,
                cuProject != null);
        gdFireSourceChanged(ISources.WORKBENCH, IS_PROJECT_PROTECTED,
                isProjectProtected);
        gdFireSourceChanged(ISources.WORKBENCH, IS_DB_CONNECTION_ESTABLISHED,
                Persistor.instance() != null ? true : false);
        gdFireSourceChanged(ISources.WORKBENCH, ARE_MULTIPLE_TCB_OPEN,
                MultipleTCBTracker.getInstance().getOpenTCBs().size() > 1);
    }

    /**
     * {@inheritDoc}
     */
    public void reactOnDatabaseEvent(DatabaseStateEvent e) {
        fireSourceChanged();
    }
}
