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
package org.eclipse.jubula.client.ui.rcp.handlers.rename;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.rcp.views.TestCaseBrowser;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Clemens Fabig
 * @created 09.03.2006
 */
public class RenameTreeItemHandlerTCBrowser extends
        AbstractRenameTreeItemHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);

        if (part instanceof TestCaseBrowser) {
            dialogPopUp(getSelection());
        }
        return null;
    }
}