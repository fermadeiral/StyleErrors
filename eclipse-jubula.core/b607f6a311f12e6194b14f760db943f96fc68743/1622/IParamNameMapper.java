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
package org.eclipse.jubula.client.core.businessprocess;

import org.eclipse.jubula.client.core.model.IParamNamePO;


/**
 * interface for management of param names between memory and database
 * @author BREDEX GmbH
 * @created 12.07.2007
 */
public interface IParamNameMapper {
    
    /**
     * @param guid guid of parameter
     * @param rootProjId of project the parameter belongs to
     * @return name of parameter with given guid
     */
    public String getName(String guid, Long rootProjId);
    
    /**
     * @param namePO paramName to insert in db
     */
    public void addParamNamePO(IParamNamePO namePO);
    
    /**
     * @param guid guid of param name to remove
     */
    public void removeParamNamePO(String guid);
}
