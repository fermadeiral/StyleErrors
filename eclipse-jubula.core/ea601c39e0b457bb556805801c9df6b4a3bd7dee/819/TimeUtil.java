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
package org.eclipse.jubula.tools.internal.utils;

import java.util.Date;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * Class utility for all things around time, delays, timestamps, ...
 *
 * @author BREDEX GmbH
 * @created 18.02.2008
 */
public abstract class TimeUtil {
    /**
     * Hidden for class utility
     */
    private TimeUtil() {
        // just hide the constructor
    }
    /**
     * Delay for a certain amount of time. 
     * The delay will handle InterruptedException
     * by restarting the Thread.sleep(), thus guaranteeing to wait for
     * "delayInMs" milliseconds
     * @param delayInMs the delay in ms, must not be negative
     * @return the passed delay
     */
    public static long delay(final long delayInMs) {
        if (delayInMs == 0) {
            return delayInMs;
        }
        if (delayInMs < 0) {
            throw new IllegalArgumentException("delay has to be positive"); //$NON-NLS-1$
        }
        long endTime = new Date().getTime() + delayInMs;
        long delay = delayInMs;
        do {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // just ignore any interruption
            }
            delay = endTime - (new Date().getTime());
        } while (delay > 0);
        return delayInMs;
    }
    
    /**
     * Delay for a certain amount of time. Uses if set the given keys property
     * value as a delay
     * 
     * @param defaultDelay
     *            the delay in ms, must not be negative
     * @param delayPropertyKey
     *            the property key; can be used to override / allow external
     *            setting of amount of delay
     */
    public static void delayDefaultOrExternalTime(final long defaultDelay,
        final String delayPropertyKey) {
        long timeToWait = defaultDelay;
        try {
            String value = EnvironmentUtils
                    .getProcessOrSystemProperty(delayPropertyKey);
            timeToWait = Long.valueOf(value).longValue();
        } catch (NumberFormatException e) {
            // ignore invalid formatted values and use default instead
        }
        TimeUtil.delay(timeToWait);
    }
    
    /**
     * @param startTime The start time.
     * @param endTime The end time.
     * @return a String representation of the difference between the provided 
     *         times.
     */
    public static String getDurationString(Date startTime, Date endTime) {
        long timeInSeconds = endTime.getTime() - startTime.getTime();
        timeInSeconds = timeInSeconds / 1000;
        long hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        String secondsString = (seconds < 10) ? "0" + seconds : String.valueOf(seconds); //$NON-NLS-1$ 
        String minutesString = (minutes < 10) ? "0" + minutes : String.valueOf(minutes); //$NON-NLS-1$ 
        String hoursString = (hours < 10) ? "0" + hours : String.valueOf(hours); //$NON-NLS-1$ 
        return hoursString + StringConstants.COLON + minutesString 
            + StringConstants.COLON + secondsString;
    }
}
