/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.client.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 *
 */
@Entity
@Table(name = "PARAM_VALUE_SETS")
public class ParamValueSetPO implements IParamValueSetPO {
    /** id */
    private Long m_id;
    /** project id */
    private Long m_parentProjectID;
    /** the map from values with comment */
    private List<IValueCommentPO> m_values  = new ArrayList<>();
    /** the default value */
    private String m_defaultValue;

    /**
     * {@inheritDoc}
     */
    @Override
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }

    /**
     * @param id the persistence id
     */
    void setId(Long id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Column(name = "PARENT_PROJECT")
    public Long getParentProjectId() {
        return m_parentProjectID;
    }

    /**
     * {@inheritDoc}
     */
    public void setParentProjectId(Long parentProjectID) {
        this.m_parentProjectID = parentProjectID;
    }

    /**
     * {@inheritDoc}
     */
    @OneToMany(fetch = FetchType.EAGER, 
            cascade = CascadeType.ALL, 
            targetEntity = ValueCommentPO.class, orphanRemoval = true)
    @JoinColumn(name = "VALUE_SET_ID")
    public List<IValueCommentPO> getValues() {
        return m_values;
    }

    /**
     * @param values {@link Map} with the value as key and the comment as value
     */
    void setValues(List<IValueCommentPO> values) {
        m_values = values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Column(name = "DEFAULT_VALUE")
    public String getDefaultValue() {
        return m_defaultValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultValue(String defaultValue) {
        m_defaultValue = defaultValue;
    }
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getId() + StringConstants.SPACE + getDefaultValue()
                + StringConstants.SPACE + getValues();
    }

}
