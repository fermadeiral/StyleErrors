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

package org.eclipse.jubula.client.cmd.exceptions;

/**
 * This exception is thown, if there were arguments missing
 * 
 * @author BREDEX GmbH
 * @created Feb 10, 2007
 */
public class PreValidateException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3262222086674644138L;

    /**
     * public constructor
     * 
     * @param message
     *            The detailed message. {@inheritDoc}
     */
    public PreValidateException(String message) {
        super(message);
    }
}
