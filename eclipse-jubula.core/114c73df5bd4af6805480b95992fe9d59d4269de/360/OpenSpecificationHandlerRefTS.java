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
package org.eclipse.jubula.client.ui.rcp.handlers.open;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jul 29, 2010
 */
public class OpenSpecificationHandlerRefTS extends AbstractOpenHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (sel instanceof IStructuredSelection) {
            IStructuredSelection iss = (IStructuredSelection)sel;
            Object firstElement = iss.getFirstElement();
            if (firstElement instanceof IRefTestSuitePO) {
                IRefTestSuitePO refTS = (IRefTestSuitePO)firstElement;
                ITestSuitePO testSuite = refTS.getTestSuite();
                openEditor(testSuite);
                InteractionEventDispatcher.getDefault().
                    fireProgammableSelectionEvent(
                            new StructuredSelection(testSuite));
            }
        }
        return null;
    }
}