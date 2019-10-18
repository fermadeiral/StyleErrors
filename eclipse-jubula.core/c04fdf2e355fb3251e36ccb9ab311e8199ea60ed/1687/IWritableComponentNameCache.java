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

import java.util.Map;

import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP.CompNameCreationContext;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemType;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IComponentNameReuser;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * Caching mechanism for Component Names.
 *
 * @author BREDEX GmbH 
 * @created Feb 5, 2009
 */
public interface IWritableComponentNameCache extends IComponentNameCache {

    /**
     * Adds the given Component Name to this cache.
     * @param compNamePo The new Component Name to add.
     */
    public void addCompNamePO(IComponentNamePO compNamePo);
    
    /**
     * Creates and returns a new Component Name with the given attributes.
     * 
     * @param name The name for the Component Name.
     * @param type The reuse type for the Component Name.
     * @param creationContext The creation context.
     * @return the newly created Component Name.
     */
    public IComponentNamePO createComponentNamePO(String name, String type, 
            CompNameCreationContext creationContext);

    /**
     * Creates and returns a new Component Name with the given attributes.
     * 
     * @param guid The GUID for the Component Name.
     * @param name The name for the Component Name.
     * @param type The reuse type for the Component Name.
     * @param creationContext The creation context.
     * @return the newly created Component Name.
     */
    public IComponentNamePO createComponentNamePO(String guid, String name, 
            String type, CompNameCreationContext creationContext);
    
    /**
     * Marks the Component name with the given GUID as renamed to 
     * <code>newName</code>.
     * 
     * @param guid The GUID of the Component Name to rename.
     * @param newName The new name for the Component Name.
     */
    public void renameComponentName(String guid, String newName);

    /**
     * Renames a Component Name
     * @param guid the guid
     * @param newName the new name
     */
    public void renamedCompName(String guid, String newName);
    
    /**
     * Clones a component name into the local cache if it is not yet present there
     * @param guid the guid of the CN to clone
     */
    public void addIfNotYetPresent(String guid);
    
    /**
     * Clears those Component Names which have 0 local usage changes
     * Should be only called from TS / TC Editors
     * @param node the editor root
     */
    public void clearUnusedCompNames(INodePO node);
    
    /**
     * Removes a Component Name from the local changes
     * @param guid the guid of the Component Name
     */
    public void removeCompName(String guid);

    /**
     * Stores locally caused CN type problems, and writes types
     * Should be used for unsaved changes
     * @param calc the calculator which calculated the problems 
     */
    public void storeLocalProblems(CalcTypes calc);
    
    /**
     * Returns new problems after having run a full type recalculation
     * @param calc the calculator
     * @return the new problems
     */
    public Map<String, ProblemType> getNewProblems(CalcTypes calc);
    
    /**
     * Updates the the given object to use the Component Name with the new GUID
     * instead of the one with the old GUID.
     * 
     * @param user The object having its reuse changed.
     * @param oldGuid The GUID of the Component Name that is no longer reused.
     *                May be <code>null</code>, which indicates that no
     *                Component Name was being used.
     * @param newGuid The GUID of the Component Name that is now to be reused.
     *                May be <code>null</code>, which indicates that no
     *                Component Name will be used.
     */
    public void changeReuse(IComponentNameReuser user, String oldGuid,
            String newGuid);
    
    /**
     * Handles the existence of Component Names that were to be inserted into
     * the database. This occurs when, for example: an editor with a new
     * Component Name is saved, but another Component Name with the same name
     * already exists in the database. The resolution is usually to identify
     * references to the saved GUID and replace them with references to the 
     * pre-existing GUID.
     * 
     * @param guidToCompNameMap Mapping from GUID of Component Names that were
     *                          supposed to be inserted in the database to the
     *                          GUID of Component Names that already exist in 
     *                          the database.
     */
    public void handleExistingNames(Map<String, String> guidToCompNameMap);
    
    /**
     * Sets the context
     * @param context the context
     */
    public void setContext(IPersistentObject context);
    
}