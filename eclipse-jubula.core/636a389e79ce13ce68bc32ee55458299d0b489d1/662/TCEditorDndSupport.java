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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.db.TestCaseBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.TDCell;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMDirtyVersionException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.RefToken;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.MultipleTCBTracker;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator;
import org.eclipse.jubula.client.ui.rcp.utils.NodeTargetCalculator.NodeTarget;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;


/**
 * Utility class containing methods for use in drag and drop as well as 
 * cut and paste operations in the Test Case Editor.
 *
 * @author BREDEX GmbH
 * @created 25.03.2008
 */
public class TCEditorDndSupport extends AbstractEditorDndSupport {
    
    /**
     * String to compare if the Message for a circular Dependency was already reported
     */
    private static String reportMessage = StringConstants.EMPTY;

    /**
     * Private constructor
     */
    private TCEditorDndSupport() {
        // Do nothing
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
        
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<Object> selectedElements = toDrop.toList();
        Collections.reverse(selectedElements);
        Iterator iter = selectedElements.iterator();
        while (iter.hasNext()) {
            INodePO droppedNode = null;
            Object obj = iter.next();
            if (!(obj instanceof INodePO)) {
                return false;
            }
            INodePO node = (INodePO)obj;
            boolean exp = targetEditor.getTreeViewer().
                    getExpandedState(dropTarget);
            if (!(node instanceof ISpecTestCasePO)) {
                NodeTarget tar = NodeTargetCalculator.calcNodeTarget(node,
                        dropTarget, dropPosition, exp);
                if (tar != null) {
                    droppedNode = moveNode(node, tar.getNode(), tar.getPos());
                }
            } else {
                droppedNode = performDrop(targetEditor, dropTarget, 
                        dropPosition, (ISpecTestCasePO)node, exp);
                if (droppedNode == null) {
                    return false;
                }
            }
            postDropAction(droppedNode, targetEditor);
        }
        targetEditor.runLocalChecks();
        return true;
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
    public static boolean copyPaste(AbstractTestCaseEditor targetEditor,
            IStructuredSelection toDrop, INodePO dropTarget) {
        
        if (targetEditor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK
                || !(dropTarget.getSpecAncestor() instanceof ISpecTestCasePO)) {
            return false;
        }
        if (toDrop.isEmpty()) {
            return false;
        }
        ISpecTestCasePO targetSpecTC = (ISpecTestCasePO) 
                dropTarget.getSpecAncestor();
        
        boolean noTCHandlers = false;
        INodePO last = null;
        for (Object obj : getFullList(toDrop)) {
            if (!(obj instanceof INodePO)) {
                return false;
            }

            if (obj instanceof IEventExecTestCasePO) {
                
                last = copyPasteEventExecTestCase(targetEditor,
                        (IEventExecTestCasePO)obj, targetSpecTC);
                continue;
            }
            noTCHandlers = true;
            if (obj instanceof IParamNodePO) {
                IParamNodePO paramNode = (IParamNodePO)obj;
                if (!(targetSpecTC.equals(paramNode.getSpecAncestor())
                        || checkParentParameters(targetSpecTC, paramNode, null,
                                false))) {
                    return false;
                }
            }
        }
        if (noTCHandlers) {
            NodeTarget tar = NodeTargetCalculator.calcNodeTarget(
                    (INodePO) toDrop.getFirstElement(), dropTarget,
                    ViewerDropAdapter.LOCATION_ON, false);
            last = copyPasteNodes(targetEditor, tar.getNode(),
                    toDrop.toList(), tar.getPos());
        }
        postDropAction(last, targetEditor);
        targetEditor.runLocalChecks();
        return true;
    }
    
    /**
     * Recursively copies a list of nodes
     * @param editor the editor
     * @param target the target node
     * @param nodes the nodes to copy
     * @param fromPos the position
     * @return the last node
     */
    private static INodePO copyPasteNodes(AbstractTestCaseEditor editor,
            INodePO target, List<INodePO> nodes, int fromPos) {
        ParamNameBPDecorator pMapper = editor.getEditorHelper()
                .getEditSupport().getParamMapper();
        int pos = fromPos;
        INodePO last = null;
        for (INodePO node : nodes) {
            if (node instanceof ICapPO) {
                last = copyPasteCap(editor, (ICapPO) node, target, pos);
            } else if (node instanceof IExecTestCasePO) {
                last = copyPasteExecTestCase(editor, (IExecTestCasePO) node,
                        target, pos);
            } else if (node instanceof ICommentPO) {
                INodePO comm = NodeMaker.createCommentPO(
                        ((ICommentPO) node).getName());
                fillNode(node, comm);
                target.addNode(pos, comm);
                last = comm;
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
                    checkParentParameters((ISpecTestCasePO) target.
                            getSpecAncestor(), (IParamNodePO) controller,
                            pMapper, true);
                } else {
                    fillNode(node, controller);
                }
                target.addNode(pos, controller);
                for (int i = 0; i < nodeList.size(); i++) {
                    copyPasteNodes(editor, contList.get(i),
                        nodeList.get(i).getUnmodifiableNodeList(), 0);
                }
                last = controller;
            }
            pos++;
        }
        return last;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param origEvent The item that was dragged/cut.
     * @param targetNode target parent node
     * @return the node
     */
    public static INodePO copyPasteEventExecTestCase(
            AbstractTestCaseEditor targetEditor,
            IEventExecTestCasePO origEvent, ISpecTestCasePO targetNode) {
        IEventExecTestCasePO newEvent = null;
        if (targetNode.getEventExecTcMap()
                .containsKey(origEvent.getEventType())) {
            
            boolean status = MessageDialog.openQuestion(null,
                    Messages.DoubleEventTypeTitle,
                    NLS.bind(Messages
                            .TestCaseEditorDoubleEventTypeErrorDetailOverwrite,
                             new Object[]{targetNode.getName(), 
                                I18n.getString(origEvent.getEventType())}));
            if (status) {
                targetNode.getEventExecTcMap()
                    .remove(origEvent.getEventType());
            } else {
                return null;
            }
        }
        
        final EditSupport editSupport = targetEditor.getEditorHelper()
                .getEditSupport();
        ParamNameBPDecorator pMapper = targetEditor.getEditorHelper()
                .getEditSupport().getParamMapper();
        if (targetNode.equals(origEvent.getParentNode())
                || checkParentParameters(targetNode, origEvent, pMapper,
                        false)) {
            
            try {
                newEvent = NodeMaker
                        .createEventExecTestCasePO(origEvent
                        .getSpecTestCase(), targetNode);
                fillExec(origEvent, newEvent, false);
                checkParentParameters(targetNode, newEvent, pMapper, true);
                TestCaseBP.addEventHandler(editSupport, targetNode, newEvent);
                targetEditor.getEditorHelper().setDirty(true);

                DataEventDispatcher.getInstance()
                    .fireDataChangedListener(newEvent,
                        DataState.Added, UpdateState.onlyInEditor);
            } catch (InvalidDataException e) {
                // no log entry, because it is a use case!
                ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_DOUBLE_EVENT, null, 
                    new String[]{NLS.bind(
                            Messages.TestCaseEditorDoubleEventTypeErrorDetail,
                            new Object[]{targetNode.getName(), 
                                I18n.getString(origEvent.getEventType())})});
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            }
        } else {
            return null;
        }
        return newEvent;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param execTestCase The item that was dragged/cut.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @param targetNode target parent node
     * @return the new node
     */
    public static INodePO copyPasteExecTestCase(
        AbstractTestCaseEditor targetEditor, IExecTestCasePO execTestCase,
        INodePO targetNode, int dropPosition) {
    
        IExecTestCasePO newExecTestCase = NodeMaker
                .createExecTestCasePO(execTestCase.getSpecTestCase());
        fillExec(execTestCase, newExecTestCase, false);
        ParamNameBPDecorator pMapper = targetEditor.getEditorHelper()
                .getEditSupport().getParamMapper();
        checkParentParameters((ISpecTestCasePO) targetNode.getSpecAncestor(),
                newExecTestCase, pMapper, true);
        TestCaseBP.addReferencedTestCase(targetNode, newExecTestCase,
                dropPosition);
        targetEditor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance()
            .fireDataChangedListener(newExecTestCase,
                DataState.Added, UpdateState.onlyInEditor);
        
        return newExecTestCase;
    }
    
    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param cap The item that was dragged/cut.
     * @param targetNode target parent node
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @return the new node
     */
    public static INodePO copyPasteCap(
            AbstractTestCaseEditor targetEditor, ICapPO cap,
            INodePO targetNode, int dropPosition) {
        
        ParamNameBPDecorator pMapper = targetEditor.getEditorHelper()
                .getEditSupport().getParamMapper();
        
        ICapPO newCap = CapBP.createCapWithDefaultParams(cap.getName(),
                cap.getComponentName(), cap.getComponentType(),
                cap.getActionName());
        fillCap(cap, newCap);
        newCap.setParentNode(targetNode);
        targetNode.addNode(dropPosition, newCap);
        checkParentParameters((ISpecTestCasePO) targetNode.getSpecAncestor(),
                newCap, pMapper, true);
        targetEditor.getTreeViewer().expandToLevel(targetNode, 1);
        DataEventDispatcher.getInstance().fireParamChangedListener();
        
        return newCap;
    }

    /**
     * 
     * @param targetEditor The editor to which the item is to be dropped/pasted.
     * @param toDrop The item that was dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param dropPosition One of the values defined in ViewerDropAdapter to 
     *                     indicate the drop position relative to the drop
     *                     target.
     * @param exp whether target is expanded
     * @return the new IExecTestCasePO object if the drop/paste was successful. 
     *         Otherwise <code>null</code>.
     */
    private static IExecTestCasePO performDrop(
        AbstractTestCaseEditor targetEditor, INodePO dropTarget,
        int dropPosition, ISpecTestCasePO toDrop, boolean exp) {
        INodePO target = dropTarget;
        if (target != toDrop) {
            EditSupport editSupport = 
                targetEditor.getEditorHelper().getEditSupport();
            try {
                NodeTarget tar = NodeTargetCalculator.calcNodeTarget(toDrop,
                        dropTarget, dropPosition, exp);
                if (tar == null) {
                    return null;
                }
                if (target instanceof ISpecTestCasePO) {
                    return dropOnSpecTc(editSupport, toDrop, tar.getNode(),
                            tar.getPos());
                } else if (target instanceof ITestSuitePO) {
                    return dropOnTestsuite(editSupport,
                            (ITestSuitePO)target, toDrop, tar.getPos());
                } else {
                    return dropOnSgElse(editSupport, toDrop, tar.getNode(),
                            tar.getPos());
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
            }
        }
        
        return null;
    }
    
    /**
     * Check the parameter of the new parent node. If possible it generate
     * the needed parameters and return a modified execTestCase
     * else it drop up an error message and return null. 
     *  
     * @param targetNode target node
     * @param paramNode original exec test case node
     * @param pMapper ParamNameBPDecorator
     * @param create if <code>true</code> the parameter will be created
     * @return <code>false</code> if target parent node has a different parameter type
     *          with same name than the new parameter node .Otherwise <code>true</code>.
     */
    
    private static boolean checkParentParameters(
            ISpecTestCasePO targetNode, IParamNodePO paramNode,
            ParamNameBPDecorator pMapper, boolean create) {
        
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
                    String paramName = ParamNameBP.getInstance().getName(
                            oldGUID, childDesc.getParentProjectId());

                    @SuppressWarnings("unchecked")
                    Map<String, String> oldToNewGuids = new HashedMap();
                    IParamDescriptionPO parentParamDescr = targetNode
                            .getParameterForName(paramName);
                    
                    if (parentParamDescr == null) {
                        if (create) {
                            targetNode.addParameter(childDesc.getType(),
                                    paramName, pMapper);
                            parentParamDescr = targetNode
                                    .getParameterForName(paramName);
                        }
                    } else if (!parentParamDescr.getType()
                            .equals(childDesc.getType())) {
                        MessageDialog.openInformation(null,
                                Messages.ParameterConfligtDetectedTitle,
                                NLS.bind(Messages.ParameterConfligtDetected,
                                new Object[] {parentParamDescr.getName(),
                                        targetNode.getName()}));
                        return false;
                    }
                    
                    if (create) {
                        if (parentParamDescr != null) {
                            String newGuid = parentParamDescr.getUniqueId();
                            oldToNewGuids.put(oldGUID, newGuid);
                        }
                        // update test data of child with UUID for reference
                        conv.replaceUuidsInReferences(oldToNewGuids);
                        cell.setTestData(conv.getModelString());
                    }
                }
            }
        }
        return true;
    }

    /**
     * 
     * @param toDrop The items that were copy.
     * @param dropTarget The paste target.
     * @return <code>true</code> if the given information indicates that the
     *         paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateCopy(IStructuredSelection toDrop,
            INodePO dropTarget) {
        
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null
                || !(dropTarget.getSpecAncestor() instanceof ISpecTestCasePO)) {
            return false;
        }
        
        ISpecTestCasePO targSpecTC;
        targSpecTC = (ISpecTestCasePO)dropTarget.getSpecAncestor();
        
        INodePO par = null;
        for (Object obj : toDrop.toArray()) {
            if (!(obj instanceof INodePO)) {
                return false;
            }
            if (obj instanceof IControllerPO
                    && !(dropTarget instanceof ISpecTestCasePO)
                    && dropTarget.getParentNode() != targSpecTC) {
                // Controllers can only be direct children of SpecTCs
                return false;
            }

            INodePO dropNode = (INodePO)obj;
            if (par != null && !par.equals(dropNode.getParentNode())) {
                return false;
            }
        }
        
        for (Object obj : getFullList(toDrop)) {
            INodePO dropNode = (INodePO)obj;
            par = dropNode.getParentNode();
            if (!(dropNode instanceof IExecTestCasePO
                    || dropNode instanceof ISpecTestCasePO)
                    || targSpecTC.equals(dropNode.getSpecAncestor())) {
                continue;
            }
            ISpecTestCasePO dropSpecTC = null;
            if (dropNode instanceof ISpecTestCasePO) {
                dropSpecTC = (ISpecTestCasePO)dropNode;
            } else if (dropNode instanceof IExecTestCasePO) {
                dropSpecTC = ((IExecTestCasePO)dropNode).getSpecTestCase();
            }
            if (dropSpecTC.hasCircularDependencies(targSpecTC)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns the full list of nodes by adding grandchildren of ControllerPOs
     * @param nodes the list of nodes
     * @return the new list
     */
    private static List getFullList(IStructuredSelection nodes) {
        List<Object> res = new ArrayList<>(nodes.size());
        for (Iterator it = nodes.iterator(); it.hasNext(); ) {
            Object next = it.next();
            res.add(next);
            if (next instanceof IControllerPO) {
                for (Iterator<INodePO> itC = ((INodePO) next).getAllNodeIter();
                        itC.hasNext(); ) {
                    res.add(itC.next());
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @param sourceViewer The viewer containing the dragged/cut item.
     * @param targetViewer The viewer to which the item is to be dropped/pasted.
     * @param toDrop The items that were dragged/cut.
     * @param dropTarget The drop/paste target.
     * @param allowFromBrowser Whether items from the Test Case Browser are 
     *                         allowed to be dropped/pasted.
     * @return <code>true</code> if the given information indicates that the
     *         drop/paste is valid. Otherwise <code>false</code>.
     */
    public static boolean validateDrop(Viewer sourceViewer, Viewer targetViewer,
            IStructuredSelection toDrop, INodePO dropTarget, 
            boolean allowFromBrowser) {
        if (toDrop == null || toDrop.isEmpty() || dropTarget == null) {
            return false;
        }

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

        Iterator iter = toDrop.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (!(obj instanceof INodePO)
                    || obj instanceof IAbstractContainerPO) {
                return false;
            }
            if (obj instanceof IControllerPO
                && !(dropTarget instanceof ISpecTestCasePO)
                && !(dropTarget.getParentNode() instanceof ISpecTestCasePO)) {
                return false;
            }
            INodePO transferGUI = (INodePO)obj;
            if (!(transferGUI instanceof ISpecTestCasePO) 
                    && transferGUI.getSpecAncestor() 
                        != dropTarget.getSpecAncestor()) {
                return false;
            }
            if (!(transferGUI instanceof ISpecTestCasePO)) {
                continue;
            }
            ISpecTestCasePO specTcGUI = (ISpecTestCasePO) dropTarget.
                    getSpecAncestor();
            if (transferGUI.hasCircularDependencies(specTcGUI)) {
                reportCircularDependencies(transferGUI, specTcGUI);
                return false;
            }
        }
        return true;
    }
    
    /**
     * write a message to the console to notify the user of an attempt
     * to add a TestCase that would result in a recursive call
     * @param transferGUI the node at which a circular dependency would occur by performing the drop
     * @param specTcGUI 
     * 
     */
    private static void reportCircularDependencies(
            INodePO transferGUI, ISpecTestCasePO specTcGUI) {
        Plugin plugin = Plugin.getDefault();
        if (transferGUI.getParentNode() != null) {
            String path = transferGUI.collectPathtoConflictNode(specTcGUI);
            String message = "CIRCULAR DEPENDENCIES AT " + path + //$NON-NLS-1$
                " FOUND TESTCASE NOT APPLIED"; //$NON-NLS-1$
            if (getReportedMessage().equals(message)) {
                return;
            }
            setReportedMessage(message);
            Status status = new Status(2, Constants.PLUGIN_ID, message);
            plugin.writeStatus(status, Constants.PLUGIN_ID);
        }
    }

    /**
     * @param editSupport The EditSupport in which to perform the action.
     * @param node the node to be dropped.
     * @param target the target node.
     * @param pos the position
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     * 
     * @return the new execTestCaseNode
     */
    private static IExecTestCasePO dropOnSpecTc(EditSupport editSupport, 
            INodePO node, INodePO target, int pos)
        throws PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        return TestCaseBP.addReferencedTestCase(editSupport, 
                target, (ISpecTestCasePO)node, pos);
    }
    
    /**
     * Drops the given TestCase on the given TestSuite.
     * The TestCase will be inserted at the end.
     * @param editSupport The EditSupport in which to perform the action.
     * @param testSuite the TestSuite to drop on
     * @param testcase the TestCAse to drop
     * @param pos the position
     * @throws PMAlreadyLockedException in case of persistence error
     * @throws PMDirtyVersionException in case of persistence error
     * @throws PMException in case of persistence error
     * 
     * @return the new execTestCaseNode
     */
    private static IExecTestCasePO dropOnTestsuite(EditSupport editSupport, 
            ITestSuitePO testSuite, ISpecTestCasePO testcase, int pos) 
        throws PMAlreadyLockedException, 
        PMDirtyVersionException, PMException {
        
        return TestCaseBP.addReferencedTestCase(editSupport, testSuite, 
                testcase, pos);
    }

    /**
     * @param editSupport The EditSupport in which to perform the action.
     * @param node the node to be dropped
     * @param target the target node.
     * @param pos the position
     * @throws PMDirtyVersionException in case of version conflict (dirty read)
     * @throws PMAlreadyLockedException if the origSpecTc is already locked by another user
     * @throws PMException in case of unspecified db error
     * 
     * @return the new execTestCaseNode
     */
    private static IExecTestCasePO dropOnSgElse(EditSupport editSupport, 
            INodePO node, INodePO target,
            int pos) throws PMAlreadyLockedException,
            PMDirtyVersionException, PMException {
        return TestCaseBP.addReferencedTestCase(editSupport, target, 
                (ISpecTestCasePO) node, pos);
    }

    /**
     * @return the last reported circular dependecy  message
     */
    public static String getReportedMessage() {
        return reportMessage;
    }

    /**
     * @param message reported circular dependecy  message
     */
    public static void setReportedMessage(String message) {
        reportMessage = message;
    }
}
