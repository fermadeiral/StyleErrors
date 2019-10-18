/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.IEditorOperation;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.wizards.refactor.ReplaceTCRWizard;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.DialogUtils.SizeType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Markus Tiede
 * @created Jul 20, 2011
 */
public class ReplaceWithTestCaseHandler extends AbstractSelectionBasedHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        final AbstractTestCaseEditor tce = 
            (AbstractTestCaseEditor)HandlerUtil.getActiveEditor(event);
        tce.getEditorHelper().doEditorOperation(new IEditorOperation() {
            public void run(IPersistentObject workingPo) {
                List<INodePO> listOfExecsToReplace = getSelection().toList();
                WizardDialog dialog = new WizardDialog(getActiveShell(),
                        new ReplaceTCRWizard(tce, listOfExecsToReplace)) {
                    /** {@inheritDoc} */
                    protected void configureShell(Shell newShell) {
                        super.configureShell(newShell);
                        DialogUtils.adjustShellSizeRelativeToClientSize(
                                newShell, .6f, .6f, SizeType.SIZE);
                    }
                };
                dialog.setHelpAvailable(true);
                dialog.open();
            }
        });
 
        return null;
    }
}
