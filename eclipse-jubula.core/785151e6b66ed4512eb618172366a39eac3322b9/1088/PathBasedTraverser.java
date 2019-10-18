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
 * Traverses based on a given path.
 *
 * @author BREDEX GmbH
 * @created Dec 1, 2006
 */
public class PathBasedTraverser extends AbstractPathBasedTraverser {

    /**
     * The tree node path.
     */
    private INodePath m_treePath;

    /**
     * @param context The context
     * @param treePath 
     *      A series of <code>Object</code>s representing tree nodes. The
     *      Traverser attempts to follow this path.
     */
    public PathBasedTraverser(AbstractTreeOperationContext context, 
            INodePath treePath) {
        super(context, treePath);
        
        m_treePath = treePath;
    }

    /**
     * @param context The context
     * @param treePath 
     *      A series of <code>Object</code>s representing tree nodes. The
     *      Traverser attempts to follow this path.
     * @param constraint The constraint
     */
    public PathBasedTraverser(AbstractTreeOperationContext context, 
            INodePath treePath, TreeNodeOperationConstraint constraint) {
        super(context, treePath, constraint);
        
        m_treePath = treePath;
    }

    /**
     * Traverses the tree node path, is called recursively.
     * 
     * @param node
     *            The tree node.
     * @param level
     *            The level
     * @param operation
     *            The tree node operation.
     * @throws StepExecutionException
     *             If the <code>operate()</code> method of the operation
     *             fails or a tree node in the path cannot be found at the
     *             given level.
     */
    private void traversePath(Object node, int level, 
            TreeNodeOperation operation) throws StepExecutionException {
    
        if (level == m_treePath.getLength()) {
            return;
        }
        
        boolean found = false;
        int childCount = getContext().getNumberOfChildren(node);
    
        for (int i = 0; i < childCount && !found; i++) {
            Object child = getContext().getChild(node, i);

            found = m_treePath.isInPath(child, level, getContext());
    
            if (found) {
                if (isOperable(level, m_treePath.getLength())) {
                    callOperation(child, operation);
                }
                traversePath(child, level + 1, operation);
            }
        }
        
        if (!found) {
            throwTreeNodeNotFound(level);
        }
    }

    /**
     * {@inheritDoc}
     * Traverses the tree node path passed to the constructor and calls the
     * passed tree node operation. The tree path entries are compared to the
     * result of
     * {@link JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)}.
     * @param operation
     *            The tree node operation
     * @throws StepExecutionException
     *             If the tree path is invalid or if the <code>operate()</code>
     *             method of the operation fails.
     */
    public void traversePath(TreeNodeOperation operation, Object startNode) 
        throws StepExecutionException {
        
        operation.setContext(getContext());
        
        if (startNode == null) {
            
            if (m_treePath.getLength() > 0) {
                Object [] rootNodes = getContext().getRootNodes();
            
                
                boolean found = false;
            
                for (int i = 0; i < rootNodes.length && !found; i++) {
                    Object root = rootNodes[i];

                    found = m_treePath.isInPath(root, 0, getContext());
            
                    if (found) {
                        if (isOperable(0, m_treePath.getLength())) {
                            callOperation(root, operation);
                        }
                        traversePath(root, 1, operation);
                    }
                }
                
                if (!found) {
                    throwTreeNodeNotFound(0);
                }
            }
            
        } else {
            traverseRelativePath(startNode, operation);
        }
    }

    /**
     * Traverses the tree node path passed to the constructor and calles the
     * passed tree node operation. The tree path entries are compared to the
     * result of
     * {@link JTree#convertValueToText(java.lang.Object, boolean, boolean, boolean, int, boolean)}.
     * {@inheritDoc}
     * @param operation
     *            The tree node operation
     * @throws StepExecutionException
     *             If the tree path is invalid or if the <code>operate()</code>
     *             method of the operation fails.
     */
    private void traverseRelativePath(Object startNode, 
            TreeNodeOperation operation) throws StepExecutionException {
        
        if (isOperable(-1, m_treePath.getLength())) {
            callOperation(startNode, operation);
        }
    
        boolean found = false;
        int childCount = getContext().getNumberOfChildren(startNode);
        if (m_treePath.getLength() != 0) {
    
            for (int i = 0; i < childCount && !found; i++) {
                Object child = getContext().getChild(startNode, i);

                found = m_treePath.isInPath(child, 0, getContext());
    
                if (found) {
                    if (isOperable(0, m_treePath.getLength())) {
                        callOperation(child, operation);
                    }
                    traversePath(child, 1, operation);
                }
            }
    
            if (!found) {
                throwTreeNodeNotFound(0);
            }
    
        }
        
    }

    /**
     * 
     * @return The tree path
     */
    protected INodePath getTreePath() {
        return m_treePath;
    }

}