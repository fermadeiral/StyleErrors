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
package org.eclipse.jubula.client.alm.mylyn.ui.bridge.monitor;

import java.util.List;

import org.eclipse.jubula.client.alm.mylyn.ui.bridge.constants.ContentType;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.Plugin.ClientStatus;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.CentralTestDataEditor;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
import org.eclipse.jubula.client.ui.rcp.editors.PersistableEditorInput;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorPart;


/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class EditorInteractionMonitor extends AbstractEditorTracker {
    /**
     * <code>CTDE_ORIGIN_ID</code>
     */
    private static final String CTDE_ORIGIN_ID = "ctde"; //$NON-NLS-1$
    /**
     * <code>CDTE_HANDLE</code>
     */
    private static final String CDTE_HANDLE = "00000000000000000000000000000005"; //$NON-NLS-1$

    /** {@inheritDoc} */
    protected void editorBroughtToTop(IEditorPart part) {
        // not needed
    }

    /** {@inheritDoc} */
    protected void editorClosed(IEditorPart part) {
        if (Plugin.getDefault().getClientStatus() == ClientStatus.STOPPING
                || TasksUi.getTaskActivityManager().getActiveTask() == null) {
            return;
        }
        final IInteractionContext activeContext = ContextCore
                .getContextManager().getActiveContext();
        List<IInteractionElement> interesting = activeContext.getInteresting();
        if (part instanceof CentralTestDataEditor) {
            for (IInteractionElement element : interesting) {
                if (element.getContentType()
                        .equals(ContentType.CTD_EDITOR)) {
                    activeContext.delete(element);
                }
            }
        } else if (part instanceof ObjectMappingMultiPageEditor) {
            PersistableEditorInput input = 
                (PersistableEditorInput)((ObjectMappingMultiPageEditor)part)
                    .getEditorInput();
            IPersistentObject po = input.getNode();
            String id = ((IAUTMainPO)po).getGuid();
            for (IInteractionElement element : interesting) {
                if (element.getHandleIdentifier().equals(id)) {
                    activeContext.delete(element);
                }
            }
        } else if (part instanceof AbstractJBEditor) {
            AbstractContextStructureBridge editorBridge = ContextCore
                    .getStructureBridge(part);
            for (IInteractionElement element : interesting) {
                if (element.getHandleIdentifier().equals(
                        editorBridge.getHandleIdentifier(part))) {
                    activeContext.delete(element);
                }
            }
        }
    }

    /** {@inheritDoc} */
    protected void editorOpened(IEditorPart part) {
        if (TasksUi.getTaskActivityManager().getActiveTask() == null) {
            return;
        }
        InteractionEvent iEvent = null;
        if (part instanceof ObjectMappingMultiPageEditor) {
            iEvent = getInteractionEvent((ObjectMappingMultiPageEditor)part);
        } else if (part instanceof CentralTestDataEditor) {
            iEvent = getInteractionEvent((CentralTestDataEditor)part);
        } else if (part instanceof IJBEditor) {
            iEvent = getInteractionEvent((IJBEditor)part);
        }
        if (iEvent != null) {
            ContextCore.getContextManager().processInteractionEvent(iEvent);
        }
    }

    /**
     * @param jbEditor
     *            a jubula editor
     * @return the interaction event
     */
    private InteractionEvent getInteractionEvent(IJBEditor jbEditor) {
        String guid = null;
        AbstractContextStructureBridge bridge = ContextCore
                .getStructureBridge(jbEditor);
        IPersistentObject workingVersion = jbEditor.getEditorHelper()
                .getEditSupport().getWorkVersion();
        if (workingVersion instanceof INodePO) {
            guid = ((INodePO)workingVersion).getGuid();
            if (guid != null) {
                return new InteractionEvent(InteractionEvent.Kind.SELECTION,
                        bridge.getContentType(),
                        bridge.getHandleIdentifier(jbEditor), guid);
            }
        }
        return null;
    }

    /**
     * @param ctde
     *            the central test data editor
     * @return the interaction event
     */
    private InteractionEvent getInteractionEvent(CentralTestDataEditor ctde) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION,
                ContentType.CTD_EDITOR, CDTE_HANDLE, CTDE_ORIGIN_ID);
    }

    /**
     * @param ome
     *            the OME
     * @return the interaction event
     */
    private InteractionEvent getInteractionEvent(
            ObjectMappingMultiPageEditor ome) {
        PersistableEditorInput input = (PersistableEditorInput)(ome)
                .getEditorInput();
        IPersistentObject element = input.getNode();
        String id = ((IAUTMainPO)element).getGuid();
        return new InteractionEvent(InteractionEvent.Kind.SELECTION,
                ContentType.OM_EDITOR, id, id);
    }
}
