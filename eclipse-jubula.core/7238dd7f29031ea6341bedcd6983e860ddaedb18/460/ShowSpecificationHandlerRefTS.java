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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.UINodeBP;
import org.eclipse.jubula.client.ui.rcp.views.AbstractJBTreeView;
import org.eclipse.jubula.client.ui.rcp.views.TestSuiteBrowser;
import org.eclipse.ui.IViewPart;


/**
 * @author BREDEX GmbH
 * @created Aug 11, 2010
 */
public class ShowSpecificationHandlerRefTS extends
        AbstractShowSpecificationHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IStructuredSelection iss = getSelection();
        Object firstElement = iss.getFirstElement();
        if (firstElement instanceof IRefTestSuitePO) {
            IRefTestSuitePO refTS = (IRefTestSuitePO)firstElement;
            ITestSuitePO testSuite = refTS.getTestSuite();
            IViewPart activatedView = 
                Plugin.showView(Constants.TS_BROWSER_ID);
            if (activatedView instanceof TestSuiteBrowser) {
                AbstractJBTreeView jbtv = (TestSuiteBrowser)activatedView;
                UINodeBP.selectNodeInTree(testSuite.getId(),
                        jbtv.getTreeViewer(), jbtv.getEntityManager());
            }
        }
        return null;
    }
}