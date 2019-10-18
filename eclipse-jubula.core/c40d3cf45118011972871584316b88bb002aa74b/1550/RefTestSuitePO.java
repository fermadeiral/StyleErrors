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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * class only for specification data of testcase specificaton data are infos to
 * CapPO tripel like name of CapPO, name of component, action name and the fixed
 * value for each parameter of CapPO as far as set (static data) this static
 * part of testcase is only once existent and will be used as reference for one
 * or more ExecTestCases
 * 
 * @author BREDEX GmbH
 * @created 07.10.2004
 */
@Entity
@DiscriminatorValue(value = "R")
class RefTestSuitePO extends NodePO implements IRefTestSuitePO {
    /** Persistence (JPA / EclipseLink) property */
    private String m_testSuiteGuid;
    /** Persistence (JPA / EclipseLink) property */
    private String m_testSuiteAutID;

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    RefTestSuitePO() {
        super();
    }

    /**
     * constructor when GUID is already defined
     * 
     * @param testCaseName
     *            name of testCase
     * @param guid
     *            GUID of the testCase
     *            
     * @param tsGuid referenced TS
     * @param tsAutId AUT Id to be used for this entry
     */
    RefTestSuitePO(String testCaseName, String guid, String tsGuid,
            String tsAutId) {
        super(testCaseName, guid, false);
        m_testSuiteGuid = tsGuid;
        m_testSuiteAutID = tsAutId;
    }
    
    /**
     * constructor when GUID is already defined
     * 
     * @param testCaseName
     *            name of testCase
     * @param tsGuid referenced TS
     * @param tsAutId AUT Id to be used for this entry
     */

    RefTestSuitePO(String testCaseName, String tsGuid,
            String tsAutId) {
        super(testCaseName, false);
        m_testSuiteGuid = tsGuid;
        m_testSuiteAutID = tsAutId;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof IRefTestSuitePO) {
            IRefTestSuitePO other = (IRefTestSuitePO)obj;
            return getGuid().equals(other.getGuid());
        }
        
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return getGuid().hashCode();
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.NodePO#isInterfaceLocked()
     */
    @Transient
    public Boolean isReused() {
        return true;
    }

    /**
     * 
     * @return the testSuiteGuid
     */
    @Basic
    @Column(name = "TS_GUID")
    public String getTestSuiteGuid() {
        return m_testSuiteGuid;
    }

    /**
     * @param testSuiteGuid the testSuiteGuid to set
     */
    public void setTestSuiteGuid(String testSuiteGuid) {
        m_testSuiteGuid = testSuiteGuid;
    }

    /**
     * 
     * @return the testSuiteAutID
     */
    @Basic
    @Column(name = "AUT_ID", length = MAX_STRING_LENGTH)
    public String getTestSuiteAutID() {
        return m_testSuiteAutID;
    }

    /**
     * @param testSuiteAutID the testSuiteAutID to set
     */
    public void setTestSuiteAutID(String testSuiteAutID) {
        m_testSuiteAutID = testSuiteAutID;
    }

    /** {@inheritDoc} */
    @Transient
    public ITestSuitePO getTestSuite() {
        return NodePM.getTestSuite(getTestSuiteGuid());
    }
    
    /**{@inheritDoc} */
    @Transient
    public String getName() {
        String name = super.getName();
        if (name == null || name.equals(StringConstants.EMPTY)) {
            if (getTestSuite() != null) {
                return getTestSuite().getName();
            }
            return Messages.RefTestSuitePOMissingReference;
        }
        return name;
    }
    
    /** {@inheritDoc} */
    @Transient
    public String getRealName() {
        return super.getName();
    }
}
