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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;


/**
 * @author BREDEX GmbH
 * @created 26.04.2005
 */
public class OMDropTargetListener extends ViewerDropAdapter {

    /** editor */
    private ObjectMappingMultiPageEditor m_editor;

    /**
     * Constructor
     * 
     * @param editor
     *            The editor.
     * @param viewer
     *            The viewer.
     */
    public OMDropTargetListener(ObjectMappingMultiPageEditor editor,
            Viewer viewer) {
        super(viewer);
        m_editor = editor;
        boolean scrollExpand = Plugin.getDefault().getPreferenceStore()
                .getBoolean(Constants.TREEAUTOSCROLL_KEY);
        setScrollExpandEnabled(scrollExpand);
    }

    /**{@inheritDoc} */
    public void dropAccept(DropTargetEvent event) {
        if (!(LocalSelectionTransfer.getTransfer().getSelection() 
                instanceof IStructuredSelection)) {
            return;
        }
        
        IStructuredSelection selection = 
            (IStructuredSelection)LocalSelectionTransfer.getTransfer()
                .getSelection();
        
        ObjectMappingMultiPageEditor editor = getEditor();
        if (editor.getEditorHelper().requestEditableState() 
                != JBEditorHelper.EditableState.OK) {
            return;
        }
        
        Object target = getCurrentTarget() != null 
            ? getCurrentTarget() : getViewer().getInput();

        if (containsOnlyType(selection, 
                IObjectMappingAssoziationPO.class)) {
            // Use logic for dropping associations
            List<IObjectMappingAssoziationPO> toMove = selection.toList();
            dropAssociations(editor, toMove, target);
        } else if (containsOnlyType(selection, 
                IObjectMappingCategoryPO.class)) {
            // Use logic for dropping categories
            List<IObjectMappingCategoryPO> toMove = selection.toList();
            dropCategories(toMove, target);
            editor.getTreeViewer().refresh();
        } else if (containsOnlyType(selection, IComponentNamePO.class)) {
            // Use logic for dropping Component Names
            List<IComponentNamePO> toMove = selection.toList();
            if (dropComponentNames(editor, toMove, target)) {
                return;
            }
        }

        LocalSelectionTransfer.getTransfer().setSelection(null);
        editor.getEditorHelper().setDirty(true);
        event.item = null; // because to avoid an SWTException:
        // item will be disposed after getViewer().refresh()

        getViewer().setSelection(new StructuredSelection(target));
    }

    /**
     * 
     * @param editor
     *            The editor in which the drop occurred.
     * @param toMove
     *            The associations being moved.
     * @param target
     *            The location to which the associations are being moved.
     */
    protected void dropAssociations(ObjectMappingMultiPageEditor editor,
            List<IObjectMappingAssoziationPO> toMove, Object target) {
        if (target instanceof IObjectMappingCategoryPO) {
            OMEditorDndSupport.checkAndMoveAssociations(toMove,
                    (IObjectMappingCategoryPO)target, editor);
        }
    }

    /**
     * 
     * @param editor
     *            The editor in which the drop occurred.
     * @param toMove
     *            The Component Names being moved.
     * @param target
     *            The location to which the Component Names are being moved.
     * @return whether the operation is cancelled by the user
     */
    protected boolean dropComponentNames(ObjectMappingMultiPageEditor editor,
            List<IComponentNamePO> toMove, Object target) {

        if (target instanceof IObjectMappingAssoziationPO) {
            return OMEditorDndSupport.checkTypeCompatibilityAndMove(toMove,
                    (IObjectMappingAssoziationPO)target, editor);
        } else if (target instanceof IObjectMappingCategoryPO) {
            OMEditorDndSupport.checkTypeCompatibilityAndMove(toMove,
                    (IObjectMappingCategoryPO)target, editor);
        }
        return false;
    }

    /**
     * 
     * @param toMove
     *            The categories being moved.
     * @param target
     *            The location to which the categories are being moved.
     * 
     * @return <code>true</code> if the drop was successful. Otherwise
     *         <code>false</code>.
     */
    protected boolean dropCategories(List<IObjectMappingCategoryPO> toMove,
            Object target) {

        if (target instanceof IObjectMappingCategoryPO) {
            IObjectMappingCategoryPO targetCategory = 
                (IObjectMappingCategoryPO)target;
            return OMEditorDndSupport.moveCategories(toMove, targetCategory);
        }

        return false;
    }

