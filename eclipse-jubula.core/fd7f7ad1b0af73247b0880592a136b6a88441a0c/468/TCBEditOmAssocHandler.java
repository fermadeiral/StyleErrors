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

import java.util.Collection;

import javax.persistence.EntityManager;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.locking.LockManager;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.AssocOMtoSpecTCDialog;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;

/**
 * @author BREDEX GmbH
 *
 */
public class TCBEditOmAssocHandler extends AbstractSelectionBasedHandler {

    /**
     * {@inheritDoc}
     */
    protected Object executeImpl(ExecutionEvent event)
            throws ExecutionException {
        final ISpecTestCasePO firstElement =
                getFirstElement(ISpecTestCasePO.class);
        if (firstElement != null) {
            EntityManager masterSession =
                    GeneralStorage.getInstance().getMasterSession();
            try {
                Persistor.instance().lockPO(masterSession, firstElement);
            } catch (PMException e) {
                PMExceptionHandler.handlePMExceptionForMasterSession(e);
                return null;
            }
            AssocOMtoSpecTCDialog dialog =
                    new AssocOMtoSpecTCDialog(getActiveShell(), firstElement);
            dialog.create();
            int result = dialog.open();
            if (result != Window.OK) {
                LockManager.instance().unlockPO(firstElement);
                return null;
            }
            persistChanges(firstElement, dialog.getSelectedItems());
        }
        LockManager.instance().unlockPO(firstElement);

        return null;
    }

    /**
     * @param firstElement the {@link ISpecTestCasePO} to associate the {@link IObjectMappingCategoryPO} with
     * @param selectedItems the selected {@link IObjectMappingCategoryPO}
     */
    private void persistChanges(ISpecTestCasePO firstElement,
            Collection<IObjectMappingCategoryPO> selectedItems) {
        try {
            LockManager.instance().unlockPO(firstElement);
            NodePM.setOMAssoc(firstElement, selectedItems);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                    firstElement, DataState.StructureModified, UpdateState.all);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForEditor(e, null);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        }
    }
}
