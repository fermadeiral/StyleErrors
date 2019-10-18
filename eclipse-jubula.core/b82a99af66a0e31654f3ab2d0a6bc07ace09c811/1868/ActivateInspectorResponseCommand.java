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
package org.eclipse.jubula.client.inspector.ui.commands;

import org.eclipse.jubula.client.inspector.ui.i18n.Messages;
import org.eclipse.jubula.client.inspector.ui.provider.sourceprovider.InspectorStateProvider;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.ActivateInspectorResponseMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The command object for an ToggleInspectorResponseMessage. <br>
 * 
 * @author BREDEX GmbH
 * @created 06.07.2009
 * 
 */
public class ActivateInspectorResponseCommand implements ICommand {

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(ActivateInspectorResponseCommand.class);

    /** the message */
    private ActivateInspectorResponseMessage m_message;
    
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
        m_message = (ActivateInspectorResponseMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        ISourceProviderService service = PlatformUI.getWorkbench().getService(
                    ISourceProviderService.class);
        InspectorStateProvider sourceProvider = 
            (InspectorStateProvider)service.getSourceProvider(
                    InspectorStateProvider.IS_INSPECTOR_ACTIVE);
        sourceProvider.setInspectorActive(true);
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + Messages.TimeoutCalled);
    }

}
