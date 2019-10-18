/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.persistence.annotations.Index;

/**
 * @author BREDEX GmbH
 * @created Nov 01, 2011
 */
@Entity
@Table(name = "TEST_DATA_CAT")
class TestDataCategoryPO implements ITestDataCategoryPO {

    /** Persistence (JPA / EclipseLink) OID */
    private transient Long m_id = null;
    
    /** Persistence (JPA / EclipseLink) version id */
    private transient Integer m_version = null;

    /** The ID of the parent project */
    private Long m_parentProjectId = null;

    /** the name of the category */
    private String m_name = null;

    /** all Central Test Data children of this element */
    private List<ITestDataCubePO> m_testDataChildList = 
        new ArrayList<ITestDataCubePO>();

    /** all Test Data Category children of this element */
    private List<ITestDataCategoryPO> m_categoryChildList = 
            new ArrayList<ITestDataCategoryPO>();

    /** the parent category */
    private ITestDataCategoryPO m_parent = null;

    /**
     * JPA accessor for Central Test Data children.
     * 
     * @return the Central Test Data children.
     */
    @OneToMany(cascade = CascadeType.ALL, 
               fetch = FetchType.EAGER, 
               targetEntity = TestDataCubePO.class, 
               mappedBy = "parent")
    List<ITestDataCubePO> getHbmTestDataChildList() {
        return m_testDataChildList;
    }

    /**
     * JPA mutator for Central Test Data children.
     * 
     * @param testDataChildList The Central Test Data children.
     */
    void setHbmTestDataChildList(List<ITestDataCubePO> testDataChildList) {
        m_testDataChildList = testDataChildList;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public List<ITestDataCubePO> getTestDataChildren() {
        return Collections.unmodifiableList(getHbmTestDataChildList());
    }

    /**
     * JPA accessor for Test Data Category children.
     * 
     * @return the Test Data Category children.
     */
    @OneToMany(cascade = CascadeType.ALL, 
            fetch = FetchType.EAGER, 
            targetEntity = TestDataCategoryPO.class,
            mappedBy = "parent")
    List<ITestDataCategoryPO> getHbmCategoryChildList() {
        return m_categoryChildList;
    }

    /**
     * JPA mutator for Central Test Data children.
     * 
     * @param categoryChildList The Test Data Category children.
     */
    void setHbmCategoryChildList(List<ITestDataCategoryPO> categoryChildList) {
        m_categoryChildList = categoryChildList;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Transient
    public List<ITestDataCategoryPO> getCategoryChildren() {
        return Collections.unmodifiableList(getHbmCategoryChildList());
    }

    /**
     * 
     * {@inheritDoc}
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
    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return m_version;
    }
    /**
     * @param version The version to set.
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
    public Long getParentProjectId() {
        return getHbmParentProjectId();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setParentProjectId(Long projectId) {
        setHbmParentProjectId(projectId);
        for (ITestDataCategoryPO category : getCategoryChildren()) {
            category.setParentProjectId(projectId);
        }
        for (ITestDataCubePO testData : getTestDataChildren()) {
            testData.setParentProjectId(projectId);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addCategory(ITestDataCategoryPO toAdd) {
        Assert.verify(toAdd != null);
        toAdd.setParent(this);
        getHbmCategoryChildList().add(toAdd);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeCategory(ITestDataCategoryPO toRemove) {
        Assert.verify(toRemove != null);
        toRemove.setParent(null);
        getHbmCategoryChildList().remove(toRemove);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addTestData(ITestDataCubePO toAdd) {
        Assert.verify(toAdd != null);
        toAdd.setParent(this);
        getHbmTestDataChildList().add(toAdd);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeTestData(ITestDataCubePO toRemove) {
        Assert.verify(toRemove != null);
        toRemove.setParent(null);
        getHbmTestDataChildList().remove(toRemove);
    }

    /**
     * JPA accessor for parent Project ID.
     * 
     * @return the ID of the Project to which the receiver belongs.
     */
    @Basic
    @Column(name = "PARENT_PROJ")
    @Index(name = "PI_DATA_CAT_PARENT_PROJ")
    Long getHbmParentProjectId() {
        return m_parentProjectId;
    }

    /**
     * JPA mutator for parent Project ID.
     * 
     * @param projectId the ID of the Project to which the receiver belongs.
     */
    void setHbmParentProjectId(Long projectId) {
        m_parentProjectId = projectId;
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "NAME", length = MAX_STRING_LENGTH)
    public String getName() {
        return m_name;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setName(String name) {
        m_name = name;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @ManyToOne(targetEntity = TestDataCategoryPO.class)
    @JoinColumn(name = "FK_PARENT")
    public ITestDataCategoryPO getParent() {
        return m_parent;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void setParent(ITestDataCategoryPO parent) {
        m_parent = parent;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeNode(ITestDataNodePO toRemove) {
        if (toRemove instanceof ITestDataCubePO) {
            removeTestData((ITestDataCubePO)toRemove);
        } else if (toRemove instanceof ITestDataCategoryPO) {
            removeCategory((ITestDataCategoryPO)toRemove);
        }
    }
}
