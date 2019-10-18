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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.JobUtils;


/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public class StartTestJobHandler extends AbstractStartTestHandler {

    /** ID of command parameter for Test Job to start */
    public static final String TEST_JOB_TO_START = 
        "org.eclipse.jubula.client.ui.rcp.commands.StartTestJobCommand.parameter.testJobToStart"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(final ExecutionEvent event) 
        throws ExecutionException {
        if (!canStartTestExecution()) {
            return null;
        }
        Object testJobToStartObj = null;
        ITestJobPO testJobToStart = null;
        testJobToStartObj = 
            event.getObjectParameterForExecution(TEST_JOB_TO_START);

        if (testJobToStartObj instanceof ITestJobPO) {
            testJobToStart = (ITestJobPO)testJobToStartObj;
            final ITestJobPO finalTestJob = testJobToStart;
            final boolean autoScreenshots = Plugin.getDefault()
                    .getPreferenceStore().getBoolean(
                            Constants.AUTO_SCREENSHOT_KEY);
            final int iterMax = Plugin.getDefault()
                    .getPreferenceStore().getInt(
                            Constants.MAX_ITERATION_KEY);
            final String jobName = Messages.UIJobStartTestJob;
            Job startTestJobJob = new Job(jobName) {
                protected IStatus run(IProgressMonitor monitor) {
                    monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                    final AtomicBoolean prepareOk = new AtomicBoolean(false);
                    Plugin.getDisplay().syncExec(new Runnable() {
                        public void run() {
                            if (prepareTestExecution() 
                                && initTestExecution(event)) {
                                prepareOk.set(true);
                            }
                        }
                    });
                    if (prepareOk.get()) {
                        ClientTest.instance().startTestJob(
                                finalTestJob, autoScreenshots,
                                iterMax, null, null);
                    }
                    monitor.done();
                    return Status.OK_STATUS;
                }
            };
            startTestJobJob.setSystem(true);
            JobUtils.executeJob(startTestJobJob, null);
        }
        return null;
    }

}
