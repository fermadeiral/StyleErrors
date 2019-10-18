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
package org.eclipse.jubula.client.core.model;

/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface IEventExecTestCasePO extends IExecTestCasePO {
    
    /** smallest value allowed for maximum number of retries for RETRY reentry type */
    public static final int MIN_VALUE_MAX_NUM_RETRIES = 1;
    
    /** largest value allowed for maximum number of retries for RETRY reentry type */
    public static final int MAX_VALUE_MAX_NUM_RETRIES = Integer.MAX_VALUE;

    /** default value for maximum number of retries for RETRY reentry type */
    public static final int DEFAULT_MAX_NUM_RETRIES = 
        MIN_VALUE_MAX_NUM_RETRIES;

    /**
     * @return the ReentryProperty
     */
    public abstract ReentryProperty getReentryProp();

    /**
     * Set property
     * @param prop property to be set
     */
    public abstract void setReentryProp(ReentryProperty prop);

    /**
     * @return Returns the eventType.
     */
    public abstract String getEventType();

    /**
     * only for Persistence (JPA / EclipseLink)
     * @param eventType The eventType to set.
     */
    public abstract void setEventType(String eventType);

    /**
     * @return Returns the maximum number of times this event handler will retry
     *         the test step that caused it to run, or <code>null</code> if this
     *         value is not meaningful for the receiver.
     */
    public Integer getMaxRetries();

    /**
     * @param maxRetries
     *          The maximum number of times this event handler will retry
     *          the test step that caused it to run. Can be <code>null</code>.
     */
    public void setMaxRetries(Integer maxRetries);
}