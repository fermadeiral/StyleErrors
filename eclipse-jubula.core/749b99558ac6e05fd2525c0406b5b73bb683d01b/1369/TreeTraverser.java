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
package org.eclipse.jubula.client.core.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.tools.internal.exception.JBException;


/**
 * The tree traverser traverses a tree of <code>INodePO</code> instances
 * top-down. The traversion starts at the root node passed to the constructor.
 * The recursion is established by calling
 * {@link org.eclipse.jubula.client.core.model.INodePO#getNodeListIterator()}
 * on any node. On the way top-down, the <code>operate()</code> method of the
 * passed operation is called for any node.
 * 
 * @author BREDEX GmbH
 * @created 13.09.2005
 */
public class TreeTraverser {
    
    /** constant for no maximum traversal depth */
    public static final int NO_DEPTH_LIMIT = -1;

    /**
     * The tree operation.
     */
    private List<ITreeNodeOperation<INodePO>> m_operations = 
        new ArrayList<ITreeNodeOperation<INodePO>>();
    /**
     * The root node.
     */
    private INodePO m_rootNode;
    /**
     * Flag to indicate if the event handlers should be traversed.
     */
    private boolean m_traverseEventHandlers = false;
    
    /**
     * traverses the specification when project as root is given, default
     * is traversing the execution 
     */
    private boolean m_traverseSpecPart = false;
    
    /**
     * traverses the execution when project as root is given, this is the
     * default behavior
     */
    private boolean m_traverseExecPart = true;
    
    /**
     * Traverses reused projects when project as the root is given
     */
    private boolean m_traverseReused = true;
    
    /** Traverses through ExecTCs */
    private boolean m_traverseIntoExecs = true;
    
    /** 
     * The maximum traversal depth. <code>NO_DEPTH_LIMIT</code> by default. 
     */
    private int m_maxDepth = NO_DEPTH_LIMIT;

    /**
     * already visited Nodes. We do not want to visit the same node twice
     */
    private Set<String> m_visited = new HashSet<String>(1001);

    /** the progress monitor */
    private IProgressMonitor m_monitor;

    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     */
    public TreeTraverser(INodePO rootNode) {
        m_rootNode = rootNode;
        setMonitor(new NullProgressMonitor());
    }

    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     * @param operation
     *            The operation to call on any node
     */
    public TreeTraverser(INodePO rootNode, 
            ITreeNodeOperation<INodePO> operation) {
        this(rootNode);
        m_operations.add(operation);
    }

    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     * @param operation
     *            The operation to call on any node
     * @param traverseSpecPart
     *          boolean to indicate if specPart or execPart should be traversed,
     *          when project is given as root
     */
    public TreeTraverser(INodePO rootNode, 
            ITreeNodeOperation<INodePO> operation, 
        boolean traverseSpecPart) {
        this(rootNode, operation, traverseSpecPart, !traverseSpecPart);
    }
    
    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     * @param operation
     *            The operation to call on any node
     * @param traverseSpecPart
     *          boolean to indicate if specPart should be traversed,
     *          when project is given as root
     * @param traverseExecPart
     *          boolean to indicate if execPart should be traversed,
     *          when project is given as root
     */
    public TreeTraverser(INodePO rootNode, 
            ITreeNodeOperation<INodePO> operation, 
        boolean traverseSpecPart,
        boolean traverseExecPart) {
        this(rootNode);
        m_operations.add(operation);
        m_traverseSpecPart = traverseSpecPart;
        m_traverseExecPart = traverseExecPart;
    }

    /**
     * The constructor.
     * 
     * @param rootNode
     *            The node where the traversion starts
     * @param operation
     *            The operation to call on any node
     * @param traverseSpecPart
     *          boolean to indicate if specPart or execPart should be traversed,
     *          when project is given as root
     * @param maxTraversalDepth The maximum depth of traversal. 
     */
    public TreeTraverser(INodePO rootNode, 
            ITreeNodeOperation<INodePO> operation, 
        boolean traverseSpecPart, int maxTraversalDepth) {
        this(rootNode, operation, traverseSpecPart, !traverseSpecPart);
        m_maxDepth = maxTraversalDepth;
    }

    /**
     * Implements the recursive traversion.
     * 
     * @param context
     *            The context
     * @param parent
     *            The parent node
     * @param node
     *            The current node
     */
    protected void traverseImpl(ITreeTraverserContext<INodePO> context, 
            INodePO parent, INodePO node) {
        if (m_maxDepth == NO_DEPTH_LIMIT && !getMonitor().isCanceled()
                || m_maxDepth > context.getCurrentTreePath().size()) {
            context.append(node);
            final boolean alreadyVisited = alreadyVisited(node);
            Set<ITreeNodeOperation<INodePO>> suspendedOps = null;
                
            for (ITreeNodeOperation<INodePO> operation : m_operations) {
                boolean continueWork =
                    operation.operate(context, parent, node, alreadyVisited);
                if (!continueWork) {
                    if (suspendedOps ==  null) {
                        suspendedOps = new HashSet<ITreeNodeOperation<INodePO>>(
                                m_operations.size());
                    }
                    suspendedOps.add(operation);
                }
            }
            if (suspendedOps != null) {
                m_operations.removeAll(suspendedOps);
            }
            addToVisited(node);
            if (context.isContinue() && !m_operations.isEmpty()) {
                if (node instanceof IProjectPO) {
                    IProjectPO project = (IProjectPO)node;
                    traverseProject(context, project);
                } else {
                    if (!(node instanceof IExecTestCasePO)
                            || m_traverseIntoExecs) {
                        // ExecTCs' getNodeListIterator returns the corresponding SpecTestCasePO's node list!
                        for (Iterator<INodePO> it = node.getNodeListIterator(); 
                                it.hasNext();) {
                            traverseImpl(context, node, it.next());
                        }
                    }
                    if (node instanceof IControllerPO) {
                        for (INodePO child : node.getUnmodifiableNodeList()) {
                            traverseImpl(context, node, child);
                        }
                    }
                    if (m_traverseEventHandlers
                            && node instanceof ITestCasePO) {
                        ISpecTestCasePO testCase;
                        if (node instanceof IExecTestCasePO) {
                            testCase = 
                                ((IExecTestCasePO)node).getSpecTestCase();
                        } else {
                            testCase = (ISpecTestCasePO)node;
                        }
                        if (testCase != null) {
                            for (Iterator<IEventExecTestCasePO> it = 
                                    testCase.getAllEventEventExecTC()
                                        .iterator();
                                it.hasNext();) {
                                traverseImpl(context, node, it.next());
                            }
                        }
                    }
                    for (ITreeNodeOperation<INodePO> operation : m_operations) {
                        operation.postOperate(context, parent, node, 
                                alreadyVisited);
                    }
                }
            }
            context.removeLast();
            if (suspendedOps != null) {
                m_operations.addAll(suspendedOps);
            }

        }
    }

