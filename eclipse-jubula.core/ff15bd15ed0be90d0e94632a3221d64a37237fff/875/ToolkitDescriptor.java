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
package org.eclipse.jubula.tools.internal.xml.businessmodell;

/**
 * @author BREDEX GmbH
 * @created 24.05.2007
 */
public class ToolkitDescriptor implements Comparable {
    /** m_toolkitID */
    private String m_toolkitID;
    
    /** the order */
    private int m_order;
    
    /** the name */
    private String m_name;
    
    /** m_includes */
    private String m_includes;
    
    /** m_depends */
    private String m_depends;
    
    /** the majorVersion */
    private int m_majorVersion;
    
    /** the minorVersion */
    private int m_minorVersion;
    
    /** m_isUserToolkit */
    private boolean m_isUserToolkit;
    
    /** the level */
    private String m_level;
    
    /** Only for deserialisation! */
    public ToolkitDescriptor() {
        super();
    }
    
    /**
     * Constructor
     * @param toolkitID the unique id of the toolkit 
     *                  (e.g. com.bredexsw.guidancer.SwingToolkitPlugin)
     * @param name the displayable name of the toolkit
     *                 (e.g. Swing)
     * @param includes id of the extended-toolkit or
     *                 empty String if the toolkit is independent.
     * @param depends id of the toolkit on which this toolkit depends.
     * @param level the level of abstraction (abstract, concrete or toolkit)
     * @param order the order of read into the CompSystem
     * @param isUserToolkit whether the toolkit is user defined or not
     * @param majorVersion the major version
     * @param minorVersion the minor version
     */
    public ToolkitDescriptor(String toolkitID, String name, 
        String includes, String depends, String level, int order, 
        boolean isUserToolkit, int majorVersion, int minorVersion) {

        m_toolkitID = toolkitID;
        m_name = name;
        m_includes = includes;
        m_level = level;
        m_order = order;
        m_isUserToolkit = isUserToolkit;
        m_majorVersion = majorVersion;
        m_minorVersion = minorVersion;
        m_depends = depends;
    }

    /**
     * @return id of the extended-toolkit or
     *         empty String if the toolkit is independent.
     */
    public String getIncludes() {
        return m_includes;
    }

    /**
     * @return the dependency to another toolkit. The id of the base
     * toolkit.
     */
    public String getDepends() {
        return m_depends;
    }

    /** @return whether the toolkit is user defined or not */
    public boolean isUserToolkit() {
        return m_isUserToolkit;
    }

    /** @return the level */
    public String getLevel() {
        return m_level;
    }

    /** @return the majorVerision */
    public int getMajorVersion() {
        return m_majorVersion;
    }

    /** @return the minorVersion */
    public int getMinorVersion() {
        return m_minorVersion;
    }

    /** @return the displayable name of the toolkit */
    public String getName() {
        return m_name;
    }

    /** @return the order of read into the CompSystem */
    public int getOrder() {
        return m_order;
    }

    /** @return the id of the toolkit (e.g. org.eclipse.jubula.toolkit.swing) */
    public String getToolkitID() {
        return m_toolkitID;
    }

    /** {@inheritDoc} */
    public int compareTo(Object o) {
        if (!(o instanceof ToolkitDescriptor)) {
            return 0;            
        }
        ToolkitDescriptor descr = (ToolkitDescriptor)o;
        return getToolkitID().compareTo(descr.getToolkitID());
    }

    /** {@inheritDoc} */
    public String toString() {
        return getToolkitID();
    }
}