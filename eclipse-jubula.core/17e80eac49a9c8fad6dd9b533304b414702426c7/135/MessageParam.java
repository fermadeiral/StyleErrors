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
package org.eclipse.jubula.communication.internal.message;

import java.io.Serializable;

/**
 * This class is the parameter of the component action parameter triple.
 * 
 * @author BREDEX GmbH
 * @created 14.10.2004
 * */
public class MessageParam implements Serializable {
    /** The value of the CAPParam */
    private String m_value;

    /** The type of CAPParam */
    private String m_type;

    /** The default constructor */
    public MessageParam() {
        super();
    }

    /**
     * @param value
     *            The value of parameter
     * @param type
     *            The type of parameter
     */
    public MessageParam(String value, String type) {
        m_value = value;
        m_type = type;
    }

    /** @return Returns the type. */
    public String getType() {
        return m_type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        m_type = type;
    }

    /** @return Returns the value. */
    public String getValue() {
        return m_value;
    }

    /**
     * @param value
     *            The value to set.
     */
    public void setValue(String value) {
        m_value = value;
    }
}