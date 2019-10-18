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
 * @author BREDEX GmbH
 */
@Entity
@DiscriminatorValue(value = "B")
class ConditionalStatementPO extends CondStructPO
        implements IConditionalStatementPO {

    /** only for Persistence (JPA / EclipseLink) */
    ConditionalStatementPO() {
        // only for Persistence
    }
    
    /** default constructor
     * @param name of condition
     * @param guid id 
     */
    ConditionalStatementPO(String name, String guid) {
        super(name, guid);
        init();
    }
    
    /** default constructor
     * @param name of condition
     */
    ConditionalStatementPO(String name) {
        super(name);
        init();
    }
    
    /** Initialises the 2 new child nodes */
    private void init() {
        newNode(0, NodeMaker.createContainerPO(Messages.Condition));
        newNode(1, NodeMaker.createContainerPO(Messages.Then));
        newNode(2, NodeMaker.createContainerPO(Messages.Else));
    }
    
    /** {@inheritDoc} */
    @Transient
    public IAbstractContainerPO getCondition() {
        return (IAbstractContainerPO) getNodeList().get(0);
    }
    
    /** {@inheritDoc} */
    @Transient
    public IAbstractContainerPO getThenBranch() {
        return (IAbstractContainerPO) getNodeList().get(1);
    }

    /** {@inheritDoc} */
    @Transient
    public IAbstractContainerPO getElseBranch() {
        return (IAbstractContainerPO) getNodeList().get(2);
    }
    
    /** {@inheritDoc} */
    public boolean needIncludeInTRTree(INodePO node) {
        return getCondition().equals(node);
    }

    /** {@inheritDoc} */
    @Transient
    public IAbstractContainerPO getDoBranch() {
        throw new UnsupportedOperationException(NOSUPPORT);
    }

    /** {@inheritDoc} */
    @Transient
    public String getDefaultName(INodePO node) {
        if (this.equals(node)) {
            return Messages.IfThenElseName;
        }
        if (this.getUnmodifiableNodeList().get(0).equals(node)) {
            return Messages.Condition;
        }
        if (this.getUnmodifiableNodeList().get(1).equals(node)) {
            return Messages.Then;
        }
        return Messages.Else;
    }
}