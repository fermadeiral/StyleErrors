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
package org.eclipse.jubula.rc.common.commands;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;

import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotMessage;
import org.eclipse.jubula.communication.internal.message.TakeScreenshotResponseMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.RobotConfiguration;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.tools.internal.serialisation.SerializedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Apr 19, 2010
 */
public class TakeScreenshotCommand implements ICommand {
    /**
     * make sure line length % 2 != 0 so that the center of the square is the
     * actual pointing device position
     */
    private static final int TOTAL_LINE_LENGTH = 11;

    /** the distance from the center of the square to its bounds */
    private static final int LINE_LENGTH_PER_DIRECTION = 
            (TOTAL_LINE_LENGTH - 1) / 2;
    
    /** Logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(TakeScreenshotCommand.class);

    /** message */
    private TakeScreenshotMessage m_message;

    /**
     * {@inheritDoc}
     */
    public Message execute() {
        TakeScreenshotResponseMessage response = 
                new TakeScreenshotResponseMessage();
        IRobot robot = AUTServer.getInstance()
                .getRobot();
        final BufferedImage createScreenCapture = robot
                .createFullScreenCapture();
        try {
            Point currentPointingDevicePosition =
                    robot.getCurrentMousePosition();

            if (currentPointingDevicePosition != null) {
                final int pdX = currentPointingDevicePosition.x;
                final int pdY = currentPointingDevicePosition.y;

                final int xStart = pdX - LINE_LENGTH_PER_DIRECTION;
                final int yStart = pdY - LINE_LENGTH_PER_DIRECTION;

                for (int i = 0; i < TOTAL_LINE_LENGTH; i++) {
                    for (int j = 0; j < TOTAL_LINE_LENGTH; j++) {
                        invertPixelAtPoint(createScreenCapture, xStart + i,
                                yStart + j);
                    }
                }
            }
        } catch (Exception e) {
            // ignore so the mouse position will not be visible
        }
        if (RobotConfiguration.getInstance().isErrorHighlighting()) {
            WeakReference<IComponent> errorComponent = 
                    AUTServer.getInstance().getErrorComponent();
            if (errorComponent != null) {
                try {
                    highlightErrorComponent(errorComponent,
                            robot, createScreenCapture);
                } catch (RuntimeException e) {
                    LOG.error("Highlighting component during screenshot failed", e); //$NON-NLS-1$
                }
            }
        }
        
        final SerializedImage computedSerializeImage = SerializedImage
                .computeSerializeImage(createScreenCapture);
        response.setScreenshot(computedSerializeImage);
        return response;
    }

    /**
     * @param weakRefError weak reference to the component at which an error occurred
     * @param robot the robot
     * @param screenCapture the screeenshot that was taken
     */
    private void highlightErrorComponent(
            WeakReference<IComponent> weakRefError,
            IRobot robot, BufferedImage screenCapture) {
        IComponent errorComp = weakRefError.get();
        IRobot roboter = robot;
        if (errorComp != null) {
            BufferedImage image = screenCapture;
            Graphics2D graphic = image.createGraphics();
            Color recColor = new Color(0, 128, 0);
            graphic.setColor(recColor);
            graphic.setStroke(new BasicStroke(3));
            Rectangle recErrorComp = robot.getComponentBounds(errorComp);
            if (recErrorComp != null) {
                graphic.drawRect((int)recErrorComp.getX() - 2,
                        (int)recErrorComp.getY() - 2,
                        (int)recErrorComp.getWidth() + 2,
                        (int)recErrorComp.getHeight() + 2);
            }
        }
    }

    /**
     * If the given coordinates are outside of the image nothing will be
     * inverted
     * 
     * @param image
     *            the image to modify
     * @param x
     *            the coordinate
     * @param y
     *            the coordinate
     */
    private void invertPixelAtPoint(BufferedImage image, final int x,
            final int y) {
        if (x < 0 || y < 0) {
            return;
        }

        if (x >= image.getWidth() || y >= image.getHeight()) {
            return;
        }
        
        int rgb = image.getRGB(x, y);
        Color origPxColor = new Color(rgb);
        Color newPxColor = new Color(
                255 - origPxColor.getRed(),
                255 - origPxColor.getGreen(), 
                255 - origPxColor.getBlue());
        image.setRGB(x, y, newPxColor.getRGB());
    }
    
    /**
     * {@inheritDoc}
     */
    public void timeout() {
        LOG.error(this.getClass().getName() + ".timeout() called"); //$NON-NLS-1$
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(Message message) {
        m_message = (TakeScreenshotMessage)message;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return m_message;
    }
}
