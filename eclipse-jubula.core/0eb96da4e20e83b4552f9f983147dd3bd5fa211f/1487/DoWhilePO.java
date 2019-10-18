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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.eclipse.jubula.client.core.i18n.Messages;

/**
 * A Do-While loop
 * @author BREDEX GmbH
 *
 */
@Entity
@DiscriminatorValue(value = "F")
class DoWhilePO extends CondStructPO implements IDoWhilePO {

    /** only for Persistence (JPA / EclipseLink) */
    DoWhilePO() {
        // only for Persistence
    }
    
    /**
     * Constructor
     * @param name the name
     */
    DoWhilePO(String name) {
        super(name);
        newNode(1, NodeMaker.createContainerPO(Messages.Do));
        newNode(1, NodeMaker.createContainerPO(Messages.While));
    }
    
    /**
     * Constructor
     * @param name the name
     * @param guid the guid
     */
    DoWhilePO(String name, String guid) {
        super(name, guid);
        newNode(1, NodeMaker.createContainerPO(Messages.Do));
        newNode(1, NodeMaker.createContainerPO(Messages.While));
    }
    
    /** {@inheritDoc} */
    public boolean needIncludeInTRTree(INodePO node) {
        return true;
    }

    /** {@inheritDoc} */
    @Transient
    public IAbstractContainerPO getDoBranch() {
        return (IAbstractContainerPO) getNodeList().get(0);
    }

    /** {@inheritDoc} */
    @Transient
    public IAbstractContainerPO getCondition() {
        return (IAbstractContainerPO) getNodeList().get(1);
    }

    /** {@inheritDoc} */
    public String getDefaultName(INodePO node) {
        if (this.equals(node)) {
            return Messages.DoWhileName;
        }
        if (this.getUnmodifiableNodeList().get(0).equals(node)) {
            return Messages.Do;
        }
        return Messages.While;
    }
    
}
