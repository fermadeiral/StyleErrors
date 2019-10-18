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
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;


/**
 * Creates a new component name in the Component Name Browser
 * @author BREDEX GmbH
 * @created Mar 13, 2009
 */
public class NewComponentNameInViewHandler extends
        AbstractNewComponentNameHandler {

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        // Show dialog
        String newName = openDialog();
        
        try {
            if (newName != null) {
                String compType = ComponentBuilder.getInstance()
                        .getCompSystem().getMostAbstractComponent().getType();
                IComponentNamePO cNamePO = CompNameManager.getInstance().
                        createAndPersistCompNamePO(newName, compType,
                                CompNameCreationContext.OBJECT_MAPPING);
                DataEventDispatcher.getInstance().fireDataChangedListener(
                        cNamePO, DataState.Added, UpdateState.notInEditor);
            }
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        }
        
        return null;
    }

}
