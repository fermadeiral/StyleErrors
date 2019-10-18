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
package org.eclipse.jubula.communication.internal.message;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Profile;
/**
 * Sets the Profile in the AUT Server
 *
 * @author BREDEX GmbH
 * @created 12.01.2016
 * 
 */
public class SetProfileMessage extends Message {

    /** the Profile **/
    private Profile m_profile = null;
    @Override
    public String getCommandClass() {
        return CommandConstants.SET_PROFILE_COMMAND;
    }
    
    /**
     * 
     * @param p the Profile
     */
    public void setProfile(Profile p) {
        m_profile = p;
    }

    /**
     * 
     * @return the profile
     */
    public Profile getProfile() {
        return m_profile;
    }
}
