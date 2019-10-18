/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.bridge.monitor;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class UserInteractionMonitor extends AbstractUserInteractionMonitor {
    /**
     * <code>m_oldSelection</code>
     */
    private StructuredSelection m_oldSelection = null;

    /**
     * Constructor
     */
    public UserInteractionMonitor() {
        super();
    }

    /** {@inheritDoc} */
    protected void handleWorkbenchPartSelection(IWorkbenchPart part,
            ISelection selection, boolean contributeToContext) {
        if (selection instanceof StructuredSelection) {
            StructuredSelection structuredSelection = 
                (StructuredSelection)selection;
            if (structuredSelection.equals(m_oldSelection)) {
                return;
            }
            m_oldSelection = structuredSelection;
            if (structuredSelection != null) {
                for (Iterator<?> iterator = structuredSelection.iterator(); 
                    iterator.hasNext();) {
                    Object selectedObject = iterator.next();
                    if (selectedObject instanceof INodePO
                            || selectedObject instanceof IReusedProjectPO) {
                        super.handleElementSelection(part, selectedObject,
                                contributeToContext);
                    }
                }
            }
        }
    }

}
