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
package org.eclipse.jubula.toolkit.common.exception;

/**
 * @author BREDEX GmbH
 * @created 25.05.2007
 */
public class ToolkitPluginException extends Exception {

    /**
     * @param message a message
     */
    public ToolkitPluginException(String message) {
        super(message);
    }

    /**
     * @param message a message
     * @param cause the initial Throwable
     */
    public ToolkitPluginException(String message, Throwable cause) {
        super(message, cause);
    }

}
