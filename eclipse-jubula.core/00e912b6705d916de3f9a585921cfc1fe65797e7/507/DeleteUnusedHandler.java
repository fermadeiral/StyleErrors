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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBP;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.businessprocess.UnusedSpecTestCasesBP;
import org.eclipse.jubula.client.core.businessprocess.progress.ProgressMonitorTracker;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.TestresultState;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.AbstractCmdHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteEvHandle;
import org.eclipse.jubula.client.core.persistence.MultipleNodePM.DeleteTCHandle;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author BREDEX GmbH
 * @created 06.10.2016
 */
public class DeleteUnusedHandler extends AbstractSelectionBasedHandler {

    /**
     * This class is used for the deletion to be executed in a separate task
     * coupled with a progress bar.
     * 
     * @author BREDEX GmbH
     */
    private class DeleteUnusedOperation implements IRunnableWithProgress {

        /**
         * the List<INodePO> to delete
         */
        private List<INodePO> m_deleteList;

        /**
         * 
         * @param deleteList
         *            the List<INodePO> to delete
         */
        public DeleteUnusedOperation(List<INodePO> deleteList) {
            m_deleteList = deleteList;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run(IProgressMonitor monitor) {
            ProgressMonitorTracker instance = ProgressMonitorTracker.SINGLETON;
            Plugin.startLongRunning(Messages.DeleteUnusedProgress);
            instance.setProgressMonitor(monitor);
            monitor.beginTask(Messages.UIJobDeletingUnused,
                    m_deleteList.size());
            // we only delete SpecTCs, and they are always independent (no Categories...)
            DeleteNodesTransaction.deleteTopNodes(m_deleteList, m_deleteList,
                    monitor);
            monitor.done();
            DataEventDispatcher.getInstance()
                    .fireTestresultChanged(TestresultState.Refresh);
            instance.setProgressMonitor(null);
            Plugin.stopLongRunning();
        }

    }

    /**
     * The maximum amount of nodes that will be deleted to be shown.
     */
    private static final int MAX_NODES_DISPLAYED = 10;

    /**
     * Closes the editor for the given Node.
     * 
     * @param node
     *            the node of the editor to be closed
     */
    private static void closeOpenEditor(IPersistentObject node) {
        IEditorPart editor = Utils.getEditorByPO(node);
        if (editor != null) {
            editor.getSite().getPage().closeEditor(editor, false);
        }
    }

    /**
     * Creates a label for the confirm dialog from a List<String> containing the
     * names of the items that will be deleted.
     * 
     * @param itemNames
     *            a List<String> containing the item names
     * @return the label for the confirm dialog
     */
    private static String createConfirmLabel(List<String> itemNames) {
        StringBuilder label = new StringBuilder(
                NLS.bind(Messages.DeleteUnusedAction, itemNames.size()));
        label.append(StringConstants.NEWLINE);
        label.append(StringConstants.NEWLINE);
        Iterator iterator = itemNames.iterator();
        int size = 0;
        while (iterator.hasNext()) {
            label.append(Constants.BULLET);
            label.append(iterator.next().toString());
            label.append(StringConstants.NEWLINE);
            if (MAX_NODES_DISPLAYED <= ++size) {
                break;
            }
        }
        if (iterator.hasNext()) {
            label.append(StringConstants.NEWLINE);
            label.append(NLS.bind(Messages.DeleteUnusedActionOverflow,
                    (itemNames.size() - size)));
        }
        return label.toString();
    }

    /**
     * Creates delete commands from a List<INodePO>.
     * 
     * @param nodesToDelete
     *            the nodes to delete
     * @return a List<AbstractCmdHandle> for node deletion
     */
    private static List<AbstractCmdHandle>
            createDeleteCommands(List<INodePO> nodesToDelete) {
        List<AbstractCmdHandle> cmds =
                new ArrayList<AbstractCmdHandle>(nodesToDelete.size());
        ParamNameBPDecorator dec =
                new ParamNameBPDecorator(ParamNameBP.getInstance());
        for (INodePO node : nodesToDelete) {
            dec.clearAllNames();
            if (node instanceof ISpecTestCasePO) {
                cmds.add(new DeleteTCHandle((ISpecTestCasePO) node, dec));
            }
            if (node instanceof IEventExecTestCasePO) {
                cmds.add(new DeleteEvHandle((IEventExecTestCasePO) node));
            }
        }
        return cmds;
    }

    /**
     * Creates an Object[] with a String with the locations of use of the given
     * ISpecTestCasePO.
     * 
     * @param specTcPO
     *            a SpecTestCasePO
     * @param reusesSet
     *            List <IExecTestCasePO>
     * @param nodesToDelete
     *            List<INodePO>
     * @return a String
     */
    private static Object[] createLocOfUseArray(ISpecTestCasePO specTcPO,
            List<IExecTestCasePO> reusesSet, List<INodePO> nodesToDelete) {
        StringBuilder locOfUse = new StringBuilder();
        int size = 0;
        for (IExecTestCasePO node : reusesSet) {
            INodePO parent = node.getParentNode();
            if (parent != null && !nodesToDelete.contains(parent)) {
                locOfUse.append(Constants.BULLET);
                locOfUse.append(parent.getName());
                locOfUse.append(StringConstants.NEWLINE);
                size++;
            }
        }
        return new Object[] { specTcPO.getName(), size, locOfUse.toString() };
    }

