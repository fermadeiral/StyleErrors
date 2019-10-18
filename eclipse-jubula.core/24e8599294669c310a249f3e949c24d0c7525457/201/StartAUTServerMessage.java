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
 * The class <code>StartAutServerCommand</code> and the associated
 * <code>StartAutServerMessage</code> are used as examples for the intended use
 * of the communications layer in Jubula. Since changes are expected, this
 * documentation is inlined in the source code. Please reevaluate the Java doc
 * frequently for changes in this templates. The message send from the client to
 * the server to start the AUTServer. <br>
 * The response message is StartAUTServerStateMessage.
 * 
 * @author BREDEX GmbH
 * @created 04.08.2004
 */
public class StartAUTServerMessage extends Message {
    /** the actual autToolKit of the project as String */
    private String m_autToolKit;

    /** The Map with the AUT configuration */
    private Map<String, String> m_autConfiguration = null;

    /**
     * @deprecated Default constructor for transportation layer. Don't use for
     *             normal programming.
     */
    public StartAUTServerMessage() {
        super();
    }

    /**
     * Constructs a complete message. No null values are allowed as parameters.
     * 
     * @param autConfig
     *            a Map<String, String> with the AutConfiguration
     * @param autToolKit
     *            the autToolKit of the actual project as string
     */
    public StartAUTServerMessage(Map<String, String> autConfig,
        String autToolKit) {
        super();

        setAutConfiguration(autConfig);
        setAutToolKit(autToolKit);
    }

    /**
     * @param autToolKit
     *            the actual autToolKit of the project as String
     */
    private void setAutToolKit(String autToolKit) {
        m_autToolKit = autToolKit;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.START_AUT_SERVER_COMMAND;
    }

    /** @return the actual autToolKit of the project as String */
    public String getAutToolKit() {
        return m_autToolKit;
    }

    /** @return the autConfiguration */
    public Map<String, String> getAutConfiguration() {
        return m_autConfiguration;
    }

    /**
     * @param autConfiguration
     *            the autConfiguration to set
     */
    private void setAutConfiguration(Map<String, String> autConfiguration) {
        m_autConfiguration = autConfiguration;
    }
}