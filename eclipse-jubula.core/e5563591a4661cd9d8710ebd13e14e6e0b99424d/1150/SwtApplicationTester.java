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
package org.eclipse.jubula.rc.swt.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.AbstractApplicationTester;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.common.util.WorkaroundUtil;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.components.SwtComponent;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;
import org.eclipse.jubula.rc.swt.listener.ComponentHandler;
import org.eclipse.jubula.rc.swt.listener.FocusTracker;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.tester.util.EventListener;
import org.eclipse.jubula.rc.swt.tester.util.EventListener.Condition;
import org.eclipse.jubula.rc.swt.utils.SwtPointUtil;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * @author BREDEX GmbH
 */
public class SwtApplicationTester extends AbstractApplicationTester {

    /** The logging. */
    private static AutServerLogger log = 
        new AutServerLogger(SwtApplicationTester.class);
    
    /**
     * This condition is true if the event source is a Shell with a matching
     * title.
     *
     * @author BREDEX GmbH
     * @created Jun 17, 2009
     */
    private static class WindowEventCondition implements Condition {

        /** the expected window title */
        private String m_windowTitle;
        
        /** the operator used for matching the window title */
        private String m_matchingOperator;

        /** 
         * determines whether the event source being disposed should be 
         * treated as a match 
         */
        private boolean m_valForDisposed;
        
        /**
         * Constructor
         * 
         * @param windowTitle The expected window title.
         * @param matchingOperator The operator used for matching the
         *                         window title.
         * @param valForDisposed Whether the event source being disposed
         *                       should be treated as a match.
         */
        public WindowEventCondition(String windowTitle, 
                String matchingOperator, boolean valForDisposed) {
            m_windowTitle = windowTitle;
            m_matchingOperator = matchingOperator;
            m_valForDisposed = valForDisposed;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isTrue(Event event) {
            if (event.widget instanceof Shell) {
                Shell window = (Shell)event.widget;
                if (window.isDisposed()) {
                    return m_valForDisposed;
                }
                String windowText = CAPUtil.getWidgetText(
                        window, window.getText());
                return MatchUtil.getInstance().match(windowText, 
                        m_windowTitle, m_matchingOperator);

            }

            return false;
        }
        
    }
    
    /** The Robot factory. */
    private IRobotFactory m_robotFactory;
    
    /**
     * @return The Robot factory instance
     */
    private IRobotFactory getRobotFactory() {
        if (m_robotFactory == null) {
            m_robotFactory = new RobotFactoryConfig().getRobotFactory();
        }
        return m_robotFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     * Waits <code>timeMillSec</code> if the application opens a window with the given title.
     * @param title the title
     * @param operator the comparing operator
     * @param timeout the time in ms
     * @param delay delay after the window is shown
     */
    public void rcWaitForWindow(final String title, final String operator, 
        int timeout, int delay) {

        final EventListener.Condition cond = 
            new WindowEventCondition(title, operator, false);
        final EventLock lock = new EventLock();
        final Listener listener = new EventListener(lock, cond);
        final Display display = 
            ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addWindowOpenedListeners", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() {
                display.addFilter(SWT.Activate, listener);
                display.addFilter(SWT.Show, listener);
                if (isWindowOpen(title, operator)) {
                    lock.release();
                }
                
                return null;
            }
        });

        try {
            synchronized (lock) {
                long currentTimeout = timeout;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (currentTimeout > 0)) {
                    try {
                        lock.wait(currentTimeout);                    
                        now = System.currentTimeMillis();
                        currentTimeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }                    
            }
        } finally {
            queuer.invokeAndWait("removeWindowOpenedListeners", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    display.removeFilter(SWT.Activate, listener);
                    display.removeFilter(SWT.Show, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("window did not open", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.TIMEOUT_EXPIRED));
        }
        TimeUtil.delay(delay);
    }
    
