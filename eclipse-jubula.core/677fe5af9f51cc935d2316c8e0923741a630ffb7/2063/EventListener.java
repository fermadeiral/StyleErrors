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
package org.eclipse.jubula.rc.swing.tester.util;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;

import org.eclipse.jubula.rc.common.listener.EventLock;

/**
 * This Listener waits for a condition in the AWT event queue. If this
 * happens it calls notifyAll on the lock object.
 *
 * @author BREDEX GmbH
 * @created 18.01.2006
 */
public class EventListener implements AWTEventListener {
    /**
     * Object that checks if a condition about an event is true or false
     */
    public interface Condition {
        /**
         * checks if the condition about an event is true
         *  
         * @param event
         *          the event
         * @return
         *          true or false
         */
        public boolean isTrue(AWTEvent event);
    }

    /**
     * a lock
     */
    private final EventLock m_lock;
    /**
     * This condition defines about which events the caller gets informed.
     */
    private final Condition m_condition;
    /**
     * constructor
     * 
     * @param lock
     *          a lock
     * @param condition
     *          a condition
     */
    public EventListener(EventLock lock,
            Condition condition) {
        m_lock = lock;
        m_condition = condition;
    }
    /**
     * {@inheritDoc}
     */
    public void eventDispatched(AWTEvent event) {
        synchronized (m_lock) {
            try {
                if (m_condition.isTrue(event)) {
                    m_lock.release();
                    m_lock.notifyAll();
                }
            } catch (RuntimeException rte) {
                m_lock.release(rte);
                m_lock.notifyAll();
            }
        }
    }
}