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
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;


/**
 * The info class for parameters.
 *
 * @author BREDEX GmbH
 * @created 16.09.2005
 */
public class ParamInfo extends Info implements Comparable {
    /**
     * The parameter.
     */
    private Param m_param;
    /**
     * the help id for our online help system
     */
    private String m_helpid;
    /**
     * the internationalized name for the parameter type
     */
    private String m_i18nType;
    /**
     * The short internal name for the parameter, i.e. "Count"
     * instead of "CompSystem.Count"
     */
    private String m_shortName;
    /**
     * @param param
     *            The parameter
     * @param actionHelpid
     *      the help id for the containing action 
     */
    public ParamInfo(Param param, String actionHelpid) {
        super(CompSystemI18n.getString(param.getName()));
        m_param = param;
        m_i18nType = CompSystemI18n.getString(param.getType());
        String[] tokens = StringUtils.split(m_param.getName(), '.');
        m_shortName = tokens[tokens.length - 1];
        m_helpid = actionHelpid + "_" + m_shortName; //$NON-NLS-1$
    }
    /**
     * @return Returns the parameter.
     */
    public Param getParam() {
        return m_param;
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
        ParamInfo rhs = (ParamInfo)o;
        return this.getI18nName().compareTo(rhs.getI18nName());
    }
    /**
     * @return Returns the i18nType.
     */
    public String getI18nType() {
        return m_i18nType;
    }
    /**
     * @return Returns the shortName.
     */
    public String getShortName() {
        return m_shortName;
    }
}
