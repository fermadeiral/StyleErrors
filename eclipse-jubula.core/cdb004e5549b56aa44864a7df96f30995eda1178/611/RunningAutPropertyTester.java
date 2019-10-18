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
package org.eclipse.jubula.client.ui.rcp.propertytester;

import org.eclipse.jubula.client.core.businessprocess.RunningAutBP;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;

/**
 * Property Tester for AUT Identifiers (Running AUTs).
 * 
 * @author BREDEX GmbH
 * @created May 10, 2010
 */
public class RunningAutPropertyTester extends AbstractBooleanPropertyTester {
    /**
     * ID of the "isDefined" property, which describes whether an AUT Definition
     * for the given AUT Identifier exists in the current project.
     */
    public static final String IS_DEFINED = "isDefined"; //$NON-NLS-1$
    
    /**
     * <code>PROPERTIES</code>
     */
    private static final String[] PROPERTIES = new String[] { IS_DEFINED };

    /** {@inheritDoc} */
    public boolean testImpl(Object receiver, String property, Object[] args) {
        AutIdentifier autId = (AutIdentifier)receiver;
        if (property.equals(IS_DEFINED)) {
            return  RunningAutBP.isAutDefined(autId);
        }
        return false;
    }

    /** {@inheritDoc} */
    public Class<? extends Object> getType() {
        return AutIdentifier.class;
    }

    /** {@inheritDoc} */
    public String[] getProperties() {
        return PROPERTIES;
    }
}
