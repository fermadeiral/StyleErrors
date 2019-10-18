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

import org.eclipse.jubula.autagent.AutStarter.Verbosity;
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

    /**
     * @return a sharable, already connected instance of an AUTAgent
     * @throws CommunicationException
     */
    public AUTAgent agent() throws CommunicationException {
        if (m_agent == null) {
            int port = NetUtil.getFreePort();
            AutStarter starter = AutStarter.getInstance();
            try {
                starter.start(port, false, Verbosity.QUIET, false);
                m_agent = MakeR.createAUTAgent(EnvConstants.LOCALHOST_ALIAS,
                        port);
                m_agent.connect();
            } catch (JBVersionException | IOException e) {
                throw new CommunicationException(e);
            }
        }

        return m_agent;
    }
}