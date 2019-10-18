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
package org.eclipse.jubula.rc.swt.tester.util;

import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author BREDEX GmbH
 * @created 08.05.2007
 */
public class EventListener implements Listener {

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
        public boolean isTrue(Event event);
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
    public void handleEvent(Event event) {
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
