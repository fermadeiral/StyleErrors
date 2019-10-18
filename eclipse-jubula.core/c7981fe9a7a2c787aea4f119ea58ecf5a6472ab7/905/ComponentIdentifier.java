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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.eclipse.jubula.tools.Profile;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds the information for identifying a component in the AUT. <br>
 * 
 * Currently a component is identified by its type, e.g.
 * 'javax.swing.JTextfield' and the names of the container building the
 * hierarchy of the GUI of the AUT.
 * 
 
 * @author BREDEX GmbH
 * @created 27.08.2004
 */
public class ComponentIdentifier implements Serializable, IComponentIdentifier {
    /**
     * Define the serialization ID to prevent
     * deserialization errors after changing
     * instance variables
     */
    static final long serialVersionUID = 1031;

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
            ComponentIdentifier.class);
    
    /**
     * the name of the class as which class is being handled as
     * for example myTextField would be TextField
     */
    private String m_supportedClassName = null;
    
    /**
     * a list of all neighbours of a component
     * this is some context information
     */
    private List<String> m_neighbours = new ArrayList<String>();
    
    /** the name of the class of the component, such as 'javax.swing.JButton' 
     *  this attribute decides how to test a class. So if class itself is not
     * testable, it will be superClass name.
     * */
    private String m_componentClassName = null;
    
    /**
     * the hierarchy for the component. The names of the container hierarchy
     * from top to (checkstyle :-) down, inclusive the component name itself
     */
    private List<String> m_hierarchyNames = new ArrayList<String>();

    /**
     * the alternative name for display of the component or null if the normal 
     * name returned by method getComponentName() has to be used as display name
     */
    private String m_alternativeDisplayName = null;
    
    /**
     * the <code>m_matchPercentage</code> when this component identifier has
     * been collected
     */
    private double m_matchPercentage = -1.0d;

    /**
     * the <code>m_numberOfOtherMatchingComponents</code> which may also be
     * likely to be found in future
     */
    private int m_numberOfOtherMatchingComponents = -1;

    /**
     * <code>m_equalOriginalFound</code> whether this component identifier could
     * be used to retrieve the original component on collection
     */
    private boolean m_equalOriginalFound = false;
    
    /**
     * Map for storing additional properties of a component
     * the key represents the name of the property
     * the value is only represented by its first 200 characters
     */
    private Map<String, String> m_componentProperties;
    
    /** profile for the component identifier */
    private Profile m_profile = null;
    /**
     * public constructor <br>
     * 
     * initializes m_hierarchyNames
     */
    public ComponentIdentifier() {
        //
    }
    
    /**
     * @return Returns the componentClassName.
     */
    public String getComponentClassName() {
        return m_componentClassName;
    }
    
    /**
     * @param componentClassName The componentClassName to set.
     */
    public void setComponentClassName(String componentClassName) {
        m_componentClassName = componentClassName;
    }
    
    /**
     * @return the name of the component
     */
    public String getComponentName() {
        // return the last element
        try {
            if (m_hierarchyNames != null && m_hierarchyNames.size() > 0) {
                return m_hierarchyNames.get(
                        m_hierarchyNames.size() - 1);
            }
        } catch (ClassCastException cce) {
            log.error("unexpected element type", cce); //$NON-NLS-1$
        }
        return StringConstants.EMPTY;
    }
    
    /**
     * @param hierarchyNames
     *            The hierarchyNames to set. if null, the list will be cleared.
     */
    public void setHierarchyNames(List<String> hierarchyNames) {
        if (hierarchyNames == null) {
            m_hierarchyNames = new ArrayList<String>();
        } else {
            m_hierarchyNames = hierarchyNames;
        }
    }
    
    /**
     * @return Returns the hierarchyNames.
     */
    public List<String> getHierarchyNames() {
        return m_hierarchyNames;
    }
    /**
     * @param hierarchyNames The hierarchyNames to add.
     */
    public void addHierarchyName(String hierarchyNames) {
        m_hierarchyNames.add(hierarchyNames);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("component class", m_componentClassName) //$NON-NLS-1$
            .append("supported class", m_supportedClassName) //$NON-NLS-1$
            .append("hierarchy", m_hierarchyNames) //$NON-NLS-1$
            .append("neigbours", m_neighbours) //$NON-NLS-1$
            .append("alternative name", m_alternativeDisplayName) //$NON-NLS-1$
            .toString();    
    }
    
    /**
     * clears the hierarchynames list
     *
     */
    public void clearHierarchyNames() {
        m_hierarchyNames.clear();
    }
    
    /**
     * @return Clone of object
     */
    public IComponentIdentifier makeClone() {
        IComponentIdentifier clone = new ComponentIdentifier();
        clone.setHierarchyNames(new ArrayList<String>(
            m_hierarchyNames));
        clone.setComponentClassName(m_componentClassName);
        if (m_supportedClassName != null) {
            clone.setSupportedClassName(m_supportedClassName);
        }
        if (m_neighbours != null) {
            clone.setNeighbours(new ArrayList<String>(m_neighbours));
        }
        clone.setAlternativeDisplayName(m_alternativeDisplayName);
        clone.setProfile(m_profile);
        return clone;
    }
    
    /**
     * @return Returns the supportedClassName.
     */
    public String getSupportedClassName() {
        return m_supportedClassName;
    }
    /**
     * @param supportedClassName The supportedClassName to set.
     */
    public void setSupportedClassName(String supportedClassName) {
        m_supportedClassName = supportedClassName;
    }
    /**
     * @return Returns the neighbours.
     */
    public List<String> getNeighbours() {
        return m_neighbours;
    }
    /**
     * @param neighbours The neighbours to set.
     */
    public void setNeighbours(List<String> neighbours) {
        m_neighbours = neighbours;
    }

    /**
     * @param neighbours The hierarchyNames to add.
     */
    public void addNeighbour(String neighbours) {
        m_neighbours.add(neighbours);
    }

    /**
     * generates a name for the component
     * @return String
     */
    public String generateLogicalName() {
        String returnVal = null;
        final String supportedClassName = getSupportedClassName();
        if (supportedClassName.lastIndexOf(".") > -1 //$NON-NLS-1$
                && supportedClassName.length() > (supportedClassName.lastIndexOf(".") + 1)) { //$NON-NLS-1$
            returnVal = checkDefaultMapping(supportedClassName);
            if (returnVal != null) {
                return returnVal;
            }
            
            returnVal = supportedClassName.substring(
                    supportedClassName.lastIndexOf(".") + 1) + "(";  //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            returnVal = supportedClassName + "(";  //$NON-NLS-1$
        }

        StringBuffer hash = new StringBuffer();
        Iterator<String> iter = getHierarchyNames().iterator();
        while (iter.hasNext()) {
            hash.append(iter.next()); 
        }
        returnVal += hash.toString().hashCode(); 
        returnVal += ")"; //$NON-NLS-1$
        return returnVal;
    }
    
    /**
     * Checks if the given supportedClassName has a Default-Mapping.
     * If it has, the logical name of the Default-Maping will be returned,
     * null otherwise.
     * @param supportedClassName the supported class name
     * @return the logical name or null.
     */
    private String checkDefaultMapping(String supportedClassName) {
        if (MappingConstants.SWING_APPLICATION_CLASSNAME.equals(
            supportedClassName)
            || MappingConstants.SWT_APPLICATION_CLASSNAME.equals(
                supportedClassName)) { 
            
            return CompSystemI18n.getString(TestDataConstants
                .APPLICATION_DEFAULT_MAPPING_I18N_KEY);
        } 
        if (MappingConstants.SWT_MENU_CLASSNAME.equals(supportedClassName)
            || MappingConstants.SWING_MENU_CLASSNAME
                .equals(supportedClassName)) {
            
            return CompSystemI18n.getString(
                TestDataConstants.MENU_DEFAULT_MAPPING_I18N_KEY);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getAlternativeDisplayName() {
        return m_alternativeDisplayName;
    }

    /**
     * {@inheritDoc}
     */
    public void setAlternativeDisplayName(String alternativeDisplayName) {
        m_alternativeDisplayName = alternativeDisplayName;
    }

    /**
     * {@inheritDoc}
     */
    public String getComponentNameToDisplay() {
        final String componentNameToDisplay;
        if (m_alternativeDisplayName == null) {
            // no alternative name set, so use standard name
            componentNameToDisplay = getComponentName();
        } else {
            // use alternative name
            componentNameToDisplay = m_alternativeDisplayName;
        }
        
        return componentNameToDisplay;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof IComponentIdentifier) {
            IComponentIdentifier compId = (IComponentIdentifier)obj;
            List<String> neighbours1 = new ArrayList<String>(getNeighbours());
            List<String> neighbours2 = new ArrayList<String>(
                compId.getNeighbours());
            Collections.sort(neighbours1);
            Collections.sort(neighbours2);
            return new EqualsBuilder()
                    .append(getHierarchyNames(), compId.getHierarchyNames())
                    .append(neighbours1, neighbours2)
                    .isEquals();
        }
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        List<String> neighbours = new ArrayList<String>(getNeighbours());
        Collections.sort(neighbours);
        return new HashCodeBuilder()
            .append(getHierarchyNames())
            .append(neighbours)
            .toHashCode();
    }

    /** {@inheritDoc} */
    public void setMatchPercentage(double matchPercentage) {
        m_matchPercentage = matchPercentage;
    }

    /** {@inheritDoc} */
    public double getMatchPercentage() {
        return m_matchPercentage;
    }

    /** {@inheritDoc} */
    public void setNumberOfOtherMatchingComponents(
            int numberOfOtherMatchingComponents) {
        m_numberOfOtherMatchingComponents = numberOfOtherMatchingComponents;
    }

    /** {@inheritDoc} */
    public int getNumberOfOtherMatchingComponents() {
        return m_numberOfOtherMatchingComponents;
    }

    /** {@inheritDoc} */
    public void setEqualOriginalFound(boolean equalOriginalFound) {
        m_equalOriginalFound = equalOriginalFound;
    }

    /** {@inheritDoc} */
    public boolean isEqualOriginalFound() {
        return m_equalOriginalFound;
    }
    
    /** {@inheritDoc} */
    public Map<String, String> getComponentPropertiesMap() {
        return m_componentProperties;
    }

    /** {@inheritDoc} */
    public void setComponentPropertiesMap(
            Map<String, String> componentProperties) {
        m_componentProperties = componentProperties;
    }

    /** {@inheritDoc} */
    public void setProfile(Profile profile) {
        m_profile = profile;
    }

    /** {@inheritDoc} */
    public Profile getProfile() {
        return m_profile;
    }
}
