/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.listener;

import org.apache.commons.lang.Validate;

/**
 * Objects of this class are used hold locks for waiting for an event
 * condition.
 * 
 * @author BREDEX GmbH
 * @created 06.05.2011
 */
public class EventLock {

    /**
     * This variable is true, the event condition occurred otherwise
     * there was a timeout
     */
    private boolean m_released = false;

    /** exception that occurred while handling an event */
    private RuntimeException m_exception = null;

    /**
     * This method is called if the event condition occurred
     */
    public void release() {
        m_released = true;
    }

    /**
     * This method is called if an exception prevented evaluation of the 
     * event condition. All subsequent calls to {@link #isReleased()} will
     * throw the given exception.
     * 
     * @param rte The exception that prevented evaluation.
     */
    public void release(RuntimeException rte) {
        Validate.notNull(rte);
        m_exception = rte;
        m_released = true;
    }

    /**
     * This method returns true if the event condition occurred
     * @return true if the event condition occurred
     * @throws RuntimeException if an exception prevented evaluation of 
     *                          the event condition. 
     */
    public boolean isReleased() throws RuntimeException {
        if (m_exception != null) {
            throw m_exception;
        }
        return m_released;
    }

}
