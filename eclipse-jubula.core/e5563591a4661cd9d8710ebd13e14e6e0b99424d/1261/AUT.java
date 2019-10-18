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

import java.awt.image.BufferedImage;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.exceptions.CommunicationException;
import org.eclipse.jubula.client.exceptions.ExecutionException;
import org.eclipse.jubula.client.exceptions.ExecutionExceptionHandler;
import org.eclipse.jubula.communication.CAP;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.Profile;

/**
 * Instances of this class represent a (remotely) running instance of an AUT.
 * This <b>A</b>pplication <b>U</b>nder <b>T</b>est may either be started internally via an
 * {@link org.eclipse.jubula.client.launch.AUTConfiguration AUTConfiguration} or
 * externally by using autrun.
 * 
 * @author BREDEX GmbH
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface AUT extends Remote {

    /**
     * connect to the remote side - note: currently the underlying
     * implementation only supports <b>ONE</b> connection at a time to the
     * remote side; multiple connections may only be established sequentially by
     * calling {@link #disconnect()} on this instance first!
     * 
     * @param timeOut 
     * @throws CommunicationException
     *             in case of communication problems with the remote side
     * @since 3.2
     */
    void connect(int timeOut) throws CommunicationException;
    
    /**
     * @return the identifier for this AUT
     */
    AUTIdentifier getIdentifier();

    /**
     * @param cap
     *            the CAP to execute on the AUT
     * @param <T>
     *            the payload type
     * @param payload
     *            the additional payload for the execution
     * @return the result of the execution
     * @throws ExecutionException
     *             in case of remote execution problems; behavior may vary if
     *             ExecutionExceptionHandler are being used
     * @throws CommunicationException
     *             in case of communication problems with the remote side
     */
    <T> Result<T> execute(CAP cap, @Nullable T payload)
        throws ExecutionException, CommunicationException;

    /**
     * @param handler
     *            the exception handler for this AUT; may be <code>null</code>.
     */
    void setHandler(@Nullable ExecutionExceptionHandler handler);
    
    /**
     * @return a screenshot of the AUTs primary display; may be <code>null</code>.
     * 
     * @throws IllegalStateException
     *             in case of e.g. being called when not connected to an AUT
     * @since 3.2
     */
    @Nullable BufferedImage getScreenshot() throws IllegalStateException;
    
    /**
     * Sets the Profile which will be used in the test execution
     * @param profile the heuristic profile to use in the test execution
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws CommunicationException
     * @since 3.2
     */
    void setProfile(Profile profile) throws IllegalArgumentException,
            IllegalStateException, CommunicationException;
    /**
     * if set to <code>true<code> the name of the method and the parameters
     * from the CAP which will be executed are written to the console (standard out).
     * Also if there is an exception the name will be printed.
     * @param logCapToConsole <code>true</code> if there should be logging to the console
     * @since 3.2
     */
    void setCAPtoConsoleLogging(boolean logCapToConsole);
}