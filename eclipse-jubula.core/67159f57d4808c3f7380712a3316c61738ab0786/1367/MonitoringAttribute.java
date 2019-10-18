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
package org.eclipse.jubula.toolkit.common.monitoring;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * This class represents an attribute from the extension point.  
 *  
 * @author BREDEX GmbH
 * @created 26.07.2010
 */
public class MonitoringAttribute {
    
    /** the type of the attribute */
    private String m_type;
    /** the description of the attribute */
    private String m_description;
    /** the id of the attribute */
    private String m_id;
    /** the name of the monitoring agent */
    private String m_monitoringName;
    /** the default value of the attribute */
    private String m_defaultValue;
    /** the text for the info bobble text*/
    private String m_infoBobbleText;
    /** the value if this attribute should be renderd */      
    private boolean m_render; 
    /** the validator */    
    private IValidator m_validator;
    /** extension filters for file selection */
    private String[] m_extensionFilters;
    /**
     * default Constructor
     */
    public MonitoringAttribute() {
        //default Constructor
    }

    /**
     * @param type the type of the attribute
     * @param description the description of the attribute
     * @param id the id of the attribute
     * @param defaultValue the default value of the attribute
     * @param monitoringName the monitoring name of the attribute
     * @param render true: the attribute will be rendered, false: no rendering
     * @param infoBoobleText The text to display as info booble
     * @param validator An optional validator
     * @param extensionFilters extension filters for file selection 
     */
    public MonitoringAttribute(String type, String description, String id,
            String defaultValue, boolean render, String monitoringName,
            String infoBoobleText, IValidator validator,
            String[] extensionFilters) {
        
        m_type = type;
        m_description = description;
        m_id = id;
        m_monitoringName = monitoringName;
        m_defaultValue = defaultValue;
        if (m_defaultValue == null) {
            m_defaultValue = StringConstants.EMPTY;
        }        
        m_render = render;
        m_infoBobbleText = infoBoobleText;
        if (m_defaultValue == null) {
            m_defaultValue = StringConstants.EMPTY;
        }
        m_validator = validator;
        m_extensionFilters = extensionFilters;
    }
    /**
     * @return the type of the attribute
     */
    public String getType() {
        return m_type;
    }
    /**
     * @param type the type to set for the attribute
     */
    public void setType(String type) {
        m_type = type;
    }
    /**
     * @return the description of the attribute
     */
    public String getDescription() {
        return m_description;
    }
    /**
     * @param description the description to set the attribute
     */
    public void setDescription(String description) {
        m_description = description;
    }
    /**
     * @return the id of the attribute
     */
    public String getId() {
        return m_id;
    }
    /**
     * @param id the id to set for the attribute
     */
    public void setId(String id) {
        m_id = id;
    }
    /**
     * @return the name of the monitoring agent which is the parent of this 
     * attribute
     */
    public String getMonitoringName() {
        return m_monitoringName;
    }
    /**
     * @param monitoringName sets the parent monitoring agent name for this attribute
     */
    public void setMonitoringName(String monitoringName) {
        m_monitoringName = monitoringName;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
             
        m_defaultValue = defaultValue;
    }

    /**
     * @return the render
     */
    public boolean isRender() {
        return m_render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        m_render = render;
    }
    /**
     * 
     * @return the info bobble text from the extension point
     */
    public String getInfoBobbleText() {
        return m_infoBobbleText;
    }
    /**
     * 
     * @param infoBobbleText the info bobble text to set
     */
    public void setInfoBobbleText(String infoBobbleText) {
        this.m_infoBobbleText = infoBobbleText;
    }
    /**
     * @return The validator for this attribute or null 
     */
    public IValidator getValidator() {
        
        return m_validator;
    }

    /**
     * @return extension filters for file selection 
     */
    public String[] getExtensionFilters() {
        return m_extensionFilters;
    }
}
