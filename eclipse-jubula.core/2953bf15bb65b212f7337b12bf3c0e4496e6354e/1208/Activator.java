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
package org.eclipse.jubula.client.alm.mylyn.ui.bridge;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.alm.mylyn.ui.bridge.listener.TaskActivationListener;
import org.eclipse.jubula.client.alm.mylyn.ui.bridge.monitor.EditorInteractionMonitor;
import org.eclipse.jubula.client.alm.mylyn.ui.bridge.monitor.UserInteractionMonitor;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher;
import org.eclipse.jubula.client.core.events.InteractionEventDispatcher.IProgrammableSelectionListener;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin.ResourcesUiBridgeStartup;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class Activator extends AbstractUIPlugin {
    /**
     * <code>ID_PLUGIN</code>
     */
    public static final String ID_PLUGIN = "org.eclipse.jubula.client.alm.mylyn.ui.bridge"; //$NON-NLS-1$

    /**
     * the instance of this class
     */
    private static Activator instance;

    /**
     * the activation listener
     */
    private TaskActivationListener m_taskAktivationListener;

    /**
     * 
     */
    public Activator() {
        super();
    }

    /**
     * @param context
     *            -context
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        instance = this;
    }
    
    /**
     * @author BREDEX GmbH
     * @created Nov 10, 2010
     */
    public class ModifyInterest implements IProgrammableSelectionListener {
        /** {@inheritDoc} */
        public void processSelection(IStructuredSelection s) {
            for (Object interactionObject : s.toList()) {
                String originID = null;
                if (interactionObject instanceof INodePO) {
                    originID = ((INodePO)interactionObject).getGuid();
                } else if (interactionObject instanceof IReusedProjectPO) {
                    originID = ((IReusedProjectPO)interactionObject)
                            .getProjectGuid();
                }
                
                if (originID != null) {
                    AbstractContextStructureBridge bridge = ContextCore
                            .getStructureBridge(interactionObject);
                    InteractionEvent selectionEvent = new InteractionEvent(
                            InteractionEvent.Kind.SELECTION,
                            bridge.getContentType(),
                            bridge.getHandleIdentifier(interactionObject),
                            originID);
                    ContextCore.getContextManager().processInteractionEvent(
                            selectionEvent);
                }

            }
        }
    }

    /**
     * adds needed listeners
     */
    void lazyStart() {
        EditorInteractionMonitor interestEditorTracker;
        interestEditorTracker = new EditorInteractionMonitor();
        interestEditorTracker.install(PlatformUI.getWorkbench());

        InteractionEventDispatcher.getDefault()
                .addIProgrammableSelectionListener(new ModifyInterest());

        m_taskAktivationListener = new TaskActivationListener();
        TasksUi.getTaskActivityManager().addActivationListener(
                m_taskAktivationListener);

        DataEventDispatcher.getInstance().addProjectStateListener(
                m_taskAktivationListener);
        UserInteractionMonitor uIM = new UserInteractionMonitor();
        MonitorUi.getSelectionMonitors().add(uIM);
    }

    /**
     * @return this
     */
    public static Activator getDefault() {
        return instance;
    }

    /**
     * @author BREDEX GmbH
     * @created Nov 10, 2010
     */
    public static class UiBridgeStartup extends ResourcesUiBridgeStartup {
        /** {@inheritDoc} */
        public void lazyStartup() {
            Activator.getDefault().lazyStart();
            super.lazyStartup();
        }

    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception {
        DataEventDispatcher.getInstance().removeProjectStateListener(
                m_taskAktivationListener);
        super.stop(context);
    }
}
