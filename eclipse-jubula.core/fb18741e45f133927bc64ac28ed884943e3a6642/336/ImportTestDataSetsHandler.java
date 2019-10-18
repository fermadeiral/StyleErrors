/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jubula.client.ui.handlers.project.AbstractProjectHandler;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.editors.JBEditorHelper.EditableState;
import org.eclipse.jubula.client.ui.rcp.wizards.ImportTestDataSetsWizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created 08.11.2004
 */
public class ImportTestDataSetsHandler extends AbstractProjectHandler {
    
    /**
     * <code>m_ctde</code>
     */
    private CentralTestDataEditor m_ctde;
    
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        
        IWorkbenchWindow workbench = HandlerUtil.getActiveWorkbenchWindow(
                event);
        IWizard importTestDataSetsWizard = new ImportTestDataSetsWizard(
                workbench);
        WizardDialog dialog = new WizardDialog(getActiveShell(),
                importTestDataSetsWizard);
        dialog.setHelpAvailable(true);
        IEditorPart activeEditor = workbench.getActivePage().getActiveEditor();
        if (activeEditor instanceof CentralTestDataEditor) {
            m_ctde = (CentralTestDataEditor)activeEditor;
            if (m_ctde.getEditorHelper()
                    .requestEditableState() == EditableState.OK) {
                dialog.open();
            }
        }
        return null;
    }

    
}