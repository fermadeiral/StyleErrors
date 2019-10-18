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
package org.eclipse.jubula.client.teststyle.properties.nodes;

import javax.persistence.EntityManager;

import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.CheckConfMock;
import org.eclipse.jubula.client.teststyle.checks.DecoratingCheck;
import org.eclipse.jubula.client.teststyle.checks.Severity;


/**
 * @author marcell
 * @created Oct 21, 2010
 */
public class CheckNode implements INode {

    /** The copy of the check of this node */
    private BaseCheck m_checkCopy;

    /** The real check */
    private BaseCheck m_check;

    /** The parent as a node */
    private INode m_parent;

    /**
     * The constructor which prepares this node for usage.
     * 
     * @param parent
     *            Most definitily the category of this check.
     * @param check
     *            The check which belongs to this node.
     */
    public CheckNode(INode parent, BaseCheck check) {
        this.m_parent = parent;
        this.m_check = check;

        // Copy check for configuration purpose
        this.m_checkCopy = check.clone();
    }

    /**
     * {@inheritDoc}
     */
    public INode[] getChildren() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public INode getParent() {
        return m_parent;
    }

    /**
     * {@inheritDoc}
     */
    public void save(EntityManager s) {        
        if (m_check.getConf() instanceof CheckConfMock) {
            return; // just not try to save it here - its no use
        }
        m_check.setConf(s.merge(m_check.getConf()));
        m_check.setActive(m_checkCopy.isActive());
        m_check.setAttributes(m_checkCopy.getAttributes());
        m_check.setContexts(m_checkCopy.getContexts());
        m_check.setSeverity(m_checkCopy.getSeverity());
    }

    /**
     * {@inheritDoc}
     */
    public void setState(TreeState state) {
        m_checkCopy.setActive(state == TreeState.CHECKED);
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return m_checkCopy.getName();
    }

    /**
     * {@inheritDoc}
     */
    public TreeState getState() {
        return m_checkCopy.isActive() ? TreeState.CHECKED : TreeState.EMPTY;
    }

    /**
     * @return The check of this CheckNode.
     */
    public BaseCheck getCheck() {
        return m_checkCopy;
    }

    /**
     * 
     * @return The current severity of this node.
     */
    public Severity getSeverity() {
        return m_checkCopy.getSeverity();
    }

    /**
     * @param sev
     *            The new severity for this node.
     */
    public void setSeverity(Severity sev) {
        m_checkCopy.setSeverity(sev);
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(INode o) {
        return this.getText().toLowerCase().compareTo(
                o.getText().toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSeverity() {
        return !(m_check instanceof DecoratingCheck);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEditable() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof CheckNode) {
            CheckNode other = (CheckNode)obj;
            return this.m_check.equals(other.m_check);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return m_check.hashCode();
    }
    
    /** {@inheritDoc} */
    public String getTooltip() {
        return m_check.getFulltextDescription();
    }
}
