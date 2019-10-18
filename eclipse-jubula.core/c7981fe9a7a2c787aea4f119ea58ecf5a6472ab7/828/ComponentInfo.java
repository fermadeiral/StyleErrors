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
package org.eclipse.jubula.tools.internal.utils.generator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;


/**
 * The info class for Components.
 * 
 * @author BREDEX GmbH
 * @created 16.09.2005
 */
/**
 * @author BREDEX GmbH
 * @created Jul 19, 2007
 */
public class ComponentInfo extends Info implements Comparable {
    /**
     * the prefix for our help ids
     */
    public static final String HID_PREFIX = "SWING"; //$NON-NLS-1$
    /**
     * The component.
     */
    private Component m_component;
    /**
     * The level of the component in the component inheritance hierarchy,
     * starting at <code>0</code>.
     */
    private int m_level;
    
    /**
     * the help id for the online help system.
     */
    private String m_helpid;
    
    /**
     * the component type
     */
    private String m_type;
    
    /**
     * the shortname of the type (i.e. without de.bredex.etc.foo.bar)
     */
    private String m_shortType;
    
    /**
     * <code>m_tkInfo</code>
     */
    private ToolkitInfo m_tkInfo;

    /**
     * @param component
     *            The component
     * @param tkInfo the toolkit info
     */
    public ComponentInfo(Component component, ToolkitInfo tkInfo) {
        this(component, 0, tkInfo);
    }
    /**
     * @param component
     *            The component
     * @param level
     *            The inheritance hierarchy level of the component
     * @param tkInfo The toolkit to which the component belongs
     */
    public ComponentInfo(Component component, int level, ToolkitInfo tkInfo) {
        super(CompSystemI18n.getString(component.getType()));
        m_component = component;
        m_level = level;
        m_type = m_component.getType();
        // We want only the simple class name, so take the last token
        // after splitting.
        String[] tokens = StringUtils.split(m_type, '.');
        final int tokenLength = tokens.length;
        if (tokenLength > 0) {
            m_shortType = tokens[tokenLength - 1];
        } else {
            m_shortType = m_type;
        }
        m_tkInfo = tkInfo;
        m_helpid = m_tkInfo.getHelpid() + "_" + m_shortType; //$NON-NLS-1$
    }
    /**
     * @return Returns the component.
     */
    public Component getComponent() {
        return m_component;
    }
    /**
     * @return Returns the level.
     */
    public int getLevel() {
        return m_level;
    }
    /**
     * @return Returns the helpid.
     */
    public String getHelpid() {
        return m_helpid;
    }
    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        ComponentInfo rhs = (ComponentInfo)o;
        return this.getI18nName().compareTo(rhs.getI18nName());
    }
    /**
     * @return Returns the type.
     */
    public String getType() {
        return m_type;
    }
    /**
     * @return Returns the shortType.
     */
    public String getShortType() {
        return m_shortType;
    }
    /**
     * @return the tkInfo
     */
    public ToolkitInfo getTkInfo() {
        return m_tkInfo;
    }
}
