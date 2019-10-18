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
package org.eclipse.jubula.client.core.model;

import org.apache.commons.lang.Validate;

/**
 * @author BREDEX GmbH
 * @created Aug 13, 2010
 */
public abstract class AbstractTestResult implements ITestResult {

    /** the root of the test result tree */
    private TestResultNode m_rootResultNode;

    /**
     * Private constructor to prevent instantiation of a Test Result without
     * valid Project data. 
     * 
     * @param rootResultNode The root of the Test Result tree. Must not be 
     *                       <code>null</code>.
     */
    protected AbstractTestResult(TestResultNode rootResultNode) {
        Validate.notNull(rootResultNode);
        m_rootResultNode = rootResultNode;
    }

    /**
     * 
     * @return the root of the test result tree.
     */
    public TestResultNode getRootResultNode() {
        return m_rootResultNode;
    }

}
