/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent;

import java.io.IOException;

import org.eclipse.jubula.autagent.common.AutStarter;
import org.eclipse.jubula.autagent.common.AutStarter.Verbosity;
import org.eclipse.jubula.autagent.common.agent.AutAgent;
import org.eclipse.jubula.autagent.common.desktop.DesktopIntegration;
import org.eclipse.jubula.autagent.common.utils.AutStartHelperRegister;
import org.eclipse.jubula.autagent.internal.APIAgentAutStartHelper;
import org.eclipse.jubula.autagent.internal.EmbeddedDesktopIntegration;
import org.eclipse.jubula.client.AUTAgent;
import org.eclipse.jubula.client.MakeR;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.utils.NetUtil;

/** @author BREDEX GmbH */
public enum Embedded {
    /** Singleton */
    INSTANCE;
    /** the agent */
    private AUTAgent m_agent = null;
    
    /** the agent */
    private DesktopIntegration m_tray = null;

    /**
     * Starts the embedded AUTAgent or if its already started gives back the
     * {@link AutAgent} from the embedded Autagent
     * 
     * @return a sharable instance of an AUTAgent
     * @throws CommunicationException
     */
    public AUTAgent agent() throws CommunicationException {
        if (m_agent == null) {
            int port = NetUtil.getFreePort();
            return agent(port);
        }
        return m_agent;
    }

    /**
     * Starts the embedded AUTAgent or if its already started gives back the
     * {@link AutAgent} from the embedded Autagent
     * 
     * @return a sharable instance of an AUTAgent
     * @param port the port number to use
     * @throws CommunicationException
     */
    public AUTAgent agent(int port) throws CommunicationException {
        if (m_agent == null) {

            AutStarter starter = AutStarter.getInstance();
            try {
                starter.start(port, false, Verbosity.QUIET, false);
                AutStartHelperRegister.INSTANCE
                        .setAutStartHelper(new APIAgentAutStartHelper());
                m_agent = MakeR.createAUTAgent(EnvConstants.LOCALHOST_ALIAS,
                        port);
                m_tray = new EmbeddedDesktopIntegration();
                m_tray.setPort(port);
                starter.getAgent().addPropertyChangeListener(
                        AutAgent.PROP_NAME_AUTS, m_tray);
            } catch (JBVersionException | IOException e) {
                throw new CommunicationException(e);
            }
        }
        return m_agent;
    }
    /**
     * shuts the AUTAgent down
     */
    public void shutdown() {
        AutStarter.shutdown();
        m_tray.removeSystemTray();
        m_agent = null;
    }
}