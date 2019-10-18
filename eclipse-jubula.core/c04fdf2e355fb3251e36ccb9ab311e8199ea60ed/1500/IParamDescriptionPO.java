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
 * @created 20.12.2005
 */
public interface IParamDescriptionPO extends IPersistentObject {

    /**
     * @return Returns the type.
     */
    public abstract String getType();

    /**
     * @param type The type to set.
     */
    public abstract void setType(String type);

    /**
     * 
     * @return name of parameter
     */
    public abstract String getName();
    
    /**
     * @return the guid
     */
    public abstract String getUniqueId();
    
    /**
     * @return id of root project
     */
    public abstract Long getParentProjectId();
    
    /**
     * @param projectId set id of root project
     */
    public abstract void setParentProjectId(Long projectId);
}