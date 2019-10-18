/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;

/**
 * Interface for all necessary methods for testing trees.
 * 
 * @author BREDEX GmbH
 * @param <T>
 *            the type of a tree item
 */
public interface ITreeComponent<T> extends IWidgetComponent {

    /**
     * Gets the TreeOperationContext which is created through an toolkit
     * specific implementation.
     * 
     * @return the TreeOperationContext for the tree
     */
    public AbstractTreeOperationContext getContext();

    /**
     * Gets the property value of a tree cell
     * 
     * @param name
     *            the name of the property
     * @param cell
     *            the cell
     * @return the value
     */
    public String getPropertyValueOfCell(String name, T cell);

    /**
     * Gets the root node(s) of the tree. This could be either a single node or
     * multiple nodes
     * 
     * @return The root node(s).
     */
    public Object getRootNode();

    /**
     * 
     * @return The visibility of the Root Node
     */
    public boolean isRootVisible();

}
