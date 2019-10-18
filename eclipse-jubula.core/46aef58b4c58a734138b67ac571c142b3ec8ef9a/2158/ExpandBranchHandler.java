/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.controllers.TreeViewContainerGUIController;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.views.IMultiTreeViewerContainer;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author BREDEX GmbH
 * @created 28.06.2017
 */
public class ExpandBranchHandler extends AbstractHandler {

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ExpandBranchHandler.class);

    /**
     * Since expanding the Branch can cost quite some time it is run in a
     * process
     */
    private class ExpandBranchRunnable implements IRunnableWithProgress {
        /** the event */
        private ExecutionEvent m_execEvent;

        /**
         * @param event the {@link ExecutionEvent} to get the {@link TreeViewer}
         *            from
         */
        public ExpandBranchRunnable(ExecutionEvent event) {
            m_execEvent = event;
        }
        
        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {
            monitor.beginTask(Messages.ExpandBranchMessage,
                    IProgressMonitor.UNKNOWN);
            IWorkbenchPart part = HandlerUtil.getActivePart(m_execEvent);
            TreeViewer activeTreeViewer = null;
            if (part instanceof IMultiTreeViewerContainer) {
                activeTreeViewer = ((IMultiTreeViewerContainer) part)
                        .getActiveTreeViewer();
            } else if (part instanceof ITreeViewerContainer) {
                activeTreeViewer =
                        ((ITreeViewerContainer) part).getTreeViewer();
            }
            expandTree(activeTreeViewer);
        }

        /**
         * executes the action in the ui Thread
         * 
         * @param activeTreeViewer the {@link TreeViewer} to expand
         */
        private void expandTree(final TreeViewer activeTreeViewer) {
            Display.getDefault().syncExec(new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                public void run() {
                    TreeViewContainerGUIController
                            .expandBranch(activeTreeViewer);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object execute(final ExecutionEvent event) {
        try {
            PlatformUI.getWorkbench().getProgressService().run(true, false,
                    new ExpandBranchRunnable(event));
        } catch (InvocationTargetException e) {
            log.error("Error during expanding tree", e); //$NON-NLS-1$
        } catch (InterruptedException e) {
            log.error("Error during expanding tree", e); //$NON-NLS-1$
        }

        return null;
    }

}
