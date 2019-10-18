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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Abstract Class for Handlers
 * 
 * Handler may use Jobs to perform the work in background.
 * 
 * @author BREDEX GmbH
 * @created 16.02.2009
 */
public abstract class AbstractJobHandler extends AbstractHandler {
    /** the active part */
    private IWorkbenchPart m_activePart;

    /** the current selection */
    private IStructuredSelection m_currentSelection;

    /**
     * init active part and current selection
     * 
     * @param event
     *            the event to check the environment for
     */
    protected void init(ExecutionEvent event) {
        setActivePart(HandlerUtil.getActivePart(event));

        IStructuredSelection structuredSelection = new StructuredSelection();
        ISelection sel = HandlerUtil.getCurrentSelection(event);

        if (sel instanceof IStructuredSelection) {
            setCurrentSelection((IStructuredSelection)sel);
        } else {
            setCurrentSelection(structuredSelection);
        }
    }

    /**
     * @return the activePart
     */
    public IWorkbenchPart getActivePart() {
        return m_activePart;
    }

    /**
     * @param activePart
     *            the activePart to set
     */
    private void setActivePart(IWorkbenchPart activePart) {
        m_activePart = activePart;
    }

    /**
     * @return the currentSelection
     */
    public IStructuredSelection getCurrentSelection() {
        return m_currentSelection;
    }

    /**
     * @param currentSelection
     *            the currentSelection to set
     */
    private void setCurrentSelection(IStructuredSelection currentSelection) {
        m_currentSelection = currentSelection;
    }
}
