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
package org.eclipse.jubula.client.ui.propertytester;

import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;

/**
 * PropertyTester for TestResultSummary.
 * 
 * @author BREDEX GmbH
 * @created Mar 5, 2009
 */
public class TestResultSummaryPropertyTester 
    extends AbstractBooleanPropertyTester {
    /** the id of the "hasMonitoringData" property */
    public static final String HAS_MONITORING_DATA_PROP = "hasMonitoringData"; //$NON-NLS-1$

    /** the id of the "hasTestResultDetails" property */
    public static final String HAS_TEST_RESULT_DETAILS_PROP = "hasTestResultDetails"; //$NON-NLS-1$
    
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { 
        HAS_MONITORING_DATA_PROP, 
        HAS_TEST_RESULT_DETAILS_PROP};

    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        ITestResultSummaryPO summary = (ITestResultSummaryPO) receiver;
        if (property.equals(HAS_MONITORING_DATA_PROP)) {
            return summary.isReportWritten();
        }
        if (property.equals(HAS_TEST_RESULT_DETAILS_PROP)) {
            return summary.hasTestResultDetails();
        }
        return false;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return ITestResultSummaryPO.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
