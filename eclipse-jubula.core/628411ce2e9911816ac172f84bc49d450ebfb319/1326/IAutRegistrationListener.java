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
package org.eclipse.jubula.client.core.agent;


/**
 * Handles events related to registering and deregistering an AUT with an 
 * AUT Agent.
 *
 * @author BREDEX GmbH
 * @created Jan 26, 2010
 */
public interface IAutRegistrationListener {

    /**
     * 
     * @param event Specific information regarding the registration event.
     */
    public void handleAutRegistration(AutRegistrationEvent event);
    
}
