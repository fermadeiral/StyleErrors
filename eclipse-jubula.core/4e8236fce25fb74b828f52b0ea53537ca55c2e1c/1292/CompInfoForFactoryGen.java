/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.toolkit.api.gen.internal.genmodel;

import org.eclipse.jubula.tools.internal.xml.businessmodell.ComponentClass;



/**
 * Contains all necessary information of a component for factory generation
 * @author BREDEX GmbH
 * @created 20.10.2014
 */
public class CompInfoForFactoryGen {
    
    /** the class name */
    private String m_packageName;

    /** Whether an interface should be generated */
    private Boolean m_hasDefaultMapping;

    /** name of the component */
    private String m_className;
    
    /** the most specific visible super type of a component */
    private String m_mostSpecificVisibleSuperTypeName;

    /** the componentClass */
    private ComponentClass m_componentClass;

    /** semantic version information */
    private String m_since;
    
    /**
     * Contains all necessary information of a component for factory generation
     * @param className the class name
     * @param packageName the package name
     * @param componentClass the real component class
     * @param hasDefaultMapping true if and only if component has default mapping
     * @param mostSpecificVisibleSuperTypeName most specific visible super type of a component
     */
    public CompInfoForFactoryGen(String className, String packageName,
            ComponentClass componentClass, boolean hasDefaultMapping,
            String mostSpecificVisibleSuperTypeName) {
        m_className = className;
        setPackageName(packageName);
        setComponentClass(componentClass);
        m_hasDefaultMapping = hasDefaultMapping;
        m_mostSpecificVisibleSuperTypeName = mostSpecificVisibleSuperTypeName;
    }
    
    /**
     * Returns the component name
     * @return the component name
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * Returns true if and only if component has a default mapping
     * @return the toolkit name
     */
    public Boolean hasDefaultMapping() {
        return m_hasDefaultMapping;
    }

    /** 
     * Returns the most specific visible super type of a component
     * @return the most specific visible super type of a component
     */
    public String getMostSpecificVisibleSuperTypeName() {
        return m_mostSpecificVisibleSuperTypeName;
    }

    /**
     * @return the componentClass
     */
    public ComponentClass getComponentClass() {
        return m_componentClass;
    }

    /**
     * @param componentClass the componentClass to set
     */
    public void setComponentClass(ComponentClass componentClass) {
        m_componentClass = componentClass;
    }

    /**
     * @return the since
     */
    public String getSince() {
        return m_since;
    }

    /**
     * @param since the since to set
     */
    public void setSince(String since) {
        m_since = since;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return m_packageName;
    }

    /**
     * @param packageName the packageName to set
     */
    private void setPackageName(String packageName) {
        m_packageName = packageName;
    }
}