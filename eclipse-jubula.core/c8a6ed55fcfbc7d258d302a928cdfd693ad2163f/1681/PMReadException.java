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
package org.eclipse.jubula.client.core.persistence;


/** in case of a read error */
public class PMReadException extends PMException {
    /**
     * {@inheritDoc}
     * @param message guess what...
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public PMReadException(String message, Integer id) {
        super(message, id);
    }
}