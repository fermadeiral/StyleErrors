/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
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

/**
 * @author BREDEX GmbH
 * @created Nov 01, 2011
 */
public interface ITestDataCategoryPO extends ITestDataNodePO {

    /**
     * @return an unmodifiable list of Test Data Categories
     */
    public List<ITestDataCategoryPO> getCategoryChildren();
    
    /**
     * @return an unmodifiable list of Central Test Data
     */
    public List<ITestDataCubePO> getTestDataChildren();

    /**
     * 
     * @param toAdd The category to add to the receiver. Must not be 
     *              <code>null</code>.
     */
    public void addCategory(ITestDataCategoryPO toAdd);

    /**
     * 
     * @param toRemove The category to remove from the receiver. Must not be 
     *              <code>null</code>.
     */
    public void removeCategory(ITestDataCategoryPO toRemove);

    /**
     * 
     * @param toAdd The Central Test Data to add to the receiver. Must not be 
     *              <code>null</code>.
     */
    public void addTestData(ITestDataCubePO toAdd);

    /**
     * 
     * @param toRemove The Central Test Data to remove from the receiver. 
     *                 Must not be <code>null</code>.
     */
    public void removeTestData(ITestDataCubePO toRemove);

    /**
     * 
     * @param name The receiver's new name. Must not be <code>null</code>.
     */
    public void setName(String name);
    
    /**
     * Removes the given node from the receiver's children.
     * 
     * @param toRemove The node to remove.
     */
    public void removeNode(ITestDataNodePO toRemove);
}
