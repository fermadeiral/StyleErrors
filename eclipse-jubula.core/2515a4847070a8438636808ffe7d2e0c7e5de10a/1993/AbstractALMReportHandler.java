/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.handler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO.AlmReportStatus;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;

/**
 * @author BREDEX GmbH
 */
public abstract class AbstractALMReportHandler extends
    AbstractSelectionBasedHandler {
    /**
     * @return a list of pending summaries
     */
    protected List<ITestResultSummaryPO> getPendingSummaries() {
        final List<ITestResultSummaryPO> pendingSummaries = 
            new LinkedList<ITestResultSummaryPO>();
        Iterator iterator = getSelection().iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (o instanceof ITestResultSummaryPO) {
                ITestResultSummaryPO summary = (ITestResultSummaryPO) o;
                if (summary.hasTestResultDetails()
                        && AlmReportStatus.NOT_YET_REPORTED.equals(summary
                                .getAlmReportStatus())) {
                    pendingSummaries.add(summary);
                }
            }
        }
        return pendingSummaries;
    }
}