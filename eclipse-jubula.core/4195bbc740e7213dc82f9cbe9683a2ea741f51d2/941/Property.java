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
 * the property class
 * @author BREDEX GmbH
 * @created Nov 12, 2009
 */
public class Property {
    /** the property name */
    private String m_name;

    /** the property value */
    private String m_value;
    
    /** @param name the name to set */
    public void setName(String name) {
        m_name = name;
    }

    /** @return the name */
    public String getName() {
        return m_name;
    }

    /** @param value the value to set  */
    public void setValue(String value) {
        m_value = value;
    }

    /** @return the value */
    public String getValue() {
        return m_value;
    }
}