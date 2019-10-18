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
package org.eclipse.jubula.client.archive.errorhandling;

import java.util.List;

/**
 * Interfaces for classes capable of resolving a project name conflict.
 *
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public interface IProjectNameConflictResolver {

    /**
     * Attempts to resolve a project name conflict using the provided possible
     * names. 
     * 
     * @param availableNames All available names.
     * @return the new name to use in order to resolve the conflict, or 
     *         <code>null</code> if the conflict could not be resolved.
     */
    public String resolveNameConflict(List<String> availableNames);
    
}
