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
package org.eclipse.jubula.client.core.model;

import java.util.List;

/**
 * Interface for a Data Set, which is capable of storing and retrieving 
 * Test Data for given Parameters.
 * 
 * @author BREDEX GmbH
 * @created 20.12.2005
 */
public interface IDataSetPO extends IPersistentObject {
    /**
     * @return the list of column entries as strings
     */
    public abstract List<String> getColumnStringValues();

    /** @return empty string */
    public abstract String getName();

    /**
     * @param column the column index
     * @return the value
     */
    public String getValueAt(int column);

    /**
     * @param column change the value at the given index
     * @param value the new value
     */
    public void setValueAt(int column, String value);

    /**
     * @return The number of columns
     */
    public int getColumnCount();

    /**
     * Adds a new test data value as a column.
     * 
     * @param testData
     *            The test data
     */
    public void addColumn(String testData);

    /**
     * Removes the given value column.
     * 
     * @param column
     *            The column index
     */
    public void removeColumn(int column);
}