    /**
     * Waits <code>timeMillSec</code> if the application activates a window
     * with the given title.
     * 
     * @param title the title
     * @param operator the comparing operator
     * @param timeout the time in ms
     * @param delay delay after the window is activated
     */
    public void rcWaitForWindowActivation(final String title, 
            final String operator, final int timeout, int delay) {
        
        final EventListener.Condition cond = 
            new WindowEventCondition(title, operator, false);
        final EventLock lock = new EventLock();
        final Listener listener = new EventListener(lock, cond);
        final Display display = 
            ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addWindowActiveListeners", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() {
                display.addFilter(SWT.Activate, listener);
                if (isWindowActive(title, operator)) {
                    lock.release();
                }

                return null;
            }
        });
        
        try {
            synchronized (lock) {
                long currentTimeout = timeout;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (currentTimeout > 0)) {
                    try {
                        lock.wait(currentTimeout);                    
                        now = System.currentTimeMillis();
                        currentTimeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }                    
            }
        } finally {
            queuer.invokeAndWait("removeWindowActiveListeners", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    display.removeFilter(SWT.Activate, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("window was not activated", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.TIMEOUT_EXPIRED));
        }
        TimeUtil.delay(delay);
    }

    /**
     * Waits <code>timeMillSec</code> if the application closes (or hides) 
     * a window with the given title. If no window with the given title can
     * be found, then it is assumed that the window has already closed.
     * 
     * @param title the title
     * @param operator the comparing operator
     * @param timeout the time in ms
     * @param delay delay after the window is activated
     */
    public void rcWaitForWindowToClose(final String title, 
            final String operator, int timeout, int delay) {

        final EventListener.Condition cond = 
            new WindowEventCondition(title, operator, true);
        final EventLock lock = new EventLock();
        final Listener listener = new EventListener(lock, cond);
        final Display display = 
            ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addWindowClosedListeners", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() {
                display.addFilter(SWT.Close, listener);
                display.addFilter(SWT.Hide, listener);
                display.addFilter(SWT.Dispose, listener);
                if (!isWindowOpen(title, operator)) {
                    lock.release();
                }
                
                return null;
            }
        });

        try {
            synchronized (lock) {
                long currentTimeout = timeout;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (currentTimeout > 0)) {
                    try {
                        lock.wait(currentTimeout);                    
                        now = System.currentTimeMillis();
                        currentTimeout = done - now;
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }                    
            }
        } finally {
            queuer.invokeAndWait("removeWindowClosedListeners", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    display.removeFilter(SWT.Close, listener);
                    display.removeFilter(SWT.Hide, listener);
                    display.removeFilter(SWT.Dispose, listener);
                    
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("window did not close", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.TIMEOUT_EXPIRED));
        }
        TimeUtil.delay(delay);
    }

    /**
     * Returns <code>true</code> if a window with the given title is open and 
     * visible
     * 
     * @param title the title
     * @param operator the matches/equals operator
     * @return if the window is open and visible
     */
    private boolean isWindowOpen(final String title, final String operator) {
        boolean wasInterrupted = false;
        boolean equal = false;
        do {
            try {
                wasInterrupted = false;
                Collection components = ComponentHandler
                    .getAutHierarchy().getHierarchyMap()
                        .keySet();
                for (Iterator it = components.iterator(); it.hasNext();) {
                
                    Widget comp = ((SwtComponent)it.next()).getComponent();
                    if (comp instanceof Shell 
                            && !comp.isDisposed()
                            && ((Shell)comp).isVisible()) {

                        Shell frame = (Shell)comp;
                        if (MatchUtil.getInstance().match(
                                CAPUtil.getWidgetText(frame, frame.getText()),
                                title, operator)) {

                            equal = true;
                            break;
                        }
                    }
                }

            } catch (ConcurrentModificationException e) {
                log.debug("hierarchy modified while traversing", e); //$NON-NLS-1$
                wasInterrupted = true;
            }
        } while (wasInterrupted);
        return equal;
    }

    /**
     * Checks for the existence of a window with the given title
     * 
     * @param title
     *            the title
     * @param operator
     *            the comparing operator
     * @param exists
     *            <code>True</code> if the component is expected to exist and be
     *            visible, otherwise <code>false</code>.
     * @param timeout the amount of time to wait for the existence of the
     *          window to be checked
     */
    public void rcCheckExistenceOfWindow(final String title,
            final String operator, final boolean exists, int timeout) {
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        invokeAndWait("rcCheckExistenceOfWindow", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                Boolean windowExists = queuer.invokeAndWait(
                        "isWindowOpen", new IRunnable<Boolean>() { //$NON-NLS-1$
                            public Boolean run() throws StepExecutionException {
                                return isWindowOpen(title, operator);
                            }
                        });
                Verifier.equals(exists, windowExists.booleanValue());
            }
        });
        
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getActiveWindowBounds() {
        org.eclipse.swt.graphics.Rectangle activeWindowSize = 
            getRobotFactory()
                .getEventThreadQueuer().invokeAndWait(
                    this.getClass().getName() + ".getActiveWindowBounds", //$NON-NLS-1$
                        new IRunnable<org.eclipse.swt.graphics.Rectangle>() {
                            // SYNCH THREAD START
                            public org.eclipse.swt.graphics.Rectangle run() {
                                Display d = ((SwtAUTServer)AUTServer
                                        .getInstance()).getAutDisplay();
                                if (d != null && d.getActiveShell() != null) {
                                    return d.getActiveShell().getBounds();
                                }
                                return null;
                            }
                        });
        if (activeWindowSize != null) {
            return SwtPointUtil.toAwtRectangle(activeWindowSize);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    protected IRobot getRobot() {
        return AUTServer.getInstance().getRobot();
    }

    /**
     * perform a keystroke
     * @param modifierSpec the string representation of the modifiers
     * @param keySpec the string representation of the key
     */
    public void rcKeyStroke(String modifierSpec, String keySpec) {
        if (keySpec == null || keySpec.trim().length() == 0) {
            throw new StepExecutionException(
                "The base key of the key stroke must not be null or empty", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.INVALID_PARAM_VALUE));
        }
        String keyStrokeSpec = keySpec.trim();
        String mod = KeyStrokeUtil.getModifierString(modifierSpec);
        if (mod.length() > 0) {
            keyStrokeSpec = mod + " " + keyStrokeSpec; //$NON-NLS-1$
        }
        String keySpecification = keySpec.trim().toLowerCase();
        if (EnvironmentUtils.isMacOS() && keySpecification.length() == 1
                && keySpecification.charAt(0) == WorkaroundUtil.CHAR_B) {
            rcNativeKeyStroke(modifierSpec, keySpec);
        } else {
            // at this the key stroke specification is not fully fulfilled as the
            // key stroke spec base key is not definitely upper case
            getRobot().keyStroke(keyStrokeSpec);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected Object getFocusOwner() {
        return FocusTracker.getFocusOwner();
    }
    
    /**
     * {@inheritDoc}
     */
    protected int getEventCode(int key) {
        int event = 0;
        switch (key) {
            case 1 : 
                event = SWT.NUM_LOCK;
                break;
            case 2 : 
                event = SWT.CAPS_LOCK;
                break;
            case 3 : 
                event = SWT.SCROLL_LOCK;
                break;
            default : 
                break;
        }
        return event;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Object getActiveWindow() {
        return getRobotFactory().getEventThreadQueuer()
                .invokeAndWait(this.getClass().getName() + ".getActiveWindow", //$NON-NLS-1$
                        new IRunnable<Shell>() {
                            public Shell run() { // SYNCH THREAD START
                                Display d = ((SwtAUTServer) AUTServer
                                        .getInstance()).getAutDisplay();
                                return d.getActiveShell();
                            }
                        }
                );
    }
    
    /**
     * Returns <code>true</code> if a window with the given title is active
     * (the window with focus).
     * 
     * @param title the title
     * @param operator the matches/equals operator
     * @return if the window is open and visible
     */
    private boolean isWindowActive(final String title, final String operator) {
        final Shell activeWindow = (Shell) getActiveWindow();
        
        if (activeWindow == null) {
            if (log.isWarnEnabled()) {
                log.warn("No active Window found while searching for Window with title: '" //$NON-NLS-1$
                        + String.valueOf(title) + "'! " + //$NON-NLS-1$
                    "(SwtApplicationImplClass#isWindowActive(String, String))"); //$NON-NLS-1$
            }
            return false;
        }        
        final String windowTitle = CAPUtil.getWidgetText(activeWindow,
                activeWindow.getText());
        return MatchUtil.getInstance().match(windowTitle, title, operator);
    }
}