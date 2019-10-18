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


/**
 * @author BREDEX GmbH
 * @created Jun 5, 2007
 */
public interface IReusedProjectPO 
        extends IPersistentObject, Comparable<IReusedProjectPO> {

    /**
     * @return the GUID of the reused project.
     */
    public String getProjectGuid();
    
    /**
     * @return the major version number.
     */
    public Integer getMajorNumber();
    
    /**
     * @return the minor version number.
     */
    public Integer getMinorNumber();
    
    /**
     * 
     * @return Returns the major version number.
     */
    public Integer getMicroNumber();

    /**
     * 
     * @return Returns the minor version number.
     */
    public String getVersionQualifier();
    
    /**
     * @return The name of the referenced project, if it is available. 
     *         Otherwise, <code>null</code>.
     */
    public String getProjectName();
    
    /**
     * @return a String representing the version number of the reused project
     */
    public abstract String getVersionString();

    /**
     * @return a {@link ProjectVersion} representing the version of the reused project
     */
    public ProjectVersion getProjectVersion();
}
