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
 * @created Apr 19, 2010
 */
public class EndTestExecutionMessage extends Message {
    /** <code>TIMEOUT</code> the timeout for this message */
    public static final int TIMEOUT = 10000;

    /** Default constructor. */
    public EndTestExecutionMessage() {
        // empty
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.END_TESTEXECUTION_COMMAND;
    }
}