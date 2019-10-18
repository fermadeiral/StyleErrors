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
package org.eclipse.jubula.rc.common.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.MethodUtils;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.rc.common.exception.MethodParamException;


/**
 * Invokes the implementation class action method.
 * @author BREDEX GmbH
 * @created 20.12.2006
 */
public class MethodInvoker {
    /**  The method parameters. */
    private MethodParams m_methodParams;
    
    /** The message data received by the <code>CAPTestMessage</code>. */
    private MessageCap m_messageCap;
    
    /**
     * Creates a new method invoker.
     * @param messageCap The message data
     * @throws MethodParamException If the action method parameters cannot be evaluated successfully.
     */
    public MethodInvoker(MessageCap messageCap) throws MethodParamException {

        m_methodParams = new MethodParams();
        List<MessageParam> paramList = messageCap.getMessageParams();

        for (Iterator<MessageParam> it = paramList.iterator(); it.hasNext();) {
            MessageParam param = it.next();
            m_methodParams.add(param);
        }
        m_messageCap = messageCap;
    }
    /**
     * Invokes the action method of the passed implementation class.
     * @param target The implementation class the action method will be invoked on.
     * @return the return value of the invoked method
     * @throws NoSuchMethodException If the action method name is invalid.
     * @throws IllegalAccessException If the action method name cannot be accessed.
     * @throws InvocationTargetException If the action method throws an exception.
     */
    public Object invoke(Object target)
        throws NoSuchMethodException, IllegalAccessException,
        InvocationTargetException {

        String method = m_messageCap.getMethod();
        return MethodUtils.invokeMethod(
            target, method, m_methodParams.getValues());
    }
}