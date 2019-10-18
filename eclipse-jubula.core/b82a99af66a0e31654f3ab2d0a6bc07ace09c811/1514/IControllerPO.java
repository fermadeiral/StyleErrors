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
 * Just an empty marker for Controllers
 *    these are nodes with a restricted set of children
 * @author BREDEX GmbH
 *
 */
public interface IControllerPO extends INodePO {
    /**
     * Returns the default name of the node - either the controller itself or a branch of it
     * @param node the node
     * @return the name
     */
    public String getDefaultName(INodePO node);
    
}
