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

import org.eclipse.core.runtime.Status;

/**
 * Status with time stamp
 * 
 * @author BREDEX GmbH
 * @created 06.08.2015
 **/
public class TimeStatus extends Status implements ITimeStatus {

    /** time stamp **/
    private long m_time;
    
    /**
     * Creates a new status object with a time stamp.  The created status has no children.
     *
     * @param severity the severity; one of <code>OK</code>, <code>ERROR</code>, 
     * <code>INFO</code>, <code>WARNING</code>,  or <code>CANCEL</code>
     * @param pluginId the unique identifier of the relevant plug-in
     * @param code the plug-in-specific status code, or <code>OK</code>
     * @param message a human-readable message, localized to the
     *    current locale
     * @param exception a low-level exception, or <code>null</code> if not
     *    applicable 
     */
    public TimeStatus(int severity, String pluginId, int code, String message,
            Throwable exception) {
        super(severity, pluginId, code, message, exception);
        m_time = System.currentTimeMillis();
    }
    
    /**
     * Simplified constructor of a new status object with time stamp; assumes that code is <code>OK</code> and
     * exception is <code>null</code>. The created status has no children.
     *
     * @param severity the severity; one of <code>OK</code>, <code>ERROR</code>, 
     * <code>INFO</code>, <code>WARNING</code>,  or <code>CANCEL</code>
     * @param pluginId the unique identifier of the relevant plug-in
     * @param message a human-readable message, localized to the
     *    current locale
     */
    public TimeStatus(int severity, String pluginId, String message) {
        super(severity, pluginId, message);
        m_time = System.currentTimeMillis();
    }
    
    /**
     * Simplified constructor of a new status object with time stamp; assumes that code is <code>OK</code>.
     * The created status has no children.
     *
     * @param severity the severity; one of <code>OK</code>, <code>ERROR</code>, 
     * <code>INFO</code>, <code>WARNING</code>,  or <code>CANCEL</code>
     * @param pluginId the unique identifier of the relevant plug-in
     * @param message a human-readable message, localized to the
     *    current locale
     * @param exception a low-level exception, or <code>null</code> if not
     *    applicable
     */
    public TimeStatus(int severity, String pluginId, String message,
            Throwable exception) {
        super(severity, pluginId, message, exception);
        m_time = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    public long getTime() {
        return m_time;
    }

}
