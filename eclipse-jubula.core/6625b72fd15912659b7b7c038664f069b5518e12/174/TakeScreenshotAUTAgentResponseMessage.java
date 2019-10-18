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
import org.eclipse.jubula.tools.internal.serialisation.SerializedImage;

/**
 * The AUT Agent's response containing the screenshot
 * @author Miklos Hartmann
 * @created Jun 24, 2016
 */
public class TakeScreenshotAUTAgentResponseMessage extends Message {

    /** the screenshot */
    private SerializedImage m_screenshot = null;

    /** Default constructor. */
    public TakeScreenshotAUTAgentResponseMessage() {
        // empty
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.TAKE_SCREENSHOT_AUTAGENT_RESPONSE_COMMAND;
    }
    
    /**
     * @param screenshot
     *            the screenshot to set
     */
    public void setScreenshot(SerializedImage screenshot) {
        m_screenshot = screenshot;
    }

    /** @return the screenshot */
    public SerializedImage getScreenshot() {
        return m_screenshot;
    }
}
