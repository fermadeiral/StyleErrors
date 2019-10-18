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
package org.eclipse.jubula.tools.internal.objects;

import java.io.Serializable;

import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;

/**
 * @author BREDEX GmbH
 * @created 20.08.2010
 */
public class MonitoringValue implements Serializable, IMonitoringValue {

    /** the monitored value */
    private String m_value;

    /** the type for the value */
    private String m_type;

    /** the category name, default MonitoringConstants.NO_CATEGORY */
    private String m_category;

    /** sets if this monitoring value is significant */
    private Boolean m_significant;

    /**
     * default constructor
     */
    public MonitoringValue() {
        // default
    }

    /**
     * @param value
     *            The monitored value
     * @param type
     *            PERCENT, DOUBLE, INTEGER
     */
    public MonitoringValue(String value, String type) {

        this.m_value = value;
        this.m_type = type;
        this.m_category = MonitoringConstants.NO_CATEGORY;
        this.m_significant = Boolean.FALSE;
    }

    /**
     * 
     * @param value
     *            The monitored value
     * @param type
     *            PERCENT, DOUBLE, INTEGER
     * @param category
     *            The category to set
     */
    public MonitoringValue(String value, String type, String category) {

        this.m_value = value;
        this.m_type = type;
        this.m_category = category;
        this.m_significant = Boolean.FALSE;
    }

    /**
     * 
     * @param value
     *            The monitored value
     * @param type
     *            PERCENT, DOUBLE, INTEGER
     * @param category
     *            The category to set
     * @param isSignificant
     *            set this value significant
     */
    public MonitoringValue(String value, String type, String category,
            Boolean isSignificant) {

        this.m_value = value;
        this.m_type = type;
        this.m_category = category;
        this.m_significant = isSignificant;
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return m_value;
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return m_type;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String value) {
        this.m_value = value;
    }

    /**
     * {@inheritDoc}
     */
    public void setType(String type) {
        this.m_type = type;
    }

    /**
     * {@inheritDoc}
     */
    public void setCategory(String category) {

        this.m_category = category;

    }

    /**
     * {@inheritDoc}
     */
    public String getCategory() {

        return m_category;

    }

    /**
     * {@inheritDoc}
     */
    public Boolean isSignificant() {
        return m_significant;
    }

    /**
     * {@inheritDoc}
     */
    public void setSignificant(Boolean significant) {
        this.m_significant = significant;
    }

}
