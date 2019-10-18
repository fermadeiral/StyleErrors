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

import org.eclipse.jubula.communication.internal.message.MessageParam;

/**
 * Represents an exception during the method parameter evaluation.
 * @author BREDEX GmbH
 * @created Oct 12, 2006
 */
public class MethodParamException extends Exception {
    
    /** The method parameter object. */
    private MessageParam m_param;
    
    /**
     * @param message A message.
     * @param param The method parameter object.
     */
    public MethodParamException(String message, MessageParam param) {
        super(message);
        m_param = param;
    }

    /**
     * @return the param
     */
    public MessageParam getParam() {
        return m_param;
    }
}