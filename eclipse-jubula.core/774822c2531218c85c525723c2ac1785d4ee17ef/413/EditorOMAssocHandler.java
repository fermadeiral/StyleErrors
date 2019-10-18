/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.client.ui.rcp.handlers.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.AssocOMtoSpecTCDialog;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 *
 */
public class EditorOMAssocHandler extends AbstractSelectionBasedHandler {

    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event)
            throws ExecutionException {
        final ISpecTestCasePO firstElement =
                getFirstElement(ISpecTestCasePO.class);
        final IEditorPart editor = HandlerUtil.getActiveEditor(event);
        ((IJBEditor) editor).getEditorHelper().doEditorOperation(workingPo -> {
            if (firstElement != null) {
                AssocOMtoSpecTCDialog dialog = new AssocOMtoSpecTCDialog(
                        getActiveShell(), firstElement);
                dialog.create();
                int result = dialog.open();
                if (result != Window.OK) {
                    return;
                }
                saveNewSelection(firstElement, editor,
                        dialog.getSelectedItems());
            }
        });

        return null;
    }
    /**
     * 
     * @param specTC the {@link ISpecTestCasePO} to save the new OM Assoc
     * @param editor the editor, must be instance of {@link IJBEditor}
     * @param selectedItems the selected {@link IObjectMappingCategoryPO}
     */
    private void saveNewSelection(final ISpecTestCasePO specTC,
            final IEditorPart editor,
            Collection<IObjectMappingCategoryPO> selectedItems) {
        if (editor instanceof IJBEditor) {
            JBEditorHelper editHelper = ((IJBEditor) editor).getEditorHelper();
            editHelper.setDirty(true);
            List<IObjectMappingCategoryPO> toAdd = new ArrayList<>();
            
            for (IObjectMappingCategoryPO category : selectedItems) {
                toAdd.add(editHelper.getEditSupport().getSession().find(
                        PoMaker.getObjectMappingCategoryClass(),
                        category.getId()));
                
            }
            specTC.setOmCategoryAssoc(new ArrayList<>(toAdd));
            DataEventDispatcher.getInstance().firePropertyChanged(false);
        } else {
            throw new IllegalArgumentException("IEdittorPart is not instance if IJBEditor"); //$NON-NLS-1$
        }
    }
}
