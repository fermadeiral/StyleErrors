/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
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
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 */
public class NewViewInstanceHandler extends AbstractHandler {
    /**
     * the prefix for the secondary id
     */
    private static final String SECONDARY_ID_PREFIX = "instance_"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        IWorkbenchWindow activeWorkbenchWindow = HandlerUtil
                .getActiveWorkbenchWindow(event);

        try {
            activeWorkbenchWindow.getActivePage().showView(
                    Constants.TC_BROWSER_ID,
                    SECONDARY_ID_PREFIX + System.currentTimeMillis(),
                    IWorkbenchPage.VIEW_ACTIVATE);
        } catch (PartInitException e) {
            new ExecutionException(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
