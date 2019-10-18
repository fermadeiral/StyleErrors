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
 * This exception indicates that the verification of an implementation class
 * action method failed. For example, it will be thrown if an implementation
 * class asserts that the text in a textfield is not as expected.
 * 
 * @author BREDEX GmbH
 * @created 06.04.2005
 */
public class StepVerifyFailedException 
    extends StepExecutionException {
    /**
     * @param message The message.
     * @param event The test error event.
     * {@inheritDoc}
     */
    public StepVerifyFailedException(
        String message, TestErrorEvent event) {
        super(message, event, MessageIDs.E_STEP_VERIFY);
    }
}