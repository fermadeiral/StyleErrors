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

import java.util.Set;

/**
 * @author BREDEX GmbH
 * @created Jun 11, 2007
 */
public interface IAUTContPO extends IPersistentObject {

    /**
     * @return Returns the autMainList.
     */
    public abstract Set<IAUTMainPO> getAutMainList();

    /**
     * Adds an AUT to a project.
     * @param aut The AUT to add.
     */
    public abstract void addAUTMain(IAUTMainPO aut);

    /**
     * Removes an AUT from a project.
     * @param aut The AUT to remove.
     */
    public abstract void removeAUTMain(IAUTMainPO aut);

}
