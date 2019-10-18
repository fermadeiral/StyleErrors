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
package org.eclipse.jubula.client.ui.utils;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * Utility class for jobs
 * 
 * @author BREDEX GmbH
 * @created Dec 17, 2009
 */
public class JobUtils {
    /** hide constructor */
    private JobUtils() {
    // hide
    }

    /**
     * Use this method to delegate job execution
     * 
     * @param job
     *            the job to execute
     * @param part
     *            the part to use for progress support; maybe null
     */
    public static void executeJob(Job job, IWorkbenchPart part) {
        executeJob(job, part, 0);
    }

    /**
     * Use this method to delegate job execution
     * 
     * @param job
     *            the job to execute
     * @param part
     *            the part to use for progress support; maybe null
     * @param delay
     *            the delay to use for scheduling
     */
    public static void executeJob(Job job, IWorkbenchPart part, long delay) {
        if (part != null) {
            IWorkbenchSiteProgressService ps = part.getSite().getAdapter(
                    IWorkbenchSiteProgressService.class);
            if (ps != null) {
                ps.schedule(job);
            } else {
                job.schedule(delay);
            }
        } else {
            job.schedule(delay);
        }
    }
}
