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
package org.eclipse.jubula.communication.internal.message;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * This message is send from JubulaClient to AUTServer in order to start the
 * Inspector. <br>
 * 
 * @author BREDEX GmbH
 * @created 10.06.2009
 */
public class ActivateInspectorMessage extends Message {
    /**
     * Default constructor. Do nothing (required by Betwixt).
     */
    public ActivateInspectorMessage() {
        // Nothing to be done
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.ACTIVATE_INSPECTOR_COMMAND;
    }
}