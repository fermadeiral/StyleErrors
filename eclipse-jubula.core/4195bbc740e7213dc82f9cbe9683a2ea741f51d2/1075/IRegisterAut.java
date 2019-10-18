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
package org.eclipse.jubula.rc.common.registration;

import java.io.IOException;

import org.eclipse.jubula.tools.internal.exception.JBVersionException;

/**
 * Interface for objects capable of registering an AUT.
 *
 * @author BREDEX GmbH
 * @created Dec 11, 2009
 */
public interface IRegisterAut {

    /**
     * Registers the AUT managed by the receiver.
     * 
     * @throws IOException if an I/O error prevents the registration. This 
     *                     indicates that the registration was unsuccessful.
     * @throws JBVersionException if there is a version mismatch between Communicators
     */
    public void register() throws IOException, JBVersionException;

}
