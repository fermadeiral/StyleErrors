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
 * This class represents a default object mapping for a
 * {@link org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent}.
 * @author BREDEX GmbH
 * @created 11.08.2005
 */
public class DefaultMapping {
    /** The logical name of the mapping. */
    private String m_logicalName;
    
    /** The technical name of the mapping. */
    private String m_technicalName;
    
    /** The type factory. */
    private String m_typeFactory;

    /** Constructor. Used by deserialization only. */
    public DefaultMapping() {
        // Nothing to be done
    }
    
    /**
     * The constructor.
     * @param logicalName The logical name of the mapping.
     * @param technicalName The technical name of the mapping.
     * @param typeFactory The type factory.
     */
    public DefaultMapping(String logicalName, String technicalName,
        String typeFactory) {
        m_logicalName = logicalName;
        m_technicalName = technicalName;
        m_typeFactory = typeFactory;
    }
    
    /**
     * @return The logical name of the mapping. It is expected that this name is
     *         an I18N key to get the real localized logical name.
     */
    public String getLogicalName() {
        return m_logicalName;
    }
    
    /**
     * @return The technical name of the mapping. It will be used serverside to
     *         identify the graphics component and client-side for the object
     *         mapping
     */
    public String getTechnicalName() {
        return m_technicalName;
    }
    
    /**
     * @return The type factory. It will be used server-side to create a new
     *         type, that means a new graphics component that will be mapped to
     *         the implementation (tester) class
     */
    public String getTypeFactory() {
        return m_typeFactory;
    }
}