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
package org.eclipse.jubula.tools.internal.xml.businessmodell;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * Null Object representing a Component that does not conform to the current 
 * CompSystem configuration. 
 *
 * @author BREDEX GmbH
 * @created Jan 29, 2007
 */
public class InvalidComponent extends Component {
    /** Constant for invalid version- or order-number. */
    public static final int INVALID_NUMBER = 0;
    
    /** Constant for invalid component type */
    public static final String INVALID_COMPONENT_TYPE = 
        "INVALID_COMPONENT_TYPE"; //$NON-NLS-1$
    
    /** The i18n key for component type "unknown" */
    private static final String UNKNOWN_TYPE_I18N_KEY = 
        "CompSystem.UnknownComponentType"; //$NON-NLS-1$

    /** Constructor */
    public InvalidComponent() {
        setType(INVALID_COMPONENT_TYPE);
        setToolkitDesriptor(new ToolkitDescriptor(
            INVALID_COMPONENT_TYPE, INVALID_COMPONENT_TYPE, 
            INVALID_COMPONENT_TYPE,  INVALID_COMPONENT_TYPE, 
            StringConstants.EMPTY, INVALID_NUMBER, true, INVALID_NUMBER,
            INVALID_NUMBER));
    }
    
    /**
     * @see org.eclipse.jubula.tools.internal.xml.businessmodell.Component#findAction(java.lang.String)
     * @param name name
     * @return invalid action
     */
    public Action findAction(String name) {

        return new InvalidAction();
    }

    /** @return <code>false</code> */
    public boolean isVisible() {
        return false;
    }

    /** {@inheritDoc} */
    public void setVisible(boolean visible) {
        super.setVisible(false);
    }
    
    /** {@inheritDoc} */
    public boolean isValid() {
        return false;
    }

    /** {@inheritDoc} */
    public String getType() {
        return UNKNOWN_TYPE_I18N_KEY;
    }
}