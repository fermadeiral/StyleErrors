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

import javax.persistence.EntityManager;

/**
 * 
 * @author BREDEX GmbH
 * @created 09.02.2011
 */
public interface ITDManager extends IPersistentObject {
    /**
     * Deletes the Data Set with the specified index.
     * 
     * @param idx The index of the Data Set to delete.
     */
    public abstract void removeDataSet(int idx);

    /**
    * Deletes the values for the parameter with the given id
     * in all rows. If the data set rows are empty after this operation, they
     * will be deleted too.
     * 
     * @param uniqueId
     *            The unique id of the parameter the data to delete
     */
    public abstract void removeColumn(String uniqueId);

    /**
     * Inserts a new empty Data Set at the given index.
     * 
     * @param index The index at which to insert.
     */
    public abstract void insertDataSet(int index);

    /**
     * Inserts a the given Data Set at the given index.
     * 
     * @param dataSet The Data Set to insert. Must not be <code>null</code>.
     * @param index The index at which to insert.
     */
    public abstract void insertDataSet(IDataSetPO dataSet, int index);

    /**
     * @param dataSetNumber
     *            The number (index) of the Data Set from which to retrieve the
     *            Test Data.
     * @param parameter
     *            The Parameter for which to retrieve Test Data.
     * @return the Test Data for the given Parameter at the given Data Set 
     *         index, or <code>null</code> if no such Test Data can be found.
     * @throws IllegalArgumentException
     *             If the given Parameter is not supported by the receiver.
     */
    public abstract String getCell(int dataSetNumber, 
            IParamDescriptionPO parameter) throws IllegalArgumentException;
    
    /**
     * 
     * @param idx
     *            The index of the Data Set to return. 
     * @return the Data Set at the given index, or <code>null</code> if no
     *         Data Set exists for the given index.
     */
    public abstract IDataSetPO getDataSet(int idx);

    /**
     * 
     * @return all Data Sets managed by the receiver.
     */
    public abstract List<IDataSetPO> getDataSets();

    /**
     * Updates the test data at the specified row and column. The data in the
     * passed test data instance are copied into the test data in the specified
     * cell. If the row and/or column are greater than the existing row/column
     * count, new rows/columns are created automatically.
     * 
     * @param testData
     *            The test data to update
     * @param row
     *            The row
     * @param column
     *            The column
     */
    public abstract void updateCell(String testData, int row, int column);

    /**
     * Updates the test data at the specified row and parameter name. The data
     * in the passed test data instance are copied into the test data in the
     * specified cell. If the row and/or column are greater than the existing
     * row/column count, new rows/columns are created automatically.
     * 
     * @param testData
     *            The test data to update
     * @param row
     *            The row
     * @param uniqueId
     *            The uniqueId of the parameter
     */
    public abstract void updateCell(String testData, int row,
        String uniqueId);

    /**
     * @return the number of Data Sets managed by the receiver.
     */
    public abstract int getDataSetCount();

    /**
     * @return The number of columns
     */
    public abstract int getColumnCount();

    /**
     * Copies the data of this TDManager to the given TDManager
     * @param tdMan the TDManager to copy the data to
     * @return the given TDManager with the new data.
     */
    public abstract ITDManager deepCopy(ITDManager tdMan);

    /**
     * Clears this TDManager. Removes all TestData!
     */
    public abstract void clear();

    /**
     * @param uniqueId uniqueId of a new parameter (independent of display order)
     */
    public void addUniqueId(String uniqueId);
    
    /**
     * removes the parameter id and its testdata
     * @param uniqueId id of parameter to remove
     */
    public void removeUniqueId(String uniqueId);
    
    /**
     * clears the unique ids list
     */
    public void clearUniqueIds();
    
    /**
     * @return list with unique ids of all params TDManager manages data for
     */
    public List<String> getUniqueIds();
    
    /**
     * @param uniqueId unique id of parameter to find the column in datatable
     * @return the column contains values for given parameter or -1, if param is not contained in datatable
     */
    public int findColumnForParam(String uniqueId);

    /**
     * @param parentProjectId id of parent project
     */
    public void setParentProjectId(Long parentProjectId);
    
    /**
     * @return the parent project id
     */
    public Long getParentProjectId();
    
    /**
     * This object going to be deleted, so it should remove all of its dependencies
     * @param sess the session
     */
    public void goingToBeDeleted(EntityManager sess);
}
