/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ConnectAutAgentBP;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionGUIController;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager.AutAgent;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/**
 * @created 02.03.2012
 */
public class AUTAgentConnectHandler extends AbstractHandler 
    implements IElementUpdater {
    /** ID of command parameter for AUT Agent name to connect */
    public static final String AUT_AGENT_NAME_TO_CONNECT = "org.eclipse.jubula.client.ui.rcp.commands.ConnectToAUTAgentCommand.parameter.name"; //$NON-NLS-1$
    /** ID of command parameter for AUT Agent port to connect */
    public static final String AUT_AGENT_PORT_TO_CONNECT = "org.eclipse.jubula.client.ui.rcp.commands.ConnectToAUTAgentCommand.parameter.port"; //$NON-NLS-1$

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) throws ExecutionException {
        try {
            String name = event.getParameter(AUT_AGENT_NAME_TO_CONNECT);
            String port = event.getParameter(AUT_AGENT_PORT_TO_CONNECT);
            Integer portNo = null;

            if (port != null && name != null) {
                portNo = Integer.parseInt(event
                        .getParameter(AUT_AGENT_PORT_TO_CONNECT));
            } else {
                AutAgent fallbackAgent = ConnectAutAgentBP.getInstance()
                        .getWorkingAutAgent();
                if (fallbackAgent != null) {
                    name = fallbackAgent.getName();
                    portNo = fallbackAgent.getPort();
                } else {
                    return null;
                }
            }

            AutAgent autAgent = new AutAgent(name, portNo);
            TestExecutionGUIController.connectToAutAgent(autAgent);
        } catch (Exception e) {
            throw new ExecutionException(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void updateElement(UIElement element, Map parameters) {
        Object oName = parameters.get(AUT_AGENT_NAME_TO_CONNECT);
        Object oPort = parameters.get(AUT_AGENT_PORT_TO_CONNECT);

        if (oName == null || oPort == null) {
            return;
        }
        
        String name = oName.toString();
        String port = oPort.toString();
        
        boolean setChecked = false;
        AutAgent mostRecent = ConnectAutAgentBP.getInstance()
                .getWorkingAutAgent();
        if (mostRecent != null) {
            String mostRecentName = mostRecent.getName();
            String mostRecentPort = mostRecent.getPort().toString();
            if (name.equals(mostRecentName) && port.equals(mostRecentPort)) {
                setChecked = true;
            }
        }

        element.setChecked(setChecked);
    }
}