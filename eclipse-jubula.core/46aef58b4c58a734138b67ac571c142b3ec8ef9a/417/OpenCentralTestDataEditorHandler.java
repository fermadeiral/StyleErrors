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
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.ui.IEditorPart;


/**
 * @author BREDEX GmbH
 * @created Jun 28, 2010
 */
public class OpenCentralTestDataEditorHandler extends AbstractOpenHandler {
    /**
     * {@inheritDoc}
     */
    protected boolean isEditableImpl(INodePO selected) {
        return (selected instanceof IProjectPO);
    }

    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            ITestDataCategoryPO centralTestData = project.getTestDataCubeCont();
            if (centralTestData != null) {
                IEditorPart editor = openEditor(centralTestData);
                if (editor != null) {
                    editor.getSite().getPage().activate(editor);
                }
            }
        }
        return null;
    }
}
