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
package org.eclipse.jubula.communication.internal;

import org.eclipse.jubula.communication.internal.message.Message;

/**
 * Every command class must implement the <code>ICommand</code> interface.
 * This interface has two methods <code>setMessage()</code> and
 * <code>getMessage()</code> which are used mostly to support the proper
 * operation of the class. The methods convert from the generic
 * <code>Message</code> object to the specific class used for the command.
 * 
 * The most important methods if of course <code>execute()</code> which will
 * do the processing defined for this command. The method will use the data from
 * the supplied <code>Message</code> object and perform its task. The method
 * returns a <code>Message</code> object which contains all necessary
 * information for the originator of the message to proceed. This may be error
 * messages or a normal result.
 * 
 * The <code>timeout()</code> method is called only if this command is a
 * response to a request and the answer didn't arrive in the specified time. If
 * <code>timeout()</code> gets called it is guaranteed that
 * <code>execute()</code> won't be called if the answer arrives later.
 * 
 * This interface defines the methods for a command object.
 * @author BREDEX GmbH
 * @created 06.07.2004
 * 
 */
public interface ICommand {

    /**
     * Getter for the message - (the data) for this command
     * 
     * @return the message
     */
    public Message getMessage();

    /**
     * Sets the message for this command. This method is intendend for internal use
     * by the communications layer. It must convert from the base class <code>Message</code>
     * to the command specific message class. The method may throw a cast class exception
     * if the wrong message class is provided.
     * 
     * @param message -
     *            the message
     */
    public void setMessage(Message message);

    /**
     * This method performs the command.
     * 
     * @return the message to send as answer, return null if this command does
     *         not produce an answer.
     */
    public Message execute();

    /**
     * called if the command results from a message send as a request, but was
     * received to late.
     */
    public void timeout();

}
