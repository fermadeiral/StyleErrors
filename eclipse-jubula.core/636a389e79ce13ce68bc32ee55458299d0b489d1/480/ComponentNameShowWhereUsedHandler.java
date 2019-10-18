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
package org.eclipse.jubula.client.ui.rcp.handlers.showwhereused;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowWhereUsedComponentNameQuery;
import org.eclipse.search.ui.NewSearchUI;


/**
 * Class for Show Component Name usage Handler
 * 
 * @author BREDEX GmbH
 * @created 13.02.2009
 */
public class ComponentNameShowWhereUsedHandler extends
        AbstractShowWhereUsedHandler {

    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        init(event);
        final Object fe = getCurrentSelection().getFirstElement();
        
        if (fe instanceof IComponentNamePO) {
            NewSearchUI.runQueryInBackground(
                    new ShowWhereUsedComponentNameQuery((IComponentNamePO)fe));
        }
        return null;
    }
}
