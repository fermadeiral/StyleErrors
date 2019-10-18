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
package org.eclipse.jubula.client.internal.exceptions;

import org.eclipse.jubula.tools.internal.exception.CommunicationException;

/**
 * An exception thrown if a connection to the AUT-Agent or to the AUTServer
 * could not initialized.
 * 
 * @author BREDEX GmbH
 * @created 12.08.2004
 */
public class ConnectionException 
    extends CommunicationException {
    /**
     * public constructor
     * @param message the detailed message
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public ConnectionException (String message, Integer id) {
        super(message, id);
    }
}
