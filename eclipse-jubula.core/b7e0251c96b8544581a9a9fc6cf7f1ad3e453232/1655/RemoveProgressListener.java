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
package org.eclipse.jubula.client.core.businessprocess.progress;

import javax.persistence.PostRemove;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jubula.client.core.model.INodePO;


/**
 * Listens to JPA events and reports these events, if appropriate,
 * to a progress monitor. This listener focuses on events concerning deletion
 * of entities from the database.
 *
 * @author BREDEX GmbH
 * @created 01.02.2008
 */
public class RemoveProgressListener {

    /**
     * {@inheritDoc}
     */
    @PostRemove
    public void onPostRemove(Object entity) {
        IProgressMonitor monitor = 
            ProgressMonitorTracker.SINGLETON.getProgressMonitor();
        if (monitor != null && entity instanceof INodePO) {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            // Do unit of work
            monitor.worked(1);
        } 
    }

}
