/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.client.ui.rcp.actions.TransactionWrapper;

/**
 * Deletes a collection of nodes from the DB the nodes have to be independent
 * and they all must be either TSBrowser or TCBrowser-based
 * 
 * @author BREDEX GmbH
 *
 */
public class DeleteNodesTransaction implements ITransaction {

    /** The nodes to delete */
    private Collection<INodePO> m_nodes;

    /** All nodes which will be deleted */
    private Collection<INodePO> m_allNodes;

    /** Nodes to lock for the operation */
    private List<IPersistentObject> m_toLock;

    /** Nodes to refresh after the operation */
    private Set<IPersistentObject> m_toRefresh;

    /** The progress monitor or null */
    private IProgressMonitor m_monitor;

    /**
     * @param topNodes
     *            the non-empty list of nodes to delete
     * @param allNodes
     *            the list of all nodes to be deleted (this includes all
     *            descendants)
     * @param monitor
     *            the progress monitor or null if no monitor
     */
    public DeleteNodesTransaction(Collection<INodePO> topNodes,
            Collection<INodePO> allNodes, IProgressMonitor monitor) {
        m_nodes = topNodes;
        m_allNodes = allNodes;
        m_monitor = monitor;
        m_toRefresh = new HashSet<>();
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        for (INodePO node : topNodes) {
            m_toRefresh.add(node.getParentNode());
        }
        m_toLock = new ArrayList<>();
        m_toLock.addAll(m_allNodes);
        m_toLock.addAll(m_toRefresh);
    }

    /** {@inheritDoc} */
    public Collection<? extends IPersistentObject> getToLock() {
        return m_toLock;
    }

    /** {@inheritDoc} */
    public Collection<? extends IPersistentObject> getToRefresh() {
        return m_toRefresh;
    }

    /** {@inheritDoc} */
    public Collection<? extends IPersistentObject> getToMerge() {
        return null;
    }

    /** {@inheritDoc} */
    public void run(EntityManager sess) {
        NativeSQLUtils.deleteFromTCTSTreeAFFECTS(sess, m_nodes, m_monitor);
    }

    /**
     * Deletes nodes from either the TC or TS Browser and notifies the ITE of
     * this
     * 
     * @param topNodes
     *            the top nodes - these should be independent
     * @param allNodes
     *            all nodes going to be deleted (this includes all descendants
     *            of the top nodes)
     * @param monitor
     *            the progress monitor
     */
    public static void deleteTopNodes(Collection<INodePO> topNodes,
            Collection<INodePO> allNodes, IProgressMonitor monitor) {
        if (!TransactionWrapper.executeOperation(
                new DeleteNodesTransaction(topNodes, allNodes, monitor))) {
            return;
        }

        List<DataChangedEvent> eventList = new ArrayList<DataChangedEvent>();
        for (INodePO node : topNodes) {
            eventList.add(new DataChangedEvent(node, DataState.Deleted,
                    UpdateState.all));
        }
        CompNameManager.getInstance().countUsage();
        DataEventDispatcher.getInstance().fireDataChangedListener(
                eventList.toArray(new DataChangedEvent[0]));
    }

}