    /**
     * Checks if a set contains any parent node of a specified node.
     * 
     * @param set
     *            the Set<INodePO>
     * @param node
     *            the specific INodePO
     * @return <code>true</code> if any parent is already in set,
     *         <code>false</code> otherwise
     */
    private static boolean doesSetContainAnyParent(Set<INodePO> set,
            INodePO node) {
        INodePO parent = node.getParentNode();
        while (parent != null) {
            if (set.contains(parent)) {
                return true;
            }
            parent = parent.getParentNode();
        }
        return false;
    }

    /**
     * Creates a List<INodePO> from an IStructuredSelection.
     * 
     * @param selection
     *            the IStructuredSelection
     * @return return the List<INodePO>
     */
    private static List<INodePO>
            selectionToList(IStructuredSelection selection) {
        @SuppressWarnings("unchecked")
        Set<INodePO> set = new HashSet<INodePO>(selection.toList());
        List<INodePO> top = new ArrayList<INodePO>();
        for (INodePO node : set) {
            if (!doesSetContainAnyParent(set, node)) {
                top.add(node);
            }
        }
        return top;
    }

    /** {@inheritDoc} */
    @Override
    public Object executeImpl(ExecutionEvent event) {
        List<INodePO> nodeList = selectionToList(getSelection());
        if (isValid(nodeList)) {
            List<INodePO> unusedList = new ArrayList<INodePO>();
            for (INodePO node : nodeList) {
                unusedList.addAll(UnusedSpecTestCasesBP
                        .getUnusedSpecTestCases((ISpecTestCasePO) node));
            }
            if (unusedList.isEmpty()) {
                return null;
            }
            Collections.reverse(unusedList);
            if (isConfirmed(unusedList)) {
                try {
                    /*
                     * Execute the operation.
                     */
                    for (INodePO node : unusedList) {
                        closeOpenEditor(node);
                    }
                    PlatformUI.getWorkbench().getProgressService().run(true,
                            false, new DeleteUnusedOperation(unusedList));
                } catch (InvocationTargetException e) {
                    /*
                     * Exception occurred during the operation. The exception
                     * was already handled by the operation. Do nothing.
                     */
                } catch (InterruptedException e) {
                    /*
                     * The operation was canceled. Do nothing.
                     */
                }
            }
        }
        return null;
    }

    /**
     * Pops up a "confirm delete" dialog.
     * 
     * @param top
     *            the List<INodePO> of top nodes to delete
     * @return <code>true</code> if "yes" was clicked, <code>false</code>
     *         otherwise
     */
    private boolean isConfirmed(List<INodePO> top) {
        List<String> itemNames = new ArrayList<String>();
        for (Object obj : top) {
            if (obj instanceof INodePO) {
                itemNames.add(((INodePO) obj).getName());
            } else {
                String name = String.valueOf(obj);
                if (!StringUtils.isBlank(name)) {
                    itemNames.add(name);
                }
            }
        }
        if (itemNames.isEmpty()) {
            return false;
        }
        MessageDialog dialog = new MessageDialog(getActiveShell(),
                Messages.DeleteUnusedActionShellTitle, null,
                createConfirmLabel(itemNames), MessageDialog.QUESTION,
                new String[] { Messages.DialogMessageButton_YES,
                    Messages.DialogMessageButton_NO },
                0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog.getReturnCode() == 0;
    }

    /**
     * Checks if the List<INodePO> of top nodes can be deleted without affecting
     * other nodes and pops up an information dialog if they can not be deleted.
     * 
     * @param top
     *            the List<INodePO> of top nodes to delete
     * @return <code>true</code> if the operation is valid, <code>false</code>
     *         otherwise
     */
    private boolean isValid(List<INodePO> top) {
        for (INodePO node : top) {
            if (node instanceof ISpecTestCasePO) {
                ISpecTestCasePO specTcPO = (ISpecTestCasePO) node;
                List<IExecTestCasePO> execTestCases;
                execTestCases = NodePM.getInternalExecTestCases(
                        specTcPO.getGuid(), specTcPO.getParentProjectId());
                if (!MultipleNodePM.allExecsFromList(top, execTestCases)) {
                    /**
                     * The node is reused somewhere else and therefore cannot be
                     * deleted.
                     */
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.I_REUSED_SPEC_TCS,
                            createLocOfUseArray(specTcPO, execTestCases, top),
                            null);
                    return false;
                }
            }
        }
        return true;
    }

}