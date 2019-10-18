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
/**
 * 
 */
package org.eclipse.jubula.client.archive.dto;

import org.eclipse.jubula.client.core.model.ICommentPO;

/**
 * @author BREDEX GmbH
 *
 */
public class CommentDTO extends NodeDTO {
    
    /** needed because JSON mapping */
    public CommentDTO() {
        
    }
    /**
     * 
     * @param node the po to make to a DTO
     */
    public CommentDTO(ICommentPO node) {
        super(node);
    }

}
