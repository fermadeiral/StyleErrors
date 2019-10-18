/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.handler;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.alm.mylyn.core.bp.CommentReporter;
import org.eclipse.jubula.client.alm.mylyn.ui.i18n.Messages;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.editors.TestResultViewer.GenerateTestResultTreeOperation;

/**
 * @author BREDEX GmbH
 */
public class ReportToAlmHandler extends AbstractALMReportHandler {
    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event) {
        final List<ITestResultSummaryPO> summaries = 
            getPendingSummaries();
        
        final int sumCount = summaries.size();
        if (sumCount > 0) {
            Job reportToALMOperation = new Job(Messages.BatchALMReporting) {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    monitor.beginTask(Messages.BatchALMReporting, sumCount);
                    for (ITestResultSummaryPO summary : summaries) {
                        try {
                            if (monitor.isCanceled()) {
                                return Status.CANCEL_STATUS;
                            }
                            reportResults(summary, monitor);
                        } catch (InterruptedException e) {
                            monitor.setCanceled(true);
                        }
                        monitor.worked(1);
                    }
                    monitor.done();
                    return Status.OK_STATUS;
                }
            };
            reportToALMOperation.schedule();
        }
        
        return null;
    }

    /**
     * @param summary
     *            the summary to report the results for
     * @param monitor
     *            the monitor to use
     * @throws InterruptedException
     */
    private void reportResults(ITestResultSummaryPO summary,
        IProgressMonitor monitor)
        throws InterruptedException {
        GenerateTestResultTreeOperation operation = 
            new GenerateTestResultTreeOperation(
                summary.getId(), GeneralStorage
                    .getInstance().getMasterSession());

        operation.run(new NullProgressMonitor());

        Job job = CommentReporter.getInstance()
            .gatherInformationAndCreateReportToALMJob(summary, summary,
                operation.getRootNode());

        if (job != null) {
            job.schedule();
            job.join();
        }
    }
}
