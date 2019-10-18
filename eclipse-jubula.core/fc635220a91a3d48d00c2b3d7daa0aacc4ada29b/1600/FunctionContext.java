/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import org.eclipse.jubula.client.core.model.INodePO;

/**
 * @author BREDEX GmbH
 */
public class FunctionContext {
    /**
     * the node
     */
    private INodePO m_node;

    /**
     * Constructor
     * 
     * @param node
     *            the current node this function is evaluated for
     */
    public FunctionContext(INodePO node) {
        setNode(node);
    }

    /**
     * @return the node
     */
    public INodePO getNode() {
        return m_node;
    }

    /**
     * @param node the node to set
     */
    private void setNode(INodePO node) {
        m_node = node;
    }
}
