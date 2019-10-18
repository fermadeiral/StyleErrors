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

import java.util.List;

/**
 * Stores information that is being entered by the user
 * in the wizard, so it can be easily accessed afterwards.
 * 
 * @author BREDEX GmbH
 */
public final class Storage {
    
    /** The selected toolkit */
    private Toolkit m_toolkit;
    
    /** The entered project name */
    private String m_projectName;
    
    /** 
     * The chosen project location.
     * <code>m_location</code> is <code>null</code> for the default location.
     */
    private String m_location;
    
    /** The chosen execution environment */
    private String m_executionEnvironment;
    
    /** The entered ID */
    private String m_id;
    
    /** The entered version */
    private String m_version;
    
    /** The entered name */
    private String m_name;
    
    /** The entered vendor */
    private String m_vendor;
    
    /** The chosen component type's qualifier */
    private String m_componentType;
    
    /** The entered or chosen component */
    private String m_component;
    
    /** The entered tester class name */
    private String m_className;
    
    /** The tester class's actions */
    private List<Action> m_actions;
    
    /** 
     * A boolean that is <code>true</code> when the selected component is a
     * custom component, <code>false</code> otherwise.
     */
    private boolean m_isComponentCustom;
    
    /**
     * This boolean must be set to <code>true</code> when the user selected
     * a toolkit on the toolkit selection page. It will be set to 
     * <code>false</code> if the change has been processed.
     */
    private boolean m_hasToolkitChanged;

    /** 
     * <code>true</code> when the target platform file should be created,
     * <code>false</code> otherwise.
     */
    private boolean m_targetPlatform;
    
    /**
     * Sets whether the selected component is custom
     * @param isCustom <code>true</code> if the component is custom,
     * <code>false</code> otherwise
     */
    public void setComponentCustom(boolean isCustom) {
        m_isComponentCustom = isCustom;
    }
    
    /**
     * @return <code>true</code> if the component is custom, <code>false</code>
     * otherwise
     */
    public boolean isComponentCustom() {
        return m_isComponentCustom;
    }
        
    /** 
     * Sets the toolkit 
     * @param toolkit the toolkit to be set
     */
    public void setToolkit(Toolkit toolkit) {
        m_toolkit = toolkit;
    }
    
    /**
     * @return the toolkit
     */
    public Toolkit getToolkit() {
        return m_toolkit;
    }

    /**
     * @return the project name
     */
    public String getProjectName() {
        return m_projectName;
    }

    /**
     * Sets the project name
     * @param projectName the project name to be set
     */
    public void setProjectName(String projectName) {
        this.m_projectName = projectName;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return m_location;
    }

    /**
     * Sets the location
     * @param location the location to be set
     */
    public void setLocation(String location) {
        m_location = location;
    }

    /**
     * @return the execution environment
     */
    public String getExecutionEnvironment() {
        return m_executionEnvironment;
    }

    /**
     * Sets the execution environment
     * @param executionEnvironment the execution environment
     * to be set
     */
    public void setExecutionEnvironment(String executionEnvironment) {
        this.m_executionEnvironment = executionEnvironment;
    }

    /**
     * @return the ID
     */
    public String getID() {
        return m_id;
    }

    /**
     * Sets the ID
     * @param id the ID to be set
     */
    public void setID(String id) {
        m_id = id;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return m_version;
    }

    /**
     * Sets the version
     * @param version the version to be set
     */
    public void setVersion(String version) {
        this.m_version = version;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name
     * @param name the name to be set
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return m_vendor;
    }

    /**
     * Sets the vendor
     * @param vendor the vendor to be set
     */
    public void setVendor(String vendor) {
        this.m_vendor = vendor;
    }

    /**
     * @return the component's type
     */
    public String getComponentType() {
        return m_componentType;
    }

    /**
     * Sets the component's type
     * @param componentType the component's type to be set
     */
    public void setComponentType(String componentType) {
        this.m_componentType = componentType;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return m_component;
    }

    /**
     * Sets the component
     * @param component the component to be set
     */
    public void setComponent(String component) {
        this.m_component = component;
    }

    /**
     * @return the tester class's name
     */
    public String getClassName() {
        return m_className;
    }

    /**
     * Sets the tester class's name
     * @param className the tester class name to be set
     */
    public void setClassName(String className) {
        this.m_className = className;
    }

    /**
     * @return the class's actions
     */
    public List<Action> getActions() {
        return m_actions;
    }

    /**
     * Sets the action's parameters
     * @param actions the action parameters to be set
     */
    public void setActions(List<Action> actions) {
        this.m_actions = actions;
    }

    /**
     * @return <code>true</code> when the toolkit has recently changed,
     * <code>false</code> otherwise
     */
    public boolean hasToolkitChanged() {
        return m_hasToolkitChanged;
    }

    /**
     * Sets whether the toolkit has recently changed
     * @param hasToolkitChanged <code>true</code> if the toolkit has recently
     * changed, <code>false</code> otherwise
     */
    public void setToolkitChanged(boolean hasToolkitChanged) {
        this.m_hasToolkitChanged = hasToolkitChanged;
    }
    
    /**
     * @return <code>true</code> if the target platform file should be created,
     * <code>false</code> otherwise
     */
    public boolean getTargetPlatform() {
        return m_targetPlatform;
    }

    /**
     * Sets whether the target platform should be created or not.
     * @param targetPlatform <code>true</code> if the target platform file
     * should be created, <code>false</code> otherwise
     */
    public void setTargetPlatform(boolean targetPlatform) {
        m_targetPlatform = targetPlatform;
    }
}
