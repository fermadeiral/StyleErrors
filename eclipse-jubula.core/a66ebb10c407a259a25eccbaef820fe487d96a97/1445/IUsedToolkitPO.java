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
 * @created 05.06.2007
 * 
 */
public interface IUsedToolkitPO extends IPersistentObject {
    
    /**
     * @return the major version number of the used toolkit.
     */
    public int getMajorVersion();
    
    /**
     * @return the minor version number of the used toolkit.
     */
    public int getMinorVersion();
    
    /**
     * @return The id of the used toolkit.
     */
    public String getToolkitId();
}
