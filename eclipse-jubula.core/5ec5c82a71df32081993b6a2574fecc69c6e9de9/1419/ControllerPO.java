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

import java.util.Iterator;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.lang.StringUtils;

/**
 * A class which represents a node with a rigid set of children
 * @author BREDEX GmbH
 *
 */
@Entity
@DiscriminatorValue(value = "W")
abstract class ControllerPO extends NodePO
    implements IControllerPO {
    
    /** only for Persistence (JPA / EclipseLink) */
    ControllerPO() {
        // only for Persistence
    }
    
    /**
     * Constructor
     * @param name the name
     * @param guid the guid
     */
    ControllerPO(String name, String guid) {
        super(name, guid, false);
    }
    
    /**
     * Constructor
     * @param name the name
     */
    ControllerPO(String name) {
        super(name, false);
    }
    
    /** {@inheritDoc} */
    public final void addNode(INodePO childNode) {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /** {@inheritDoc} */
    public final void addNode(int position, INodePO childNode) {
        throw new UnsupportedOperationException(NOSUPPORT);
    }

    /** {@inheritDoc} */
    public final void removeNode(INodePO node) {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /** {@inheritDoc} */
    public final void removeAllNodes() {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /**
     * Add a node - only to be used by subclasses
     * @param pos the position
     * @param node the new node
     */
    void newNode(int pos, INodePO node) {
        super.addNode(pos, node);
    }
    
    /** {@inheritDoc} */
    @Transient
    public Iterator<INodePO> getAllNodeIter() {
        IteratorChain chain =  new IteratorChain();
        for (Iterator<INodePO> it = getNodeListIterator(); it.hasNext(); ) {
            chain.addIterator(it.next().getAllNodeIter());
        }
        return chain;
    }
    
    /** {@inheritDoc} */
    public void setName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            super.setName(name);
        } else {
            super.setName(getDefaultName(this));
        }
    }

}
