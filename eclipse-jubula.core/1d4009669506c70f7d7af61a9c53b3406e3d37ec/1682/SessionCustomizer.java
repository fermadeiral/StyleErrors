/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.persistence;

import org.eclipse.persistence.sessions.Session;

/**
 * @author BREDEX GmbH
 * @created 16.12.2011
 */
public class SessionCustomizer implements 
    org.eclipse.persistence.config.SessionCustomizer {
    
    /**
     * EclipseLink can be configured to use streams to store large binary data. 
     * This can improve the max size for reading/writing on some JDBC drivers.
     * @param session the session to optimize
     */
    public void customize(Session session) throws Exception {
        
        session.getLogin().setUsesStreamsForBinding(true);
 
    }
}
