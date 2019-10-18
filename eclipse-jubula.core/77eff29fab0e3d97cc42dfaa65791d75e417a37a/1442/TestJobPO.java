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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author BREDEX GmbH
 * @created Mar 16, 2010
 * 
 * class for TestJob in testexecution tree
 *
 */
@Entity
@DiscriminatorValue(value = "J")
class TestJobPO extends NodePO implements ITestJobPO {

    /** Flag that indicates if this TS is editable */
    private transient boolean m_isEditable = true;       

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    TestJobPO() {
        // only for Persistence (JPA / EclipseLink)
    }   
    
    /**
     * constructor
     * @param testJobName name of TestJob
     * @param isGenerated indicates whether this node has been generated
     */
    TestJobPO(String testJobName, boolean isGenerated) {
        super(testJobName, isGenerated);
        addTrackedChange(CREATED, false);
    }
        
    
    /**
     * constructor when GUID is already defined
     * @param testJobName name of testsuite
     * @param guid guid of the testsuite
     * @param isGenerated indicates whether this node has been generated
     */
    TestJobPO(String testJobName, String guid, boolean isGenerated) {
        super(testJobName, guid, isGenerated);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isEditable() {
        return m_isEditable;
    }

    /**
     * {@inheritDoc}
     */
    public void setEditable(boolean editable) {
        m_isEditable = editable;
    }
    
    /**
     * {@inheritDoc}
     * Note: this class has a natural ordering that is
     * inconsistent with equals.
     */
    public int compareTo(Object o) {
        ITestSuitePO ts = (ITestSuitePO)o;
        return this.getName().compareTo(ts.getName());
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.NodePO#isInterfaceLocked()
     */
    @Transient
    public Boolean isReused() {
        return true;
    }

}