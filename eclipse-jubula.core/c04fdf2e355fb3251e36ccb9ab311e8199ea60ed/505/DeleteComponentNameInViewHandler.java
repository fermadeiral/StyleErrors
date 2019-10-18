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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;


/**
 * Handles the deletion of one or more Component Names in a View.
 * 
 * @author BREDEX GmbH
 * @created Mar 6, 2009
 */
public class DeleteComponentNameInViewHandler 
    extends AbstractDeleteTreeItemHandler {

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IStructuredSelection structuredSelection = getSelection();
        Set<IComponentNamePO> toDelete = new HashSet<IComponentNamePO>();
        for (Object obj : structuredSelection.toArray()) {
            if (obj instanceof IComponentNamePO) {
                toDelete.add((IComponentNamePO)obj);
            }
        }

        List<String> itemNames = new ArrayList<String>();
        for (IComponentNamePO compName : toDelete) {
            itemNames.add(compName.getName());
        }

        if (confirmDelete(itemNames)) {
            try {
                CompNameManager.getInstance().deleteCompNames(toDelete);
                for (IComponentNamePO compName : toDelete) {
                    DataEventDispatcher.getInstance()
                            .fireDataChangedListener(compName,
                                    DataState.Deleted, UpdateState.all);
                }
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
            } catch (ProjectDeletedException e) {
                PMExceptionHandler.handleProjectDeletedException();
            }
        }
        
        return null;
    }

}
