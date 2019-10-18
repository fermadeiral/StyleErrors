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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Base class for all selection based handlers
 */
public abstract class AbstractSelectionBasedHandler extends AbstractHandler {
    /**
     * the selection
     */
    private IStructuredSelection m_selection;

    /**
     * @param ss the selection
     */
    private void setSelection(IStructuredSelection ss) {
        m_selection = ss;
    }
    
    /**
     * @return the selection; may not be <code>null</code>
     */
    protected IStructuredSelection getSelection() {
        return m_selection;
    }
    
    /**
     * @param type The expected type of object.
     * @param <T> The expected type of the object. 
     * @return the first element in the current selection, if 
     *         the first element is an instance of <code>type</code>. 
     *         Otherwise, returns <code>null</code>.
     */
    public <T> T getFirstElement(Class<T> type) {
        Object firstElement = getSelection().getFirstElement();
        if (type.isInstance(firstElement)) {
            return type.cast(firstElement);
        }
        
        return null;
    }
    
    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            setSelection((IStructuredSelection) selection);
            return super.execute(event);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     * 
     * !!! This method will not be called if no structured selection is
     * available !!!
     */
    protected abstract Object executeImpl(ExecutionEvent event)
        throws ExecutionException;
}
