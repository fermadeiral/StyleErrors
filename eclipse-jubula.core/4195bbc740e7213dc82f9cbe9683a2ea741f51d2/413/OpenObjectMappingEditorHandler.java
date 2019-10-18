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
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

 
/**
 * @author BREDEX GmbH
 * @created 14.04.2005
 */
public class OpenObjectMappingEditorHandler extends AbstractOpenHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = 
                (IStructuredSelection)selection;
            Object firstSelElement = 
                structuredSelection.getFirstElement();
            if (firstSelElement instanceof ITestSuitePO) {
                IEditorPart editor = null;
                ITestSuitePO suite = (ITestSuitePO)firstSelElement;
                if (suite != null && suite.getAut() != null) {
                    IAUTMainPO aut = suite.getAut();
                    editor = openEditor(aut);
                }
                if (editor != null) {
                    editor.getSite().getPage().activate(editor);
                }
            }
        }
        return null;
    }
    
    /** {@inheritDoc} */
    protected boolean isEditableImpl(INodePO selected) {
        return (selected instanceof ITestSuitePO);
    }
}
