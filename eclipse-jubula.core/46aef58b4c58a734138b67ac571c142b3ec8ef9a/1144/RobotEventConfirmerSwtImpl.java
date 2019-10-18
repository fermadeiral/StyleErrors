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
package org.eclipse.jubula.rc.swt.driver;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IEventMatcher;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.WorkaroundUtil;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


/**
 * <p> This event confirmer works on a class of SWT events defined by an
 * <code>InterceptorOptions</code> instance. The confirmer adds a Listener
 * to the SWT event queue 
 * using the <code>InterceptorOptions</code> event mask.</p>
 * 
 * <p> To confirm an event, call <code>waitToConfirm()</code>.</p>
 * 
 * @author BREDEX GmbH
 * @created 26.07.2006
 */
class RobotEventConfirmerSwtImpl implements IRobotEventConfirmer,
    Listener {
    
    /** The logger. */
    private static AutServerLogger log = new AutServerLogger(
            RobotEventConfirmerSwtImpl.class);
    
    /** Stores if the confirmer is enabled. */
    private boolean m_enabled = false;
    
    /** Stores if the confirmer is being waiting for an event to confirm. */
    private boolean m_waiting = false;
    
    /** The interceptor options. */
    private InterceptorOptions m_options;
    /** The graphics component on which the event occurs. */
    
    private Object m_eventTarget;
    
    /** The event matcher. */
    private IEventMatcher m_eventMatcher;
    
    /** Stores all events of a given class after the confirmer has been enabled. */
    private List<Event> m_eventList = new LinkedList<Event>();
    
    /**
     * Creates a new confirmer for a class of events defined by <code>options</code>. 
     * @param options The options.
     */
    RobotEventConfirmerSwtImpl(InterceptorOptions options) {
        m_options = options;
    }
    
    
    /**
     * Logs a list. 
     * @param list The list.
     */
    private void logList(List<Event> list) {
        log.debug("Stored SWTEvents["); //$NON-NLS-1$
        List<Event> copy = (List<Event>)((LinkedList<Event>)list).clone();
        for (Iterator<Event> it = copy.iterator(); it.hasNext();) {
            Object element = it.next();
            log.debug(element);
        }
        log.debug("]"); //$NON-NLS-1$
    }
    
    /**
     * Checks if the given event matches.
     * @param event The event.
     * @return <code>true</code> if the event matches, otherwise <code>false</code>.
     */
    private boolean isEventMatching(Event event) {
        if (log.isDebugEnabled()) {
            log.debug("SWTEvent matching?: " + event); //$NON-NLS-1$
            log.debug("Matching ID?      : " //$NON-NLS-1$
                + (event.type == m_eventMatcher.getEventId()));
            log.debug("Matching source?  : " //$NON-NLS-1$
                + (m_eventTarget == null 
                   || event.widget == m_eventTarget));
            log.debug("*Source: " + event.widget); //$NON-NLS-1$
            log.debug("*Target: " + m_eventTarget); //$NON-NLS-1$
        }
        return ((m_eventTarget == null
                 || matchComponent(m_eventTarget, event.widget)
                 || isInBounds(m_eventTarget, event.widget))
                && m_eventMatcher.isMatching(event));
    }
    
    /**
     * Determines whether one widget is completely contained within another.
     * 
     * @param eventTarget The "owning" widget.
     * @param eventWidget The "child" widget.
     * @return <code>true</code> if <code>eventWidget</code> is completely 
     *         contained within <code>boundsWidget</code>.
     */
    private boolean isInBounds(Object eventTarget, Widget eventWidget) {
        return SwtUtils.isInBounds(
            (Widget)eventTarget, eventWidget);
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
        return isComponentMatching(expComp, currComp, true);
    }
    
    /**
     * 
     * @param eventTarget a target for the expected event.
     * @return true if no EventMatching should be executed on the given
     * event target, false otherwise.
     */
    protected boolean isNoConfirmComponent(Object eventTarget) {
        // Menu and MenuItem: We receive no mouse events
        // Shell: We receive no mouse events under GTK when clicking/moving 
        //        in a location where there is no component (ex. an empty 
        //        editor pane in an RCP application).
        return eventTarget instanceof Menu 
                || eventTarget instanceof MenuItem
                || eventTarget instanceof Shell;
    }

    /**
     * Checks if the current component matches to the expected component.
     * If the current Component does not match, its parents will be checked
     * recursive.
     * @param expComp the expected Component
     * @param currComp the current component to check.
     * @param checkChildren should the children of the component be checked
     *                      if the component itself does not match?
     * @return true or false.
     */
    private boolean isComponentMatching(Object expComp, Object currComp, 
        boolean checkChildren) {
        
        if (log.isDebugEnabled()) {
            log.debug("Matching source? : " + (expComp == currComp)); //$NON-NLS-1$
        }
        if (expComp == currComp) {
            return true;
        }
        // if no matching, try to match children.
        boolean match = false;
        Widget curr = (Widget)currComp;
        final Widget[] widgetChildren = SwtUtils.getWidgetChildren(curr, true);
        if (checkChildren && widgetChildren.length > 0) {
            Widget[] children = widgetChildren;
            for (int i = 0; i < children.length; i++) {
                match = isComponentMatching(expComp, children[i]);
                if (match) {
                    return match;
                }
            }
        }

        /* corner case: subcomponents of a composite that should be treated
         * as its own component
         * See: SwtUtils.checkControlParent
         */
        if (currComp instanceof Control) {
            Control controlComp = (Control)currComp;
            Control parentControl = SwtUtils.checkControlParent(controlComp);
            if (controlComp != parentControl) {
                match = isComponentMatching(expComp, parentControl, false);
            }
            if (match) {
                return match;
            }
        }
        return match;
    }

    /**
     * Checks if one of the events stored into the given list matches.
     * @param eventList The list of events
     * @return <code>true</code> if one or more of the event matches, otherwise <code>false</code>.
     */
    private boolean isEventMatching(List<Event> eventList) {
        for (Iterator<Event> it = eventList.iterator(); it.hasNext();) {
            Event event = it.next();
            if (isEventMatching(event)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds the event to the event list.
     * @param event The event.
     */
    private void addEventToList(Event event) {
        synchronized (m_eventList) {
            ((LinkedList<Event>)m_eventList).addFirst(event);
        }
    }
    
    /**
     * Stops the waiting status. That means, that the current waiting thread is
     * being notified and the <code>m_waiting</code> property is set to
     * <code>false</code>.
     */
    private void stopWaiting() {
        synchronized (this) {
            m_waiting = false;
            notify();
        }
        if (log.isDebugEnabled()) {
            log.debug("Notified waiting thread"); //$NON-NLS-1$
        }
    }
    
    /**
     * Enables or disables the confirmer. If the confirmer is enabled, the AWT
     * listener is added to the SWT event queue so that the confirmer starts
     * storing events of the configured class of events. If it is disabled, the
     * listener is removed from the SWT event queue.
     * @param enabled <code>true</code> or <code>false</code>.
     */
    void setEnabled(final boolean enabled) {
        m_enabled = enabled;
        synchronized (m_eventList) {
            m_eventList.clear();
        }
        if (log.isDebugEnabled()) {
            log.debug("Enabled?    : " + enabled); //$NON-NLS-1$
            if (enabled) {
                log.debug("Storing SWTEvents with: " + m_options); //$NON-NLS-1$
            }
        }
        final long[] eventMask = m_options.getEventMask();
        final IEventThreadQueuer evThreadQueuer = 
            new EventThreadQueuerSwtImpl();
        evThreadQueuer.invokeAndWait("add-/removeDisplayFilters", //$NON-NLS-1$
            new IRunnable<Void>() { 
                public Void run() {
                    final Display autDisplay = ((SwtAUTServer)AUTServer
                        .getInstance()).getAutDisplay();
                    final int maskLength = eventMask.length;
                    for (int i = 0; i < maskLength; i++) {
                        if (enabled) {
                            autDisplay.addFilter((int)eventMask[i],
                                    RobotEventConfirmerSwtImpl.this);
                        } else {
                            autDisplay.removeFilter((int)eventMask[i],
                                RobotEventConfirmerSwtImpl.this);
                        }
                    }
                    return null;
                }
            });
    }
    
    /**
     * {@inheritDoc}
     */
    public void waitToConfirm(final Object eventTarget,
        IEventMatcher matcher, long timeout) throws RobotException {

        m_eventTarget = eventTarget;
        m_eventMatcher = matcher;
        // Put every code in this try-block! Otherwise it is not ensured
        // that this listener will ever be removed from the AUT!!!
        try {
            if (isNoConfirmComponent(eventTarget)) {
                return;
            }
            synchronized (m_eventList) {
                if (isEventMatching(m_eventList)) {
                    return;
                }
            }
            m_waiting = true;
            waitFor(timeout);
            if (m_waiting) {
                // I'm still waiting. This means that the event could not
                // be confirmed during the confirm time interval, that means
                // the event matcher didn't find a matching event.
                // But the event matcher may accept a different event, which has
                // already dispatched, as a fall back.
                boolean fallBackMatching;
                synchronized (m_eventList) {
                    fallBackMatching = m_eventMatcher
                        .isFallBackEventMatching(m_eventList, m_eventTarget);

                }
                if (log.isDebugEnabled()) {
                    if (!fallBackMatching) {
                        log.debug("Received timeout"); //$NON-NLS-1$
                        log.debug(m_options);
                        synchronized (m_eventList) {
                            logList(m_eventList);
                        }
                    } else {
                        log.debug("-> Fall back event has matched!"); //$NON-NLS-1$
                    }
                }
                if (!fallBackMatching && !WorkaroundUtil.isIgnoreTimeout()) {
                    throw new RobotException(
                        "Timeout received before confirming the posted event: " //$NON-NLS-1$
                        + m_eventMatcher.getEventId(), 
                        EventFactory.createActionError(
                                TestErrorEvent.CONFIRMATION_TIMEOUT));
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Got notification"); //$NON-NLS-1$
            }
        } finally {
            setEnabled(false);
        }
    }
    
    /**
     * waits for the event to arrive with a timeout
     * @param pTimeout timeout after this amount of ms
     */
    private void waitFor(long pTimeout) {
        synchronized (this) {
            long timeout = pTimeout;
            long done = System.currentTimeMillis() + timeout; 
            long now;
            do {
                try {
                    wait(timeout);
                } catch (InterruptedException e) {
                    // ignore
                }
                now = System.currentTimeMillis();
                timeout = done - now;
        
            } while (m_waiting && (timeout > 0));
        }
    }


    /**
     * {@inheritDoc}
     */
    public void handleEvent(final Event event) {
        if (!m_enabled) {
            return;
        }
        // !! Never block in the GUI thread, it may cause deadlocks!
        new Thread(new Runnable() {
            public void run() {
                try {
                    addEventToList(event);
                    synchronized (m_eventList) {
                        if (log.isDebugEnabled()) {
                            log.debug("SWTEvent    : " + event); //$NON-NLS-1$
                            log.debug("Event target: " + m_eventTarget); //$NON-NLS-1$
                            log.debug("Waiting?    : " + m_waiting); //$NON-NLS-1$
                            logList(m_eventList);
                        }
                        if (m_waiting && isEventMatching(m_eventList)) {
                            stopWaiting();
                        }
                    }
                } catch (Throwable t) {
                    log.error("exception in handleEvent", t); //$NON-NLS-1$
                }
            }
        }).start();
    }
    

    /**
     * {@inheritDoc}
     */
    public void waitToConfirm(Object eventTarget, 
        IEventMatcher matcher) throws RobotException {
        
        waitToConfirm(eventTarget, matcher, 
            RobotTiming.getEventConfirmTimeout());
    }
}