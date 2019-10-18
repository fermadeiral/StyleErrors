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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.handlers.project.AbstractProjectHandler;
import org.eclipse.jubula.client.ui.views.TestresultSummaryView;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for refreshing testresults in testresult summary view
 * 
 * @author BREDEX GmbH
 * @created Feb 4, 2010
 */
public class RefreshTestresultsHandler extends AbstractProjectHandler {
    /**
     * {@inheritDoc}
     */
    public Object executeImpl(ExecutionEvent event) {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof TestresultSummaryView) {
            final TestresultSummaryView summary = 
                (TestresultSummaryView)activePart;
            summary.loadViewInput();
        }
        return null;
    }
}
