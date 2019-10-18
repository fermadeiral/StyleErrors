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

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * @author BREDEX GmbH
 * @created May 18, 2009
 */
public class SendDirectoryMessage extends Message {
    /** m_dirname */
    private String m_dirname;

    /** base constructor */
    public SendDirectoryMessage() {
        super();
    }

    /**
     * base constructor
     * 
     * @param dirname
     *            Directory to browse
     */
    public SendDirectoryMessage(String dirname) {
        super();
        m_dirname = dirname;
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.SEND_DIRECTORY_COMMAND;
    }

    /** @return the dirname */
    public String getDirname() {
        return m_dirname;
    }
}