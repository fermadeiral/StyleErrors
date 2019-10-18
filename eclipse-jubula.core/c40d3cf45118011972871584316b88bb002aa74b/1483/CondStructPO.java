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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Class representing a negatable conditional structure
 * @author BREDEX GmbH
 *
 */
@Entity
@DiscriminatorValue(value = "U")
abstract class CondStructPO extends ControllerPO implements ICondStructPO {

    /** Whether the Conditional Statement is negated */
    private boolean m_isNegated = false;

    /** only for Persistence (JPA / EclipseLink) */
    CondStructPO() {
        // only for Persistence
    }

    
    /** default constructor
     * @param name name of condition
     */    
    CondStructPO(String name) {
        super(name);
    }

    /** default constructor
     * @param name name of condition
     * @param guid the gud
     */
    CondStructPO(String name, String guid) {
        super(name, guid);
    }
    
    /** {@inheritDoc} */
    @Basic(optional = false)
    @Column(name = "IS_NEGATED")
    public boolean isNegate() {
        return m_isNegated;
    }

    /** {@inheritDoc} */
    public void setNegate(boolean neg) {
        m_isNegated = neg;
    }
}
