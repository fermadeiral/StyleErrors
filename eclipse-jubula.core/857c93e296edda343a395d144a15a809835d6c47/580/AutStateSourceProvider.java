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

import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.IAutRegistrationListener;
import org.eclipse.ui.ISources;


/**
 * Provides variables related to the running status of AUTs.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class AutStateSourceProvider extends AbstractJBSourceProvider 
        implements IAutRegistrationListener {

    /** 
     * ID of variable that indicates which AUTs are currently running
     */
    public static final String RUNNING_AUTS = 
        "org.eclipse.jubula.client.ui.rcp.variable.runningAuts"; //$NON-NLS-1$
    
    /**
     * Constructor.
     */
    public AutStateSourceProvider() {
        AutAgentRegistration.getInstance().addListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        AutAgentRegistration.getInstance().removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = 
            new HashMap<String, Object>();

        currentState.put(RUNNING_AUTS, 
                AutAgentRegistration.getInstance().getRegisteredAuts());
        return currentState;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String [] {RUNNING_AUTS};
    }

    /**
     * {@inheritDoc}
     */
    public void handleAutRegistration(AutRegistrationEvent event) {
        gdFireSourceChanged(ISources.WORKBENCH, RUNNING_AUTS, 
                AutAgentRegistration.getInstance().getRegisteredAuts());
    }
}
