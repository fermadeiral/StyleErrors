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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import javax.persistence.EntityManager;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;


/**
 * BP utility class for GUINodes.
 *
 * @author BREDEX GmbH
 * @created 03.03.2006
 */
public class UINodeBP {

    /**
     * Default utility constructor.
     */
    private UINodeBP() {
        // do nothing
    }
    
    /**
     * sets the given selection in the given TreeViewer and also gives the given
     * tree viewer focus
     * 
     * @param sel
     *            the selection to set
     * @param tv
     *            the tree viewer to use
     */
    public static void setFocusAndSelection(ISelection sel, TreeViewer tv) {
        tv.getTree().setFocus();
        tv.setSelection(sel, true);
    }
    
    /**
     * @param structuredSel
     *            find the referenced specification test case for the given
     *            structured selection
     * @return a valid ISpecTestCasePO or <code>null</code> if no reference
     *         could be found
     */
    public static ISpecTestCasePO getSpecTC(
        IStructuredSelection structuredSel) {
        IExecTestCasePO execTc = null;
        Object firstElement = structuredSel.getFirstElement();
        if (firstElement instanceof IExecTestCasePO) {
            execTc = (IExecTestCasePO)firstElement;
        } else if (firstElement instanceof TestResultNode) {
            TestResultNode trNode = (TestResultNode)firstElement;
            INodePO nodePO = getExecFromTestResultNode(trNode);
            if (nodePO instanceof ITestCasePO
                    && !(nodePO instanceof IExecTestCasePO)) {
                nodePO = NodePM.getNode(GeneralStorage.getInstance()
                        .getProject().getId(), nodePO.getGuid());
                if (nodePO == null) {
                    for (IReusedProjectPO usedProject
                                : GeneralStorage.getInstance()
                            .getProject().getUsedProjects()) {
                        nodePO = NodePM.getNode(usedProject.getId(),
                                nodePO.getGuid());
                        if (nodePO != null) {
                            break;
                        }
                    }
                }
            }
            while (!(nodePO instanceof IExecTestCasePO)) {
                trNode = trNode.getParent();
                if (trNode == null) {
                    return null;
                }
                nodePO = getExecFromTestResultNode(trNode);
            }
            execTc = (IExecTestCasePO)nodePO;
        }
        if (execTc != null) {
            return execTc.getSpecTestCase();
        }
        return null;
    }
    
    /**
     * 
     * @param structuredSel find the referenced {@link ITestSuitePO}
     * @return a valid {@link ITestSuitePO} <code>null</code> if no reference
     *         could be found
     */
    public static ITestSuitePO getSpecTS(IStructuredSelection structuredSel) {
        Object firstElement = structuredSel.getFirstElement();
        if (firstElement instanceof TestResultNode) {
            TestResultNode trNode = (TestResultNode)firstElement;
            INodePO nodePO = trNode.getNode();
            if (nodePO instanceof ITestSuitePO) {
                return (ITestSuitePO) NodePM.getNode(
                        GeneralStorage.getInstance()
                        .getProject().getId(), nodePO.getGuid());
            }
        } else if (firstElement instanceof ITestSuitePO) {
            return (ITestSuitePO) firstElement;
        }
        return null;
    }

    /**
     * Tries to select a node with the given ID in the given TreeViewer.
     * 
     * @param id
     *            The id of the node to select
     * @param tv
     *            the TreeViewer
     * @param em
     *            the entity manager to use for retrieving the node with the
     *            given id; bear in mind that if e.g. the entity manager is the
     *            master session it the object with the given id may be found
     *            within this entity manager but not in the given viewer, as it
     *            does not display this element
     * @return true if select succeeded, false otherwise
     */
    public static INodePO selectNodeInTree(Long id, TreeViewer tv,
            EntityManager em) {
        return (INodePO)selectNodeInTree(
                em.find(NodeMaker.getNodePOClass(), id), tv);
    }
    
    /**
     * Tries to select the given node in the given TreeViewer.
     * 
     * @param o
     *            The Object to select
     * @param tv
     *            the TreeViewer
     * @return the object which should be selected if found in tree viewer, null
     *         otherwise
     */
    public static Object selectNodeInTree(Object o, AbstractTreeViewer tv) {
        ISelection oldSelection = tv.getSelection();
        if (o != null) {
            tv.refresh();
            tv.expandToLevel(o, 0);
            tv.reveal(o);
            StructuredSelection newSelection = new StructuredSelection(o);
            tv.setSelection(newSelection);
            InteractionEventDispatcher.getDefault()
                    .fireProgammableSelectionEvent(newSelection);
            ISelection currSelection = tv.getSelection();
            if (currSelection instanceof StructuredSelection) {
                Object currObj = ((StructuredSelection)currSelection)
                        .getFirstElement();
                IElementComparer comparer = tv.getComparer();
                if (comparer != null) {
                    if (comparer.equals(o, currObj)) {
                        return o;
                    }
                } else {
                    if (o.equals(currObj)) {
                        return o;
                    }
                }
            }
        }
        tv.setSelection(oldSelection);
        return null;
    }
    
    /**
     * This method is getting the Node from {@link TestResultNode}. 
     * If this is a generated {@link ITestCasePO} than it is searched in the 
     * database for the correct node.
     * @param trNode the testResult node
     * @return an {@link INodePO} which can be of any kind and from any project
     */
    private static INodePO getExecFromTestResultNode(TestResultNode trNode) {
        INodePO nodePO = trNode.getNode();
        String guid = nodePO.getGuid();
        if (nodePO instanceof ITestCasePO 
                && nodePO.isGenerated()) {
            nodePO = NodePM.getNode(GeneralStorage.getInstance()
                    .getProject().getId(), guid);
            if (nodePO == null) {
                for (IReusedProjectPO usedProject : GeneralStorage.getInstance()
                        .getProject().getUsedProjects()) {
                    nodePO = NodePM.getNode(usedProject.getId(), guid);
                    if (nodePO != null) {
                        break;
                    }
                }
            }
        }
        return nodePO;
    }
}