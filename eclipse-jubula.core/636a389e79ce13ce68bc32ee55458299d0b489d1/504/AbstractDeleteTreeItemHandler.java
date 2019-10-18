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
package org.eclipse.jubula.client.ui.rcp.handlers.delete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;


/**
 * Superclass of all DeleteTreeItem Handlers
 *
 * @author BREDEX GmbH
 * @created 28.02.2006
 */
public abstract class AbstractDeleteTreeItemHandler 
    extends AbstractSelectionBasedHandler {

    /**
     * Pops up a "confirmDelete" dialog.
     * 
     * @param sel
     *            The actual selection.
     * @return True, if "yes" was clicked, false otherwise
     */
    protected boolean confirmDelete(IStructuredSelection sel) {
        List<String> itemNames = new ArrayList<String>();
        for (Object obj : sel.toList()) {
            if (obj instanceof INodePO) {
                itemNames.add(((INodePO)obj).getName());
            } else {
                String name = getName(obj);
                if (!StringUtils.isBlank(name)) {
                    itemNames.add(name);
                }
            }
        }

        return confirmDelete(itemNames);
    }

    /**
     * Pops up a "confirmDelete" dialog.
     * 
     * @param itemNames
     *            The names of the items to be deleted.
     * @return <code>true</code>, if "yes" was clicked, 
     *         <code>false</code> otherwise.
     */
    public boolean confirmDelete(Collection<String> itemNames) {
        String label = StringConstants.EMPTY;
        if (itemNames.size() == 1) {
            label = NLS.bind(Messages.DeleteTreeItemActionDeleteOneItem,
                itemNames.iterator().next());
        } else if (itemNames.size() == 0) {
            return false;  
        } else {
            label = NLS.bind(Messages.DeleteTreeItemActionDeleteMultipleItems,
                itemNames.size());
        }
        MessageDialog dialog = new MessageDialog(getActiveShell(), 
                Messages.DeleteTreeItemActionShellTitle,
            null, 
            label, MessageDialog.QUESTION, new String[] {
                Messages.DialogMessageButton_YES,
                Messages.DialogMessageButton_NO }, 0);
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.open();
        return dialog.getReturnCode() == 0;
    }

    /**
     * Closes the editor for the given Node
     * @param node the node of the editor to be closed.
     */
    protected void closeOpenEditor(IPersistentObject node) {
        IEditorPart editor = Utils.getEditorByPO(node);
        if (editor != null) {
            editor.getSite().getPage().closeEditor(editor, false);
        }
    }
    
    /**
     * Subclasses may override to provide name for given object
     * 
     * @param obj
     *            the object to get the name for
     * @return may return "null" if no name available; otherwise the name to
     *         display for delete operation
     */
    protected String getName(Object obj) {
        return String.valueOf(obj);
    }
}