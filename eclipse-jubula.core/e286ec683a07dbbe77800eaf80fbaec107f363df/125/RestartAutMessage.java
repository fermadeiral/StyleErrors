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
package org.eclipse.jubula.communication.internal.message;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * @author BREDEX GmbH
 * @created Mar 25, 2010
 */
public class RestartAutMessage extends Message {
    /** the ID of the Running AUT to restart */
    private AutIdentifier m_autId;
    /**
     * the timeout to use
     */
    private int m_timeout;
    
    /**
     * Constructor for use in framework methods. Do not use for normal
     * programming.
     * 
     * @deprecated
     */
    public RestartAutMessage() {
        // Nothing to initialize
    }

    /**
     * Constructor
     * 
     * @param autId
     *            The ID of the Running AUT to restart.
     * @param timeout
     *            the timeout to use; 0 indicates that the AUT should be forced
     *            to terminate
     */
    public RestartAutMessage(AutIdentifier autId, int timeout) {
        Validate.isTrue(timeout >= 0);
        m_autId = autId;
        setTimeout(timeout);
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.RESTART_AUT_COMMAND;
    }

    /** @return the ID of the Running AUT to restart. */
    public AutIdentifier getAutId() {
        return m_autId;
    }

    /**
     * @param autId
     *            The ID of the Running AUT to restart.
     */
    public void setAutId(AutIdentifier autId) {
        m_autId = autId;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return m_timeout;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(int timeout) {
        m_timeout = timeout;
    }
}