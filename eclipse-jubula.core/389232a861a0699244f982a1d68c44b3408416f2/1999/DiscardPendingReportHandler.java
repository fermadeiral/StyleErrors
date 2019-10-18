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
import org.eclipse.jubula.client.core.businessprocess.TestresultSummaryBP;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO.AlmReportStatus;

/**
 * @author BREDEX GmbH
 */
public class DiscardPendingReportHandler extends AbstractALMReportHandler {
    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event) {
        final List<ITestResultSummaryPO> summaries = getPendingSummaries();
        final int sumCount = summaries.size();
        if (sumCount > 0) {
            for (ITestResultSummaryPO summary : summaries) {
                TestresultSummaryBP.getInstance().setALMReportStatus(summary,
                    AlmReportStatus.REPORT_DISCARDED);
            }
        }
        return null;
    }
}
