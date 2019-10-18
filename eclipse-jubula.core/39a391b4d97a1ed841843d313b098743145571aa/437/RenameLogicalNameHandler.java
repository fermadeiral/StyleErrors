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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.handlers.rename.AbstractRenameComponentNameHandler;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler for renaming Logical Component Names.
 *
 * @author BREDEX GmbH
 * @created Jan 12, 2009
 */
public class RenameLogicalNameHandler 
    extends AbstractRenameComponentNameHandler {

    /** {@inheritDoc} */
    public Object executeImpl(final ExecutionEvent event) {
        final IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
        if (activeEditor instanceof ObjectMappingMultiPageEditor) {
            ObjectMappingMultiPageEditor omEditor = 
                (ObjectMappingMultiPageEditor)activeEditor;
            final IComponentNamePO compName = getSelectedComponentName();
            if (compName != null) {
                final JBEditorHelper editorHelper = omEditor.getEditorHelper();
                editorHelper.doEditorOperation(new IEditorOperation() {
                    public void run(IPersistentObject workingPo) {
                        String newName = 
                                getNewName(event, compName);
                        if (newName != null) {
                            
                            rename(editorHelper.getEditSupport().getCache(), 
                                compName.getGuid(), newName);
                            
                            editorHelper.setDirty(true);

                            DataEventDispatcher.getInstance()
                                .fireDataChangedListener(compName, 
                                        DataState.Renamed, 
                                        UpdateState.onlyInEditor);

                            // Issue a selection event to update properties 
                            // view, if open.
                            ISelectionProvider selectionProvider =
                                activeEditor.getSite().getSelectionProvider();
                            if (selectionProvider != null) {
                                ISelection currentSelection = 
                                    selectionProvider.getSelection();
                                selectionProvider.setSelection(
                                        StructuredSelection.EMPTY);
                                selectionProvider.setSelection(
                                        currentSelection);
                            }
                        }
                    }
                });
            }
        }
        
        return null;
    }

}
