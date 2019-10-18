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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.collections.iterators.IteratorChain;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;


/**
 * class for testsuite in testexecution tree
 *
 * @author BREDEX GmbH
 * @created 12.10.2004
 */
@Entity
@DiscriminatorValue(value = "T")
class TestSuitePO extends NodePO implements ITestSuitePO {
        
    /** Flag that indicates if this TS is editanle or not */
    private transient boolean m_isEditable = true;
    
    /** the step delay */
    private int m_stepDelay;
    
    /** the relevance */
    private boolean m_relevant = true;
    
    /** the name of the AUT of this test suite */
    private IAUTMainPO m_aut;

    /** is the testSuite started */
    private transient boolean m_isStarted = false;
    
    /** map with the default eventHandlers for this test suite */
    private Map<String, Integer> m_defaultEventHandler = 
        new HashMap<String, Integer>(
                IEventHandlerContainer.MAX_NUMBER_OF_EVENT_HANDLER);
    
    /**
     * only for Persistence (JPA / EclipseLink)
     */
    TestSuitePO() {
        // only for Persistence (JPA / EclipseLink)
    }   
    
    /**
     * constructor
     * @param testSuiteName name of testsuite
     * @param isGenerated indicates whether this node has been generated
     */
    TestSuitePO(String testSuiteName, boolean isGenerated) {
        super(testSuiteName, isGenerated);
        addTrackedChange(CREATED, false);
        init();
    }
    
    /**
     * constructor when GUID is already defined
     * @param testSuiteName name of testsuite
     * @param guid guid of the testsuite
     * @param isGenerated indicates whether this node has been generated
     */
    TestSuitePO(String testSuiteName, String guid, boolean isGenerated) {
        super(testSuiteName, guid, isGenerated);
        init();
    }

    /**
     * Initialize this instance
     *
     */
    private void init() {
        Map<String, Integer> map = ComponentBuilder.getInstance()
                .getCompSystem().getEventTypes();
        Set mapKeySet = map.keySet();
        for (Object object : mapKeySet) {
            String key = (String)object;
            getDefaultEventHandler().put(key, map.get(key));
        }            
    }
    
    /**
     * 
     * @return Returns the stepDelay.
     */
    @Basic
    public int getStepDelay() {
        return m_stepDelay;
    }
    
    /**
     * @param stepDelay The stepDelay to set.
     */
    public void setStepDelay(int stepDelay) {
        m_stepDelay = stepDelay;
    }
    
    /**
     * 
     * @return Returns whether the test suite is relevant.
     */
    @Basic
    public boolean getRelevant() {
        return m_relevant;
    }
    
    /**
     * @param relevant whether the test suite is relevant.
     */
    public void setRelevant(boolean relevant) {
        m_relevant = relevant;
    }
    
    /**
     * 
     * @return Returns the AUT.
     */
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = AUTMainPO.class)
    @JoinColumn(name = "AUT")
    @BatchFetch(value = BatchFetchType.JOIN)
    public IAUTMainPO getAut() {
        return m_aut;
    }
    /**
     * @param aut The AUT to set.
     */
    public void setAut(IAUTMainPO aut) {
        m_aut = aut;
    }

   /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TestSuitePO || obj instanceof ITestSuitePO)) {
            return false;
        }
        
        return super.equals(obj);
    }
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return super.hashCode();
    }
    /**
     * @return Returns the isStarted.
     */
    @Transient
    public boolean isStarted() {
        return m_isStarted;
    }
    /**
     * @param isStarted The isStarted to set.
     */
    public void setStarted(boolean isStarted) {
        m_isStarted = isStarted;
    }

    /**
     * 
     * @return Returns the defaultEventHandler.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DEF_EVENTH")
    @MapKeyColumn(name = "EVENT_TYPE")
    @Column(name = "REENTRY")
    // No @BatchFetch(value = BatchFetchType.JOIN) here, as it was causing
    // an NPE when deleting (with dbtool) a Project that contains Test Suites.
    public Map<String, Integer> getDefaultEventHandler() {
        return m_defaultEventHandler;
    }
    
    /**
     * Removes the default handlers
     * @param sess the session
     */
    private void removeDefaultEventHandlers(EntityManager sess) {
        Query q = sess.createNativeQuery("delete from DEF_EVENTH where TESTSUITEPO_ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId()).executeUpdate();
    }

    /**
     * @param defaultEventHandler
     *            The defaultEventHandler to set.
     */
    public void setDefaultEventHandler(
            Map<String, Integer> defaultEventHandler) {
        // only for conversion of old projects
        if (defaultEventHandler != null) {
            m_defaultEventHandler = defaultEventHandler;
        }
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
    
    /** {@inheritDoc} */
    public void goingToBeDeleted(EntityManager sess) {
        super.goingToBeDeleted(sess);
        removeDefaultEventHandlers(sess);
    }
    
    /** {@inheritDoc} */
    @Transient
    public Iterator<INodePO> getAllNodeIter() {
        IteratorChain chain = new IteratorChain();
        chain.addIterator(getNodeListIterator());
        for (INodePO node : getUnmodifiableNodeList()) {
            if (node instanceof IControllerPO) {
                for (INodePO cont : node.getUnmodifiableNodeList()) {
                    chain.addIterator(cont.getNodeListIterator());
                }
            }
        }
        return chain;
    }

}