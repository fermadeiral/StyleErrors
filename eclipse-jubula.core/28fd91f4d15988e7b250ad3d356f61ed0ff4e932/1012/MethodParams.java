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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.rc.common.exception.MethodParamException;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * Represents all parameters of the implementation class method that will be invoked by this command.
 * @author BREDEX GmbH
 * @created 20.12.2006
 */
public class MethodParams {
    /** The parameter types as <code>Class</code> objects. */
    private List<Object> m_types = new ArrayList<Object>();
    
    /** The parameter values. */
    private List<Object> m_objectValues = new ArrayList<Object>();
    
    /**
     * Adds the type and value of a method parameter to the internal lists.
     * @param param The parameter object that holds the type and value information in a string manner.
     * @throws MethodParamException If the parameter type cannot be found or If the parameter string value cannot be converted into an object defined by the parameter type.
     */
    public void add(MessageParam param) throws MethodParamException {
        try {
            Class type = null;
            try {
                type = Class.forName(param.getType(), true, 
                    Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                // this happens if guidancer.datatype.Variable is used!
                type = Class.forName(String.class.getName(), true, 
                    Thread.currentThread().getContextClassLoader());
            }
            String stringValue = param.getValue() == null 
                ? StringConstants.EMPTY : param.getValue();
            Object objectValue = ConvertUtils.convert(stringValue, type);
            if (objectValue == null
                || !type.isAssignableFrom(objectValue.getClass())) {
                throw new MethodParamException("Failed converting " //$NON-NLS-1$
                    + stringValue + " into an instance of " + type, param); //$NON-NLS-1$
            }
            m_types.add(type);
            m_objectValues.add(objectValue);
        } catch (ClassNotFoundException e) {
            throw new MethodParamException(
                "Action parameter type not found: " + e, param); //$NON-NLS-1$
        }
    }
    /**
     * Copies the passed source list into the passed array and returns it.
     * @param dest The array the list is copied into.
     * @param source The list
     * @return The <code>dest</code> array.
     */
    private Object[] createArray(Object[] dest, List<Object> source) {
        source.toArray(dest);
        return dest;
    }
    
    /**
     * @return The list of parameter types as <code>Class</code> objects.
     */
    public List<Object> getTypesAsList() {
        return m_types;
    }
    
    /**
     * @return The array of parameter types.
     */
    public Class[] getTypes() {
        return m_types.isEmpty() ? null : (Class[])createArray(
            new Class[m_types.size()], m_types);
    }
    
    /**
     * @return The list of parameter values.
     */
    public List<Object> getValuesAsList() {
        return m_objectValues;
    }
    
    /**
     * @return The array of parameter values.
     */
    public Object[] getValues() {
        return m_objectValues.isEmpty() ? null : createArray(
            new Object[m_objectValues.size()], m_objectValues);
    }
}