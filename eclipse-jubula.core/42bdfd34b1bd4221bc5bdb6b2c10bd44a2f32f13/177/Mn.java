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

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

/**
 * @author BREDEX GmbH
 */
public class Mn extends Plugin {
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.eclipse.jubula.client.stats"; //$NON-NLS-1$

    /** The shared instance */
    private static Mn plugin;

    /** track job executions */
    private IJobChangeListener m_jobLister;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        m_jobLister = new JobExecutionTracker();
        Job.getJobManager().addJobChangeListener(m_jobLister);
        plugin = this;

    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        Job.getJobManager().removeJobChangeListener(m_jobLister);
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Mn getDefault() {
        return plugin;
    }
}