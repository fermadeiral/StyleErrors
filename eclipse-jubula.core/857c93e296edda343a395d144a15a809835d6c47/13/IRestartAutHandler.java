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
package org.eclipse.jubula.autagent.common.agent;


/**
 * Sends the necessary messages to restart an AUT.
 *
 * @author BREDEX GmbH
 * @created Mar 26, 2010
 */
public interface IRestartAutHandler {
    /**
     * @param agent
     *            The AUT Agent with which the AUT to be restarted is
     *            registered. This may be used, for example, to stop the AUT.
     * @param timeout
     *            indicates whether the AUT should be forced to quit (timeout ==
     *            0) or whether the AUT should terminate by itself (timeout > 0)
     */
    public void restartAut(AutAgent agent, int timeout);
    
    /**
     * Get the class which was used to start the AUT
     * @return the class name of the class
     */
    public String getAUTStartClass();
}
