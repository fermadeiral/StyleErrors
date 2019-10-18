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
package org.eclipse.jubula.communication.internal.connection;


/**
 * @author BREDEX GmbH
 * @created Mar 26, 2010
 */
public final class RestartAutProtocol {

    /** the message requesting preparation for a restart */
    public static final String REQ_PREPARE_FOR_RESTART = "Req.PrepareForRestart"; //$NON-NLS-1$

    /** the message requesting preparation for a restart */
    public static final String REQ_RESTART = "Req.Restart"; //$NON-NLS-1$

    /**
     * Private constructor for utility class.
     */
    private RestartAutProtocol() {
        // Nothing to initialize
    }
    
}
