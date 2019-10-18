/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
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
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowWhereReferencedCTDSValueQuery;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowWhereUsedTestDataCubeQuery;
import org.eclipse.jubula.client.ui.rcp.views.dataset.DataSetView;
import org.eclipse.jubula.client.ui.rcp.views.dataset.TestDataCubeDataSetPage;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.IPage;

/**
 * Handler used to show where a given CTDS data cell is referenced from
 *    through the ?getCentralTestData function
 * @author BREDEX GmbH
 *
 */
public class ShowWhereUsedCTDSHandler extends AbstractHandler {

    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event) {
        IViewPart part = Plugin.getActiveView();
        if (!(part instanceof DataSetView)) {
            return null;
        }
        IPage page = ((DataSetView) part).getCurrentPage();
        if (!(page instanceof TestDataCubeDataSetPage)) {
            return null;
        }
        TestDataCubeDataSetPage dsPage = (TestDataCubeDataSetPage) page;
        IParameterInterfacePO param = dsPage.getParamInterfaceObj();
        int row = dsPage.getCurrentRow();
        int col = dsPage.getCurrentCol();
        if (row < 0 || col < 1) {
            // The first column is the row number
            // instead search for the complete reuse of the CTDS
            if (param instanceof ITestDataCubePO) {
                NewSearchUI.runQueryInBackground(
                        new ShowWhereUsedTestDataCubeQuery(
                                (ITestDataCubePO) param));
            }
            return null;
        }
        NewSearchUI.runQueryInBackground(new ShowWhereReferencedCTDSValueQuery(
                param,  row,  col - 1));
        return null;
    }

}
