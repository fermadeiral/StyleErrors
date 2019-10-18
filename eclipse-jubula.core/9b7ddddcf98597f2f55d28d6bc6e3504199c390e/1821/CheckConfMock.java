/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.checks;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.client.core.model.ICheckConfPO;


/**
 * @author marcell
 * @created Nov 19, 2010
 */
public class CheckConfMock implements ICheckConfPO {
    
    /** */
    private static final long serialVersionUID = 1L;
    /** active */
    private boolean m_active = false;
    /** severity */
    private String m_severity = Severity.INFO.name();
    /** attr */
    private Map<String, String> m_attr = new HashMap<String, String>();
    /** contexts */
    private Map<String, Boolean> m_contexts = new HashMap<String, Boolean>();
    

    /**
     * {@inheritDoc}
     */
    public void setActive(Boolean active) {
        m_active = active;
    }
    
    /**
     * {@inheritDoc}
     */
    public Boolean isActive() {
        return m_active;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setSeverity(String sev) {
        m_severity = sev;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getSeverity() {
        return m_severity;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setAttr(Map<String, String> attributes) {
        m_attr = attributes;
    }
    
    /**
     * {@inheritDoc}
     */
    public Map<String, String> getAttr() {
        return m_attr;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setContexts(Map<String, Boolean> contexts) {
        m_contexts = contexts;
    }
    
    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> getContexts() {
        return m_contexts;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public Long getId() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getParentProjectId() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getVersion() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        // nothing because its mocking
    }

}
