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

import java.util.Map;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created 05.08.2010
 */
public class GetAutConfigMapResponseMessage extends Message {
    /** The returned AutConfigMap from the agent */
    private Map<String, String> m_autConfigMap;

    /**
     * default constructor
     * 
     * @deprecated use constructor with parameters
     */
    public GetAutConfigMapResponseMessage() {
        // do not use
    }

    /**
     * @param autConfigMap
     *            The autConfigMap
     */
    public GetAutConfigMapResponseMessage(Map<String, String> autConfigMap) {
        m_autConfigMap = autConfigMap;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.GET_AUT_CONFIGMAP_COMMAND_RESPONSE_COMMAND;
    }

    /** @return The autConfigMap form the Agent */
    public Map<String, String> getAutConfigMap() {
        return m_autConfigMap;
    }

    /**
     * @param autConfigMap
     *            the autConfigMap, set by the agent
     */
    public void setAutConfigMap(Map<String, String> autConfigMap) {
        this.m_autConfigMap = autConfigMap;
    }
}