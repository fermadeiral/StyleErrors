/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
/**
 * context based view which is using a context for activation of handler
 * @author BREDEX GmbH
 */
public abstract class ContextBasedView extends ViewPart implements IJBPart {

    /**
     * context activation
     */
    private IContextActivation m_contextActivation;
    /**
     * the contextID used for{@link ContextService#activateContext(String)}
     */
    private String m_contextID;
    
    /**
     * The selectionListener listens for changes in the workbench's selection
     * service.
     */
    private ISelectionListener m_selectionListener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart part, 
                ISelection selection) {
            handleSelection(selection);
        }
    };
    
    /**
     * @param contextID the contextID used for{@link ContextService#activateContext(String)}
     */
    public ContextBasedView(String contextID) {
        super();
        m_contextID = contextID;
    }

    /**
     * @return the selection service
     */
    protected ISelectionService getSelectionService() {
        return getSite().getWorkbenchWindow().getSelectionService();
    }

    /**
     * set the status of the image context - does nothing if context service is
     * not available
     * 
     * @param active  the status to set
     */
    protected void setStatusOfContext(boolean active) {
        IContextService cs = getSite().getWorkbenchWindow().getService(
                IContextService.class);
        if (cs != null) {
            if (active) {
                m_contextActivation = cs.activateContext(m_contextID);
            } else {
                cs.deactivateContext(m_contextActivation);
            }
        }
    }
    /**
     * 
     * @param selection the changed selection
     */
    protected abstract void handleSelection(ISelection selection);
    
    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        getSelectionService().addSelectionListener(m_selectionListener);
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        getSelectionService().removeSelectionListener(m_selectionListener);
        super.dispose();
    }

}