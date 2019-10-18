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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.Category;


/**
 * @author marcell
 * @created Oct 21, 2010
 */
public class CategoryNode implements INode {

    /** The category of this node */
    private Category m_category;

    /** The children as a arraylist of nodes */
    private INode[] m_children;

    /**
     * The constructor which prepares this node for usage.
     * 
     * @param category
     *            The category which belongs to this node.
     */
    public CategoryNode(Category category) {
        this.m_category = category;
        List<INode> nodes = new ArrayList<INode>();
        for (BaseCheck check : category.getChecks()) {
            nodes.add(new CheckNode(this, check));
        }
        Collections.sort(nodes);
        m_children = nodes.toArray(new INode[nodes.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public INode[] getChildren() {
        return m_children;
    }

    /**
     * {@inheritDoc}
     */
    public INode getParent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void save(EntityManager s) {
        for (INode node : m_children) {
            node.save(s);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setState(TreeState state) {
        for (INode node : m_children) {
            node.setState(state);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return m_category.getName();
    }

    /**
     * {@inheritDoc}
     */
    public TreeState getState() {
        if (m_children.length == 0) { // no checks, not checked
            return TreeState.EMPTY;
        }
        TreeState state = TreeState.EMPTY; // we assume its empty
        boolean allChecked = true;
        for (INode node : m_children) {
            // if one of the children is checked...
            if (node.getState().equals(TreeState.CHECKED)) { 
                state = TreeState.GRAYED; // ...we gray it
            } else {
                // if one is not, we set the flag on false
                allChecked = false;
            }
        }
        if (allChecked) { // if not even one was not checked...
            return TreeState.CHECKED; // ...the category node is fully checked
        }
        return state; // otherwise its empty or grayed
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(INode o) {
        return this.getText().compareTo(o.getText());
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSeverity() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEditable() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof CategoryNode) {
            CategoryNode other = (CategoryNode)obj;
            return this.m_category.equals(other.m_category);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return m_category.hashCode();
    }

    /** {@inheritDoc} */
    public String getTooltip() {
        return m_category.getDescription();
    }
}
