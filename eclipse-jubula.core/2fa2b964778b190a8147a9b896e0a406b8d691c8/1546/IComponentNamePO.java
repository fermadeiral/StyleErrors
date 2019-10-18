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

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;

/**
 * @author BREDEX GmbH
 * @created Apr 7, 2008
 */
public interface IComponentNamePO extends IAbstractGUIDNamePO {

    /**
     * @return the Component Type.
     */
    public String getComponentType();

    /**
     * @return the referenced Guid.
     */
    public String getReferencedGuid();

    /**
     * @return The context of creation.
     */
    public CompNameCreationContext getCreationContext();
    
    /**
     * @param componentType the Component Type to set.
     */
    public void setComponentType(String componentType);

    /**
     * @param referencedGuid the referenced Guid to set.
     */
    public void setReferencedGuid(String referencedGuid);
    
    /**
     * Two ComponentNamePOs are equal if their GUIDs are equal.
     * @param compNamePO a ComponentNamePO to compare.
     * @return true if the GUID of the given ComponentNamePO equals this GUID,
     * false otherwise.
     */
    public boolean isNameEqual(ComponentNamePO compNamePO);
    
    /**
     * Setting the id outside of JPA is necessary sometimes for Component Names
     * @param id the new id
     */
    public void setId(Long id);

    /** Returns the type problem if the CN has one
     * One CN can have at most one type problems: Incompatible Usage or Map
     * @return the problem or null if there's no problem
     */
    public IProblem getTypeProblem();

    /**
     *  Removes the type problem if the CN has one
     *  @param problem the problem to set
     */
    public void setTypeProblem(IProblem problem);
    
    /**
     * Sets the usage type
     * @param type the type
     */
    public void setUsageType(String type);
    
    /**
     * Gets the transient type
     * @return the type
     */
    public String getUsageType();
}
