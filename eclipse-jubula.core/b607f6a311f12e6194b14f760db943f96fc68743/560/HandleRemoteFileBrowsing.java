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
package org.eclipse.jubula.client.ui.rcp.businessprocess;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendDirectoryResponseMessage;

/**
 * Handle the answer to the browsing request
 */
class HandleRemoteFileBrowsing implements ICommand {

    /**
     * <code>m_remoteFileBrowserBP</code>
     */
    private final RemoteFileBrowserBP m_remoteFileBrowserBP;

    /** returned message */
    private SendDirectoryResponseMessage m_msg;

    /**
     * @param remoteFileBrowserBP originating BP
     */
    HandleRemoteFileBrowsing(RemoteFileBrowserBP remoteFileBrowserBP) {
        m_remoteFileBrowserBP = remoteFileBrowserBP;
    }

    
    /**
     * {@inheritDoc}
     */
    public Message execute() {            
        m_remoteFileBrowserBP.setResponseMsg(m_msg);
        wakeup();
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Message getMessage() {            
        return m_msg;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        m_msg = (SendDirectoryResponseMessage)message;            
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        m_remoteFileBrowserBP.setTimeOut(true);     
        wakeup();
    }
    
    /** notify the requesting thread */
    private void wakeup() {
        final Boolean responseMonitor = 
            m_remoteFileBrowserBP.getResponseMonitor();
        synchronized (responseMonitor) {
            m_remoteFileBrowserBP.setHasReponsedArrived(true);
            responseMonitor.notifyAll();
        }
    }
}