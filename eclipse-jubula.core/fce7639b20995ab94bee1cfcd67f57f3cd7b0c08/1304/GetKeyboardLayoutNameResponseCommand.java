/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.internal.commands;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.jubula.client.RCPKeyboardRegistry;
import org.eclipse.jubula.client.internal.BaseConnection;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.APICommand;
import org.eclipse.jubula.communication.internal.message.GetKeyboardLayoutNameResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SetKeyboardLayoutMessage;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accepts the name of the keyboard layout used for the AUT.
 *
 * @author BREDEX GmbH
 * @created Aug 2, 2011
 */
public class GetKeyboardLayoutNameResponseCommand implements APICommand {
    /** the logger */
    private static final Logger LOG = LoggerFactory
        .getLogger(GetKeyboardLayoutNameResponseCommand.class);

    /** the message */
    private GetKeyboardLayoutNameResponseMessage m_message;

    /** the AUT connection */
    private BaseConnection m_connection;

    /**
     * Constructor
     * 
     * @param connection
     *            the connection to use
     */
    public GetKeyboardLayoutNameResponseCommand(BaseConnection connection) {
        m_connection = connection;
    }

    /** {@inheritDoc} */
    public GetKeyboardLayoutNameResponseMessage getMessage() {
        return m_message;
    }

    /** {@inheritDoc} */
    public void setMessage(Message message) {
        m_message = (GetKeyboardLayoutNameResponseMessage) message;
    }

    /** {@inheritDoc} */
    public Message execute() {
        String layoutName = m_message.getKeyboardLayoutName();
        if (layoutName != null && layoutName.length() > 0) {
            Properties prop = RCPKeyboardRegistry.INSTANCE
                    .getPropertiesForLocalCode(layoutName);
            InputStream stream = null;
            try {
                if (prop == null) {
                    String filename =
                            SwtToolkitConstants.KEYBOARD_MAPPING_FILE_PREFIX
                                    + layoutName
                                    + SwtToolkitConstants
                                    .KEYBOARD_MAPPING_FILE_POSTFIX;
                    stream = getClass().getClassLoader()
                            .getResourceAsStream(filename);
                    if (stream != null) {
                        prop = new Properties();
                        prop.load(stream);
                    }
                }
                if (prop != null) {
                    m_connection.send(new SetKeyboardLayoutMessage(prop));
                } else {
                    LOG.error("Mapping for '" + layoutName + "' could not be found."); //$NON-NLS-1$//$NON-NLS-2$
                }
            } catch (IOException ioe) {
                LOG.error("Error occurred while loading Keyboard Mapping.", ioe); //$NON-NLS-1$
            } catch (IllegalArgumentException iae) {
                LOG.error("Error occurred while loading Keybaord Mapping.", iae); //$NON-NLS-1$
            } catch (ConnectionException e) {
                LOG.error("Error occurred while loading Keybaord Mapping.", e); //$NON-NLS-1$
            } catch (CommunicationException e) {
                LOG.error("Error occurred while loading Keybaord Mapping.", e); //$NON-NLS-1$
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        LOG.warn("Error occurred while closing stream.", e); //$NON-NLS-1$
                    }
                }
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public void timeout() {
        LOG.error(this.getClass().getName() + "timeout() called"); //$NON-NLS-1$
    }
}