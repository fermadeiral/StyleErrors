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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;


/**
 * @author BREDEX GmbH
 * @created May 10, 2010
 */
public class RunningAutBP {

    /**
     * Private constructor to prevent instantiation of a utility class.
     */
    private RunningAutBP() {
        // Nothing to initialize
    }
    
    /**
     * 
     * @param autId an AUT ID.
     * @return <code>true</code> if an AUT Definition for <code>autId</code>
     *         exists within the current project. Otherwise, <code>false</code>.
     */
    public static boolean isAutDefined(AutIdentifier autId) {
        IProjectPO currentProject = 
            GeneralStorage.getInstance().getProject();
        return currentProject != null 
            && AutAgentRegistration.getAutForId(autId, currentProject) 
                != null;
    }
    
    /**
     * @return a list of all defined running AUTs
     */
    public static Collection<AutIdentifier> getListOfDefinedRunningAuts() {
        IProjectPO currentProject = 
            GeneralStorage.getInstance().getProject();
        Collection<AutIdentifier> definedRunningAuts = 
            new HashSet<AutIdentifier>();
        if (currentProject != null) {
            Collection<AutIdentifier> runningAutIds = 
                new HashSet<AutIdentifier>(
                        AutAgentRegistration.getInstance().getRegisteredAuts());
            for (AutIdentifier autId : runningAutIds) {
                if (AutAgentRegistration.getAutForId(autId, currentProject) 
                        != null) {
                    definedRunningAuts.add(autId);
                }
            }
        }
        return definedRunningAuts;
    }
}
