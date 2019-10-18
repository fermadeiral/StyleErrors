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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.businessprocess.progress.ElementLoadedProgressListener;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.utils.NativeSQLUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.Index;


/**
 * class to link parameter description to values or references <br>
 * provides methods for handling of testdata
 * 
 * @author BREDEX GmbH
 * @created 08.12.2004
 * 
 */
@Entity
@Table(name = "TD_MANAGER")
@EntityListeners(value = { ElementLoadedProgressListener.class })
class TDManagerPO implements ITDManager {
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;

    /**
     * <code>m_dataTable</code> list with DataSetPO objects inside <br>
     * <li>index of list corresponds to dataset number</li>
     * <li>listWrapper object contains the reference to a list with testdata
     * objects
     * <li>constraint: the order of entries in this referenced list must
     * correspond to the order of parameters in parameterList of paramNode</li>
     * <br>
     * 
     */
    private List<IDataSetPO> m_dataTable = new ArrayList<IDataSetPO>();
    
    /** 
     * unique id of each parameter to get the assignment between parameter and its testdata
     */
    private List<String> m_uniqueIds = new ArrayList<String>();
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;

    /**
     * @param node
     *            corresponding node to TDManagerPO
     */
    TDManagerPO(IParameterInterfacePO node) {
        Validate.notNull(node);
        createUniqueIds(node);
    }

    /**
     * @param node corresponding node to TDManagerPO
     * @param uniqueIds the uniqueIds
     */
    TDManagerPO(IParameterInterfacePO node, List<String> uniqueIds) {
        Validate.notNull(node);
        setUniqueIds(uniqueIds);
    }
    
    /**
     * private constructor only for Persistence (JPA / EclipseLink)
     */
    TDManagerPO() {
        // nothing so far
    }
    
    /**
     * create the list with ids of all parameters
     * 
     * @param node The Parameter Interface from which Parameters are determined.
     */
    private void createUniqueIds(IParameterInterfacePO node) {
        List<IParamDescriptionPO> params = 
            node.getParameterList();
        for (IParamDescriptionPO param : params) {
            getUniqueIds().add(param.getUniqueId());
        }
    }
    
    /**
     * 
     * @return Id
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    
    /**
     * @param id The id to set.
     */
    @SuppressWarnings("unused")
    private void setId(Long id) {
        m_id = id;
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Transient
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (IDataSetPO lWrapperPO : getDataTable()) {
            lWrapperPO.setParentProjectId(projectId);
        }
    }

