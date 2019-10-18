/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler which provides default capabilities for active context support
 */
public abstract class AbstractHandler 
    extends org.eclipse.core.commands.AbstractHandler {
    
    /** Log */
    static final Logger LOG = LoggerFactory.getLogger(AbstractHandler.class);
    
    /**
     * the active shell
     */
    private Shell m_activeShell;

    /**
     * @param shell set the shell to set for the active shell
     */
    private void setActiveShell(Shell shell) {
        m_activeShell = shell;
    }

    /**
     * @return the active shell when this handler was executed; may be
     *         <code>null</code>
     */
    protected Shell getActiveShell() {
        return m_activeShell;
    }
    
    /** {@inheritDoc}*/
    @SuppressWarnings("nls")
    public Object execute(ExecutionEvent event) throws ExecutionException {
        setActiveShell(HandlerUtil.getActiveShell(event));
        if (LOG.isDebugEnabled()) {
            StringBuilder str = new StringBuilder();
            str.append("\nCommand executed: ");
            str.append(event.getCommand().getId());
            str.append("\n");
            str.append("Parameters: ");
            str.append(event.getParameters().toString());
            str.append("\n");
            str.append("Handler class: ");
            str.append(this.getClass().getName());
            str.append("\n");
            ISelection sel = HandlerUtil.getCurrentSelection(event);
            if (sel != null && (sel instanceof IStructuredSelection)
                    && !(sel.isEmpty())) {
                IStructuredSelection struct = (IStructuredSelection) sel;
                str.append("The current selection is: ");
                str.append(struct.toString());
                str.append("\n");
            }
            str.append("Active part: ");
            str.append(HandlerUtil.getActivePartId(event));
            str.append("\n");
            LOG.debug(str.toString());
        }
        return executeImpl(event);
    }
    
    /**
     * Executes with the map of parameter values by name.
     * 
     * @param event
     *            An event containing all the information about the current
     *            state of the application; must not be <code>null</code>.
     * @throws ExecutionException
     *             if an exception occurred during execution.
     * @return an object as the execution result
     */
    protected abstract Object executeImpl(ExecutionEvent event)
        throws ExecutionException;
}
