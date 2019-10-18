/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.stats;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.StopWatch;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.progress.ProgressConsoleRegistry;
import org.eclipse.jubula.client.stats.i18n.Messages;
import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 */
public class JobExecutionTracker implements IJobChangeListener {
    /** track the job executions */
    private Map<Job, StopWatch> m_tracker = new HashMap<Job, StopWatch>();
    
    /** Constructor */
    public JobExecutionTracker() {
    }

    /** {@inheritDoc} */
    public void aboutToRun(IJobChangeEvent event) {
        StopWatch sw = new StopWatch();
        m_tracker.put(event.getJob(), sw);
        sw.start();
    }

    /** {@inheritDoc} */
    public void done(IJobChangeEvent event) {
        Job job = event.getJob();
        StopWatch stopWatch = m_tracker.get(job);
        if (stopWatch != null) {
            stopWatch.stop();
            if (!job.isSystem()) {
                log(NLS.bind(Messages.ConsoleOutput, new Object[] {
                    job.getName(),
                    job.getResult(),
                    stopWatch.getTime()}));
            }
            m_tracker.remove(job);
        }
    }

    /**
     * @param string
     *            the string to log
     */
    private void log(String string) {
        ProgressConsoleRegistry.INSTANCE.getConsole().writeStatus(new Status(
                IStatus.INFO, Mn.PLUGIN_ID, string));
    }

    /** {@inheritDoc} */
    public void awake(IJobChangeEvent event) { /* not required */ }

    /** {@inheritDoc} */
    public void running(IJobChangeEvent event) { /* not required */ }

    /** {@inheritDoc} */
    public void scheduled(IJobChangeEvent event) { /* not required */ }

    /** {@inheritDoc} */
    public void sleeping(IJobChangeEvent event) { /* not required */ }
}