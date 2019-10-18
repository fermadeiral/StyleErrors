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
package org.eclipse.jubula.client.core.progress;

import org.eclipse.core.runtime.IStatus;

/**
 * Presents an interface for writing text messages to a console.
 *
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public interface IProgressConsole {
    /**
     * Writes a single line to the console.
     * 
     * @param line The text to write to the console.
     */
    public void writeLine(String line);
    
    /**
     * Writes a single error line to the console.
     * 
     * @param line The text to write to the console as an error.
     */
    public void writeErrorLine(String line);
    
    /**
     * Writes the status to the console.
     * 
     * @param status The status to write to the console
     */
    public void writeStatus(IStatus status);
    
    /**
     * Writes the status to the console, which belongs to the given session. If
     * no session exist a new one will be created. The managing of sessions is
     * part of the concrete IProgressConsole implementations. To be more
     * speccific, the ITE will create new console windows if no console window
     * with the given name exist. The cmd clients will write the id in the first
     * line
     * 
     * @param status The status to write to the console
     * @param id Identification string
     */
    public void writeStatus(IStatus status, String id);
    
    /**
     * The ite will close all console windows, for other IProgressConsole
     * implementations this is currently a no op
     */
    public void closeConsole();
}
