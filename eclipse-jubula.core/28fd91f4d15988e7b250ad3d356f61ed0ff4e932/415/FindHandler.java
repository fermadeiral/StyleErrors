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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.FindDialog;
import org.eclipse.jubula.client.ui.rcp.views.TestResultTreeView;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 */
public class FindHandler extends AbstractHandler {
    /** the dialog to rename an selected item */
    private FindDialog<?> m_dialog;

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart part = HandlerUtil.getActivePart(event);
        if (part instanceof ITreeViewerContainer) {
            if (m_dialog == null || m_dialog.isDisposed()) {
                if (part instanceof TestResultTreeView) {
                    m_dialog = new FindDialog<TestResultNode>(
                            getActiveShell(), (ITreeViewerContainer) part);
                } else {
                    m_dialog = new FindDialog<INodePO>(getActiveShell(),
                            (ITreeViewerContainer) part);
                }
            }
            m_dialog.open();
        }
        return null;
    }
}