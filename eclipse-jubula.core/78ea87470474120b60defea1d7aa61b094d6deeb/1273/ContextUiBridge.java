/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.ui.bridge.bridge;

import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;


/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class ContextUiBridge extends AbstractContextUiBridge {
    /** {@inheritDoc} */
    @Override
    public boolean acceptsEditor(IEditorPart editorPart) {
        return editorPart instanceof AbstractTestCaseEditor;
    }

    /** {@inheritDoc} */
    @Override
    public void close(IInteractionElement element) {
        // currently empty
    }

    /** {@inheritDoc} */
    @Override
    public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getContentType() {
        return NodeStructureBridge.CONTENT_TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public IInteractionElement getElement(IEditorInput input) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Object getObjectForTextSelection(TextSelection selection,
            IEditorPart editor) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void open(IInteractionElement element) {
        // currently empty
    }
}
