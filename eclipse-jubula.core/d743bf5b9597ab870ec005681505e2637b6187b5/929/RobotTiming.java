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
package org.eclipse.jubula.rc.common.driver;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsServer;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;


/**
 * This class bundles all Robot timing related issues
 *
 * @author BREDEX GmbH
 * @created Oct 4, 2010
 */
public class RobotTiming extends TimingConstantsServer {
    /** <code>EXTERNAL_PROPERTY_NAME_PRE_MOUSE_UP_DELAY</code> */
    private static final String EXTERNAL_PROPERTY_NAME_PRE_MOUSE_UP_DELAY = "TEST_DELAY_PRE_MOUSE_UP"; //$NON-NLS-1$

    /** <code>EXTERNAL_PROPERTY_NAME_POST_MOUSE_UP_DELAY</code> */
    private static final String EXTERNAL_PROPERTY_NAME_POST_MOUSE_UP_DELAY = "TEST_DELAY_POST_MOUSE_UP"; //$NON-NLS-1$

    /** <code>EXTERNAL_PROPERTY_NAME_MAX_AUT_RESPONSE_TIME</code> */
    private static final String EXTERNAL_PROPERTY_NAME_MAX_AUT_RESPONSE_TIME = "TEST_MAX_AUT_RESPONSE_TIME"; //$NON-NLS-1$
    
    /** <code>EXTERNAL_PROPERTY_NAME_KEY_INPUT_POST_DELAY</code> */
    private static final String EXTERNAL_PROPERTY_NAME_KEY_INPUT_POST_DELAY = "TEST_KEY_INPUT_POST_DELAY"; //$NON-NLS-1$

