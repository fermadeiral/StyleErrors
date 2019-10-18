/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * A value set containing all legal values for a parameter
 * 
 * @author BREDEX GmbH
 */
public class ValueSet {

    /** The value set */
    private Set<String> m_valueSet;
    
    /** Constructor */
    public ValueSet() {
        m_valueSet = new LinkedHashSet<>();
    }
    
    /**
     * @param valueSet the value set to be set
     */
    public void setValueSet(Set<String> valueSet) {
        m_valueSet = valueSet;
    }
    
    /**
     * @return the value set
     */
    public Set<String> getSet() {
        return m_valueSet;
    }
    
    /**
     * Overwrites the value set with the elements given in a comma separated
     * list
     * @param csvSet the comma separated list of elements that should be
     * contained within the new value set
     */
    public void setElements(String csvSet) {
        m_valueSet.clear();
        if (!csvSet.trim().equals("")) { //$NON-NLS-1$
            String[] elements = csvSet.split("[,]|[,]([\\s])+"); //$NON-NLS-1$
            for (String elem : elements) {
                m_valueSet.add(elem.trim());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String elem : m_valueSet) {
            sb.append(elem.trim() + StringConstants.COMMA
                    + StringConstants.SPACE);
        }
        if (sb.length() > 0) {
            sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(" "), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return sb.toString().trim();
    }
}
