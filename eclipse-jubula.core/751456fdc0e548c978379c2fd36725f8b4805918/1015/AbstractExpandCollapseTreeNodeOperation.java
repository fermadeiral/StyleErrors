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
package org.eclipse.jubula.rc.common.implclasses.tree;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * This operation expands or collapses a JTree node.
 * 
 * @author BREDEX GmbH
 * @created 22.03.2005
 */
public abstract class AbstractExpandCollapseTreeNodeOperation 
    extends AbstractTreeNodeOperation {
    /**
     * If <code>true</code>, the operation collapses the node, otherwise
     * it expands the node. Defaults to <code>false</code>.
     */
    private boolean m_collapse = false;
    
    /**
     * Creates a new instance. It collapses the nodes if the passed
     * <code>collapse</code> parameter is <code>true</code>, otherwise it
     * expands the nodes.
     * @param collapse <code>true</code> or <code>false</code>
     */
    protected AbstractExpandCollapseTreeNodeOperation(boolean collapse) {
        m_collapse = collapse;
    }
    
    /**
     * {@inheritDoc}
     * Expands or collapses the passed tree node. This is done by calling the
     * expand()/collapse() method of JTree. If the node is already expanded or 
     * collapsed, respectively, nothing happens.
     */
    public abstract boolean operate(final Object node)
        throws StepExecutionException;

    /**
     * @return the collapse
     */
    public boolean isCollapse() {
        return m_collapse;
    }

    /**
     * @param collapse the collapse to set
     */
    public void setCollapse(boolean collapse) {
        m_collapse = collapse;
    }
}
