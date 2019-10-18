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

import java.util.Iterator;


/**
 * @author BREDEX GmbH
 * @created 19.12.2005
 */
public interface IParamNodePO extends INodePO, IParameterInterfacePO {

    /**
     * Gets an iterator over the list of all variable references of type
     * <code>TDCell</code> in all data sets.
     * 
     * @return The iterator over all references.
     */
    public Iterator<TDCell> getParamReferencesIterator();

    /**
     * Gets an iterator over the list of all variable references of type
     * <code>TDCell</code> in the data sets with the specified row.
     * @param dataSetRow The data set row
     * @return The iterator over all references.
     */
    public Iterator<TDCell> getParamReferencesIterator(int dataSetRow);

    /**
     * Checks if the entry sets have complete data.
     * @return <code>true</code> if data is complete, 
     *         <code>false</code> otherwise.
     */
    public boolean isTestDataComplete();

    /**
     * Clears the Test Data for this node.
     */
    public void clearTestData();
}