    /** <code>NO_EXTERNAL_WAIT</code> */
    private static final int NO_EXTERNAL_WAIT = -1;

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            RobotTiming.class);

    /**
     * The mouse click delay. Before a click (single, double etc.) is being
     * performed by the Robot, the current thread sleeps this delay amount of
     * time.
     */
    private static int preClickDelay = -1;

    /**
     * The waiting time between mouse down and mouse up
     */
    private static int postMouseDown = -1;

    /**
     * max AUT response time
     */
    private static int maxAUTresponseTime = -1;
    
    /**
     * The waiting time after mouse up
     */
    private static int postMouseUp = -1;

    /**
     * The timeout used by the event confirmer.
     */
    private static int eventConfirmTimeout = -1;
    
    /**
     * The timestamp of the most recently performed wait
     */
    private static long lastPerformedClick = -1;

    /**
     * The delay between key input
     */
    private static int keyInputPostDelay = -1;

    /**
     * Constructor
     */
    private RobotTiming() {
    // to hide default constructor
    }

    /**
     * @return the EventConfirmTimeout
     */
    public static final int getEventConfirmTimeout() {
        if (eventConfirmTimeout >= 0) {
            return eventConfirmTimeout;
        }

        eventConfirmTimeout = 
            TimeoutConstants.SERVER_TIMEOUT_EVENTCONFIRM_DEFAULT;
        return eventConfirmTimeout;
    }

    /**
     * @return the pre click delay
     */
    private static final int getPreClickDelay() {
        if (preClickDelay >= 0) {
            return preClickDelay;
        }
        
        int systemDoubleClickIntervall = EnvironmentUtils
                .getPlatformDoubleClickSpeed();
        if (systemDoubleClickIntervall > 0) {
            preClickDelay = systemDoubleClickIntervall
                    + DEFAULT_DELAY_PRE_CLICK_INCREMENT;
        } else {
            preClickDelay = DEFAULT_DELAY_PRE_CLICK;
        }
        
        return preClickDelay;
    }

    /**
     * @return The waiting time between mouse press and mouse release.
     */
    private static final int getPostMouseDownDelay() {
        if (postMouseDown >= 0) {
            return postMouseDown;
        }

        int externalWait = getExternalWait(
                EXTERNAL_PROPERTY_NAME_PRE_MOUSE_UP_DELAY);
        if (externalWait != NO_EXTERNAL_WAIT) {
            postMouseDown = externalWait;
            return postMouseDown;
        }

        // default
        postMouseDown = DEFAULT_DELAY_POST_MOUSE_DOWN;
        return postMouseDown;
    }

    /**
     * @return the maximum amount of time the AUT might *hang* / be unresponsive
     */
    public static final int getMaxAUTResponseInterval() {
        if (maxAUTresponseTime >= 0) {
            return maxAUTresponseTime;
        }

        int externalWait = getExternalWait(
                EXTERNAL_PROPERTY_NAME_MAX_AUT_RESPONSE_TIME);
        if (externalWait != NO_EXTERNAL_WAIT) {
            maxAUTresponseTime = externalWait;
            return maxAUTresponseTime;
        }

        // default
        maxAUTresponseTime = DEFAULT_MAX_AUT_RESPONSE_TIME;
        return maxAUTresponseTime;
    }
    
    /**
     * @return The waiting time after mouse up.
     */
    private static final int getPostMouseUpDelay() {
        if (postMouseUp >= 0) {
            return postMouseUp;
        }

        int externalWait = getExternalWait(
                EXTERNAL_PROPERTY_NAME_POST_MOUSE_UP_DELAY);
        if (externalWait != NO_EXTERNAL_WAIT) {
            postMouseUp = externalWait;
            return postMouseUp;
        }

        // default
        postMouseUp = DEFAULT_DELAY_POST_MOUSE_UP;
        return postMouseUp;
    }

    /**
     * @return The delay between key input
     */
    public static final int getKeyInputPostDelay() {
        if (keyInputPostDelay >= 0) {
            return keyInputPostDelay;
        }

        int externalWait = getExternalWait(
                EXTERNAL_PROPERTY_NAME_KEY_INPUT_POST_DELAY);
        if (externalWait != NO_EXTERNAL_WAIT) {
            keyInputPostDelay = externalWait;
            return keyInputPostDelay;
        }

        // default
        keyInputPostDelay = DEFAULT_KEY_INPUT_POST_DELAY;
        return postMouseUp;
    }

    /**
     * @param propertyName
     *            the name of the external property
     * @return the external wait time or -1 if no such
     */
    private static final int getExternalWait(String propertyName) {
        int wait = NO_EXTERNAL_WAIT;
        String delay = EnvironmentUtils
            .getProcessOrSystemProperty(propertyName);
        if (delay != null) {
            try {
                wait = new Integer(delay).intValue();
            } catch (NumberFormatException e) {
                log.warn("Error while parsing external process / system property: " //$NON-NLS-1$
                        + propertyName
                        + "=" + delay + ". Integer value has been expected", e); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return wait;
    }

    /**
     * sets the click delay
     * 
     * @param delay
     *            the delay before a click
     */
    public static final void setPreClickDelay(int delay) {
        preClickDelay = delay;
    }

    /**
     * Sets the EventConfirmTimeout in milliseconds. Default is 2000. <br>
     * If timeout is < 0, timeout is set to default 2000 milliseconds. <br>
     * <b>Note:</b> This is the timeout the event confirmer waits for a
     * confirmation for a sent event. If the timeout is set too short, the
     * tests could get inexecutable!
     * 
     * @param timeout
     *            the timeout to set.
     */
    public static final void setEventConfirmTimeout(int timeout) {
        if (timeout < 0) {
            eventConfirmTimeout = 
                TimeoutConstants.SERVER_TIMEOUT_EVENTCONFIRM_DEFAULT;
        } else {
            eventConfirmTimeout = timeout;
        }
    }

    /**
     * this method sleeps the pre configured system double click time + 50 to
     * avoid unwanted double clicks during test execution
     */
    public static final void sleepPreClickDelay() {
        long pcd = getPreClickDelay();
        long timeDiff = System.currentTimeMillis() - lastPerformedClick;
        if (timeDiff < pcd) {
            // wait only the required time difference
            pcd = pcd - timeDiff;
            delay(pcd);
        }
    }

    /**
     * sleeps in current thread for the configured pre click delay amount of
     * time
     */
    public static final void sleepPostMouseDownDelay() {
        delay(getPostMouseDownDelay());
        lastPerformedClick = System.currentTimeMillis();
    }

    /**
     * sleeps in current thread for the configured post click delay amount of
     * time
     */
    public static final void sleepPostMouseUpDelay() {
        delay(getPostMouseUpDelay());
    }

    /**
     * sleep the pre show popup delay
     */
    public static final void sleepPreShowPopupDelay() {
        delay(PRE_SHOW_POPUP_DELAY);
    }
    
    /**
     * sleep the post show popup delay
     * 
     * @param lookAndFeelSpecific
     *            a look and feel specific delay
     */
    public static final void sleepPostShowSubMenuItem(int lookAndFeelSpecific) {
        delay(lookAndFeelSpecific);
        delay(POST_SHOW_SUB_MENU_DELAY);
    }
    
    /**
     * sleep the wait for component polling delay
     */
    public static final void sleepWaitForComponentPollingDelay() {
        delay(POLLING_DELAY_WAIT_FOR_COMPONENT);
    }
    
    /**
     * sleep the post window activation delay
     */
    public static final void sleepPostWindowActivationDelay() {
        delay(POST_WINDOW_ACTIVATION_DELAY);
    }
    
    /**
     * ignores all InterruptedExceptions
     * 
     * @param timeInMilliSecs
     *            the time to sleep
     */
    private static final void delay(long timeInMilliSecs) {
        TimeUtil.delay(timeInMilliSecs);
    }
}