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
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * <p>
 * This exception is thrown to indicate an error during the execution of a test
 * step.
 * </p>
 * 
 * <p>
 * In case of an error the method of an implementation class should throw
 * this exception. It will be caught by the caller of the method.
 * </p>
 * 
 * @author BREDEX GmbH
 * @created 27.09.2004
 */
public class StepExecutionException extends EventSupportException {
    /**
     * @param message The message.
     * @param event The test error event.
     * {@inheritDoc}
     */
    public StepExecutionException(
        String message, TestErrorEvent event) {
        super(message, event, MessageIDs.E_STEP_EXEC);
    }
    /**
     * @param message The message.
     * @param event The test error event.
     * @param id An ErrorMessage.ID.
     * {@inheritDoc}
     */
    public StepExecutionException(
        String message, TestErrorEvent event, Integer id) {
        super(message, event, id);
    }
    /**
     * @param cause The cause exception.
     * {@inheritDoc}
     */
    public StepExecutionException(Throwable cause) {
        super(cause, MessageIDs.E_STEP_EXEC);
    }
    
    /**
     * for action which have no valid implementation in the current toolkit this
     * method is called and an appropriate exception is thrown.
     */
    public static void throwUnsupportedAction() {
        throw new StepExecutionException(
                TestErrorEvent.UNSUPPORTED_OPERATION_IN_TOOLKIT_ERROR,
                EventFactory.createUnsupportedActionError());
    }
}