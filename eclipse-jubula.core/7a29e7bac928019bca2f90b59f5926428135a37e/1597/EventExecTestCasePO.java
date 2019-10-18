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

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;


/**
 * class to handle an event in testexecution
 * 
 * @author BREDEX GmbH
 * @created 04.04.2005
 */
@Entity
@DiscriminatorValue(value = "H")
class EventExecTestCasePO extends ExecTestCasePO implements
    IEventExecTestCasePO {
    
    /**
     * <code>m_reentryProp</code> option for resuming of testexecution
     */
    private Integer m_reentryProp;
    
    /**
     * <code>m_eventType</code> <br>
     * eventType, for which this eventTestCase is valid
     */
    private String m_eventType;
    
    /**
     * <code>m_maxRetries</code> maximum number of retries for this event handler.
     * This value is currently only used for event handlers with the RETRY
     * reentry type.
     */
    private Integer m_maxRetries;

    /**
     * Persistence (JPA / EclipseLink) constructor
     */
    EventExecTestCasePO() {
        super();
    }
    
    /**
     * constructor
     * @param specTC referenced specTestCase
     * @param assocNode the associated Node which uses this EventHandler
     * @param isGenerated indicates whether this node has been generated
     */
    EventExecTestCasePO(ISpecTestCasePO specTC, INodePO assocNode, 
            boolean isGenerated) {
        super(specTC, isGenerated);
        setParentNode(assocNode);
    }
    
    /**
     * Constructor for unique instances
     * @param prop a reentry property
     */
    EventExecTestCasePO(ReentryProperty prop) {
        super();
        setReentryProp(prop);
    }
    
    /**
     * constructor
     * @param specTC referenced specTestCase
     * @param assocNode the associated Node which uses this EventHandler
     * @param guid the GUID for this EventHandler
     * @param isGenerated indicates whether this node has been generated
     */
    EventExecTestCasePO(ISpecTestCasePO specTC, INodePO assocNode, 
        String guid, boolean isGenerated) {

        super(specTC, guid, isGenerated);
        setParentNode(assocNode);
    }

    /**
     * constructor
     * @param specTCGuid referenced specTestCase GUID
     * @param projectGuid referenced specTestCase's parent project GUID
     * @param assocNode the associated Node which uses this EventHandler
     * @param guid the GUID for this EventHandler
     * @param isGenerated indicates whether this node has been generated
     */
    EventExecTestCasePO(String specTCGuid, String projectGuid, 
        INodePO assocNode, String guid, boolean isGenerated) {

        super(specTCGuid, projectGuid, guid, isGenerated);
        setParentNode(assocNode);
    }

    /**
     * constructor
     * @param specTCGuid referenced specTestCase GUID
     * @param projectGuid referenced specTestCase's parent project GUID
     * @param assocNode the associated Node which uses this EventHandler
     * @param isGenerated indicates whether this node has been generated
     */
    EventExecTestCasePO(String specTCGuid, String projectGuid, 
        INodePO assocNode, boolean isGenerated) {

        super(specTCGuid, projectGuid, isGenerated);
        setParentNode(assocNode);
    }

    /**
     * 
     * @return Returns the reentryProperty value
     */
    @Basic
    @Column(name = "REENTRY_PROP")
    private Integer getReentryPropValue() {
        return m_reentryProp;
    }
    /**
     * @param reentryProp The reentryPoint to set.
     */
    private void setReentryPropValue(Integer reentryProp) {
        m_reentryProp = reentryProp;
    }
    
    /**
     * 
     * @return the ReentryProperty
     */
    @Transient
    public ReentryProperty getReentryProp() {
        try {
            return ReentryProperty.getProperty(getReentryPropValue());
        } catch (InvalidDataException e) {
            return null;
        }
    }
    
    /**
     * Set property
     * @param prop property to be set
     */
    public void setReentryProp(ReentryProperty prop) {
        setReentryPropValue(Integer.valueOf(prop.getValue()));
        if (prop == ReentryProperty.RETRY) {
            // ensure that there is always a maxRetries set for a RETRY action
            if (getMaxRetries() == null) {
                setMaxRetries(Integer.valueOf(1));
            }
        } else {
            setMaxRetries(null); // drop if not doing a RETRY action
        }
    }
    /**
     * 
     * @return Returns the eventType.
     */
    @Basic
    @Column(name = "EVENT_TYPE")
    public String getEventType() {
        return m_eventType;
    }
    /**
     * only for Persistence (JPA / EclipseLink)
     * @param eventType The eventType to set.
     */
    public void setEventType(String eventType) {
        m_eventType = eventType;
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.core.model.NodePO#removeMe(org.eclipse.jubula.client.core.model.INodePO)
     */
    protected void removeMe(INodePO parent) {
        ((ISpecTestCasePO)parent).getEventExecTcMap().remove(getEventType());
        setParentNode(null);
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "MAX_RETRIES")
    public Integer getMaxRetries() {
        return m_maxRetries;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxRetries(Integer maxRetries) {
        m_maxRetries = maxRetries;
    }

}
