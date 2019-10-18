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

import java.util.List;
import java.util.ListIterator;

/**
 * Interface for objects that require Parameters.
 * 
 * @see IParamDescriptionPO
 *
 * @author BREDEX GmbH
 * @created Jul 13, 2010
 */
public interface IParameterInterfacePO extends IPersistentObject {

    /**
     * Gets the parameter with the given unique id
     * 
     * @param uniqueId uniqueId (GUID or I18NKey) of parameter
     * @return The parameter or <code>null</code>, if this node doesn't
     *         contain a parameter with the passed unique id
     */
    public abstract IParamDescriptionPO getParameterForUniqueId(
            String uniqueId);
    
    /**
     * 
     * @param paramName Name of Parameter to get.
     * @return the Parameter Description for the given name, or 
     *         <code>null</code> if the receiver does not have a Parameter with 
     *         the given name.
     */
    public abstract IParamDescriptionPO getParameterForName(String paramName);
    
    /**
     * @return names of parameters belonging to this node
     */
    public abstract List<String> getParamNames();

    /**
     * @return an unmodifiable copy for further use
     */
    public abstract List<IParamDescriptionPO> getParameterList();

    /**
     * 
     * @return An iterator for the list. Don't use for modification!
     */
    public abstract ListIterator<IParamDescriptionPO> getParameterListIter();
    /**
     * 
     * @return Size of ParameterList to prevent calls get getParamterList()
     * just to check if there are any parameters
     */
    public abstract int getParameterListSize();
    
    /**
     * @return Returns the dataManager.
     */
    public abstract ITDManager getDataManager();

    /**
     * @param dataManager The dataManager to set.
     */
    public abstract void setDataManager(ITDManager dataManager);

    /**
     * gets the value of the m_dataFile property
     * 
     * @return the name of the node
     */
    public abstract String getDataFile();

    /**
     * sets the File
     * @param pathToExternalDataFile
     *      path to file
     */
    public abstract void setDataFile(String pathToExternalDataFile);
    
    /**
     * 
     * @return the referenced "Test Data Cube" that provides the receiver with
     *         test data, or <code>null</code> if no "Test Data Cube" is 
     *         referenced. 
     */
    public abstract IParameterInterfacePO getReferencedDataCube();

    /**
     * 
     * @param dataCube The Data Cube to be referenced by the receiver.
     */
    public abstract void setReferencedDataCube(IParameterInterfacePO dataCube);

    /**
     * 
     * @return the node that uses the receiver in order to define its parameter
     *         interface and test data, or <code>null</code> if the receiver is
     *         not used in this way.
     */
    public INodePO getSpecificationUser();
}
