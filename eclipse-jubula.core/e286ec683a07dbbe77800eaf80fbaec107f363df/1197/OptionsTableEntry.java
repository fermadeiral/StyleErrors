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
package org.eclipse.jubula.examples.aut.adder.swing.model;


/**
 * Represents a row in the options table.
 *
 * @author BREDEX GmbH
 * @created 29.03.2005
 */
public class OptionsTableEntry {
    /** description */
    private String m_description;
    /** value*/
    private Object m_value;
    /**
     * @param description The first column.
     * @param value The second column.
     */
    public OptionsTableEntry(String description, Object value) {
        m_description = description;
        m_value = value;
    }
    /**
     * @return The first column.
     */
    public String getDescription() {
        return m_description;
    }
    /**
     * @param description The first column.
     */
    public void setDescription(String description) {
        m_description = description;
    }
    /**
     * @return The second column.
     */
    public Object getValue() {
        return m_value;
    }
    /**
     * @param value The second column.
     */
    public void setValue(Object value) {
        m_value = value;
    }
}