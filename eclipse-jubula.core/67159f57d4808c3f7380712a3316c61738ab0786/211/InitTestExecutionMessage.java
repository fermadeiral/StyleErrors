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
package org.eclipse.jubula.communication.internal.message;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;

/**
 * This message tells the server that a test execution started
 * 
 * @author BREDEX GmbH
 * @created 07.02.2006
 */
public class InitTestExecutionMessage extends Message {
    /** default activation method */
    private String m_defaultActivationMethod;
    /** whether to highlight the component at which an error occurred
     * when testing*/
    private Boolean m_errorHighlighting;

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.INIT_TEST_EXECUTION_COMMAND;
    }

    /** @return default activation method */
    public String getDefaultActivationMethod() {
        return m_defaultActivationMethod;
    }

    /**
     * @param defaultActivationMethod
     *            default activation method
     */
    public void setDefaultActivationMethod(String defaultActivationMethod) {
        m_defaultActivationMethod = defaultActivationMethod;
    }

    /**
     * @return boolean value to decide whether to highlight the component at which an error occurred
     * when testing*/
    public Boolean isErrorHighlighting() {
        return m_errorHighlighting;
    }

    /**
     * @param getErrorHighlighting to decide whether to highlight the component at which an error occurred
     * when testing*/
    public void setErrorHighlighting(Boolean getErrorHighlighting) {
        this.m_errorHighlighting = getErrorHighlighting;
    }
}