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
package org.eclipse.jubula.rc.common.implclasses.table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This class represents a cell in a Table.
 * @author BREDEX GmbH
 * @created 23.03.2005
 */
public class Cell {
    
    /** The zero based row of the cell. */
    private int m_row;
    
    /** The zero based column of the cell. */
    private int m_col;
    
    /**
     * Creates a new Cell instance.
     * @param row The zero based row of the cell.
     * @param col The zero based column of the cell.
     */
    public Cell(int row, int col) {
        m_row = row;
        m_col = col;
    }
    
    /**
     * @return The zero based column of the cell.
     */
    public int getCol() {
        return m_col;
    }
    
    /**
     * @return The zero based row of the cell.
     */
    public int getRow() {
        return m_row;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "(" + m_row + "," + m_col + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}