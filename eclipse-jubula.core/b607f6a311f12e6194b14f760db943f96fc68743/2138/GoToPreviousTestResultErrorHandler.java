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
package org.eclipse.jubula.client.ui.handlers;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.ui.editors.TestResultViewer;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Handler for navigating to the "previous" error in a Test Result.
 * 
 * @author BREDEX GmbH
 * @created May 17, 2010
 */
public class GoToPreviousTestResultErrorHandler extends
        AbstractGoToTestResultErrorHandler {

    /**
     * {@inheritDoc}
     */
    protected boolean isForwardIteration() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    protected TreeViewer handleActiveWorkbenchParts(List<IWorkbenchPart> list) {
        list.get(1).setFocus();
        return ((TestResultViewer) list.get(1)).getTreeViewer();
    }
}
