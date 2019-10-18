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

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.toolkit.ToolkitInfo;
import org.eclipse.jubula.tools.AUTIdentifier;

/**
 * @author BREDEX GmbH
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface AUTAgent extends Remote {
    /**
     * start an AUT
     * 
     * @param configuration
     *            an AUT configuration to launch the AUT
     * @return an identifier for the running AUT
     * @throws CommunicationException
     *             in case of communication problems with the remote side
     */
    @Nullable
    AUTIdentifier startAUT(AUTConfiguration configuration)
        throws CommunicationException;

    /**
     * stop an AUT
     * 
     * @param aut
     *            a reference to the AUT to stop
     * @throws CommunicationException
     *             in case of communication problems with the remote side
     */
    void stopAUT(AUTIdentifier aut) throws CommunicationException;

    /**
     * @return an unmodifiable list of currently known / registered AUT IDs
     * @throws CommunicationException
     *             in case of communication problems with the remote side
     */
    List<AUTIdentifier> getAllRegisteredAUTIdentifier()
        throws CommunicationException;

    /**
     * @param autID
     *            the autID to get an AUT for
     * @param information
     *            the information about the toolkit
     * @return an AUT - note: currently the underlying implementation only
     *         supports <b>ONE</b> connection at a time to a remotely running AUT;
     *         multiple connections may only be established sequentially!
     * @throws CommunicationException
     *             in case of communication problems with the remote side
     */
    AUT getAUT(AUTIdentifier autID, ToolkitInfo information)
        throws CommunicationException;
}