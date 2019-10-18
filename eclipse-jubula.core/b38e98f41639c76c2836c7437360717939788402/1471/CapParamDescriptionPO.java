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
package org.eclipse.jubula.client.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;

/**
 * @author BREDEX GmbH
 * @created 29.06.2007
 */
@Entity
@DiscriminatorValue(value = "C")
class CapParamDescriptionPO extends ParamDescriptionPO implements 
    ICapParamDescriptionPO {
    
    /**
     * only for Persistence (JPA / EclipseLink)
     */
    CapParamDescriptionPO() {
        // nothing
    }
    
    /**
     * @param type parameter type
     * @param uniqueId I18NKey for parameter
     */
    CapParamDescriptionPO(String type, String uniqueId) {
        super(type, uniqueId);
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getName() {
        return CompSystemI18n.getString(getUniqueId(), true);
    }

}
