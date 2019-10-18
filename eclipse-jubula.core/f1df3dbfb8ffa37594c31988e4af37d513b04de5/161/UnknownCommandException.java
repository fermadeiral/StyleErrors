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

import org.eclipse.jubula.tools.internal.exception.CommunicationException;

/**
 * Exception to throw if an appropriate command could not created for a message. 
 * 
 * @author BREDEX GmbH
 * @created 20.07.2004
 */
public class UnknownCommandException 
    extends CommunicationException {

    /**
     * public constructor
     * 
     * @param message The detailed message.
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public UnknownCommandException(String message, Integer id) {
        super(message, id);
    }
}
