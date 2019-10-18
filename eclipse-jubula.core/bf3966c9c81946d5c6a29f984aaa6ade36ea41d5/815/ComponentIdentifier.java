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
package org.eclipse.jubula.tools;


/**
 * Information for identifying a component in the AUT.
 * 
 * @author BREDEX GmbH
 * @created 13.10.2014
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @param <T>
 *            the type of UI component this identifier represents
 */
public interface ComponentIdentifier<T> {
    /**
     * Set the Profile for this component identifier
     * @param profile the profile to set
     * @since 4.0
     */
    public void setProfile(Profile profile);
    
    /**
     * Get the Profile of this component identifier
     * 
     * @return the profile which is used for this component identifier or null
     *         if no profile was set
     * @since 4.0
     */
    public Profile getProfile();
}
