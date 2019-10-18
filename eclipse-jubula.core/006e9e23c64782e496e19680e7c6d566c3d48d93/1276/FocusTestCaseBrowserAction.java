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
package org.eclipse.jubula.client.alm.mylyn.ui.bridge.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jubula.client.ui.views.ITreeViewerContainer;
import org.eclipse.mylyn.context.ui.AbstractAutoFocusViewAction;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.ui.IViewPart;


/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class FocusTestCaseBrowserAction extends AbstractAutoFocusViewAction {
    /**
     * Constructor
     */
    public FocusTestCaseBrowserAction() {
        super(new InterestFilter(), true, true, true);
    }

    /**
     * @param interestFilter
     *            - interestFilter
     * @param manageViewer
     *            - manageViewer
     * @param manageFilters
     *            - manageFilters
     * @param manageLinking
     *            - manageLinking
     */
    public FocusTestCaseBrowserAction(InterestFilter interestFilter,
        boolean manageViewer, boolean manageFilters, boolean manageLinking) {
        super(interestFilter, manageViewer, manageFilters, manageLinking);
    }

    /** {@inheritDoc} */
    public List<StructuredViewer> getViewers() {
        List<StructuredViewer> viewers = new ArrayList<StructuredViewer>();
        IViewPart part = super.getPartForAction();
        if (part instanceof ITreeViewerContainer) {
            viewers.add(((ITreeViewerContainer) part).getTreeViewer());
        }
        return viewers;
    }
}
