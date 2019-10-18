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
package org.eclipse.jubula.client.ui.rcp.handlers.switchto;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Sep 3, 2010
 */
public abstract class AbstractSwitchToHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        Plugin.showView(getViewIDToSwitchTo());
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        executeSetFocus(activePart);
        return null;
    }

    /**
     * @return the view it to switch to
     */
    protected abstract String getViewIDToSwitchTo();
    
    /**
     * @param activePart the part which has been made active
     * set the focus in the view to show; subclasses may override
     */
    protected void executeSetFocus(IWorkbenchPart activePart) {
        // empty default implementation
    }
}
