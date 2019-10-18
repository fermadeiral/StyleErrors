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
package org.eclipse.jubula.client.ui.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * Abstract base class for implementations of the Paste action.
 *
 * @author BREDEX GmbH
 * @created 19.03.2008
 */
public abstract class AbstractPasteTreeItemAction extends Action {

    /**
     * Constructor.
     */
    public AbstractPasteTreeItemAction() {
        super();
        IWorkbenchAction pasteAction = ActionFactory.PASTE.create(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow());

        setText(pasteAction.getText());
        setToolTipText(pasteAction.getToolTipText());
        setImageDescriptor(pasteAction.getImageDescriptor());
        setDisabledImageDescriptor(pasteAction.getDisabledImageDescriptor());
        setId(pasteAction.getId());
        setActionDefinitionId(pasteAction.getActionDefinitionId());

        pasteAction.dispose();
    }
    
}
