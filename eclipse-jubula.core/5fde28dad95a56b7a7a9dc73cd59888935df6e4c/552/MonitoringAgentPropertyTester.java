/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;

/**
 * PropertyTester for the TestResultSummary to determine the used 
 * MonitoringAgent.
 */
public class MonitoringAgentPropertyTester extends PropertyTester {
    
    /**
     * returns true if the given MonitoringAgent (expectedVaule) 
     * was used in the TestResultSummary.
     * {@inheritDoc} 
     */
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        
        ITestResultSummaryPO summary = (ITestResultSummaryPO)receiver;
        return summary.getInternalMonitoringId().equals(expectedValue);
    }
}
