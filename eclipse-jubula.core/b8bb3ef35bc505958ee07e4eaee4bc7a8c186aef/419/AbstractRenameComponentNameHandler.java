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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.RenameLogicalCompNameDialog;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.ui.handlers.HandlerUtil;



/**
 * Abstract handler for renaming Component Names.
 *
 * @author BREDEX GmbH
 * @created Mar 5, 2009
 */
public abstract class AbstractRenameComponentNameHandler extends
        AbstractSelectionBasedHandler {

    /**
     * @return the currently selected Component Name, or <code>null</code> if 
     *         no Component Name is currently selected.
     */
    protected final IComponentNamePO getSelectedComponentName() {
        IStructuredSelection structuredSelection = getSelection();
        Object selectedObject = structuredSelection.getFirstElement();
        if (selectedObject instanceof IComponentNamePO) {
            return (IComponentNamePO)selectedObject;
        }
        return null;
    }
    
    /**
     * 
     * @param event
     *            An event containing all the information about the current
     *            state of the application; must not be <code>null</code>.
     * @param compName The Component Name to rename.
     * @return the new name for the given Component Name, or <code>null</code>
     *         if the rename operation should not occur.
     */
    protected final String getNewName(ExecutionEvent event,
            IComponentNamePO compName) {
        final String originalName = compName.getName();
        
        RenameLogicalCompNameDialog dialog = new RenameLogicalCompNameDialog(
                HandlerUtil.getActiveShell(event), originalName);
        dialog.setHelpAvailable(true);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        Plugin.getHelpSystem().setHelp(dialog.getShell(), 
                ContextHelpIds.RENAME_COMPONENT_NAME);
        int result = dialog.open();
        if (result != Window.OK) {
            return null;
        }
        String newName = dialog.getName();
        return newName;
    }

    /**
     * Performs the rename operation.
     * 
     * @param compNameCache Used for finding and resolving component names.
     * @param guid The GUID of the component name to update.
     * @param newName The new name for the component name.
     */
    protected final void rename(IWritableComponentNameCache compNameCache, 
            String guid, String newName) {
        
        compNameCache.renameComponentName(guid, newName);
    }

}
