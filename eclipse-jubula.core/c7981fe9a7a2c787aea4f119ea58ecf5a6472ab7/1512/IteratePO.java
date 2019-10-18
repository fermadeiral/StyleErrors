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
import org.eclipse.jubula.client.core.i18n.Messages;

/**
 * Class representing a For loop
 * @author BREDEX GmbH
 *
 */
@Entity
@DiscriminatorValue(value = "I")
class IteratePO extends ParamNodePO implements IIteratePO {
    
    /** only for Persistence (JPA / EclipseLink) */
    IteratePO() {
        // only for Persistence
    }
    
    /**
     * Constructor
     * @param name the name
     */
    IteratePO(String name) {
        super(name, false);
        this.addParameter(PoMaker.createCapParamDescriptionPO(
                "java.lang.Integer", Messages.IterateCount)); //$NON-NLS-1$
        super.addNode(0, NodeMaker.createContainerPO(Messages.Do));
    }
    
    /**
     * Constructor
     * @param name the name
     * @param guid the guid
     */
    IteratePO(String name, String guid) {
        super(name, guid, false);
        this.addParameter(PoMaker.createCapParamDescriptionPO(
                "java.lang.Integer", Messages.IterateCount)); //$NON-NLS-1$
        super.addNode(0, NodeMaker.createContainerPO(Messages.Do));
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
    public final void removeNode(INodePO childNode) {
        throw new UnsupportedOperationException(NOSUPPORT);
    }
    
    /** {@inheritDoc} */
    public final void removeAllNodes() {
        throw new UnsupportedOperationException(NOSUPPORT);
    }

    /** {@inheritDoc} */
    @Transient
    public IAbstractContainerPO getDoBranch() {
        return (IAbstractContainerPO) getNodeList().get(0);
    }

    /** {@inheritDoc} */
    @Transient
    public Iterator<INodePO> getAllNodeIter() {
        // This method is from ControllerPO...
        IteratorChain chain =  new IteratorChain();
        for (Iterator<INodePO> it = getNodeListIterator(); it.hasNext(); ) {
            chain.addIterator(it.next().getAllNodeIter());
        }
        return chain;
    }
    
    /** {@inheritDoc} */
    public String getDefaultName(INodePO node) {
        if (this.equals(node)) {
            return Messages.RepeatName;
        }
        return Messages.Do;
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
