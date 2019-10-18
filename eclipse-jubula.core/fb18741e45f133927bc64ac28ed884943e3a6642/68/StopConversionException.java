/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter.exceptions;

/**
 * Exception for the case that the conversion needs to stop
 * @created 26.11.2014
 */
public class StopConversionException extends RuntimeException {
    
    /** whether the abort of conversion was triggered by user's cancel action */
    private boolean m_manuallyTriggered = false;

    /** StopConversionException */
    public StopConversionException() {
        super();
    }
    

    /**
     * StopConversionException
     * 
     * @param manuallyTriggered
     *            whether the abort of conversion was triggered by user's cancel
     *            action
     */
    public StopConversionException(boolean manuallyTriggered) {
        super();
        m_manuallyTriggered = manuallyTriggered;
    }

    /**
     * @return whether the abort of conversion was triggered by user's cancel
     *            action
     */
    public boolean wasManuallyTriggered() {
        return m_manuallyTriggered;
    }

}
