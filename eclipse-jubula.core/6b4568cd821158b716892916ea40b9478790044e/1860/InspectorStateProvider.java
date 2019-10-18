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
package org.eclipse.jubula.client.inspector.ui.provider.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IAutStateListener;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;


/**
 * @author BREDEX GmbH
 * @created Jul 7, 2009
 */
public class InspectorStateProvider extends AbstractSourceProvider 
        implements IAutStateListener {

    /** 
     * ID of variable that indicates whether the Inspector is currently active.
     */
    public static final String IS_INSPECTOR_ACTIVE = 
        "org.eclipse.jubula.client.inspector.ui.variable.isInspectorActive"; //$NON-NLS-1$
    
    /** 
     * <code>true</code> if the Inspector is currently active. 
     * Otherwise <code>false</code>. 
     */
    private boolean m_isInspectorActive = false;

    /**
     * Constructor
     */
    public InspectorStateProvider() {
        DataEventDispatcher.getInstance().addAutStateListener(this, true);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = 
            new HashMap<String, Object>();

        currentState.put(IS_INSPECTOR_ACTIVE, m_isInspectorActive);
        
        return currentState;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String [] {IS_INSPECTOR_ACTIVE};
    }

    /**
     * Sets the active status of the Inspector and updates listeners 
     * if the value has changed as a result.
     * 
     * @param isActive the new value for the active status of the 
     *                 Inspector.
     */
    public void setInspectorActive(final boolean isActive) {
        m_isInspectorActive = isActive;
        // The syncExec call is required in order to avoid InvalidThreadAccess 
        //  errors when updating certain controls based on the new status.
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
                fireSourceChanged(
                        ISources.WORKBENCH, IS_INSPECTOR_ACTIVE, isActive);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher.getInstance().removeAutStateListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void handleAutStateChanged(AutState state) {
        if (state == AutState.notRunning) {
            setInspectorActive(false);
        }
    }

}
