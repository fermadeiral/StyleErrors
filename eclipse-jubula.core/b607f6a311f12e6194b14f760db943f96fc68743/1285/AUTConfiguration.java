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
package org.eclipse.jubula.client.launch;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Representing the configuration information used to launch an
 * {@link org.eclipse.jubula.client.AUT AUT}.
 * 
 * @author BREDEX GmbH
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface AUTConfiguration {
    /**
     * @return a unmodifiable map of information used to launch the
     *         {@link org.eclipse.jubula.client.AUT AUT}
     */
    @NonNull
    Map<String, String> getLaunchInformation();
}