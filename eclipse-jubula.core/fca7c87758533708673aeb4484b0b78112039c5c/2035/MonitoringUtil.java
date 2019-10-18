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
package org.eclipse.jubula.autagent.monitoring;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.agent.AutAgent;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * This class contains monitoring utility methods
 *  
 * @author BREDEX GmbH
 * @created 20.08.2010
 */
public abstract class MonitoringUtil {
    /**
     * to prevent instantiation
     */
    private MonitoringUtil () {
        //do nothing
    }
    
    /**
     * Checks whether an AUT with this AUT_ID is running or not     
     * @param autId The autID to check
     * @return true if an AUT with the given AUT_ID is already running or false if not.
     */
    public static boolean checkForDuplicateAutID(String autId) {
        AutAgent agent = AutStarter.getInstance().getAgent();
        AutIdentifier autID = new AutIdentifier(autId);  
        Set<AutIdentifier> set = agent.getAuts();        
        Iterator<AutIdentifier>  it = set.iterator();        
        
        while (it.hasNext()) {
            AutIdentifier val = it.next();
            if (val.getExecutableName().equals(autID.getExecutableName())) {
                return true;
            }
            
        }       
        return false;
    }
    
    /**
     * Checks whether duplicate AUTs will be killed or not.
     * @return true if AUT-Agent runs in strict mode false if not.
     */
    public static boolean isKillDuplicateAuts() {
        AutAgent agent = AutStarter.getInstance().getAgent();
        return agent.isKillDuplicateAuts();
    }
    
}
