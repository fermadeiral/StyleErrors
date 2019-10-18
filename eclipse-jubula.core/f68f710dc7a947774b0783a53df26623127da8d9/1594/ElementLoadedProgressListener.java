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

import javax.persistence.PostLoad;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;


/**
 * Reports loaded nodes to a progress monitor.
 *
 * @author BREDEX GmbH
 * @created 24.01.2008
 */
public class ElementLoadedProgressListener {
    /** {@inheritDoc} */
    @PostLoad
    public void elementLoaded(Object entity) throws OperationCanceledException {
        IProgressMonitor monitor = 
            ProgressMonitorTracker.SINGLETON.getProgressMonitor();
        if (monitor != null) {
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            monitor.worked(1);
        }
    }

}
