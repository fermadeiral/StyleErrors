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
import java.util.List;
import java.util.ListIterator;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;

/**
 * class to manage the list with testdata, associated with a dataset number
 * 
 * @author BREDEX GmbH
 * @created 13.06.2005
 */
@Entity
@Table(name = "TEST_DATA_LIST")
class DataSetPO implements IDataSetPO {
    
    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /**
     * <code>m_columns</code> list with testdata
     */
    private List<IDataCellPO> m_columns = new ArrayList<IDataCellPO>();
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version;
    
    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /**
     * @param list list to manage from DataSetPO
     */
    DataSetPO(List<String> list) {
        if (list == null) {
            setColumns(new ArrayList<IDataCellPO>());
        } else {
            List<IDataCellPO> dataValueList = new ArrayList<IDataCellPO>(
                    list.size());
            for (ListIterator<String> iterator = list.listIterator(); iterator
                    .hasNext();) {
                dataValueList.add(new DataCellPO(iterator.next(),
                        getParentProjectId()));
                
            }
            setColumns(dataValueList);
        }
    }
    
    /**
     * constructor
     *
     */
    DataSetPO() {
        // only for Persistence (JPA / EclipseLink)    
    }

    /**
     * 
     * @return Returns the id.
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    
    /**
     * @param id The id to set.
     */
    void setId(Long id) {
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
     * @param projectId The (database) ID of the parent project.
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (IDataCellPO cell : getColumns()) {
            cell.setParentProjectId(projectId);
        }
    }

    /**
     *    
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }
    
    /**
     * @param projectId The (database) ID of the parent project.
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /** {@inheritDoc} */
    @Version
    public Integer getVersion() {
        return m_version;
    }

    /**
     * @param version version
     */
    @SuppressWarnings("unused")
    private void setVersion(Integer version) {
        m_version = version;
    }

    /** {@inheritDoc} */
    @Transient
    public String getName() {
        return StringConstants.EMPTY;
    }

    /** {@inheritDoc} */
    public String getValueAt(int column) {
        return getColumns().get(column).getDataValue();
    }

    /** {@inheritDoc} */
    public void setValueAt(int column, String value) {
        getColumns().set(column, new DataCellPO(value, getParentProjectId()));
    }
    
    /** {@inheritDoc} */
    @Transient
    public int getColumnCount() {
        return getColumns().size();
    }

    /** {@inheritDoc} */
    public void addColumn(String value) {
        getColumns().add(new DataCellPO(value, getParentProjectId()));
    }

    /** {@inheritDoc} */
    public void removeColumn(int column) {
        if (column < getColumnCount()) {
            getColumns().remove(column);
        }
    }

    /**
     * @return the columns
     */
    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            targetEntity = DataCellPO.class)
    @OrderColumn(name = "IDX")
    @JoinColumn(name = "FK_DATASET_ID")
    @BatchFetch(value = BatchFetchType.JOIN)
    private List<IDataCellPO> getColumns() {
        return m_columns;
    }

    /**
     * @param columns the columns to set
     */
    void setColumns(List<IDataCellPO> columns) {
        m_columns = columns;
    }
    
    /** {@inheritDoc} */
    @Transient
    public List<String> getColumnStringValues() {
        List<String> list = new ArrayList<String>(getColumnCount());
        for (IDataCellPO dataValue : getColumns()) {
            list.add(dataValue.getDataValue());
        }
        return list;
    }
}
