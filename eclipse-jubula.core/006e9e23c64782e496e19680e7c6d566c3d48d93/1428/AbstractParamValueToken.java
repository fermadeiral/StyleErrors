/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.utils;

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;

/**
 * Base class for implementors of {@link IParamValueToken}.
 */
public abstract class AbstractParamValueToken implements IParamValueToken {

    /** Constant for a Variable as a data type of test data */
    protected static final String VARIABLE = "guidancer.datatype.Variable"; //$NON-NLS-1$

    /**
     * <code>m_value</code> string represents the token in the GUI
     */
    private String m_value = null;

    /**
     * index of first character of this token in the entire parameter value
     */
    private int m_startPos = 0;
    
    /**
     * <code>m_errorKey</code>I18NKey for error message 
     * associated with result of invocation of validate()
     */
    private Integer m_errorKey = null;

    /** param description belonging to currently edited parameter value */
    private IParamDescriptionPO m_desc;

    /**
     * Constructor
     * 
     * @param s string represents the token
     * @param pos index of first character of token in entire string
     * @param desc param description belonging to currently edited parameter value
     */
    public AbstractParamValueToken(
            String s, int pos, IParamDescriptionPO desc) {
        
        m_value = s;
        m_startPos = pos;
        m_desc = desc;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final Integer getErrorKey() {
        return m_errorKey;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public final void setErrorKey(Integer errorKey) {
        m_errorKey = errorKey;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected final String getValue() {
        return m_value;
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected final IParamDescriptionPO getParamDescription() {
        return m_desc;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final int getStartIndex() {
        return m_startPos;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public final int getEndIndex() {
        return m_startPos + m_value.length();
    }

}
