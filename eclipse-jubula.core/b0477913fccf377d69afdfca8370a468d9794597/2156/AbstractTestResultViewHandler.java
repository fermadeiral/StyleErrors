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
package org.eclipse.jubula.client.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.ui.handlers.project.AbstractProjectHandler;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created Mar 3, 2011
 */
public abstract class AbstractTestResultViewHandler 
    extends AbstractProjectHandler {
    /**
     * @param event
     *            the execution event
     * @return the selected summary or null if no selection available
     */
    protected ITestResultSummaryPO getSelectedSummary(ExecutionEvent event) {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)selection;
            Object selectedObject = structuredSelection.getFirstElement();
            if (selectedObject instanceof ITestResultSummaryPO) {
                return (ITestResultSummaryPO)selectedObject;
            }
        }
        return null;
    }
}
