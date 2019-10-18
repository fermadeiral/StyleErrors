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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.alm.mylyn.ui.mapping.TestResultNodeTaskMapping;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * Add Task Handler
 */
public class AddTaskHandler extends AbstractSelectionBasedHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        Object selectedElement = getSelection().getFirstElement();
        if (selectedElement instanceof TestResultNode) {
            final TestResultNode node = (TestResultNode) selectedElement;
            TasksUiUtil.openNewTaskEditor(getActiveShell(),
                    new TestResultNodeTaskMapping(node), null);
        }
        return null;
    }
}