    /**
     * 
     * @return the ID of the Project to which the receiver belongs.
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    @Index(name = "PI_TD_MANAGER_PARENT_PROJ")
    private Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * 
     * @param projectId The ID of the Project to which the receiver belongs.
     */
    private void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * Only use this method for internal purposes!</b>
     * 
     * @return Returns the dataTable.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               orphanRemoval = true, 
               targetEntity = DataSetPO.class,
               fetch = FetchType.EAGER)
    @OrderColumn(name = "IDX")
    @BatchFetch(value = BatchFetchType.JOIN)
    public List<IDataSetPO> getDataTable() {
        return m_dataTable;
    }
    
    /**
     * Removes the Data Sets
     * @param sess the session
     */
    private void removeDataSets(EntityManager sess) {
        if (m_dataTable.isEmpty()) {
            return;
        }
        Query q = sess.createNativeQuery("delete from TD_MANAGER_TEST_DATA_LIST where TDMANAGERPO_ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId());
        q.executeUpdate();
        String list = NativeSQLUtils.getIdList(m_dataTable);
        q = sess.createNativeQuery("delete from TEST_DATA_CELL where FK_DATASET_ID in " + list); //$NON-NLS-1$
        q.executeUpdate();
        q = sess.createNativeQuery("delete from TEST_DATA_LIST where ID in " + list); //$NON-NLS-1$
        q.executeUpdate();
    }

    /**
     * 
     * @param dataTable The dataTable to set.
     */
    @SuppressWarnings("unused")
    private void setDataTable(List<IDataSetPO> dataTable) {
        m_dataTable = dataTable;
    }

    /**
     * deletes the dataset with specified number from datatable shifts all
     * following datasets to the next lower number
     * 
     * @param number
     *            number of dataset to delete
     */
    public void removeDataSet(int number) {
        getDataTable().remove(number);
    }
    
    /**
     * Deletes the values for the parameter with the given id
     * in all rows. If the data set rows are empty after this operation, they
     * will be deleted too.
     * 
     * @param uniqueId
     *            The unique id of the parameter the data to delete
     */
    public void removeColumn(String uniqueId) {
        int index = findColumnForParam(uniqueId);
        if (index >= 0) {
            for (IDataSetPO dataSet : getDataSets()) {
                dataSet.removeColumn(index);
            }
            if (getColumnCount() == 0) {
                getDataTable().clear();
            }
        }
    }

    /**
     * Creates new rows with empty test data.
     * @param row The new row count
     */
    private void expandRows(int row) {
        int colCount = getColumnCount();
        while (row >= getDataTable().size()) {
            List <String> columns = new ArrayList <String> (colCount);
            for (int i = 0; i < colCount; i++) {
                columns.add(""); //$NON-NLS-1$
            }
            IDataSetPO listW = PoMaker.createListWrapperPO(columns);
            getDataTable().add(listW);
            listW.setParentProjectId(getParentProjectId());
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void insertDataSet(int position) {
        int colCount = getColumnCount();
        List <String> columns = new ArrayList <String> (colCount);
        for (int i = 0; i < colCount; i++) {
            columns.add(""); //$NON-NLS-1$
        }

        insertDataSet(PoMaker.createListWrapperPO(columns), position);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void insertDataSet(IDataSetPO dataSet, int position) {
        Validate.notNull(dataSet);

        dataSet.setParentProjectId(getParentProjectId());
        if (position > getDataTable().size()) {
            // add empty columns up to the position where the given
            // Data Set will be inserted
            expandRows(position - 1);
        }

        getDataTable().add(position, dataSet);
    }
    
    /**
     * Creates new columns in all rows with empty test data.
     * 
     * @param column
     *            The new column count
     */
    private void expandColumns(int column) {
        while (column >= getColumnCount()) {
            for (IDataSetPO dataSet : getDataSets()) {
                dataSet.addColumn(""); //$NON-NLS-1$
            }
        }
    }
    /**
     * reads a single testdata object with value or reference for a specified
     * dataset row and the parameter name.
     * 
     * @param dataSetRow
     *            dataSetRow of dataset
     * @param uniqueId
     *            unique id of parameter, which is wanted the value/reference for
     * @return testdata object for parameter in specified dataset or null, if no
     *         testdata object is available
     * @throws IllegalArgumentException
     *             If the parameter with the userdefined name
     *             <code>paramName</code> doesn't exist
     */
    private String getCell(int dataSetRow, String uniqueId)
        throws IllegalArgumentException {
        int index = getUniqueIds().indexOf(uniqueId);
        System.out.println(index);
        if (index == -1) {
            throw new IndexOutOfBoundsException(Messages.ParameterWithUniqueId 
                    + StringConstants.SPACE + uniqueId + StringConstants.SPACE 
                    + Messages.IsNotAvailable + StringConstants.DOT);
        }
        return getCell(dataSetRow, index);
    }
    
    /**
     * Gets a test data entry at the specified row and column indices.
     * 
     * @param row
     *            The row
     * @param column
     *            The column
     * @return The test data
     */
    private String getCell(int row, int column) {
        return getDataSet(row).getValueAt(column);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCell(int dataSetRow, IParamDescriptionPO parameter)
        throws IllegalArgumentException {

        return getCell(dataSetRow, parameter.getUniqueId());
    }
    
    /**
     * reads a single dataset with specified dataSetRow <br>
     * <p>
     * <b>usage: </b> <br>
     * for restore of a single dataset
     * 
     * @param dataSetRow
     *            dataSetRow of wanted dataset 
     * @return the list with testdata objects for specified dataset or null
     */
    public IDataSetPO getDataSet(int dataSetRow) {
        return getDataTable().get(dataSetRow);
    }

    /**
     * Reads all datasets of a node.
     * 
     * @return The list of data sets or an empty list if the manager is empty.
     */
    @Transient
    public List<IDataSetPO> getDataSets() {
        return Collections.unmodifiableList(getDataTable());
    }
    
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
    public void updateCell(String testData, int row, int column) {
        expandRows(row);
        expandColumns(column);
        getDataSet(row).setValueAt(column, testData);
    }
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
     *            uniqueId of the parameter
     */
    public void updateCell(String testData, int row, String uniqueId) {
        int index = getUniqueIds().indexOf(uniqueId);
        if (index > -1) {
            updateCell(testData, row, index);
        }
    }
    /**
     * @return number of datasets 
     */
    @Transient
    public int getDataSetCount() {
        return getDataTable().size();
    }
    /**
     * @return The number of columns
     */
    @Transient
    public int getColumnCount() {
        int columns = 0;
        try {
            List<IDataSetPO> dataTable = getDataTable();
            if (dataTable.size() > 0) {
                IDataSetPO listW = dataTable.get(0);
                columns = listW.getColumnCount();
            }
        
        } catch (IndexOutOfBoundsException e) { // NOPMD by al on 3/19/07 1:37 PM
            // Nothing to be done
        }
        return columns;
    }

    /**
     * Copies the data of this TDManager to the given TDManager
     * @param tdMan the TDManager to copy the data to
     * @return the given TDManager with the new data.
     */
    public ITDManager deepCopy(ITDManager tdMan) {
        for (String uniqueId : getUniqueIds()) {
            tdMan.addUniqueId(uniqueId);
        }
        tdMan.clear();
        for (IDataSetPO dataSet : getDataSets()) {
            int columncount = dataSet.getColumnCount();
            List<String> newRow = new ArrayList<String> (columncount);
            for (int i = 0; i < columncount; i++) {
                newRow.add(dataSet.getValueAt(i));
            }
            tdMan.insertDataSet(PoMaker.createListWrapperPO(newRow), 
                    tdMan.getDataSetCount());
        }
        return tdMan;
    }
    
    /**
     * Clears this TDManager. Removes all TestData!
     */
    public void clear() {
        getDataTable().clear();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Version
    public Integer getVersion() {        
        return m_version;
    }

    /** 
     * 
     * @param version The version number to set for JPA optimistic-locking.
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }
    
    
    /**
     *          
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return toString();
    }

    /**
     * 
     * @return the uniqueIds
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TD_MANAGER_PARAM_ID",
        indexes = {@javax.persistence.Index(
                name = "TDMPO_IDX", columnList = "TDManagerPO_ID")})
    @Column(name = "UNIQUE_ID")
    @OrderColumn(name = "IDX")
    @JoinColumn(name = "FK_TD_MANAGER")
    @BatchFetch(value = BatchFetchType.EXISTS)
    public List<String> getUniqueIds() {
        return m_uniqueIds;
    }
    
    /**
     * Removes the entries from the collection table
     * @param sess the session
     */
    private void removeLinksToParams(EntityManager sess) {
        Query q = sess.createNativeQuery("delete from TD_MANAGER_PARAM_ID where TDMANAGERPO_ID = ?1"); //$NON-NLS-1$
        q.setParameter(1, getId()).executeUpdate();
    }
    
    /**
     * @param uniqueId unique id of parameter to find the column in datatable
     * @return the column contains values for given parameter or -1, if param is not contained in datatable
     */
    public int findColumnForParam(String uniqueId) {
        return getUniqueIds().indexOf(uniqueId);
    }

    /**
     * @param uniqueIds the uniqueIds to set
     */
    private void setUniqueIds(List<String> uniqueIds) {
        m_uniqueIds = uniqueIds;
    }
    
    /**
     * @param uniqueId uniqueId of a new parameter (independent of display order)
     */
    public void addUniqueId(String uniqueId) {
        getUniqueIds().add(uniqueId);
    }
    
    /**
     * clears the unique ids list
     */
    public void clearUniqueIds() {
        getUniqueIds().clear();
    }
    
    /**
     * removes the parameter id and its testdata
     * @param uniqueId id of parameter to remove
     */
    public void removeUniqueId(String uniqueId) {
        if (getUniqueIds().contains(uniqueId)) {
            removeColumn(uniqueId);
            getUniqueIds().remove(uniqueId);
        }
    }

    /** {@inheritDoc} */
    public void goingToBeDeleted(EntityManager sess) {
        removeDataSets(sess);
        removeLinksToParams(sess);
    }

}