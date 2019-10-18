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
package org.eclipse.jubula.client.core.model;

import java.util.List;

/**
 * @author BREDEX GmbH
 */
public interface IALMReportingProperties {
    /**
     * @return the URL of the central dashboard instance
     */
    public abstract String getDashboardURL();

    /**
     * @param dashboardURL
     *            the URL of the central dashboard instance
     */
    public abstract void setDashboardURL(String dashboardURL);

    /**
     * @return the name of the central ALM repository
     */
    public abstract String getALMRepositoryName();

    /**
     * @param almRepositoryName
     *             the name of the central ALM repository
     */
    public abstract void setALMRepositoryName(String almRepositoryName);

    /**
     * @return <code>true</code> if this project should report succeeded test
     *         executions to the connected ALM repository
     */
    public abstract boolean getIsReportOnSuccess();

    /**
     * @param isReportOnSuccess
     *            <code>true</code> if this project should report succeeded test
     *            executions to the connected ALM repository
     */
    public abstract void setIsReportOnSuccess(boolean isReportOnSuccess);

    /**
     * @return <code>true</code> if this project should report failed test
     *         executions to the connected ALM repository
     */
    public abstract boolean getIsReportOnFailure();

    /**
     * @param isReportOnFailure
     *            <code>true</code> if this project should report failed test
     *            executions to the connected ALM repository
     */
    public abstract void setIsReportOnFailure(boolean isReportOnFailure);

    /**
     * @param reportingRules The reporting rules to set.
     */
    public void setALMReportingRules(
            List<IALMReportingRulePO> reportingRules);

    /**
     * @return Returns the reporting rules set.
     */
    public List<IALMReportingRulePO> getALMReportingRules();

}