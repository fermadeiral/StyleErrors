/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.util;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for creating screenshots
 * 
 * @author BREDEX GmbH
 * @created Jan 21, 2013
 */
public class LocalScreenshotUtil {
    /** Logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(LocalScreenshotUtil.class);

    /**
     * Private constructor for utility class.
     */
    private LocalScreenshotUtil() {
        // Nothing to initialize
    }

    /**
     * Creates an image containing pixels read from the screen.
     * 
     * @param screenRect
     *            Rect to capture in screen coordinates
     * @return The captured image; or <code>null</code> in case of any
     *         AWTExceptions
     */
    public static BufferedImage createScreenCapture(Rectangle screenRect) {
        try {
            return new Robot().createScreenCapture(screenRect);
        } catch (AWTException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getLocalizedMessage(), e);
            }
            return null;
        }
    }

    /**
     * Creates an image containing pixels read from the screen.
     * 
     * @return The captured image; or <code>null</code> in case of any
     *         AWTExceptions
     */
    public static BufferedImage createFullScreenCapture() {
        // Determine current screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);
        return createScreenCapture(screenRect);
    }
}
