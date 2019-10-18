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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

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
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Case Browser.
 *
 * @author BREDEX GmbH
 * @created 19.10.2011
 */
public abstract class AbstractBrowserDndSupport {

    /**
     * Private constructor
     */
    protected AbstractBrowserDndSupport() {
        // Do nothing
    }

    /**
     * tries to move all selected node into the target node. Operates on the GUI model
     * and on the INodePO model.
     * @param target
     *      GuiNode
     * @param nodes
     *      List <INodePO>
     * @return whether the operation was successfull
     */
    protected static boolean doMove(final List<INodePO> nodes,
            final IPersistentObject target) {
        if (nodes.isEmpty()) {
            return false;
        }
        
        final Set<IPersistentObject> toLock = new HashSet<>();
        final Set<IPersistentObject> toRefresh = new HashSet<>();
        IProjectPO proj = GeneralStorage.getInstance().getProject();
        
        for (INodePO node : nodes) {
            toLock.add(node.getParentNode());
            toRefresh.add(node.getParentNode());
            toLock.add(node);
            toRefresh.add(node);
        }
        toLock.add(target);
        toRefresh.add(target);
        
        boolean succ = TransactionWrapper.executeOperation(new
                ITransaction() {
            
            /** {@inheritDoc} */
            public void run(EntityManager sess) {
                for (INodePO node : nodes) {
                    NativeSQLUtils.moveNode(sess, node, target);
                }
            }
            
            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToRefresh() {
                return toRefresh;
            }
            
            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToLock() {
                return toLock;
            }

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToMerge() {
                return null;
            }
        });
        
        if (!succ) {
            return false;
        }
        
        List<DataChangedEvent> eventList = 
                new ArrayList<DataChangedEvent>();
        for (INodePO nodeToMove : nodes) {
            eventList.add(new DataChangedEvent(target, 
                    DataState.StructureModified, UpdateState.notInEditor));
            eventList.add(new DataChangedEvent(nodeToMove.getParentNode(), 
                    DataState.StructureModified, UpdateState.notInEditor));
        }
        // notify listener for updates
        DataEventDispatcher.getInstance().fireDataChangedListener(
                eventList.toArray(new DataChangedEvent[0]));
        return true;
    }
    
}
