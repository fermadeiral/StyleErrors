/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.command.parameters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.tools.internal.constants.StandardProfileNames;
/**
 * Profile Parameter for component specific profiles. This is necessary for the command handler.
 *
 * @author BREDEX GmbH
 * @created 21.12.2015
 */
public class ProfileTypeParameter implements StandardProfileNames {
    
    /**
     * Name for the Global Profile
     */
    public static final String GLOBAL = "Global"; //$NON-NLS-1$

    /** the type **/
    private String m_type = null;

    /**
     * Set the type to one of the following values:
     * STANDARD,
     * GIVEN_NAMES,
     * STRICT,
     * GLOBAL
     * @param type the type for the profile
     */
    public void setType(String type) {
        switch (type) {
            case STANDARD:
                m_type = STANDARD;
                break;
            case GIVEN_NAMES:
                m_type = GIVEN_NAMES;
                break;
            case STRICT:
                m_type = STRICT;
                break;
            case GLOBAL:
                m_type = GLOBAL;
                break;
            default:
                break;
        }
    }

    /**
     * 
     * @return the type
     */
    public String getType() {
        return m_type;
    }
    
    /**
     * 
     * @return the possible type values
     */
    public static List<String> getValues() {
        List<String> values = new ArrayList<String>();
        values.add(GLOBAL);
        values.add(GIVEN_NAMES);
        values.add(STANDARD);
        values.add(STRICT);
        return values;
    }
}
