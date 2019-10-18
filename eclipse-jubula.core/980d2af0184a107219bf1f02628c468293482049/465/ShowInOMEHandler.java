/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.handlers.open.AbstractOpenHandler;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.NodeSearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.ObjectMappingSearchResultElementAction;

import java.util.Set;

/**
 * @author BREDEX GmbH
 * @created 29.06.2016
 */
public class ShowInOMEHandler extends AbstractHandler {
    
    /** The parameter id of the ID of the association to jump to */
    private static final String JUMP_ID_COMMANDID = "org.eclipse.jubula.client.ui.rcp.commands.ShowInOME.parameter.jumpId"; //$NON-NLS-1$
    
    /** The parameter id of the type of action that should perform the jump to */
    private static final String JUMP_ACTION_COMMANDID = "org.eclipse.jubula.client.ui.rcp.commands.ShowInOME.parameter.jumpAction"; //$NON-NLS-1$

    /** The parameter id of the aut of which the object mapping editor should be opened */
    private static final String JUMP_AUTNAME_COMMANDID = "org.eclipse.jubula.client.ui.rcp.commands.ShowInOME.parameter.jumpAutName"; //$NON-NLS-1$
    
    /** The ObjectMappingSearchResultElementAction */
    private static final String ACTION_OBJ_MAP = "ObjectMappingSearchResultElementAction"; //$NON-NLS-1$
    
    /** The NodeSearchResultElementAction */
    private static final String ACTION_NODE_ELEM = "NodeSearchResultElementAction"; //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object executeImpl(ExecutionEvent event) {
        
        String jumpId = event.getParameter(JUMP_ID_COMMANDID);
        
        String action = event.getParameter(JUMP_ACTION_COMMANDID);
        
        String jumpAutName = event
                .getParameter(JUMP_AUTNAME_COMMANDID);
        
        try {
            if (action != null) {
                if (jumpId != null && action == ACTION_OBJ_MAP) {
                    ObjectMappingSearchResultElementAction elementAction = 
                            new ObjectMappingSearchResultElementAction();
                    
                    elementAction.jumpTo(Long.valueOf(jumpId));
                } else if (jumpAutName != null 
                        && action == ACTION_NODE_ELEM) {
                    jumpToAutsOME(jumpAutName);
                }
            }
                
        } catch (Exception e) {
            return Status.CANCEL_STATUS;
        }
        
        return Status.OK_STATUS;

    }

    /**
     * If necessary opens and then jumps to the OME of the given AUT
     * @param jumpAutName the name of the AUT the OME should be opened for 
     * and then jumped to
     */
    private void jumpToAutsOME(String jumpAutName) {
        NodeSearchResultElementAction elementAction =
                new NodeSearchResultElementAction();
        
        IAUTMainPO jumpAut = null;
        Set<IAUTMainPO> auts =  GeneralStorage.getInstance()
                .getProject().getAutMainList();
        for (IAUTMainPO aut : auts) {
            if (aut.getName().equals(jumpAutName)) {
                jumpAut = aut;
                break;
            }
        }
        if (jumpAut != null) {
            AbstractOpenHandler.openEditor(jumpAut);
        }
    }
}
