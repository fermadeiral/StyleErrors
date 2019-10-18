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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.handlers.AbstractGoToTestResultErrorHandler;
import org.eclipse.jubula.client.ui.rcp.views.TestResultTreeView;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Handler for navigating to the "next" error in a Test Result.
 * 
 * @author BREDEX GmbH
 * @created September 10, 2013
 */
public class GoToNextTestResultErrorHandler extends
        AbstractGoToTestResultErrorHandler {

    /**
     * {@inheritDoc}
     */
    protected boolean isForwardIteration() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected TreeViewer handleActiveWorkbenchParts(List<IWorkbenchPart> list) {
        list.get(0).setFocus();
        return ((TestResultTreeView) list.get(0)).getTreeViewer();
    }
}
