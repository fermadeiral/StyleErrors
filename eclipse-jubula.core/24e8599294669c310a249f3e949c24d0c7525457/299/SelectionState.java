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
package org.eclipse.jubula.client.ui.rcp.search.data;

/**
 * Data class for storing the selection state used for buttons.
 * @author BREDEX GmbH
 * @created April 25, 2013
 * @see TypeName
 * @see org.eclipse.jubula.client.ui.rcp.search.page.ButtonSelections
 */
public class SelectionState {

    /** True, if selected, otherwise false. */
    private boolean m_isSelected;

    /**
     * @param isSelected True, if selected, otherwise false.
     */
    public SelectionState(boolean isSelected) {
        m_isSelected = isSelected;
    }

    /**
     * @param isSelected True, if selected, otherwise false.
     */
    public void setSelected(boolean isSelected) {
        m_isSelected = isSelected;
    }

    /**
     * @return the enabled
     */
    public boolean isSelected() {
        return m_isSelected;
    }

}
