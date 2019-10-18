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
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowWhereUsedTestDataCubeQuery;
import org.eclipse.search.ui.NewSearchUI;


/**
 * @author BREDEX GmbH
 * @created Jul 21, 2010
 */
public class TestDataCubeShowWhereUsedHandler extends
        AbstractShowWhereUsedHandler {

    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        init(event);
        
        final Object fe = getCurrentSelection().getFirstElement();
        if (fe instanceof ITestDataCubePO) {
            ITestDataCubePO tdc = (ITestDataCubePO)fe;
            NewSearchUI.runQueryInBackground(
                    new ShowWhereUsedTestDataCubeQuery(tdc));
        }
        return null;
    }
}
