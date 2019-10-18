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

import java.util.HashSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Handler for creating a new Logical Component Name in the OMEditor.
 *
 * @author BREDEX GmbH
 * @created Jan 12, 2009
 */
public class NewLogicalNameHandler extends AbstractNewComponentNameHandler {

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof ObjectMappingMultiPageEditor) {
            final ObjectMappingMultiPageEditor omEditor = 
                (ObjectMappingMultiPageEditor)activePart;
            omEditor.getEditorHelper().doEditorOperation(
                    new IEditorOperation() {
                        public void run(IPersistentObject workingPo) {
                            // Show dialog
                            String newName = openDialog();
                            if (newName != null) {
                                performOperation(omEditor, newName);
                            }
                        }
                    });
        }
        
        return null;
    }
    
    /**
     * Creates the new Component Name.
     * 
     * @param omEditor The editor in which the creation is taking place.
     * @param newName The name for the new Component Name.
     */
    private void performOperation(
            ObjectMappingMultiPageEditor omEditor, String newName) {
        
        IObjectMappingPO objMap = omEditor.getAut().getObjMap();
        IWritableComponentNameCache cache = omEditor.getCompNameCache();
        IObjectMappingAssoziationPO assoc = 
            PoMaker.createObjectMappingAssoziationPO(
                    null, new HashSet<String>());
        IComponentNamePO compName = performOperation(newName, cache);
        
        cache.changeReuse(assoc, null, compName.getGuid());
        objMap.getUnmappedLogicalCategory().addAssociation(assoc);
        omEditor.getEditorHelper().setDirty(true);
        DataEventDispatcher.getInstance().fireDataChangedListener(
                objMap.getUnmappedLogicalCategory(), 
                DataState.StructureModified, UpdateState.onlyInEditor);
        DataEventDispatcher.getInstance().fireDataChangedListener(
                compName, DataState.Added, UpdateState.all);
        omEditor.getTreeViewer().setExpandedState(
                objMap.getUnmappedLogicalCategory(), true);
    }

}
