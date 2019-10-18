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
package org.eclipse.jubula.client.ui.rcp.handlers.filter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.ui.rcp.handlers.filter.components.FilterReusedComponentNames;
import org.eclipse.jubula.client.ui.rcp.views.ComponentNameBrowser;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created 16.02.2009
 */
public class ReusedData extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart ap = HandlerUtil.getActivePart(event);
        
        if (ap instanceof ComponentNameBrowser) {
            ComponentNameBrowser cnb = (ComponentNameBrowser)ap;
            
            boolean filterAlreadyActive = false;
            
            for (ViewerFilter vf : cnb.getTreeViewer().getFilters()) {
                if (vf instanceof FilterReusedComponentNames) {
                    cnb.getTreeViewer().removeFilter(vf);
                    filterAlreadyActive = true;
                }
            }
            
            if (!filterAlreadyActive) {
                cnb.getTreeViewer().addFilter(new FilterReusedComponentNames());
            }
        }
        
        return null;
    }
}
