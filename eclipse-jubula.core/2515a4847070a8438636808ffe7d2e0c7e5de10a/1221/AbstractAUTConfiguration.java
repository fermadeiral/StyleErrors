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
package org.eclipse.jubula.toolkit.base.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.launch.AUTConfiguration;
import org.eclipse.jubula.tools.AUTIdentifier;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public abstract class AbstractAUTConfiguration implements AUTConfiguration {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
            AbstractAUTConfiguration.class);
    
    /** the name */
    @Nullable private String m_name;
    /** the autID */
    @NonNull private AUTIdentifier m_autID;
    /** the information used to launch the AUT */
    @NonNull private Map<String, String> m_launchInformation = 
            new HashMap<String, String>();

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     */
    public AbstractAUTConfiguration(
        @Nullable String name, 
        @NonNull String autID) {
        m_name = name;
        
        Validate.notEmpty(autID, "The AUT-Identifier must not be empty"); //$NON-NLS-1$
        m_autID = new AutIdentifier(autID);
        
        add(AutConfigConstants.AUT_ID, getAutID().getID());
    }

    /**
     * @param option
     *            the option to add
     * @param value
     *            the value to set
     */
    protected void add(String option, String value) {
        if (m_launchInformation.containsKey(option)) {
            log.warn("Option has already been configured: " + option); //$NON-NLS-1$
        }
        m_launchInformation.put(option, value);
    }
    
    /**
     * @return the name
     */
    @Nullable public String getName() {
        return m_name;
    }

    /**
     * @return the autID
     */
    @NonNull public AUTIdentifier getAutID() {
        return m_autID;
    }

    /**
     * @return the launchInformation
     */
    @NonNull public Map<String, String> getLaunchInformation() {
        return Collections.unmodifiableMap(m_launchInformation);
    }
}