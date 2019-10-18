/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/

package org.eclipse.jubula.client.core.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author BREDEX GmbH
 *
 */
@Entity
@Table(name = "PARAM_VALUE_VALUES")
class ValueCommentPO implements IValueCommentPO {
    /** */
    private Long m_id;
    /** */
    private String m_value;
    /** */
    private String m_comment;
    /** for persistence */
    ValueCommentPO() {
        // persistence
    }
    /**
     * @param value value
     * @param comment comment
     */
    public ValueCommentPO(String value, String comment) {
        m_value = value;
        m_comment = comment;
    }
    /**
     * {@inheritDoc}
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return m_id;
    }
    /**
     * @param id persistence id
     */
    public void setId(Long id) {
        m_id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Basic
    @Column(name = "VALUE")
    public String getValue() {
        return m_value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String value) {
        m_value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Basic
    @Column(name = "VALUE_COMMENT")
    public String getComment() {
        return m_comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComment(String comment) {
        m_comment = comment;
    }

}
