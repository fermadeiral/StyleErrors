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
package org.eclipse.jubula.autagent.agent;

import org.eclipse.jubula.autagent.commands.StartAUTServerCommand;
import org.eclipse.jubula.communication.internal.message.StartAUTServerMessage;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * Restarts an AUT that was started via an AUT Configuration.
 * 
 * @author BREDEX GmbH
 * @created Mar 26, 2010
 */
public class RestartAutConfiguration implements IRestartAutHandler {

    /** the message used to start the AUT */
    private StartAUTServerMessage m_startAutMessage;

    /** the ID of the the started AUT */
    private AutIdentifier m_autId;

    /**
     * Constructor
     * 
     * @param autId
     *            The ID of the started AUT.
     * @param startAutMessage
     *            The message used to start the AUT.
     */
    public RestartAutConfiguration(AutIdentifier autId,
            StartAUTServerMessage startAutMessage) {
        m_startAutMessage = startAutMessage;
        m_autId = autId;
    }

    /** {@inheritDoc} */
    public void restartAut(AutAgent agent, int timeout) {
        agent.stopAut(m_autId, timeout);
        StartAUTServerCommand startCommand = new StartAUTServerCommand();
        startCommand.setMessage(m_startAutMessage);
        startCommand.execute();
    }

    @Override
    public String getAUTStartClass() {
        String autToolkit = m_startAutMessage.getAutToolKit();
        String toolkitName = autToolkit.substring(
            autToolkit.lastIndexOf('.') + 1, 
            autToolkit.lastIndexOf("ToolkitPlugin")); //$NON-NLS-1$
        String className = "org.eclipse.jubula.autagent.commands.Start" //$NON-NLS-1$
            + toolkitName + "AutServerCommand"; //$NON-NLS-1$
        return className;
    }
}
