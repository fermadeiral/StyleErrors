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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping.OMEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.OMEditorTreeLabelProvider;
import org.eclipse.osgi.util.NLS;


/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 */
public class MapIntoCategoryHandler extends AbstractSelectionBasedHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        setCategoryToMapInto(getSelection());
        return null;
    }
    
    /**
     * Sets the category to map into.
     * 
     * @param selection The current workbench selection.
     */
    private void setCategoryToMapInto(IStructuredSelection selection) {
        ObjectMappingMultiPageEditor editor = 
            ((ObjectMappingMultiPageEditor)Plugin.getActivePart());
        if (editor.getAut().equals(TestExecution.getInstance()
                .getConnectedAut())) {
            
            IObjectMappingCategoryPO category = null;
            
            Object node;
            if (selection.size() == 1) {
                node = selection.getFirstElement();
                IObjectMappingCategoryPO unmappedTechNames = 
                    editor.getAut().getObjMap().getUnmappedTechnicalCategory();
                if (node instanceof IObjectMappingCategoryPO) {
                    IObjectMappingCategoryPO catNode = 
                        (IObjectMappingCategoryPO)node;
                    if (unmappedTechNames.equals(
                            OMEditorDndSupport.getSection(catNode))) {

                        category = catNode;
                    }
                } else if (node instanceof IObjectMappingAssoziationPO) {
                    IObjectMappingAssoziationPO assocNode = 
                        (IObjectMappingAssoziationPO)node;
                    if (unmappedTechNames.equals(
                            OMEditorDndSupport.getSection(assocNode))) {
                        category = assocNode.getCategory();
                    }
                }
                
                editor.getOmEditorBP().setCategoryToCreateIn(category);
                
                String strCat;
                IObjectMappingCategoryPO cat = 
                    ObjectMappingEventDispatcher.getCategoryToCreateIn();
                if (cat != null) {
                    strCat = cat.getName();
                    if (OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat) != null) {
                        strCat = OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat);
                    }
                } else {
                    strCat = Messages.TestExecutionContributorCatUnassigned;
                }
                String message = NLS.bind(
                        Messages.TestExecutionContributorAUTStartedMapping,
                    strCat); 
                Plugin.showStatusLine(Constants.MAPPING, message);
            }
        }
    }
}