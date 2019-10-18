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
package org.eclipse.jubula.client.core.commands;


import java.util.Iterator;

import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.ObjectMappedMessage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for ObjectMappedMessage. <br>
 * 
 * The <code>execute()</code> method just logs the information from the
 * message (info level). No message is returned.
 * @author BREDEX GmbH
 * @created 25.08.2004
 * 
 */
public class ObjectMappedCommand implements ICommand {

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ObjectMappedCommand.class);
    
    /** the message */
    private ObjectMappedMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public Message getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_message = (ObjectMappedMessage)message;
    }

    /**
     * Just create a log entry on info level. <br> 
     * Returns always null.
     *  
     * {@inheritDoc}
     */
    public Message execute() {
        mapObject(m_message.getComponentIdentifiers());
        return null;
    }

    /**
     * @param componentIdentifiers The identifiers to be mapped
     */
    private void mapObject(IComponentIdentifier[] componentIdentifiers) {
        if (log.isInfoEnabled()) {
            for (IComponentIdentifier componentIdentifier 
                    : componentIdentifiers) {
                try {
                    String logMessage = Messages.MappedObject
                            + StringConstants.SPACE
                            + StringConstants.APOSTROPHE
                            + componentIdentifier.getComponentName()
                            + StringConstants.APOSTROPHE
                            + StringConstants.SPACE + Messages.OfType
                            + StringConstants.SPACE
                            + StringConstants.APOSTROPHE
                            + componentIdentifier.getComponentClassName()
                            + StringConstants.APOSTROPHE
                            + StringConstants.SPACE + Messages.InHierachy
                            + StringConstants.COLON + StringConstants.SPACE;
                    for (Iterator iter = componentIdentifier
                            .getHierarchyNames().iterator(); iter.hasNext();) {
                        String element = (String) iter.next();
                        logMessage = logMessage + element
                                + StringConstants.COMMA;
                    }
                    log.info(logMessage);
                } catch (ClassCastException cce) {
                    log.error(Messages.ComponentIdentifiersDoes);
                }
            }
        }
        ObjectMappingEventDispatcher
                .notifyObjectMappedObserver(componentIdentifiers);
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + Messages.TimeoutCalled);
    }

}
