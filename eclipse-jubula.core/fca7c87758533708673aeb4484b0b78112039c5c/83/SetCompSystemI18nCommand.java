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
package org.eclipse.jubula.communication.internal.commands;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Oct 31, 2007
 */
public class SetCompSystemI18nCommand implements ICommand {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(SetCompSystemI18nCommand.class);
    
    /** The Message */
    private SendCompSystemI18nMessage m_message;
    
    /** {@inheritDoc} */
    public Message execute() {
        final String resourceBundles = m_message.getResourceBundles();
        CompSystemI18n.fromString(resourceBundles); 
        return null;
    }

    /** {@inheritDoc} */
    public Message getMessage() {
        return m_message;
    }

    /** {@inheritDoc} */
    public void setMessage(Message message) {
        try {
            m_message = (SendCompSystemI18nMessage)message;            
        } catch (ClassCastException cce) {
            if (log.isErrorEnabled()) {
                log.error("Cannot convert from " //$NON-NLS-1$
                        + message.getClass().toString() + " to " //$NON-NLS-1$
                        + m_message.getClass().toString(), cce);
            }
            throw cce;
        }
    }

    /** {@inheritDoc} */
    public void timeout() {
        log.error(this.getClass().getName() + "timeout() called when it shouldn't (no response)"); //$NON-NLS-1$
    }

}
