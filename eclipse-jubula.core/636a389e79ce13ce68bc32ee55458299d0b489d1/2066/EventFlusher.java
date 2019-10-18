/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swing.driver;

import java.awt.Robot;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is based on the code snippet posted on
 * http://stackoverflow.com/questions/11042979/does-java-awt-robot-waitforidle-wait-for-events-to-be-dispatched
 */
public class EventFlusher {
    /** logger */
    private static final Logger LOG = 
            LoggerFactory.getLogger(EventFlusher.class);
    /** the toolkit class name */
    private static final String TOOLKIT_CLASS_NAME = "sun.awt.SunToolkit";  //$NON-NLS-1$
    /** the native sync queue method */
    private Method m_syncNativeQueue;
    /** indicates whether the method zero arguments (Java 6) or not (Java 7) */
    private boolean m_isSyncNativeQueueZeroArguments;
    /** the robot to use */
    private final Robot m_robot;
    /** the flush timeout to use */
    private final long m_flushTimeout;
    /**
     * indicates whether the default toolkit is compatible to the required
     * toolkit implementation for native event flushing
     */ 
    private boolean m_isCompatibleToolkit = false;
    
    /**
     * Constructor
     * 
     * @param robot
     *            the robot
     * @param flushTimeout
     *            the flush timeout
     */
    public EventFlusher(Robot robot, long flushTimeout) {
        m_robot = robot;
        m_flushTimeout = flushTimeout;
        m_syncNativeQueue = null;
        m_isSyncNativeQueueZeroArguments = true;
        try {
            Class sunToolkitClass = Class.forName(TOOLKIT_CLASS_NAME);

            if (sunToolkitClass.isAssignableFrom(Toolkit
                    .getDefaultToolkit().getClass())) {
                m_isCompatibleToolkit = true;
            }
            
            // Since it's a protected method, we have to iterate over declared
            // methods and setAccessible.
            Method[] methods = sunToolkitClass.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                String name = method.getName();
                if ("syncNativeQueue".equals(name)) { //$NON-NLS-1$
                    List parameterTypes = Arrays.asList(
                            method.getParameterTypes());
                    if (Arrays.asList(new Object[] { long.class })
                            .equals(parameterTypes)) {
                        m_isSyncNativeQueueZeroArguments = false;
                    } else if (parameterTypes.isEmpty() 
                            && null == m_syncNativeQueue) {
                        m_isSyncNativeQueueZeroArguments = true;
                    } else {
                        continue;
                    }
                    m_syncNativeQueue = method;
                    m_syncNativeQueue.setAccessible(true);
                }
            }
        } catch (SecurityException e) {
            throw new RobotException(e);
        } catch (ClassNotFoundException e) {
            throw new RobotException(e);
        }
    }

    /**
     * Block until Swing has dispatched events caused by the Robot or user.
     * 
     * <p>
     * It is based on {@link SunToolkit#realSync()}. Use that method if you want
     * to try to wait for everything to settle down (e.g. if an event listener
     * calls {@link java.awt.Component#requestFocus()},
     * {@link SwingUtilities#invokeLater(Runnable)}, or
     * {@link javax.swing.Timer}, realSync will block until all of those are
     * done, or throw exception after trying). The disadvantage of realSync is
     * that it throws {@link SunToolkit.InfiniteLoop} when the queues don't
     * become idle after 20 tries.
     * 
     * <p>
     * Use this method if you only want to wait until the direct event listeners
     * have been called. For example, if you need to simulate a user click
     * followed by a stream input, then you can ensure that they will reach the
     * program under test in the right order:
     * 
     * <pre>
     * robot.mousePress(InputEvent.BUTTON1);
     * EventFlusher.flush();
     * writer.write(&quot;done with press&quot;);
     * </pre>
     * 
     * @see {@link java.awt.Robot#waitForIdle()} is no good; does not wait for
     *      OS input events to get to the Java process.
     * @see {@link SunToolkit#realSync()} tries 20 times to wait for queues to
     *      settle and then throws exception. In contrast, flushInputEvents does
     *      not wait for queues to settle, just to flush what's already on them
     *      once.
     * @see {@link java.awt.Toolkit#sync()} flushes graphics pipeline but not
     *      input events.
     */
    public void flush() {
        // 1) SunToolkit.syncNativeQueue: block until the operating system
        // delivers Robot or user events to the process.
        if (m_isCompatibleToolkit && m_syncNativeQueue != null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            try {
                if (m_isSyncNativeQueueZeroArguments) {
                    // java 1.6
                    m_syncNativeQueue.invoke(toolkit,
                            ArrayUtils.EMPTY_OBJECT_ARRAY);
                } else {
                    // java 1.7
                    m_syncNativeQueue.invoke(toolkit, 
                            new Object[] { m_flushTimeout });
                }
            } catch (IllegalArgumentException e) {
                LOG.error("Error occurred while invoking syncNativeQueue.", e); //$NON-NLS-1$
            } catch (IllegalAccessException e) {
                LOG.error("Error occurred while invoking syncNativeQueue.", e); //$NON-NLS-1$
            } catch (InvocationTargetException e) {
                LOG.error("Error occurred while invoking syncNativeQueue.", e); //$NON-NLS-1$
            }
        }

        // 2) SunToolkit.flushPendingEvents: block until the Toolkit thread
        // (aka AWT-XAWT, AWT-AppKit, or AWT-Windows) delivers enqueued events
        // to the EventQueue 
        //
        //                                  +
        //
        // 3) SwingUtilities.invokeAndWait: block until the Swing thread (aka
        // AWT-EventQueue-0) has dispatched all the enqueued input events.
        m_robot.waitForIdle();
    }
}
