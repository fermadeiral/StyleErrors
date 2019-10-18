/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.components;

import org.eclipse.swt.widgets.Item;

/**
 * Interface containing common methods for SWT Trees and Tables
 * @author BREDEX GmbH
 *
 */
public interface ISWTTableComponent {

    /**
     * @param row the row index
     * @return whether the checkbox is checked
     */
    public boolean isChecked(int row);

    /**
     * @return The index of the selected row
     */
    public int getSelectionIndex();

    /**
     * @return the column items
     */
    public Item[] getColumnItems();

}
