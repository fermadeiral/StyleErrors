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
package org.eclipse.jubula.client.inspector.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handles copying the names for an entire hierarchy to the clipboard.
 *
 * @author BREDEX GmbH
 * @created Jun 12, 2009
 */
public class CopyHierarchyHandler extends AbstractHandler {

    /** the separator for test specification paths */
    private static final String PATH_SEPARATOR = StringConstants.SLASH;
    
    /**
     * 
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structSel = (IStructuredSelection)selection;
            Object firstElement = structSel.getFirstElement();
            if (firstElement instanceof TreeNode) {
                StringBuilder sb = new StringBuilder();
                TreeNode node = (TreeNode)firstElement;
                while (node != null) {
                    String nodeString = getString(node);
                    if (nodeString != null) {
                        sb.insert(0, nodeString);
                        sb.insert(0, PATH_SEPARATOR);
                    }
                    node = node.getParent();
                }

                sb.delete(0, PATH_SEPARATOR.length());
                
                if (sb.length() > 0) {
                    Clipboard cb = new Clipboard(
                            HandlerUtil.getActiveShellChecked(event)
                            .getDisplay());
                    cb.setContents(
                            new String [] {sb.toString()}, 
                            new Transfer[] {TextTransfer.getInstance()});
                    cb.dispose();
                }
            }
        }
        
        return null;
    }

    /**
     * @param node The tree node for which to get the string.
     * @return the string representation of the given tree node.
     */
    private String getString(TreeNode node) {
        Object value = node.getValue();
        if (value instanceof String []) {
            String [] strArray = (String [])value;
            if (strArray.length > 0) {
                return strArray[0];
            }
        }
            
        return null;
    }

}
