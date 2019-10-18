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
package org.eclipse.jubula.client.ui.rcp.editors;

import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.ui.IEditorInput;


/**
 * @author BREDEX GmbH
 * @created 02.09.2005
 */
public class NodeEditorInput extends PersistableEditorInput 
    implements IEditorInput {

    /**
     * @param node INodePO to be edited
     * @throws PMException if the node can not be loaded
     */
    public NodeEditorInput(INodePO node) throws PMException {
        super(node);
    }

    /**
     * {@inheritDoc}
     * @param adapter
     * @return
     */
    public Object getAdapter(Class adapter) {        
        if (adapter == NodeEditorInput.class) {
            return this;
        }
        return super.getAdapter(adapter);
    }

    /**
     * {@inheritDoc}
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NodeEditorInput)) {
            return false;
        }
        NodeEditorInput o = (NodeEditorInput)obj;
        return getNode().equals(o.getNode());
    }


    /**
     * @return Returns the node.
     */
    public INodePO getNode() {
        return (INodePO)super.getNode();
    }
}
