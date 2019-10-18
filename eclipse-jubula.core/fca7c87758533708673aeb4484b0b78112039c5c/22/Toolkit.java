/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A toolkit and its properties.
 * 
 * @author BREDEX GmbH
 */
@XmlRootElement(name = "toolkit")
@XmlAccessorType(XmlAccessType.FIELD)
public class Toolkit {

    /** The toolkit's name*/
    @XmlElement(name = "name")
    private String m_name;
    
    /** The toolkit's ID */
    @XmlElement(name = "id")
    private String m_toolkitId;
    
    /** 
     * The toolkit's description that is displayed on the toolkit selection
     * page.
     */
    @XmlElement(name = "description")
    private String m_description;
    
    /** The URI, where the toolkit's RRobot file can be found */
    @XmlElement(name = "robotUri")
    private String m_robotUri;
    
    /** The dependency of the toolkit */
    @XmlElement(name = "toolkitDependency")
    private String m_toolkitDependency;
    
    /** The extension name of the toolkit */
    @XmlElement(name = "toolkitExtensionName")
    private String m_toolkitExtensionName;
    
    /** The adapter statement of the toolkit */
    @XmlElement(name = "adapter")
    private String m_adapter;
    
    /** The adapter package of the toolkit */
    @XmlElement(name = "adapterPackage")
    private String m_adapterPackage;
    
    /** Additional imports for the toolkit */
    @XmlElement(name = "additionalImports")
    private String m_additionalImports;
    
    /** Fragment host of the toolkit */
    @XmlElement(name = "fragmentHost")
    private String m_fragmentHost;  
    
    
    /**
     * The constructor
     * @param name the toolkit's name
     * @param toolkitId the toolkit's ID
     * @param description the toolkit's description
     * @param robotUri the toolkit's RRobot file
     */
    public Toolkit(String name, String toolkitId, String description, 
            String robotUri) {
        m_name = name;
        m_toolkitId = toolkitId;
        m_description = description;
        m_robotUri = robotUri;
    }
    
    /**
     * A constructor that should only be used to create Toolkit instances
     * to compare them to other instances.
     * @param name the toolkit's name
     */
    public Toolkit(String name) {
        m_name = name;
    }
    
    /**
     * Default constructor
     */
    public Toolkit() {
        
    }
    
    /**
     * @return the toolkit's name
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * @param name the toolkit's name
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * @return the toolkit's id
     */
    public String getToolkitId() {
        return m_toolkitId;
    }
    
    /**
     * @param toolkitId the toolkit's id
     */
    public void setToolkitId(String toolkitId) {
        m_toolkitId = toolkitId;
    }
    
    
    /**
     * @return the toolkit's description for the toolkit selection page
     */
    public String getDescription() {
        return m_description;
    }
    
    /**
     * @param description the toolkit's description
     */
    public void setDescription(String description) {
        m_description = description;
    }
    
    
    /**
     * @return the URI of the toolkit's RRobot file
     */
    public String getRobotUri() {
        return m_robotUri;
    }
    
    /**
     * @param robotUri the toolkit's robot file uri
     */
    public void setRobotUri(String robotUri) {
        m_robotUri = robotUri;
    }
    
    /**
     * @return the toolkit's dependency
     */
    public String getToolkitDependency() {
        return m_toolkitDependency;
    }

    /**
     * @param toolkitDependency the toolkit's dependency
     */
    public void setToolkitDependency(String toolkitDependency) {
        this.m_toolkitDependency = toolkitDependency;
    }

    /**
     * @return the toolkit extension name
     */
    public String getToolkitExtensionName() {
        return m_toolkitExtensionName;
    }

    /**
     * @param toolkitExtensionName the toolkit extension name
     */
    public void setToolkitExtensionName(String toolkitExtensionName) {
        this.m_toolkitExtensionName = toolkitExtensionName;
    }

    /**
     * @return the adapter statement
     */
    public String getAdapter() {
        return m_adapter;
    }

    /**
     * @param adapter the adapter statement
     */
    public void setAdapter(String adapter) {
        this.m_adapter = adapter;
    }

    /**
     * @return the adapter's package
     */
    public String getAdapterPackage() {
        return m_adapterPackage;
    }

    /**
     * @param adapterPackage the adapter's package
     */
    public void setAdapterPackage(String adapterPackage) {
        this.m_adapterPackage = adapterPackage;
    }

    /**
     * @return additional imports
     */
    public String getAdditionalImports() {
        return m_additionalImports;
    }

    /**
     * @param additionalImports additional imports
     */
    public void setAdditionalImports(String additionalImports) {
        this.m_additionalImports = additionalImports;
    }
    
    /**
     * @return the fragment host
     */
    public String getFragmentHost() {
        return m_fragmentHost;
    }
    
    /**
     * @param fragmentHost the fragment host
     */
    public void setFragmentHost(String fragmentHost) {
        this.m_fragmentHost = fragmentHost;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_name)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof Toolkit)) {
            Toolkit toolkit = (Toolkit) obj;
            return new EqualsBuilder()
                .append(m_name, toolkit.getName())                
                .isEquals();
        }
        return false;
    }
    
    
}
