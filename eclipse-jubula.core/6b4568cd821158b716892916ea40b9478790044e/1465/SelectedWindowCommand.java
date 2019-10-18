/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.commands;

import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.html.OMSelWinResponseMessage;

/**
 * Command executed by the ITE after having received the newly
 *    selected window's title from the AUT.
 * @author BREDEX GmbH
 *
 */
public class SelectedWindowCommand implements ICommand {

    /** The response message */
    private OMSelWinResponseMessage m_message;

    /** {@inheritDoc} */
    public Message getMessage() {
        return m_message;
    }

    /** {@inheritDoc} */
    public void setMessage(Message message) {
        m_message = (OMSelWinResponseMessage) message;
    }

    /** {@inheritDoc} */
    public Message execute() {
        DataEventDispatcher.getInstance().fireWindowTitleChanged(m_message);
        return null;
    }

    /** {@inheritDoc} */
    public void timeout() {
        m_message = new OMSelWinResponseMessage();
        m_message.setCode(OMSelWinResponseMessage.UNEXPECTED_ERROR);
        DataEventDispatcher.getInstance().fireWindowTitleChanged(m_message);
    }
}
