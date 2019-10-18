/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeTableOperationContext;

/**
 * 
 * @author BREDEX GmbH
 *
 * @param <T>
 */
public interface ITreeTableComponent<T>
    extends ITreeComponent<T>, ITableComponent<T> {

    /**
     * Gets the TreeTableOperationContext which is created through an toolkit
     * specific implementation.
     * 
     * @param column
     *            the column
     * @return the TreeTablesOperationContext for the tree
     */
    public AbstractTreeTableOperationContext getContext(int column);

}
