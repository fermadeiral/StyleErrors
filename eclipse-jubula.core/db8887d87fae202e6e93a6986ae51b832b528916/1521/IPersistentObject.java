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

import java.io.Serializable;

/**
 * interface to bundle all PO-classes
 * @author BREDEX GmbH
 * @created 20.06.2005
 */
public interface IPersistentObject extends Serializable {
    /**
     * max length of a string in the database
     */
    public static final int MAX_STRING_LENGTH = 4000;

    /** Max length of strings in character (roughly MAX_STRING_LENGTH / 3) */
    public static final int MAX_STR_LGT_CHAR = 1300;
    /**
     * @return version
     */
    public Integer getVersion();
    
    /**
     * only for Persistence (JPA / EclipseLink)
     * @return returns the id.
     */
    public Long getId();
    
    /**
     * @return name of object
     */
    public String getName();
    
    /**
     * @return the (database) ID of the parent project.
     */
    public Long getParentProjectId();

    /**
     * @param projectId the (database) ID of the parent project.
     */
    public void setParentProjectId(Long projectId);

}
