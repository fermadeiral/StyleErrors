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

/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 */
public interface IRefTestSuitePO extends INodePO {
    /**
     * access to Persistence (JPA / EclipseLink) property
     * 
     * @return the property
     * */
    public String getTestSuiteGuid();

    /**
     * access to Persistence (JPA / EclipseLink) property
     * 
     * @param testSuiteGuid
     *            GUID if the referenced TS
     */
    public void setTestSuiteGuid(String testSuiteGuid);

    /**
     * access to Persistence (JPA / EclipseLink) property
     * 
     * @return the property
     */
    public String getTestSuiteAutID();

    /**
     * access to Persistence (JPA / EclipseLink) property
     * 
     * @param testSuiteAutID
     *            ID of the used AUT
     */
    public void setTestSuiteAutID(String testSuiteAutID);
    
    /**
     * @return the referenced test suite
     */
    public ITestSuitePO getTestSuite();
    
    /**
     * Gets the real name of this reference test suite, may be null
     * @return null or the referenced name
     */
    public String getRealName();
}
