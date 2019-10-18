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
package org.eclipse.jubula.client.core.businessprocess.db;

import javax.persistence.EntityManager;

import org.eclipse.jubula.client.core.model.ITimestampPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;


/**
 * BP-Class for ITimestampPOs
 *
 * @author BREDEX GmbH
 * @created Oct 12, 2007
 */
public class TimestampBP {

    /**
     * hidden utility Constructor.
     */
    private TimestampBP() {
        // nothing
    }
    
    /**
     * Checks if the MasterSession is up to date with the given ITimestampPO.
     * @param timeStampPO an ITimestampPO
     * @return true if the MasterSession is up to date with the given 
     * ITimestampPO, false otherwise.
     */
    private static boolean isMasterSessionUpToDate(ITimestampPO timeStampPO) {
        final ITimestampPO masterObject = getFromMasterSession(timeStampPO);
        return masterObject.getTimestamp() >= timeStampPO.getTimestamp();
    }

    /**
     * Gets the given ITimestampPO from the MasterSession.
     * @param timeStampPO an ITimestampPO
     * @return the given ITimestampPO from the MasterSession.
     */
    private static ITimestampPO getFromMasterSession(ITimestampPO timeStampPO) {
        final EntityManager masterSession = GeneralStorage.getInstance()
            .getMasterSession();
        return masterSession.find(timeStampPO.getClass(), timeStampPO.getId());
    }
    
    /**
     * Refreshes, if needed, the given ITimestampPO of an Editor in the 
     * MasterSession.
     * @param timeStampPO an ITimestampPO
     * @return true if refreshed, false otherwise.
     */
    public static boolean refreshEditorNodeInMasterSession(ITimestampPO 
        timeStampPO) {
        
        final boolean isUpToDate = isMasterSessionUpToDate(timeStampPO);
        if (!isUpToDate) {
            final EntityManager masterSession = GeneralStorage.getInstance()
                .getMasterSession();
            ITimestampPO masterObject = getFromMasterSession(timeStampPO);
            masterSession.detach(masterObject);
            masterObject = masterSession.find(
                    masterObject.getClass(), masterObject.getId());
            masterSession.refresh(masterObject);
            GeneralStorage.getInstance().fireDataModified(masterObject);
        }
        return isUpToDate;
    }
    
    /**
     * Refreshes the timestamp of the given ITimestampPO.
     * @param timeStampPO an ITimestampPO.
     */
    public static void refreshTimestamp(ITimestampPO timeStampPO) {
        final long timestamp = System.currentTimeMillis();
        timeStampPO.setTimestamp(timestamp);
    }
}
