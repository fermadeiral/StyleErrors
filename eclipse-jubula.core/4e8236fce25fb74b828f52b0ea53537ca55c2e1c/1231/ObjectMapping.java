/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.tools.ComponentIdentifier;

/**
 * Responsible for retrieving
 * {@link org.eclipse.jubula.tools.ComponentIdentifier ComponentIdentifier}
 * 
 * @author BREDEX GmbH
 * @created Oct 13, 2014
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ObjectMapping {

    /**
     * Returns a component identifier for the given component name
     * 
     * @param compName
     *            the component name
     * @return the identifier or <code>null</code> if no identifier was found
     */
    @Nullable
    public ComponentIdentifier get(String compName);
}