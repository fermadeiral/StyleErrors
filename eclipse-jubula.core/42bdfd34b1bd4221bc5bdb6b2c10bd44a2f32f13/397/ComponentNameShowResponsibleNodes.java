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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowResponsibleNodeForComponentName;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchPart;


/**
 * Class for Show Component Name usage Handler
 * 
 * @author BREDEX GmbH
 * @created 13.02.2009
 */
public class ComponentNameShowResponsibleNodes extends AbstractJobHandler {

    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        init(event);
        final Object fe = getCurrentSelection().getFirstElement();
        final IWorkbenchPart part = getActivePart();
        if (fe instanceof IComponentNamePO) {
            ISearchQuery query;
            IComponentNamePO compName = (IComponentNamePO)fe;
            if (part instanceof ObjectMappingMultiPageEditor) {
                ObjectMappingMultiPageEditor editor = 
                    (ObjectMappingMultiPageEditor)part;
                query = new ShowResponsibleNodeForComponentName(
                        compName, editor.getAut());
                NewSearchUI.runQueryInBackground(query);
            }
        }
        return null;
    }
}
