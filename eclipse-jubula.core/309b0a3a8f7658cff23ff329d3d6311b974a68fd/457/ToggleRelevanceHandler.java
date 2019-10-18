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

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.TestresultSummaryBP;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.ui.handlers.AbstractTestResultViewHandler;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created Mar 3, 2011
 */
public class ToggleRelevanceHandler extends AbstractTestResultViewHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection =
                (IStructuredSelection)selection;
            for (Iterator iterator = structuredSelection.iterator(); 
                    iterator.hasNext();) {
                Object selectedObject = iterator.next();
                if (selectedObject instanceof ITestResultSummaryPO) {
                    ITestResultSummaryPO selectedSummary =
                            (ITestResultSummaryPO)selectedObject;
                    TestresultSummaryBP.getInstance().setRelevance(
                            selectedSummary,
                            !selectedSummary.isTestsuiteRelevant());
                }
            }
        }
        return null;
    }
}
