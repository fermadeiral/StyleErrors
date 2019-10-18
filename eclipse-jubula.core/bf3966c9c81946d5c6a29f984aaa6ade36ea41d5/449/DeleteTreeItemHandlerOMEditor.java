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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.sourceprovider.AbstractJBSourceProvider;
import org.eclipse.jubula.client.ui.rcp.sourceprovider.ObjectMappingModeSourceProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 *
 */
public class DeleteTreeItemHandlerOMEditor 
        extends AbstractDeleteTreeItemHandler {
 
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (!(activePart instanceof ObjectMappingMultiPageEditor)) {
            return null;
        }

        final ObjectMappingMultiPageEditor editor = 
            (ObjectMappingMultiPageEditor)activePart;
        editor.getEditorHelper().doEditorOperation(new IEditorOperation() {
            public void run(IPersistentObject workingPo) {
                IStructuredSelection selection = getSelection();
                Class classType = null;
                for (Object obj : selection.toArray()) {
                    //selectionitems must be same type 
                    if (classType == null) {
                        classType = obj.getClass();
                    }
                    if (obj.getClass() != classType) {
                        return;
                    }
                }
                Object lastParent = null;
                if (selection.size() == 1) { 
                    lastParent = deleteSingleElement(
                            selection.getFirstElement(), editor);
                } else if (selection.size() > 1) {
                    boolean delete = false;
                    delete = MessageDialog.openConfirm(getActiveShell(),
                        Messages.DeleteTreeItemActionOMEditorOMTitle,
                        Messages.DeleteTreeItemActionOMEditorOMText3);
                    if (delete) {
                        lastParent = deleteMultipleElements(
                                selection.toArray(), editor);
                    }
                }
                if (lastParent != null) {
                    refreshViewer(editor, lastParent);
                }
            }
        });
        
        return null;
    }

    /**
     * @param toDelete The items to delete.
     * @param editor The editor in which the delete is taking place.
     * @return the parent of one of the given elements before its deletion, 
     *         or <code>null</code> if no elements are deleted.
     */
    public static Object deleteMultipleElements(Object [] toDelete,
            ObjectMappingMultiPageEditor editor) {
        IObjectMappingCategoryPO lastParent = null;
        for (Object node : toDelete) {
            if (node instanceof IComponentNamePO) {
                lastParent = editor.getOmEditorBP().deleteCompName(
                        (IComponentNamePO)node, true);
            } else if (node instanceof IObjectMappingAssoziationPO) {
                lastParent = editor.getOmEditorBP().deleteAssociation(
                        (IObjectMappingAssoziationPO)node);
            } else if (node instanceof IObjectMappingCategoryPO) {
                lastParent = editor.getOmEditorBP().deleteCategory(
                        (IObjectMappingCategoryPO)node);
            }
        }
        if (toDelete.length > 0) {
            editor.getEditorHelper().setDirty(true);
            editor.cleanupNames();
            editor.synchronizeViewers();
        }
        return lastParent;
    }
    
    /**
     * Prompts the user if they really want to delete the given item. 
     * If the user consents, the item is deleted. Otherwise, no action is
     * taken.
     * 
     * @param toDelete The item to delete.
     * @param editor The editor in which the delete is taking place.
     * @return the parent of the element before its deletion, or 
     *         <code>null</code> if no element is deleted.
     */
    private Object deleteSingleElement(Object toDelete,
            ObjectMappingMultiPageEditor editor) {

        boolean delete = false;
        Object lastParent = null;
        if (toDelete instanceof IObjectMappingAssoziationPO) {
            delete = MessageDialog.openConfirm(getActiveShell(),
                Messages.DeleteTreeItemActionOMEditorOMTitle,
                Messages.DeleteTreeItemActionOMEditorOMText1);
            if (delete) {
                lastParent = editor.getOmEditorBP().deleteAssociation(
                        (IObjectMappingAssoziationPO)toDelete);
                editor.getEditorHelper().setDirty(true);
                editor.cleanupNames();
                editor.synchronizeViewers();
            }
        } else if (toDelete instanceof IComponentNamePO) {
            delete = MessageDialog.openConfirm(getActiveShell(),
                Messages.DeleteTreeItemActionOMEditorOMTitle,
                Messages.DeleteTreeItemActionOMEditorOMText2);
            if (delete) {
                lastParent = editor.getOmEditorBP().deleteCompName(
                        (IComponentNamePO)toDelete, true);
                editor.getEditorHelper().setDirty(true);
                editor.cleanupNames();
                editor.synchronizeViewers();
            }
        } else if (toDelete instanceof IObjectMappingCategoryPO) {
            delete = MessageDialog.openConfirm(getActiveShell(),
                Messages.DeleteTreeItemActionOMEditorOMTitle,
                Messages.DeleteTreeItemActionOMEditorOMText4);
            if (delete) {
                lastParent = editor.getOmEditorBP().deleteCategory(
                        (IObjectMappingCategoryPO)toDelete);
                editor.getEditorHelper().setDirty(true);
                editor.cleanupNames();
                editor.synchronizeViewers();
            }
        }
        return lastParent;
    }

    /**
     * @param editor The editor containing the viewer to refresh
     * @param newSelection The element that should be selected after the
     *                     refresh. 
     */
    private void refreshViewer(ObjectMappingMultiPageEditor editor,
            Object newSelection) {
        editor.getTreeViewer().refresh();
        if (newSelection != null) {
            checkCategoryToMapInto(editor);
            editor.getTreeViewer().setSelection(
                new StructuredSelection(newSelection));
        }
    }

    /**
     * Checks, if the deleted node was the current category to map into.
     * <br>If <code>true</code>, set category to "unassigned".
     * 
     * @param editor The editor in which the delete occurred.
     */
    private void checkCategoryToMapInto(ObjectMappingMultiPageEditor editor) {
        
        ISelection sel = null;
        IObjectMappingCategoryPO categoryToMapInto = 
            editor.getOmEditorBP().getCategoryToCreateIn();
        if (categoryToMapInto != null) {
            editor.getTreeViewer().setSelection(
                new StructuredSelection(categoryToMapInto));
            sel = editor.getTreeViewer().getSelection();
        }
        if (sel == null || sel.isEmpty()) {
            editor.getOmEditorBP().setCategoryToCreateIn(null);
            ObjectMappingModeSourceProvider omsp = 
                (ObjectMappingModeSourceProvider) 
                    AbstractJBSourceProvider
                        .getSourceProviderInstance(null,
                                ObjectMappingModeSourceProvider.ID);
            if (editor.getAut().equals(TestExecution.getInstance().
                getConnectedAut()) && omsp != null && omsp.isRunning()) {
                String message = NLS.bind(
                    Messages.TestExecutionContributorAUTStartedMapping,
                    Messages.TestExecutionContributorCatUnassigned); 
                int icon = Constants.MAPPING;
                Plugin.showStatusLine(icon, message);
            }
        }
    }
}