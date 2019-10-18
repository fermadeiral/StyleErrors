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
package org.eclipse.jubula.rc.common.exception;

import org.eclipse.jubula.tools.internal.exception.JBRuntimeException;

/**
 * This exception supports notification and processing of test execution
 * 
 * @author BREDEX GmbH
 * @created 06.04.2005
 */
public class ExecutionEvent 
    extends JBRuntimeException {
    
    /** test failed: component not found in the AUT*/
    public static final int PAUSE_EXECUTION = 31;

    /**
     * what should happen.
     */
    private int m_event = 0;
    
    /**
     * Constructor
     * @param action int
     */
    public ExecutionEvent(int action) {
        super(new Throwable(), new Integer(0));
        m_event = action; 
    }

    /**
     * returns the event
     * @return int
     */
    public int getEvent() {
        return m_event;
    }

}