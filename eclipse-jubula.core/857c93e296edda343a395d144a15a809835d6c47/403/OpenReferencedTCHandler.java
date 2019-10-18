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
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.rcp.businessprocess.UINodeBP;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author BREDEX GmbH
 * @created Jul 29, 2010
 */
public class OpenReferencedTCHandler extends AbstractOpenHandler {
    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        ISelection sel = HandlerUtil.getCurrentSelection(event);
        if (!(sel instanceof IStructuredSelection)) {
            return null;
        }
        openReferencedPlace((IStructuredSelection)sel);
        return null;
    }

    /**
     * Search for the SpecTC of an ExecTC and opens the corresponding editor if
     * possible.
     * 
     * @param structuredSel
     *            the current selection
     */
    private void openReferencedPlace(IStructuredSelection structuredSel) {
        Object firstElement = structuredSel.getFirstElement();
        if (firstElement instanceof TestResultNode) {
            TestResultNode trnode = (TestResultNode) firstElement;
            INodePO node = UINodeBP.getExecFromTestResultNode(trnode);
            if (node == null) {
                ErrorHandlingUtil
                        .createMessageDialog(MessageIDs.I_NON_EDITABLE_NODE);
            }
            INodePO parentNode = node.getParentNode();
            IEditorPart openEditorAndSelectNode =
                    openEditorAndSelectNode(parentNode, node);
            if (openEditorAndSelectNode == null) {
                ErrorHandlingUtil
                        .createMessageDialog(MessageIDs.I_NON_EDITABLE_NODE);
            }
        }
    }
}