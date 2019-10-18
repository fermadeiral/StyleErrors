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
package org.eclipse.jubula.client.core.events;

import java.util.EventListener;

/**
 * The interface for listening to events concerning the AUTServer.
 *
 * @author BREDEX GmbH
 * @created 13.08.2004
 */
public interface IAUTServerEventListener extends EventListener {
    /**
     * This method will be called when the state of the Server changes. The
     * event contains detailed information about the changes.
     * 
     * @param event -
     *            the detailed event
     */
    public void stateChanged(AUTServerEvent event);
}
