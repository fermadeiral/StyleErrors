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

/**
 * Interface for an Exception handler. <br>
 * It's used by the servers: The thread reading from the network is the last
 * thread running, so it catches Throwable and calls an exception handler
 * implementing this interface.
 * 
 * @author BREDEX GmbH
 * @created 21.09.2004
 */
public interface IExceptionHandler {
    /**
     * handle the exception
     * 
     * @param t
     *            the occurred exception, throwable, this means every!!!
     *            Exception/Error was caught
     * @return false if the execution should stopped, true otherwise.
     */
    public boolean handle(Throwable t);
}
