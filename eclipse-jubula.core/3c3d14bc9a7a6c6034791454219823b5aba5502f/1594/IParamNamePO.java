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
 * @created 25.06.2007
 */
public interface IParamNamePO extends IAbstractGUIDNamePO {
    /**
     * get the id of the root project
     * @return id of root project
     */
    public Long getParentProjectId();
    
    /**
     * @param id id of root project
     */
    public void setParentProjectId(Long id);
}
