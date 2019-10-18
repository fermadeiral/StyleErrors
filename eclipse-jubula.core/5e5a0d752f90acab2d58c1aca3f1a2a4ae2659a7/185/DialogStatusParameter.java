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
package org.eclipse.jubula.client.ui.rcp.utils;

/**
 * @author BREDEX GmbH
 * @created Jan 19, 2007
 */
public class DialogStatusParameter {

    /** the status message */
    private String m_message;
    /** the button state */
    private Boolean m_buttonState;
    /** the status type */
    private Integer m_statusType;
    
    /**
     * @return the buttonState
     */
    public Boolean getButtonState() {
        return m_buttonState;
    }
    
    /**
     * @param buttonState the buttonState to set
     */
    public void setButtonState(Boolean buttonState) {
        m_buttonState = buttonState;
    }
    
    /**
     * @return the message
     */
    public String getMessage() {
        return m_message;
    }
    
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        m_message = message;
    }
    
    /**
     * @return the statusType
     */
    public Integer getStatusType() {
        return m_statusType;
    }
    
    /**
     * @param statusType the statusType to set
     */
    public void setStatusType(Integer statusType) {
        m_statusType = statusType;
    }
}