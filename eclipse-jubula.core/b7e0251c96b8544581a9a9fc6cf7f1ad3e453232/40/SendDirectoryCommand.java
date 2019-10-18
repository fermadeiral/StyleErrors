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
package org.eclipse.jubula.autagent.common.commands;

import java.io.File;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendDirectoryMessage;
import org.eclipse.jubula.communication.internal.message.SendDirectoryResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created May 18, 2009
 */
public class SendDirectoryCommand implements ICommand {

    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(SendDirectoryCommand.class);

    /** the message */
    private SendDirectoryMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        SendDirectoryResponseMessage resp = new SendDirectoryResponseMessage();
        resp.setBase(m_message.getDirname()); // association for cache
        
        File workDir = new File(m_message.getDirname());
        if (!workDir.isDirectory()) {
            return resp;
        }

        File[] entries = workDir.listFiles();
        if (entries == null) { // IO error
            resp.setError(SendDirectoryResponseMessage.IO_ERROR);
            return resp;
        }
        for (int i = 0; i < entries.length; ++i) {
            File entry = entries[i];
            if (!entry.isHidden() && entry.canRead()) {
                if (entry.isDirectory()) {
                    resp.addDir(entry.getPath());
                } else {
                    resp.addFile(entry.getPath());
                }
            }

        }
        File[] roots = File.listRoots();
        if (roots != null) {
            for (int i = 0; i < roots.length; ++i) {
                resp.addRoot(roots[i].getAbsolutePath());
            }
        }

        return resp;
    }

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
        m_message = (SendDirectoryMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        log.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

}
