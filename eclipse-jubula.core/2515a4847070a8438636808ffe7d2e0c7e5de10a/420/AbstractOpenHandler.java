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
package org.eclipse.jubula.client.ui.rcp.handlers.open;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.db.NodeBP;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.client.ui.rcp.editors.PersistableEditorInput;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;


/**
 * An abstract action to open editors.
 * 
 * @author BREDEX GmbH
 * @created 24.11.2004
 */
public abstract class AbstractOpenHandler extends AbstractHandler {
    /**
     * If the supplied node is not editable move up the tree until a editable
     * node is found. The editable state depends on the action to be performed.
     * @param selected Node to check
     * @return the first match for an editable node
     */
    public INodePO findEditableNode(INodePO selected) {
        INodePO candidate = selected;
        while (candidate != null && !isEditableImpl(candidate)) {
            candidate = candidate.getParentNode();
        }
        return candidate;
    }
    
    /**
     * Check if this INodePO is the correct selection for the open action.
     * @param selected Node to check
     * @return true if the selection is applicable; subclasses may override
     */
    protected boolean isEditableImpl(INodePO selected) {
        return true;
    }
    
    /**
     * 
     * @param testSuite the testsuite to open the editor for
     */
    protected void openEditorForSpecTS(ITestSuitePO testSuite) {
        openEditorForSpec(testSuite);
    }
    /**
     * @param specTc the spec to open the editor for
     */
    protected void openEditorForSpecTC(ISpecTestCasePO specTc) {
        openEditorForSpec(specTc);
    }

    /**
     * This method is checking if the node belongs to the current project
     * and if not is opening a dialog to show that this node is from a other project
     * @param node the node to open. 
     */
    private void openEditorForSpec(INodePO node) {
        boolean isNodeEditable = NodeBP.belongsToCurrentProject(node);
        if (isNodeEditable) {
            IEditorPart editor = openEditor(node);
            editor.getSite().getPage().activate(editor);
            if (editor instanceof AbstractJBEditor) {
                AbstractJBEditor jbEditor =
                        (AbstractJBEditor) editor;
                jbEditor.setSelection(
                        new StructuredSelection(node));
            }
        } else {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_NON_EDITABLE_NODE);
        }
    }
    
    /**
     * create editor input
     * @param node which shall be edited
     * @return NodeEditorInput
     * @throws PMException if the node can not be loaded
     */
    private static IEditorInput createEditorInput(IPersistentObject node)
        throws PMException {
        if (node instanceof INodePO) {
            return new NodeEditorInput((INodePO)node);
        }
        return new PersistableEditorInput(node);
    }
    
    /**
     * @param node node for which the editor shall be opened
     * @return an open Editor
     */
    public static IEditorPart openEditor(IPersistentObject node) {
        if (node == null || !Utils.openPerspective(
                Constants.SPEC_PERSPECTIVE)) {
            return null;
        }
        
        try {
            IPersistentObject nodeToEdit = node;
            if (!(node instanceof ISpecTestCasePO || node instanceof IAUTMainPO
                    || node instanceof ITestJobPO
                    || node instanceof ITestDataCategoryPO)
                    && node instanceof INodePO) {
                nodeToEdit = ((INodePO) node).getSpecAncestor();
            }

            IEditorInput input = createEditorInput(nodeToEdit);
            String editorId = getEditorId(nodeToEdit);
            
            IWorkbenchPage page = Plugin.getActivePage();
            IEditorPart editor = page.findEditor(input);
            if (editor == null) {
                try {
                    editor = page.openEditor(input, editorId);
                } catch (PartInitException e) {
                    if (e.getStatus().getSeverity() != IStatus.CANCEL) {
                        final String msg = Messages.EditorCanNotBeOpened 
                            + StringConstants.DOT;
                        throw new JBFatalException(msg, e, 
                            MessageIDs.E_CANNOT_OPEN_EDITOR);
                    }
                }
            } else {
                editor.getSite().getPage().activate(editor);
            }
            return editor;
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForEditor(e, null);
        }
        return null;    
    } 
    
    /**
     * @param node node to open
     * @return the editor ID
     */
    protected static String getEditorId(Object node) {
        if (node instanceof ISpecTestCasePO) {
            return Constants.TEST_CASE_EDITOR_ID;
        }
        if (node instanceof ITestSuitePO) {
            return Constants.TEST_SUITE_EDITOR_ID;
        }
        if (node instanceof IAUTMainPO) {
            return Constants.OBJECTMAPPINGEDITOR_ID;
        }
        if (node instanceof ITestJobPO) {
            return Constants.TEST_JOB_EDITOR_ID;
        }
        if (node instanceof ITestDataCategoryPO) {
            return Constants.CENTRAL_TESTDATA_EDITOR_ID;
        }
        
        
        Assert.notReached();
        return StringUtils.EMPTY;
    }

    /**
     * Opens the editor for the root, and if possible, selects the child
     * @param root the root node of the editor
     * @param child the child to select
     * @return the editor (or null if the operation failed)
     */
    public static IEditorPart openEditorAndSelectNode(
            INodePO root, INodePO child) {
        if (root != null && NodeBP.isEditable(root)) {
            IEditorPart ed = AbstractOpenHandler.openEditor(root);
            if (ed instanceof AbstractJBEditor) {
                INodePO edChild = ((AbstractJBEditor) ed).getEntityManager().
                        find(child.getClass(), child.getId());
                if (edChild != null) {
                    ((AbstractJBEditor) ed).setSelection(
                            new StructuredSelection(edChild));
                }
            }
            return ed;
        }
        return null;
    }
}
