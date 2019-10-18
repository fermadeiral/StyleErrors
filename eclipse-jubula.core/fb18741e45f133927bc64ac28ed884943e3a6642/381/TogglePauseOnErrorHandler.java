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
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;


/**
 * @author BREDEX GmbH
 * @created May 17, 2010
 */
public class TogglePauseOnErrorHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final Display display = HandlerUtil.getActiveShellChecked(event)
                .getDisplay();
        final Command command = event.getCommand();
        final IClientTest ct = ClientTest.instance();
        display.syncExec(new Runnable() {
            public void run() {
                State state = 
                    command.getState(RegistryToggleState.STATE_ID);
                state.setValue(!ct.isPauseTestExecutionOnError());
            }
        });
        ct.pauseTestExecutionOnError(!ct.isPauseTestExecutionOnError());
        return null;
    }

}
