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
package org.eclipse.jubula.client.exceptions;

import org.eclipse.jdt.annotation.Nullable;

/**
 *  Exception for the case that a resource could not be loaded correctly.
 *
 * @author BREDEX GmbH
 * @created 13.10.2014
 */
public class LoadResourceException extends Exception {
    /**
     * @param message The detailed message for this exception.
     */
    public LoadResourceException(String message) {
        super(message);
    }
    
    /**
     * @param message The detailed message for this exception.
     * @param cause The cause for the exception.
     */
    public LoadResourceException(String message, 
        @Nullable Throwable cause) {
        super(message, cause);
    }
}