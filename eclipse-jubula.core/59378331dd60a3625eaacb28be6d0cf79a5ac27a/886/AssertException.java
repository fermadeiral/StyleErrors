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
package org.eclipse.jubula.tools.internal.exception;

/**
 * @author BREDEX GmbH
 * @created 08.10.2004
 */
public class AssertException extends RuntimeException {
    /**
     * AssertException constructor comment.
     * 
     * @param s
     *            java.lang.String
     */
    public AssertException(String s) {
        super(s);
    }
}
