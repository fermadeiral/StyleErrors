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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMReadException;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.rcp.editors.TestJobEditor;
import org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Job Editor.
 *
 * @author BREDEX GmbH
 * @created Mar 17, 2010
 */
public class TJEditorDndSupport extends AbstractEditorDndSupport {

    /**
     * Private constructor
     */
    private TJEditorDndSupport() {
        // Do nothing
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
    public static boolean copyPaste(AbstractJBEditor targetEditor,
            IStructuredSelection toDrop, INodePO dropTarget) {
        
        final IProjectPO project = GeneralStorage.getInstance().getProject();
        
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Object> selectedElements = toDrop.toList();
        
        ITestJobPO targetNode;
        if (dropTarget instanceof ITestJobPO) {
            targetNode = (ITestJobPO)dropTarget;
        } else {
            targetNode = (ITestJobPO)dropTarget.getParentNode();
        }
        int position = targetNode.indexOf(dropTarget);
        INodePO last = null;
        for (Object obj : selectedElements.toArray()) {
            position++;
            
            if (obj instanceof IRefTestSuitePO) {
                
                last = copyPasteTestSuite(targetEditor, (IRefTestSuitePO)obj,
                        targetNode, position, project);
            } else if (obj instanceof ICommentPO) {
                INodePO comm = NodeMaker.createCommentPO(
                        ((ICommentPO) obj).getName());
                fillNode((INodePO) obj, comm);
                targetNode.addNode(position, comm);
                last = comm;
            } else {
                return false;
            }
        }
        postDropAction(last, targetEditor);
        return true;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be pasted.
     * @param refTestSuit The item that was copy.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @param targetNode target parent node
     * @param project currently project
     * @return the created node
     */
    public static INodePO copyPasteTestSuite(
        AbstractJBEditor targetEditor, IRefTestSuitePO refTestSuit,
        ITestJobPO targetNode, int dropPosition, IProjectPO project) {
    
        IRefTestSuitePO newRefTestSuite = NodeMaker
                .createRefTestSuitePO(refTestSuit.getTestSuite());
        fillRefTestSuit(refTestSuit, newRefTestSuite);
        ParamNameBPDecorator pMapper = targetEditor.getEditorHelper()
                .getEditSupport().getParamMapper();
        targetNode.addNode(dropPosition, newRefTestSuite);
        targetEditor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance()
            .fireDataChangedListener(newRefTestSuite,
                DataState.Added, UpdateState.onlyInEditor);
        return newRefTestSuite;
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
    public static boolean performDrop(TestJobEditor targetEditor,
            IStructuredSelection toDrop, INodePO dropTarget, int dropPosition) {
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        List<Object> selectedElements = toDrop.toList();
        Collections.reverse(selectedElements);
        Iterator iter = selectedElements.iterator();
        while (iter.hasNext()) {
            Object objectToDrop = iter.next();
            if (objectToDrop instanceof ITestSuitePO) {
                ITestSuitePO testSuite = (ITestSuitePO)objectToDrop;
                if (dropTarget != testSuite) {
                    try {
                        if (dropTarget instanceof IRefTestSuitePO) {
                            dropOnRefTS(
                                    testSuite, dropTarget, dropPosition);
                        } else if (dropTarget instanceof ITestJobPO) {
                            dropOnTJ(testSuite, dropTarget);
                        } 
                    } catch (PMException e) {
                        NodeEditorInput inp = (NodeEditorInput)targetEditor.
                            getAdapter(NodeEditorInput.class);
                        INodePO inpNode = inp.getNode();
                        PMExceptionHandler.handlePMExceptionForMasterSession(e);

                        // If an object was already locked, *and* the locked 
                        // object is not the editor Test Case, *and* the editor 
                        // is dirty, then we do *not* want to revert all 
                        // editor changes.
                        // The additional test as to whether the the editor is 
                        // marked as dirty is important because, due to the 
                        // requestEditableState() call earlier in this method, 
                        // the editor TC is locked (even though the editor 
                        // isn't dirty). Reopening the editor removes this lock.
                        if (!(e instanceof PMAlreadyLockedException
                                && ((PMAlreadyLockedException)e)
                                    .getLockedObject() != null
                                && !((PMAlreadyLockedException)e)
                                    .getLockedObject().equals(inpNode))
                            || !targetEditor.isDirty()) {
                            
                            try {
                                targetEditor.reOpenEditor(inpNode);
                            } catch (PMException e1) {
                                PMExceptionHandler.handlePMExceptionForEditor(e,
                                        targetEditor);
                            }
                        }
                        return false;
                    }
                }
            }
            if (objectToDrop instanceof INodePO) {
                INodePO nodeToDrop = (INodePO)objectToDrop;
                if (nodeToDrop instanceof IRefTestSuitePO
                        || nodeToDrop instanceof ICommentPO) {
                    INodePO target = dropTarget;
                    if (target != nodeToDrop
                            && (target instanceof IRefTestSuitePO
                                    || target instanceof ICommentPO)) {
                        moveNode(nodeToDrop, target, target.getParentNode().
                                indexOf(target));
                    }
                }
                postDropAction(nodeToDrop, targetEditor);
            }
        }
        return true;
    }

    /**
     * 
     * @param toDrop The items that were dragged/cut.
     * @param dropTarget The drop/paste target.
     * @return <code>true</code> if the given information indicates that the
     *         drop/paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateCopy(IStructuredSelection toDrop,
            INodePO dropTarget) {
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
            return false;
        }
        for (Iterator it = toDrop.iterator(); it.hasNext(); ) {
            Object next = it.next();
            if (!(next instanceof IRefTestSuitePO
                    || next instanceof ICommentPO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param sourceViewer The viewer containing the dragged/cut item.
     * @param targetViewer The viewer to which the item is to be dropped/pasted.
     * @param toDrop The items that were dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param allowFromBrowser Whether items from the Test Case Suite are 
     *                         allowed to be dropped/pasted.
     * @return <code>true</code> if the given information indicates that the
     *         drop/paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateDrop(Viewer sourceViewer,
            Viewer targetViewer, IStructuredSelection toDrop,
            INodePO dropTarget, boolean allowFromBrowser) {
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
            return false;
        }
        if (sourceViewer != null && !sourceViewer.equals(targetViewer)) {
            TestSuiteBrowser tsb = TestSuiteBrowser.getInstance();
            if (tsb != null) {
                if (!(allowFromBrowser && sourceViewer.equals(tsb
                        .getTreeViewer()))) {
                    return false;
                }
            } else {
                return false;
            }
        }

        for (Object toDropElement : toDrop.toArray()) {
            if (!(toDropElement instanceof ITestSuitePO 
                    || toDropElement instanceof IRefTestSuitePO
                    || toDropElement instanceof ICommentPO)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param node the node to be dropped.
     * @param target the target node.
     * @throws PMReadException in case of db read error
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     */
    private static void dropOnTJ(ITestSuitePO node, INodePO target)
        throws PMReadException, PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        TestSuiteBrowser tsb = TestSuiteBrowser.getInstance();
        if (tsb != null) {
            tsb.addReferencedTestSuite(node, target, 0);
        }
    }

    /**
     * @param node the node to be dropped
     * @param target the target node.
     * @param location One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @throws PMReadException in case of db read error
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     */
    private static void dropOnRefTS(ITestSuitePO node, INodePO target, 
            int location) throws PMReadException, PMAlreadyLockedException, 
            PMDirtyVersionException, PMException {
        INodePO parentGUI = target.getParentNode();
        int position = parentGUI.indexOf(target);
        if (location != ViewerDropAdapter.LOCATION_BEFORE) {
            position++;
        }
        TestSuiteBrowser tsb = TestSuiteBrowser.getInstance();
        if (tsb != null) {
            tsb.addReferencedTestSuite(node, parentGUI, position);
        }
    }
}
