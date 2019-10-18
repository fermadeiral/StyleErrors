/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.driver;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.WeakEventHandler;
import javafx.scene.Scene;
import javafx.stage.Window;

import org.eclipse.jubula.rc.common.driver.IEventMatcher;
import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.WorkaroundUtil;
import org.eclipse.jubula.rc.javafx.components.ParentGetter;
import org.eclipse.jubula.rc.javafx.tester.util.JavaFXEventConverter;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * <p>
 * This event confirmer works on a class of AWT events defined by an
 * <code>InterceptorOptions</code> instance. The confirmer adds a
 * {@link java.awt.event.AWTEventListener} to the AWT event queue using the
 * <code>InterceptorOptions</code> event mask.
 * </p>
 * 
 * <p>
 * To confirm an event, call <code>waitToConfirm()</code>.
 * </p>
 * 
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
class RobotEventConfirmerJavaFXImpl implements IRobotEventConfirmer,
        EventHandler<Event> {

    /**
     * The logger.
     */
    private static AutServerLogger log = new AutServerLogger(
            RobotEventConfirmerJavaFXImpl.class);

    /**
     * Stores if the confirmer is enabled.
     */
    private volatile boolean m_enabled = false;
    /**
     * Stores if the confirmer is being waiting for an event to confirm.
     */
    private boolean m_waiting = false;
    /**
     * The interceptor options.
     */
    private volatile InterceptorOptions m_options;
    /**
     * Stores all events of a given class after the confirmer has been enabled.
     */
    private LinkedBlockingQueue<Event> m_eventList = 
            new LinkedBlockingQueue<Event>();

    /**
     * Stores Windows on wich Events could occur, this includes Popups such as
     * contextmenus
     */
    private LinkedBlockingQueue<ReadOnlyObjectProperty
            <? extends Window>> m_sceneGraphs;

    /**
     * Creates a new confirmer for a class of events defined by
     * <code>options</code>.
     * 
     * @param options
     *            The options.
     * @param sceneGraphs
     *            List with instances of Windows and their Scene-Graphs
     */
    protected RobotEventConfirmerJavaFXImpl(
            InterceptorOptions options,
            LinkedBlockingQueue<ReadOnlyObjectProperty
            <? extends Window>> sceneGraphs) {
        m_options = options;
        m_sceneGraphs = sceneGraphs;
    }

    @Override
    public void waitToConfirm(Object eventTarget, IEventMatcher matcher)
        throws RobotException {
        waitToConfirm(eventTarget, matcher,
                RobotTiming.getEventConfirmTimeout());
    }

    @Override
    public void waitToConfirm(Object eventTarget, IEventMatcher matcher,
            long pTimeout) throws RobotException {

        if (log.isDebugEnabled()) {
            log.debug("Waiting for EventID: " + String.valueOf(matcher) //$NON-NLS-1$
                    + " on Component: " + String.valueOf(eventTarget)); //$NON-NLS-1$
        }
        ArrayList<Event> history = new ArrayList<>();
        try {
            m_waiting = true;
            
            try {
                long timeout = pTimeout;
                long done = System.currentTimeMillis() + timeout;
                long now;
                do {
                    Event e = m_eventList.poll(timeout, TimeUnit.MILLISECONDS);
                    history.add(e);
                    if (isEventMatch(e, matcher, (EventTarget)eventTarget)) {
                        return;
                    }
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
                boolean fallBackMatching = matcher
                        .isFallBackEventMatching(history,
                                eventTarget);

                if (!fallBackMatching && !WorkaroundUtil.isIgnoreTimeout()) {
                    throw new RobotException(
                            "Timeout received before confirming the posted event: " //$NON-NLS-1$
                                    + matcher.getEventId(),
                            EventFactory
                                    .createActionError(TestErrorEvent.
                                            CONFIRMATION_TIMEOUT));
                }
            }

        } finally {
            setEnabled(false);
        }
    }

    /**
     * 
     * @param e The received event. May be <code>null</code>, in which case 
     *          <code>false</code> will be returned.
     * @param matcher The matcher to apply to the event.
     * @param expectedTarget The expected target (Graphics Component) of the 
     *                       event. May be <code>null</code>, in which case the 
     *                       event target is ignored.
     * @return <code>true</code> if the given event qualifies as a match 
     *         according to the <code>matcher</code> and 
     *         </code>expectedTarget</code>.
     */
    private boolean isEventMatch(
            Event e, IEventMatcher matcher, EventTarget expectedTarget) {

        return e != null 
                && isComponentMatch(expectedTarget, e.getTarget()) 
                && matcher.isMatching(e);
    }

    /**
     * 
     * @param expectedTarget The expected event target (Graphics Component). 
     *                       May be <code>null</code>, in which case 
     *                       <code>true</code> will be returned.
     * @param actualTarget The actual event target. May be <code>null</code>, 
     *                     in which case <code>false</code> will be returned 
     *                     <em>unless</em> <code>expectedTarget</code> is 
     *                     also <code>null</code>. 
     * @return <code>true</code> if <code>actualTarget</code> matches 
     *         <code>expectedTarget</code>. 
     */
    private boolean isComponentMatch(
            EventTarget expectedTarget, EventTarget actualTarget) {

        if (expectedTarget == null) {
            return true;
        }

        EventTarget currentExpectedTarget = expectedTarget;
        while (currentExpectedTarget != null) {
            if (currentExpectedTarget == actualTarget) {
                return true;
            }
            
            currentExpectedTarget = ParentGetter.get(currentExpectedTarget);
        }

        EventTarget currentActualTarget = actualTarget;
        while (currentActualTarget != null) {
            if (currentActualTarget == expectedTarget) {
                return true;
            }
            
            currentActualTarget = ParentGetter.get(currentActualTarget);
        }

        return false;
    }

    /**
     * Enables or disables the confirmer. If the confirmer is enabled, the
     * JavaFX Filter is added to the currently focused stage so that the
     * confirmer starts storing events of the configured class of events. If it
     * is disabled, the listener is removed from the AWT event queue.
     * 
     * @param enabled
     *            <code>true</code> or <code>false</code>.
     */
    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
        m_eventList.clear();
        if (m_enabled) {
            final long[] masks = m_options.getEventMask();
            for (int i = 0; i < masks.length; i++) {
                for (final ReadOnlyObjectProperty<? extends Window> w 
                        : m_sceneGraphs) {
                    if (w.getValue() == null || !(w.getValue().isShowing())) {
                        // Removing this property from the list because the
                        // window it belongs to is not present.
                        m_sceneGraphs.remove(w);
                        continue;
                    }
                    final Window win = w.get();
                    final long mask = masks[i];
                    final RobotEventConfirmerJavaFXImpl me = this;
                    EventThreadQueuerJavaFXImpl.invokeAndWait(
                            "Add EventFilter for conforming", //$NON-NLS-1$
                            new Callable<Void>() {
                                @Override
                                public Void call() throws Exception {
                                    win.addEventFilter(
                                            JavaFXEventConverter.awtToFX(mask),
                                            new WeakEventHandler<>(me));
                                    Scene s = win.getScene();
                                    if (s != null 
                                            && s.getFocusOwner() != null) {
                                        s.getFocusOwner().addEventFilter(
                                                JavaFXEventConverter
                                                        .awtToFX(mask),
                                                new WeakEventHandler<>(me));
                                    }
                                    return null;
                                }
                            });
                }
            }
        } else {
            long[] masks = m_options.getEventMask();
            for (int i = 0; i < masks.length; i++) {
                for (ReadOnlyObjectProperty<? extends Window> w 
                        : m_sceneGraphs) {
                    if (w.getValue() == null || !(w.getValue().isShowing())) {
                        // Removing this property from the list because the
                        // window it belongs to is not present.
                        m_sceneGraphs.remove(w);
                        continue;
                    }
                    final Window win = w.get();
                    final long mask = masks[i];
                    final RobotEventConfirmerJavaFXImpl me = this;
                    EventThreadQueuerJavaFXImpl.invokeAndWait(
                            "Remove EventFilter for conforming", //$NON-NLS-1$
                            new Callable<Void>() {
                                @Override
                                public Void call() throws Exception {
                                    win.removeEventFilter(
                                            JavaFXEventConverter.awtToFX(mask),
                                            new WeakEventHandler<>(me));
                                    Scene s = win.getScene();
                                    if (s != null 
                                            && s.getFocusOwner() != null) {
                                        s.getFocusOwner().removeEventFilter(
                                                JavaFXEventConverter
                                                        .awtToFX(mask),
                                                new WeakEventHandler<>(me));
                                    }
                                    return null;
                                }
                            });
                }
            }
        }
    }

    @Override
    public void handle(Event event) {
        try {
            m_eventList.put(event);
        } catch (InterruptedException e) {
            log.error("InterruptedException: " + event); //$NON-NLS-1$
        }
    }
}