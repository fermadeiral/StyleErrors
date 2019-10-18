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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator.NodeTarget;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Suite Editor.
 *
 * @author BREDEX GmbH
 * @created 27.03.2008
 */
public class TSEditorDndSupport extends AbstractEditorDndSupport {

    /** For copy actions the last copied node */
    private INodePO m_last = null;
    
    /** Constructor */
    public TSEditorDndSupport() {
        // for copy actions....
        // ugly, but still nicer than other options...
    }

    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param toDrop The items that were dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @return <code>true</code> if the drop/paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public static boolean performDrop(AbstractTestCaseEditor targetEditor,
            IStructuredSelection toDrop, INodePO dropTarget, int dropPosition) {
    
        return TCEditorDndSupport.performDrop(targetEditor, toDrop, dropTarget, 
                dropPosition);
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be pasted.
     * @param toDrop The items that were copy.
     * @param dropTarget The paste target.
     *                     indicate the drop position relative to the drop
     *                     target.
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    public boolean copyPaste(AbstractTestCaseEditor targetEditor,
            IStructuredSelection toDrop, INodePO dropTarget) {
        
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        
        for (Iterator it = toDrop.iterator(); it.hasNext(); ) {
            if (!(it.next() instanceof INodePO)) {
                return false;
            }
        }
        if (toDrop.isEmpty()) {
            return false;
        }
        
        NodeTarget tar = NodeTargetCalculator.calcNodeTarget(
                (INodePO) toDrop.getFirstElement(), dropTarget,
                ViewerDropAdapter.LOCATION_ON, false);
        
        @SuppressWarnings("unchecked")
        int warning = copyPasteNodes(tar.getNode(),
                toDrop.toList(), tar.getPos());

        if ((warning & 1) != 0) {
            MessageDialog.openInformation(null,
                    Messages.NotUseReferenceParameterTitle,
                    Messages.NotUseReferenceParameter);
        }
        if ((warning & 2) != 0) {
            MessageDialog.openInformation(null,
                    Messages.NotUsePropagatedComponentNameTitle,
                    Messages.NotUsePropagatedComponentName);
        }
        postDropAction(m_last, targetEditor);
        return true;
    }
    
    /**
     * Copies a list of nodes to a node
     * @param target the target
     * @param nodes the nodes
     * @param pos the position
     * @return the message mask whether there were parameter / propagation issues
     */
    private int copyPasteNodes(INodePO target, List<INodePO> nodes,
            int pos) {
        int position = pos;
        int msg = 0;
        for (INodePO node : nodes) {
            if (node instanceof IParamNodePO
                    && ((IParamNodePO) node).getParamReferencesIterator().
                    hasNext()) {
                msg |= 1;
            }
            if (node instanceof IExecTestCasePO) {
                msg |= copyPasteExecTestCase((IExecTestCasePO) node,
                        target, position);
            } else if (node instanceof IControllerPO) {
                // we assume a very strict structure here
                // controllers have Container children which in turn can only have
                // CapPO, ExecTCPO, CommentPO, ... children (so no Controllers or Containers)
                IControllerPO controller = NodeMaker.
                        createControllerPO((IControllerPO) node); 
                List<INodePO> nodeList = node.getUnmodifiableNodeList();
                List<INodePO> contList = controller.getUnmodifiableNodeList();
                if (node instanceof IParamNodePO) {
                    fillParamNode((IParamNodePO) node,
                            (IParamNodePO) controller);
                    deleteRefDatas((IParamNodePO) controller);
                } else {
                    fillNode(node, controller);
                }
                target.addNode(position, controller);
                for (int i = 0; i < nodeList.size(); i++) {
                    copyPasteNodes(contList.get(i),
                            nodeList.get(i).getUnmodifiableNodeList(), 0);
                }
                m_last = controller;
            } else if (node instanceof ICommentPO) {
                INodePO comm = NodeMaker.createCommentPO(
                        ((ICommentPO) node).getName());
                fillNode(node, comm);
                target.addNode(pos, comm);
                m_last = comm;
            }
            position++;
        }
        return msg;
    }
        
    /**
     * @param exec original exec test case node
     * @return <code>false</code> if paramNodePO contain propagated component name
     */
    private static boolean checkCompName(
            IExecTestCasePO exec) {
        boolean was = false;
        for (ICompNamesPairPO origPair : exec.getCompNamesPairs()) {
            if (origPair.isPropagated()) {
                origPair.setPropagated(false);
                was = true;
            }
        }
        return was;
    }
    
    /**
     * @param execTestCase The item that was dragged/cut.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @param targetNode target parent node
     * @return <code>true</code> if the paste was successful. 
     *         Otherwise <code>false</code>.
     */
    private int copyPasteExecTestCase(IExecTestCasePO execTestCase,
            INodePO targetNode, int dropPosition) {
        int msg = 0;
        IExecTestCasePO newExecTestCase = NodeMaker.createExecTestCasePO(
                execTestCase.getSpecTestCase());
        m_last = newExecTestCase;
        fillExec(execTestCase, newExecTestCase, true);
        if (checkCompName(newExecTestCase)) {
            msg = 2;
        }
        TestCaseBP.addReferencedTestCase(targetNode, newExecTestCase,
                dropPosition);
        
        return msg;
    }

    /**
     * Checks whether the nodes in the given selection can legally be copied
     * to the given target location.
     *
     * @param toDrop The selection to check.
     * @param target The target location to check.
     * @return <code>true</code> if the move is legal. Otherwise, 
     *         <code>false</code>.
     */
    public static boolean validateCopy(IStructuredSelection toDrop,
            INodePO target) {
        if (toDrop == null || toDrop.isEmpty() || target == null) {
            return false;
        }
        for (Iterator it = toDrop.iterator(); it.hasNext(); ) {
            Object next = it.next();
            if (!(next instanceof IExecTestCasePO || next instanceof ICommentPO
                    || next instanceof IControllerPO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the nodes in the given selection can legally be moved
     * to the given target location.
     *
     * @param sourceViewer The viewer containing the dragged/cut item.
     * @param targetViewer The viewer to which the item is to be dropped/pasted.
     * @param selection The selection to check.
     * @param target The target location to check.
     * @param allowFromBrowser Whether items from the Test Case Browser are 
     *                         allowed to be dropped/pasted.
     * @return <code>true</code> if the move is legal. Otherwise, 
     *         <code>false</code>.
     */
    public static boolean validateDrop(Viewer sourceViewer, Viewer targetViewer,
            IStructuredSelection selection, 
            Object target, boolean allowFromBrowser) {
        
        if (selection == null || !(target instanceof INodePO)) {
            return false;
        }
        
        INodePO targNode = (INodePO) target;

        if (sourceViewer != null && !sourceViewer.equals(targetViewer)) {
            boolean foundOne = false;
            for (TestCaseBrowser tcb : MultipleTCBTracker.getInstance()
                    .getOpenTCBs()) {
                if (sourceViewer.equals(tcb.getTreeViewer())) {
                    foundOne = true;
                }
            }
            if (!(allowFromBrowser && foundOne)) {
                return false;
            }
        }

        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object next = iter.next();
            if (!(next instanceof INodePO)
                    || next instanceof IAbstractContainerPO) {
                return false;
            } 
            if (next instanceof IControllerPO
                    && !(targNode instanceof ITestSuitePO)
                    && !(targNode.getParentNode() instanceof ITestSuitePO)) {
                return false;
            }
            INodePO node = (INodePO)next;
            if (!((node instanceof IExecTestCasePO
                    && sourceViewer == targetViewer)
                    || (node instanceof ICommentPO
                            && sourceViewer == targetViewer)
                    || (node instanceof ISpecTestCasePO)
                    || (node instanceof IControllerPO))) {
                
                return false;
            }
        }
        return true;
    }

}
