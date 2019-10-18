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


/**
 * @author BREDEX GmbH
 * @created Jul 4, 2007
 */
public class ToolkitInfo extends Info implements Comparable {
    
//    /**
//     * The components contained in this toolkit
//     */
//    private List m_components;
    
    /**
     * The name used internally
     */
    private String m_type;
    
    /**
     * The prefix to be used when generating help id's
     */
    private String m_helpid;

    /**
     * <code>m_shortName</code>
     */
    private String m_shortType;

    /**
     * @param name the displayable name
     * @param type the internally used name
     */
    public ToolkitInfo(String name, String type) {
        super(name);
        m_type = type;
        calculateShortType();
        setHelpidPrefix();
        m_helpid = m_shortType.toUpperCase();
    }

    /**
     * formats the toolkit type into a shorter version that will
     * use internally
     */
    private void calculateShortType() {
        String shortType;
        String[] tokens = StringUtils.split(m_type, '.');
        final int tokenLength = tokens.length;
        if (tokenLength > 0) {
            shortType = tokens[tokenLength - 1];
        } else {
            shortType = m_type;
        }
        // Strip off ToolkitPlugin, so that we're left with Abstract, Swt, etc.
        final String needle = "ToolkitPlugin"; //$NON-NLS-1$
        if (shortType.endsWith(needle)) {
            shortType = StringUtils.left(shortType, shortType.indexOf(needle));
        }
        m_shortType = shortType;
    }

    /**
     * @return the shortName
     */
    public String getShortType() {
        return m_shortType;
    }

    /**
     * @return the type
     */
    public String getType() {
        return m_type;
    }

    /**
     * 
     */
    private void setHelpidPrefix() {
        m_helpid = m_shortType.toUpperCase();
    }

    /**
     * @return the help id
     */
    public String getHelpid() {
        return m_helpid;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        // sort based on I18n Name
        return getI18nName().compareTo(((ToolkitInfo)o).getI18nName());
    }
}
