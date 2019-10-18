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
package org.eclipse.jubula.client.core.model;

/**
 * @author BREDEX GmbH
 */
public interface ITestResultAdditionPO {
    
    /**
     * the type for the addition, the name of the enum is used in the database
     * @author BREDEX GmbH
     */
    public static enum TYPE {
        /** output and error log */
        OUT_AND_ERR,
        /** is this a JUNIT testsuite*/
        JUNIT_TEST_SUITE;
    }
    
    
    /**
     * only for Persistence (JPA / EclipseLink)
     * @return Returns the id.
     */
    public Long getId();
        
    /**
     * @return the saved object
     */
    public Object getData();
    
    /**
     * @return the type for the saved object
     */
    public TYPE getType();
    
    /**
     * @return the m_testResultSummaryId
     */
    public Long getInternalTestResultSummaryID();
    
    /**
     * @param testResultSummaryId the testResultSummaryId to set
     */
    public void setInternalTestResultSummaryID(Long testResultSummaryId);
}
