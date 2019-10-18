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
package org.eclipse.jubula.client.core.persistence.locking;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 *
 * @author BREDEX GmbH
 * @created 30.11.2005
 */
@Entity
@Table(name = "APPLICATION")
public class ApplicationPO {

    /** Persistence (JPA / EclipseLink) OID */
    private Long m_id = null;
    
    /** Who started this instance of the application */
    private String m_userName = null;
    
    /** When was this application still running? */
    private Date m_timestamp = null;

    /** Persistence (JPA / EclipseLink) constructor, don't do anything fancy */
    ApplicationPO() {
        // internal for Persistence (JPA / EclipseLink)
    }
    
    /** external constructor, dthe dummy parameter is used to distinguish it
     * from the Persistence (JPA / EclipseLink) contructor.
     * @param dummy not used
     */
    public ApplicationPO(long dummy) {
        setUserName(System.getProperty("user.name")); //$NON-NLS-1$
    }
    /**
     * @return Returns the id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
     * @return Returns the timestamp.
     */
    @Basic
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getTimestamp() {
        return m_timestamp;
    }

    /**
     * @param timestamp The timestamp to set.
     */
    void setTimestamp(Date timestamp) {
        m_timestamp = timestamp;
    }

    /**
     * @return Returns the userName.
     */
    @Basic
    public String getUserName() {
        return m_userName;
    }

    /**
     * @param userName The userName to set.
     */
    void setUserName(String userName) {
        m_userName = userName;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof ApplicationPO)) {
            return false;
        }
        ApplicationPO o = (ApplicationPO)obj;
        if (getId() != null) {
            return getId().equals(o.getId());
        }
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer res = new StringBuffer(100);
        res.append(Messages.ApplicationID);
        res.append(StringConstants.SPACE);
        res.append(StringConstants.EQUALS_SIGN);
        res.append(StringConstants.SPACE);
        res.append(getId());
        res.append(StringConstants.COMMA);
        res.append(StringConstants.SPACE);
        res.append(Messages.User);
        res.append(StringConstants.SPACE);
        res.append(StringConstants.EQUALS_SIGN);
        res.append(StringConstants.SPACE);
        res.append(getUserName());
        if (getTimestamp() != null) {
            res.append(StringConstants.COMMA);
            res.append(StringConstants.SPACE);
            res.append(Messages.Timestamp);
            res.append(StringConstants.SPACE);
            res.append(StringConstants.EQUALS_SIGN);
            res.append(StringConstants.SPACE);
            String timestamp = DateFormat.getDateInstance().format(
                    getTimestamp());
            res.append(timestamp);
        }
        return res.toString();
    }

}
