/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.jface.action.Action;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.SharedImages;
import org.eclipse.ui.views.properties.PropertySheetEntry;

/**
 * Handler for copying the value of a property in the properties view
 * 
 * @author BREDEX GmbH
 * @created 27.06.2016
 */
public class CopyPropertyValueHandler extends Action {

    /** The system clipboard */
    private Clipboard m_clipboard;
    
    /** The tree control */
    private Control m_control;
    
    /**
     * The constructor
     * @param control the tree control
     * @param clipboard the system clipboard
     */
    public CopyPropertyValueHandler(Control control, Clipboard clipboard) {
        super(Messages.PropertyCopyValue, new SharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
        m_clipboard = clipboard;
        m_control = control;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (m_control instanceof Tree) {
            Tree tree = (Tree) m_control;
            
            if (tree.getSelectionCount()  == 1) {
                Object[] values = ((PropertySheetEntry) tree.getSelection()[0]
                        .getData()).getValues();
                
                if (values.length == 1) {
                    m_clipboard.setContents(new Object[] { values[0] }, 
                            new Transfer[] { TextTransfer.getInstance() });
                }
            }
        }
    }
}
