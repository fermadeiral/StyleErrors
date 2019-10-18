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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping;

import org.apache.commons.lang.Validate;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

/**
 * Drag source listener for unmapped Component Names.
 *
 * @author BREDEX GmbH
 * @created Aug 19, 2010
 */
public class LimitingDragSourceListener extends DragSourceAdapter {
    
    /** provides the selections that will be "dragged" */
    private ISelectionProvider m_selectionProvider;

    /** the limiter for DnD operations */
    private Object m_token;

    /**
     * Constructor
     * 
     * @param selectionProvider Provides the selections that will be "dragged". 
     *                          Must not be <code>null</code>
     * @param token The limiter for DnD operations.
     */
    public LimitingDragSourceListener(ISelectionProvider selectionProvider, 
            Object token) {
        Validate.notNull(selectionProvider);
        m_selectionProvider = selectionProvider;
        m_token = token;
    }
    
    /**
     * {@inheritDoc}
     */
    public void dragStart(DragSourceEvent event) {
        ObjectMappingTransferHelper.setDndToken(m_token);
        LocalSelectionTransfer.getTransfer().setSelection(
                m_selectionProvider.getSelection());
        LocalSelectionTransfer.getTransfer().setSelectionSetTime(
                event.time & 0xFFFFFFFFL);
    }

    /**
     * {@inheritDoc}
     */
    public void dragFinished(DragSourceEvent event) {
        ObjectMappingTransferHelper.setDndToken(null);
        LocalSelectionTransfer.getTransfer().setSelection(null);
        LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
    }
}
