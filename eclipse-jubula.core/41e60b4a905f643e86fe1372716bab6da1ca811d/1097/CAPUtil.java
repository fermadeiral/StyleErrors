/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester.util;

import java.awt.Point;
import java.util.StringTokenizer;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.driver.SwtRobot;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
/**
 * Utility class for some SWT specific commands.
 * 
 * @author BREDEX GmbH
 */
public class CAPUtil {
    /**
     * private constructor to prevent instantiation of utility class
     */
    private CAPUtil() { }    
    
    /**
     * Move the mouse pointer from its current position to a few points in
     * its proximity. This is used to initiate a drag operation.
     * 
     */
    public static void shakeMouse() {
        /** number of pixels by which a "mouse shake" offsets the mouse cursor */
        final int mouseShakeOffset = 10;
        
        Point origin = AUTServer.getInstance().getRobot()
                .getCurrentMousePosition();
        SwtRobot lowLevelRobot = new SwtRobot(Display.getDefault());
        lowLevelRobot.mouseMove(
                origin.x + mouseShakeOffset, 
                origin.y + mouseShakeOffset);
        lowLevelRobot.mouseMove(
                origin.x - mouseShakeOffset, 
                origin.y - mouseShakeOffset);
        lowLevelRobot.mouseMove(origin.x, origin.y);
        if (!EnvironmentUtils.isWindowsOS() 
                && !EnvironmentUtils.isMacOS()) {
            boolean moreEvents = true;
            while (moreEvents) {
                moreEvents = Display.getDefault().readAndDispatch();
            }
        }
    }
    
    /**
     * Presses or releases the given modifier.
     * @param modifier the modifier.
     * @param press if true, the modifier will be pressed.
     * if false, the modifier will be released.
     */
    public static void pressOrReleaseModifiers(String modifier, boolean press) {
        final IRobot robot = AUTServer.getInstance().getRobot();
        final StringTokenizer modTok = new StringTokenizer(
                KeyStrokeUtil.getModifierString(modifier), " "); //$NON-NLS-1$
        while (modTok.hasMoreTokens()) {
            final String mod = modTok.nextToken();
            final int keyCode = KeyCodeConverter.getKeyCode(mod);
            if (press) {
                robot.keyPress(null, keyCode);
            } else {
                robot.keyRelease(null, keyCode);
            }
        }
    }
    
    /**
     * This method is intended to be called from within an EventThreadQueuer
     * runnable.
     * 
     * @param widget
     *            the widget to get the text for
     * @param key
     *            the key to use for text retrieval via getData(...).
     * @param fallbackText
     *            the text to use in case that the given key
     * @return the string retrieved via the given key or the
     *         <code>fallbackText</code> if
     *         - if the given key is <code>null</code>
     *         - no data with the given key found or
     *         - object is not an instance of String
     */
    public static String getWidgetText(final Widget widget, final String key,
            final String fallbackText) {
        if (key != null) {
            Object o = widget.getData(key);
            if (o instanceof String) {
                return (String) o;
            }
        }
        return fallbackText;
    }
    
    /**
     * This method is intended to be called from within an EventThreadQueuer
     * runnable.
     * 
     * @param widget
     *            the widget to get the text for
     * @param fallbackText
     *            the fallback text to use
     * @return the string retrieved or the <code>fallbackText</code> if 
     *          - no text data is found or 
     *          - data object is not an instance of String
     */
    public static String getWidgetText(final Widget widget,
            final String fallbackText) {
        return getWidgetText(widget, SwtToolkitConstants.WIDGET_TEXT_KEY,
                fallbackText);
    }
}
