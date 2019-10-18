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


import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * This tree node operation selects a tree node.
 * 
 * @author BREDEX GmbH
 * @created 16.03.2005
 */
public class SelectTreeNodeOperation extends AbstractSelectTreeNodeOperation {
    
    /**
     * Constructor
     * @param co the click options to use in this operation
     */
    public SelectTreeNodeOperation(ClickOptions co) {
        super(co);
    }
    /**
     * {@inheritDoc}
     * Selects the node passed to the constructor by performing a single click
     * on the node. This is done only if
     * {@link AbstractTreeNodeOperation#isDeepestPathLevel(String[], int)}
     * returns <code>true</code>. The node is identified by calling
     * {@link JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)}
     * and the returned text is compared to the constructor argument. The method
     * throws a <code>StepExecutionException</code> If the node has not
     * been selected (invalid node).
     */
    public boolean operate(final Object node) throws StepExecutionException {
        getContext().clickNode(node, getClickOption());
        return true;
    }
}