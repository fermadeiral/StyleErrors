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

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.NodeEditorInput;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.ui.part.EditorPart;


/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class EditorContextStructureBridge 
    extends AbstractContextStructureBridge {
    /**
     * the editor content type
     */
    public static final String EDITOR_CONTENT_TYPE = "org.eclipse.jubula.client.alm.mylyn.ui.bridge.content.type.editor"; //$NON-NLS-1$

    /**
     * <code>EDITOR_ID_POSTFIX</code>
     */
    public static final String EDITOR_ID_POSTFIX = "editor"; //$NON-NLS-1$

    /**
     * <code>EDITOR_SEPERATOR</code>
     */
    private static final String EDITOR_SEPERATOR = ":"; //$NON-NLS-1$


    /** {@inheritDoc} */
    public boolean acceptsObject(Object object) {
        return object instanceof AbstractJBEditor;
    }

    /** {@inheritDoc} */
    public boolean canBeLandmark(String handle) {
        return false;
    }

    /** {@inheritDoc} */
    public boolean canFilter(Object element) {
        return false;
    }

    /** {@inheritDoc} */
    public List<String> getChildHandles(String handle) {
        return null;
    }

    /** {@inheritDoc} */
    public String getContentType() {
        return EDITOR_CONTENT_TYPE;
    }

    /** {@inheritDoc} */
    public String getContentType(String elementHandle) {
        return EDITOR_CONTENT_TYPE;
    }

    /** {@inheritDoc} */
    public String getHandleForOffsetInObject(Object resource, int offset) {
        return null;
    }

    /** {@inheritDoc} */
    public String getHandleIdentifier(Object object) {
        if (object instanceof AbstractJBEditor) {
            INodePO np = ((NodeEditorInput)((EditorPart)object)
                    .getEditorInput()).getNode();
            AbstractContextStructureBridge bridge = ContextCore
                    .getStructureBridge(NodeStructureBridge.CONTENT_TYPE);
            String handle = bridge.getHandleIdentifier(np) + EDITOR_SEPERATOR
                    + EDITOR_ID_POSTFIX;
            return handle;
        }
        return null;
    }

    /** {@inheritDoc} */
    public String getLabel(Object object) {
        return StringConstants.EMPTY;
    }

    /** {@inheritDoc} */
    public Object getObjectForHandle(String handle) {
        if (handle.endsWith(EDITOR_ID_POSTFIX)) {
            AbstractContextStructureBridge bridge = ContextCore
                    .getStructureBridge(NodeStructureBridge.CONTENT_TYPE);
            if (bridge != null) {
                String id = handle.split(EDITOR_SEPERATOR)[0];
                return bridge.getObjectForHandle(id);
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public String getParentHandle(String handle) {
        return null;
    }

    /** {@inheritDoc} */
    public boolean isDocument(String handle) {
        return false;
    }

}
