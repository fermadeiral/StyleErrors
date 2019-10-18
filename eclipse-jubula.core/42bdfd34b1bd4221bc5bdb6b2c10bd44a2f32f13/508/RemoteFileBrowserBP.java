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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.SendDirectoryMessage;
import org.eclipse.jubula.communication.internal.message.SendDirectoryResponseMessage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;


/**
 * @author BREDEX GmbH
 * @created May 28, 2009
 */
public class RemoteFileBrowserBP {
    /** caching for directories */
    private static Map<String, SendDirectoryResponseMessage> remoteDirs = 
        new HashMap<String, SendDirectoryResponseMessage>(1017);
    /** max. time for a browser reponse */
    private static final int TIMEOUT = 5000;
    /** the Communicator channel to be used */
    private Communicator m_com;
    /** ICommand to be called as a response to the request */
    private ICommand m_response;
    /** monitor for wait/notify */
    private final Boolean m_responseMonitor = new Boolean(true);
    /** thread sync */
    private boolean m_hasReponsedArrived;
    /** thread sync */
    private boolean m_timeOut;
    /** result of remote browsing */
    private SendDirectoryResponseMessage m_responseMsg;
    /** convenience: no error occurred */
    private boolean m_isDataValid;
    
    /**
     * @param com is the Communicator channel to be used. This is needed as 
     * soon as multiple connections to AUT controllers are supported.
     */
    public RemoteFileBrowserBP(Communicator com) {
        Assert.isNotNull(com, "no null connection allowed"); //$NON-NLS-1$
        m_com = com;
        m_response = new HandleRemoteFileBrowsing(this);
    }
    
    /**
     * fetch the results from the remote directory browser
     * 
     * @param root Directory from which to get the content list 
     * @return true if the directory contents could be fetched, false in case
     * of errors.
     */
    public boolean fetchRemoteDirContent(String root) {
        SendDirectoryResponseMessage fromCache = null;
        synchronized (remoteDirs) {
            fromCache = remoteDirs.get(root);
            if (fromCache != null) {
                m_responseMsg = fromCache;
                m_hasReponsedArrived = true;
                m_timeOut = false;
            }
        }
        if (fromCache == null) {
            SendDirectoryMessage sd = new SendDirectoryMessage(root);
            m_hasReponsedArrived = false;
            m_timeOut = false;
            synchronized (m_responseMonitor) {
                try {
                    m_com.request(sd, m_response, TIMEOUT);
                } catch (CommunicationException e) {
                    return false;
                }
                while (!m_hasReponsedArrived) {
                    try {
                        m_responseMonitor.wait();
                    } catch (InterruptedException e) {
                        // not relevant
                    }
                }
            }
        }
        m_isDataValid = !m_timeOut
                && (m_responseMsg.getError() 
                        == SendDirectoryResponseMessage.OK);
        return m_isDataValid;
    }

    /**
     * return the results from the last fetch from the remote directory browser
     * The strings contain of a starting character which specifies if the
     * entry is a directory or file and the relative pathname of 
     * @return a (possibly empty) list of Strings decribing the contents of
     * root.
     */
    @SuppressWarnings("unchecked")
    public List<String> getRemoteDirContent() {   
        if (m_isDataValid && m_responseMsg != null) {
            return m_responseMsg.getDirEntries();
        }
        return Collections.EMPTY_LIST;
    }
    
    /**
     * 
     * @return the Strings describing the remote file system roots
     */
    @SuppressWarnings("unchecked")
    public List<String> getRemoteFilesystemRoots() {
        if (m_isDataValid && m_responseMsg != null) {
            return m_responseMsg.getRoots();
        }
        return Collections.EMPTY_LIST;
    }
    /**
     * 
     * @return the separator character for the remote system or an empty String
     * in case of errors.
     */
    public String getSepChar() {
        if (m_isDataValid && m_responseMsg != null) {
            return String.valueOf(m_responseMsg.getSeparator());
        }
        return StringConstants.EMPTY;
    }

    /**
     * @return the responseMonitor
     */
    public Boolean getResponseMonitor() {
        return m_responseMonitor;
    }

    /**
     * @param hasReponsedArrived the hasReponsedArrived to set
     */
    public void setHasReponsedArrived(boolean hasReponsedArrived) {
        m_hasReponsedArrived = hasReponsedArrived;
    }

    /**
     * @param timeOut the timeOut to set
     */
    public void setTimeOut(boolean timeOut) {
        m_timeOut = timeOut;
    }

    /**
     * @param responseMsg the responseMsg to set
     */
    public void setResponseMsg(SendDirectoryResponseMessage responseMsg) {
        synchronized (remoteDirs) {
            m_responseMsg = responseMsg;
            remoteDirs.put(m_responseMsg.getBase(), m_responseMsg);            
        }
    }

    /**
     * @return the isDataValid
     */
    public boolean isDataValid() {
        return m_isDataValid;
    }
    
    /**
     * disgard the cache when done with a request
     */
    public static void clearCache() {
        synchronized (remoteDirs) {
            remoteDirs.clear();            
        }
    }
}
