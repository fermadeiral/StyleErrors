/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jubula.client.core.model.IALMReportingRulePO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.TestResultNode;

/**
 * @author BREDEX GmbH
 *
 */
public class FieldUpdate extends ALMChange {
    
    /** map containing all attribute changes which have to be reported */
    private Map<String, String> m_attributesToChange =
            new HashMap<String, String>();

    /**
     * Constructor
     * @param resultNode the node
     * @param dashboardURL the dashboardURL
     * @param summary the summary
     * @param nodeNumber the node number
     * @param rules the list of reporting rules
     */
    public FieldUpdate(TestResultNode resultNode, String dashboardURL,
            ITestResultSummaryPO summary, Long nodeNumber,
            List<IALMReportingRulePO> rules) {
        super(resultNode, dashboardURL, summary, nodeNumber);
        
        for (IALMReportingRulePO rule : rules) {
            m_attributesToChange.put(rule.getAttributeID(), rule.getValue());
        }
    }
    
    /**
     * @return the map containing all attribute changes
     */
    public Map<String, String> getAttributesToChange() {
        return m_attributesToChange;
    }
}
