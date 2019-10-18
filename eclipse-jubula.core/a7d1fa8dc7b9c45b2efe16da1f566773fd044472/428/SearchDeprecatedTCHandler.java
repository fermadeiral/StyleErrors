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
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.search.query.DeprecatedModulesQuery;
import org.eclipse.search.ui.NewSearchUI;

/**
 * Handler for searching for deprecated modules
 * @author BREDEX GmbH
 */
public class SearchDeprecatedTCHandler extends AbstractHandler {

     /**
      * {@inheritDoc}
      */
    @Override
    protected Object executeImpl(ExecutionEvent event)
            throws ExecutionException {
        DeprecatedModulesQuery query = new DeprecatedModulesQuery();
        NewSearchUI.runQueryInBackground(query);
        return null;
    }

}
