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

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;

/**
 * This class creates a message object with a severity, a short message text, a message id
 * and a detailed message text. Such message information can be displayed in a 
 * message dialog.
 *
 * @author BREDEX GmbH
 * @created 20.03.2006
 */
public class Message {
    /**
     * <code>NO_DETAILS</code>
     */
    public static final String NO_DETAILS = I18n.getString("Message.noDetails"); //$NON-NLS-1$

    /** Status type severity (bit mask, value 0) indicating this status represents an question.
     * @see IStatus.OK 
     */
    public static final int QUESTION = 0;

    /** Status type severity (bit mask, value 1) indicating this status is informational only. 
     * @see IStatus.INFO 
     */
    public static final int INFO = 0x01;

    /** Status type severity (bit mask, value 2) indicating this status represents a warning. 
     * @see IStatus.WARNING 
     */
    public static final int WARNING = 0x02;
    
    /** Status type severity (bit mask, value 4) indicating this status represents an error. 
     * @see IStatus.ERROR 
     */
    public static final int ERROR = 0x04;

    /** the dteails text */
    private String[] m_details;
    
    /** the message id */ 
    private Integer m_id;
    
    /** the message text */
    private String m_message;
    
    /** the message severity */
    private int m_severity;
    
    /**
     * Creates a new message object.
     * @param id The id of the message.
     * @param severity <code>Message.INFO</code>, <code>Message.ERROR</code>, 
     * <code>Message.WARNING</code> or <code>Message.QUESTION.</code>
     * @param message The message text.
     * @param details The details text or null (if you don't need details).
     */
    public Message(Integer id, int severity, String message, String[] details) {
        m_id = id;
        m_severity = severity;
        m_message = message;
        m_details = details;
    }

    /**
     * @return Returns the details.
     */
    public String[] getDetails() {
        if (m_details == null || m_details.length == 0 || m_details[0] == null
                || m_details[0].equals(StringConstants.EMPTY)) {
            return new String[] { NO_DETAILS };
        }
        String[] details = new String[m_details.length];
        for (int i = 0; i < details.length; i++) {
            details[i] = I18n.getString(m_details[i]);
        }
        return details;
    }
    
    /**
     * @param params Parameter of the message text.
     * @return Returns the message.
     */
    public String getMessage(Object[] params) {
        String message = m_id.toString() + ": " + I18n.getString(m_message, params); //$NON-NLS-1$
        if (m_severity == Message.QUESTION) {
            message = I18n.getString(m_message, params); 
        }
        return message;
    }

    /**
     * @return Returns the severity: <br>
     * 0 = QUESTION <br>
     * 1 = INFO <br>
     * 2 = WARNING <br>
     * 4 = ERROR <br>
     */
    public int getSeverity() {
        return m_severity;
    }

    /**
     * @return Returns the id.
     */
    protected Integer getId() {
        return m_id;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(String[] details) {
        m_details = details;
    }
}