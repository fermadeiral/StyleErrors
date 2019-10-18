/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
/**
 * Util class for Reflection calls 
 * @author Bredex GmbH
 * @created 27.10.2015
 */
public class ReflectionUtil {
    /** 
     * Defines which string should be replaced with null
     */
    private static final String JUBULA_NULL = "jb_null"; //$NON-NLS-1$
    /**
     * The logging.
     */
    private static AutServerLogger log = 
        new AutServerLogger(ReflectionUtil.class);
    
    /**
     * private constructor, because this is a util class
     */
    private ReflectionUtil() {
        //Util class
    }

    /**
     * Get instances of the given arguments
     * @param args the values for the arguments
     * @param argsSplit the argument separator
     * @param parameterClasses the classes for the arguments
     * @return Object array containing the created objects with the specified value
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object[] getParameterValues(
            String args, 
            @Nullable String argsSplit,
            Class<?>[] parameterClasses)
                    throws NoSuchMethodException, IllegalAccessException,
                    InvocationTargetException, InstantiationException {
        String[] argValues;
        if (!StringUtils.isEmpty(argsSplit)) {
            argValues = StringUtils.splitPreserveAllTokens(args, argsSplit);
        } else {
            argValues = new String[] { args };
        }
        if (argValues.length != parameterClasses.length) {
            throw new StepExecutionException("Invalid number of parameters", //$NON-NLS-1$
                    EventFactory
                            .createActionError(TestErrorEvent.INVALID_INPUT));
        }
        Object[] argObjects = new Object[argValues.length];
        for (int i = 0; i < argValues.length; i++) {
            if (JUBULA_NULL.equalsIgnoreCase(argValues[i])) {
                argObjects[i] = null;
            } else {
                argObjects[i] = ConstructorUtils
                    .invokeExactConstructor(parameterClasses[i], argValues[i]);
            }
        }
        return argObjects;
    }

    /**
     * Get class instances for a given list of fully qualified class names separated by ","
     * @param signatureList the list of fqcn's
     * @param classLoader the class loader which will be used to load the classes
     * @return Array containing the class objects
     * @throws ClassNotFoundException
     */
    public static Class<?>[] getParameterClasses(String signatureList,
            ClassLoader classLoader) throws ClassNotFoundException {
        String[] sign = signatureList.split(StringConstants.COMMA);
        Class<?>[] parameterClasses = new Class[sign.length];
        for (int i = 0; i < sign.length; i++) {
            parameterClasses[i] = Class.forName(sign[i], true, classLoader);
        }
        return parameterClasses;
    }

    /**
     * invokes the specified method
     * @param fqcn fully qualified class name
     * @param name method name
     * @param signature method signature
     * @param args argument values
     * @param argsSplit argument separator
     * @param uiClassLoader class loader
     * @return return value of the invoked method
     * @throws Throwable
     * @throws StepVerifyFailedException
     */
    public static Object invokeMethod(
            final String fqcn, 
            final String name,
            @Nullable final String signature, 
            @Nullable final String args, 
            @Nullable final String argsSplit,
            ClassLoader uiClassLoader)
                    throws Throwable, StepVerifyFailedException {
        Class<?> clazz = Class.forName(fqcn, true, uiClassLoader);
        Class[] parameterClasses = {};
        Object[] argObjects = {};
        if (!StringUtils.isEmpty(signature) && !StringUtils.isEmpty(args)) {
            parameterClasses = ReflectionUtil
                    .getParameterClasses(signature, uiClassLoader);
            argObjects = ReflectionUtil.getParameterValues(args, argsSplit,
                    parameterClasses);
        }
        return MethodUtils.invokeStaticMethod(clazz, name, argObjects,
                parameterClasses);
    }

    /**
     * invokes the specified method
     * @param fqcn fully qualified class name
     * @param name method name
     * @param uiClassloader class loader
     * @return return value of the invoked method
     * @throws Throwable
     * @throws StepVerifyFailedException
     */
    public static Object invokeMethod(String fqcn, String name,
            ClassLoader uiClassloader)
                    throws Throwable, StepVerifyFailedException {
        Class<?> clazz = Class.forName(fqcn, true, uiClassloader);
        return MethodUtils.invokeStaticMethod(clazz, name, null, null);
    }
    
    /**
     * Throws the appropriate StepExecutionException for a given exception which
     * occurred during the method invocation
     * 
     * @param e the occurred exception
     */
    public static void handleException(Throwable e) {
        if (e instanceof InvocationTargetException) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalStateException) {
                throw new StepExecutionException(e.toString(),
                        EventFactory.createVerifyFailed("", //$NON-NLS-1$
                                cause.toString()));
            }
            throw new StepExecutionException(cause.toString(),
                    EventFactory.createActionError(
                            TestErrorEvent.EXECUTION_ERROR,
                            new String[] {cause.toString()}));
        }
        if (e instanceof NullPointerException) {
            log.warn("Nullpointer occurred trying to invoke a method", e); //$NON-NLS-1$
            throw new StepExecutionException(e.toString(),
                    EventFactory.createActionError(
                            TestErrorEvent.EXECUTION_ERROR,
                            new String[] {"Invoke method failed. The method might not be static"})); //$NON-NLS-1$
        
        }
        throw new StepExecutionException(e.toString(),
                EventFactory.createActionError(
                        TestErrorEvent.EXECUTION_ERROR,
                        new String[] {e.toString()}));

    }
}
