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

/**
 * @author BREDEX GmbH
 * @created Nov 04, 2011
 */
public interface ITestDataNodePO extends IPersistentObject {

    /**
     * 
     * @return the receiver's parent category, or <code>null</code> if the 
     *         receiver is a top-level element (i.e. has no parent category).
     */
    public ITestDataCategoryPO getParent();

    /**
     * 
     * @param parent The receiver's new parent. A value of <code>null</code>
     *               indicates that the receiver is a top-level element (i.e.
     *               has no parent category).
     */
    public void setParent(ITestDataCategoryPO parent);

}
