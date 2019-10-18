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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.IAutRegistrationListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.sourceprovider.AbstractJBSourceProvider;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to currently available, inspectable AUTs.
 *
 * @author BREDEX GmbH
 * @created Mar 23, 2010
 */
public class InspectableAutSourceProvider extends AbstractJBSourceProvider 
        implements IAutRegistrationListener, IProjectLoadedListener, 
                   IProjectStateListener {

    /** 
     * ID of variable that indicates which AUTs are currently running
     */
    public static final String INSPECTABLE_AUTS = 
        "org.eclipse.jubula.client.inspector.ui.variable.inspectableAuts"; //$NON-NLS-1$
    
    /**
     * Constructor
     */
    public InspectableAutSourceProvider() {
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addProjectLoadedListener(this, true);
        ded.addProjectStateListener(this);
        AutAgentRegistration.getInstance().addListener(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        final DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeProjectLoadedListener(this);
        ded.removeProjectStateListener(this);
        AutAgentRegistration.getInstance().removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = new HashMap<String, Object>();
        List<AutIdentifier> inspectableAuts = new ArrayList<AutIdentifier>();
        currentState.put(INSPECTABLE_AUTS, inspectableAuts);
        Map<IAUTMainPO, Collection<AutIdentifier>> autToId =
            AutAgentRegistration.getRunningAuts(
                    GeneralStorage.getInstance().getProject(), 
                    AutAgentRegistration.getInstance().getRegisteredAuts());
        for (IAUTMainPO aut : autToId.keySet()) {
            if (isInspectable(aut)) {
                for (AutIdentifier autId : autToId.get(aut)) {
                    inspectableAuts.add(autId);
                }
            }
        }
        
        return currentState;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String [] {INSPECTABLE_AUTS};
    }

    /**
     * 
     * @param aut The AUT to check.
     * @return <code>true</code> if the AUT supports inspection. 
     *         Otherwise, <code>false</code>.
     */
    private boolean isInspectable(IAUTMainPO aut) {
        return CommandConstants.RCP_TOOLKIT.equals(aut.getToolkit());
    }

    /**
     * {@inheritDoc}
     */
    public void handleAutRegistration(AutRegistrationEvent event) {
        gdFireSourceChanged(ISources.WORKBENCH, getCurrentState());
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        gdFireSourceChanged(ISources.WORKBENCH, getCurrentState());
    }

    /** {@inheritDoc} */
    public void handleProjectStateChanged(ProjectState state) {
        if (ProjectState.prop_modified.equals(state)) {
            gdFireSourceChanged(ISources.WORKBENCH, getCurrentState());
        }
    }
}