    /**
     * Traverses a Project.
     * 
     * @param context
     *            The traversal context.
     * @param project
     *            The Project to traverse.
     */
    private void traverseProject(ITreeTraverserContext<INodePO> context,
            IProjectPO project) {
        if (m_traverseSpecPart) {
            traverseLocalSpecPart(context, project);
            if (m_traverseReused) {
                traverseReusedProjectSpecPart(context, project);
            }
        } 
        if (m_traverseExecPart) {
            traverseExecPart(context, project);
        }
    }

    /**
     * @param context the context 
     * @param project the project
     */
    protected void traverseExecPart(ITreeTraverserContext<INodePO> context,
            IProjectPO project) {
        for (INodePO exec : project.getUnmodExecList()) {
            traverseImpl(context, project, exec);
        }
    }

    /**
     * @param context the context 
     * @param project the project
     */
    protected void traverseReusedProjectSpecPart(
            ITreeTraverserContext<INodePO> context, IProjectPO project) {
        for (IReusedProjectPO reused
                : project.getUsedProjects()) {

            try {
                IProjectPO reusedProject = 
                    ProjectPM.loadReusedProjectInMasterSession(
                            reused);

                if (reusedProject != null) {
                    traverseLocalSpecPart(context, reusedProject);
                }

            } catch (JBException e) {
                // Unable to load Reused Project.
                // The Reused Project will not be traversed.
            }
        }
    }

    /**
     * @param context the context 
     * @param project the project
     */
    protected void traverseLocalSpecPart(
            ITreeTraverserContext<INodePO> context, IProjectPO project) {
        for (INodePO specNode : project.getUnmodSpecList()) {
            traverseImpl(context, project, specNode);
        }
    }
    
    /**
     * Starts the traversion of the tree under the root node passed to the
     * constructor. Event handlers are not included during the traversion.
     */
    public void traverse() {
        traverse(false);
    }
    
    /**
     * Starts the traversion of the tree under the root node passed to the
     * constructor. Event handlers are included during the traversion, if
     * <code>traverseEventHandlers</code> is <code>true</code>.
     * 
     * @param traverseEventHandlers
     *            If <code>true</code>, the event handlers are included
     */
    public void traverse(boolean traverseEventHandlers) {
        clearVisited();
        m_traverseEventHandlers = traverseEventHandlers;
        traverseImpl(new TreeTraverserContext<INodePO>(m_rootNode), 
                null, m_rootNode);
    }
    
    /**
     * 
     * @return the tree node operation
     */
    protected List<ITreeNodeOperation<INodePO>> getOperations() {
        return m_operations;
    }
    
    /**
     * adds a <code>ITreeNodeOperation</code> to the list of operations
     * that are executed on every step
     * 
     * @param op
     *      <code>ITreeNodeOperation</code>
     */
    public void addOperation(ITreeNodeOperation<INodePO> op) {
        m_operations.add(op);
    }

    /**
     * marks this node as processed
     * @param node the usual suspect
     */
    private void addToVisited(INodePO node) {
        m_visited.add(node.getGuid());
    }
    
    /**
     * checks if the node has been processed before
     * @param node the usual suspect
     * @return true if the node is in the set of already processed nodes
     */
    private boolean alreadyVisited(INodePO node) {
        return m_visited.contains(node.getGuid());
    }
    
    /**
     * resets the visited set to empty
     */
    private void clearVisited() {
        m_visited.clear();
    }

    /**
     * @return the monitor
     */
    public IProgressMonitor getMonitor() {
        return m_monitor;
    }

    /**
     * @param monitor the monitor to set
     */
    public void setMonitor(IProgressMonitor monitor) {
        m_monitor = monitor;
    }
    
    /**
     * @param reused whether to traverse reused projects
     */
    public void setTraverseReused(boolean reused) {
        m_traverseReused = reused;
    }
    
    /**
     * @param spec whether to traverse the Spec part (TCB)
     */
    public void setTraverseSpecPart(boolean spec) {
        m_traverseSpecPart = spec;
    }
    
    /**
     * @param bool whether to traverse through ExecTCs (that is, into the corresponding SpecTC)
     */
    public void setTraverseIntoExecs(boolean bool) {
        m_traverseIntoExecs = bool;
    }
}
