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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface IExecTestCasePO extends ITestCasePO {

    /**
     * Gets the name of this node or, if na name is set, 
     * gets the name of the SpecTestCase.
     * {@inheritDoc}
     * @return
     */
    public abstract String getName();
    
    /**
     * Gets the real name of this execTC
     * {@inheritDoc}
     * @return
     */
    public abstract String getRealName();

    /**
     * Gets the comment of this node or, if no comment is set,
     * gets the comment of the SpecTestCase.
     * {@inheritDoc}
     * @return
     */
    public abstract String getComment();

    /**
     * @return Returns the specTestCase.
     */
    public abstract ISpecTestCasePO getSpecTestCase();

    /** 
     * {@inheritDoc}
     * ExecTestCasePO doesn't have an own parameter list
     * it uses generally the parameter from associated specTestCase
     */
    public abstract List<IParamDescriptionPO> getParameterList();

    /**
     * 
     * @return the GUID of the parent project of the referenced test case.
     */
    public abstract String getProjectGuid();

    /**
     * Stores the referenced SpecTC's parent project guid
     * @param projectGuid the guid
     */
    public abstract void setProjectGuid(String projectGuid);
    
    /**
     * 
     * @return the GUID of the specTestCase.
     */
    public abstract String getSpecTestCaseGuid();

    /**
     * {@inheritDoc}
     * @return the TDManagerPO from the depending SpecTestCasePO.
     */
    public abstract ITDManager getDataManager();

    /**
     * Sets the data manager and changes the <code>hasReferencedTD</code> flag
     * to <code>false</code>, so that {@link #getHasReferencedTD()} will
     * return <code>false</code> after this call.
     * 
     * {@inheritDoc}
     */
    public abstract void setDataManager(ITDManager dataManager);

    /**
     * Resolves the reference to the test data manager of the associated
     * specification test case node. This method creates a deep copy of the test
     * data manager an sets it as it's own manager.
     * 
     * @return The new test data manager
     */
    public abstract ITDManager resolveTDReference();

    /** 
     * {@inheritDoc}
     */
    public abstract Iterator<INodePO> getNodeListIterator();

    /**
     * public getter for unmodifiable map of eventExecTestCases
     * @return map ov eventExecTestCases
     */
    public abstract Map<String, IEventExecTestCasePO> getEventMap();

    /**
     * @param eventType eventType, for which to set the flag
     * @param flag signals, if the associated eventhandler for given eventType 
     * will be reused (flag = true) or overwritten (flag = false)
     */
    public abstract void setFlagForRefEventTc(String eventType, boolean flag);

    /**
     * @param eventType eventType, for which to get the flag
     * @return flag, which signals, if the eventhandler for given eventType is
     * referenced from associated specTestCase or overwritten
     */
    public abstract boolean getFlagForRefEventTc(String eventType);

    /**
     * Adds the component name pair to the internal map if the pair doesn't
     * exist.
     * 
     * @param pair
     *            The component name pair
     */
    public abstract void addCompNamesPair(ICompNamesPairPO pair);

    /**
     * Removes the component name pair with the passed first name from the
     * internal map.
     * 
     * @param firstName
     *            The first name
     */
    public abstract void removeCompNamesPair(String firstName);

    /**
     * Gets the component name pair with the past first name.
     * 
     * @param firstName
     *            The first name
     * @return The component name pair or <code>null</code>, if the internal
     *         map doesn't contain a component name pair with the passed first
     *         name
     */
    public abstract ICompNamesPairPO getCompNamesPair(String firstName);

    /**
     * @return An unmodifyable list of all component name pairs
     */
    public abstract Collection<ICompNamesPairPO> getCompNamesPairs();

    /**
     * Sets the SpecTestCase and adds this node to the SpecTestCase.
     * @param specTestCase The specTestCase to set.
     */
    public void setSpecTestCase(ISpecTestCasePO specTestCase);
    
    /**
     * Clears the cached Spec Test Case for this Exec Test Case. This is used
     * in order to prevent trying to transitively load data from an 
     * incorrect / closed session.
     */
    public void clearCachedSpecTestCase();

    /**
     * for performance reason is the cached objects set while preloading
     * @param spec ISpecTestCasePO
     */
    public void setCachedSpecTestCase(ISpecTestCasePO spec);


    /**
     * synchronizes the list of parameter unique ids in TDManagers of ExecTestCases
     * and the associated parameter list
     */
    public abstract void synchronizeParameterIDs();

    /**
     * Checks if all Data Columns have a dependent Parameter.
     * @return true if all Data Columns have a dependent Parameter, 
     * false otherwise.
     */
    public boolean checkHasUnusedTestData();

    /**
     * @return <code>true</code> if the test data for this node is referenced
     *         from another node (meaning that this node does not manage
     *         its own test data). Otherwise, <code>false</code>.
     */
    public abstract boolean getHasReferencedTD();
    
    /**
     * @param hasReferencedTD
     *            set to <code>true</code> if the test data for this node should
     *            be referenced from another node (meaning that this node does
     *            not manage its own test data). Otherwise, <code>false</code>.
     */
    public abstract void setHasReferencedTD(boolean hasReferencedTD);

}