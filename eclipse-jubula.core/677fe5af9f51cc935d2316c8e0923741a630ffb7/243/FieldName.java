/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.search.data;

import org.eclipse.jubula.tools.internal.i18n.I18n;

/**
 * @author BREDEX GmbH
 */
public class FieldName extends SelectionState {
    /** The name of the search field.*/
    private String m_name;

    /**
     * @param field
     *            The field name
     * @param isSelected
     *            true, if selected, otherwise false.
     */
    public FieldName(String field, boolean isSelected) {
        super(isSelected);
        setName(field);
    }

    /**
     * @return A human readable name.
     */
    public String getDescName() {
        return I18n.getString("search.query.field." + getName()); //$NON-NLS-1$
    }


    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }


    /**
     * @param name the name to set
     */
    private void setName(String name) {
        m_name = name;
    }
}