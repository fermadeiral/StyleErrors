/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
 * 
 * Message from Client to AUTAgent. AUTAgent should take a screenshot
 * 
 * @author Miklos Hartmann
 * @created Jun 24, 2016
 *
 */
public class TakeScreenshotAUTAgentMessage extends Message {

    /**
     * {@inheritDoc}
     */
    public String getCommandClass() {
        return CommandConstants.TAKE_SCREENSHOT_AUTAGENT_COMMAND;
    }

}
