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

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.model.ICategoryPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.PMSaveException;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.InputDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 04.07.2005
 */
public class NewCategoryHandler extends AbstractNewHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        try {
            createNewCategory(event);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        }
        return null;
    }
    
    /**
     * @param event the execution event
     * @throws PMSaveException
     *             in case of DB storage problem
     * @throws PMAlreadyLockedException
     *             in case of locked catParentPO
     * @throws PMException
     *             in case of rollback failed
     * @throws ProjectDeletedException
     *             if the project was deleted in another instance
     */
    private void createNewCategory(ExecutionEvent event)
        throws PMSaveException, PMAlreadyLockedException, PMException, 
        ProjectDeletedException {

        final INodePO categoryParent = getParentNode(event);
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        InputDialog dialog = new InputDialog(
            getActiveShell(), 
            Messages.CreateNewCategoryActionCatTitle,
            InitialValueConstants.DEFAULT_CATEGORY_NAME,
            Messages.CreateNewCategoryActionCatMessage,
            Messages.CreateNewCategoryActionCatLabel,
            Messages.CreateNewCategoryActionCatError,
            Messages.CreateNewCategoryActionDoubleCatName,
            IconConstants.NEW_CAT_DIALOG_STRING,
            Messages.CreateNewCategoryActionNewCategory, false) {
            
            /**
             * @return False, if the input name already exists.
             */
            protected boolean isInputAllowed() {
                return !existCategory(categoryParent, getInputFieldText());
            }

        };
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(),
            ContextHelpIds.DIALOG_NEW_CATEGORY);
        dialog.open();
        if (Window.OK == dialog.getReturnCode()) {
            addCreatedNode(NodeMaker.createCategoryPO(dialog.getName()), event);
        }
        dialog.close();
    }

    /**
     * checks if a category exists in child nodes of given parent
     * @param node INodePO
     * @param name String
     * @return boolean
     */
    boolean existCategory(INodePO node, String name) {
        Iterator<? extends INodePO> iter = null;
        iter = node.getNodeListIterator();
        while (iter.hasNext()) {
            INodePO iterNode = iter.next();
            if (iterNode instanceof ICategoryPO
                    && iterNode.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}