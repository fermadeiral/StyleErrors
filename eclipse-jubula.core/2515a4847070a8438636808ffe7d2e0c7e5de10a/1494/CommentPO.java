/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
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

/**
 * class for comments
 *
 * @author BREDEX GmbH
 * @created 22.10.2015
 */
@Entity
@DiscriminatorValue(value = "D")
class CommentPO extends NodePO implements ICommentPO {

    /**
     * only for Persistence (JPA / EclipseLink)
     */
    CommentPO() {
        // only for Persistence (JPA / EclipseLink)
    }
    
    /**
     * constructor
     * @param comment comment
     */
    CommentPO(String comment) {
        super(comment, false);
        addTrackedChange(CREATED, false);
    }

    /**
     * @param name name
     * @param guid guid
     */
    CommentPO(String name, String guid) {
        super(name, guid, false);
    }
    
}
