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
package org.eclipse.jubula.tools.internal.xml.businessprocess;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * class to manage the version of the clientConfig.xml
 * @author BREDEX GmbH
 * @created 24.11.2005
 */
public class ConfigVersion {
    /**
     * <code>m_majorVersion</code>majorVersion read from client config xml
     */
    private String m_majorV = null;
    
    /**
     * <code>m_minorVersion</code>minorVersion read from client config xml
     */
    private String m_minorV = null;
    
    /**
     * <code>m_majorVersion</code>majorVersion read from client config xml
     */
    private Integer m_majorVersion = null;
    
    /**
     * <code>m_minorVersion</code>minorVersion read from client config xml
     */
    private Integer m_minorVersion = null;
    
    /**
     * 
     */
    public ConfigVersion() {
        // only for deserialization
    }
    
    /**
     * @return Returns the majorVersion.
     */
    public Integer getMajorVersion() {
        if (m_majorVersion == null) {
            m_majorVersion = new Integer(Integer.parseInt(m_majorV));
        }
        return m_majorVersion;
    }
    /**
     * @return Returns the minorVersion.
     */
    public Integer getMinorVersion() {
        if (m_minorVersion == null) {
            m_minorVersion = new Integer(Integer.parseInt(m_minorV));
        }
        return m_minorVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigVersion)) {
            return false;
        }
        ConfigVersion rhs = (ConfigVersion)obj;
        return new EqualsBuilder().append(m_majorVersion, rhs.m_majorVersion)
            .append(m_minorVersion, rhs.m_minorVersion).isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(m_majorVersion)
            .append(m_minorVersion).toHashCode();
    }

    /**
     * @param majorVersion The majorVersion to set.
     */
    public void setMajorVersion(Integer majorVersion) {
        m_majorVersion = majorVersion;
    }

    /**
     * @param minorVersion The minorVersion to set.
     */
    public void setMinorVersion(Integer minorVersion) {
        m_minorVersion = minorVersion;
    }
}
