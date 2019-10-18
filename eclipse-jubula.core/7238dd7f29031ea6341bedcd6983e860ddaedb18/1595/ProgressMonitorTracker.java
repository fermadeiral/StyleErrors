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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author BREDEX GmbH
 * @created Nov 26, 2010
 */
public enum ProgressMonitorTracker {
    /** Singleton */
    SINGLETON;
    
    /** The monitor to which the interceptor reports progress */
    private IProgressMonitor m_monitor;

    /** The thread which created the monitor */
    private Thread m_creator;

    /**
     * Private constructor
     */
    private ProgressMonitorTracker() {
    }
    
    /**
     * 
     * @param monitor The new progress monitor for database access. A value of
     *                <code>null</code> clears the monitor, meaning that the
     *                database access will no longer issue progress updates.
     */
    public synchronized void setProgressMonitor(IProgressMonitor monitor) {
        m_monitor = monitor;
        m_creator = Thread.currentThread();
    }

    /**
     * 
     * @return the progress monitor currently being used to monitor 
     *         database access or null if the caller thread is
     *         not the thread having created the monitor.
     */
    public synchronized IProgressMonitor getProgressMonitor() {
        if (m_creator == Thread.currentThread()) {
            return m_monitor;
        }
        return null;
    }
}
