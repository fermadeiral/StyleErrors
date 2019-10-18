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
package org.eclipse.jubula.client.ui.rcp.handlers.sorter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.rcp.sorter.ComponentNameNameViewerSorter;
import org.eclipse.jubula.client.ui.rcp.views.ComponentNameBrowser;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;



/**
 * @author BREDEX GmbH
 * @created 11.02.2009
 */
public class SortByNameHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        IWorkbenchPart wp = HandlerUtil.getActivePart(event);
        
        if (wp instanceof ComponentNameBrowser) {
            ComponentNameBrowser cnb = (ComponentNameBrowser)wp;
            cnb.getTreeViewer().setComparator(
                    new ComponentNameNameViewerSorter());
        }
        
        return null;
    }

}
