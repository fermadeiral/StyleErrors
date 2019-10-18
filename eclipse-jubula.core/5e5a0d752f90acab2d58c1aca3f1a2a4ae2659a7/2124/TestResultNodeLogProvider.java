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
package org.eclipse.jubula.client.ui.views.logview;

import org.eclipse.jubula.client.core.model.TestResultNode;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class TestResultNodeLogProvider implements LogProvider {
    /**
     * <code>m_testResultNode</code>
     */
    private TestResultNode m_testResultNode;
    
    /**
     * 
     * @param testresultnode the {@link TestResultNode}  to provide
     */
    public TestResultNodeLogProvider(TestResultNode testresultnode) {
        m_testResultNode = testresultnode;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getLogString() {
        return m_testResultNode.getCommandLog();
    }

}
