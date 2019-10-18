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

import org.apache.commons.lang.StringUtils;

/**
 * A container class to be used in some structures like conditional statements and iterates
 * @author BREDEX GmbH
 *
 */
@Entity
@DiscriminatorValue(value = "X")
class ContainerPO extends NodePO implements IAbstractContainerPO {

    
    /** only for Persistence (JPA / EclipseLink) */
    ContainerPO() {
        // only for Persistence
    }
    
    /**
     * Constructor
     * @param name the name
     */
    ContainerPO(String name) {
        super(name, false);
    }
    
    /**
     * Constructor
     * @param name the name
     * @param guid the guid
     */
    ContainerPO(String name, String guid) {
        super(name, guid, false);
    }

    /** {@inheritDoc} */
    public void setName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            super.setName(name);
            return;
        }
        if (!(getParentNode() instanceof IControllerPO)) {
            super.setName("Unknown container."); //$NON-NLS-1$
        } else {
            super.setName(((IControllerPO) getParentNode()).
                    getDefaultName(this));
        }
    }
    
}
