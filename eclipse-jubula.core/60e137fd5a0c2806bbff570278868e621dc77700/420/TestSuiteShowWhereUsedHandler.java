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
package org.eclipse.jubula.client.ui.rcp.handlers.showwhereused;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowWhereUsedTestSuiteQuery;
import org.eclipse.search.ui.NewSearchUI;


/**
 * Class for test suite "Show where used" handler
 *
 * @author BREDEX GmbH
 * @created 17.10.2013
 */
public class TestSuiteShowWhereUsedHandler extends
        AbstractShowWhereUsedHandler {

    /** {@inheritDoc} */
    public Object execute(ExecutionEvent event) {
        init(event);
        final Object first = getCurrentSelection().getFirstElement();
        ITestSuitePO testSuite = (ITestSuitePO)first;
        NewSearchUI.runQueryInBackground(
                new ShowWhereUsedTestSuiteQuery(testSuite));
        return null;
    }
}
