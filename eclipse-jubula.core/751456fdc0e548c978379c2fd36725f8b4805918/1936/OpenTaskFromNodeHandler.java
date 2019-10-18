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
package org.eclipse.jubula.client.alm.mylyn.ui.handler;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.propertytester.NodePropertyTester;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.mylyn.utils.MylynAccess;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * Open task for a selected node
 */
public class OpenTaskFromNodeHandler extends AbstractSelectionBasedHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IStructuredSelection selection = getSelection();
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (selection != null && project != null) {
            String almRepositoryName = project.getProjectProperties()
                    .getALMRepositoryName();
            TaskRepository repository = MylynAccess
                    .getRepositoryByLabel(almRepositoryName);
            if (repository != null) {
                Iterator it = selection.iterator();
                while (it.hasNext()) {
                    Object element = it.next();
                    if (element instanceof INodePO) {
                        INodePO node = (INodePO) element;
                        String taskId = NodePropertyTester
                                .getTaskIdforNode(node);
                        if (StringUtils.isNotEmpty(taskId)) {
                            TasksUiUtil.openTask(repository, taskId);
                        }
                    }
                }
            }
        }
        return null;
    }
}
