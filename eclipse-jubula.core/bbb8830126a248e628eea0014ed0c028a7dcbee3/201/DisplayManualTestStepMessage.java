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
 * @author BREDEX GmbH
 * @created Aug 19, 2010
 */
public class DisplayManualTestStepMessage extends Message {
    /** <code>m_actionToPerfom</code> */
    private String m_actionToPerfom = null;

    /** <code>m_expectedBehaviour</code> */
    private String m_expectedBehavior = null;

    /** <code>m_timeout</code> */
    private int m_timeout;

    /** Default */
    public DisplayManualTestStepMessage() {
        super();
    }

    /**
     * Constructor
     * 
     * @param actionToPerfom
     *            string description to display
     * @param expectedBehavior
     *            string description to display
     * @param timeout
     *            the timeout
     */
    public DisplayManualTestStepMessage(String actionToPerfom,
            String expectedBehavior, int timeout) {
        setActionToPerfom(actionToPerfom);
        setExpectedBehavior(expectedBehavior);
        setTimeout(timeout);
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.DISPLAY_MANUAL_TEST_STEP_COMMAND;
    }

    /**
     * @param actionToPerfom
     *            the actionToPerfom to set
     */
    public void setActionToPerfom(String actionToPerfom) {
        m_actionToPerfom = actionToPerfom;
    }

    /** @return the actionToPerfom */
    public String getActionToPerfom() {
        return m_actionToPerfom;
    }

    /**
     * @param expectedBehavior
     *            the expectedBehavior to set
     */
    public void setExpectedBehavior(String expectedBehavior) {
        m_expectedBehavior = expectedBehavior;
    }

    /** @return the expectedBehaviour */
    public String getExpectedBehavior() {
        return m_expectedBehavior;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(int timeout) {
        m_timeout = timeout;
    }

    /** @return the timeout */
    public int getTimeout() {
        return m_timeout;
    }
}