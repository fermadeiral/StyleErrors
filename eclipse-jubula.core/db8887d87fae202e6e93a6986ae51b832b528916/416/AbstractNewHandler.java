/*******************************************************************************
 * Copyright (c) 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.TransactionSupport.ITransaction;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.actions.TransactionWrapper;
import org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Markus Tiede
 */
public abstract class AbstractNewHandler extends AbstractHandler {

    /**
     * @param event the execution event
     * @return the parent node to create the new node at
     */
    protected INodePO getParentNode(ExecutionEvent event) {
        INodePO parentNode = null;
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof ITreeSelection) {
            TreePath[] paths = ((ITreeSelection) selection).getPaths();
            
            if (paths.length > 0) {
                int ind = paths[0].getSegmentCount();
                // We need a CategoryPO or the Browser root - the latter is
                // selected by default later, so we ignore that case here
                do {
                    ind--;
                } while (ind > 0
                        && !(paths[0].getSegment(ind) instanceof ICategoryPO));
                if (ind > 0) {
                    parentNode = (INodePO) paths[0].getSegment(ind);
                }
            }
        }
        if (parentNode == null) {
            IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
            IProjectPO proj = GeneralStorage.getInstance().getProject();
            if (activePart instanceof TestSuiteBrowser) {
                parentNode = proj.getExecObjCont();
            } else {
                parentNode = proj.getSpecObjCont();
            }
        }
        return parentNode;
    }
    
    /**
     * Adds a newly created node to the DB and into the master session
     * 
     * @param created the created node
     * @param ev the Event object
     */
    public void addCreatedNode(final INodePO created, ExecutionEvent ev) {
        final INodePO parent = getParentNode(ev);

        final List<IPersistentObject> toLock = new ArrayList<>();
        IProjectPO pr = GeneralStorage.getInstance().getProject();
        final IPersistentObject par;
        toLock.add(parent);
        
        boolean succ = TransactionWrapper.executeOperation(new ITransaction() {
            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToLock() {
                return toLock;
            }

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToRefresh() {
                return toLock;
            }

            /** {@inheritDoc} */
            public void run(EntityManager sess) {
                sess.persist(created);
                NativeSQLUtils.addNodeAFFECTS(sess, created, parent);
            }

            /** {@inheritDoc} */
            public Collection<? extends IPersistentObject> getToMerge() {
                ArrayList<IPersistentObject> toMerge = new ArrayList<>();
                toMerge.add(created);
                return toMerge;
            }
        });
        
        if (succ) {
            INodePO master = GeneralStorage.getInstance().getMasterSession().
                    find(created.getClass(), created.getId());
            DataEventDispatcher.getInstance().fireDataChangedListener(
                        master, DataState.Added, UpdateState.all);
        }
    }
    
}
