/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.RefToken;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;

/**
 * @author BREDEX GmbH
 */
public abstract class AbstractEditorDndSupport {
    /**
     * Executes actions after the drop.
     * 
     * @param node
     *            the dropped node.
     * @param targetEditor
     *            The editor to which the item has been dropped/pasted.
     */
    protected static void postDropAction(INodePO node,
            AbstractJBEditor targetEditor) {
        TreeViewer tree = targetEditor.getTreeViewer();
        IPersistentObject root = targetEditor.getEditorHelper().
                getEditSupport().getWorkVersion();
        tree.setExpandedState(root, true);
        IWritableComponentNameCache cache = targetEditor.getCompNameCache();
        targetEditor.setFocus();
        targetEditor.refresh();
        if (node != null) {
            targetEditor.getEditorHelper().setDirty(true);
            tree.setExpandedState(node.getParentNode(), true);
            targetEditor.setSelection(new StructuredSelection(node));
        }
        LocalSelectionTransfer.getInstance().setSelection(null);
    }
    
    /**
     * @param node the node to be moved.
     * @param target the target node.
     * @param pos the position
     * @return the dropped node.
     */
    protected static INodePO moveNode(INodePO node, INodePO target, int pos) {
        int newPos = pos;
        INodePO par = node.getParentNode();
        if (par == target && par.indexOf(node) < pos) {
            newPos--;
        }
        par.removeNode(node);
        target.addNode(newPos, node);
        return node;
    }

    /**
     * Copy the parameters from the old exec test case to the new exec test case.
     * 
     * @param origExec  The original exec test case
     * @param newExec   The new exec test case
     * @param deleteRefDatas are referenced data deleting needed or not
     */
    protected static void fillExec(IExecTestCasePO origExec, 
        IExecTestCasePO newExec, boolean deleteRefDatas) {
        fillParamNode(origExec, newExec);
        newExec.setName(origExec.getRealName());
        ISpecTestCasePO origSpecTC = origExec.getSpecTestCase();
        
        if (!origExec.getDataManager().equals(
                origSpecTC.getDataManager())) {
            newExec.setHasReferencedTD(false);
            if (newExec.getDataManager().getUniqueIds().isEmpty()) {
                origExec.getDataManager().deepCopy(
                        newExec.getDataManager());
            }
        } else {
            newExec.setHasReferencedTD(true);
        }
        
        if (deleteRefDatas) {
            deleteRefDatas(newExec);
        }
        
        for (ICompNamesPairPO origPair : origExec.getCompNamesPairs()) {
            ICompNamesPairPO newPair = PoMaker.createCompNamesPairPO(
                    origPair.getFirstName(), origPair.getSecondName(),
                    origPair.getType());
            newPair.setPropagated(origPair.isPropagated());
            newExec.addCompNamesPair(newPair);
        }
        
        if (newExec instanceof IEventExecTestCasePO
                || origExec instanceof  IEventExecTestCasePO) {
            
            IEventExecTestCasePO newEvent = (IEventExecTestCasePO)newExec;
            IEventExecTestCasePO origEvent = (IEventExecTestCasePO)origExec;
            newEvent.setEventType(origEvent.getEventType());
            newEvent.setReentryProp(origEvent.getReentryProp());
            newEvent.setMaxRetries(origEvent.getMaxRetries());
        }
    }

    /**
     * Delete the referenced test data tokens
     * 
     * @param paramNode parameter node
     */
    protected static void deleteRefDatas(IParamNodePO paramNode) {
        for (Iterator<TDCell> it = paramNode
                .getParamReferencesIterator(); it.hasNext();) {
            TDCell cell = it.next();
            String guid = paramNode.getDataManager()
                    .getUniqueIds().get(cell.getCol());
            IParamDescriptionPO childDesc = paramNode
                    .getParameterForUniqueId(guid);
            // The childDesc can be null if the parameter has been
            // removed in another session and not yet updated in the 
            // current editor session.
            if (childDesc != null) {
                ModelParamValueConverter conv = 
                        new ModelParamValueConverter(cell.getTestData(),
                                paramNode, childDesc);
                List<RefToken> refTokens = conv.getRefTokens();
                for (RefToken refToken : refTokens) {
                    String oldGUID = RefToken.extractCore(refToken
                            .getModelString());
                    conv.removeReference(oldGUID);
                    cell.setTestData(conv.getModelString());
                }
            }
        }
    }
    
    /**
     * Copy the parameters from the old Test step to the new Test step.
     * 
     * @param origCap   The original Test step
     * @param newCap    The new Test step
     */
    protected static void fillCap(ICapPO origCap, ICapPO newCap) {
        fillParamNode(origCap, newCap);
        newCap.setComponentName(origCap.getComponentName());
        newCap.setComponentType(origCap.getComponentType());
        newCap.setActionName(origCap.getActionName());
        newCap.getDataManager().clearUniqueIds();
        origCap.getDataManager().deepCopy(newCap.getDataManager());
    }

    /**
     * Copy the parameters from the old reference test suite to the new reference test suite.
     * 
     * @param origRefTS The original reference test suit.
     * @param newRefTS  The new reference test suit.
     */
    protected static void fillRefTestSuit(IRefTestSuitePO origRefTS, 
        IRefTestSuitePO newRefTS) {
        fillNode(origRefTS, newRefTS);
        newRefTS.setName(origRefTS.getRealName());
        newRefTS.setTestSuiteAutID(origRefTS.getTestSuiteAutID());
    }
    
    /**
     * Copy the parameters from the old node to the new node.
     * 
     * @param origNode  The original node.
     * @param newNode   The new node.
     */
    protected static void fillNode(INodePO origNode, INodePO newNode) {
        newNode.setActive(origNode.isActive());
        newNode.setComment(origNode.getComment());
        newNode.setGenerated(origNode.isGenerated());
        newNode.setDescription(origNode.getDescription());
        newNode.setToolkitLevel(origNode.getToolkitLevel());
    }
    
    /**
     * Copy the parameters from the old  paramter node to the new parameter node.
     * 
     * @param origNode  The original parameter node.
     * @param newNode   The new parameter node.
     */
    protected static void fillParamNode(IParamNodePO origNode,
            IParamNodePO newNode) {

        fillNode(origNode, newNode);
        newNode.setName(origNode.getName());
        newNode.setDataFile(origNode.getDataFile());
        newNode.setReferencedDataCube(origNode.getReferencedDataCube());
    }

}