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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;


/**
 * The info class for Actions.
 *
 * @author BREDEX GmbH
 * @created 16.09.2005
 */
public class ActionInfo extends Info implements Comparable {
    
    /***/
    public static final int TYPE_EXECUTE = 0;
    
    /***/
    public static final int TYPE_CHECK = 1;

    /** The action. */
    private Action m_action;
    /** */
    private ComponentInfo m_containerComp;
    /** The action method type. */
    private int m_type;
    /** The associated help id for online help */
    private String m_helpid;
    /** The list of action parameter infos. */
    private List<ParamInfo> m_params = new ArrayList<ParamInfo>();
    /**
     * The short internal name of the action, i.e. "KeyCombination"
     * instead of "CompSystem.KeyCombination"
     */
    private String m_shortName;
    /**
     * @param action The action
     * @param containerComp
     *            The component that contains the action. This may be a
     *            component which inherits the action, or a component which
     *            actually defines it
     */
    public ActionInfo(Action action, ComponentInfo containerComp) {
        
        super(CompSystemI18n.getString(action.getName()));
        m_action = action;
        m_containerComp = containerComp;
        
        // The methods for the actions still contain "Verify" rather than
        // "Check".
        m_type = m_action.getMethod().indexOf("Verify") != -1 ? TYPE_CHECK //$NON-NLS-1$
            : TYPE_EXECUTE;
        String[] tokens = StringUtils.split(m_action.getName(), '.');
        m_shortName = tokens[tokens.length - 1];
        m_helpid = m_containerComp.getHelpid() + "_" + m_shortName; //$NON-NLS-1$
        
        for (Iterator it = m_action.getParams().iterator(); it.hasNext();) {
            Param param = (Param)it.next();
            //send it the param info and this help id so it can built its own
            m_params.add(new ParamInfo(param, m_helpid));
        }
    }
    /**
     * @return Returns the action.
     */
    public Action getAction() {
        return m_action;
    }
    
    /**
     * @return The component that contains the action (inherited or defines)
     */
    public ComponentInfo getContainerComp() {
        return m_containerComp;
    }
    
    /**
     * @return Returns the list of parameter infos of this action.
     */
    public List<ParamInfo> getParams() {
        return m_params;
    }
    
    /**
     * @return Returns the type.
     */
    public int getType() {
        return m_type;
    }
    
    /**
     * {@inheritDoc}
     * @param o The ActionInfo to be compared with this
     * @return i Returns the result of the comparison
     */
    public int compareTo(Object o) {
        ActionInfo ai = (ActionInfo)o;
        //Sort by action name
        return this.getI18nName().compareTo(ai.getI18nName());
    }
    
    /**
     * @return Returns the helpid.
     */
    public String getHelpid() {
        return m_helpid;
    }
    
    /**
     * @return Returns the shortName.
     */
    public String getShortName() {
        return m_shortName;
    }
    
    /**
     * @return the type value "e" or "c"
     */
    public String getTypeValue() {
        switch (m_type) {
            case TYPE_EXECUTE:
                return "e"; //$NON-NLS-1$
            case TYPE_CHECK:
                return "c"; //$NON-NLS-1$
            default:
                throw new RuntimeException();
        }
    }
}