/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.status;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * MultiStatus with time stamp
 * 
 * @author BREDEX GmbH
 * @created 06.08.2015
 **/
public class TimeMultiStatus extends MultiStatus implements ITimeStatus {
    /** time stamp **/
    private long m_time;

    /**
     * Creates and returns a new multi-status object with the given children and a time stamp.
     *
     * @param pluginId the unique identifier of the relevant plug-in
     * @param code the plug-in-specific status code
     * @param newChildren the list of children status objects
     * @param message a human-readable message, localized to the
     *    current locale
     * @param exception a low-level exception, or <code>null</code> if not
     *    applicable 
     */
    public TimeMultiStatus(String pluginId, int code, IStatus[] newChildren,
            String message, Throwable exception) {
        super(pluginId, code, newChildren, message, exception);
        m_time = System.currentTimeMillis();
    }

    /**
     * Creates and returns a new multi-status object with no children and a time stamp.
     *
     * @param pluginId the unique identifier of the relevant plug-in
     * @param code the plug-in-specific status code
     * @param message a human-readable message, localized to the
     *    current locale
     * @param exception a low-level exception, or <code>null</code> if not
     *    applicable 
     */
    public TimeMultiStatus(String pluginId, int code, String message,
            Throwable exception) {
        super(pluginId, code, message, exception);
        m_time = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    public long getTime() {
        return m_time;
    }

}
