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
package org.eclipse.jubula.client.core.businessprocess;

import org.eclipse.jubula.client.core.model.INodePO;

/**
 * This class holds a component name and the node that is responsible for
 * defining the name.
 * 
 * @author BREDEX GmbH
 * @created 14.09.2005
 *
 */
public class CompNameResult {
    /**
     * The component name.
     */
    private String m_compName;
    /**
     * The responsible node.
     */
    private INodePO m_responsibleNode;

    /**
     * The constructor.
     * 
     * @param name
     *            The component name
     * @param node
     *            The responsible node
     */
    public CompNameResult(String name, INodePO node) {
        super();
        m_compName = name;
        m_responsibleNode = node;
    }
    /**
     * @return Returns the component name.
     */
    public String getCompName() {
        return m_compName;
    }
    /**
     * @return Returns the responsible node.
     */
    public INodePO getResponsibleNode() {
        return m_responsibleNode;
    }
}