    /**
     * @param data
     *            Object
     * @return boolean
     */
    public boolean performDrop(Object data) {
        return true;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean validateDrop(Object target, int op, TransferData type) {
        if (!(LocalSelectionTransfer.getTransfer().getSelection() 
                instanceof IStructuredSelection)) {
            return false;
        }
        IStructuredSelection selection = 
            (IStructuredSelection)LocalSelectionTransfer.getTransfer()
                .getSelection();

        Object dropTarget = target != null ? target : getViewer().getInput();
        if (dropTarget == null) {
            return false;
        }

        if (ObjectMappingTransferHelper.getDndToken() != null
                && !ObjectMappingTransferHelper.getDndToken().equals(
                        m_editor.getAut())) {
            return false;
        }
        if (!hasSingleClassType(selection)) {
            return false;
        }

        if (containsOnlyType(selection, 
                IObjectMappingAssoziationPO.class)) {
            // Use logic for validating associations
            List<IObjectMappingAssoziationPO> toMove = selection.toList();
            if (dropTarget instanceof IObjectMappingCategoryPO) {
                return OMEditorDndSupport.canMoveAssociations(toMove,
                        (IObjectMappingCategoryPO)dropTarget, m_editor);
            }
        } else if (containsOnlyType(selection, 
                IObjectMappingCategoryPO.class)) {
            // Use logic for validating categories
            if (dropTarget instanceof IObjectMappingCategoryPO) {
                return canDropCats(selection.toList(),
                        (IObjectMappingCategoryPO) dropTarget);
            }
            return false;
        } else if (containsOnlyType(selection, IComponentNamePO.class)) {
            // Use logic for validating Component Names
            if (dropTarget instanceof IObjectMappingAssoziationPO) {
                return true;
            } else if (dropTarget instanceof IObjectMappingCategoryPO) {
                return OMEditorDndSupport.canMoveCompNames(
                        (IObjectMappingCategoryPO)dropTarget, m_editor);
            }
        }

        return false;
    }
    
    /**
     * Checks whether the list of Categories is draggable to the target category
     * @param cats the categories
     * @param target the target
     * @return whether
     */
    private static boolean canDropCats(List<IObjectMappingCategoryPO> cats,
            IObjectMappingCategoryPO target) {
        if (cats.isEmpty()) {
            return false;
        }
        IObjectMappingCategoryPO currCat = target;
        while (currCat.getParent() != null) {
            if (cats.contains(currCat)) {
                // Trying to move a parent inside a child??
                // The case when currCat is the top node is ignored, but that
                //      cannot be in cats anyway 
                return false;
            }
            currCat = currCat.getParent();
        }
        IObjectMappingCategoryPO srcTop = cats.get(0);
        while (srcTop.getParent() != null) {
            srcTop = srcTop.getParent();
        }
        // cannot Dnd categories between different tree viewers
        return currCat == srcTop;
    }

    /**
     * @return Returns the editor.
     */
    public ObjectMappingMultiPageEditor getEditor() {
        return m_editor;
    }

    /**
     * Checks whether all elements in the selection are instances of the given
     * class.
     * 
     * @param selection The selection to check.
     * @param supportedClass The class/interface to check against. 
     * @return <code>true</code> if all elements in the selection are instances
     *         of the given class. Otherwise, <code>false</code>.
     */
    public boolean containsOnlyType(
            IStructuredSelection selection, Class supportedClass) {
        
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            if (!supportedClass.isInstance(iter.next())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * checks if there are objects of different classes in selection
     * 
     * @param selection The selection to check.
     * @return boolean
     */
    public boolean hasSingleClassType(IStructuredSelection selection) {
        Class classType = null;
        Iterator iter = selection.iterator();

        while (iter.hasNext()) {
            Object obj = iter.next(); 
            //selectionitems must be same type 
            if (classType == null) {
                classType = obj.getClass();
            }
            if (obj.getClass() != classType) {
                return false;
            }
        }
        return true;
    }

}