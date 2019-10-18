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
package org.eclipse.jubula.examples.aut.dvdtool.persistence;

/**
 * This exception is thrown if the object could not made persistent.
 *
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdInvalidObjectException extends DvdPersistenceException {

    /**
     * public constructor
     * @param message a message describing the exception
     * @param exception a nested exception
     */
    public DvdInvalidObjectException(String message, Exception exception) {
        super(message, exception);
    }
}
