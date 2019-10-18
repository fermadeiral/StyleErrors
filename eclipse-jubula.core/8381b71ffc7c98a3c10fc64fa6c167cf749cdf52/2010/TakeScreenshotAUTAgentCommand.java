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
package org.eclipse.jubula.autagent.commands;


import org.apache.commons.lang.Validate;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotAUTAgentMessage;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotAUTAgentResponseMessage;
import org.eclipse.jubula.tools.internal.serialisation.SerializedImage;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.AWTException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Asks the AUTAgent to take a screenshot
 * 
 * @author Miklos Hartmann
 * @created Jun 24, 2016
 *
 */
public class TakeScreenshotAUTAgentCommand implements ICommand {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(TakeScreenshotAUTAgentCommand.class);

    /** the message for this command */
    private TakeScreenshotAUTAgentMessage m_message;
    
    /**
     * {@inheritDoc}
     */
    public TakeScreenshotAUTAgentMessage getMessage() {
        return m_message;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message) {
        Validate.isTrue(message instanceof TakeScreenshotAUTAgentMessage);
        m_message = (TakeScreenshotAUTAgentMessage)message;
    }

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        BufferedImage image;
        TakeScreenshotAUTAgentResponseMessage response =
                new TakeScreenshotAUTAgentResponseMessage();
        try {
            image = new Robot().createScreenCapture(new Rectangle(
                    Toolkit.getDefaultToolkit().getScreenSize()));
            final SerializedImage computedSerializeImage = SerializedImage
                    .computeSerializeImage(image);

            response.setScreenshot(computedSerializeImage);

        } catch (AWTException e) {
            LOG.error("Error occurred while trying to take screenshot.", e);
            response.setScreenshot(null);
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }
}