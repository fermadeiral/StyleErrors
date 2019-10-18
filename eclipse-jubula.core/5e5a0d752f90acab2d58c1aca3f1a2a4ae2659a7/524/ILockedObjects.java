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
package org.eclipse.jubula.client.ui.rcp.editors;

import java.util.List;

import org.eclipse.jubula.client.core.model.IPersistentObject;


/**
 * @author BREDEX GmbH
 * @created 30.09.2005
 */
public interface ILockedObjects {
    /**
     * @return List of locked objects
     */
    public List<IPersistentObject> getLockedObjects();
}
