/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.dialogs.EnterCommentDialog;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.validator.MaxStringLengthValidator;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.ui.PlatformUI;

/**
 * @author BREDEX GmbH
 */
public class EditNodeComment extends AbstractSelectionBasedHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        INodePO node = getFirstElement(INodePO.class);

        if (node != null) {
            final String origComment = node.getComment();

            EnterCommentDialog dialog = new EnterCommentDialog(
                    getActiveShell(), new MaxStringLengthValidator(),
                    origComment);
            dialog.setHelpAvailable(true);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(),
                    ContextHelpIds.ADD_COMMENT);
            int result = dialog.open();
            if (result != Window.OK) {
                return null;
            }
            String newComment = dialog.getCommentTitle();
            if (!StringUtils.equals(origComment, newComment)) {
                try {
                    NodePM.setComment(node, newComment);
                    DataEventDispatcher.getInstance().fireDataChangedListener(
                            node, DataState.Renamed, UpdateState.all);
                } catch (PMException e) {
                    PMExceptionHandler.handlePMExceptionForMasterSession(e);
                } catch (ProjectDeletedException e) {
                    PMExceptionHandler.handleProjectDeletedException();
                }
            }
        }
        
        return null;
    }
}
