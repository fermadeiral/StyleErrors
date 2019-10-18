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
package org.eclipse.jubula.rc.common.driver;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.StandardToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class bundles the options to configure the event interceptor.
 * 
 * @author BREDEX GmbH
 * @created 17.03.2005
 */
public class InterceptorOptions {
    /**
     * The toString() style.
     */
    private static final StandardToStringStyle TOSTRING_STYLE =
        new StandardToStringStyle();
    static {
        TOSTRING_STYLE.setUseShortClassName(true);
        TOSTRING_STYLE.setUseIdentityHashCode(false);
    }
    /**
     * The Graphics API specific event mask.
     */
    private long[] m_eventMask;
    /**
     * Creates a new options instance. The event mask is Graphics API
     * dependant.
     * 
     * @param eventMask The Graphics API specific event mask.
     */
    public InterceptorOptions(long[] eventMask) {
        m_eventMask = eventMask;
    }
    /**
     * @return The Graphics API specific event mask.
     */
    public long[] getEventMask() {
        return m_eventMask;
    }
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17, 37, this);
    }
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this, TOSTRING_STYLE);
    }
}
