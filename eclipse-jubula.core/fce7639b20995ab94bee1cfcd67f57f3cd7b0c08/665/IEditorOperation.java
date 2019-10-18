/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.controllers;

import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 *
 * @author BREDEX GmbH
 * @created Sept 24, 2012
 */
public interface IEditorOperation {

    /**
     * 
     * @param workingPo The object on which to operate.
     */
    public void run(IPersistentObject workingPo);
    
}
