/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client;

import org.eclipse.jubula.client.exceptions.CommunicationException;

/**
 * @author BREDEX GmbH
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface Remote {
    /**
     * connect to the remote side - note: currently the underlying
     * implementation only supports <b>ONE</b> connection at a time to the
     * remote side; multiple connections may only be established sequentially by
     * calling {@link #disconnect()} on this instance first!
     * 
     * @throws CommunicationException
     *             in case of communication problems with the remote side
     */
    void connect() throws CommunicationException;

    /**
     * Disconnects from the remote side. Make sure to disconnect one instance
     * before calling {@link #connect()} on other instances
     */
    void disconnect();

    /**
     * @return whether a connection to the remote side is currently established
     */
    boolean isConnected();
}
