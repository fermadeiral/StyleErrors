/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.jubula.rc.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.adaptable.IPropertyValue;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 16.10.2013
 */
public class PropertyUtil {
    /** invalid XML character */
    public static final char[] INVALID_XML_CHARS = { 0 };
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(PropertyUtil.class);
    
    /** Constructor */
    private PropertyUtil() {
        //empty
    }
    
    /**
     * @param object
     *            the object to introspect
     * @param propertyName
     *            the properties name to retrieve
     * @return the property or <code>null</code> if not found
     * @throws RobotException
     *             in case of problems
     */
    @SuppressWarnings("unchecked")
    public static String getPropertyValue(Object object, String propertyName)
            throws RobotException {
        String propertyValue = StringConstants.EMPTY;
        Validate.notNull(object, "Tested component must not be null"); //$NON-NLS-1$
        try {
            ArrayList<String> path = 
                    new ArrayList<String>(Arrays.asList(
                            propertyName.split(StringConstants.SLASH)));
            
            final Object prop;
            if (path.size() > 0) {
                prop = getPropertyByPathOrMethod(object, path, 0);
            } else {
                prop = null;
            }
            
            if (prop != null) {
                // Check if an adapter exists
                IPropertyValue propertyValueAdapter = 
                    ((IPropertyValue) AdapterFactoryRegistry
                        .getInstance().getAdapter(
                                IPropertyValue.class, prop));
                if (propertyValueAdapter != null) {
                    propertyValue = propertyValueAdapter
                            .getStringRepresentation(prop);
                } else {
                    propertyValue = String.valueOf(prop);
                }
            } else {
                propertyValue = String.valueOf(prop);
            }
        } catch (IllegalAccessException e) {
            throw new RobotException(e);
        } catch (InvocationTargetException e) {
            throw new RobotException(e);
        } catch (NoSuchMethodException e) {
            throw new RobotException(e);
        }
        
        if (StringUtils.containsAny(propertyValue, INVALID_XML_CHARS)) {
            for (Character c : INVALID_XML_CHARS) {
                propertyValue = StringUtils.remove(propertyValue, c);
            }
        }
        
        return propertyValue;
    }
    
    /**
     * Either retrieves the value of the last property of the path or
     * invokes the method with the last name in the path of the return value
     * of the forelast property.
     * @param object the return value of the last recursion step
     * @param path the path of the nested properties / method name
     * @param index the current recursion's index
     * @return the value of the nested property or the return value of the
     * invoked method
     * 
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private static Object getPropertyByPathOrMethod(Object object,
            ArrayList<String> path, int index) throws IllegalAccessException,
                InvocationTargetException, NoSuchMethodException {
        String name = path.get(index);
        
        if (index >= 0 && index < (path.size() - 1)) {
            if (name.endsWith(StringConstants.PARENTHESES_PAIR)) {
                return getPropertyByPathOrMethod(
                            MethodUtils
                            .invokeExactMethod(object, 
                                name.replace(
                                    StringConstants.LEFT_PARENTHESIS
                                    + StringConstants.RIGHT_PARENTHESIS,
                                    StringConstants.EMPTY
                                ),
                                null
                        ),
                        path, index + 1);
            }
            return getPropertyByPathOrMethod(PropertyUtils
                    .getProperty(object, name), path, index + 1);
            
        } else if (index >= 0 && index == (path.size() - 1)) {
            if (name.endsWith(StringConstants.PARENTHESES_PAIR)) {
                return MethodUtils.invokeExactMethod(object,
                        name.replace(
                            StringConstants.PARENTHESES_PAIR,
                            StringConstants.EMPTY
                        ),
                        null);
            }
            return PropertyUtils.getProperty(object, path.get(index));
            
        } else {
            return null;
        }
    }
    
    /**
     * Returns a sorted map consisting of the bean properties of a component
     * 
     * @param currComp 
     *              the component
     * @return the sorted map of properties
     */
    public static Map<String, String> getMapOfComponentProperties(
        final Object currComp) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils
                .getPropertyDescriptors(currComp);
        Map<String, String> componentProperties = new TreeMap<String, String>();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor pd = propertyDescriptors[i];
            String propertyName = pd.getName();
            try {
                Method readMethod = pd.getReadMethod();
                if (readMethod != null) {
                    componentProperties.put(propertyName, 
                            getPropertyValue(currComp, propertyName));
                } else {
                    componentProperties.put(propertyName,
                            "This property is not readable"); //$NON-NLS-1$
                }
            } catch (Exception e) {
                log.warn(
                        "Property " + propertyName + " of " //$NON-NLS-1$ //$NON-NLS-2$
                                + currComp.toString()
                                + " caused an exception while being read.", e); //$NON-NLS-1$
                componentProperties.put(propertyName, "---Error---"); //$NON-NLS-1$
            }
        }
        return componentProperties;
    }
}