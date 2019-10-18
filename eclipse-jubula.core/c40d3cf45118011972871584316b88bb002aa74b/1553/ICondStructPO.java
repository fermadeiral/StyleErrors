/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
 * Interface representing a negatable conditional structure
 * @author BREDEX GmbH
 *
 */
public interface ICondStructPO extends IControllerPO {
    /**
     * Is negated?
     * @return whether it is
     */
    public boolean isNegate();
    
    /**
     * Sets the negated flag
     * @param neg the new flag
     */
    public void setNegate(boolean neg);
    
    /**
     * @return the node which should be executed after false condition
     */
    public IAbstractContainerPO getCondition();

    /**
     * @return the node which should be executed after false condition
     */
    public IAbstractContainerPO getDoBranch();
    
    /**
     * Decides whether a node should be included in the test result tree
     * @param node the node
     * @return whether
     */
    public boolean needIncludeInTRTree(INodePO node);
}
