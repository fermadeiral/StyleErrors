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
package org.eclipse.jubula.client.core.businessprocess.importfilter.exceptions;

/**
 * @author BREDEX GmbH
 * @created Nov 21, 2008
 */
public class DataReadException extends Exception {


    /**
     * @param message {@inheritDoc}
     */
    public DataReadException(String message) {
        super(message);
    }

    /**
     * @param cause {@inheritDoc}
     */
    public DataReadException(Throwable cause) {
        super(cause);        
    }

    /**
     * @param message {@inheritDoc}
     * @param cause {@inheritDoc}
     */
    public DataReadException(String message, Throwable cause) {
        super(message, cause);
    }

}
