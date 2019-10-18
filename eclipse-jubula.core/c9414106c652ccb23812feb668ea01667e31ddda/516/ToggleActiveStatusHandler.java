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
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created May 11, 2010
 */
public class ToggleActiveStatusHandler extends AbstractSelectionBasedHandler {

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);

        if (activePart instanceof AbstractJBEditor) {
            AbstractJBEditor tce = (AbstractJBEditor)activePart;
            final JBEditorHelper editorHelper = tce.getEditorHelper();
            if (editorHelper.requestEditableState() 
                    != JBEditorHelper.EditableState.OK) {
                return null;
            }
            for (Iterator<INodePO> it = getSelection().iterator(); it
                    .hasNext();) {
                INodePO node = it.next();
                node.setActive(!node.isActive());
            }
            editorHelper.setDirty(true);
            DataEventDispatcher.getInstance().firePropertyChanged(false);
        }
        return null;
    }
}
