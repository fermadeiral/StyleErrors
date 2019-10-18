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

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created 05.08.2010
 */
public class GetAutConfigMapMessage extends Message {
    /** The AUT_ID */
    private String m_autId;

    /**
     * default constructor
     * 
     * @deprecated
     */
    public GetAutConfigMapMessage() {
        // do not use
    }

    /**
     * @param autId
     *            The AutConfigMap to this AUT_ID will be returned
     */
    public GetAutConfigMapMessage(String autId) {
        this.m_autId = autId;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.GET_AUT_CONFIGMAP_COMMAND;
    }

    /** @return The AutID */
    public String getAutId() {
        return m_autId;
    }

    /**
     * @param autId
     *            sets the AUT_ID
     */
    public void setAutId(String autId) {
        this.m_autId = autId;
    }
}