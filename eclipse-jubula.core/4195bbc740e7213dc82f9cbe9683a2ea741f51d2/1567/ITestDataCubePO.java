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

import javax.persistence.EntityManager;

/**
 * @author BREDEX GmbH
 * @created Jul 15, 2010
 */
public interface ITestDataCubePO 
        extends IModifiableParameterInterfacePO, ITestDataNodePO {
    
    /**
     * @param name The new name.
     */
    public void setName(String name);
    
    /**
     * Should be called before deleting the object
     * @param sess the session
     */
    public void goingToBeDeleted(EntityManager sess);

}
