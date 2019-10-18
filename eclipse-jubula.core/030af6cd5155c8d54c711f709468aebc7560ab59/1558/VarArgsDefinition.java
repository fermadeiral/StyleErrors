/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.functions;

import org.apache.commons.lang.Validate;

/**
 * Definition of a variable number of possible arguments for a Function,
 * as , as read from an extension.
 */
public class VarArgsDefinition {

    /** the name for these varargs, for use in tooling */
    private String m_type;

    /** the default number of arguments to use/suggest */
    private int m_defaultNumberOfArgs;

    /**
     * Constuctor
     * 
     * @param type 
     *          The type for the parameter. May not be <code>null</code>.
     * @param defaultNumberOfArgs 
     *          The default number of arguments to use/suggest.
     */
    public VarArgsDefinition(String type, int defaultNumberOfArgs) {
        Validate.notNull(type);
        m_type = type;
        m_defaultNumberOfArgs = defaultNumberOfArgs;
    }

    /**
     * 
     * @return the type of the receiver. Never <code>null</code>.
     */
    public String getType() {
        return m_type;
    }

    /**
     * 
     * @return the default number of arguments to use/suggest for 
     *         a Function utilizing the receiver.
     */
    public int getDefaultNumberOfArgs() {
        return m_defaultNumberOfArgs;
    }
    
}
