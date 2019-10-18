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
package org.eclipse.jubula.rc.rcp.e3.gef.listener;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.part.MultiPageEditorPart;


/**
 * @author BREDEX GmbH
 * @created Dec 17, 2009
 */
public class GefPartListener implements IPartListener2 {

    /** Key for GEF Viewer in component data */
    public static final String TEST_GEF_VIEWER_DATA_KEY = "TEST_GEF_VIEWER"; //$NON-NLS-1$

    /**
     *
     * {@inheritDoc}
     */
    public void partActivated(IWorkbenchPartReference partRef) {
        // Do nothing
    }

    /**
     *
     * {@inheritDoc}
     */
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        // Do nothing
    }

    /**
     *
     * {@inheritDoc}
     */
    public void partClosed(IWorkbenchPartReference partRef) {
        // Do nothing
    }

    /**
     *
     * {@inheritDoc}
     */
    public void partDeactivated(IWorkbenchPartReference partRef) {
        // Do nothing
    }

    /**
     *
     * {@inheritDoc}
     */
    public void partHidden(IWorkbenchPartReference partRef) {
        // Do nothing
    }

    /**
     *
     * {@inheritDoc}
     */
    public void partInputChanged(IWorkbenchPartReference partRef) {
        // Do nothing
    }

    /**
     *
     * {@inheritDoc}
     */
    public void partOpened(final IWorkbenchPartReference partRef) {
        IWorkbenchPart part = partRef.getPart(false);

        if (part instanceof MultiPageEditorPart) {
            ((MultiPageEditorPart)part).getSite().getSelectionProvider()
                .addSelectionChangedListener(new ISelectionChangedListener() {

                    public void selectionChanged(SelectionChangedEvent event) {
                        if (event.getSource() instanceof GraphicalViewer) {
                            registerGraphicalViewer(
                                (GraphicalViewer)event.getSource(), partRef);
                        }
                    }
                });
        }

        if (part != null
                && partRef instanceof WorkbenchPartReference) {
            GraphicalViewer viewer = part.getAdapter(
                        GraphicalViewer.class);
            registerGraphicalViewer(viewer, partRef);
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    public void partVisible(IWorkbenchPartReference partRef) {
        partOpened(partRef);
    }

    /**
     * Registers a {@link GraphicalViewer} with the given
     * {@link IWorkbenchPartReference}.
     *
     * @param viewer The viewer to register.
     * @param partRef The reference to the part that (indirectly) contains the
     *                viewer.
     */
    private void registerGraphicalViewer(GraphicalViewer viewer,
            IWorkbenchPartReference partRef) {

        IWorkbenchPart part = partRef.getPart(false);
        if (part != null && partRef instanceof WorkbenchPartReference) {
            if (viewer != null) {
                // Note the viewer on the component
                Control partContent =
                    ((WorkbenchPartReference)partRef).getPane().getControl();

                if (partContent != null && !partContent.isDisposed()) {
                    partContent.setData(
                            GefPartListener.TEST_GEF_VIEWER_DATA_KEY,
                            viewer);
                }
            }
        }
    }
}
