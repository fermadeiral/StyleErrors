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

import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * This exception is thrown by the Robot API implementations.
 * 
 * @author BREDEX GmbH
 * @created 17.03.2005
 */
public class RobotException extends EventSupportException {
    /**
     * @param message The message.
     * @param event The test error event.
     * {@inheritDoc}
     */
    public RobotException(String message, TestErrorEvent event) {
        super(message, event, MessageIDs.E_ROBOT);
    }
    /**
     * @param cause The cause exception.
     * {@inheritDoc}
     */
    public RobotException(Throwable cause) {
        super(cause, MessageIDs.E_ROBOT);
    }
}