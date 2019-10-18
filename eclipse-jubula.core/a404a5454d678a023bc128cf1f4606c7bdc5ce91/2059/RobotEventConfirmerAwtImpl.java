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
package org.eclipse.jubula.rc.swing.driver;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IEventMatcher;
import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.WorkaroundUtil;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * <p> This event confirmer works on a class of AWT events defined by an
 * <code>InterceptorOptions</code> instance. The confirmer adds a
 * {@link java.awt.event.AWTEventListener} to the AWT event queue 
 * using the <code>InterceptorOptions</code> event mask.</p>
 * 
 * <p> To confirm an event, call <code>waitToConfirm()</code>.</p>
 * 
 * @author BREDEX GmbH
 * @created 17.03.2005
 */
class RobotEventConfirmerAwtImpl implements IRobotEventConfirmer,
    AWTEventListener {
    
    /**
     * The logger.
     */
    private static AutServerLogger log = 
        new AutServerLogger(RobotEventConfirmerAwtImpl.class);
    
    /**
     * Stores if the confirmer is enabled.
     */
    private boolean m_enabled = false;
    /**
     * Stores if the confirmer is being waiting for an event to confirm.
     */
    private boolean m_waiting = false;
    /**
     * The interceptor options.
     */
    private InterceptorOptions m_options;
    /**
     * The graphics component on which the event occurs.
     */
    private Object m_eventTarget;
    /**
     * The event matcher.
     */
    private IEventMatcher m_eventMatcher;
    /**
     * Stores all events of a given class after the confirmer has been enabled.
     */
    private List m_eventList = new LinkedList();
    /**
     * Creates a new confirmer for a class of events defined by
     * <code>options</code>.
     * 
     * @param options
     *            The options.
     */
    RobotEventConfirmerAwtImpl(InterceptorOptions options) {
        m_options = options;
    }

    /**
     * Checks if the given event matches.
     * @param event the event.
     * @return <code>true</code> if the event matches, otherwise
     *         <code>false</code>.
     */
    private boolean isEventMatching(AWTEvent event) {
        final Object eventSource = event.getSource();
        return ((m_eventTarget == null
                   // we get no events while dragging so matchComponent(...)!
                   // for the drop-component does not work!
                || event.getID() == MouseEvent.MOUSE_DRAGGED
                || matchComponent(m_eventTarget, eventSource))
                    && m_eventMatcher.isMatching(event));
    }
    
    /**
     * Calls isComponentMatching(...) interchanging the parameters, 
     * so that the event-target-component and its children will be checked for
     * matching or the event-source-component and its children.
     * @param evTarget evTarget
     * @param evSource evSource
     * @return boolean
     */
    private boolean matchComponent(Object evTarget, Object evSource) {
        return (isComponentMatching(evTarget, evSource) 
            || isComponentMatching(evSource, evTarget));
    }
    
    /**
     * Checks if the current component matches to the expected component.
     * If the current Component does not match, its parents will be checked
     * recursive.
     * @param expComp the expected Component
     * @param currComp the current component to check.
     * @return true or false.
     */
    private boolean isComponentMatching(Object expComp, Object currComp) {
        if (expComp == currComp) {
            return true;
        }
        // if no matching, try to match children.
        boolean match = false;
        if (currComp instanceof Container) {
            Container curr = (Container)currComp;
            Component[] children = curr.getComponents();
            final int childLength = children.length;
            for (int i = 0; i < childLength; i++) {
                match = isComponentMatching(expComp, children[i]);
                if (match) {
                    return match;
                }
            }
        }
        return match;
    }
    
    
    /**
     * Checks if one of the events stored into the given list matches.
     * 
     * @param eventList
     *            The list of events
     * @return <code>true</code> if one or more of the event matches,
     *         otherwise <code>false</code>.
     */
    private synchronized boolean isEventMatching(List eventList) {
        for (Iterator it = eventList.iterator(); it.hasNext();) {
            AWTEvent event = (AWTEvent)it.next();
            if (isEventMatching(event)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Adds the event to the event list.
     * 
     * @param event
     *            The event.
     */
    private synchronized void addEventToList(AWTEvent event) {
        ((LinkedList)m_eventList).addFirst(event);
        if (log.isDebugEnabled()) {
            log.debug("Received event: " + String.valueOf(event.toString())); //$NON-NLS-1$
        }
    }
    /**
     * Stops the waiting status. That means, that the current waiting thread is
     * being notified and the <code>m_waiting</code> property is set to
     * <code>false</code>.
     */
    private synchronized void stopWaiting() {
        m_waiting = false;
        notify();
    }
    /**
     * {@inheritDoc}
     */
    public synchronized void eventDispatched(AWTEvent event) {
        if (!m_enabled) {
            return;
        }
        
        addEventToList(event);
        if (m_waiting && isEventMatching(m_eventList)) {
            stopWaiting();
        }
    }
    /**
     * Enables or disables the confirmer. If the confirmer is enabled, the AWT
     * listener is added to the AWT event queue so that the confirmer starts
     * storing events of the configured class of events. If it is disabled, the
     * listener is removed from the AWT event queue.
     * 
     * @param enabled
     *            <code>true</code> or <code>false</code>.
     */
    void setEnabled(boolean enabled) {
        m_enabled = enabled;
        m_eventList.clear();
        if (enabled) {
            for (int i = 0; i < m_options.getEventMask().length; i++) {
                Toolkit.getDefaultToolkit().addAWTEventListener(this,
                    m_options.getEventMask()[i]);
            }
        } else {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        }
    }
    /**
     * {@inheritDoc}
     */
    public synchronized void waitToConfirm(Object eventTarget,
        IEventMatcher matcher, long pTimeout) throws RobotException {

        if (DragAndDropHelper.getInstance().isDragMode()) {
            setEnabled(false);
            return; // With a pressed mouse button, we get no events!
        }
        m_eventTarget = eventTarget;
        m_eventMatcher = matcher;
        
        if (log.isDebugEnabled()) {
            log.debug("Waiting for EventID: " + String.valueOf(matcher)  //$NON-NLS-1$
                + " on Component: " + String.valueOf(m_eventTarget)); //$NON-NLS-1$
        }
        
        try {
            if (isEventMatching(m_eventList)) {
                return;
            }
    
            if (EventQueue.isDispatchThread()) {
                throw new IllegalThreadStateException();
            }
            m_waiting = true;
    
            try {
                long timeout = pTimeout;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                do {
                    wait(pTimeout);
                    now = System.currentTimeMillis();
                    timeout = done - now;
        
                } while (m_waiting && (timeout > 0));
            } catch (InterruptedException e) {
                throw new RobotException(e);
            }
            if (m_waiting) {
                // I'm still waiting. This means that the event could not
                // be confirmed during the confirm time interval, that means
                // the event matcher didn't find a matching event.
                // But the event matcher may accept a different event, which has
                // already dispatched, as a fall back.
                boolean fallBackMatching = m_eventMatcher
                    .isFallBackEventMatching(m_eventList, m_eventTarget);

                if (!fallBackMatching && !WorkaroundUtil.isIgnoreTimeout()) {
                    throw new RobotException(
                        "Timeout received before confirming the posted event: " //$NON-NLS-1$
                        + m_eventMatcher.getEventId(), 
                        EventFactory.createActionError(
                                TestErrorEvent.CONFIRMATION_TIMEOUT));
                }
            }
            
        } finally {
            setEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void waitToConfirm(Object eventTarget, IEventMatcher matcher) 
        throws RobotException {
        
        waitToConfirm(eventTarget, matcher, 
                RobotTiming.getEventConfirmTimeout());
    }
}