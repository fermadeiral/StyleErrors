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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestExecution.PauseMode;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionContributor;

/**
 * @author BREDEX GmbH
 * @created Feb 1, 2011
 */
public class ContinueTestSuiteWOEHHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) {
        TestExecutionContributor.getInstance().pauseTestSuiteAction(
                PauseMode.CONTINUE_WITHOUT_EH);
        return null;
    }
}
