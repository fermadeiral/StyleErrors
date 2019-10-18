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
public interface IAbstractGUIDNamePO extends IPersistentObject {
    /**
     * 
     * @return The GUID associated with this name.
     */
    public String getGuid();
    
    /**
     * 
     * @param newName the name to set. <code>null</code> and empty string 
     *                (<code>""</code>) are not allowed.
     */
    public void setName(String newName);
}
