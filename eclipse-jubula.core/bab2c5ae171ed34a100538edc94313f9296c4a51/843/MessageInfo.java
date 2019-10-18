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
package org.eclipse.jubula.tools.internal.messagehandling;

/**
 * Contains all information necessary in order to represent an 
 * info/warning/error message.
 *
 * @author BREDEX GmbH
 * @created Mar 24, 2009
 */
public class MessageInfo {
    /** the ID of the base message */
    private Integer m_messageId;
    
    /** parameter of the message text or <code>null</code> if not needed */
    private String [] m_params;

    /**
     * Constructor
     * 
     * @param messageId The ID of the base message to use.
     * @param messageParams Parameter of the message text or <code>null</code> 
     *                      if not needed.
     */
    public MessageInfo(Integer messageId, String [] messageParams) {
        
        m_messageId = messageId;
        m_params = messageParams;
    }

    /**
     * @return the ID of the base message
     */
    public Integer getMessageId() {
        return m_messageId;
    }

    /**
     * @return parameter of the message text or <code>null</code> if not needed
     */
    public String[] getParams() {
        return m_params;
    }

}
